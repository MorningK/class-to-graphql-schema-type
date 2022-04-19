package io.github.morningk.graphql.tool;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Field or Method or Type annotated with @ListType will be transformed as GraphQL list type
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface ListType {
  Class<?> elementType();
  boolean elementNonNull() default false;
}
