package com.google.gson.internal.bind;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionInfo {
  private Field field;
  private Method accessor;
  private TypeToken<?> fieldType;

  public ReflectionInfo(Field field, Method accessor, TypeToken<?> fieldType) {
    this.field = field;
    this.accessor = accessor;
    this.fieldType = fieldType;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public Method getAccessor() {
    return accessor;
  }

  public void setAccessor(Method accessor) {
    this.accessor = accessor;
  }

  public TypeToken<?> getFieldType() {
    return fieldType;
  }

  public void setFieldType(TypeToken<?> fieldType) {
    this.fieldType = fieldType;
  }
}
