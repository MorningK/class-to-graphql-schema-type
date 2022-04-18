package io.github.morningk.graphql.tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class User {
  private Long id;
  private String name;
  private Boolean admin;
  private BigDecimal age;
  private Status status;
  private String[] array;
  private Role[] roleArray;
  private List<Role> roles;
  private OffsetDateTime createdAt;

  public enum Status {
    ENABLED,
    DISABLED,
  }

  @ScalarType("ID")
  @NonNull
  public Long getId() {
    return id;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @ScalarType("String")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @Ignored
  public List<Role> getRoles() {
    return roles;
  }
}
