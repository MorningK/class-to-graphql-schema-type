package io.github.morningk.graphql.tool;

import lombok.Getter;

@Getter
public class Role {
  private Integer id;
  private String name;
  private Permission[] permissions;
  private User[][] users;
}
