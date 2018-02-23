/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.component;

import static com.opengamma.sesame.config.ConfigBuilder.argument;
import static com.opengamma.sesame.config.ConfigBuilder.arguments;
import static com.opengamma.sesame.config.ConfigBuilder.config;
import static com.opengamma.sesame.config.ConfigBuilder.configureView;
import static com.opengamma.sesame.config.ConfigBuilder.function;
import static com.opengamma.sesame.config.ConfigBuilder.nonPortfolioOutput;
import static com.opengamma.sesame.config.ConfigBuilder.output;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.math.RandomUtils;
import org.fudgemsg.MutableFudgeMsg;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableList;
import com.opengamma.component.ComponentFactory;
import com.opengamma.component.ComponentLogger;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.EmbeddedJettyComponentFactory;
import com.opengamma.core.link.ConfigLink;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.engine.marketdata.spec.FixedHistoricalMarketDataSpecification;
import com.opengamma.engine.marketdata.spec.LiveMarketDataSpecification;
import com.opengamma.engine.marketdata.spec.MarketDataSpecification;
import com.opengamma.financial.analytics.curve.CurveConstructionConfiguration;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.livedata.LiveDataClient;
import com.opengamma.livedata.LiveDataListener;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.LiveDataValueUpdate;
import com.opengamma.livedata.LiveDataValueUpdateBean;
import com.opengamma.livedata.UserPrincipal;
import com.opengamma.livedata.msg.LiveDataSubscriptionResponse;
import com.opengamma.livedata.msg.LiveDataSubscriptionResult;
import com.opengamma.sesame.DefaultCurveNodeConverterFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleFn;
import com.opengamma.sesame.DefaultDiscountingMulticurveBundleResolverFn;
import com.opengamma.sesame.MarketDataResourcesLoader;
import com.opengamma.sesame.MulticurveBundle;
import com.opengamma.sesame.OutputNames;
import com.opengamma.sesame.RootFinderConfiguration;
import com.opengamma.sesame.config.ViewConfig;
import com.opengamma.sesame.engine.ResultItem;
import com.opengamma.sesame.engine.Results;
import com.opengamma.sesame.engine.ViewInputs;
import com.opengamma.sesame.interestrate.InterestRateMockSources;
import com.opengamma.sesame.server.FunctionServer;
import com.opengamma.sesame.server.FunctionServerRequest;
import com.opengamma.sesame.server.GlobalCycleOptions;
import com.opengamma.sesame.server.IndividualCycleOptions;
import com.opengamma.sesame.server.RemoteFunctionServer;
import com.opengamma.util.fudgemsg.OpenGammaFudgeContext;
import com.opengamma.util.jms.JmsConnector;
import com.opengamma.util.jms.JmsConnectorFactoryBean;
import com.opengamma.util.result.Result;

/**
 * Tests that remoting to the new engine works. Starts up an engine on a
 * separate thread in the setup method and then makes requests to it via
 * REST. This test should run fast as all component parts are local and is
 * therefore classified as a UNIT test. However, in many respects it is
 * closer to an INTEGRATION test and therefore may need to be reclassified.
 */
// TODO this will need to be fixed and re-enabled once the new engine API is don@Test(groups = TestGroup.UNIT)
public class RemotingTest {

  public static final String CLASSIFIER = "test";

  private ComponentRepository _componentRepository;

  private String _serverUrl;

  @Test(enabled = false)
  public void testSingleExecution() {

    String curveBundleOutputName = "Curve Bundle";
    ViewConfig viewConfig = createCurveBundleConfig(curveBundleOutputName);

    // Send the config to the server, along with version
    // correction, MD requirements, valuation date and
    // cycle specifics (once/multiple/infinite)
    // Proxy options?

    FunctionServer functionServer = new RemoteFunctionServer(URI.create(_serverUrl));

    MarketDataSpecification marketDataSpecification =
        new FixedHistoricalMarketDataSpecification(LocalDate.now().minusDays(2));

    IndividualCycleOptions cycleOptions = IndividualCycleOptions.builder()
        .valuationTime(ZonedDateTime.now())
        .marketDataSpecs(ImmutableList.of(marketDataSpecification))
        .build();

    FunctionServerRequest<IndividualCycleOptions> request =
        FunctionServerRequest.<IndividualCycleOptions>builder()
            .viewConfig(viewConfig)
            //.withVersionCorrection(...)
            //.withSecurities(...)
            .cycleOptions(cycleOptions)
            .build();

    Results results = functionServer.executeSingleCycle(request);
    System.out.println(results);
    assertThat(results, is(not(nullValue())));

    checkCurveBundleResult(curveBundleOutputName, results);
  }

