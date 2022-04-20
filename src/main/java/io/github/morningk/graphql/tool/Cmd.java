package io.github.morningk.graphql.tool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Cmd {

  public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {
    if (args.length != 2) {
      System.out.println(
          """
          usage: java -jar class-to-graphql-schema-type.jar [jar-file-path] [binary-class-name]
          """
      );
      System.exit(-1);
    }
    File jarFile = new File(args[0]);
    ClassLoader classLoader =
        new URLClassLoader(
            new URL[] {
                jarFile.toURI().toURL(),
            });
    Class<?> targetClass = classLoader.loadClass(args[1]);
    System.out.println(Formatter.formatSchemaType(targetClass));
  }
}
