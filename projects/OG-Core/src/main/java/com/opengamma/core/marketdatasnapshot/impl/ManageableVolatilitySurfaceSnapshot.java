/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.marketdatasnapshot.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.fudgemsg.types.IndicatorType;
import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.Maps;
import com.opengamma.core.marketdatasnapshot.ValueSnapshot;
import com.opengamma.core.marketdatasnapshot.VolatilitySurfaceSnapshot;
import com.opengamma.util.tuple.Pair;

/**
 * 
 */
@BeanDefinition
public class ManageableVolatilitySurfaceSnapshot implements Bean, VolatilitySurfaceSnapshot, Serializable {
  
  /**
   * The values in the snapshot.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private Map<Pair<Object, Object>, ValueSnapshot> _values;
  
  /**
   * Creates a Fudge representation of the snapshot:
   * <pre>
   *   message {
   *     message { // map
   *       repeated Pair key = 1;
   *       repeated ValueSnapshot value = 2;
   *     } values;
   *   }
   * </pre>
   * 
   * @param serializer Fudge serialization context, not null
   * @return the message representation of this snapshot
   */
  public FudgeMsg toFudgeMsg(final FudgeSerializer serializer) {
    MutableFudgeMsg ret = serializer.newMessage();
    // TODO: this should not be adding it's own class header; the caller should be doing that, or this be registered as a generic builder for VolatilitySurfaceSnapshot and that class name be added
    FudgeSerializer.addClassHeader(ret, ManageableVolatilitySurfaceSnapshot.class);
    MutableFudgeMsg valuesMsg = serializer.newMessage();
    if (_values != null) {
      for (Entry<Pair<Object, Object>, ValueSnapshot> entry : _values.entrySet()) {
        serializer.addToMessage(valuesMsg, null, 1, entry.getKey());
        if (entry.getValue() == null) {
          valuesMsg.add(2, IndicatorType.INSTANCE);
        } else {
          serializer.addToMessage(valuesMsg, null, 2, entry.getValue());
        }
      }
    }
    ret.add("values", valuesMsg);
    return ret;
  }

  // TODO: externalize the message representation to a Fudge builder

  /**
   * Creates a snapshot object from a Fudge message representation. See {@link #toFudgeMsg}
   * for the message format.
   * 
   * @param deserializer the Fudge deserialization context, not null
   * @param msg message containing the snapshot representation, not null
   * @return a snapshot object
   */
  @SuppressWarnings("unchecked")
  public static ManageableVolatilitySurfaceSnapshot fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    final HashMap<Pair<Object, Object>, ValueSnapshot> values = Maps.newHashMap();
    Pair<Object, Object> key = null;
    for (FudgeField fudgeField : msg.getMessage("values")) {
      Integer ordinal = fudgeField.getOrdinal();
      if (ordinal == null) {
        continue;
      }
      final int intValue = ordinal.intValue();
      if (intValue == 1) {
        key = deserializer.fieldValueToObject(Pair.class, fudgeField);
      } else if (intValue == 2) {
        ValueSnapshot value = deserializer.fieldValueToObject(ValueSnapshot.class, fudgeField);
        values.put(key, value);
        key = null;
      }
    }
    final ManageableVolatilitySurfaceSnapshot ret = new ManageableVolatilitySurfaceSnapshot();
    ret.setValues(values);
    return ret;
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ManageableVolatilitySurfaceSnapshot}.
   * @return the meta-bean, not null
   */
  public static ManageableVolatilitySurfaceSnapshot.Meta meta() {
    return ManageableVolatilitySurfaceSnapshot.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ManageableVolatilitySurfaceSnapshot.Meta.INSTANCE);
  }

  @Override
  public ManageableVolatilitySurfaceSnapshot.Meta metaBean() {
    return ManageableVolatilitySurfaceSnapshot.Meta.INSTANCE;
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
   * Gets the values in the snapshot.
   * @return the value of the property, not null
   */
  @Override
  public Map<Pair<Object, Object>, ValueSnapshot> getValues() {
    return _values;
  }

  /**
   * Sets the values in the snapshot.
   * @param values  the new value of the property, not null
   */
  public void setValues(Map<Pair<Object, Object>, ValueSnapshot> values) {
    JodaBeanUtils.notNull(values, "values");
    this._values = values;
  }

  /**
   * Gets the the {@code values} property.
   * @return the property, not null
   */
  public final Property<Map<Pair<Object, Object>, ValueSnapshot>> values() {
    return metaBean().values().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public ManageableVolatilitySurfaceSnapshot clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ManageableVolatilitySurfaceSnapshot other = (ManageableVolatilitySurfaceSnapshot) obj;
      return JodaBeanUtils.equal(getValues(), other.getValues());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getValues());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("ManageableVolatilitySurfaceSnapshot{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("values").append('=').append(JodaBeanUtils.toString(getValues())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ManageableVolatilitySurfaceSnapshot}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code values} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Map<Pair<Object, Object>, ValueSnapshot>> _values = DirectMetaProperty.ofReadWrite(
        this, "values", ManageableVolatilitySurfaceSnapshot.class, (Class) Map.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "values");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -823812830:  // values
          return _values;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ManageableVolatilitySurfaceSnapshot> builder() {
      return new DirectBeanBuilder<ManageableVolatilitySurfaceSnapshot>(new ManageableVolatilitySurfaceSnapshot());
    }

    @Override
    public Class<? extends ManageableVolatilitySurfaceSnapshot> beanType() {
      return ManageableVolatilitySurfaceSnapshot.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code values} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Map<Pair<Object, Object>, ValueSnapshot>> values() {
      return _values;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -823812830:  // values
          return ((ManageableVolatilitySurfaceSnapshot) bean).getValues();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -823812830:  // values
          ((ManageableVolatilitySurfaceSnapshot) bean).setValues((Map<Pair<Object, Object>, ValueSnapshot>) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((ManageableVolatilitySurfaceSnapshot) bean)._values, "values");
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}