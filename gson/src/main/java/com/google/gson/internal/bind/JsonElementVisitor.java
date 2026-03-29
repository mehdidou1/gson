package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

public interface JsonElementVisitor {
  public JsonToken visit(JsonObject obj) throws MalformedJsonException;

  public JsonToken visit(JsonArray array) throws MalformedJsonException;

  public JsonToken visit(JsonPrimitive primitive) throws MalformedJsonException;

  public JsonToken visit(JsonNull jsonNull) throws MalformedJsonException;
}
