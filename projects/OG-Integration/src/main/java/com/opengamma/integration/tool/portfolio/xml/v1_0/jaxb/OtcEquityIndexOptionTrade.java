/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.portfolio.xml.v1_0.jaxb;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
import org.threeten.bp.LocalDate;

import com.opengamma.financial.security.option.OptionType;
import com.opengamma.integration.tool.portfolio.xml.v1_0.conversion.OtcEquityIndexOptionTradeSecurityExtractor;
import com.opengamma.integration.tool.portfolio.xml.v1_0.conversion.TradeSecurityExtractor;
import com.opengamma.util.money.Currency;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@BeanDefinition
public class OtcEquityIndexOptionTrade extends Trade {

  @XmlElement(name = "optionType", required = true)
  @PropertyDefinition
  private OptionType _optionType;

  @XmlElement(name = "buySell", required = true)
  @PropertyDefinition
  private BuySell _buySell;

  @XmlElement(name = "underlyingId", required = true)
  @PropertyDefinition
  private IdWrapper _underlyingId;

  @XmlElement(name = "notional", required = true)
  @PropertyDefinition
  private BigDecimal notional;

  @XmlElement(name = "notionalCurrency", required = true)
  @PropertyDefinition
  private Currency notionalCurrency;

  @XmlElement(name = "strike", required = true)
  @PropertyDefinition
  private BigDecimal _strike;

  @XmlElement(name = "exerciseType", required = true)
  @PropertyDefinition
  private ExerciseType _exerciseType;

  @XmlElement(name = "expiryDate", required = true)
  @PropertyDefinition
  private LocalDate expiryDate;

  @XmlElementWrapper(name = "expiryCalendars")
  @XmlElement(name = "calendar")
  @PropertyDefinition
  private Set<Calendar> _expiryCalendars;

  @XmlElementWrapper(name = "settlementCalendars")
  @XmlElement(name = "calendar")
  @PropertyDefinition
  private Set<Calendar> _settlementCalendars;

  @Override
  public BigDecimal getQuantity() {
    return getNotional();
  }

  @Override
  public boolean canBePositionAggregated() {
    return false;
  }

