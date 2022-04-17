package io.github.morningk.graphql.tool;

import org.junit.jupiter.api.Test;

class FormatterTest {

  @Test
  void formatSchemaType() {
    String result = Formatter.formatSchemaType(User.class);
    System.out.println("formatSchemaType User:");
    System.out.println(result);
  }
}
