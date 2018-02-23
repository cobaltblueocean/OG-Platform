/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.spring;

import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.springframework.beans.factory.InitializingBean;

import com.opengamma.financial.analytics.fxforwardcurve.FXForwardCurveConfigPopulator;
import com.opengamma.financial.analytics.ircurve.YieldCurveConfigPopulator;
import com.opengamma.financial.analytics.ircurve.calcconfig.MultiCurveCalculationConfigPopulator;
import com.opengamma.financial.analytics.volatility.surface.EquityOptionSurfaceConfigPopulator;
import com.opengamma.financial.analytics.volatility.surface.FXOptionVolatilitySurfaceConfigPopulator;
import com.opengamma.financial.analytics.volatility.surface.IRFutureOptionSurfaceConfigPopulator;
import com.opengamma.financial.analytics.volatility.surface.SwaptionVolatilitySurfaceConfigPopulator;
import com.opengamma.financial.currency.CurrencyMatrixConfigPopulator;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.util.ArgumentChecker;

/**
 * Spring factory bean to create the database config master.
 */
@BeanDefinition
public class ConfigMasterPopulatorsFactoryBean extends DirectBean implements InitializingBean {

  /**
   * The config master.
   */
  @PropertyDefinition
  private ConfigMaster _configMaster;
  /**
   * The flag to create the yield curves in the config master.
   */
  @PropertyDefinition
  private boolean _yieldCurve;
  /**
   * The flag to create the currency matrix in the config master.
   */
  @PropertyDefinition
  private boolean _currencyMatrix;
  /**
   * The flag to create the surfaces in the config master.
   */
  @PropertyDefinition
  private boolean _swaptionVolatilitySurface;
  /**
   * The flag to create the surfaces in the config master.
   */
  @PropertyDefinition
  private boolean _irFutureOptionSurface;
  /**
   * The flag to create the surfaces in the config master.
   */
  @PropertyDefinition
  private boolean _fxOptionVolatilitySurface;
  /**
   * The flag to create the surfaces in the config master.
   */
  @PropertyDefinition
  private boolean _equityOptionSurface;
  /**
   * The flag to create the volatility cubes in the config master.
   */
  @PropertyDefinition
  private boolean _volatilityCube;
  /**
   * The flag to create the FX forward curves in the config master.
   */
  @PropertyDefinition
  private boolean _fxForwardCurve;
  /**
   * The flag to create curve calculation configurations in the config master.
   */
  @PropertyDefinition
  private boolean _curveCalculationConfiguration;

  //-------------------------------------------------------------------------
  @Override
  public void afterPropertiesSet() {
    final ConfigMaster cm = getConfigMaster();
    ArgumentChecker.notNull(cm, "ConfigMaster");

    if (isYieldCurve()) {
      new YieldCurveConfigPopulator(cm);
    }
    if (isCurrencyMatrix()) {
      // TODO: [PLAT-2379] This won't work if the currency pair conventions aren't already loaded
      CurrencyMatrixConfigPopulator.populateCurrencyMatrixConfigMaster(cm);
    }
    if (isSwaptionVolatilitySurface()) {
      new SwaptionVolatilitySurfaceConfigPopulator(cm);
    }
    if (isIrFutureOptionSurface()) {
      new IRFutureOptionSurfaceConfigPopulator(cm);
    }
    if (isFxOptionVolatilitySurface()) {
      new FXOptionVolatilitySurfaceConfigPopulator(cm);
    }
    if (isEquityOptionSurface()) {
      new EquityOptionSurfaceConfigPopulator(cm);
    }
    if (isFxForwardCurve()) {
      new FXForwardCurveConfigPopulator(cm);
    }
    if (isCurveCalculationConfiguration()) {
      new MultiCurveCalculationConfigPopulator(cm);
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ConfigMasterPopulatorsFactoryBean}.
   * @return the meta-bean, not null
   */
  public static ConfigMasterPopulatorsFactoryBean.Meta meta() {
    return ConfigMasterPopulatorsFactoryBean.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ConfigMasterPopulatorsFactoryBean.Meta.INSTANCE);
  }

