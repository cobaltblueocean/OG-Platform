/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.user;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

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

import com.opengamma.master.user.ManageableRole;
import com.opengamma.master.user.RoleMaster;
import com.opengamma.master.user.UserMaster;
import com.opengamma.web.WebPerRequestData;

/**
 * Data class for web-based roles.
 */
@BeanDefinition
public class WebRoleData extends WebPerRequestData {

  /**
   * The user master.
   */
  @PropertyDefinition
  private UserMaster _userMaster;
  /**
   * The role name from the input URI.
   */
  @PropertyDefinition
  private String _uriRoleName;
  /**
   * The role.
   */
  @PropertyDefinition
  private ManageableRole _role;

  /**
   * Creates an instance.
   */
  public WebRoleData() {
  }

  /**
   * Creates an instance.
   * @param uriInfo  the URI information
   */
  public WebRoleData(final UriInfo uriInfo) {
    setUriInfo(uriInfo);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the role master.
   * 
   * @return the role master, may be null
   */
  public RoleMaster getRoleMaster() {
    return getUserMaster() != null ? getUserMaster().roleMaster() : null;
  }

  /**
   * Gets the best available role name.
   * @param overrideName  the override id, null derives the result from the data
   * @return the id, may be null
   */
  public String getBestRoleUriName(final String overrideName) {
    if (overrideName != null) {
      return overrideName;
    }
    return getRole() != null ? getRole().getRoleName() : getUriRoleName();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code WebRoleData}.
   * @return the meta-bean, not null
   */
  public static WebRoleData.Meta meta() {
    return WebRoleData.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(WebRoleData.Meta.INSTANCE);
  }

  @Override
  public WebRoleData.Meta metaBean() {
    return WebRoleData.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the user master.
   * @return the value of the property
   */
  public UserMaster getUserMaster() {
    return _userMaster;
  }

  /**
   * Sets the user master.
   * @param userMaster  the new value of the property
   */
  public void setUserMaster(UserMaster userMaster) {
    this._userMaster = userMaster;
  }

  /**
   * Gets the the {@code userMaster} property.
   * @return the property, not null
   */
  public final Property<UserMaster> userMaster() {
    return metaBean().userMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the role name from the input URI.
   * @return the value of the property
   */
  public String getUriRoleName() {
    return _uriRoleName;
  }

  /**
   * Sets the role name from the input URI.
   * @param uriRoleName  the new value of the property
   */
  public void setUriRoleName(String uriRoleName) {
    this._uriRoleName = uriRoleName;
  }

  /**
   * Gets the the {@code uriRoleName} property.
   * @return the property, not null
   */
  public final Property<String> uriRoleName() {
    return metaBean().uriRoleName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the role.
   * @return the value of the property
   */
  public ManageableRole getRole() {
    return _role;
  }

  /**
   * Sets the role.
   * @param role  the new value of the property
   */
  public void setRole(ManageableRole role) {
    this._role = role;
  }

  /**
   * Gets the the {@code role} property.
   * @return the property, not null
   */
  public final Property<ManageableRole> role() {
    return metaBean().role().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public WebRoleData clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      WebRoleData other = (WebRoleData) obj;
      return JodaBeanUtils.equal(getUserMaster(), other.getUserMaster()) &&
          JodaBeanUtils.equal(getUriRoleName(), other.getUriRoleName()) &&
          JodaBeanUtils.equal(getRole(), other.getRole()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = hash * 31 + JodaBeanUtils.hashCode(getUserMaster());
    hash = hash * 31 + JodaBeanUtils.hashCode(getUriRoleName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRole());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("WebRoleData{");
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
    buf.append("userMaster").append('=').append(JodaBeanUtils.toString(getUserMaster())).append(',').append(' ');
    buf.append("uriRoleName").append('=').append(JodaBeanUtils.toString(getUriRoleName())).append(',').append(' ');
    buf.append("role").append('=').append(JodaBeanUtils.toString(getRole())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code WebRoleData}.
   */
  public static class Meta extends WebPerRequestData.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code userMaster} property.
     */
    private final MetaProperty<UserMaster> _userMaster = DirectMetaProperty.ofReadWrite(
        this, "userMaster", WebRoleData.class, UserMaster.class);
    /**
     * The meta-property for the {@code uriRoleName} property.
     */
    private final MetaProperty<String> _uriRoleName = DirectMetaProperty.ofReadWrite(
        this, "uriRoleName", WebRoleData.class, String.class);
    /**
     * The meta-property for the {@code role} property.
     */
    private final MetaProperty<ManageableRole> _role = DirectMetaProperty.ofReadWrite(
        this, "role", WebRoleData.class, ManageableRole.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "userMaster",
        "uriRoleName",
        "role");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1402846733:  // userMaster
          return _userMaster;
        case -1723907667:  // uriRoleName
          return _uriRoleName;
        case 3506294:  // role
          return _role;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends WebRoleData> builder() {
      return new DirectBeanBuilder<WebRoleData>(new WebRoleData());
    }

    @Override
    public Class<? extends WebRoleData> beanType() {
      return WebRoleData.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code userMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UserMaster> userMaster() {
      return _userMaster;
    }

    /**
     * The meta-property for the {@code uriRoleName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriRoleName() {
      return _uriRoleName;
    }

    /**
     * The meta-property for the {@code role} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageableRole> role() {
      return _role;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1402846733:  // userMaster
          return ((WebRoleData) bean).getUserMaster();
        case -1723907667:  // uriRoleName
          return ((WebRoleData) bean).getUriRoleName();
        case 3506294:  // role
          return ((WebRoleData) bean).getRole();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1402846733:  // userMaster
          ((WebRoleData) bean).setUserMaster((UserMaster) newValue);
          return;
        case -1723907667:  // uriRoleName
          ((WebRoleData) bean).setUriRoleName((String) newValue);
          return;
        case 3506294:  // role
          ((WebRoleData) bean).setRole((ManageableRole) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
