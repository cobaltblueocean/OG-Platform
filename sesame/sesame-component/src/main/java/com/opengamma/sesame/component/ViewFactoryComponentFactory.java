/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.component;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.core.convention.ConventionSource;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.core.position.Position;
import com.opengamma.core.region.RegionSource;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.engine.marketdata.live.LiveMarketDataProviderFactory;
import com.opengamma.financial.analytics.conversion.FXForwardSecurityConverter;
import com.opengamma.financial.analytics.curve.ConfigDBCurveConstructionConfigurationSource;
import com.opengamma.financial.analytics.curve.exposure.ConfigDBInstrumentExposuresProvider;
import com.opengamma.service.ServiceContext;
import com.opengamma.service.ThreadLocalServiceContext;
import com.opengamma.service.VersionCorrectionProvider;
import com.opengamma.sesame.ConfigDbMarketExposureSelectorFn;
import com.opengamma.sesame.CurveDefinitionCurveLabellingFn;
import com.opengamma.sesame.DefaultCurrencyPairsFn;
import com.opengamma.sesame.DefaultCurveDefinitionFn;
import com.opengamma.sesame.DefaultCurveNodeConverterFn;
import com.opengamma.sesame.DefaultCurveSpecificationFn;
import com.opengamma.sesame.DefaultCurveSpecificationMarketDataFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleResolverFn;
import com.opengamma.sesame.DefaultFXMatrixFn;
import com.opengamma.sesame.DefaultFXReturnSeriesFn;
import com.opengamma.sesame.DefaultFixingsFn;
import com.opengamma.sesame.DiscountingMulticurveBundleResolverFn;
import com.opengamma.sesame.ExposureFunctionsDiscountingMulticurveCombinerFn;
import com.opengamma.sesame.FXMatrixFn;
import com.opengamma.sesame.bond.BondFn;
import com.opengamma.sesame.cache.CacheInvalidator;
import com.opengamma.sesame.cache.NoOpCacheInvalidator;
import com.opengamma.sesame.cache.source.CacheAwareConfigSource;
import com.opengamma.sesame.cache.source.CacheAwareConventionSource;
import com.opengamma.sesame.cache.source.CacheAwareHistoricalTimeSeriesSource;
import com.opengamma.sesame.cache.source.CacheAwareRegionSource;
import com.opengamma.sesame.cache.source.CacheAwareSecuritySource;
import com.opengamma.sesame.config.FunctionModelConfig;
import com.opengamma.sesame.credit.IsdaCompliantCreditCurveFn;
import com.opengamma.sesame.credit.IsdaCompliantYieldCurveFn;
import com.opengamma.sesame.credit.measures.CreditBucketedCs01Fn;
import com.opengamma.sesame.credit.measures.CreditCs01Fn;
import com.opengamma.sesame.credit.measures.CreditPvFn;
import com.opengamma.sesame.engine.ComponentMap;
import com.opengamma.sesame.engine.FixedInstantVersionCorrectionProvider;
import com.opengamma.sesame.engine.FunctionService;
import com.opengamma.sesame.engine.ViewFactory;
import com.opengamma.sesame.equity.DefaultEquityPresentValueFn;
import com.opengamma.sesame.equity.EquityPresentValueFn;
import com.opengamma.sesame.fra.FRAFn;
import com.opengamma.sesame.function.AvailableImplementations;
import com.opengamma.sesame.function.AvailableImplementationsImpl;
import com.opengamma.sesame.function.AvailableOutputs;
import com.opengamma.sesame.function.AvailableOutputsImpl;
import com.opengamma.sesame.fxforward.DiscountingFXForwardPVFn;
import com.opengamma.sesame.fxforward.DiscountingFXForwardSpotPnLSeriesFn;
import com.opengamma.sesame.fxforward.DiscountingFXForwardYCNSPnLSeriesFn;
import com.opengamma.sesame.fxforward.DiscountingFXForwardYieldCurveNodeSensitivitiesFn;
import com.opengamma.sesame.fxforward.FXForwardDiscountingCalculatorFn;
import com.opengamma.sesame.fxforward.FXForwardPVFn;
import com.opengamma.sesame.fxforward.FXForwardPnLSeriesFn;
import com.opengamma.sesame.fxforward.FXForwardYCNSPnLSeriesFn;
import com.opengamma.sesame.fxforward.FXForwardYieldCurveNodeSensitivitiesFn;
import com.opengamma.sesame.fxrates.FxRatesFn;
import com.opengamma.sesame.irs.DiscountingInterestRateSwapCalculatorFactory;
import com.opengamma.sesame.irs.DiscountingInterestRateSwapFn;
import com.opengamma.sesame.irs.InterestRateSwapFn;
import com.opengamma.sesame.marketdata.DefaultHistoricalMarketDataFn;
import com.opengamma.sesame.marketdata.DefaultMarketDataFn;
import com.opengamma.sesame.pnl.DefaultHistoricalPnLFXConverterFn;
import com.opengamma.sesame.trade.TradeWrapper;
import com.opengamma.util.auth.AuthUtils;
import org.apache.shiro.concurrent.SubjectAwareExecutorService;
import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.Instant;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Component factory for creating {@link ViewFactory} instances.
 */
