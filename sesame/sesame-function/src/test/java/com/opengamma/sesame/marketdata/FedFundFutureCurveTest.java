package com.opengamma.sesame.marketdata;

import static com.opengamma.sesame.config.ConfigBuilder.argument;
import static com.opengamma.sesame.config.ConfigBuilder.arguments;
import static com.opengamma.sesame.config.ConfigBuilder.config;
import static com.opengamma.sesame.config.ConfigBuilder.function;
import static com.opengamma.sesame.config.ConfigBuilder.implementations;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.TimeZone;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableMap;
import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.link.ConfigLink;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.impl.SimpleCounterparty;
import com.opengamma.core.position.impl.SimpleTrade;
import com.opengamma.financial.analytics.curve.CurveConstructionConfiguration;
import com.opengamma.financial.security.future.FederalFundsFutureSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.service.ServiceContext;
import com.opengamma.service.ThreadLocalServiceContext;
import com.opengamma.service.VersionCorrectionProvider;
import com.opengamma.sesame.CurrencyPairsFn;
import com.opengamma.sesame.CurveDefinitionFn;
import com.opengamma.sesame.CurveNodeConverterFn;
import com.opengamma.sesame.CurveSpecificationFn;
import com.opengamma.sesame.CurveSpecificationMarketDataFn;
import com.opengamma.sesame.DefaultCurrencyPairsFn;
import com.opengamma.sesame.DefaultCurveDefinitionFn;
import com.opengamma.sesame.DefaultCurveNodeConverterFn;
import com.opengamma.sesame.DefaultCurveSpecificationFn;
import com.opengamma.sesame.DefaultCurveSpecificationMarketDataFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleResolverFn;
import com.opengamma.sesame.DefaultFXMatrixFn;
import com.opengamma.sesame.DefaultFixingsFn;
import com.opengamma.sesame.DiscountingMulticurveBundleFn;
import com.opengamma.sesame.DiscountingMulticurveBundleResolverFn;
import com.opengamma.sesame.DiscountingMulticurveCombinerFn;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.ExposureFunctionsDiscountingMulticurveCombinerFn;
import com.opengamma.sesame.FXMatrixFn;
import com.opengamma.sesame.FixingsFn;
import com.opengamma.sesame.MarketExposureSelector;
import com.opengamma.sesame.MulticurveBundle;
import com.opengamma.sesame.RootFinderConfiguration;
import com.opengamma.sesame.SimpleEnvironment;
import com.opengamma.sesame.component.RetrievalPeriod;
import com.opengamma.sesame.component.StringSet;
import com.opengamma.sesame.config.FunctionModelConfig;
import com.opengamma.sesame.engine.ComponentMap;
import com.opengamma.sesame.engine.FixedInstantVersionCorrectionProvider;
import com.opengamma.sesame.fedfundsfuture.DefaultFedFundsFutureFn;
import com.opengamma.sesame.fedfundsfuture.FedFundsFutureCalculatorFactory;
import com.opengamma.sesame.fedfundsfuture.FedFundsFutureDiscountingCalculatorFactory;
import com.opengamma.sesame.fedfundsfuture.FedFundsFutureFn;
import com.opengamma.sesame.graph.FunctionModel;
import com.opengamma.sesame.interestrate.InterestRateMockSources;
import com.opengamma.sesame.trade.FedFundsFutureTrade;
import com.opengamma.timeseries.date.localdate.ImmutableLocalDateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.OpenGammaClock;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.result.Result;
import com.opengamma.util.test.TestGroup;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.time.Expiry;

@Test(groups = TestGroup.UNIT)
public class FedFundFutureCurveTest {

  private static final ZonedDateTime VALUATION_TIME = DateUtils.getUTCDate(2014, 4, 17);
  private static final String CURVE_CONSTRUCTION_CONFIGURATION_USD_FFF = "USD_ON-FFF";

  private static final double TOLERANCE_PV = 1.0E-4;

  private static final double EXPECTED_PV = 0.0000;
  private static final int NB_TRADE = 3;
  private static final double[] EXPECTED_PRICE = new double[NB_TRADE];// Price used for curve calibration
  private static final LocalDate[] EXPIRY_DATE = new LocalDate[NB_TRADE];
  static {
    EXPECTED_PRICE[0] =  0.9990; // Price used for curve calibration FFJ4
    EXPECTED_PRICE[1] =  0.999075; // Price used for curve calibration FFK4
    EXPECTED_PRICE[2] =  0.999025; // Price used for curve calibration FFM4
    EXPIRY_DATE[0] = LocalDate.of(2014, 4, 30);
    EXPIRY_DATE[1] = LocalDate.of(2014, 5, 30);
    EXPIRY_DATE[2] = LocalDate.of(2014, 6, 30);
  }