  @Override
  public TradeSecurityExtractor getSecurityExtractor() {
    return new OtcEquityIndexOptionTradeSecurityExtractor(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code OtcEquityIndexOptionTrade}.
   * @return the meta-bean, not null
   */
  public static OtcEquityIndexOptionTrade.Meta meta() {
    return OtcEquityIndexOptionTrade.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(OtcEquityIndexOptionTrade.Meta.INSTANCE);
  }

  @Override
  public OtcEquityIndexOptionTrade.Meta metaBean() {
    return OtcEquityIndexOptionTrade.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the optionType.
   * @return the value of the property
   */
  public OptionType getOptionType() {
    return _optionType;
  }

  /**
   * Sets the optionType.
   * @param optionType  the new value of the property
   */
  public void setOptionType(OptionType optionType) {
    this._optionType = optionType;
  }

  /**
   * Gets the the {@code optionType} property.
   * @return the property, not null
   */
  public final Property<OptionType> optionType() {
    return metaBean().optionType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the buySell.
   * @return the value of the property
   */
  public BuySell getBuySell() {
    return _buySell;
  }

  /**
   * Sets the buySell.
   * @param buySell  the new value of the property
   */
  public void setBuySell(BuySell buySell) {
    this._buySell = buySell;
  }

  /**
   * Gets the the {@code buySell} property.
   * @return the property, not null
   */
  public final Property<BuySell> buySell() {
    return metaBean().buySell().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlyingId.
   * @return the value of the property
   */
  public IdWrapper getUnderlyingId() {
    return _underlyingId;
  }

  /**
   * Sets the underlyingId.
   * @param underlyingId  the new value of the property
   */
  public void setUnderlyingId(IdWrapper underlyingId) {
    this._underlyingId = underlyingId;
  }

  /**
   * Gets the the {@code underlyingId} property.
   * @return the property, not null
   */
  public final Property<IdWrapper> underlyingId() {
    return metaBean().underlyingId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notional.
   * @return the value of the property
   */
  public BigDecimal getNotional() {
    return notional;
  }

  /**
   * Sets the notional.
   * @param notional  the new value of the property
   */
  public void setNotional(BigDecimal notional) {
    this.notional = notional;
  }

  /**
   * Gets the the {@code notional} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> notional() {
    return metaBean().notional().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notionalCurrency.
   * @return the value of the property
   */
  public Currency getNotionalCurrency() {
    return notionalCurrency;
  }

  /**
   * Sets the notionalCurrency.
   * @param notionalCurrency  the new value of the property
   */
  public void setNotionalCurrency(Currency notionalCurrency) {
    this.notionalCurrency = notionalCurrency;
  }

  /**
   * Gets the the {@code notionalCurrency} property.
   * @return the property, not null
   */
  public final Property<Currency> notionalCurrency() {
    return metaBean().notionalCurrency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the strike.
   * @return the value of the property
   */
  public BigDecimal getStrike() {
    return _strike;
  }

  /**
   * Sets the strike.
   * @param strike  the new value of the property
   */
  public void setStrike(BigDecimal strike) {
    this._strike = strike;
  }

  /**
   * Gets the the {@code strike} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> strike() {
    return metaBean().strike().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the exerciseType.
   * @return the value of the property
   */
  public ExerciseType getExerciseType() {
    return _exerciseType;
  }

  /**
   * Sets the exerciseType.
   * @param exerciseType  the new value of the property
   */
  public void setExerciseType(ExerciseType exerciseType) {
    this._exerciseType = exerciseType;
  }

  /**
   * Gets the the {@code exerciseType} property.
   * @return the property, not null
   */
  public final Property<ExerciseType> exerciseType() {
    return metaBean().exerciseType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiryDate.
   * @return the value of the property
   */
  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  /**
   * Sets the expiryDate.
   * @param expiryDate  the new value of the property
   */
  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  /**
   * Gets the the {@code expiryDate} property.
   * @return the property, not null
   */
  public final Property<LocalDate> expiryDate() {
    return metaBean().expiryDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiryCalendars.
   * @return the value of the property
   */
  public Set<Calendar> getExpiryCalendars() {
    return _expiryCalendars;
  }

  /**
   * Sets the expiryCalendars.
   * @param expiryCalendars  the new value of the property
   */
  public void setExpiryCalendars(Set<Calendar> expiryCalendars) {
    this._expiryCalendars = expiryCalendars;
  }

  /**
   * Gets the the {@code expiryCalendars} property.
   * @return the property, not null
   */
  public final Property<Set<Calendar>> expiryCalendars() {
    return metaBean().expiryCalendars().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the settlementCalendars.
   * @return the value of the property
   */
  public Set<Calendar> getSettlementCalendars() {
    return _settlementCalendars;
  }

  /**
   * Sets the settlementCalendars.
   * @param settlementCalendars  the new value of the property
   */
  public void setSettlementCalendars(Set<Calendar> settlementCalendars) {
    this._settlementCalendars = settlementCalendars;
  }

  /**
   * Gets the the {@code settlementCalendars} property.
   * @return the property, not null
   */
  public final Property<Set<Calendar>> settlementCalendars() {
    return metaBean().settlementCalendars().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public OtcEquityIndexOptionTrade clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      OtcEquityIndexOptionTrade other = (OtcEquityIndexOptionTrade) obj;
      return JodaBeanUtils.equal(getOptionType(), other.getOptionType()) &&
          JodaBeanUtils.equal(getBuySell(), other.getBuySell()) &&
          JodaBeanUtils.equal(getUnderlyingId(), other.getUnderlyingId()) &&
          JodaBeanUtils.equal(getNotional(), other.getNotional()) &&
          JodaBeanUtils.equal(getNotionalCurrency(), other.getNotionalCurrency()) &&
          JodaBeanUtils.equal(getStrike(), other.getStrike()) &&
          JodaBeanUtils.equal(getExerciseType(), other.getExerciseType()) &&
          JodaBeanUtils.equal(getExpiryDate(), other.getExpiryDate()) &&
          JodaBeanUtils.equal(getExpiryCalendars(), other.getExpiryCalendars()) &&
          JodaBeanUtils.equal(getSettlementCalendars(), other.getSettlementCalendars()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getOptionType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getBuySell());
    hash = hash * 31 + JodaBeanUtils.hashCode(getUnderlyingId());
    hash = hash * 31 + JodaBeanUtils.hashCode(getNotional());
    hash = hash * 31 + JodaBeanUtils.hashCode(getNotionalCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(getStrike());
    hash = hash * 31 + JodaBeanUtils.hashCode(getExerciseType());
    hash = hash * 31 + JodaBeanUtils.hashCode(getExpiryDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getExpiryCalendars());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSettlementCalendars());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(352);
    buf.append("OtcEquityIndexOptionTrade{");
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
    buf.append("optionType").append('=').append(JodaBeanUtils.toString(getOptionType())).append(',').append(' ');
    buf.append("buySell").append('=').append(JodaBeanUtils.toString(getBuySell())).append(',').append(' ');
    buf.append("underlyingId").append('=').append(JodaBeanUtils.toString(getUnderlyingId())).append(',').append(' ');
    buf.append("notional").append('=').append(JodaBeanUtils.toString(getNotional())).append(',').append(' ');
    buf.append("notionalCurrency").append('=').append(JodaBeanUtils.toString(getNotionalCurrency())).append(',').append(' ');
    buf.append("strike").append('=').append(JodaBeanUtils.toString(getStrike())).append(',').append(' ');
    buf.append("exerciseType").append('=').append(JodaBeanUtils.toString(getExerciseType())).append(',').append(' ');
    buf.append("expiryDate").append('=').append(JodaBeanUtils.toString(getExpiryDate())).append(',').append(' ');
    buf.append("expiryCalendars").append('=').append(JodaBeanUtils.toString(getExpiryCalendars())).append(',').append(' ');
    buf.append("settlementCalendars").append('=').append(JodaBeanUtils.toString(getSettlementCalendars())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code OtcEquityIndexOptionTrade}.
   */
  public static class Meta extends Trade.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code optionType} property.
     */
    private final MetaProperty<OptionType> _optionType = DirectMetaProperty.ofReadWrite(
        this, "optionType", OtcEquityIndexOptionTrade.class, OptionType.class);
    /**
     * The meta-property for the {@code buySell} property.
     */
    private final MetaProperty<BuySell> _buySell = DirectMetaProperty.ofReadWrite(
        this, "buySell", OtcEquityIndexOptionTrade.class, BuySell.class);
    /**
     * The meta-property for the {@code underlyingId} property.
     */
    private final MetaProperty<IdWrapper> _underlyingId = DirectMetaProperty.ofReadWrite(
        this, "underlyingId", OtcEquityIndexOptionTrade.class, IdWrapper.class);
    /**
     * The meta-property for the {@code notional} property.
     */
    private final MetaProperty<BigDecimal> _notional = DirectMetaProperty.ofReadWrite(
        this, "notional", OtcEquityIndexOptionTrade.class, BigDecimal.class);
    /**
     * The meta-property for the {@code notionalCurrency} property.
     */
    private final MetaProperty<Currency> _notionalCurrency = DirectMetaProperty.ofReadWrite(
        this, "notionalCurrency", OtcEquityIndexOptionTrade.class, Currency.class);
    /**
     * The meta-property for the {@code strike} property.
     */
    private final MetaProperty<BigDecimal> _strike = DirectMetaProperty.ofReadWrite(
        this, "strike", OtcEquityIndexOptionTrade.class, BigDecimal.class);
    /**
     * The meta-property for the {@code exerciseType} property.
     */
    private final MetaProperty<ExerciseType> _exerciseType = DirectMetaProperty.ofReadWrite(
        this, "exerciseType", OtcEquityIndexOptionTrade.class, ExerciseType.class);
    /**
     * The meta-property for the {@code expiryDate} property.
     */
    private final MetaProperty<LocalDate> _expiryDate = DirectMetaProperty.ofReadWrite(
        this, "expiryDate", OtcEquityIndexOptionTrade.class, LocalDate.class);
    /**
     * The meta-property for the {@code expiryCalendars} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<Calendar>> _expiryCalendars = DirectMetaProperty.ofReadWrite(
        this, "expiryCalendars", OtcEquityIndexOptionTrade.class, (Class) Set.class);
    /**
     * The meta-property for the {@code settlementCalendars} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<Calendar>> _settlementCalendars = DirectMetaProperty.ofReadWrite(
        this, "settlementCalendars", OtcEquityIndexOptionTrade.class, (Class) Set.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "optionType",
        "buySell",
        "underlyingId",
        "notional",
        "notionalCurrency",
        "strike",
        "exerciseType",
        "expiryDate",
        "expiryCalendars",
        "settlementCalendars");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1373587791:  // optionType
          return _optionType;
        case 244977400:  // buySell
          return _buySell;
        case -771625640:  // underlyingId
          return _underlyingId;
        case 1585636160:  // notional
          return _notional;
        case -1573783695:  // notionalCurrency
          return _notionalCurrency;
        case -891985998:  // strike
          return _strike;
        case -466331342:  // exerciseType
          return _exerciseType;
        case -816738431:  // expiryDate
          return _expiryDate;
        case -952649470:  // expiryCalendars
          return _expiryCalendars;
        case 697909708:  // settlementCalendars
          return _settlementCalendars;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends OtcEquityIndexOptionTrade> builder() {
      return new DirectBeanBuilder<OtcEquityIndexOptionTrade>(new OtcEquityIndexOptionTrade());
    }

    @Override
    public Class<? extends OtcEquityIndexOptionTrade> beanType() {
      return OtcEquityIndexOptionTrade.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code optionType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<OptionType> optionType() {
      return _optionType;
    }

    /**
     * The meta-property for the {@code buySell} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BuySell> buySell() {
      return _buySell;
    }

    /**
     * The meta-property for the {@code underlyingId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IdWrapper> underlyingId() {
      return _underlyingId;
    }

    /**
     * The meta-property for the {@code notional} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> notional() {
      return _notional;
    }

    /**
     * The meta-property for the {@code notionalCurrency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> notionalCurrency() {
      return _notionalCurrency;
    }

    /**
     * The meta-property for the {@code strike} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> strike() {
      return _strike;
    }

    /**
     * The meta-property for the {@code exerciseType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExerciseType> exerciseType() {
      return _exerciseType;
    }

    /**
     * The meta-property for the {@code expiryDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LocalDate> expiryDate() {
      return _expiryDate;
    }

    /**
     * The meta-property for the {@code expiryCalendars} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<Calendar>> expiryCalendars() {
      return _expiryCalendars;
    }

    /**
     * The meta-property for the {@code settlementCalendars} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<Calendar>> settlementCalendars() {
      return _settlementCalendars;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1373587791:  // optionType
          return ((OtcEquityIndexOptionTrade) bean).getOptionType();
        case 244977400:  // buySell
          return ((OtcEquityIndexOptionTrade) bean).getBuySell();
        case -771625640:  // underlyingId
          return ((OtcEquityIndexOptionTrade) bean).getUnderlyingId();
        case 1585636160:  // notional
          return ((OtcEquityIndexOptionTrade) bean).getNotional();
        case -1573783695:  // notionalCurrency
          return ((OtcEquityIndexOptionTrade) bean).getNotionalCurrency();
        case -891985998:  // strike
          return ((OtcEquityIndexOptionTrade) bean).getStrike();
        case -466331342:  // exerciseType
          return ((OtcEquityIndexOptionTrade) bean).getExerciseType();
        case -816738431:  // expiryDate
          return ((OtcEquityIndexOptionTrade) bean).getExpiryDate();
        case -952649470:  // expiryCalendars
          return ((OtcEquityIndexOptionTrade) bean).getExpiryCalendars();
        case 697909708:  // settlementCalendars
          return ((OtcEquityIndexOptionTrade) bean).getSettlementCalendars();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1373587791:  // optionType
          ((OtcEquityIndexOptionTrade) bean).setOptionType((OptionType) newValue);
          return;
        case 244977400:  // buySell
          ((OtcEquityIndexOptionTrade) bean).setBuySell((BuySell) newValue);
          return;
        case -771625640:  // underlyingId
          ((OtcEquityIndexOptionTrade) bean).setUnderlyingId((IdWrapper) newValue);
          return;
        case 1585636160:  // notional
          ((OtcEquityIndexOptionTrade) bean).setNotional((BigDecimal) newValue);
          return;
        case -1573783695:  // notionalCurrency
          ((OtcEquityIndexOptionTrade) bean).setNotionalCurrency((Currency) newValue);
          return;
        case -891985998:  // strike
          ((OtcEquityIndexOptionTrade) bean).setStrike((BigDecimal) newValue);
          return;
        case -466331342:  // exerciseType
          ((OtcEquityIndexOptionTrade) bean).setExerciseType((ExerciseType) newValue);
          return;
        case -816738431:  // expiryDate
          ((OtcEquityIndexOptionTrade) bean).setExpiryDate((LocalDate) newValue);
          return;
        case -952649470:  // expiryCalendars
          ((OtcEquityIndexOptionTrade) bean).setExpiryCalendars((Set<Calendar>) newValue);
          return;
        case 697909708:  // settlementCalendars
          ((OtcEquityIndexOptionTrade) bean).setSettlementCalendars((Set<Calendar>) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
