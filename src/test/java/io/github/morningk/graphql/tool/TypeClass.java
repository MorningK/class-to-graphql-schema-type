package io.github.morningk.graphql.tool;

import io.github.morningk.graphql.Ignored;
import io.github.morningk.graphql.ListType;
import io.github.morningk.graphql.NonNull;
import io.github.morningk.graphql.ScalarType;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TypeClass {

  static class PrimitiveType {
    public byte byteValue;
    public short shortValue;
    public int intValue;
    public long longValue;
    public float floatValue;
    public double doubleValue;
  }

  static class PrimitiveWrapType {
    public Byte byteValue;
    public Short shortValue;
    public Integer intValue;
    public Long longValue;
    public Float floatValue;
    public Double doubleValue;
    public BigInteger bigInteger;
    public BigDecimal bigDecimal;
  }

  static class StringType {
    public char charValue;
    public char[] chars;
    public Character character;
    public Character[] characters;
    public String string;
  }

  static class ArrayType {
    public int[] ints;
    public Long[] longs;
    public String[] strings;
  }

  @ScalarType("File")
  static class ScalarTypeFile {
    private byte[] content;
  }

  static class SimpleType {
    public Long id;
    public String name;
    public String[] addresses;
    public Rank rank;
    public Student student;
  }

  static class ComplicatedType {
    private Long id;
    private String name;
    private LocalDate birthday;
    private String[] locations;
    private PrimitiveType primitiveType;
    private PrimitiveWrapType primitiveWrapType;
    private StringType stringType;
    private ArrayType arrayType;
    private ScalarTypeFile scalarTypeFile;
    private SimpleType simpleType;
    private Month month;
    private List<Student> students;
    private Set<BigDecimal> bigDecimals;
    private Map<Long, Student> studentMap;

    @ScalarType("ID")
    @NonNull
    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    @ScalarType("String")
    public LocalDate getBirthday() {
      return birthday;
    }

    public String[] getLocations() {
      return locations;
    }

    public PrimitiveType getPrimitiveType() {
      return primitiveType;
    }

    public PrimitiveWrapType getPrimitiveWrapType() {
      return primitiveWrapType;
    }

    public StringType getStringType() {
      return stringType;
    }

    public ArrayType getArrayType() {
      return arrayType;
    }

    public ScalarTypeFile getScalarTypeFile() {
      return scalarTypeFile;
    }

    public SimpleType getSimpleType() {
      return simpleType;
    }

    public Month getMonth() {
      return month;
    }

    @ListType(elementType = Student.class)
    @NonNull
    public List<Student> getStudents() {
      return students;
    }

    @ListType(elementType = BigDecimal.class, elementNonNull = true)
    @NonNull
    public Set<BigDecimal> getBigDecimals() {
      return bigDecimals;
    }

    @Ignored
    public Map<Long, Student> getStudentMap() {
      return studentMap;
    }
  }

  enum Rank {
    FIRST,
    SECOND,
    THIRD,
  }

  enum Month {
    JAN(1),
    FEB(2),
    MAR(3),
    ;
    private final int value;

    Month(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  static record Student(@NonNull Long id, String name, double score, @Ignored File file) {}

  public static void main(String[] args) {
    System.out.println(Formatter.formatSchemaType(ComplicatedType.class));
  }
}
