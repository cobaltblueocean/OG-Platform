/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.opengamma.sesame.Environment;
import com.opengamma.sesame.config.EngineUtils;
import com.opengamma.sesame.function.scenarios.FilteredScenarioDefinition;
import com.opengamma.sesame.graph.ClassNode;
import com.opengamma.sesame.graph.FunctionId;
import com.opengamma.sesame.graph.FunctionIdProvider;
import com.opengamma.sesame.graph.FunctionModelNode;
import com.opengamma.sesame.graph.InterfaceNode;
import com.opengamma.sesame.graph.NodeDecorator;
import com.opengamma.sesame.graph.ProxyNode;
import com.opengamma.sesame.proxy.AbstractProxyInvocationHandler;
import com.opengamma.sesame.proxy.InvocationHandlerFactory;
import com.opengamma.sesame.proxy.ProxyInvocationHandler;
import com.opengamma.util.ArgumentChecker;

/**
 * Decorates a node in the graph with a proxy which performs memoization using a cache.
 */
public class CachingProxyDecorator extends NodeDecorator {

  private static final Logger s_logger = LoggerFactory.getLogger(CachingProxyDecorator.class);

  private final ExecutingMethodsThreadLocal _executingMethods;
  private final CacheProvider _cacheProvider;

  /**
   * Constructs an instance for throwaway uses where the cache doesn't need to be invalidated (e.g. tools)
   *
   * @param cacheProvider provider of a cache used to store the calculated values
   */
  public CachingProxyDecorator(CacheProvider cacheProvider) {
    this(cacheProvider, new ExecutingMethodsThreadLocal());
  }

  /**
   * @param cacheProvider provider of a cache used to store the calculated values
   * @param executingMethods records the currently executing methods and allows cache entries to be removed when
   */
  public CachingProxyDecorator(CacheProvider cacheProvider, ExecutingMethodsThreadLocal executingMethods) {
    _cacheProvider = cacheProvider;
    _executingMethods = ArgumentChecker.notNull(executingMethods, "executingMethods");
  }

  @Override
  public FunctionModelNode decorateNode(FunctionModelNode node) {
    if (!(node instanceof ProxyNode) && !(node instanceof InterfaceNode)) {
      return node;
    }
    Class<?> interfaceType;
    Class<?> implementationType;
    if (node instanceof InterfaceNode) {
      implementationType = ((InterfaceNode) node).getImplementationType();
      interfaceType = ((InterfaceNode) node).getType();
    } else {
      implementationType = ((ProxyNode) node).getImplementationType();
      interfaceType = ((ProxyNode) node).getType();
    }
    if (EngineUtils.hasMethodAnnotation(interfaceType, Cacheable.class) ||
        EngineUtils.hasMethodAnnotation(implementationType, Cacheable.class)) {
      Set<Class<?>> subtreeTypes = subtreeImplementationTypes(node);
      CachingHandlerFactory handlerFactory =
          new CachingHandlerFactory(implementationType, interfaceType, _cacheProvider, _executingMethods, subtreeTypes);
      return createProxyNode(node, interfaceType, implementationType, handlerFactory);
    }
    return node;
  }

  /**
   * Returns the types built by all nodes in the node's subtree.
   *
   * @param node a node
   * @return the set of all node types in the node's subtree
   */
  private static Set<Class<?>> subtreeImplementationTypes(FunctionModelNode node) {
    Set<Class<?>> types = new HashSet<>();
    populateSubtreeImplementationTypes(node, types);
    return types;
  }

  private static void populateSubtreeImplementationTypes(FunctionModelNode node, Set<Class<?>> accumulator) {
    // we only want the types for real function nodes, not proxies
    FunctionModelNode concreteNode = node.getConcreteNode();

    if (concreteNode instanceof ClassNode) {
      accumulator.add(((ClassNode) concreteNode).getImplementationType());
    }
    for (FunctionModelNode childNode : node.getDependencies()) {
      populateSubtreeImplementationTypes(childNode, accumulator);
    }
  }

