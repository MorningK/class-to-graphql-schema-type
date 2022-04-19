package io.github.morningk.graphql.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Format Java Class to GraphQL schema type
 */
public class Formatter {

  private static final String TYPE_TEMPLATE = """
      type %s {
      %s
      }
      %s""";
  private static final String ENUM_TEMPLATE = """
      enum %s {
      %s
      }
      """;
  private static final String SCALAR_TEMPLATE = "scalar %s\n";

  /**
   * format given class to GraphQL schema type include relative classes and enums
   *
   * @param sourceClass the class to formatted
   * @return GraphQL schema type output
   */
  public static String formatSchemaType(Class<?> sourceClass) {
    return formatSchemaType(sourceClass, new HashSet<>());
  }

  private static String formatSchemaType(Class<?> sourceClass, Set<Class<?>> existingSet) {
    existingSet.add(sourceClass);
    if (sourceClass.getAnnotation(ScalarType.class) != null) {
      return String.format(SCALAR_TEMPLATE, sourceClass.getAnnotation(ScalarType.class).value());
    }
    if (sourceClass.isEnum()) {
      return formatEnum(sourceClass);
    }
    String name = sourceClass.getSimpleName();
    StringBuilder extra = new StringBuilder();
    Set<Class<?>> extraClassSet = new HashSet<>();
    String fieldDeclaration = formatFieldDeclaration(sourceClass, extraClassSet);
    for (Class<?> extraClass : extraClassSet) {
      if (existingSet.contains(extraClass)) {
        continue;
      }
      existingSet.add(extraClass);
      extra.append(formatSchemaType(extraClass, existingSet));
    }
    return String.format(TYPE_TEMPLATE, name, fieldDeclaration, extra);
  }

  private static String formatFieldDeclaration(Class<?> sourceClass, Set<Class<?>> extraClassSet) {
    Map<String, String> fields = new LinkedHashMap<>();
    for (Field field : sourceClass.getFields()) {
      if (field.getAnnotation(Ignored.class) == null) {
        fields.put(field.getName(), getFieldSchemaType(field, extraClassSet));
      }
    }
    for (Method method : sourceClass.getMethods()) {
      if (needInclude(method)) {
        fields.put(getMethodFieldName(method.getName()), getFieldSchemaType(method, extraClassSet));
      }
    }
    return
        fields.entrySet().stream()
            .map(field -> "\t" + field.getKey() + ": " + field.getValue())
            .collect(Collectors.joining("\n"));
  }

  private static String getFieldSchemaType(Field field, Set<Class<?>> extra) {
    Class<?> type = field.getType();
    String fieldType;
    if (field.getAnnotation(ScalarType.class) != null) {
      fieldType = field.getAnnotation(ScalarType.class).value();
    } else if (field.getAnnotation(ListType.class) != null) {
      ListType listType = field.getAnnotation(ListType.class);
      fieldType = getFieldSchemaType(listType, extra);
    } else {
      fieldType = getFieldSchemaType(type, extra);
    }
    if (field.getAnnotation(NonNull.class) != null) {
      return fieldType + "!";
    }
    return fieldType;
  }

  private static String getFieldSchemaType(ListType listType, Set<Class<?>> extra) {
    Class<?> elementClass = listType.elementType();
    return "["
        + getFieldSchemaType(elementClass, extra)
        + (listType.elementNonNull() ? "!" : "")
        + "]";
  }

  private static boolean needInclude(Method method) {
    if (method.getParameterCount() > 0) {
      return false;
    }
    if (method.getReturnType().equals(Void.TYPE)) {
      return false;
    }
    if (method.getAnnotation(Ignored.class) != null) {
      return false;
    }
    for (Method objectMethod : Object.class.getMethods()) {
      if (objectMethod.getName().equals(method.getName())) {
        return false;
      }
    }
    return true;
  }