  @Test(enabled = false)
  public void testExecutionWithCapture() {

    String curveBundleOutputName = "Curve Bundle";
    ViewConfig viewConfig = createCurveBundleConfig(curveBundleOutputName);

    // Send the config to the server, along with version
    // correction, MD requirements, valuation date and
    // cycle specifics (once/multiple/infinite)
    // Proxy options?

    FunctionServer functionServer = new RemoteFunctionServer(URI.create(_serverUrl));

    ZonedDateTime now = ZonedDateTime.now();
    MarketDataSpecification marketDataSpecification =
        new FixedHistoricalMarketDataSpecification(LocalDate.now().minusDays(2));

    IndividualCycleOptions cycleOptions = IndividualCycleOptions.builder()
        .valuationTime(now)
        .marketDataSpecs(ImmutableList.of(marketDataSpecification))
        .captureInputs(true)
        .build();

    FunctionServerRequest<IndividualCycleOptions> request =
        FunctionServerRequest.<IndividualCycleOptions>builder()
            .viewConfig(viewConfig)
                //.withVersionCorrection(...)
                //.withSecurities(...)
            .cycleOptions(cycleOptions)
            .build();

    Results results = functionServer.executeSingleCycle(request);
    System.out.println(results);
    assertThat(results, is(not(nullValue())));

    checkCurveBundleResult(curveBundleOutputName, results);

    ViewInputs viewInputs = results.getViewInputs();
    assertThat(viewInputs, is(not(nullValue())));

    assertThat(viewInputs.getValuationTime(), is(now));
    assertThat(viewInputs.getConfigData().isEmpty(), is(false));
    // TODO does MarketDataEnvironment need isEmpty()? seems a bit much just for testing
    //assertThat(viewInputs.getMarketDataEnvironment().isEmpty(), is(false));
    assertThat(viewInputs.getTradeInputs().isEmpty(), is(true));
    // Following line would work were it not for Fudge compressing
    // values and converting (Int) 1000 to (Short) 1000
    // BeanAssert.assertBeanEquals(viewInputs.getViewConfig(), viewConfig);
  }

  private void checkCurveBundleResult(String curveBundleOutputName, Results results) {

    ResultItem resultItem = results.get(curveBundleOutputName);
    assertThat(resultItem, is(not(nullValue())));

    Result<Object> result = resultItem.getResult();
    if (!result.isSuccess()) {
      fail("Expected success but got: " + result);
    }

    @SuppressWarnings("unchecked") MulticurveBundle pair = (MulticurveBundle) result.getValue();
    assertThat(pair.getMulticurveProvider(), is(not(nullValue())));
    assertThat(pair.getCurveBuildingBlockBundle(), is(not(nullValue())));
  }

  @Test(enabled = false)
  public void testMultipleExecution() throws InterruptedException {

    String curveBundleOutputName = "Curve Bundle";
    ViewConfig viewConfig = createCurveBundleConfig(curveBundleOutputName);

    // Send the config to the server, along with version
    // correction, MD requirements, valuation date and
    // cycle specifics (once/multiple/infinite)
    // Proxy options?

    FunctionServer functionServer = new RemoteFunctionServer(URI.create(_serverUrl));

    GlobalCycleOptions cycleOptions = GlobalCycleOptions.builder()
        .valuationTime(ZonedDateTime.now())
        .marketDataSpec(new FixedHistoricalMarketDataSpecification(LocalDate.now().minusDays(2)))
        .numCycles(2)
        .build();

    FunctionServerRequest<GlobalCycleOptions> request =
        FunctionServerRequest.<GlobalCycleOptions>builder()
            .viewConfig(viewConfig)
                //.withVersionCorrection(...)
                //.withSecurities(...)
            .cycleOptions(cycleOptions)
            .build();

    List<Results> results = functionServer.executeMultipleCycles(request);
    System.out.println(results);
    assertThat(results, is(not(nullValue())));
    assertThat(results.size(), is(2));

    checkCurveBundleResult(curveBundleOutputName, results.get(0));
    checkCurveBundleResult(curveBundleOutputName, results.get(1));
  }