  private static final String TRADING_EX = "CME";
  private static final String SETTLE_EX = "CME";
  private static final Currency CCY = Currency.USD;
  private static final double UNIT_AMOUNT = 5000000.0d/12.0d;
  private static final ExternalId FED_FUND_INDEX_ID = InterestRateMockSources.getOvernightIndexId();
  private static final String CATEGORY = "Category";
  private static final int NB_CONTRACTS = 20 ; // 100 m
  private static final BigDecimal TRADE_QUANTITY = BigDecimal.valueOf(NB_CONTRACTS);
  private static final Counterparty COUNTERPARTY = new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "COUNTERPARTY"));
  private static final LocalDate TRADE_DATE = VALUATION_TIME.toLocalDate();
  private static final OffsetTime TRADE_TIME = OffsetTime.of(LocalTime.of(0, 0), ZoneOffset.UTC);
  
  
  
  private DiscountingMulticurveBundleResolverFn _curveBundle;
  
  private FedFundsFutureFn _fedFundsFutureFn;
  private Clock _defaultInstance;
  
  @BeforeClass
  public void setUpClass() throws IOException {
    // required for futures lookup to work.
    // BloombergFutureUtils.getMonthlyExpiryCodeForFutures(String, int, LocalDate) uses LocalDate.now()
    // to infer the future code so it's important that this remains static from the point of view of this
    // test.
    _defaultInstance = OpenGammaClock.getInstance();
    OpenGammaClock.setInstance(Clock.fixed(VALUATION_TIME.toInstant(), ZoneId.of("UTC"))); 
    FunctionModelConfig config =
        config(
            arguments(
                function(
                    MarketExposureSelector.class,
                    argument("exposureFunctions", ConfigLink.resolved(InterestRateMockSources.mockFFExposureFunctions()))),
                function(
                    DefaultDiscountingMulticurveBundleFn.class,
                    argument("impliedCurveNames", StringSet.of())),
                function(
                    RootFinderConfiguration.class,
                    argument("rootFinderAbsoluteTolerance", 1e-9),
                    argument("rootFinderRelativeTolerance", 1e-9),
                    argument("rootFinderMaxIterations", 1000)),
                function(
                    DefaultCurveNodeConverterFn.class,
                    argument("timeSeriesDuration", RetrievalPeriod.of(Period.ofYears(1))))),
            implementations(
                FedFundsFutureFn.class, DefaultFedFundsFutureFn.class,
                FedFundsFutureCalculatorFactory.class, FedFundsFutureDiscountingCalculatorFactory.class,
                CurveNodeConverterFn.class, DefaultCurveNodeConverterFn.class,
                CurrencyPairsFn.class, DefaultCurrencyPairsFn.class,
                CurveSpecificationMarketDataFn.class, DefaultCurveSpecificationMarketDataFn.class,
                FXMatrixFn.class, DefaultFXMatrixFn.class,
                DiscountingMulticurveCombinerFn.class, ExposureFunctionsDiscountingMulticurveCombinerFn.class,
                CurveDefinitionFn.class, DefaultCurveDefinitionFn.class,
                DiscountingMulticurveBundleFn.class, DefaultDiscountingMulticurveBundleFn.class,
                DiscountingMulticurveBundleResolverFn.class, DefaultDiscountingMulticurveBundleResolverFn.class,
                HistoricalMarketDataFn.class, DefaultHistoricalMarketDataFn.class,
                CurveSpecificationFn.class, DefaultCurveSpecificationFn.class,
                FixingsFn.class, DefaultFixingsFn.class,
                MarketDataFn.class, DefaultMarketDataFn.class));

    ImmutableMap<Class<?>, Object> components = InterestRateMockSources.generateBaseComponents();
    VersionCorrectionProvider vcProvider = new FixedInstantVersionCorrectionProvider(Instant.now());
    ServiceContext serviceContext = ServiceContext.of(components).with(VersionCorrectionProvider.class, vcProvider);
    ThreadLocalServiceContext.init(serviceContext);

    _curveBundle = FunctionModel.build(DiscountingMulticurveBundleResolverFn.class, config, ComponentMap.of(components));

    _fedFundsFutureFn = FunctionModel.build(FedFundsFutureFn.class, config, ComponentMap.of(components));
  }

  private static MarketDataBundle createMarketDataBundle() {
    LocalDate[] dateFixing = new LocalDate[]{
        LocalDate.of(2014, 4, 1),
        LocalDate.of(2014, 4, 2),
        LocalDate.of(2014, 4, 3),
        LocalDate.of(2014, 4, 4),
        LocalDate.of(2014, 4, 7),
        LocalDate.of(2014, 4, 8),
        LocalDate.of(2014, 4, 9),
        LocalDate.of(2014, 4, 10),
        LocalDate.of(2014, 4, 11),
        LocalDate.of(2014, 4, 14),
        LocalDate.of(2014, 4, 15)
    };
    double[] rateFixing = new double[]{
        0.0010,
        0.0011,
        0.0012,
        0.0013,
        0.0014,
        0.0015,
        0.0015,
        0.0015,
        0.0015,
        0.0014,
        0.0015
    };
    LocalDate valuationDate = DateUtils.getUTCDate(2014, 4, 17).toLocalDate();

    LocalDateDoubleTimeSeries fixingSeries = ImmutableLocalDateDoubleTimeSeries.of(dateFixing, rateFixing);
    ExternalIdBundle fixingSeriesId = InterestRateMockSources.getOvernightIndexId().toBundle();

    ImmutableLocalDateDoubleTimeSeries irFutureSeries = ImmutableLocalDateDoubleTimeSeries.of(valuationDate, 0.975);
    ExternalId irFutureId = ExternalSchemes.syntheticSecurityId("Test future");

    MarketDataEnvironmentBuilder builder =
        InterestRateMockSources.createMarketDataEnvironment(valuationDate, false).toBuilder();

    builder.add(RawId.of(irFutureId.toBundle()), irFutureSeries);
    builder.add(RawId.of(fixingSeriesId), fixingSeries);
    builder.add(RawId.of(fixingSeriesId), fixingSeries);
    return new MapMarketDataBundle(builder.build());
  }

  /**
   * Build the curve with Fed Fund futures (including the current one).
   * Re-price futures trade with trade date the calibration date and trade price the calibration price and compare to 0.
   */
  @Test
  public void buildCurve() {
    MarketDataBundle marketDataBundle = createMarketDataBundle();

    Environment env = new SimpleEnvironment(VALUATION_TIME, marketDataBundle);
    Result<MulticurveBundle> pairProviderBlock =
        _curveBundle.generateBundle(env, ConfigLink.resolvable(CURVE_CONSTRUCTION_CONFIGURATION_USD_FFF,
            CurveConstructionConfiguration.class).resolve());
    if (!pairProviderBlock.isSuccess()) {
      fail(pairProviderBlock.getFailureMessage());
    }
    // Re-pricing FF futures trades
    FedFundsFutureTrade[] ffTrades = new FedFundsFutureTrade[NB_TRADE];
    for(int i = 0; i < NB_TRADE; i++) {
      ffTrades[i] = createFFTrade(EXPIRY_DATE[i], EXPECTED_PRICE[i]);
    }
    for(int i = 0; i < NB_TRADE; i++) {
      Result<MultipleCurrencyAmount> resultPVJ4 = _fedFundsFutureFn.calculatePV(env, ffTrades[i]);
      if (resultPVJ4.isSuccess()) {
        MultipleCurrencyAmount mca = resultPVJ4.getValue();
        assertThat("FedFundFutureCurve: node " + i, mca.getCurrencyAmount(Currency.USD).getAmount(), is(closeTo(EXPECTED_PV, TOLERANCE_PV)));
      } else {
        fail(resultPVJ4.getFailureMessage());
      }      
    }
  } 
  
  private FedFundsFutureTrade createFFTrade(LocalDate expiryDate, double tradePrice) {    
    Expiry expiry = new Expiry(ZonedDateTime.of(expiryDate, LocalTime.of(0, 0), ZoneOffset.UTC));
    FederalFundsFutureSecurity fedFundsFuture = 
        new FederalFundsFutureSecurity(expiry, TRADING_EX, SETTLE_EX, CCY, UNIT_AMOUNT, FED_FUND_INDEX_ID, CATEGORY);
    fedFundsFuture.setExternalIdBundle(ExternalSchemes.syntheticSecurityId("Test future").toBundle());
    SimpleTrade trade = new SimpleTrade(fedFundsFuture, TRADE_QUANTITY, COUNTERPARTY, TRADE_DATE, TRADE_TIME);
    trade.setPremiumCurrency(Currency.USD);
    trade.setPremium(tradePrice);
    return new FedFundsFutureTrade(trade);
  }
  
  @AfterClass
  public void tearDown() {
    OpenGammaClock.setInstance(_defaultInstance);
  }
}

