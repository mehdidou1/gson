package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;

public class TokenPeekVisitor implements JsonElementVisitor {

  @Override
  public JsonToken visit(JsonObject object) {
    return JsonToken.BEGIN_OBJECT;
  }

  @Override
  public JsonToken visit(JsonArray array) {
    return JsonToken.BEGIN_ARRAY;
  }

  @Override
  public JsonToken visit(JsonNull jsonNull) {
    return JsonToken.NULL;
  }

  @Override
  public JsonToken visit(JsonPrimitive primitive) {
    if (primitive.isString()) return JsonToken.STRING;
    if (primitive.isBoolean()) return JsonToken.BOOLEAN;
    if (primitive.isNumber()) return JsonToken.NUMBER;
    throw new AssertionError();
  }
}