  /**
   * Creates an instance of {@link Handler} when the graph is built.
   * The handler is invoked when a cacheable method is called and takes care of returning a cached result
   * or calculating one and putting it in the cache.
   */
  private static final class CachingHandlerFactory implements InvocationHandlerFactory {

    private final Class<?> _interfaceType;
    private final Class<?> _implementationType;
    private final ExecutingMethodsThreadLocal _executingMethods;
    private final Set<Class<?>> _subtreeTypes;
    private final CacheProvider _cacheProvider;

    private CachingHandlerFactory(Class<?> implementationType,
                                  Class<?> interfaceType,
                                  CacheProvider cacheProvider,
                                  ExecutingMethodsThreadLocal executingMethods,
                                  Set<Class<?>> subtreeTypes) {
      _cacheProvider = ArgumentChecker.notNull(cacheProvider, "cacheProvider");
      _executingMethods = ArgumentChecker.notNull(executingMethods, "executingMethods");
      _subtreeTypes = ArgumentChecker.notNull(subtreeTypes, "subtreeTypes");
      _implementationType = ArgumentChecker.notNull(implementationType, "implementationType");
      _interfaceType = ArgumentChecker.notNull(interfaceType, "interfaceType");
    }

    @Override
    public ProxyInvocationHandler create(Object delegate, ProxyNode node, FunctionIdProvider functionIdProvider) {
      Set<Method> cachedMethods = Sets.newHashSet();
      for (Method method : _interfaceType.getMethods()) {
        if (method.getAnnotation(Cacheable.class) != null) {
          cachedMethods.add(method);
        }
      }
      for (Method method : _implementationType.getMethods()) {
        if (method.getAnnotation(Cacheable.class) != null) {
          // the proxy will always see the interface method. no point caching the instance method
          // need to go up the inheritance hierarchy and find all interface methods implemented by this method
          // and cache those
          for (Class<?> iface : EngineUtils.getInterfaces(_implementationType)) {
            try {
              Method ifaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
              cachedMethods.add(ifaceMethod);
            } catch (NoSuchMethodException e) {
              // expected
            }
          }
        }
      }
      return new Handler(delegate, cachedMethods, _cacheProvider, _executingMethods, _subtreeTypes, functionIdProvider);
    }

    @Override
    public int hashCode() {
      return Objects.hash(_interfaceType, _implementationType);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final CachingHandlerFactory other = (CachingHandlerFactory) obj;
      return
          Objects.equals(this._interfaceType, other._interfaceType) &&
          Objects.equals(this._implementationType, other._implementationType);
    }
  }

