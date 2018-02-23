/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.sources;

import static com.opengamma.sesame.config.ConfigBuilder.argument;
import static com.opengamma.sesame.config.ConfigBuilder.arguments;
import static com.opengamma.sesame.config.ConfigBuilder.config;
import static com.opengamma.sesame.config.ConfigBuilder.function;
import static com.opengamma.sesame.config.ConfigBuilder.implementations;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.testng.internal.annotations.Sets;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.Period;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opengamma.analytics.financial.legalentity.LegalEntity;
import com.opengamma.analytics.financial.legalentity.LegalEntityFilter;
import com.opengamma.analytics.financial.legalentity.LegalEntityShortName;
import com.opengamma.analytics.math.interpolation.Interpolator1DFactory;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.core.config.impl.ConfigItem;
import com.opengamma.core.convention.ConventionSource;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.holiday.impl.WeekendHolidaySource;
import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.legalentity.LegalEntitySource;
import com.opengamma.core.link.ConfigLink;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.impl.SimpleCounterparty;
import com.opengamma.core.position.impl.SimpleTrade;
import com.opengamma.core.region.RegionSource;
import com.opengamma.core.region.impl.SimpleRegion;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.financial.analytics.curve.ConfigDBCurveConstructionConfigurationSource;
import com.opengamma.financial.analytics.curve.CurveConstructionConfiguration;
import com.opengamma.financial.analytics.curve.CurveConstructionConfigurationSource;
import com.opengamma.financial.analytics.curve.CurveGroupConfiguration;
import com.opengamma.financial.analytics.curve.CurveNodeIdMapper;
import com.opengamma.financial.analytics.curve.CurveTypeConfiguration;
import com.opengamma.financial.analytics.curve.DiscountingCurveTypeConfiguration;
import com.opengamma.financial.analytics.curve.InterpolatedCurveDefinition;
import com.opengamma.financial.analytics.curve.IssuerCurveTypeConfiguration;
import com.opengamma.financial.analytics.curve.exposure.ExposureFunctions;
import com.opengamma.financial.analytics.ircurve.CurveInstrumentProvider;
import com.opengamma.financial.analytics.ircurve.StaticCurveInstrumentProvider;
import com.opengamma.financial.analytics.ircurve.strips.BillNode;
import com.opengamma.financial.analytics.ircurve.strips.BondNode;
import com.opengamma.financial.analytics.ircurve.strips.CurveNode;
import com.opengamma.financial.analytics.ircurve.strips.PeriodicallyCompoundedRateNode;
import com.opengamma.financial.convention.ConventionBundle;
import com.opengamma.financial.convention.ConventionBundleImpl;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.convention.InMemoryConventionBundleMaster;
import com.opengamma.financial.convention.businessday.ModifiedFollowingBusinessDayConvention;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.financial.convention.yield.SimpleYieldConvention;
import com.opengamma.financial.convention.yield.YieldConvention;
import com.opengamma.financial.currency.CurrencyMatrix;
import com.opengamma.financial.security.bond.BillSecurity;
import com.opengamma.financial.security.bond.BondSecurity;
import com.opengamma.financial.security.bond.CorporateBondSecurity;
import com.opengamma.financial.security.bond.GovernmentBondSecurity;
import com.opengamma.financial.security.future.BondFutureDeliverable;
import com.opengamma.financial.security.future.BondFutureSecurity;
import com.opengamma.financial.security.option.BondFutureOptionSecurity;
import com.opengamma.financial.security.option.EuropeanExerciseType;
import com.opengamma.financial.security.option.ExerciseType;
import com.opengamma.financial.security.option.OptionType;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.config.impl.InMemoryConfigMaster;
import com.opengamma.master.config.impl.MasterConfigSource;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesSelector;
import com.opengamma.master.historicaltimeseries.ManageableHistoricalTimeSeriesInfo;
import com.opengamma.master.historicaltimeseries.impl.DefaultHistoricalTimeSeriesResolver;
import com.opengamma.master.historicaltimeseries.impl.InMemoryHistoricalTimeSeriesMaster;
import com.opengamma.master.historicaltimeseries.impl.MasterHistoricalTimeSeriesSource;
import com.opengamma.master.legalentity.LegalEntityDocument;
import com.opengamma.master.legalentity.LegalEntityMaster;
import com.opengamma.master.legalentity.ManageableLegalEntity;
import com.opengamma.master.legalentity.impl.InMemoryLegalEntityMaster;
import com.opengamma.master.legalentity.impl.MasterLegalEntitySource;
import com.opengamma.master.security.SecurityDocument;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.impl.InMemorySecurityMaster;
import com.opengamma.master.security.impl.MasterSecuritySource;
import com.opengamma.sesame.CurveDefinitionCurveLabellingFn;
import com.opengamma.sesame.CurveDefinitionFn;
import com.opengamma.sesame.CurveLabellingFn;
import com.opengamma.sesame.CurveNodeConverterFn;
import com.opengamma.sesame.CurveSpecificationFn;
import com.opengamma.sesame.CurveSpecificationMarketDataFn;
import com.opengamma.sesame.DefaultCurveDefinitionFn;
import com.opengamma.sesame.DefaultCurveNodeConverterFn;
import com.opengamma.sesame.DefaultCurveSpecificationFn;
import com.opengamma.sesame.DefaultCurveSpecificationMarketDataFn;
import com.opengamma.sesame.DefaultDiscountingIssuerProviderBundleFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleFn;
import com.opengamma.sesame.DefaultFXMatrixFn;
import com.opengamma.sesame.DefaultFixingsFn;
import com.opengamma.sesame.DiscountingMulticurveBundleFn;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.ExposureFunctionsIssuerProviderFn;
import com.opengamma.sesame.FXMatrixFn;
import com.opengamma.sesame.FixingsFn;
import com.opengamma.sesame.InterpolatedIssuerBundleFn;
import com.opengamma.sesame.IssuerProviderBundleFn;
import com.opengamma.sesame.IssuerProviderFn;
import com.opengamma.sesame.MarketExposureSelector;
import com.opengamma.sesame.RootFinderConfiguration;
import com.opengamma.sesame.SimpleEnvironment;
import com.opengamma.sesame.bond.BondFn;
import com.opengamma.sesame.bond.DiscountingBondFn;
import com.opengamma.sesame.bondfuture.BondFutureCalculatorFactory;
import com.opengamma.sesame.bondfuture.BondFutureDiscountingCalculatorFactory;
import com.opengamma.sesame.bondfuture.BondFutureFn;
import com.opengamma.sesame.bondfuture.DefaultBondFutureFn;
import com.opengamma.sesame.bondfutureoption.BlackBondFuturesProviderFn;
import com.opengamma.sesame.bondfutureoption.BlackExpStrikeBondFuturesProviderFn;
import com.opengamma.sesame.bondfutureoption.BondFutureOptionBlackCalculatorFactory;
import com.opengamma.sesame.bondfutureoption.BondFutureOptionCalculatorFactory;
import com.opengamma.sesame.bondfutureoption.BondFutureOptionFn;
import com.opengamma.sesame.bondfutureoption.DefaultBondFutureOptionFn;
import com.opengamma.sesame.bondfutureoption.TestBlackBondFuturesProviderFn;
import com.opengamma.sesame.cache.FunctionCache;
import com.opengamma.sesame.cache.NoOpFunctionCache;
import com.opengamma.sesame.component.RetrievalPeriod;
import com.opengamma.sesame.component.StringSet;
import com.opengamma.sesame.config.FunctionModelConfig;
import com.opengamma.sesame.marketdata.DefaultHistoricalMarketDataFn;
import com.opengamma.sesame.marketdata.DefaultMarketDataFn;
import com.opengamma.sesame.marketdata.FieldName;
import com.opengamma.sesame.marketdata.HistoricalMarketDataFn;
import com.opengamma.sesame.marketdata.MarketDataBundle;
import com.opengamma.sesame.marketdata.MarketDataEnvironment;
import com.opengamma.sesame.marketdata.MarketDataEnvironmentBuilder;
import com.opengamma.sesame.marketdata.MarketDataFn;
import com.opengamma.sesame.marketdata.RawId;
import com.opengamma.sesame.marketdata.SecurityId;
import com.opengamma.sesame.trade.BondFutureOptionTrade;
import com.opengamma.sesame.trade.BondFutureTrade;
import com.opengamma.sesame.trade.BondTrade;
import com.opengamma.timeseries.date.localdate.ImmutableLocalDateDoubleTimeSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.time.Expiry;
import com.opengamma.util.time.Tenor;

