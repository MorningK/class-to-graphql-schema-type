package io.github.morningk.graphql.tool;

import java.math.BigDecimal;
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
  private List<Role> roles;

  public enum Status {
    ENABLED,
    DISABLED,
  }

  @Id
  public Long getId() {
    return id;
  }
}
