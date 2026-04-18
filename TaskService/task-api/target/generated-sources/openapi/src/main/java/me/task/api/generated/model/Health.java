package me.task.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Health
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-30T22:55:48.183322900+08:00[Asia/Shanghai]", comments = "Generator version: 7.21.0")
public class Health {

  private String status;

  private String version;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.OffsetDateTime checkedAt;

  public Health() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Health(String status, String version, java.time.OffsetDateTime checkedAt) {
    this.status = status;
    this.version = version;
    this.checkedAt = checkedAt;
  }

  public Health status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  public Health version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   */
  @NotNull 
  @Schema(name = "version", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }

  public Health checkedAt(java.time.OffsetDateTime checkedAt) {
    this.checkedAt = checkedAt;
    return this;
  }

  /**
   * Get checkedAt
   * @return checkedAt
   */
  @NotNull @Valid 
  @Schema(name = "checkedAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("checkedAt")
  public java.time.OffsetDateTime getCheckedAt() {
    return checkedAt;
  }

  @JsonProperty("checkedAt")
  public void setCheckedAt(java.time.OffsetDateTime checkedAt) {
    this.checkedAt = checkedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Health health = (Health) o;
    return Objects.equals(this.status, health.status) &&
        Objects.equals(this.version, health.version) &&
        Objects.equals(this.checkedAt, health.checkedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, version, checkedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Health {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    checkedAt: ").append(toIndentedString(checkedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

