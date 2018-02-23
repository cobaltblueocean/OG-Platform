/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.sesame.credit.measures;

import java.math.BigDecimal;
import java.util.Iterator;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetTime;

import com.google.common.collect.ImmutableSortedSet;
import com.opengamma.analytics.financial.credit.isdastandardmodel.CDSAnalytic;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.Trade;
import com.opengamma.core.position.impl.SimpleCounterparty;
import com.opengamma.core.position.impl.SimpleTrade;
import com.opengamma.core.security.Security;
import com.opengamma.financial.analytics.isda.credit.CreditCurveDataKey;
import com.opengamma.financial.security.cds.CDSIndexComponentBundle;
import com.opengamma.financial.security.cds.CreditDefaultSwapIndexComponent;
import com.opengamma.financial.security.credit.IndexCDSDefinitionSecurity;
import com.opengamma.financial.security.credit.IndexCDSSecurity;
import com.opengamma.financial.security.credit.LegacyCDSSecurity;
import com.opengamma.financial.security.credit.StandardCDSSecurity;
import com.opengamma.financial.security.swap.InterestRateNotional;
import com.opengamma.id.ExternalId;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.credit.CdsData;
import com.opengamma.sesame.credit.IsdaCompliantCreditCurveFn;
import com.opengamma.sesame.credit.IsdaCreditCurve;
import com.opengamma.sesame.credit.converter.IndexCdsConverterFn;
import com.opengamma.sesame.credit.converter.LegacyCdsConverterFn;
import com.opengamma.sesame.credit.converter.StandardCdsConverterFn;
import com.opengamma.sesame.credit.market.CreditMarketDataResolverFn;
import com.opengamma.sesame.credit.market.IndexCdsMarketDataResolverFn;
import com.opengamma.sesame.credit.market.LegacyCdsMarketDataResolverFn;
import com.opengamma.sesame.credit.market.StandardCdsMarketDataResolverFn;
import com.opengamma.sesame.trade.IndexCDSTrade;
import com.opengamma.sesame.trade.LegacyCDSTrade;
import com.opengamma.sesame.trade.StandardCDSTrade;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.result.Result;
import com.opengamma.util.time.Tenor;

/**
 * Abstract implementation of a credit risk measure, e.g. pv, cs01, etc. This
 * class enforces a template for the generation of credit risk measures. The
 * standard steps are:
 * <li> resolve market data for security
 * <li> convert security to its analytics type
 * <li> extract any required meta-data into {@link CdsData}
 * <li> price
 * 
 * All steps are implemented except the last.
 * 
 * @param <T> the type of risk measure this function produces
 */
public abstract class AbstractCreditRiskMeasureFn<T> implements CreditRiskMeasureFn<T> {
  
  private final LegacyCdsConverterFn _legacyCdsConverterFn;
  private final StandardCdsConverterFn _standardCdsConverterFn;
  private final IndexCdsConverterFn _indexCdsConverterFn;
  private final StandardCdsMarketDataResolverFn _standardCdsMarketDataResolverFn;
  private final IndexCdsMarketDataResolverFn _indexCdsMarketDataResolverFn;
  private final LegacyCdsMarketDataResolverFn _legacyCdsMarketDataResolverFn;
  private final IsdaCompliantCreditCurveFn _creditCurveFn;
  