  /**
   * Handles method invocations and possibly returns a cached result instead of calling the underlying object.
   * If the method doesn't have a {@link Cacheable} annotation the underlying object is called.
   * If the cache contains an element that corresponds to the method and arguments it's returned and the underlying
   * object isn't called.
   * If the cache doesn't contain an element the underlying object is called and the cache is populated.
   * The values in the cache are futures. This allows multiple threads to request the same value and for all of
   * them to block while the first thread calculates it.
   * This is package scoped for testing.
   */
  /* package */ static final class Handler extends AbstractProxyInvocationHandler {

    private final Object _delegate;
    private final Set<Method> _cachedMethods;
    private final CacheProvider _cacheProvider;
    private final ExecutingMethodsThreadLocal _executingMethods;
    private final Set<Class<?>> _subtreeTypes;
    private final FunctionId _functionId;

    private Handler(Object delegate,
                    Set<Method> cachedMethods,
                    CacheProvider cacheProvider,
                    ExecutingMethodsThreadLocal executingMethods,
                    Set<Class<?>> subtreeTypes,
                    FunctionIdProvider functionIdProvider) {
      super(delegate);
      _subtreeTypes = ArgumentChecker.notNull(subtreeTypes, "subtreeTypes");
      _cacheProvider = ArgumentChecker.notNull(cacheProvider, "cache");
      _executingMethods = ArgumentChecker.notNull(executingMethods, "executingMethods");
      _delegate = ArgumentChecker.notNull(delegate, "delegate");
      _cachedMethods = ArgumentChecker.notNull(cachedMethods, "cachedMethods");
      Object proxiedObject = EngineUtils.getProxiedObject(delegate);
      _functionId = functionIdProvider.getFunctionId(proxiedObject);
    }

    /**
     * Handles a method invocation, returning a cached value if available, otherwise calling the underlying
     * method to produce the value.
     * <p>
     * If the proxied method is not annotated with {@link Cacheable} it is always invoked. If the
     * method is annotated a key is created representing the method, the receiver and all the arguments.
     * This key is used to query the cache.
     *
     * @param proxy  the proxy on which the method was invoked
     * @param method  the method which was invoked
     * @param args  the method arguments
     * @return  the return value of the underlying method or a previously cached value
     * @throws Throwable  if the underlying method throws an exception
     */
    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
      // check if the method is annotated with @Cacheable.
      if (_cachedMethods.contains(method)) { // the method is @Cacheable
        Object[] keyArgs = getArgumentsForCacheKey(args);
        // create a key representing the method call - the receiver's ID, the method and its arguments
        MethodInvocationKey key = new MethodInvocationKey(_functionId, method, keyArgs);
        // create a task to calculate the value if it's not in the cache - calls the underlying method
        CallableMethod calculationTask = new CallableMethod(key, method, args);
        // get the value from the cache - if it's not already present it's calculated
        return _cacheProvider.get().get(key, calculationTask);
      } else {
        // the method isn't annotated with @Cacheable, call it
        try {
          s_logger.debug("Calculating non-cacheable result by invoking method {}", method);
          return method.invoke(_delegate, args);
        } catch (InvocationTargetException e) {
          throw e.getCause();
        }
      }
    }

    /**
     * <p>Returns the method call arguments that should be used in the cache key for the call's return value.
     * If the input arguments don't have an {@link Environment} as their first element they are returned.
     * If the input arguments have an environment as their first element a new set of arguments is returned
     * that is a copy of the input arguments but containing a different environment.</p>
     *
     * <p>The new environment is copied from the environment in the input but uses a different set of scenario
     * arguments. The new arguments only include the arguments for the functions below this function
     * in the graph.</p>
     *
     * <p>Scenarios above the current function in the graph can't affect the function's return value. If they
     * were included in the cache key they could cause a cache miss even though there is no way they can
     * invalidate the cache entry. By removing those arguments from the key we ensure that the only arguments
     * in the key are the ones that can change this function's return value.</p>
     *
     * @param args the arguments to a method call
     * @return the arguments that should be used in the cache key
     */
    private Object[] getArgumentsForCacheKey(Object[] args) {
      if (args == null || args.length == 0 || !(args[0] instanceof Environment)) {
        return args;
      }
      Environment env = (Environment) args[0];

      if (env.getScenarioDefinition().isEmpty()) {
        return args;
      }
      FilteredScenarioDefinition scenarioDef = env.getScenarioDefinition().forFunctions(_subtreeTypes);
      Environment newEnv = env.withScenarioDefinition(scenarioDef);
      Object[] keyArgs = args.clone();
      keyArgs[0] = newEnv;
      return keyArgs;
    }

    /** Visible for testing */
    /* package */ Object getDelegate() {
      return _delegate;
    }

    private class CallableMethod implements Callable<Object> {

      private final MethodInvocationKey _key;
      private final Method _method;
      private final Object[] _args;

      public CallableMethod(MethodInvocationKey key, Method method, Object[] args) {
        _key = key;
        _method = method;
        _args = args;
      }

      @Override
      public Object call() throws Exception {
        try {
          _executingMethods.push(_key);
          return _method.invoke(_delegate, _args);
        } catch (IllegalAccessException | InvocationTargetException e) {
          Throwable cause = e.getCause();
          if (cause instanceof Error) {
            throw ((Error) cause);
          } else {
            throw ((Exception) cause);
          }
        } finally {
          _executingMethods.pop();
        }
      }
    }
  }
}
