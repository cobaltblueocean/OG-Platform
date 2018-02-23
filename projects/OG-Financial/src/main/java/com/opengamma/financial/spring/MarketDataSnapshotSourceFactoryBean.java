/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.spring;

import java.util.Map;

import net.sf.ehcache.CacheManager;

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

import com.opengamma.core.marketdatasnapshot.MarketDataSnapshotSource;
import com.opengamma.core.marketdatasnapshot.impl.DelegatingSnapshotSource;
import com.opengamma.master.marketdatasnapshot.MarketDataSnapshotMaster;
import com.opengamma.master.marketdatasnapshot.impl.MasterSnapshotSource;
import com.opengamma.util.spring.SpringFactoryBean;

/**
 * Spring factory bean to create the snapshot source.
 */
@BeanDefinition
public class MarketDataSnapshotSourceFactoryBean extends SpringFactoryBean<MarketDataSnapshotSource> {

  /**
   * The exchange master.
   */
  @PropertyDefinition
  private MarketDataSnapshotMaster _snapshotMaster;
  /**
   * The cache manager.
   */
  @PropertyDefinition
  private CacheManager _cacheManager;

  /**
   * Creates an instance.
   */
  public MarketDataSnapshotSourceFactoryBean() {
    super(MarketDataSnapshotSource.class);
  }

  //-------------------------------------------------------------------------
  @Override
  protected MarketDataSnapshotSource createObject() {
    MarketDataSnapshotSource source = new MasterSnapshotSource(getSnapshotMaster());
    source = new DelegatingSnapshotSource(source);
    return source;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code MarketDataSnapshotSourceFactoryBean}.
   * @return the meta-bean, not null
   */
  public static MarketDataSnapshotSourceFactoryBean.Meta meta() {
    return MarketDataSnapshotSourceFactoryBean.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(MarketDataSnapshotSourceFactoryBean.Meta.INSTANCE);
  }

  @Override
  public MarketDataSnapshotSourceFactoryBean.Meta metaBean() {
    return MarketDataSnapshotSourceFactoryBean.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the exchange master.
   * @return the value of the property
   */
  public MarketDataSnapshotMaster getSnapshotMaster() {
    return _snapshotMaster;
  }

  /**
   * Sets the exchange master.
   * @param snapshotMaster  the new value of the property
   */
  public void setSnapshotMaster(MarketDataSnapshotMaster snapshotMaster) {
    this._snapshotMaster = snapshotMaster;
  }

  /**
   * Gets the the {@code snapshotMaster} property.
   * @return the property, not null
   */
  public final Property<MarketDataSnapshotMaster> snapshotMaster() {
    return metaBean().snapshotMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the cache manager.
   * @return the value of the property
   */
  public CacheManager getCacheManager() {
    return _cacheManager;
  }

  /**
   * Sets the cache manager.
   * @param cacheManager  the new value of the property
   */
  public void setCacheManager(CacheManager cacheManager) {
    this._cacheManager = cacheManager;
  }

  /**
   * Gets the the {@code cacheManager} property.
   * @return the property, not null
   */
  public final Property<CacheManager> cacheManager() {
    return metaBean().cacheManager().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public MarketDataSnapshotSourceFactoryBean clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      MarketDataSnapshotSourceFactoryBean other = (MarketDataSnapshotSourceFactoryBean) obj;
      return JodaBeanUtils.equal(getSnapshotMaster(), other.getSnapshotMaster()) &&
          JodaBeanUtils.equal(getCacheManager(), other.getCacheManager()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getSnapshotMaster());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCacheManager());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("MarketDataSnapshotSourceFactoryBean{");
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
    buf.append("snapshotMaster").append('=').append(JodaBeanUtils.toString(getSnapshotMaster())).append(',').append(' ');
    buf.append("cacheManager").append('=').append(JodaBeanUtils.toString(getCacheManager())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code MarketDataSnapshotSourceFactoryBean}.
   */
  public static class Meta extends SpringFactoryBean.Meta<MarketDataSnapshotSource> {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code snapshotMaster} property.
     */
    private final MetaProperty<MarketDataSnapshotMaster> _snapshotMaster = DirectMetaProperty.ofReadWrite(
        this, "snapshotMaster", MarketDataSnapshotSourceFactoryBean.class, MarketDataSnapshotMaster.class);
    /**
     * The meta-property for the {@code cacheManager} property.
     */
    private final MetaProperty<CacheManager> _cacheManager = DirectMetaProperty.ofReadWrite(
        this, "cacheManager", MarketDataSnapshotSourceFactoryBean.class, CacheManager.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "snapshotMaster",
        "cacheManager");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -2046916282:  // snapshotMaster
          return _snapshotMaster;
        case -1452875317:  // cacheManager
          return _cacheManager;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends MarketDataSnapshotSourceFactoryBean> builder() {
      return new DirectBeanBuilder<MarketDataSnapshotSourceFactoryBean>(new MarketDataSnapshotSourceFactoryBean());
    }

    @Override
    public Class<? extends MarketDataSnapshotSourceFactoryBean> beanType() {
      return MarketDataSnapshotSourceFactoryBean.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code snapshotMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<MarketDataSnapshotMaster> snapshotMaster() {
      return _snapshotMaster;
    }

    /**
     * The meta-property for the {@code cacheManager} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CacheManager> cacheManager() {
      return _cacheManager;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -2046916282:  // snapshotMaster
          return ((MarketDataSnapshotSourceFactoryBean) bean).getSnapshotMaster();
        case -1452875317:  // cacheManager
          return ((MarketDataSnapshotSourceFactoryBean) bean).getCacheManager();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -2046916282:  // snapshotMaster
          ((MarketDataSnapshotSourceFactoryBean) bean).setSnapshotMaster((MarketDataSnapshotMaster) newValue);
          return;
        case -1452875317:  // cacheManager
          ((MarketDataSnapshotSourceFactoryBean) bean).setCacheManager((CacheManager) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
