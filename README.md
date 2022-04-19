# class-to-graphql-schema-type
format Java Class to GraphQL Schema Type

## Example
### Java Class
```Java
import io.github.morningk.graphql.tool.NonNull;
import io.github.morningk.graphql.tool.ScalarType;

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
```
```java
import io.github.morningk.graphql.tool.Formatter;

String result = Formatter.formatSchemaType(User.class);
```
### GraphQL Schema Type
```graphql
type User {
  name: String
  age: Int
  id: ID!
}
```