  @Test(enabled = false)
  public void testLiveExecution() {

    String curveBundleOutputName = "Curve Bundle";
    ViewConfig viewConfig = createCurveBundleConfig(curveBundleOutputName);

    // Send the config to the server, along with version
    // correction, MD requirements, valuation date and
    // cycle specifics (once/multiple/infinite)
    // Proxy options?

    FunctionServer functionServer = new RemoteFunctionServer(URI.create(_serverUrl));

    MarketDataSpecification liveSpec = LiveMarketDataSpecification.LIVE_SPEC;
    IndividualCycleOptions cycleOptions = IndividualCycleOptions.builder()
        .valuationTime(ZonedDateTime.now())
        .marketDataSpecs(ImmutableList.of(liveSpec))
        .build();

    FunctionServerRequest<IndividualCycleOptions> request =
        FunctionServerRequest.<IndividualCycleOptions>builder()
            .viewConfig(viewConfig)
            //.withVersionCorrection(...)
            //.withSecurities(...)
            .cycleOptions(cycleOptions)
            .build();

    Results results = functionServer.executeSingleCycle(request);
    System.out.println(results);
    assertThat(results, is(not(nullValue())));

    checkCurveBundleResult(curveBundleOutputName, results);
  }

  private ViewConfig createCurveBundleConfig(String curveBundleOutputName) {

    CurveConstructionConfiguration curveConstructionConfiguration =
        ConfigLink.resolvable("USD_ON-OIS_LIBOR3M-FRAIRS_1U", CurveConstructionConfiguration.class).resolve();

    return
        configureView(
            "Curve Bundle only",
            nonPortfolioOutput(
                curveBundleOutputName,
                output(
                    OutputNames.DISCOUNTING_MULTICURVE_BUNDLE,
                    config(
                        arguments(
                            function(
                                RootFinderConfiguration.class,
                                argument("rootFinderAbsoluteTolerance", 1e-9),
                                argument("rootFinderRelativeTolerance", 1e-9),
                                argument("rootFinderMaxIterations", 1000)),
                            function(
                                DefaultCurveNodeConverterFn.class,
                                argument("timeSeriesDuration", RetrievalPeriod.of(Period.ofYears(1)))),
                            function(
                                DefaultDiscountingMulticurveBundleResolverFn.class,
                                argument("curveConfig", curveConstructionConfiguration)),
                            function(
                                DefaultDiscountingMulticurveBundleFn.class,
                                argument("impliedCurveNames", StringSet.of())))))));
  }

  // test execution with streaming results

  @BeforeClass
  public void setUp() throws Exception {

    _componentRepository = new ComponentRepository(new ComponentLogger.Console(1));

    Map<Class<?>, Object> componentMap = InterestRateMockSources.generateBaseComponents();
    LinkedHashMap<String, String> properties = addComponentsToRepo(componentMap);

    //  initialise engine
    ViewFactoryComponentFactory engineComponentFactory = new ViewFactoryComponentFactory();
    engineComponentFactory.setClassifier(CLASSIFIER);
    register(engineComponentFactory, _componentRepository, properties);

    registerFunctionServerComponentFactory();

    // todo - keeping the below as we should port tests to be like this one
/*


    // initialise pricer
    NewEngineFXForwardPricingManagerComponentFactory pricingManagerComponentFactory =
        new NewEngineFXForwardPricingManagerComponentFactory();
    pricingManagerComponentFactory.setEngine(_componentRepository.getInstance(Engine.class, "main"));
    pricingManagerComponentFactory.setAvailableImplementations(_componentRepository.getInstance(AvailableImplementations.class,
                                                                                                "main"));
    pricingManagerComponentFactory.setAvailableOutputs(_componentRepository.getInstance(AvailableOutputs.class, "main"));
    register(pricingManagerComponentFactory, _componentRepository);*/

    // initialize server

    // Pick a random port in the ephemeral port range (49152-65535)
    // TODO - We need to detect if the port is in use and pick another one
    int serverPort = 49152 + RandomUtils.nextInt(65535 - 49152);
    _serverUrl = "http://localhost:" + serverPort + "/jax";

    // initialise Jetty server
    EmbeddedJettyComponentFactory jettyComponentFactory = new EmbeddedJettyComponentFactory();
    jettyComponentFactory.setPort(serverPort);

    // TODO - can we supply the config required directly rather than a file?
    Resource resource = new ClassPathResource("web-engine");
    jettyComponentFactory.setResourceBase(resource);
    register(jettyComponentFactory, _componentRepository);
    _componentRepository.start();
  }

