/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import java.util.List;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.sesame.function.scenarios.FilteredScenarioDefinition;
import com.opengamma.sesame.function.scenarios.ScenarioArgument;
import com.opengamma.sesame.function.scenarios.ScenarioFunction;
import com.opengamma.sesame.marketdata.MarketDataBundle;

/**
 * The execution environment for functions, includes the valuation time and market data.
 */
public interface Environment {

  /**
   * Gets the valuation date.
   * <p>
   * This method should be used in preference to {@link #getValuationTime()} if only the date is required.
   * 
   * @return the valuation date, not null
   */
  LocalDate getValuationDate();

  /**
   * Gets the valuation time.
   * <p>
   * Use {@link #getValuationDate()} in preference to this method if you only need the date.
   * 
   * @return the valuation time, not null
   */
  ZonedDateTime getValuationTime();

  /**
   * Gets the bundle containing market data.
   *
   * @return the market data bundle, not null
   */
  MarketDataBundle getMarketDataBundle();

  /**
   * Returns the scenario arguments for the specified function.
   *
   * @param <A> the argument type
   * @param <F> the function type
   * @param scenarioFunction  the scenario function to retrieve the arguments for
   * @return  the scenario arguments for the specified function
   */
  <A extends ScenarioArgument<A, F>, F extends ScenarioFunction<A, F>> List<A> getScenarioArguments(
      ScenarioFunction<A, F> scenarioFunction);

  /**
   * @return  the environment's scenario definition
   *
   * TODO is there a nice way to avoid exposing this to the functions?
   * make Environment an abstract class and make this package private? would have to re-jig the packages
   * move to a subtype and downcast in the proxy?
   */
  FilteredScenarioDefinition getScenarioDefinition();

  /**
   * Returns a new environment copied from this environment but with a different
   * valuation time. This will automatically switch the market data source to one
   * appropriate to the valuation data. If this behaviour is not wanted then
   * it can be avoided by calling {@link #withValuationTimeAndFixedMarketData(ZonedDateTime)}.
   *
   * @param valuationTime  the valuation time for the new environment
   * @return a new environment copied from this environment but with the specified
   * valuation time and appropriate market data source
   */
  Environment withValuationTime(ZonedDateTime valuationTime);

  // TODO do we need this method?
  /**
   * Returns a new environment copied from this environment but with a different
   * valuation time. Unlike {@link #withValuationTime(ZonedDateTime)}, this method
   * will use the same market data source as the original environment.
   *
   * @param valuationTime  the valuation time for the new environment
   * @return a new environment copied from this environment but with the specified
   * valuation time
   */
  Environment withValuationTimeAndFixedMarketData(ZonedDateTime valuationTime);

  /**
   * Returns a new environment copied from this environment but with altered
   * market data. Note that the general intention is that this is used to
   * alter existing input data and it should not be used to attempt to retrieve
   * new external data.
   *
   * @param marketData  the market data for the new environment, not null
   * @return a new environment copied from this environment but with the specified market data, not null
   */
  Environment withMarketData(MarketDataBundle marketData);

  /**
   * Returns a new environment copied from this one but with a different scenario definition.
   *
   * @param scenarioDefinition the scenario definition
   * @return an environment copied from this one but with a different scenario definition
   */
  Environment withScenarioDefinition(FilteredScenarioDefinition scenarioDefinition);
}