  /**
   * Creates an instance.
   * 
   * @param legacyCdsConverterFn a legacy cds converter
   * @param standardCdsConverterFn a standard cds converter
   * @param indexCdsConverterFn a index cds converter
   * @param indexCdsMarketDataResolverFn a market data resolver for index cds
   * @param standardCdsMarketDataResolverFn a market data resolver for standard cds
   * @param legacyCdsMarketDataResolverFn a market data resolver for legacy cds
   * @param creditCurveFn the credit curve function
   */
  protected AbstractCreditRiskMeasureFn(LegacyCdsConverterFn legacyCdsConverterFn, 
                                     StandardCdsConverterFn standardCdsConverterFn,
                                     IndexCdsConverterFn indexCdsConverterFn,
                                     IndexCdsMarketDataResolverFn indexCdsMarketDataResolverFn,
                                     StandardCdsMarketDataResolverFn standardCdsMarketDataResolverFn,
                                     LegacyCdsMarketDataResolverFn legacyCdsMarketDataResolverFn, 
                                     IsdaCompliantCreditCurveFn creditCurveFn) {
    _legacyCdsConverterFn = ArgumentChecker.notNull(legacyCdsConverterFn, "legacyCdsConverterFn");
    _standardCdsConverterFn = ArgumentChecker.notNull(standardCdsConverterFn, "standardCdsConverterFn");
    _indexCdsConverterFn = ArgumentChecker.notNull(indexCdsConverterFn, "indexCdsConverterFn");
    _indexCdsMarketDataResolverFn = ArgumentChecker.notNull(indexCdsMarketDataResolverFn,
                                                               "indexCdsMarketDataResolverFn");
    _standardCdsMarketDataResolverFn = ArgumentChecker.notNull(standardCdsMarketDataResolverFn, 
                                                               "standardCdsMarketDataResolverFn");
    _legacyCdsMarketDataResolverFn = ArgumentChecker.notNull(legacyCdsMarketDataResolverFn, 
                                                             "legacyCdsMarketDataResolverFn");
    _creditCurveFn = ArgumentChecker.notNull(creditCurveFn, "creditCurveFn");
  }

  
  /**
   * Resolve market data for the security using the passed function.
   * 
   * @param env pricing environment
   * @param resolverFn resolver function
   * @param security security
   * @return a resolved credit curve
   */
  private <S> Result<IsdaCreditCurve> resolveMarketData(Environment env, 
                                                        CreditMarketDataResolverFn<S> resolverFn, 
                                                        S security) {
    Result<CreditCurveDataKey> mdKeyResult = resolverFn.resolve(env, security);
    
    if (!mdKeyResult.isSuccess()) {
      return Result.failure(mdKeyResult);
    }
    
    return _creditCurveFn.buildIsdaCompliantCreditCurve(env, mdKeyResult.getValue()); //source from env
    
  }

  @Override
  public Result<T> priceStandardCds(Environment env, StandardCDSTrade trade) {
    StandardCDSSecurity cds = (StandardCDSSecurity) trade.getTrade().getSecurity();
    Result<IsdaCreditCurve> marketDataResult = resolveMarketData(env, _standardCdsMarketDataResolverFn, cds);

    //not much we can do if we can't resolve/build market data
    if (!marketDataResult.isSuccess()) {
      return Result.failure(marketDataResult);
    }
    IsdaCreditCurve creditCurve = marketDataResult.getValue();

    Result<CDSAnalytic> analyticResult = _standardCdsConverterFn.toCdsAnalytic(env, cds, creditCurve);

    if (analyticResult.isSuccess()) {
      return price(extractForStandardCds(cds, creditCurve),
                   analyticResult.getValue(),
                   creditCurve);
    } else {
      return Result.failure(analyticResult);
    }
  }

  @Override
  public Result<T> priceLegacyCds(Environment env, LegacyCDSTrade trade) {
    LegacyCDSSecurity cds = (LegacyCDSSecurity) trade.getTrade().getSecurity();
    Result<IsdaCreditCurve> marketDataResult = resolveMarketData(env, _legacyCdsMarketDataResolverFn, cds);

    //not much we can do if we can't resolve/build market data
    if (!marketDataResult.isSuccess()) {
      return Result.failure(marketDataResult);
    }

    IsdaCreditCurve creditCurve = marketDataResult.getValue();
    Result<CDSAnalytic> analyticResult = _legacyCdsConverterFn.toCdsAnalytic(env, cds, creditCurve);

    if (analyticResult.isSuccess()) {
      return price(extractForLegacyCds(cds, creditCurve),
                   analyticResult.getValue(),
                   creditCurve);
    } else {
      return Result.failure(analyticResult);
    }
  }

  @Override
  public Result<T> priceIndexCds(Environment env, IndexCDSTrade trade) {
    IndexCDSSecurity cds = (IndexCDSSecurity) trade.getTrade().getSecurity();
    Result<IsdaCreditCurve> marketDataResult = resolveMarketData(env, _indexCdsMarketDataResolverFn, cds);

    //not much we can do if we can't resolve/build market data
    if (!marketDataResult.isSuccess()) {
      return Result.failure(marketDataResult);
    }
    IsdaCreditCurve creditCurve = marketDataResult.getValue();

    Result<CDSAnalytic> analyticResult = _indexCdsConverterFn.toCdsAnalytic(env, cds, creditCurve);

    if (analyticResult.isSuccess()) {
      return price(extractForIndexCds(cds, creditCurve),
                   analyticResult.getValue(),
                   creditCurve);
    } else {
      return Result.failure(analyticResult);
    }
  }

