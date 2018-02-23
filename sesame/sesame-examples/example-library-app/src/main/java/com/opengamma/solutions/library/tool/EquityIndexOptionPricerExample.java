/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.solutions.library.tool;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.opengamma.sesame.engine.Results;
import com.opengamma.solutions.library.engine.EngineModule;
import com.opengamma.solutions.library.storage.InMemoryStorageModule;
import com.opengamma.solutions.library.storage.SourcesModule;
import com.opengamma.solutions.util.ViewUtils;
import com.opengamma.util.time.DateUtils;

/**
 * Equity Index Option example
 */
public class EquityIndexOptionPricerExample {

  private static final Logger s_logger = LoggerFactory.getLogger(EquityIndexOptionPricerExample.class);
  private static final String DISCOUNTING_CURVES = "equity-data/curves/discounting-curves.csv";
  private static final String FORWARD_CURVES = "equity-data/curves/forward-curves.csv";
  private static final String VOLATILITY_SURFACES = "equity-data/vols/vol-surface.csv";
  private static final String PRICE_SURFACES = "equity-data/vols/price-surface.csv";
  private static final String TRADES = "equity-data/trades/equity-index-options.csv";
  private static final List<ZonedDateTime> VALUATION_TIMES =
      ImmutableList.of(DateUtils.getUTCDate(2015, 4, 27),
                       DateUtils.getUTCDate(2015, 4, 28),
                       DateUtils.getUTCDate(2015, 4, 29),
                       DateUtils.getUTCDate(2015, 5, 1),
                       DateUtils.getUTCDate(2015, 6, 1));

  /**
   * Entry point to running the Equity Index Option Pricer.
   * Log PV results to the console
   * @param args, no args are need to run this tool
   */
  public static void main(String[] args) throws IOException {

    Set<Module> modules = Sets.newHashSet();
    modules.add(new InMemoryStorageModule());
    modules.add(new SourcesModule());
    modules.add(new EngineModule());
    Injector injector = Guice.createInjector(modules);

    EquityIndexOptionPricer pricer = injector.getInstance(EquityIndexOptionPricer.class);
    for (ZonedDateTime valuation : VALUATION_TIMES) {
      Results results = pricer.price(valuation,
                                     TRADES,
                                     DISCOUNTING_CURVES,
                                     FORWARD_CURVES,
                                     VOLATILITY_SURFACES,
                                     false);
      s_logger.info(valuation.toLocalDate() + " Got results:\n" + ViewUtils.format(results));
    }

    Results results = pricer.price(VALUATION_TIMES.get(0),
                                   TRADES,
                                   DISCOUNTING_CURVES,
                                   FORWARD_CURVES,
                                   PRICE_SURFACES,
                                   true);
    s_logger.info(VALUATION_TIMES.get(0) + " Got results:\n" + ViewUtils.format(results));

  }

}
