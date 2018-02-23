/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.engine;

import com.google.common.cache.Cache;
import com.opengamma.sesame.cache.CacheProvider;
import com.opengamma.util.ArgumentChecker;

/**
 * Immutable provider of a cache.
 */
public class DefaultCacheProvider implements CacheProvider {

  private final Cache<Object, Object> _cache;

  /**
   * Creates a new instance which provides the supplied cache.
   *
   * @param cache the cache, not null
   */
  public DefaultCacheProvider(Cache<Object, Object> cache) {
    _cache = ArgumentChecker.notNull(cache, "cache");
  }

  /**
   * @return the cache, not null
   */
  @Override
  public Cache<Object, Object> get() {
    return _cache;
  }
}
