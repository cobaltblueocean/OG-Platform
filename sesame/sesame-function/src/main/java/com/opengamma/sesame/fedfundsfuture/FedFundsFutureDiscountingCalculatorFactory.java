/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.fedfundsfuture;

import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.financial.analytics.conversion.FederalFundsFutureTradeConverter;
import com.opengamma.financial.analytics.conversion.FixedIncomeConverterDataProvider;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesBundle;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.sesame.DiscountingMulticurveCombinerFn;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.FixingsFn;
import com.opengamma.sesame.MulticurveBundle;
import com.opengamma.sesame.trade.FedFundsFutureTrade;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.result.Result;

/**
 * Discounting calculator factory for federal funds futures.
 */
public class FedFundsFutureDiscountingCalculatorFactory implements FedFundsFutureCalculatorFactory {

  private final FederalFundsFutureTradeConverter _converter;
  
  private final FixedIncomeConverterDataProvider _definitionToDerivativeConverter;
  
  private final DiscountingMulticurveCombinerFn _discountingMulticurveCombinerFn;
  
  private final FixingsFn _fixingsFn;
  
  /**
   * Constructs a factory that creates discounting calculators for federal funds futures.
   *
   * @param converter the converter used to convert the OG-Financial federal funds future to the OG-Analytics definition.
   * @param definitionToDerivativeConverter the converter used to convert the definition to derivative.
   * @param discountingMulticurveCombinerFn the multicurve function.
   * @param fixingsFn the historical time series function, not null.
   */
  public FedFundsFutureDiscountingCalculatorFactory(FederalFundsFutureTradeConverter converter,
                                                    FixedIncomeConverterDataProvider definitionToDerivativeConverter,
                                                    DiscountingMulticurveCombinerFn discountingMulticurveCombinerFn,
                                                    FixingsFn fixingsFn) {
    _converter = ArgumentChecker.notNull(converter, "converter");
    _definitionToDerivativeConverter =
        ArgumentChecker.notNull(definitionToDerivativeConverter, "definitionToDerivativeConverter");
    _discountingMulticurveCombinerFn =
        ArgumentChecker.notNull(discountingMulticurveCombinerFn, "discountingMulticurveCombinerFn");
    _fixingsFn = ArgumentChecker.notNull(fixingsFn, "htsFn");
  }
  
  @Override
  public Result<FedFundsFutureCalculator> createCalculator(Environment env, FedFundsFutureTrade trade) {

    FinancialSecurity security = trade.getSecurity();
    
    Result<MulticurveBundle> bundleResult = _discountingMulticurveCombinerFn.getMulticurveBundle(env, trade);

    Result<HistoricalTimeSeriesBundle> fixingsResult = _fixingsFn.getFixingsForSecurity(env, security);
    
    if (Result.allSuccessful(bundleResult, fixingsResult)) {
    
      MulticurveProviderDiscount bundle = bundleResult.getValue().getMulticurveProvider();
    
      HistoricalTimeSeriesBundle fixings = fixingsResult.getValue();
    
      FedFundsFutureCalculator calculator = new FedFundsFutureDiscountingCalculator(trade, bundle, _converter, env.getValuationTime(), _definitionToDerivativeConverter, fixings);
      
      return Result.success(calculator);
      
    } else {
      
      return Result.failure(bundleResult, fixingsResult);
    }
  }

}
