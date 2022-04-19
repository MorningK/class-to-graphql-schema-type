package io.github.morningk.graphql.tool;

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
