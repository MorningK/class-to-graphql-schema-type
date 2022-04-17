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

  public static String formatSchemaType(Class<?> sourceClass) {
    if (sourceClass.isEnum()) {
      return formatEnum(sourceClass);
    }
    String name = sourceClass.getSimpleName();
    StringBuilder extra = new StringBuilder();
    Set<Class<?>> extraClassSet = new HashSet<>();
    Map<String, String> fields = new LinkedHashMap<>();
    for (Field field: sourceClass.getFields()) {
      fields.put(field.getName(), getFieldSchemaType(field, extraClassSet));
    }
    for (Method method: sourceClass.getMethods()) {
      if (needInclude(method)) {
        fields.put(getMethodFieldName(method.getName()), getFieldSchemaType(method, extraClassSet));
      }
    }
    String fieldDeclaration =
        fields.entrySet().stream()
          .map(field -> "\t" + field.getKey() + ": " + field.getValue())
          .collect(Collectors.joining("\n"));
    for (Class<?> extraClass: extraClassSet) {
      extra.append(formatSchemaType(extraClass));
    }
    return String.format(TYPE_TEMPLATE, name, fieldDeclaration, extra);
  }

  private static String getFieldSchemaType(Field field, Set<Class<?>> extra) {
    Class<?> type = field.getType();
    if (field.getAnnotation(Id.class) != null) {
      return "ID";
    }
    return getFieldSchemaType(type, extra);
  }

  private static boolean needInclude(Method method) {
    if (method.getParameterCount() > 0) {
      return false;
    }
    if (method.getReturnType().equals(Void.TYPE)) {
      return false;
    }
    for (Method objectMethod: Object.class.getMethods()) {
      if (objectMethod.getName().equals(method.getName())) {
        return false;
      }
    }
    return true;
  }

  private static String getMethodFieldName(String name) {
    if (name.startsWith("get")) {
      return name.substring("get".length(), "get".length() + 1).toLowerCase() + name.substring("get".length() + 1);
    }
    return name;
  }

  private static String getFieldSchemaType(Method method, Set<Class<?>> extra) {
    if (method.getAnnotation(Id.class) != null) {
      return "ID";
    }
    return getFieldSchemaType(method.getReturnType(), extra);
  }

  private static String getFieldSchemaType(Class<?> type, Set<Class<?>> extra) {
    if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
      return "Boolean";
    }
    if (type.equals(Byte.TYPE) || type.equals(Byte.class)
        ||type.equals(Short.TYPE) || type.equals(Short.class)
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
    if (type.equals(Character.TYPE) || type.equals(Character.class) || type.equals(String.class)) {
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
    return type.getSimpleName();
  }

  private static String getIterableFieldTypeName(Class<?> type, Set<Class<?>> extra) {
    if (type.getGenericSuperclass() != null) {
      ParameterizedType parameterizedType = (ParameterizedType) type.getGenericSuperclass();
      return "[" + getFieldSchemaType((Class<?>) parameterizedType.getActualTypeArguments()[0], extra) + "]";
    }
    Type[] parameterizedTypes = type.getGenericInterfaces();
    if (parameterizedTypes.length > 0) {
      for (Type parameterizedType: parameterizedTypes) {
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
      for (Class<?> extendInterface: type.getInterfaces()) {
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