@BeanDefinition
public class ViewFactoryComponentFactory extends AbstractComponentFactory {

  /**
   * The default maximum size of the view factory cache if none is specified in the config.
   */
  private static final long MAX_CACHE_ENTRIES = 10_000;

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;

  /**
   * For obtaining the live market data provider names.
   */
  @PropertyDefinition
  private LiveMarketDataProviderFactory _liveMarketDataProviderFactory;

  /**
   * Maximum number of entries to store in the cache.
   */
  @PropertyDefinition
  private long _maxCacheEntries = MAX_CACHE_ENTRIES;

  /**
   * The set of function services to be enabled for the server for
   * most runs of the engine. These can be overridden at run time
   * for individual views. The names need to match those of the
   * {@link FunctionService} enum - any that do not will be ignored.
   * If null, then {@link FunctionService#DEFAULT_SERVICES} will be used.
   */
  @PropertyDefinition
  private List<String> _defaultFunctionServices;

  /**
   * The registry to be used for recording metrics, may be null.
   */
  @PropertyDefinition
  private MetricRegistry _metricRegistry;

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    Map<Class<?>, Object> components = getComponents(repo, configuration);
    // TODO cache invalidation isn't fully implemented yet, this will need to be changed for a real implementation
    CacheInvalidator cacheInvalidator = new NoOpCacheInvalidator();
    ComponentMap componentMap = decorateSources(ComponentMap.of(components), cacheInvalidator);

    // Indicate remaining configuration has been used
    configuration.clear();

    // Initialize the service context with the same wrapped components that we use within the engine itself
    initServiceContext(repo, componentMap.getComponents());

    ExecutorService executor = createExecutorService(repo);
    AvailableOutputs availableOutputs = createAvailableOutputs(repo);
    AvailableImplementations availableImplementations = createAvailableImplementations(repo);
    CacheBuilder<Object, Object> cacheBuilder = createCacheBuilder(repo);

    FunctionServiceParser parser = new FunctionServiceParser(_defaultFunctionServices);
    EnumSet<FunctionService> functionServices = parser.determineFunctionServices();

    if (functionServices.contains(FunctionService.METRICS) && _metricRegistry == null) {
      throw new OpenGammaRuntimeException(
          "Metrics service has been requested but no registry has been provided. " +
          "Either remove METRICS from defaultFunctionServices or specify a valid " +
          "metrics registry");
    }
    ViewFactory viewFactory = new ViewFactory(executor,
                                              componentMap,
                                              availableOutputs,
                                              availableImplementations,
                                              FunctionModelConfig.EMPTY,
                                              functionServices,
                                              cacheBuilder,
                                              cacheInvalidator,
                                              Optional.fromNullable(_metricRegistry));

