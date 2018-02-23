/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
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
import com.opengamma.component.ComponentServer;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.component.rest.RemoteComponentServer;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.core.change.JmsChangeManager;
import com.opengamma.util.ReflectionUtils;
import com.opengamma.util.jms.JmsConnector;

/**
 * Component factory for accessing remote masters from the local machine.
 */
@BeanDefinition
public class RemoteMastersComponentFactory extends AbstractComponentFactory {

  /**
   * The remote URI.
   */
  @PropertyDefinition(validate = "notNull")
  private URI _baseUri;
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The JMS connector.
   */
  @PropertyDefinition
  private JmsConnector _jmsConnector;

  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) {
    RemoteComponentServer remote = new RemoteComponentServer(_baseUri);
    ComponentServer server = remote.getComponentServer();
    for (ComponentInfo info : server.getComponentInfos()) {
      initComponent(repo, info);
    }
  }

  /**
   * Initialize the remote component.
   * 
   * @param repo  the local repository, not null
   * @param info  the remote information, not null
   */
  protected void initComponent(ComponentRepository repo, ComponentInfo info) {
    URI componentUri = info.getUri();
    
    if (info.getAttributes().containsKey(ComponentInfoAttributes.REMOTE_CLIENT_JAVA) && 
        info.getAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA).endsWith("Master")) {
      String remoteTypeStr = info.getAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA);
      Class<?> remoteType = ReflectionUtils.loadClass(remoteTypeStr);
      String jmsBrokerUri = info.getAttributes().get(ComponentInfoAttributes.JMS_BROKER_URI);
      String jmsTopic = info.getAttributes().get(ComponentInfoAttributes.JMS_CHANGE_MANAGER_TOPIC);
      Object target;
      if (_jmsConnector != null && jmsTopic != null && ObjectUtils.equals(jmsBrokerUri, _jmsConnector.getClientBrokerUri().toString())) {
        // only sets up JMS if supplied connector matches that needed
        // this approach could be enhanced...
        JmsChangeManager changeManager = new JmsChangeManager(_jmsConnector, jmsTopic);
        repo.registerLifecycle(changeManager);
        Constructor<?> con = ReflectionUtils.findConstructor(remoteType, URI.class, ChangeManager.class);
        target = ReflectionUtils.newInstance(con, componentUri, changeManager);
      } else {
        // do not use JMS
        Constructor<?> con = ReflectionUtils.findConstructor(remoteType, URI.class);
        target = ReflectionUtils.newInstance(con, componentUri);
      }
      repo.registerComponent(info, target);
      if (isPublishRest()) {
        repo.getRestComponents().republish(info);
      }
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RemoteMastersComponentFactory}.
   * @return the meta-bean, not null
   */
  public static RemoteMastersComponentFactory.Meta meta() {
    return RemoteMastersComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RemoteMastersComponentFactory.Meta.INSTANCE);
  }

  @Override
  public RemoteMastersComponentFactory.Meta metaBean() {
    return RemoteMastersComponentFactory.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the remote URI.
   * @return the value of the property, not null
   */
  public URI getBaseUri() {
    return _baseUri;
  }

  /**
   * Sets the remote URI.
   * @param baseUri  the new value of the property, not null
   */
  public void setBaseUri(URI baseUri) {
    JodaBeanUtils.notNull(baseUri, "baseUri");
    this._baseUri = baseUri;
  }

  /**
   * Gets the the {@code baseUri} property.
   * @return the property, not null
   */
  public final Property<URI> baseUri() {
    return metaBean().baseUri().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JMS connector.
   * @return the value of the property
   */
  public JmsConnector getJmsConnector() {
    return _jmsConnector;
  }

  /**
   * Sets the JMS connector.
   * @param jmsConnector  the new value of the property
   */
  public void setJmsConnector(JmsConnector jmsConnector) {
    this._jmsConnector = jmsConnector;
  }

  /**
   * Gets the the {@code jmsConnector} property.
   * @return the property, not null
   */
  public final Property<JmsConnector> jmsConnector() {
    return metaBean().jmsConnector().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public RemoteMastersComponentFactory clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RemoteMastersComponentFactory other = (RemoteMastersComponentFactory) obj;
      return JodaBeanUtils.equal(getBaseUri(), other.getBaseUri()) &&
          (isPublishRest() == other.isPublishRest()) &&
          JodaBeanUtils.equal(getJmsConnector(), other.getJmsConnector()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getBaseUri());
    hash = hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash = hash * 31 + JodaBeanUtils.hashCode(getJmsConnector());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("RemoteMastersComponentFactory{");
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
    buf.append("baseUri").append('=').append(JodaBeanUtils.toString(getBaseUri())).append(',').append(' ');
    buf.append("publishRest").append('=').append(JodaBeanUtils.toString(isPublishRest())).append(',').append(' ');
    buf.append("jmsConnector").append('=').append(JodaBeanUtils.toString(getJmsConnector())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RemoteMastersComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code baseUri} property.
     */
    private final MetaProperty<URI> _baseUri = DirectMetaProperty.ofReadWrite(
        this, "baseUri", RemoteMastersComponentFactory.class, URI.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", RemoteMastersComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code jmsConnector} property.
     */
    private final MetaProperty<JmsConnector> _jmsConnector = DirectMetaProperty.ofReadWrite(
        this, "jmsConnector", RemoteMastersComponentFactory.class, JmsConnector.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "baseUri",
        "publishRest",
        "jmsConnector");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -332625701:  // baseUri
          return _baseUri;
        case -614707837:  // publishRest
          return _publishRest;
        case -1495762275:  // jmsConnector
          return _jmsConnector;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends RemoteMastersComponentFactory> builder() {
      return new DirectBeanBuilder<RemoteMastersComponentFactory>(new RemoteMastersComponentFactory());
    }

    @Override
    public Class<? extends RemoteMastersComponentFactory> beanType() {
      return RemoteMastersComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code baseUri} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<URI> baseUri() {
      return _baseUri;
    }

    /**
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code jmsConnector} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<JmsConnector> jmsConnector() {
      return _jmsConnector;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -332625701:  // baseUri
          return ((RemoteMastersComponentFactory) bean).getBaseUri();
        case -614707837:  // publishRest
          return ((RemoteMastersComponentFactory) bean).isPublishRest();
        case -1495762275:  // jmsConnector
          return ((RemoteMastersComponentFactory) bean).getJmsConnector();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -332625701:  // baseUri
          ((RemoteMastersComponentFactory) bean).setBaseUri((URI) newValue);
          return;
        case -614707837:  // publishRest
          ((RemoteMastersComponentFactory) bean).setPublishRest((Boolean) newValue);
          return;
        case -1495762275:  // jmsConnector
          ((RemoteMastersComponentFactory) bean).setJmsConnector((JmsConnector) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((RemoteMastersComponentFactory) bean)._baseUri, "baseUri");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
