package io.github.morningk.graphql.tool;

import io.github.morningk.graphql.NonNull;
import io.github.morningk.graphql.ScalarType;

public class User {
  private Long id;
  public String name;
  public int age;

  @ScalarType("ID")
  @NonNull
  public Long getId() {
    return this.id;
  }
}
