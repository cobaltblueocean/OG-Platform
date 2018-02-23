/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.credit.measures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableMap;
import com.opengamma.analytics.financial.model.interestrate.curve.DiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.provider.curve.CurveBuildingBlockBundle;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.analytics.math.curve.InterpolatedDoublesCurve;
import com.opengamma.analytics.math.interpolation.CombinedInterpolatorExtrapolatorFactory;
import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.engine.marketdata.spec.LiveMarketDataSpecification;
import com.opengamma.financial.security.credit.IndexCDSSecurity;
import com.opengamma.financial.security.credit.StandardCDSSecurity;
import com.opengamma.service.ServiceContext;
import com.opengamma.service.ThreadLocalServiceContext;
import com.opengamma.service.VersionCorrectionProvider;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.MulticurveBundle;
import com.opengamma.sesame.config.FunctionModelConfig;
import com.opengamma.sesame.credit.CreditPricingSampleData;
import com.opengamma.sesame.engine.CalculationArguments;
import com.opengamma.sesame.engine.ComponentMap;
import com.opengamma.sesame.engine.FixedInstantVersionCorrectionProvider;
import com.opengamma.sesame.engine.FunctionRunner;
import com.opengamma.sesame.graph.FunctionModel;
import com.opengamma.sesame.interestrate.InterestRateMockSources;
import com.opengamma.sesame.marketdata.EmptyMarketDataFactory;
import com.opengamma.sesame.marketdata.EmptyMarketDataSpec;
import com.opengamma.sesame.marketdata.MarketDataEnvironment;
import com.opengamma.sesame.marketdata.MarketDataEnvironmentBuilder;
import com.opengamma.sesame.marketdata.MarketDataFactory;
import com.opengamma.sesame.marketdata.MulticurveId;
import com.opengamma.sesame.marketdata.builders.MarketDataBuilders;
import com.opengamma.sesame.marketdata.builders.MarketDataEnvironmentFactory;
import com.opengamma.sesame.trade.IndexCDSTrade;
import com.opengamma.sesame.trade.StandardCDSTrade;
import com.opengamma.util.function.Function;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.result.Result;
import com.opengamma.util.test.TestGroup;
import com.opengamma.util.time.DateUtils;

/**
 * Test the CDS PV using the mapping ISDA compliant yield curve provider
 * Pricing CDS tenor P5Y - validated in CDSYieldCurveExampleTest
 * log-linear: pv = -37154.039151	cs01 = 531.936202
 */
@Test(groups = TestGroup.UNIT)
public class MappingIsdaCompliantYieldCurveFnTest {

  private static final ZonedDateTime VALUATION_TIME = DateUtils.getUTCDate(2014, 10, 16);
  private static final double STD_TOLERANCE_PV = 1.0E-3;
  // Validated in OG Analytics
  public static final double SINGLE_NAME_EXPECTED_PV =  -37154.043;
  private static final double BP = 1e-3;
  private static final CalculationArguments ARGS =
      CalculationArguments.builder()
          .valuationTime(VALUATION_TIME)
          .marketDataSpecification(EmptyMarketDataSpec.INSTANCE)
          .build();

  private FunctionRunner _functionRunner;
  private DefaultCreditPvFn _pvFunction;

  private static final Interpolator1D s_interpolator =
      CombinedInterpolatorExtrapolatorFactory.getInterpolator("LogNaturalCubicWithMonotonicity",
                                                              "QuadraticLeftExtrapolator",
                                                              "LinearExtrapolator");

  @BeforeMethod
  public void setUpClass()  {
    FunctionModelConfig config = CreditPricingSampleData.createYCMappingFunctionModelConfig();
    ImmutableMap<Class<?>, Object> components = InterestRateMockSources.generateBaseComponents();
    VersionCorrectionProvider vcProvider = new FixedInstantVersionCorrectionProvider(Instant.now());
    ServiceContext serviceContext = ServiceContext.of(components).with(VersionCorrectionProvider.class, vcProvider);
    ThreadLocalServiceContext.init(serviceContext);
    ComponentMap componentMap = ComponentMap.of(components);
    EmptyMarketDataFactory dataFactory = new EmptyMarketDataFactory();
    MarketDataEnvironmentFactory environmentFactory =
        new MarketDataEnvironmentFactory(dataFactory,
                                         MarketDataBuilders.creditCurve(),
                                         MarketDataBuilders.isdaYieldCurve());
    _functionRunner = new FunctionRunner(environmentFactory);
    _pvFunction = FunctionModel.build(DefaultCreditPvFn.class, config, componentMap);
  }

  private MarketDataEnvironment getSuppliedData(MulticurveBundle multicurveBundle) {

    MulticurveId multicurveId = MulticurveId.of("Curve Bundle");
    return new MarketDataEnvironmentBuilder()
        .add(multicurveId, multicurveBundle)
        .add(CreditPricingSampleData.getCreditCurveDataSnapshotId(), CreditPricingSampleData.createCreditCurveDataSnapshot())
        .valuationTime(VALUATION_TIME)
        .build();
  }


  @Test
  public void testDiscountingStandardCdsPV() {

    final StandardCDSTrade trade = CreditPricingSampleData.createStandardCDSSecurity();
    StandardCDSSecurity security = (StandardCDSSecurity) trade.getTrade().getSecurity();
    double tolerance = security.getNotional().getAmount() * BP;
    Result<CurrencyAmount> result = _functionRunner.runFunction(ARGS, getSuppliedData(getDiscountCurveBundle()), new Function<Environment, Result<CurrencyAmount>>() {
      @Override
      public Result<CurrencyAmount> apply(Environment env) {
        return _pvFunction.priceStandardCds(env, trade);
      }
    });

    assertThat(result.isSuccess(), is(true));
    CurrencyAmount amount = result.getValue();
    assertThat(amount.getCurrency(), is(Currency.USD));
    assertThat(amount.getAmount(), is(closeTo(CreditPvFnTest.SINGLE_NAME_EXPECTED_PV, tolerance)));

    assertThat(amount.getAmount(), is(closeTo(SINGLE_NAME_EXPECTED_PV, STD_TOLERANCE_PV)));

  }