    repo.registerComponent(ViewFactory.class, getClassifier(), viewFactory);
    repo.registerComponent(AvailableOutputs.class, getClassifier(), availableOutputs);
    repo.registerComponent(AvailableImplementations.class, getClassifier(), availableImplementations);
    repo.registerComponent(ComponentMap.class, getClassifier(), componentMap);
    repo.registerComponent(ExecutorService.class, getClassifier(), executor);
  }

  private Map<Class<?>, Object> getComponents(ComponentRepository repo, LinkedHashMap<String, String> configuration) {
    Map<String, ComponentInfo> infos = repo.findInfos(configuration);
    configuration.keySet().removeAll(infos.keySet());
    Map<Class<?>, Object> components = new HashMap<>();
    for (ComponentInfo info : infos.values()) {
      components.put(info.getType(), repo.getInstance(info));
    }
    return components;
  }

  /**
   * Initializes the static {@code ServiceContext}.
   * 
   * @param repo  the component repository, typically not used, not null
   * @param components  the map of components, not null
   */
  protected void initServiceContext(ComponentRepository repo, Map<Class<?>, Object> components) {
    ServiceContext serviceContext = ServiceContext.of(components)
        .with(VersionCorrectionProvider.class, new FixedInstantVersionCorrectionProvider(Instant.now()));
    ThreadLocalServiceContext.init(serviceContext);
  }

  /**
   * Create the executor service.
   * <p>
   * This implementation uses a fixed size thread pool based on the number of available processors.
   * If authentication is in use, Apache Shiro is attached to the executor service.
   * 
   * @param repo  the component repository, typically not used, not null
   * @return the executor service, not null
   */
  protected ExecutorService createExecutorService(ComponentRepository repo) {
    // TODO allow the thread pool to grow to allow for threads that block waiting for a cache value to be calculated?
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
    return AuthUtils.isPermissive() ? executor : new SubjectAwareExecutorService(executor);
  }

  /**
   * Create the available outputs.
   * 
   * @param repo  the component repository, typically not used, not null
   * @return the available outputs, not null
   */
  protected AvailableOutputs createAvailableOutputs(ComponentRepository repo) {
    AvailableOutputs available = new AvailableOutputsImpl(ImmutableSet.of(Position.class,
                                                                          Security.class,
                                                                          TradeWrapper.class));
    available.register(EquityPresentValueFn.class,
        FRAFn.class,
        InterestRateSwapFn.class,
        DiscountingMulticurveBundleResolverFn.class,
        FXForwardPnLSeriesFn.class,
        FXForwardPVFn.class,
        FXForwardYCNSPnLSeriesFn.class,
        FXForwardYieldCurveNodeSensitivitiesFn.class,
        FXMatrixFn.class,
        BondFn.class,
        IsdaCompliantYieldCurveFn.class,
        IsdaCompliantCreditCurveFn.class,
        CreditPvFn.class,
        CreditCs01Fn.class,
        FxRatesFn.class,
        CreditBucketedCs01Fn.class);
    return available;
  }

  /**
   * Create the available implementations.
   * 
   * @param repo  the component repository, typically not used, not null
   * @return the available implementations, not null
   */
  protected AvailableImplementations createAvailableImplementations(ComponentRepository repo) {
    AvailableImplementations available = new AvailableImplementationsImpl();
    available.register(
        DiscountingFXForwardYieldCurveNodeSensitivitiesFn.class,
        DiscountingFXForwardSpotPnLSeriesFn.class,
        DiscountingFXForwardYCNSPnLSeriesFn.class,
        DiscountingInterestRateSwapFn.class,
        DiscountingInterestRateSwapCalculatorFactory.class,
        DiscountingFXForwardPVFn.class,
        DefaultFXReturnSeriesFn.class,
        DefaultCurrencyPairsFn.class,
        DefaultEquityPresentValueFn.class,
        FXForwardSecurityConverter.class,
        ConfigDBInstrumentExposuresProvider.class,
        DefaultCurveSpecificationMarketDataFn.class,
        DefaultFXMatrixFn.class,
        DefaultCurveDefinitionFn.class,
        CurveDefinitionCurveLabellingFn.class,
        DefaultDiscountingMulticurveBundleFn.class,
        DefaultDiscountingMulticurveBundleResolverFn.class,
        DefaultCurveSpecificationFn.class,
        ConfigDBCurveConstructionConfigurationSource.class,
        DefaultFixingsFn.class,
        FXForwardDiscountingCalculatorFn.class,
        ConfigDbMarketExposureSelectorFn.class,
        ExposureFunctionsDiscountingMulticurveCombinerFn.class,
        DefaultMarketDataFn.class,
        DefaultHistoricalMarketDataFn.class,
        DefaultCurveNodeConverterFn.class,
        DefaultHistoricalPnLFXConverterFn.class);
    return available;
  }

  /**
   * Creates a cache builder used by the view factory when it needs to create a new cache.
   * <p>
   * New caches are created are created whenever data in the current cache needs to be discarded.
   * Caches are shared between multiple views so it isn't safe to clear an existing cache as
   * it may be in use. So a new, empty cache is created and supplied to each view at the
   * start of its next calculation cycle.
   * 
   * @param repo  the component repository, typically not used, not null
   * @return the cache builder, not null
   */
  protected CacheBuilder<Object, Object> createCacheBuilder(ComponentRepository repo) {
    int nProcessors = Runtime.getRuntime().availableProcessors();
    // concurrency level controls how many segments are created in the cache. a segment is locked while a value
    // is being calculated so we want enough segments to make it highly unlikely that two threads will try
    // to write a value to the same segment at the same time.
    // N.B. read operations can happen concurrently with writes, so the concurrency level only affects cache writes
    int concurrencyLevel = nProcessors * 8;
    return CacheBuilder.newBuilder()
        .maximumSize(getMaxCacheEntries())
        .softValues()
        .concurrencyLevel(concurrencyLevel);
  }

  /**
   * Decorates the sources with cache aware versions that register when data is
   * queried so cache entries can be invalidated when it changes. The returned
   * component map contains the cache aware sources in place of the originals.
   * <p>
   * This functionality isn't complete yet. The cache aware sources record when data is used
   * by a function but nothing listens to change notifications from the underlying sources.
   * Ultimately the {@code CacheInvalidator} should have a method to add and remove listeners
   * and logic to process change notifications and maintain a set of invalid cache keys.
   * TODO should this be somewhere else? a CacheUtils class? ComponentMap? CacheInvalidator?
   *
   * @param components  platform components used by functions
   * @return a component map containing the decorated sources instead of the originals
   */
  private static ComponentMap decorateSources(ComponentMap components, CacheInvalidator cacheInvalidator) {
    // Copy the original set and overwrite the ones we're interested in
    Map<Class<?>, Object> sources = Maps.newHashMap(components.getComponents());

    // need to record which ChangeManagers we're listening to so we can remove the listeners and avoid leaks
    Collection<ChangeManager> changeManagers = Lists.newArrayList();

    ConfigSource configSource = components.findComponent(ConfigSource.class);
    if (configSource != null) {
      changeManagers.add(configSource.changeManager());
      sources.put(ConfigSource.class, new CacheAwareConfigSource(configSource, cacheInvalidator));
    }

    RegionSource regionSource = components.findComponent(RegionSource.class);
    if (regionSource != null) {
      changeManagers.add(regionSource.changeManager());
      sources.put(RegionSource.class, new CacheAwareRegionSource(regionSource, cacheInvalidator));
    }

    SecuritySource securitySource = components.findComponent(SecuritySource.class);
    if (securitySource != null) {
      changeManagers.add(securitySource.changeManager());
      sources.put(SecuritySource.class, new CacheAwareSecuritySource(securitySource, cacheInvalidator));
    }

    ConventionSource conventionSource = components.findComponent(ConventionSource.class);
    if (conventionSource != null) {
      changeManagers.add(conventionSource.changeManager());
      sources.put(ConventionSource.class, new CacheAwareConventionSource(conventionSource, cacheInvalidator));
    }

    HistoricalTimeSeriesSource timeSeriesSource = components.findComponent(HistoricalTimeSeriesSource.class);
    if (timeSeriesSource != null) {
      changeManagers.add(timeSeriesSource.changeManager());
      sources.put(HistoricalTimeSeriesSource.class,
                  new CacheAwareHistoricalTimeSeriesSource(timeSeriesSource, cacheInvalidator));
    }
    // TODO HolidaySource (which has a horrible design WRT decorating)

    // TODO something needs to add listeners to the change managers. probably CacheInvalidator. addListeners() method?

    return ComponentMap.of(sources);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ViewFactoryComponentFactory}.
   * @return the meta-bean, not null
   */
  public static ViewFactoryComponentFactory.Meta meta() {
    return ViewFactoryComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ViewFactoryComponentFactory.Meta.INSTANCE);
  }

  @Override
  public ViewFactoryComponentFactory.Meta metaBean() {
    return ViewFactoryComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets for obtaining the live market data provider names.
   * @return the value of the property
   */
  public LiveMarketDataProviderFactory getLiveMarketDataProviderFactory() {
    return _liveMarketDataProviderFactory;
  }

  /**
   * Sets for obtaining the live market data provider names.
   * @param liveMarketDataProviderFactory  the new value of the property
   */
  public void setLiveMarketDataProviderFactory(LiveMarketDataProviderFactory liveMarketDataProviderFactory) {
    this._liveMarketDataProviderFactory = liveMarketDataProviderFactory;
  }

  /**
   * Gets the the {@code liveMarketDataProviderFactory} property.
   * @return the property, not null
   */
  public final Property<LiveMarketDataProviderFactory> liveMarketDataProviderFactory() {
    return metaBean().liveMarketDataProviderFactory().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets maximum number of entries to store in the cache.
   * @return the value of the property
   */
  public long getMaxCacheEntries() {
    return _maxCacheEntries;
  }

  /**
   * Sets maximum number of entries to store in the cache.
   * @param maxCacheEntries  the new value of the property
   */
  public void setMaxCacheEntries(long maxCacheEntries) {
    this._maxCacheEntries = maxCacheEntries;
  }

  /**
   * Gets the the {@code maxCacheEntries} property.
   * @return the property, not null
   */
  public final Property<Long> maxCacheEntries() {
    return metaBean().maxCacheEntries().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the set of function services to be enabled for the server for
   * most runs of the engine. These can be overridden at run time
   * for individual views. The names need to match those of the
   * {@link FunctionService} enum - any that do not will be ignored.
   * If null, then {@link FunctionService#DEFAULT_SERVICES} will be used.
   * @return the value of the property
   */
  public List<String> getDefaultFunctionServices() {
    return _defaultFunctionServices;
  }

  /**
   * Sets the set of function services to be enabled for the server for
   * most runs of the engine. These can be overridden at run time
   * for individual views. The names need to match those of the
   * {@link FunctionService} enum - any that do not will be ignored.
   * If null, then {@link FunctionService#DEFAULT_SERVICES} will be used.
   * @param defaultFunctionServices  the new value of the property
   */
  public void setDefaultFunctionServices(List<String> defaultFunctionServices) {
    this._defaultFunctionServices = defaultFunctionServices;
  }

  /**
   * Gets the the {@code defaultFunctionServices} property.
   * most runs of the engine. These can be overridden at run time
   * for individual views. The names need to match those of the
   * {@link FunctionService} enum - any that do not will be ignored.
   * If null, then {@link FunctionService#DEFAULT_SERVICES} will be used.
   * @return the property, not null
   */
  public final Property<List<String>> defaultFunctionServices() {
    return metaBean().defaultFunctionServices().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the registry to be used for recording metrics, may be null.
   * @return the value of the property
   */
  public MetricRegistry getMetricRegistry() {
    return _metricRegistry;
  }

  /**
   * Sets the registry to be used for recording metrics, may be null.
   * @param metricRegistry  the new value of the property
   */
  public void setMetricRegistry(MetricRegistry metricRegistry) {
    this._metricRegistry = metricRegistry;
  }

  /**
   * Gets the the {@code metricRegistry} property.
   * @return the property, not null
   */
  public final Property<MetricRegistry> metricRegistry() {
    return metaBean().metricRegistry().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public ViewFactoryComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ViewFactoryComponentFactory other = (ViewFactoryComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getLiveMarketDataProviderFactory(), other.getLiveMarketDataProviderFactory()) &&
          (getMaxCacheEntries() == other.getMaxCacheEntries()) &&
          JodaBeanUtils.equal(getDefaultFunctionServices(), other.getDefaultFunctionServices()) &&
          JodaBeanUtils.equal(getMetricRegistry(), other.getMetricRegistry()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getLiveMarketDataProviderFactory());
    hash = hash * 31 + JodaBeanUtils.hashCode(getMaxCacheEntries());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDefaultFunctionServices());
    hash = hash * 31 + JodaBeanUtils.hashCode(getMetricRegistry());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("ViewFactoryComponentFactory{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("classifier").append('=').append(JodaBeanUtils.toString(getClassifier())).append(',').append(' ');
    buf.append("liveMarketDataProviderFactory").append('=').append(JodaBeanUtils.toString(getLiveMarketDataProviderFactory())).append(',').append(' ');
    buf.append("maxCacheEntries").append('=').append(JodaBeanUtils.toString(getMaxCacheEntries())).append(',').append(' ');
    buf.append("defaultFunctionServices").append('=').append(JodaBeanUtils.toString(getDefaultFunctionServices())).append(',').append(' ');
    buf.append("metricRegistry").append('=').append(JodaBeanUtils.toString(getMetricRegistry())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ViewFactoryComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", ViewFactoryComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code liveMarketDataProviderFactory} property.
     */
    private final MetaProperty<LiveMarketDataProviderFactory> _liveMarketDataProviderFactory = DirectMetaProperty.ofReadWrite(
        this, "liveMarketDataProviderFactory", ViewFactoryComponentFactory.class, LiveMarketDataProviderFactory.class);
    /**
     * The meta-property for the {@code maxCacheEntries} property.
     */
    private final MetaProperty<Long> _maxCacheEntries = DirectMetaProperty.ofReadWrite(
        this, "maxCacheEntries", ViewFactoryComponentFactory.class, Long.TYPE);
    /**
     * The meta-property for the {@code defaultFunctionServices} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<String>> _defaultFunctionServices = DirectMetaProperty.ofReadWrite(
        this, "defaultFunctionServices", ViewFactoryComponentFactory.class, (Class) List.class);
    /**
     * The meta-property for the {@code metricRegistry} property.
     */
    private final MetaProperty<MetricRegistry> _metricRegistry = DirectMetaProperty.ofReadWrite(
        this, "metricRegistry", ViewFactoryComponentFactory.class, MetricRegistry.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "liveMarketDataProviderFactory",
        "maxCacheEntries",
        "defaultFunctionServices",
        "metricRegistry");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -301472921:  // liveMarketDataProviderFactory
          return _liveMarketDataProviderFactory;
        case -949200334:  // maxCacheEntries
          return _maxCacheEntries;
        case -544798537:  // defaultFunctionServices
          return _defaultFunctionServices;
        case 1925437965:  // metricRegistry
          return _metricRegistry;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ViewFactoryComponentFactory> builder() {
      return new DirectBeanBuilder<ViewFactoryComponentFactory>(new ViewFactoryComponentFactory());
    }

    @Override
    public Class<? extends ViewFactoryComponentFactory> beanType() {
      return ViewFactoryComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code liveMarketDataProviderFactory} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LiveMarketDataProviderFactory> liveMarketDataProviderFactory() {
      return _liveMarketDataProviderFactory;
    }

    /**
     * The meta-property for the {@code maxCacheEntries} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Long> maxCacheEntries() {
      return _maxCacheEntries;
    }

    /**
     * The meta-property for the {@code defaultFunctionServices} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<String>> defaultFunctionServices() {
      return _defaultFunctionServices;
    }

    /**
     * The meta-property for the {@code metricRegistry} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<MetricRegistry> metricRegistry() {
      return _metricRegistry;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((ViewFactoryComponentFactory) bean).getClassifier();
        case -301472921:  // liveMarketDataProviderFactory
          return ((ViewFactoryComponentFactory) bean).getLiveMarketDataProviderFactory();
        case -949200334:  // maxCacheEntries
          return ((ViewFactoryComponentFactory) bean).getMaxCacheEntries();
        case -544798537:  // defaultFunctionServices
          return ((ViewFactoryComponentFactory) bean).getDefaultFunctionServices();
        case 1925437965:  // metricRegistry
          return ((ViewFactoryComponentFactory) bean).getMetricRegistry();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((ViewFactoryComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -301472921:  // liveMarketDataProviderFactory
          ((ViewFactoryComponentFactory) bean).setLiveMarketDataProviderFactory((LiveMarketDataProviderFactory) newValue);
          return;
        case -949200334:  // maxCacheEntries
          ((ViewFactoryComponentFactory) bean).setMaxCacheEntries((Long) newValue);
          return;
        case -544798537:  // defaultFunctionServices
          ((ViewFactoryComponentFactory) bean).setDefaultFunctionServices((List<String>) newValue);
          return;
        case 1925437965:  // metricRegistry
          ((ViewFactoryComponentFactory) bean).setMetricRegistry((MetricRegistry) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((ViewFactoryComponentFactory) bean)._classifier, "classifier");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