  private static String getMethodFieldName(String name) {
    if (name.startsWith("get")) {
      return name.substring("get".length(), "get".length() + 1).toLowerCase()
          + name.substring("get".length() + 1);
    }
    if (name.startsWith("is")) {
      return name.substring("is".length(), "is".length() + 1).toLowerCase()
          + name.substring("is".length() + 1);
    }
    return name;
  }

  private static String getFieldSchemaType(Method method, Set<Class<?>> extra) {
    String fieldType;
    if (method.getAnnotation(ScalarType.class) != null) {
      fieldType = method.getAnnotation(ScalarType.class).value();
    } else if (method.getAnnotation(ListType.class) != null) {
      ListType listType = method.getAnnotation(ListType.class);
      fieldType = getFieldSchemaType(listType, extra);
    } else {
      fieldType = getFieldSchemaType(method.getReturnType(), extra);
    }
    if (method.getAnnotation(NonNull.class) != null) {
      return fieldType + "!";
    }
    return fieldType;
  }

  private static String getFieldSchemaType(Class<?> type, Set<Class<?>> extra) {
    if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
      return "Boolean";
    }
    if (type.equals(Byte.TYPE) || type.equals(Byte.class)
        || type.equals(Short.TYPE) || type.equals(Short.class)
        || type.equals(Integer.TYPE) || type.equals(Integer.class)
        || type.equals(Long.TYPE) || type.equals(Long.class)
        || type.equals(BigInteger.class)) {
      return "Int";
    }
    if (type.equals(Float.TYPE) || type.equals(Float.class)
        || type.equals(Double.TYPE) || type.equals(Double.class)
        || type.equals(BigDecimal.class)) {
      return "Float";
    }
    if (type.equals(Character.TYPE)
        || type.equals(Character.class)
        || type.equals(String.class)
        || (type.isArray() &&
        (type.getComponentType().equals(Character.TYPE)
            || type.getComponentType().equals(Character.class)))) {
      return "String";
    }
    if (type.isEnum()) {
      extra.add(type);
      return type.getSimpleName();
    }
    if (type.isArray()) {
      return getArrayFieldTypeName(type, extra);
    }
    if (isIterableType(type)) {
      return getIterableFieldTypeName(type, extra);
    }
    extra.add(type);
    return type.getSimpleName();
  }

  private static String getIterableFieldTypeName(Class<?> type, Set<Class<?>> extra) {
    if (type.getGenericSuperclass() != null) {
      ParameterizedType parameterizedType = (ParameterizedType) type.getGenericSuperclass();
      return "["
          + getFieldSchemaType((Class<?>) parameterizedType.getActualTypeArguments()[0], extra)
          + "]";
    }
    Type[] parameterizedTypes = type.getGenericInterfaces();
    if (parameterizedTypes.length > 0) {
      for (Type parameterizedType : parameterizedTypes) {
        if (parameterizedType instanceof ParameterizedType) {
          Type actualType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
          if (actualType instanceof Class) {
            return "[" + getFieldSchemaType((Class<?>) actualType, extra) + "]";
          }
          return "[" + actualType.getTypeName() + "]";
        }
      }
    }
    return type.getSimpleName();
  }

  private static boolean isIterableType(Class<?> type) {
    if (type.equals(Iterable.class)) {
      return true;
    }
    if (type.isInterface()) {
      for (Class<?> extendInterface : type.getInterfaces()) {
        return isIterableType(extendInterface);
      }
    } else {
      if (type.getSuperclass() != null) {
        return isIterableType(type.getSuperclass());
      }
    }
    return false;
  }

  private static String formatEnum(Class<?> type) {
    return ENUM_TEMPLATE.formatted(
        type.getSimpleName(),
        Arrays.stream(
            type.getEnumConstants())
            .map(val -> "\t" + val.toString()).collect(Collectors.joining("\n")));
  }

  private static String getArrayFieldTypeName(Class<?> type, Set<Class<?>> extra) {
    return "[" + getFieldSchemaType(type.getComponentType(), extra) + "]";
  }
}
