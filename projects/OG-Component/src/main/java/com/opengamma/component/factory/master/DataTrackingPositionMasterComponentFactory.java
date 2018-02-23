/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.LinkedHashMap;
import java.util.Map;

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

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.master.position.PositionMaster;
import com.opengamma.master.position.impl.DataPositionMasterResource;
import com.opengamma.master.position.impl.DataTrackingPositionMaster;

/**
 * Component factory for {@link DataTrackingPositionMaster}.
 */
@BeanDefinition
public class DataTrackingPositionMasterComponentFactory extends AbstractComponentFactory {

  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  
  @PropertyDefinition(validate = "notNull")
  private PositionMaster _trackedMaster;
  
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    
    ComponentInfo componentInfo = new ComponentInfo(PositionMaster.class, _classifier);
    
    DataTrackingPositionMaster dataTrackingPositionMaster = new DataTrackingPositionMaster(_trackedMaster);
    
    repo.registerComponent(componentInfo, dataTrackingPositionMaster);
    
    repo.getRestComponents().publish(componentInfo, new DataPositionMasterResource(_trackedMaster));
    
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DataTrackingPositionMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static DataTrackingPositionMasterComponentFactory.Meta meta() {
    return DataTrackingPositionMasterComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DataTrackingPositionMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public DataTrackingPositionMasterComponentFactory.Meta metaBean() {
    return DataTrackingPositionMasterComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier.
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
   * Gets the trackedMaster.
   * @return the value of the property, not null
   */
  public PositionMaster getTrackedMaster() {
    return _trackedMaster;
  }

  /**
   * Sets the trackedMaster.
   * @param trackedMaster  the new value of the property, not null
   */
  public void setTrackedMaster(PositionMaster trackedMaster) {
    JodaBeanUtils.notNull(trackedMaster, "trackedMaster");
    this._trackedMaster = trackedMaster;
  }

  /**
   * Gets the the {@code trackedMaster} property.
   * @return the property, not null
   */
  public final Property<PositionMaster> trackedMaster() {
    return metaBean().trackedMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public DataTrackingPositionMasterComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DataTrackingPositionMasterComponentFactory other = (DataTrackingPositionMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getTrackedMaster(), other.getTrackedMaster()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTrackedMaster());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("DataTrackingPositionMasterComponentFactory{");
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
    buf.append("trackedMaster").append('=').append(JodaBeanUtils.toString(getTrackedMaster())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DataTrackingPositionMasterComponentFactory}.
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
        this, "classifier", DataTrackingPositionMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code trackedMaster} property.
     */
    private final MetaProperty<PositionMaster> _trackedMaster = DirectMetaProperty.ofReadWrite(
        this, "trackedMaster", DataTrackingPositionMasterComponentFactory.class, PositionMaster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "trackedMaster");

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
        case -1965332948:  // trackedMaster
          return _trackedMaster;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends DataTrackingPositionMasterComponentFactory> builder() {
      return new DirectBeanBuilder<DataTrackingPositionMasterComponentFactory>(new DataTrackingPositionMasterComponentFactory());
    }

    @Override
    public Class<? extends DataTrackingPositionMasterComponentFactory> beanType() {
      return DataTrackingPositionMasterComponentFactory.class;
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
     * The meta-property for the {@code trackedMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PositionMaster> trackedMaster() {
      return _trackedMaster;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return ((DataTrackingPositionMasterComponentFactory) bean).getClassifier();
        case -1965332948:  // trackedMaster
          return ((DataTrackingPositionMasterComponentFactory) bean).getTrackedMaster();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          ((DataTrackingPositionMasterComponentFactory) bean).setClassifier((String) newValue);
          return;
        case -1965332948:  // trackedMaster
          ((DataTrackingPositionMasterComponentFactory) bean).setTrackedMaster((PositionMaster) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((DataTrackingPositionMasterComponentFactory) bean)._classifier, "classifier");
      JodaBeanUtils.notNull(((DataTrackingPositionMasterComponentFactory) bean)._trackedMaster, "trackedMaster");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