/**
 * Unit test helper to mock sources for bond pricing.
 */
public class BondMockSources {
  
  private static final ChangeManager MOCK_CHANGE_MANAGER = mock(ChangeManager.class);

  /*Static data*/
  private static final String TICKER = "Ticker";
  private static final String GOVERNMENT_BOND_ISSUER_KEY = "UK GOVERNMENT";
  private static final String CORPORATE_BOND_ISSUER_KEY = "TELECOM ITALIA SPA";
  private static final String BOND_PRE_CALIBRATED_EXPOSURE_FUNCTIONS = "Test Bond Exposure Functions for pre-calibrated curves";
  public static final String BOND_EXPOSURE_FUNCTIONS = "Test Bond Exposure Functions";
  private static final ExternalId GB_ID = ExternalSchemes.financialRegionId("GB");
  private static final ExternalId US_ID = ExternalSchemes.financialRegionId("US");
  private static final ExternalId IT_ID = ExternalSchemes.financialRegionId("IT");

  /*USD and GBP curve share all the same data, except the name*/
  private static final String BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER = "Test Bond Pre Calibrated Mapper";
  private static final String BOND_CURVE_NODE_ID_MAPPER = "Test Bond Mapper";
  private static final String BOND_USD_PRE_CALIBRATED_CURVE_NAME = "USD Bond Pre-Calibrated Curve";
  public static final String BOND_GBP_PRE_CALIBRATED_CURVE_NAME = "GBP Bond Pre-Calibrated Curve";
  public static final String BOND_GBP_CURVE_NAME = "GBP Bond Curve";
  private static final String BOND_PRE_CALIBRATED_CURVE_CONFIG_NAME = "Test Bond Pre Calibrated Curve Config";
  public static final String BOND_CURVE_CONFIG_NAME = "Test Bond Curve Config";

