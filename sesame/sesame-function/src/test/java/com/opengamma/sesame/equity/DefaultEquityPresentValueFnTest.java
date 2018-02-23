/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.equity;

import static com.opengamma.util.result.FailureStatus.MISSING_DATA;
import static com.opengamma.util.result.SuccessStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.SimpleEnvironment;
import com.opengamma.sesame.marketdata.DefaultMarketDataFn;
import com.opengamma.sesame.marketdata.MarketDataEnvironment;
import com.opengamma.sesame.marketdata.MarketDataEnvironmentBuilder;
import com.opengamma.sesame.marketdata.SecurityId;
import com.opengamma.util.money.Currency;
import com.opengamma.util.result.Result;
import com.opengamma.util.result.ResultStatus;
import com.opengamma.util.test.TestGroup;

@Test(groups= TestGroup.UNIT)
public class DefaultEquityPresentValueFnTest {

  private EquityPresentValueFn _equityPresentValueFn;

  @BeforeMethod
  public void setUp() {
    _equityPresentValueFn = new DefaultEquityPresentValueFn(new DefaultMarketDataFn());
  }

  @Test
  public void testMarketDataUnavailable() {
    EquitySecurity security = new EquitySecurity("LSE", "LSE", "BloggsCo", Currency.GBP);
    security.setExternalIdBundle(ExternalSchemes.bloombergTickerSecurityId("BLGG").toBundle());
    ZonedDateTime valuationTime = ZonedDateTime.now();
    MarketDataEnvironment emptyEnvironment = new MarketDataEnvironmentBuilder().valuationTime(valuationTime).build();
    Environment env = new SimpleEnvironment(valuationTime, emptyEnvironment.toBundle());
    Result<Double> result = _equityPresentValueFn.presentValue(env, security);
    assertThat(result.getStatus(), is((ResultStatus) MISSING_DATA));
  }

  @Test
  public void testMarketDataAvailable() {
    EquitySecurity security = new EquitySecurity("LSE", "LSE", "BloggsCo", Currency.GBP);
    security.setExternalIdBundle(ExternalSchemes.bloombergTickerSecurityId("BLGG").toBundle());
    ZonedDateTime valuationTime = ZonedDateTime.now();
    MarketDataEnvironmentBuilder builder = new MarketDataEnvironmentBuilder();
    MarketDataEnvironment marketDataEnvironment = builder.add(SecurityId.of(security), 123.45)
                                                         .valuationTime(ZonedDateTime.now())
                                                         .build();
    Environment env = new SimpleEnvironment(valuationTime, marketDataEnvironment.toBundle());
    Result<Double> result = _equityPresentValueFn.presentValue(env, security);
    assertThat(result.getStatus(), is((ResultStatus) SUCCESS));
    assertThat(result.getValue(), is(123.45));
  }

}