  private void registerFunctionServerComponentFactory() throws Exception {

    FunctionServerComponentFactory serverComponentFactory = new FunctionServerComponentFactory();
    serverComponentFactory.setClassifier(CLASSIFIER);

    register(serverComponentFactory, _componentRepository);
  }

  private LiveDataClient createMockLiveDataClient() throws IOException {

    return new LiveDataClient() {

      final Map<ExternalIdBundle, Double> marketData = MarketDataResourcesLoader.getData(
          "/usdMarketQuotes-20140122.properties", "Ticker");
      long counter = 0;

      @Override
      public void subscribe(UserPrincipal user,
                            LiveDataSpecification requestedSpecification,
                            LiveDataListener listener) { }

      @Override
      public void subscribe(UserPrincipal user,
                            Collection<LiveDataSpecification> requestedSpecifications,
                            LiveDataListener listener) {

        List<LiveDataSubscriptionResponse> subResponses = new ArrayList<>();
        List<LiveDataValueUpdate> dataValues = new ArrayList<>();

        for (LiveDataSpecification specification : requestedSpecifications) {
          if (marketData.containsKey(specification.getIdentifiers())) {
            subResponses.add(new LiveDataSubscriptionResponse(specification, LiveDataSubscriptionResult.SUCCESS, null, specification, null, null));
            MutableFudgeMsg msg = OpenGammaFudgeContext.getInstance().newMessage();
            msg.add(MarketDataRequirementNames.MARKET_VALUE, marketData.get(specification.getIdentifiers()));
            dataValues.add(new LiveDataValueUpdateBean(counter++, specification, msg));
          } else {
            subResponses.add(new LiveDataSubscriptionResponse(specification, LiveDataSubscriptionResult.NOT_PRESENT));
          }
        }
        listener.subscriptionResultsReceived(subResponses);

        for (LiveDataValueUpdate value : dataValues) {
          listener.valueUpdate(value);
        }
      }

      @Override
      public void unsubscribe(UserPrincipal user,
                              LiveDataSpecification fullyQualifiedSpecification,
                              LiveDataListener listener) { }

      @Override
      public void unsubscribe(UserPrincipal user,
                              Collection<LiveDataSpecification> fullyQualifiedSpecifications,
                              LiveDataListener listener) { }

      @Override
      public LiveDataSubscriptionResponse snapshot(UserPrincipal user,
                                                   LiveDataSpecification requestedSpecification,
                                                   long timeout) {
        return null;
      }

      @Override
      public Collection<LiveDataSubscriptionResponse> snapshot(UserPrincipal user,
                                                               Collection<LiveDataSpecification> requestedSpecifications,
                                                               long timeout) {
        return null;
      }

      @Override
      public String getDefaultNormalizationRuleSetId() {
        return null;
      }

      @Override
      public void close() {

      }

      @Override
      public boolean isEntitled(UserPrincipal user, LiveDataSpecification requestedSpecification) {
        return false;
      }

      @Override
      public Map<LiveDataSpecification, Boolean> isEntitled(UserPrincipal user,
                                                            Collection<LiveDataSpecification> requestedSpecifications) {
        return null;
      }
    };
  }

  private JmsConnector createJmsConnector() {
    final JmsConnectorFactoryBean factory = new JmsConnectorFactoryBean();
    factory.setName(getClass().getSimpleName());
    factory.setClientBrokerUri(URI.create("vm://remotingTestBroker"));
    factory.setConnectionFactory(new ActiveMQConnectionFactory(factory.getClientBrokerUri()));
    return factory.getObjectCreating();
  }

  @SuppressWarnings("unchecked")
  private LinkedHashMap<String, String> addComponentsToRepo(Map<Class<?>, Object> componentMap) {

    LinkedHashMap<String, String> props = new LinkedHashMap<>();

    for (Map.Entry<Class<?>, Object> entry : componentMap.entrySet()) {
      final Class<?> clss = entry.getKey();
      _componentRepository.registerComponent((Class<Object>) clss, CLASSIFIER, entry.getValue());
      props.put(clss.getSimpleName(), clss.getSimpleName() + "::" + CLASSIFIER);
    }
    return props;
  }

  @AfterClass
  public void tearDown() {
    System.out.println("Shutting down components");
    _componentRepository.stop();
  }

  private void register(ComponentFactory componentFactory,
                        ComponentRepository repo) throws Exception {
    register(componentFactory, repo, new LinkedHashMap<String, String>());
  }

  private void register(ComponentFactory componentFactory,
                        ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    componentFactory.init(repo, configuration);
  }

}
