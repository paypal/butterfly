package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "application",
    "extensionName",
    "templateClassName"
})
public class TransformationRequestSimple {

  @NotNull
  @JsonProperty("userId")
  private String userId;

  @JsonProperty("application")
  @NotNull
  @Valid
  private Application application;

  @JsonProperty("extensionName")
  @NotNull
  private String extensionName;

  @NotNull
  @JsonProperty("templateClassName")
  private String templateClassName;

  /**
   * Id of the user who requested the transformation
   */
  @JsonProperty("userId")
  public String getUserId() {
    return this.userId;
  }

  @JsonProperty("userId")
  public void setUserId(String userId) {
    this.userId = userId;
  }

  @JsonProperty("application")
  public Application getApplication() {
    return this.application;
  }

  @JsonProperty("application")
  public void setApplication(Application application) {
    this.application = application;
  }

  /**
   * Butterfly extension name
   */
  @JsonProperty("extensionName")
  public String getExtensionName() {
    return this.extensionName;
  }

  @JsonProperty("extensionName")
  public void setExtensionName(String extensionName) {
    this.extensionName = extensionName;
  }

  /**
   * Butterfly transformation template class name
   */
  @JsonProperty("templateClassName")
  public String getTemplateClassName() {
    return this.templateClassName;
  }

  @JsonProperty("templateClassName")
  public void setTemplateClassName(String templateClassName) {
    this.templateClassName = templateClassName;
  }

}
