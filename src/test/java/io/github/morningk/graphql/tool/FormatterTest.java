package io.github.morningk.graphql.tool;

import org.junit.jupiter.api.Test;

class FormatterTest {

  @Test
  void formatSchemaType() {
    System.out.println(Formatter.formatSchemaType(User.class));
  }
}
