/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import static com.opengamma.sesame.TimeSeriesReturnConverterFactory.ConversionType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.analytics.financial.timeseries.util.TimeSeriesRelativeWeightedDifferenceOperator;
import com.opengamma.analytics.financial.timeseries.util.TimeSeriesWeightedVolatilityOperator;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.ArgumentChecker;

/**
 * Produces a volatility weighted return series from a spot rate series
 * using a fixed lambda value.
 */
@BeanDefinition
public final class VolatilityWeightedReturnConverter implements TimeSeriesReturnConverter, ImmutableBean {

  private static final TimeSeriesRelativeWeightedDifferenceOperator RELATIVE_WEIGHTED_DIFFERENCE =
      new TimeSeriesRelativeWeightedDifferenceOperator();

  @PropertyDefinition(validate = "notNull")
  private final ConversionType _conversionType;

  @PropertyDefinition
  private final double _lambda;

  private final TimeSeriesWeightedVolatilityOperator _weightedVolatilityOperator;

  /**
   * Constructor with the desired difference operator.
   *
   * @param conversionType  the type of conversion to be done, not null
   * @param lambda  the lambda value to be used for the weighting, not null
   */
  @ImmutableConstructor
  public VolatilityWeightedReturnConverter(ConversionType conversionType, double lambda) {
    _conversionType = ArgumentChecker.notNull(conversionType, "conversionType");
    _lambda = lambda;
    _weightedVolatilityOperator = conversionType == ConversionType.ABSOLUTE ?
        TimeSeriesWeightedVolatilityOperator.absolute(_lambda) :
        TimeSeriesWeightedVolatilityOperator.relative(_lambda);
  }

  @Override
  public LocalDateDoubleTimeSeries convert(LocalDateDoubleTimeSeries spotSeries) {

    LocalDateDoubleTimeSeries weightedVolSeries =
        (LocalDateDoubleTimeSeries) _weightedVolatilityOperator.evaluate(spotSeries);
    return (LocalDateDoubleTimeSeries) RELATIVE_WEIGHTED_DIFFERENCE.evaluate(spotSeries, weightedVolSeries);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code VolatilityWeightedReturnConverter}.
   * @return the meta-bean, not null
   */
  public static VolatilityWeightedReturnConverter.Meta meta() {
    return VolatilityWeightedReturnConverter.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(VolatilityWeightedReturnConverter.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static VolatilityWeightedReturnConverter.Builder builder() {
    return new VolatilityWeightedReturnConverter.Builder();
  }

  @Override
  public VolatilityWeightedReturnConverter.Meta metaBean() {
    return VolatilityWeightedReturnConverter.Meta.INSTANCE;
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
   * Gets the conversionType.
   * @return the value of the property, not null
   */
  public ConversionType getConversionType() {
    return _conversionType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the lambda.
   * @return the value of the property
   */
  public double getLambda() {
    return _lambda;
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
      VolatilityWeightedReturnConverter other = (VolatilityWeightedReturnConverter) obj;
      return JodaBeanUtils.equal(getConversionType(), other.getConversionType()) &&
          JodaBeanUtils.equal(getLambda(), other.getLambda());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getConversionType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getLambda());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("VolatilityWeightedReturnConverter{");
    buf.append("conversionType").append('=').append(getConversionType()).append(',').append(' ');
    buf.append("lambda").append('=').append(JodaBeanUtils.toString(getLambda()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code VolatilityWeightedReturnConverter}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code conversionType} property.
     */
    private final MetaProperty<ConversionType> _conversionType = DirectMetaProperty.ofImmutable(
        this, "conversionType", VolatilityWeightedReturnConverter.class, ConversionType.class);
    /**
     * The meta-property for the {@code lambda} property.
     */
    private final MetaProperty<Double> _lambda = DirectMetaProperty.ofImmutable(
        this, "lambda", VolatilityWeightedReturnConverter.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "conversionType",
        "lambda");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return _conversionType;
        case -1110092857:  // lambda
          return _lambda;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public VolatilityWeightedReturnConverter.Builder builder() {
      return new VolatilityWeightedReturnConverter.Builder();
    }

    @Override
    public Class<? extends VolatilityWeightedReturnConverter> beanType() {
      return VolatilityWeightedReturnConverter.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code conversionType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ConversionType> conversionType() {
      return _conversionType;
    }

    /**
     * The meta-property for the {@code lambda} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> lambda() {
      return _lambda;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return ((VolatilityWeightedReturnConverter) bean).getConversionType();
        case -1110092857:  // lambda
          return ((VolatilityWeightedReturnConverter) bean).getLambda();
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
   * The bean-builder for {@code VolatilityWeightedReturnConverter}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<VolatilityWeightedReturnConverter> {

    private ConversionType _conversionType;
    private double _lambda;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(VolatilityWeightedReturnConverter beanToCopy) {
      this._conversionType = beanToCopy.getConversionType();
      this._lambda = beanToCopy.getLambda();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          return _conversionType;
        case -1110092857:  // lambda
          return _lambda;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 989646192:  // conversionType
          this._conversionType = (ConversionType) newValue;
          break;
        case -1110092857:  // lambda
          this._lambda = (Double) newValue;
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
    public VolatilityWeightedReturnConverter build() {
      return new VolatilityWeightedReturnConverter(
          _conversionType,
          _lambda);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code conversionType} property in the builder.
     * @param conversionType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder conversionType(ConversionType conversionType) {
      JodaBeanUtils.notNull(conversionType, "conversionType");
      this._conversionType = conversionType;
      return this;
    }

    /**
     * Sets the {@code lambda} property in the builder.
     * @param lambda  the new value
     * @return this, for chaining, not null
     */
    public Builder lambda(double lambda) {
      this._lambda = lambda;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("VolatilityWeightedReturnConverter.Builder{");
      buf.append("conversionType").append('=').append(JodaBeanUtils.toString(_conversionType)).append(',').append(' ');
      buf.append("lambda").append('=').append(JodaBeanUtils.toString(_lambda));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}