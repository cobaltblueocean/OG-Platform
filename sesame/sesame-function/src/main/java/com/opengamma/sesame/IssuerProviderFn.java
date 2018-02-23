/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import com.opengamma.analytics.financial.forex.method.FXMatrix;
import com.opengamma.core.position.Trade;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.util.result.Result;

/**
 * Creates a multicurve bundle for curves by issuer.
 */
public interface IssuerProviderFn {

  /**
   * Returns the multicurve bundle for curves by issuer for a specified environment, security and FX matrix. This has been
   * deprecated because ExposureFunctions can be selected by trade details such as counterparty or trade attributes.
   *
   *
   * @param env the environment to return the multicurve bundle for.
   * @param security the security to return the multicurve bundle for.
   * @param fxMatrix the FX matrix to include inside the multicurve bundle.
   * @return the multicurve bundle for curves by issuer.
   * 
   * @deprecated use {@link #getMulticurveBundle(Environment, Trade)} with the original trade.
   */
  @Deprecated
  Result<IssuerProviderBundle> createBundle(Environment env, FinancialSecurity security, FXMatrix fxMatrix);

  /**
   * Returns the multicurve bundle for curves by issuer for a specified environment, trade and FX matrix.
   *
   * @param env the environment to return the multicurve bundle for.
   * @param trade the trade to return the multicurve bundle for.
   * @param fxMatrix the FX matrix to include inside the multicurve bundle.
   * @return the multicurve bundle for curves by issuer.
   * @deprecated use {@link #getMulticurveBundle(Environment, Trade)}. The FX matrix argument isn't used in
   *   any implementation if this method and all the callers create an empty matrix just to satisfy the
   *   method signature. Also this method can't be sensibly implemented for the case where pre-calibrated
   *   curves are provided by the caller.
   */
  @Deprecated
  Result<IssuerProviderBundle> createBundle(Environment env, Trade trade, FXMatrix fxMatrix);

  /**
   * Returns the multicurve bundle for curves by issuer for a trade.
   *
   * @param env the environment to return the multicurve bundle for.
   * @param trade the trade to return the multicurve bundle for.
   * @return the multicurve bundle for curves by issuer.
   */
  Result<IssuerProviderBundle> getMulticurveBundle(Environment env, Trade trade);
}
