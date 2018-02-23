/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * A commodity notional of a swap leg. such as 2000 bushels of corn.
 */
@BeanDefinition
public class CommodityNotional extends Notional {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   */
  public CommodityNotional() {
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(NotionalVisitor<T> visitor) {
    return visitor.visitCommodityNotional(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CommodityNotional}.
   * @return the meta-bean, not null
   */
  public static CommodityNotional.Meta meta() {
    return CommodityNotional.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CommodityNotional.Meta.INSTANCE);
  }

  @Override
  public CommodityNotional.Meta metaBean() {
    return CommodityNotional.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public CommodityNotional clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("CommodityNotional{");
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
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CommodityNotional}.
   */
  public static class Meta extends Notional.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends CommodityNotional> builder() {
      return new DirectBeanBuilder<CommodityNotional>(new CommodityNotional());
    }

    @Override
    public Class<? extends CommodityNotional> beanType() {
      return CommodityNotional.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
