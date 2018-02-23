/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.solutions.library.engine;

import java.util.concurrent.ExecutorService;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.opengamma.core.link.ConfigLink;
import com.opengamma.core.marketdatasnapshot.MarketDataSnapshotSource;
import com.opengamma.core.security.Security;
import com.opengamma.financial.currency.CurrencyMatrix;
import com.opengamma.sesame.bondfuture.BondFutureFn;
import com.opengamma.sesame.bondfuture.BondFutureFn;
import com.opengamma.sesame.bondfutureoption.BondFutureOptionFn;
import com.opengamma.sesame.credit.measures.CreditCs01Fn;
import com.opengamma.sesame.credit.measures.CreditPvFn;
import com.opengamma.sesame.engine.ComponentMap;
import com.opengamma.sesame.engine.DefaultEngine;
import com.opengamma.sesame.engine.Engine;
import com.opengamma.sesame.engine.ViewFactory;
import com.opengamma.sesame.fra.FRAFn;
import com.opengamma.sesame.equityindexoptions.EquityIndexOptionFn;
import com.opengamma.sesame.function.AvailableOutputs;
import com.opengamma.sesame.function.AvailableOutputsImpl;
import com.opengamma.sesame.irfuture.InterestRateFutureFn;
import com.opengamma.sesame.irs.InterestRateSwapFn;
import com.opengamma.sesame.marketdata.SnapshotMarketDataFactory;
import com.opengamma.sesame.marketdata.builders.MarketDataBuilders;
import com.opengamma.sesame.marketdata.builders.MarketDataEnvironmentFactory;
import com.opengamma.sesame.trade.TradeWrapper;

/**
 * Configures a {@link Engine} instance and associated objects.
 */
public class EngineModule extends AbstractModule {

  @Override
  protected void configure() {
    // force componentMap and hence the threadLocal ServiceContext to be set now
    // (Some modules assume ServiceContext is set without declaring a dependency)
    bind(ComponentMap.class).toProvider(ComponentMapProvider.class).asEagerSingleton();
    bind(ViewFactory.class).toProvider(ViewFactoryProvider.class);
  }
  
  /**
   * Create the available outputs.
   * 
   * @return the available outputs, not null
   */
  @Provides
  public AvailableOutputs createAvailableOutputs() {
    AvailableOutputs available = new AvailableOutputsImpl(ImmutableSet.of(Security.class, TradeWrapper.class));
    available.register(CreditCs01Fn.class);
    available.register(CreditPvFn.class);
    available.register(InterestRateSwapFn.class);
    available.register(FRAFn.class);
    available.register(InterestRateFutureFn.class);
    available.register(EquityIndexOptionFn.class);
    available.register(BondFutureOptionFn.class);
    available.register(BondFutureFn.class);
    return available;
  }

  /**
   * Create the engine instance
   *
   * @param viewFactory the view factory
   * @param marketData the MarketDataEnvironmentFactory
   * @param service the executor service
   * @return the engine
   */
  @Provides
  @Singleton
  public Engine createEngine(ViewFactory viewFactory, MarketDataEnvironmentFactory marketData, 
      ExecutorService service) {
    return new DefaultEngine(viewFactory, marketData, service);
  }

  /**
   * Create the MarketDataEnvironmentFactory instance
   *
   * @param componentMap the ComponentMap
   * @param snapshotSource the snapshot source
   * @return the MarketDataEnvironmentFactory
   */
  @Provides
  @Singleton
  public MarketDataEnvironmentFactory createEngine(ComponentMap componentMap, MarketDataSnapshotSource snapshotSource) {
    String currencyMatrixName = "CurrencyMatrix";
    ConfigLink<CurrencyMatrix> currencyMatrixLink  = ConfigLink.resolvable(currencyMatrixName, CurrencyMatrix.class);

    return new MarketDataEnvironmentFactory(
        new SnapshotMarketDataFactory(snapshotSource), 
        MarketDataBuilders.raw(componentMap, "DEFAULT"),
        MarketDataBuilders.multicurve(componentMap, currencyMatrixLink),
        MarketDataBuilders.fxMatrix());
  }

  /**
   * Create the ExecutorService instance,
   * a single thread executor is used here to aid clarity in debugging the examples
   *
   * @return the ExecutorService
   */
  @Provides
  @Singleton
  public ExecutorService createExecutorService() {
    return MoreExecutors.sameThreadExecutor();
  }


}