  /*Bond*/
  public static final BondSecurity GOVERNMENT_BOND_SECURITY = createGovernmentBondSecurity();
  public static final BondSecurity CORPORATE_BOND_SECURITY = createCorporateBondSecurity();
  public static final BondTrade GOVERNMENT_BOND_TRADE = createGovernmentBondTrade();
  public static final BondTrade CORPORATE_BOND_TRADE = createCorporateBondTrade();

  /*Bond Future*/
  public static final BondFutureSecurity BOND_FUTURE_SECURITY = createBondFutureSecurity();
  public static final BondFutureTrade BOND_FUTURE_TRADE = createBondFutureTrade();

  /*Bond Future Option*/
  public static final BondFutureOptionSecurity BOND_FUTURE_OPTION_SECURITY = createBondFutureOptionSecurity();
  public static final BondFutureOptionTrade BOND_FUTURE_OPTION_TRADE = createBondFutureOptionTrade();

  /*Environment*/
  public static final ZonedDateTime VALUATION_TIME = DateUtils.getUTCDate(2014, 7, 22);
  public static final Environment ENV = new SimpleEnvironment(VALUATION_TIME, createMarketDataBundle());

  private static CurveNodeIdMapper getBondPreCalibratedCurveNodeIdMapper() {
    Map<Tenor, CurveInstrumentProvider> nodes = Maps.newHashMap();
    nodes.put(Tenor.ONE_YEAR, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B1")));
    nodes.put(Tenor.TWO_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B2")));
    nodes.put(Tenor.THREE_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B3")));
    nodes.put(Tenor.FOUR_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B4")));
    nodes.put(Tenor.FIVE_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B5")));
    nodes.put(Tenor.SIX_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B6")));
    nodes.put(Tenor.SEVEN_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B7")));
    nodes.put(Tenor.EIGHT_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B8")));
    nodes.put(Tenor.NINE_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B9")));
    nodes.put(Tenor.TEN_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "B10")));
    return CurveNodeIdMapper.builder().name(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER)
                                      .periodicallyCompoundedRateNodeIds(nodes)
                                      .build();
  }

  private static CurveNodeIdMapper getBondCurveNodeIdMapper() {
    Map<Tenor, CurveInstrumentProvider> billNodes = Maps.newHashMap();
    billNodes.put(Tenor.ONE_YEAR, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "Bill1")));

    Map<Tenor, CurveInstrumentProvider> bondNodes = Maps.newHashMap();
    bondNodes.put(Tenor.THREE_YEARS, new StaticCurveInstrumentProvider(ExternalId.of(TICKER, "Bond1")));
    return CurveNodeIdMapper.builder().name(BOND_CURVE_NODE_ID_MAPPER)
        .billNodeIds(billNodes)
        .bondNodeIds(bondNodes)
        .build();
  }

  private static InterpolatedCurveDefinition getBondUsdPreCalibratedCurveDefinition() {
    Set<CurveNode> nodes = new TreeSet<>();
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.ONE_YEAR, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.TWO_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.THREE_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.FOUR_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.FIVE_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.SIX_YEARS, 1));
    return new InterpolatedCurveDefinition(BOND_USD_PRE_CALIBRATED_CURVE_NAME, nodes, Interpolator1DFactory.LINEAR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR);
  }

  private static InterpolatedCurveDefinition getBondGbpPreCalibratedCurveDefinition() {
    Set<CurveNode> nodes = new TreeSet<>();
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.ONE_YEAR, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.TWO_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.THREE_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.FOUR_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.FIVE_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.SIX_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.SEVEN_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.EIGHT_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.NINE_YEARS, 1));
    nodes.add(new PeriodicallyCompoundedRateNode(BOND_PRE_CALIBRATED_CURVE_NODE_ID_MAPPER, Tenor.TEN_YEARS, 1));
    return new InterpolatedCurveDefinition(BOND_GBP_PRE_CALIBRATED_CURVE_NAME, nodes, Interpolator1DFactory.LINEAR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR);
  }

  // This curve def in currently only used to validate the DefaultDiscountingIssuerProviderBundleFn
  private static InterpolatedCurveDefinition getBondGbpCurveDefinition() {
    Set<CurveNode> nodes = new TreeSet<>();
    nodes.add(new BillNode(Tenor.ONE_YEAR, BOND_CURVE_NODE_ID_MAPPER, "Bill " + Tenor.ONE_YEAR));
    nodes.add(new BondNode(Tenor.THREE_YEARS, BOND_CURVE_NODE_ID_MAPPER, "Bond " + Tenor.THREE_YEARS));
    return new InterpolatedCurveDefinition(BOND_GBP_CURVE_NAME, nodes, Interpolator1DFactory.LINEAR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR,
                                           Interpolator1DFactory.FLAT_EXTRAPOLATOR);
  }

  public static FunctionModelConfig getPreCalibratedConfig() {
    ConfigLink<ExposureFunctions> exposureLink =
        ConfigLink.resolvable(BondMockSources.BOND_PRE_CALIBRATED_EXPOSURE_FUNCTIONS, ExposureFunctions.class);

    return
        config(
            arguments(
                function(
                    MarketExposureSelector.class,
                    argument("exposureFunctions", exposureLink)),
                function(
                    RootFinderConfiguration.class,
                    argument("rootFinderAbsoluteTolerance", 1e-9),
                    argument("rootFinderRelativeTolerance", 1e-9),
                    argument("rootFinderMaxIterations", 1000)),
                function(
                    DefaultDiscountingMulticurveBundleFn.class,
                    argument("impliedCurveNames", StringSet.of()))),
            implementations(
                CurveSpecificationMarketDataFn.class, DefaultCurveSpecificationMarketDataFn.class,
                FXMatrixFn.class, DefaultFXMatrixFn.class,
                IssuerProviderFn.class, ExposureFunctionsIssuerProviderFn.class,
                IssuerProviderBundleFn.class, InterpolatedIssuerBundleFn.class,
                CurveDefinitionFn.class, DefaultCurveDefinitionFn.class,
                CurveLabellingFn.class, CurveDefinitionCurveLabellingFn.class,
                DiscountingMulticurveBundleFn.class, DefaultDiscountingMulticurveBundleFn.class,
                CurveSpecificationFn.class, DefaultCurveSpecificationFn.class,
                CurveConstructionConfigurationSource.class, ConfigDBCurveConstructionConfigurationSource.class,
                FixingsFn.class, DefaultFixingsFn.class,
                MarketDataFn.class, DefaultMarketDataFn.class,
                FunctionCache.class, NoOpFunctionCache.class,
                BondFn.class, DiscountingBondFn.class,
                BondFutureOptionFn.class, DefaultBondFutureOptionFn.class,
                BondFutureOptionCalculatorFactory.class, BondFutureOptionBlackCalculatorFactory.class,
                BlackBondFuturesProviderFn.class, BlackExpStrikeBondFuturesProviderFn.class,
                BondFutureFn.class, DefaultBondFutureFn.class,
                HistoricalMarketDataFn.class, DefaultHistoricalMarketDataFn.class,
                BondFutureCalculatorFactory.class, BondFutureDiscountingCalculatorFactory.class));
  }

  public static FunctionModelConfig getConfig() {
    ConfigLink<ExposureFunctions> exposureLink =
        ConfigLink.resolvable(BondMockSources.BOND_EXPOSURE_FUNCTIONS, ExposureFunctions.class);
    return
        config(
            arguments(
                function(
                    MarketExposureSelector.class,
                    argument("exposureFunctions", exposureLink)),
                function(
                    RootFinderConfiguration.class,
                    argument("rootFinderAbsoluteTolerance", 1e-9),
                    argument("rootFinderRelativeTolerance", 1e-9),
                    argument("rootFinderMaxIterations", 1000)),
                function(
                    DefaultDiscountingIssuerProviderBundleFn.class,
                    argument("impliedCurveNames", StringSet.of())),
                function(
                    DefaultCurveNodeConverterFn.class,
                    argument("timeSeriesDuration", RetrievalPeriod.of(Period.ofYears(1))))),
            implementations(
                CurveSpecificationMarketDataFn.class, DefaultCurveSpecificationMarketDataFn.class,
                FXMatrixFn.class, DefaultFXMatrixFn.class,
                IssuerProviderFn.class, ExposureFunctionsIssuerProviderFn.class,
                IssuerProviderBundleFn.class, DefaultDiscountingIssuerProviderBundleFn.class,
                CurveDefinitionFn.class, DefaultCurveDefinitionFn.class,
                DiscountingMulticurveBundleFn.class, DefaultDiscountingMulticurveBundleFn.class,
                CurveSpecificationFn.class, DefaultCurveSpecificationFn.class,
                CurveConstructionConfigurationSource.class, ConfigDBCurveConstructionConfigurationSource.class,
                FixingsFn.class, DefaultFixingsFn.class,
                CurveLabellingFn.class, CurveDefinitionCurveLabellingFn.class,
                MarketDataFn.class, DefaultMarketDataFn.class,
                CurveNodeConverterFn.class, DefaultCurveNodeConverterFn.class,
                BondFn.class, DiscountingBondFn.class,
                BondFutureOptionFn.class, DefaultBondFutureOptionFn.class,
                BondFutureOptionCalculatorFactory.class, BondFutureOptionBlackCalculatorFactory.class,
                BlackBondFuturesProviderFn.class, TestBlackBondFuturesProviderFn.class,
                BondFutureFn.class, DefaultBondFutureFn.class,
                HistoricalMarketDataFn.class, DefaultHistoricalMarketDataFn.class,
                BondFutureCalculatorFactory.class, BondFutureDiscountingCalculatorFactory.class));
  }

  public static CurveConstructionConfiguration getBondCurveConfig() {
    Set<LegalEntityFilter<LegalEntity>> filters = Sets.newHashSet();
    filters.add(new LegalEntityShortName());

    Set<Object> govKeys = Sets.newHashSet();
    govKeys.add(GOVERNMENT_BOND_ISSUER_KEY);

    List<CurveTypeConfiguration> configs = Lists.newArrayList();
    configs.add(new IssuerCurveTypeConfiguration(govKeys, filters));
    configs.add(new DiscountingCurveTypeConfiguration(Currency.GBP.getCode()));

    Map<String, List<? extends CurveTypeConfiguration>> curveTypes = Maps.newHashMap();
    curveTypes.put(BOND_GBP_CURVE_NAME, configs);

    return new CurveConstructionConfiguration(BOND_CURVE_CONFIG_NAME,
                                              Lists.newArrayList(new CurveGroupConfiguration(0, curveTypes)),
                                              Collections.<String>emptyList());
  }

  private static CurveConstructionConfiguration getBondPreCalibratedCurveConfig() {
    Set<LegalEntityFilter<LegalEntity>> filters = Sets.newHashSet();
    filters.add(new LegalEntityShortName());

    Set<Object> govKeys = Sets.newHashSet();
    govKeys.add(GOVERNMENT_BOND_ISSUER_KEY);

    Set<Object> corpKeys = Sets.newHashSet();
    corpKeys.add(CORPORATE_BOND_ISSUER_KEY);

    List<CurveTypeConfiguration> configs = Lists.newArrayList();
    configs.add(new IssuerCurveTypeConfiguration(corpKeys, filters));
    configs.add(new IssuerCurveTypeConfiguration(govKeys, filters));
    
    Map<String, List<? extends CurveTypeConfiguration>> curveTypes = Maps.newHashMap();
    curveTypes.put(BOND_GBP_PRE_CALIBRATED_CURVE_NAME, configs);
    
    return new CurveConstructionConfiguration(BOND_PRE_CALIBRATED_CURVE_CONFIG_NAME,
                                              Lists.newArrayList(new CurveGroupConfiguration(0, curveTypes)),
                                              Collections.<String>emptyList());
  }
  
  private static ExposureFunctions getPreCalibratedExposureFunctions() {
    List<String> exposureFunctions = ImmutableList.of("Currency");
    Map<ExternalId, String> idsToNames = Maps.newHashMap();
    idsToNames.put(ExternalId.of("CurrencyISO", Currency.GBP.getCode()), BOND_PRE_CALIBRATED_CURVE_CONFIG_NAME);
    return new ExposureFunctions(BOND_PRE_CALIBRATED_EXPOSURE_FUNCTIONS, exposureFunctions, idsToNames);
  }

  private static ExposureFunctions getExposureFunctions() {
    List<String> exposureFunctions = ImmutableList.of("Currency");
    Map<ExternalId, String> idsToNames = Maps.newHashMap();
    idsToNames.put(ExternalId.of("CurrencyISO", Currency.GBP.getCode()), BOND_CURVE_CONFIG_NAME);
    return new ExposureFunctions(BOND_EXPOSURE_FUNCTIONS, exposureFunctions, idsToNames);
  }

  public static MarketDataBundle createMarketDataBundle() {
    return createMarketDataEnvironment().toBundle();
  }

  public static MarketDataEnvironment createMarketDataEnvironment() {
    LocalDate valuationDate = VALUATION_TIME.toLocalDate();
    LocalDateDoubleTimeSeries futurePrices = ImmutableLocalDateDoubleTimeSeries.of(valuationDate, 0.975);

    FieldName ytmMidField = FieldName.of(MarketDataRequirementNames.YIELD_YIELD_TO_MATURITY_MID);
    MarketDataEnvironmentBuilder builder = new MarketDataEnvironmentBuilder();

    ExternalIdBundle bondFutureId = ExternalSchemes.isinSecurityId("Test bond future").toBundle();
    ExternalIdBundle bondFutureOptionId = ExternalSchemes.isinSecurityId("Test bond future option").toBundle();

    return builder
        .add(RawId.of(createId("B1").toBundle()), 0.009154010130285646)
        .add(RawId.of(createId("B2").toBundle()), 0.013529850844352658)
        .add(RawId.of(createId("B3").toBundle()), 0.0172583393761524)
        .add(RawId.of(createId("B4").toBundle()), 0.020001507249248547)
        .add(RawId.of(createId("B5").toBundle()), 0.022004447649196877)
        .add(RawId.of(createId("B6").toBundle()), 0.023628241845802613)
        .add(RawId.of(createId("B7").toBundle()), 0.025005300419649393)
        .add(RawId.of(createId("B8").toBundle()), 0.02619214367588991)
        .add(RawId.of(createId("B9").toBundle()), 0.02719250291972944)
        .add(RawId.of(createId("B10").toBundle()), 0.02808602151907749)
        .add(SecurityId.of(CORPORATE_BOND_SECURITY), 1.08672)
        .add(SecurityId.of(GOVERNMENT_BOND_SECURITY), 1.36375)
        .add(SecurityId.of(CORPORATE_BOND_SECURITY, Double.class, ytmMidField), 0.043)
        .add(SecurityId.of(GOVERNMENT_BOND_SECURITY, Double.class, ytmMidField), 0.0225)
        .add(RawId.of(createId("Bond1").toBundle()), 0.01) //Yield
        .add(RawId.of(createId("Bill1").toBundle()), 0.01) //Yield
        .add(RawId.of(bondFutureId), futurePrices)
        .add(RawId.of(bondFutureOptionId), futurePrices)
        .valuationTime(VALUATION_TIME)
        .build();
  }

    private static ExternalId createId(String ticker) {
    return ExternalId.of(TICKER, ticker);
  }

  private static ImmutableMap<Class<?>, Object> generateComponentMap(Object... components) {
    ImmutableMap.Builder<Class<?>, Object> builder = ImmutableMap.builder();
    for (Object component : components) {
      builder.put(component.getClass().getInterfaces()[0], component);
    }
    return builder.build();
  }

  private static HolidaySource mockHolidaySource() {
    return new WeekendHolidaySource();
  }

  private static HistoricalTimeSeriesSource mockHistoricalTimeSeriesSource() {
    InMemoryHistoricalTimeSeriesMaster master = new InMemoryHistoricalTimeSeriesMaster();
    TestHistoricalTimeSeriesSelector selector = new TestHistoricalTimeSeriesSelector();
    DefaultHistoricalTimeSeriesResolver resolver = new DefaultHistoricalTimeSeriesResolver(selector, master);
    return new MasterHistoricalTimeSeriesSource(master, resolver);
  }

  private static RegionSource mockRegionSource() {
    RegionSource mock = mock(RegionSource.class);

    SimpleRegion usRegion = new SimpleRegion();
    usRegion.addExternalId(US_ID);
    SimpleRegion euRegion = new SimpleRegion();
    euRegion.addExternalId(IT_ID);
    SimpleRegion gbRegion = new SimpleRegion();
    gbRegion.addExternalId(GB_ID);

    when(mock.changeManager()).thenReturn(MOCK_CHANGE_MANAGER);
    when(mock.getHighestLevelRegion(eq(US_ID)))
        .thenReturn(usRegion);
    when(mock.getHighestLevelRegion(eq(IT_ID)))
        .thenReturn(euRegion);
    when(mock.getHighestLevelRegion(eq(GB_ID)))
        .thenReturn(gbRegion);
    return mock;
  }
  
  private static ConventionSource mockConventionSource() {
    return mock(ConventionSource.class);
  }
  
  private static ConventionBundleSource mockConventionBundleSource() {
    ConventionBundleSource mock = mock(ConventionBundleSource.class);
    
    String usBondConvention = "US_TREASURY_BOND_CONVENTION";
    ExternalId usConventionId = ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, usBondConvention);
    ConventionBundle usConvention =
        new ConventionBundleImpl(usConventionId.toBundle(), usBondConvention, DayCounts.THIRTY_360,
                                 new ModifiedFollowingBusinessDayConvention(), Period.ofYears(1), 1, false, US_ID);
    when(mock.getConventionBundle(eq(usConventionId))).thenReturn(usConvention);

    String gbBondConvention = "GB_TREASURY_BOND_CONVENTION";
    ExternalId gbConventionId = ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, gbBondConvention);
    ConventionBundle gbConvention =
        new ConventionBundleImpl(gbConventionId.toBundle(), gbBondConvention, DayCounts.THIRTY_360,
                                 new ModifiedFollowingBusinessDayConvention(), Period.ofYears(1), 0, false, GB_ID);
    when(mock.getConventionBundle(eq(gbConventionId))).thenReturn(gbConvention);

