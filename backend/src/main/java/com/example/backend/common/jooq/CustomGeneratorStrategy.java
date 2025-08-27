package com.example.backend.common.jooq;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class CustomGeneratorStrategy extends DefaultGeneratorStrategy {
  @Override
  public String getJavaClassName(Definition definition, Mode mode) {
    String defaultName = super.getJavaClassName(definition, mode);
    return "J" + defaultName;
  }
}
