package io.github.morningk.graphql.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FormatterTest {
  @Test
  void formatUserClass() {
    String result = Formatter.formatSchemaType(User.class);
    String except = """
        type User {
        \tname: String
        \tage: Int
        \tid: ID!
        }
        """;
    assertEquals(result, except);
  }
}