  @Override
  public ConfigMasterPopulatorsFactoryBean.Meta metaBean() {
    return ConfigMasterPopulatorsFactoryBean.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the config master.
   * @return the value of the property
   */
  public ConfigMaster getConfigMaster() {
    return _configMaster;
  }

  /**
   * Sets the config master.
   * @param configMaster  the new value of the property
   */
  public void setConfigMaster(ConfigMaster configMaster) {
    this._configMaster = configMaster;
  }

  /**
   * Gets the the {@code configMaster} property.
   * @return the property, not null
   */
  public final Property<ConfigMaster> configMaster() {
    return metaBean().configMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the yield curves in the config master.
   * @return the value of the property
   */
  public boolean isYieldCurve() {
    return _yieldCurve;
  }

  /**
   * Sets the flag to create the yield curves in the config master.
   * @param yieldCurve  the new value of the property
   */
  public void setYieldCurve(boolean yieldCurve) {
    this._yieldCurve = yieldCurve;
  }

  /**
   * Gets the the {@code yieldCurve} property.
   * @return the property, not null
   */
  public final Property<Boolean> yieldCurve() {
    return metaBean().yieldCurve().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the currency matrix in the config master.
   * @return the value of the property
   */
  public boolean isCurrencyMatrix() {
    return _currencyMatrix;
  }

  /**
   * Sets the flag to create the currency matrix in the config master.
   * @param currencyMatrix  the new value of the property
   */
  public void setCurrencyMatrix(boolean currencyMatrix) {
    this._currencyMatrix = currencyMatrix;
  }

  /**
   * Gets the the {@code currencyMatrix} property.
   * @return the property, not null
   */
  public final Property<Boolean> currencyMatrix() {
    return metaBean().currencyMatrix().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the surfaces in the config master.
   * @return the value of the property
   */
  public boolean isSwaptionVolatilitySurface() {
    return _swaptionVolatilitySurface;
  }

  /**
   * Sets the flag to create the surfaces in the config master.
   * @param swaptionVolatilitySurface  the new value of the property
   */
  public void setSwaptionVolatilitySurface(boolean swaptionVolatilitySurface) {
    this._swaptionVolatilitySurface = swaptionVolatilitySurface;
  }

  /**
   * Gets the the {@code swaptionVolatilitySurface} property.
   * @return the property, not null
   */
  public final Property<Boolean> swaptionVolatilitySurface() {
    return metaBean().swaptionVolatilitySurface().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the surfaces in the config master.
   * @return the value of the property
   */
  public boolean isIrFutureOptionSurface() {
    return _irFutureOptionSurface;
  }

  /**
   * Sets the flag to create the surfaces in the config master.
   * @param irFutureOptionSurface  the new value of the property
   */
  public void setIrFutureOptionSurface(boolean irFutureOptionSurface) {
    this._irFutureOptionSurface = irFutureOptionSurface;
  }

  /**
   * Gets the the {@code irFutureOptionSurface} property.
   * @return the property, not null
   */
  public final Property<Boolean> irFutureOptionSurface() {
    return metaBean().irFutureOptionSurface().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the surfaces in the config master.
   * @return the value of the property
   */
  public boolean isFxOptionVolatilitySurface() {
    return _fxOptionVolatilitySurface;
  }

  /**
   * Sets the flag to create the surfaces in the config master.
   * @param fxOptionVolatilitySurface  the new value of the property
   */
  public void setFxOptionVolatilitySurface(boolean fxOptionVolatilitySurface) {
    this._fxOptionVolatilitySurface = fxOptionVolatilitySurface;
  }

  /**
   * Gets the the {@code fxOptionVolatilitySurface} property.
   * @return the property, not null
   */
  public final Property<Boolean> fxOptionVolatilitySurface() {
    return metaBean().fxOptionVolatilitySurface().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the surfaces in the config master.
   * @return the value of the property
   */
  public boolean isEquityOptionSurface() {
    return _equityOptionSurface;
  }

  /**
   * Sets the flag to create the surfaces in the config master.
   * @param equityOptionSurface  the new value of the property
   */
  public void setEquityOptionSurface(boolean equityOptionSurface) {
    this._equityOptionSurface = equityOptionSurface;
  }

  /**
   * Gets the the {@code equityOptionSurface} property.
   * @return the property, not null
   */
  public final Property<Boolean> equityOptionSurface() {
    return metaBean().equityOptionSurface().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the volatility cubes in the config master.
   * @return the value of the property
   */
  public boolean isVolatilityCube() {
    return _volatilityCube;
  }

  /**
   * Sets the flag to create the volatility cubes in the config master.
   * @param volatilityCube  the new value of the property
   */
  public void setVolatilityCube(boolean volatilityCube) {
    this._volatilityCube = volatilityCube;
  }

  /**
   * Gets the the {@code volatilityCube} property.
   * @return the property, not null
   */
  public final Property<Boolean> volatilityCube() {
    return metaBean().volatilityCube().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create the FX forward curves in the config master.
   * @return the value of the property
   */
  public boolean isFxForwardCurve() {
    return _fxForwardCurve;
  }

  /**
   * Sets the flag to create the FX forward curves in the config master.
   * @param fxForwardCurve  the new value of the property
   */
  public void setFxForwardCurve(boolean fxForwardCurve) {
    this._fxForwardCurve = fxForwardCurve;
  }

  /**
   * Gets the the {@code fxForwardCurve} property.
   * @return the property, not null
   */
  public final Property<Boolean> fxForwardCurve() {
    return metaBean().fxForwardCurve().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag to create curve calculation configurations in the config master.
   * @return the value of the property
   */
  public boolean isCurveCalculationConfiguration() {
    return _curveCalculationConfiguration;
  }

  /**
   * Sets the flag to create curve calculation configurations in the config master.
   * @param curveCalculationConfiguration  the new value of the property
   */
  public void setCurveCalculationConfiguration(boolean curveCalculationConfiguration) {
    this._curveCalculationConfiguration = curveCalculationConfiguration;
  }

  /**
   * Gets the the {@code curveCalculationConfiguration} property.
   * @return the property, not null
   */
  public final Property<Boolean> curveCalculationConfiguration() {
    return metaBean().curveCalculationConfiguration().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public ConfigMasterPopulatorsFactoryBean clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ConfigMasterPopulatorsFactoryBean other = (ConfigMasterPopulatorsFactoryBean) obj;
      return JodaBeanUtils.equal(getConfigMaster(), other.getConfigMaster()) &&
          (isYieldCurve() == other.isYieldCurve()) &&
          (isCurrencyMatrix() == other.isCurrencyMatrix()) &&
          (isSwaptionVolatilitySurface() == other.isSwaptionVolatilitySurface()) &&
          (isIrFutureOptionSurface() == other.isIrFutureOptionSurface()) &&
          (isFxOptionVolatilitySurface() == other.isFxOptionVolatilitySurface()) &&
          (isEquityOptionSurface() == other.isEquityOptionSurface()) &&
          (isVolatilityCube() == other.isVolatilityCube()) &&
          (isFxForwardCurve() == other.isFxForwardCurve()) &&
          (isCurveCalculationConfiguration() == other.isCurveCalculationConfiguration());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getConfigMaster());
    hash = hash * 31 + JodaBeanUtils.hashCode(isYieldCurve());
    hash = hash * 31 + JodaBeanUtils.hashCode(isCurrencyMatrix());
    hash = hash * 31 + JodaBeanUtils.hashCode(isSwaptionVolatilitySurface());
    hash = hash * 31 + JodaBeanUtils.hashCode(isIrFutureOptionSurface());
    hash = hash * 31 + JodaBeanUtils.hashCode(isFxOptionVolatilitySurface());
    hash = hash * 31 + JodaBeanUtils.hashCode(isEquityOptionSurface());
    hash = hash * 31 + JodaBeanUtils.hashCode(isVolatilityCube());
    hash = hash * 31 + JodaBeanUtils.hashCode(isFxForwardCurve());
    hash = hash * 31 + JodaBeanUtils.hashCode(isCurveCalculationConfiguration());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(352);
    buf.append("ConfigMasterPopulatorsFactoryBean{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("configMaster").append('=').append(JodaBeanUtils.toString(getConfigMaster())).append(',').append(' ');
    buf.append("yieldCurve").append('=').append(JodaBeanUtils.toString(isYieldCurve())).append(',').append(' ');
    buf.append("currencyMatrix").append('=').append(JodaBeanUtils.toString(isCurrencyMatrix())).append(',').append(' ');
    buf.append("swaptionVolatilitySurface").append('=').append(JodaBeanUtils.toString(isSwaptionVolatilitySurface())).append(',').append(' ');
    buf.append("irFutureOptionSurface").append('=').append(JodaBeanUtils.toString(isIrFutureOptionSurface())).append(',').append(' ');
    buf.append("fxOptionVolatilitySurface").append('=').append(JodaBeanUtils.toString(isFxOptionVolatilitySurface())).append(',').append(' ');
    buf.append("equityOptionSurface").append('=').append(JodaBeanUtils.toString(isEquityOptionSurface())).append(',').append(' ');
    buf.append("volatilityCube").append('=').append(JodaBeanUtils.toString(isVolatilityCube())).append(',').append(' ');
    buf.append("fxForwardCurve").append('=').append(JodaBeanUtils.toString(isFxForwardCurve())).append(',').append(' ');
    buf.append("curveCalculationConfiguration").append('=').append(JodaBeanUtils.toString(isCurveCalculationConfiguration())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ConfigMasterPopulatorsFactoryBean}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code configMaster} property.
     */
    private final MetaProperty<ConfigMaster> _configMaster = DirectMetaProperty.ofReadWrite(
        this, "configMaster", ConfigMasterPopulatorsFactoryBean.class, ConfigMaster.class);
    /**
     * The meta-property for the {@code yieldCurve} property.
     */
    private final MetaProperty<Boolean> _yieldCurve = DirectMetaProperty.ofReadWrite(
        this, "yieldCurve", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code currencyMatrix} property.
     */
    private final MetaProperty<Boolean> _currencyMatrix = DirectMetaProperty.ofReadWrite(
        this, "currencyMatrix", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code swaptionVolatilitySurface} property.
     */
    private final MetaProperty<Boolean> _swaptionVolatilitySurface = DirectMetaProperty.ofReadWrite(
        this, "swaptionVolatilitySurface", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code irFutureOptionSurface} property.
     */
    private final MetaProperty<Boolean> _irFutureOptionSurface = DirectMetaProperty.ofReadWrite(
        this, "irFutureOptionSurface", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code fxOptionVolatilitySurface} property.
     */
    private final MetaProperty<Boolean> _fxOptionVolatilitySurface = DirectMetaProperty.ofReadWrite(
        this, "fxOptionVolatilitySurface", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code equityOptionSurface} property.
     */
    private final MetaProperty<Boolean> _equityOptionSurface = DirectMetaProperty.ofReadWrite(
        this, "equityOptionSurface", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code volatilityCube} property.
     */
    private final MetaProperty<Boolean> _volatilityCube = DirectMetaProperty.ofReadWrite(
        this, "volatilityCube", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code fxForwardCurve} property.
     */
    private final MetaProperty<Boolean> _fxForwardCurve = DirectMetaProperty.ofReadWrite(
        this, "fxForwardCurve", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code curveCalculationConfiguration} property.
     */
    private final MetaProperty<Boolean> _curveCalculationConfiguration = DirectMetaProperty.ofReadWrite(
        this, "curveCalculationConfiguration", ConfigMasterPopulatorsFactoryBean.class, Boolean.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "configMaster",
        "yieldCurve",
        "currencyMatrix",
        "swaptionVolatilitySurface",
        "irFutureOptionSurface",
        "fxOptionVolatilitySurface",
        "equityOptionSurface",
        "volatilityCube",
        "fxForwardCurve",
        "curveCalculationConfiguration");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 10395716:  // configMaster
          return _configMaster;
        case 1112236386:  // yieldCurve
          return _yieldCurve;
        case -506174670:  // currencyMatrix
          return _currencyMatrix;
        case -1209267103:  // swaptionVolatilitySurface
          return _swaptionVolatilitySurface;
        case -1409170036:  // irFutureOptionSurface
          return _irFutureOptionSurface;
        case -973280351:  // fxOptionVolatilitySurface
          return _fxOptionVolatilitySurface;
        case 1198258099:  // equityOptionSurface
          return _equityOptionSurface;
        case 69583354:  // volatilityCube
          return _volatilityCube;
        case -1016191204:  // fxForwardCurve
          return _fxForwardCurve;
        case 364174524:  // curveCalculationConfiguration
          return _curveCalculationConfiguration;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ConfigMasterPopulatorsFactoryBean> builder() {
      return new DirectBeanBuilder<ConfigMasterPopulatorsFactoryBean>(new ConfigMasterPopulatorsFactoryBean());
    }

    @Override
    public Class<? extends ConfigMasterPopulatorsFactoryBean> beanType() {
      return ConfigMasterPopulatorsFactoryBean.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code configMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigMaster> configMaster() {
      return _configMaster;
    }

    /**
     * The meta-property for the {@code yieldCurve} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> yieldCurve() {
      return _yieldCurve;
    }

    /**
     * The meta-property for the {@code currencyMatrix} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> currencyMatrix() {
      return _currencyMatrix;
    }

    /**
     * The meta-property for the {@code swaptionVolatilitySurface} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> swaptionVolatilitySurface() {
      return _swaptionVolatilitySurface;
    }

    /**
     * The meta-property for the {@code irFutureOptionSurface} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> irFutureOptionSurface() {
      return _irFutureOptionSurface;
    }

    /**
     * The meta-property for the {@code fxOptionVolatilitySurface} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> fxOptionVolatilitySurface() {
      return _fxOptionVolatilitySurface;
    }

    /**
     * The meta-property for the {@code equityOptionSurface} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> equityOptionSurface() {
      return _equityOptionSurface;
    }

    /**
     * The meta-property for the {@code volatilityCube} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> volatilityCube() {
      return _volatilityCube;
    }

    /**
     * The meta-property for the {@code fxForwardCurve} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> fxForwardCurve() {
      return _fxForwardCurve;
    }

    /**
     * The meta-property for the {@code curveCalculationConfiguration} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> curveCalculationConfiguration() {
      return _curveCalculationConfiguration;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 10395716:  // configMaster
          return ((ConfigMasterPopulatorsFactoryBean) bean).getConfigMaster();
        case 1112236386:  // yieldCurve
          return ((ConfigMasterPopulatorsFactoryBean) bean).isYieldCurve();
        case -506174670:  // currencyMatrix
          return ((ConfigMasterPopulatorsFactoryBean) bean).isCurrencyMatrix();
        case -1209267103:  // swaptionVolatilitySurface
          return ((ConfigMasterPopulatorsFactoryBean) bean).isSwaptionVolatilitySurface();
        case -1409170036:  // irFutureOptionSurface
          return ((ConfigMasterPopulatorsFactoryBean) bean).isIrFutureOptionSurface();
        case -973280351:  // fxOptionVolatilitySurface
          return ((ConfigMasterPopulatorsFactoryBean) bean).isFxOptionVolatilitySurface();
        case 1198258099:  // equityOptionSurface
          return ((ConfigMasterPopulatorsFactoryBean) bean).isEquityOptionSurface();
        case 69583354:  // volatilityCube
          return ((ConfigMasterPopulatorsFactoryBean) bean).isVolatilityCube();
        case -1016191204:  // fxForwardCurve
          return ((ConfigMasterPopulatorsFactoryBean) bean).isFxForwardCurve();
        case 364174524:  // curveCalculationConfiguration
          return ((ConfigMasterPopulatorsFactoryBean) bean).isCurveCalculationConfiguration();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 10395716:  // configMaster
          ((ConfigMasterPopulatorsFactoryBean) bean).setConfigMaster((ConfigMaster) newValue);
          return;
        case 1112236386:  // yieldCurve
          ((ConfigMasterPopulatorsFactoryBean) bean).setYieldCurve((Boolean) newValue);
          return;
        case -506174670:  // currencyMatrix
          ((ConfigMasterPopulatorsFactoryBean) bean).setCurrencyMatrix((Boolean) newValue);
          return;
        case -1209267103:  // swaptionVolatilitySurface
          ((ConfigMasterPopulatorsFactoryBean) bean).setSwaptionVolatilitySurface((Boolean) newValue);
          return;
        case -1409170036:  // irFutureOptionSurface
          ((ConfigMasterPopulatorsFactoryBean) bean).setIrFutureOptionSurface((Boolean) newValue);
          return;
        case -973280351:  // fxOptionVolatilitySurface
          ((ConfigMasterPopulatorsFactoryBean) bean).setFxOptionVolatilitySurface((Boolean) newValue);
          return;
        case 1198258099:  // equityOptionSurface
          ((ConfigMasterPopulatorsFactoryBean) bean).setEquityOptionSurface((Boolean) newValue);
          return;
        case 69583354:  // volatilityCube
          ((ConfigMasterPopulatorsFactoryBean) bean).setVolatilityCube((Boolean) newValue);
          return;
        case -1016191204:  // fxForwardCurve
          ((ConfigMasterPopulatorsFactoryBean) bean).setFxForwardCurve((Boolean) newValue);
          return;
        case 364174524:  // curveCalculationConfiguration
          ((ConfigMasterPopulatorsFactoryBean) bean).setCurveCalculationConfiguration((Boolean) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