  @Test
  public void testYieldCurveStandardCdsPV() {

    final StandardCDSTrade trade = CreditPricingSampleData.createStandardCDSSecurity();
    StandardCDSSecurity security = (StandardCDSSecurity) trade.getTrade().getSecurity();
    double tolerance = security.getNotional().getAmount() * BP;
    Result<CurrencyAmount> result = _functionRunner.runFunction(ARGS, getSuppliedData(getYieldCurveBundle()), new Function<Environment, Result<CurrencyAmount>>() {
      @Override
      public Result<CurrencyAmount> apply(Environment env) {
        return _pvFunction.priceStandardCds(env, trade);
      }
    });

    assertThat(result.isSuccess(), is(true));
    CurrencyAmount amount = result.getValue();
    assertThat(amount.getCurrency(), is(Currency.USD));
    assertThat(amount.getAmount(), is(closeTo(CreditPvFnTest.SINGLE_NAME_EXPECTED_PV, tolerance)));

    assertThat(amount.getAmount(), is(closeTo(SINGLE_NAME_EXPECTED_PV, STD_TOLERANCE_PV)));

  }

  @Test
  public void testDiscountingIndexCdsPV() {

    final IndexCDSTrade trade = CreditPricingSampleData.createIndexCDSSecurity();
    IndexCDSSecurity security = (IndexCDSSecurity) trade.getTrade().getSecurity();
    double tolerance = security.getNotional().getAmount() * BP;
    Result<CurrencyAmount> result = _functionRunner.runFunction(ARGS, getSuppliedData(getDiscountCurveBundle()), new Function<Environment, Result<CurrencyAmount>>() {
      @Override
      public Result<CurrencyAmount> apply(Environment env) {
        return _pvFunction.priceIndexCds(env, trade);
      }
    });

    assertThat(result.isSuccess(), is(true));
    CurrencyAmount amount = result.getValue();
    assertThat(amount.getCurrency(), is(Currency.USD));
    assertThat(amount.getAmount(), is(closeTo(CreditPvFnTest.INDEX_EXPECTED_PV, tolerance)));

  }

  private static final double[] DAY = new double[] {91, 183, 274, 365, 457, 548, 639, 731, 1096, 1461, 1826, 2192,
      2557, 2922, 3287, 3653, 4383, 5479, 7305, 9131, 10958, 14610, 18263 };
  private static final int NUM_NODE = DAY.length;
  private static final double[] TIME;
  static {
    TIME = new double[NUM_NODE];
    for (int i = 0; i < NUM_NODE; ++i) {
      TIME[i] = DAY[i] / 365.;
    }
  }

  private static final double[] ZERO_RATES = new double[] {6.85141288906939E-4, 6.927004450275529E-4,
      7.052829705582995E-4, 7.229431397828344E-4, 7.47375407385545E-4, 7.834380855687433E-4,
      8.347416042149961E-4, 9.038326597250597E-4, 0.0013722796143422762, 0.002176305350143962,
      0.0032811647971705956, 0.00460755549100302, 0.006040682945580618, 0.00750878742104914,
      0.008941312543172845, 0.010292758463102044, 0.012637449883912839, 0.015245511253328924,
      0.01770642792742225, 0.018777178300023893, 0.01925642557322497, 0.01971509345664727,
      0.019728349624112827};


  private static final double[] DISCOUNT_FACTOR = new double[] {0.999829198540859, 0.9996527611741278,
      0.9994706948329838, 0.9992773181206462, 0.999064682720019, 0.9988244611565431, 0.9985396977323168,
      0.9981914957448704, 0.9958878795040178, 0.9913266485465384, 0.9837191754362143, 0.9727087439132421,
      0.9585650232460775, 0.9416595859820656, 0.922635706673141, 0.90211598325509, 0.8592002467360544,
      0.7954480462459721, 0.7016145412095421, 0.6251659631564156, 0.5609548680303263, 0.45423350825036723,
      0.3726483241596532 };

  private static MulticurveBundle getDiscountCurveBundle() {

    String curveName = "USD DISC";
    InterpolatedDoublesCurve rawCurve = InterpolatedDoublesCurve.from(TIME, DISCOUNT_FACTOR, s_interpolator, curveName);
    YieldAndDiscountCurve yc = new DiscountCurve(curveName, rawCurve);
    MulticurveProviderDiscount multicurveProvider = new MulticurveProviderDiscount();
    CurveBuildingBlockBundle curveBuildingBlockBundle = new CurveBuildingBlockBundle();
    multicurveProvider.setCurve(Currency.USD, yc);
    return new MulticurveBundle(multicurveProvider, curveBuildingBlockBundle);

  }

  private static MulticurveBundle getYieldCurveBundle() {

    String curveName = "USD DISC";
    InterpolatedDoublesCurve rawCurve = InterpolatedDoublesCurve.from(TIME, ZERO_RATES, s_interpolator, curveName);
    YieldAndDiscountCurve yc = new YieldCurve(curveName, rawCurve);
    MulticurveProviderDiscount multicurveProvider = new MulticurveProviderDiscount();
    CurveBuildingBlockBundle curveBuildingBlockBundle = new CurveBuildingBlockBundle();
    multicurveProvider.setCurve(Currency.USD, yc);
    return new MulticurveBundle(multicurveProvider, curveBuildingBlockBundle);

  }

}
