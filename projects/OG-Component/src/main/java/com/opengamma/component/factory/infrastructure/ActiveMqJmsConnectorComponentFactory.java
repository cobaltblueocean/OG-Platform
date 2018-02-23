/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.infrastructure;


import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
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

import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractAliasedComponentFactory;
import com.opengamma.util.jms.JmsConnector;
import com.opengamma.util.jms.JmsConnectorFactoryBean;

/**
 * Component Factory for a shared JMS connector.
 * <p>
 * A client broker URI must be specified
 * <p>
 * If no ConnectionFactory is provided, it will default to pooled ActiveMQ implementation with some sensible defaults.
 * <p>
 * This class can be inherited from and protected methods overridden if necessary.
 */
@BeanDefinition
public class ActiveMqJmsConnectorComponentFactory extends AbstractAliasedComponentFactory {

  /**
   * The broker URI.
   */
  @PropertyDefinition(validate = "notNull")
  private String _clientBrokerUri;

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {
    JmsConnector connector = createJmsConnector(repo);
    registerComponentAndAliases(repo, JmsConnector.class, connector);
  }

  /**
   * Creates the JMS connector without registering it.
   * 
   * @param repo  the component repository, only used to register secondary items like lifecycle, not null
   * @return the JMS connector, not null
   */
  protected JmsConnector createJmsConnector(ComponentRepository repo) throws Exception {
    ConnectionFactory connectionFactory = createConnectionFactory(repo);
    JmsConnectorFactoryBean factoryBean = new JmsConnectorFactoryBean();
    factoryBean.setName("StandardJms");
    factoryBean.setConnectionFactory(connectionFactory);
    factoryBean.setClientBrokerUri(new URI(getClientBrokerUri()));
    factoryBean.afterPropertiesSet();
    return factoryBean.getObjectCreating();
  }

  /**
   * Creates the JMS connection factory without registering it.
   * 
   * @param repo  the component repository, only used to register secondary items like lifecycle, not null
   * @return the JMS connection factory, not null
   */
  protected ConnectionFactory createConnectionFactory(ComponentRepository repo) {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getClientBrokerUri());
    connectionFactory.setWatchTopicAdvisories(false);
    PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(connectionFactory);
    pooledConnectionFactory.setIdleTimeout(0);
    return pooledConnectionFactory;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ActiveMqJmsConnectorComponentFactory}.
   * @return the meta-bean, not null
   */
  public static ActiveMqJmsConnectorComponentFactory.Meta meta() {
    return ActiveMqJmsConnectorComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ActiveMqJmsConnectorComponentFactory.Meta.INSTANCE);
  }

  @Override
  public ActiveMqJmsConnectorComponentFactory.Meta metaBean() {
    return ActiveMqJmsConnectorComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the broker URI.
   * @return the value of the property, not null
   */
  public String getClientBrokerUri() {
    return _clientBrokerUri;
  }

  /**
   * Sets the broker URI.
   * @param clientBrokerUri  the new value of the property, not null
   */
  public void setClientBrokerUri(String clientBrokerUri) {
    JodaBeanUtils.notNull(clientBrokerUri, "clientBrokerUri");
    this._clientBrokerUri = clientBrokerUri;
  }

  /**
   * Gets the the {@code clientBrokerUri} property.
   * @return the property, not null
   */
  public final Property<String> clientBrokerUri() {
    return metaBean().clientBrokerUri().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public ActiveMqJmsConnectorComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ActiveMqJmsConnectorComponentFactory other = (ActiveMqJmsConnectorComponentFactory) obj;
      return JodaBeanUtils.equal(getClientBrokerUri(), other.getClientBrokerUri()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getClientBrokerUri());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("ActiveMqJmsConnectorComponentFactory{");
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
    buf.append("clientBrokerUri").append('=').append(JodaBeanUtils.toString(getClientBrokerUri())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ActiveMqJmsConnectorComponentFactory}.
   */
  public static class Meta extends AbstractAliasedComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code clientBrokerUri} property.
     */
    private final MetaProperty<String> _clientBrokerUri = DirectMetaProperty.ofReadWrite(
        this, "clientBrokerUri", ActiveMqJmsConnectorComponentFactory.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "clientBrokerUri");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1176216760:  // clientBrokerUri
          return _clientBrokerUri;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ActiveMqJmsConnectorComponentFactory> builder() {
      return new DirectBeanBuilder<ActiveMqJmsConnectorComponentFactory>(new ActiveMqJmsConnectorComponentFactory());
    }

    @Override
    public Class<? extends ActiveMqJmsConnectorComponentFactory> beanType() {
      return ActiveMqJmsConnectorComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code clientBrokerUri} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> clientBrokerUri() {
      return _clientBrokerUri;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1176216760:  // clientBrokerUri
          return ((ActiveMqJmsConnectorComponentFactory) bean).getClientBrokerUri();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1176216760:  // clientBrokerUri
          ((ActiveMqJmsConnectorComponentFactory) bean).setClientBrokerUri((String) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((ActiveMqJmsConnectorComponentFactory) bean)._clientBrokerUri, "clientBrokerUri");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}