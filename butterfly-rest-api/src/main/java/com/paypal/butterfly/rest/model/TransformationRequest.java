package com.paypal.butterfly.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Information necessary to request a transformation
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "transformationRequestSimple",
    "upgradeToVersion",
    "notify",
    "properties",
    "debug",
    "dryRun"
})
public class TransformationRequest {

  @NotNull
  @Valid
  @JsonProperty("transformationRequestSimple")
  private TransformationRequestSimple transformationRequestSimple;

  @JsonProperty("upgradeToVersion")
  private String upgradeToVersion;

  @JsonProperty("notify")
  @Size(
      min = 0
  )
  private List<String> notify;

  @Valid
  @JsonProperty("properties")
  private Map<String, String> properties;

  @JsonProperty("debug")
  private boolean debug;

  @JsonProperty("dryRun")
  private boolean dryRun;

  @JsonProperty("transformationRequestSimple")
  public TransformationRequestSimple getTransformationRequestSimple() {
    return this.transformationRequestSimple;
  }

  @JsonProperty("transformationRequestSimple")
  public void setTransformationRequestSimple(
      TransformationRequestSimple transformationRequestSimple) {
    this.transformationRequestSimple = transformationRequestSimple;
  }

  /**
   * Version application should be upgraded to (if transformation is not an upgrade, this parameter is ignored. If it is, but parameter is not set, application will be upgraded to the latest available version)
   */
  @JsonProperty("upgradeToVersion")
  public String getUpgradeToVersion() {
    return this.upgradeToVersion;
  }

  @JsonProperty("upgradeToVersion")
  public void setUpgradeToVersion(String upgradeToVersion) {
    this.upgradeToVersion = upgradeToVersion;
  }

  /**
   * Users to be notified of the transformation result
   */
  @JsonProperty("notify")
  public List<String> getNotify() {
    return this.notify;
  }

  @JsonProperty("notify")
  public void setNotify(List<String> notify) {
    this.notify = notify;
  }

  /**
   * Transformation template request specific properties
   */
  @JsonProperty("properties")
  public Map<String, String> getProperties() {
    return this.properties;
  }

  @JsonProperty("properties")
  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * Run the transformation in debug mode (false, if absent)
   */
  @JsonProperty("debug")
  public boolean getDebug() {
    return this.debug;
  }

  @JsonProperty("debug")
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * Does NOT result in a transformed application, but produces transformation logs for analysis and debug purposes (false, if absent)
   */
  @JsonProperty("dryRun")
  public boolean getDryRun() {
    return this.dryRun;
  }

  @JsonProperty("dryRun")
  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

}