  @Override
  public Result<T> priceStandardCds(Environment env, StandardCDSSecurity cds) {
    StandardCDSTrade tradeWrapper = new StandardCDSTrade(buildTrade(cds));
    return priceStandardCds(env, tradeWrapper);
  }

  @Override
  public Result<T> priceLegacyCds(Environment env, LegacyCDSSecurity cds) {
    LegacyCDSTrade tradeWrapper = new LegacyCDSTrade(buildTrade(cds));
    return priceLegacyCds(env, tradeWrapper);
  }

  @Override
  public Result<T> priceIndexCds(Environment env, IndexCDSSecurity cds) {
    IndexCDSTrade tradeWrapper = new IndexCDSTrade(buildTrade(cds));
    return priceIndexCds(env, tradeWrapper);
  }
 
  private Trade buildTrade(Security security) {
    return new SimpleTrade(security,
        BigDecimal.ONE,
        new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "CPARTY")),
        LocalDate.now(),
        OffsetTime.now());
  }
  
  /**
   * Produce the risk measure for this instance using the analytics
   * types passed.
   * 
   * @param cdsData cds data
   * @param cdsAnalytic the cds analytic constructed
   * @param curve the resolved credit curve
   * @return a result of the appropriate type
   */
  protected abstract Result<T> price(CdsData cdsData, 
                                     CDSAnalytic cdsAnalytic,
                                     IsdaCreditCurve curve);
  
  
  /**
   * Extracts relevant fields from standard cds security to CdsData.
   * 
   *
   * @param cds the standard cds
   * @param creditCurve
   * @return a CdsData instance
   */
  private CdsData extractForStandardCds(StandardCDSSecurity cds, IsdaCreditCurve creditCurve) {
    ImmutableSortedSet<Tenor> tenors = creditCurve.getCurveData().getCdsQuotes().keySet();
    return CdsData.builder()
        .coupon(cds.getCoupon())
        .tenors(tenors)
        .interestRateNotional(cds.getNotional())
        .buy(cds.isBuyProtection())
        .build();
  }

  /**
   * Extracts relevant fields from index cds security to CdsData.
   * Scaling the notional by the index factor, based on the combined weight
   * of the index basket {@link CDSIndexComponentBundle}
   *
   * @param cds the index cds
   * @return a CdsData instance
   */
  private CdsData extractForIndexCds(IndexCDSSecurity cds, IsdaCreditCurve creditCurve) {
    double indexFactor = 0d;
    IndexCDSDefinitionSecurity definition = cds.getUnderlyingIndex().resolve();
    CDSIndexComponentBundle components = definition.getComponents();
    if (components.isEmpty()) {
      indexFactor = 1d;
    } else {
      Iterator<CreditDefaultSwapIndexComponent> it = components.iterator();
      while (it.hasNext()) {
        indexFactor += it.next().getWeight();
      }
    }

    InterestRateNotional notional = cds.getNotional();
    InterestRateNotional scaledNotional = new InterestRateNotional(notional.getCurrency(),
                                                                   notional.getAmount() * indexFactor);
    ImmutableSortedSet<Tenor> tenors = creditCurve.getCurveData().getCdsQuotes().keySet();
    return CdsData.builder()
        .coupon(definition.getCoupon())
        .tenors(tenors)
        .interestRateNotional(scaledNotional)
        .buy(cds.isBuyProtection())
        .build();
  }
  
  /**
   * Extracts relevant fields from legacy cds security to CdsData.
   * 
   * @param cds the legacy cds
   * @return a CdsData instance
   */
  private CdsData extractForLegacyCds(LegacyCDSSecurity cds, IsdaCreditCurve creditCurve) {
    ImmutableSortedSet<Tenor> tenors = creditCurve.getCurveData().getCdsQuotes().keySet();
    return CdsData.builder()
        .coupon(cds.getCoupon())
        .tenors(tenors)
        .interestRateNotional(cds.getNotional())
        .buy(cds.isBuyProtection())
        .build();
  }
  
}
