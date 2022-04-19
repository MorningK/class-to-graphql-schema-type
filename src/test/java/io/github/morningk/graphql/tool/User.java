package io.github.morningk.graphql.tool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class User {
  private Long id;
  private int primaryInt;
  private long primaryLong;
  private float primaryFloat;
  private Double doubleValue;
  private BigDecimal age;

  private char[] chars;
  private String name;

  private boolean primaryBoolean;
  private Boolean admin;

  private Status status;
  private Role role;
  private OffsetDateTime createdAt;
  private LocalDate birthday;

  private double[] doubleArray;
  private String[] array;
  private Role[] roleArray;
  private List<Role> roles;

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

  @Ignored
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @ScalarType("String")
  public LocalDate getBirthday() {
    return birthday;
  }

  @NonNull
  @ListType(elementType = Role.class, elementNonNull = true)
  public List<Role> getRoles() {
    return roles;
  }
}
