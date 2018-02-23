/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.sesame.credit.measures;

import com.opengamma.financial.security.credit.IndexCDSSecurity;
import com.opengamma.financial.security.credit.LegacyCDSSecurity;
import com.opengamma.financial.security.credit.StandardCDSSecurity;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.trade.IndexCDSTrade;
import com.opengamma.sesame.trade.LegacyCDSTrade;
import com.opengamma.sesame.trade.StandardCDSTrade;
import com.opengamma.util.result.Result;

/**
 * A function which can calculate a credit risk measure.
 * 
 * @param <T> the result type
 */
public interface CreditRiskMeasureFn<T> {

  /**
   * Calculate a risk measure for a standard cds
   * 
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  @Deprecated
  Result<T> priceStandardCds(Environment env, StandardCDSSecurity cds);

  /**
   * Calculate a risk measure for a legacy cds
   * 
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  @Deprecated
  Result<T> priceLegacyCds(Environment env, LegacyCDSSecurity cds);

  /**
   * Calculate a risk measure for a index cds
   *
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  @Deprecated
  Result<T> priceIndexCds(Environment env, IndexCDSSecurity cds);


  /**
   * Calculate a risk measure for a standard cds
   *
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  Result<T> priceStandardCds(Environment env, StandardCDSTrade cds);

  /**
   * Calculate a risk measure for a legacy cds
   *
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  Result<T> priceLegacyCds(Environment env, LegacyCDSTrade cds);

  /**
   * Calculate a risk measure for a index cds
   *
   * @param env the pricing environment
   * @param cds the cds to price
   * @return the result
   */
  Result<T> priceIndexCds(Environment env, IndexCDSTrade cds);
  
}
