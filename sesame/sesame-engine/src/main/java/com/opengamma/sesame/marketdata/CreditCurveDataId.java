/**
* Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
* <p/>
* Please see distribution for license.
*/
package com.opengamma.sesame.marketdata;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.analytics.isda.credit.CreditCurveData;
import com.opengamma.financial.analytics.isda.credit.CreditCurveDataKey;

/**
* Identifies a CreditCurveData by snapshot name and key.
*/
@BeanDefinition
public class CreditCurveDataId implements MarketDataId<CreditCurveData>, ImmutableBean, Serializable {

  /** The name of the credit curve snapshot. */
  @PropertyDefinition(validate = "notEmpty")
  private final String _snapshot;

  /** The credit curve key. */
  @PropertyDefinition(validate = "notNull")
  private final CreditCurveDataKey _key;

  @Override
  public Class<CreditCurveData> getMarketDataType() {
    return CreditCurveData.class;
  }

  /**
   * Returns an ID for a credit curve data with the specified name/key
   *
   * @param snapshot the credit curve data snapshot name, not empty
   * @param key the credit curve key, not null
   * @return an ID for a credit curve data with the specified name/key
   */
  public static CreditCurveDataId of(String snapshot, CreditCurveDataKey key) {
    return CreditCurveDataId.builder().snapshot(snapshot).key(key).build();
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CreditCurveDataId}.
   * @return the meta-bean, not null
   */
  public static CreditCurveDataId.Meta meta() {
    return CreditCurveDataId.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CreditCurveDataId.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CreditCurveDataId.Builder builder() {
    return new CreditCurveDataId.Builder();
  }

  /**
   * Restricted constructor.
   * @param builder  the builder to copy from, not null
   */
  protected CreditCurveDataId(CreditCurveDataId.Builder builder) {
    JodaBeanUtils.notEmpty(builder._snapshot, "snapshot");
    JodaBeanUtils.notNull(builder._key, "key");
    this._snapshot = builder._snapshot;
    this._key = builder._key;
  }

  @Override
  public CreditCurveDataId.Meta metaBean() {
    return CreditCurveDataId.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the credit curve snapshot.
   * @return the value of the property, not empty
   */
  public String getSnapshot() {
    return _snapshot;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the credit curve key.
   * @return the value of the property, not null
   */
  public CreditCurveDataKey getKey() {
    return _key;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CreditCurveDataId other = (CreditCurveDataId) obj;
      return JodaBeanUtils.equal(getSnapshot(), other.getSnapshot()) &&
          JodaBeanUtils.equal(getKey(), other.getKey());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getSnapshot());
    hash = hash * 31 + JodaBeanUtils.hashCode(getKey());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("CreditCurveDataId{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("snapshot").append('=').append(JodaBeanUtils.toString(getSnapshot())).append(',').append(' ');
    buf.append("key").append('=').append(JodaBeanUtils.toString(getKey())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CreditCurveDataId}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code snapshot} property.
     */
    private final MetaProperty<String> _snapshot = DirectMetaProperty.ofImmutable(
        this, "snapshot", CreditCurveDataId.class, String.class);
    /**
     * The meta-property for the {@code key} property.
     */
    private final MetaProperty<CreditCurveDataKey> _key = DirectMetaProperty.ofImmutable(
        this, "key", CreditCurveDataId.class, CreditCurveDataKey.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "snapshot",
        "key");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 284874180:  // snapshot
          return _snapshot;
        case 106079:  // key
          return _key;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CreditCurveDataId.Builder builder() {
      return new CreditCurveDataId.Builder();
    }

    @Override
    public Class<? extends CreditCurveDataId> beanType() {
      return CreditCurveDataId.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code snapshot} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> snapshot() {
      return _snapshot;
    }

    /**
     * The meta-property for the {@code key} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CreditCurveDataKey> key() {
      return _key;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 284874180:  // snapshot
          return ((CreditCurveDataId) bean).getSnapshot();
        case 106079:  // key
          return ((CreditCurveDataId) bean).getKey();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code CreditCurveDataId}.
   */
  public static class Builder extends DirectFieldsBeanBuilder<CreditCurveDataId> {

    private String _snapshot;
    private CreditCurveDataKey _key;

    /**
     * Restricted constructor.
     */
    protected Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    protected Builder(CreditCurveDataId beanToCopy) {
      this._snapshot = beanToCopy.getSnapshot();
      this._key = beanToCopy.getKey();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 284874180:  // snapshot
          return _snapshot;
        case 106079:  // key
          return _key;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 284874180:  // snapshot
          this._snapshot = (String) newValue;
          break;
        case 106079:  // key
          this._key = (CreditCurveDataKey) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public CreditCurveDataId build() {
      return new CreditCurveDataId(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code snapshot} property in the builder.
     * @param snapshot  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder snapshot(String snapshot) {
      JodaBeanUtils.notEmpty(snapshot, "snapshot");
      this._snapshot = snapshot;
      return this;
    }

    /**
     * Sets the {@code key} property in the builder.
     * @param key  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder key(CreditCurveDataKey key) {
      JodaBeanUtils.notNull(key, "key");
      this._key = key;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("CreditCurveDataId.Builder{");
      int len = buf.length();
      toString(buf);
      if (buf.length() > len) {
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
      return buf.toString();
    }

    protected void toString(StringBuilder buf) {
      buf.append("snapshot").append('=').append(JodaBeanUtils.toString(_snapshot)).append(',').append(' ');
      buf.append("key").append('=').append(JodaBeanUtils.toString(_key)).append(',').append(' ');
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