    String itBondConvention = "IT_CORPORATE_BOND_CONVENTION";
    ExternalId itConventionId = ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, itBondConvention);
    ConventionBundle itConvention =
        new ConventionBundleImpl(itConventionId.toBundle(), itBondConvention, DayCounts.ACT_ACT_ICMA,
                                 new ModifiedFollowingBusinessDayConvention(), Period.ofYears(1), 3, false, IT_ID);
    when(mock.getConventionBundle(eq(itConventionId))).thenReturn(itConvention);

    return mock;
  }
  
  private static LegalEntitySource mockLegalEntitySource() {
    LegalEntityMaster master = new InMemoryLegalEntityMaster();
    ManageableLegalEntity legalEntity = new ManageableLegalEntity(GOVERNMENT_BOND_ISSUER_KEY,
                                                                  createBillSecurity().getLegalEntityId().toBundle());

    master.add(new LegalEntityDocument(legalEntity));
    return new MasterLegalEntitySource(master);
  }
  
  private static ConfigSource mockConfigSource() {

    ConfigMaster master = new InMemoryConfigMaster();

    master.add(new ConfigDocument(ConfigItem.of(getBondPreCalibratedCurveNodeIdMapper())));
    master.add(new ConfigDocument(ConfigItem.of(getBondCurveNodeIdMapper())));
    master.add(new ConfigDocument(ConfigItem.of(getBondUsdPreCalibratedCurveDefinition())));
    master.add(new ConfigDocument(ConfigItem.of(getBondGbpPreCalibratedCurveDefinition())));
    master.add(new ConfigDocument(ConfigItem.of(getBondGbpCurveDefinition())));
    master.add(new ConfigDocument(ConfigItem.of(getBondPreCalibratedCurveConfig())));
    master.add(new ConfigDocument(ConfigItem.of(getBondCurveConfig())));
    master.add(new ConfigDocument(ConfigItem.of(getPreCalibratedExposureFunctions())));
    master.add(new ConfigDocument(ConfigItem.of(getExposureFunctions())));

    return new MasterConfigSource(master);
  }
  
  private static SecuritySource mockSecuritySource() {

    SecurityMaster master= new InMemorySecurityMaster();

    BillSecurity bill = createBillSecurity();
    bill.addExternalId(createId("Bill1"));

    BondSecurity bond = createGovernmentBondSecurity();
    bond.addExternalId(createId("Bond1"));

    master.add(new SecurityDocument(BondMockSources.GOVERNMENT_BOND_SECURITY));
    master.add(new SecurityDocument(BondMockSources.CORPORATE_BOND_SECURITY));
    master.add(new SecurityDocument(BondMockSources.BOND_FUTURE_SECURITY));
    master.add(new SecurityDocument(bill));
    master.add(new SecurityDocument(bond));

    return new MasterSecuritySource(master);
  }

  public static ImmutableMap<Class<?>, Object> generateBaseComponents() {
    return generateComponentMap(mockHolidaySource(),
                                mockRegionSource(),
                                mockConventionSource(),
                                mockConventionBundleSource(),
                                mockConfigSource(),
                                mockSecuritySource(),
                                mock(CurrencyMatrix.class),
                                mockLegalEntitySource(),
                                mockHistoricalTimeSeriesSource());
  }

  private static BondSecurity createGovernmentBondSecurity() {

    String issuerName = BondMockSources.GOVERNMENT_BOND_ISSUER_KEY;
    String issuerDomicile = "GB";
    String issuerType = "Sovereign";
    Currency currency = Currency.GBP;
    YieldConvention yieldConvention = SimpleYieldConvention.UK_BUMP_DMO_METHOD;
    DayCount dayCountConvention = DayCounts.ACT_ACT_ICMA;

    Period couponPeriod = Period.parse("P6M");
    String couponType = "Fixed";
    double couponRate = 8.0;
    Frequency couponFrequency = PeriodFrequency.of(couponPeriod);

    ZonedDateTime maturityDate = DateUtils.getUTCDate(2021, 6, 7);
    ZonedDateTime firstCouponDate = DateUtils.getUTCDate(1996, 6, 7);
    ZonedDateTime interestAccrualDate = firstCouponDate.minus(couponPeriod);
    ZonedDateTime settlementDate = DateUtils.getUTCDate(2014, 6, 13);
    Expiry lastTradeDate = new Expiry(maturityDate);

    double issuancePrice = 100.0;
    double totalAmountIssued = 23499000000.0;
    double minimumAmount = 0.01;
    double minimumIncrement = 0.01;
    double parAmount = 100;
    double redemptionValue = 100;

    GovernmentBondSecurity bond =
        new GovernmentBondSecurity(issuerName, issuerType, issuerDomicile, issuerType, currency, yieldConvention,
                                   lastTradeDate, couponType, couponRate, couponFrequency, dayCountConvention,
                                   interestAccrualDate, settlementDate, firstCouponDate, issuancePrice,
                                   totalAmountIssued, minimumAmount, minimumIncrement, parAmount, redemptionValue);
    // Need this for time series lookup
    ExternalId bondId = ExternalSchemes.isinSecurityId("Test Gov bond");
    bond.setExternalIdBundle(bondId.toBundle());
    return bond;
  }

  private static BillSecurity createBillSecurity() {
    return new BillSecurity(Currency.GBP, new Expiry(ZonedDateTime.now()), ZonedDateTime.now(), 0,
                            0, GB_ID, SimpleYieldConvention.DISCOUNT, DayCountFactory.of("30/360"),
                            ExternalId.of("LegalEntity", "Test"));
  }

  private static BondSecurity createCorporateBondSecurity() {

    String issuerName = BondMockSources.CORPORATE_BOND_ISSUER_KEY;
    String issuerDomicile = "IT";
    String issuerType = "Corporate";
    Currency currency = Currency.GBP;
    YieldConvention yieldConvention = SimpleYieldConvention.US_STREET;
    DayCount dayCountConvention = DayCounts.ACT_ACT_ICMA;

    String couponType = "Fixed";
    double couponRate = 6.375;
    Period couponPeriod = Period.ofYears(1);
    Frequency couponFrequency = PeriodFrequency.of(couponPeriod);

    ZonedDateTime maturityDate = DateUtils.getUTCDate(2019, 6, 24);
    ZonedDateTime firstCouponDate = DateUtils.getUTCDate(2005, 6, 24);
    ZonedDateTime interestAccrualDate = firstCouponDate.minus(couponPeriod);
    ZonedDateTime settlementDate = DateUtils.getUTCDate(2014, 6, 13);
    Expiry lastTradeDate = new Expiry(maturityDate);

    double issuancePrice = 98.85;
    double totalAmountIssued = 850000000;
    double minimumAmount = 50000;
    double minimumIncrement = 50000;
    double parAmount = 50000;
    double redemptionValue = 100;

    CorporateBondSecurity bond =
        new CorporateBondSecurity(issuerName, issuerType, issuerDomicile, issuerType, currency, yieldConvention,
                                  lastTradeDate, couponType, couponRate, couponFrequency, dayCountConvention,
                                  interestAccrualDate, settlementDate, firstCouponDate, issuancePrice,
                                  totalAmountIssued, minimumAmount, minimumIncrement, parAmount, redemptionValue);
    // Need this for time series lookup
    ExternalId bondId = ExternalSchemes.isinSecurityId("Test Corp bond");
    bond.setExternalIdBundle(bondId.toBundle());
    return bond;
  }

  private static BondTrade createGovernmentBondTrade() {
    Counterparty counterparty = new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "COUNTERPARTY"));
    BigDecimal tradeQuantity = BigDecimal.valueOf(10000);
    LocalDate tradeDate = LocalDate.of(2014, 7, 23);
    OffsetTime tradeTime = OffsetTime.of(LocalTime.of(0, 0), ZoneOffset.UTC);
    SimpleTrade trade = new SimpleTrade(GOVERNMENT_BOND_SECURITY, tradeQuantity, counterparty, tradeDate, tradeTime);
    trade.setPremium(0.0);
    trade.setPremiumDate(tradeDate);
    trade.setPremiumCurrency(Currency.GBP);
    return new BondTrade(trade);
  }

  private static BondTrade createCorporateBondTrade() {
    Counterparty counterparty = new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "COUNTERPARTY"));
    BigDecimal tradeQuantity = BigDecimal.valueOf(10000);
    LocalDate tradeDate = LocalDate.of(2014, 7, 2);
    OffsetTime tradeTime = OffsetTime.of(LocalTime.of(0, 0), ZoneOffset.UTC);
    SimpleTrade trade = new SimpleTrade(CORPORATE_BOND_SECURITY, tradeQuantity, counterparty, tradeDate, tradeTime);
    trade.setPremiumDate(tradeDate);
    trade.setPremium(0.0);
    trade.setPremiumCurrency(Currency.GBP);
    return new BondTrade(trade);
  }

  private static BondFutureSecurity createBondFutureSecurity() {

    Currency currency = Currency.GBP;

    ZonedDateTime deliveryDate = DateUtils.getUTCDate(2014, 8, 18);
    Expiry expiry = new Expiry(deliveryDate);
    String tradingExchange = "XLON";
    String settlementExchange = "";
    double unitAmount = 1;
    Collection<BondFutureDeliverable> basket = new ArrayList<>();
    BondFutureDeliverable bondFutureDeliverable =
        new BondFutureDeliverable(GOVERNMENT_BOND_SECURITY.getExternalIdBundle(), 0.9);
    basket.add(bondFutureDeliverable);

    ZonedDateTime firstDeliveryDate = deliveryDate;
    ZonedDateTime lastDeliveryDate = deliveryDate;
    String category = "test";

    BondFutureSecurity security =  new BondFutureSecurity(expiry, tradingExchange, settlementExchange, currency, unitAmount, basket,
                                  firstDeliveryDate, lastDeliveryDate, category);
    security.setExternalIdBundle(ExternalSchemes.isinSecurityId("Test bond future").toBundle());
    return security;
  }

  private static BondFutureTrade createBondFutureTrade() {

    Counterparty counterparty = new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "COUNTERPARTY"));
    BigDecimal tradeQuantity = BigDecimal.valueOf(1);
    LocalDate tradeDate = LocalDate.of(2014, 1, 23);
    OffsetTime tradeTime = OffsetTime.of(LocalTime.of(0, 0), ZoneOffset.UTC);
    SimpleTrade trade = new SimpleTrade(BOND_FUTURE_SECURITY, tradeQuantity, counterparty, tradeDate, tradeTime);
    trade.setPremium(10.0);
    trade.setPremiumCurrency(Currency.GBP);
    return new BondFutureTrade(trade);
  }

  private static BondFutureOptionSecurity createBondFutureOptionSecurity() {

    String tradingExchange = "XLON";
    String settlementExchange = "";
    Expiry expiry = BOND_FUTURE_SECURITY.getExpiry();
    ExerciseType exerciseType = new EuropeanExerciseType();
    ExternalId underlyingId = Iterables.getOnlyElement(BOND_FUTURE_SECURITY.getExternalIdBundle());
    double pointValue = Double.NaN;
    Currency currency = BOND_FUTURE_SECURITY.getCurrency();
    double strike = 0.2;
    OptionType optionType = OptionType.PUT;
    boolean margined = true;
    BondFutureOptionSecurity security = new BondFutureOptionSecurity(tradingExchange, settlementExchange, expiry,
                                                                   exerciseType, underlyingId, pointValue, margined,
                                                                   currency, strike, optionType);
    security.setExternalIdBundle(ExternalSchemes.isinSecurityId("Test bond future option").toBundle());
    return security;
  }

  private static BondFutureOptionTrade createBondFutureOptionTrade() {

    Counterparty counterparty = new SimpleCounterparty(ExternalId.of(Counterparty.DEFAULT_SCHEME, "COUNTERPARTY"));
    BigDecimal tradeQuantity = BigDecimal.valueOf(10);
    LocalDate tradeDate = LocalDate.of(2000, 1, 1);
    OffsetTime tradeTime = OffsetTime.of(LocalTime.of(0, 0), ZoneOffset.UTC);
    SimpleTrade trade = new SimpleTrade(BOND_FUTURE_OPTION_SECURITY, tradeQuantity, counterparty, tradeDate, tradeTime);
    trade.setPremium(10.0);
    trade.setPremiumCurrency(Currency.GBP);
    return new BondFutureOptionTrade(trade);
  }

  private static class TestHistoricalTimeSeriesSelector implements HistoricalTimeSeriesSelector {

    @Override
    public ManageableHistoricalTimeSeriesInfo select(Collection<ManageableHistoricalTimeSeriesInfo> candidates,
                                                     String selectionKey) {
      return Iterables.getFirst(candidates, null);
    }
  }
}
