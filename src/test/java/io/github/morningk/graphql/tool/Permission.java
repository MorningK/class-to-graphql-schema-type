package io.github.morningk.graphql.tool;

import lombok.Getter;

@Getter
@ScalarType("Permission")
public class Permission {
  private Integer id;
  private String name;
  private Role[] roles;
  private User[] users;
}
