package com.google.gson;

import java.util.Objects;

/**
 * Configures the <em>serialization</em> side of a {@link Gson} instance.
 *
 * <p>Extracted from {@link GsonBuilder} as part of decomposing that god class. {@code GsonBuilder}
 * retains its public API unchanged and delegates to an instance of this class for all
 * serialization-specific settings.
 *
 * <p>This separation means that serialization config can be reasoned about, tested, and documented
 * independently of deserialization config and adapter registration.
 */
public final class SerializationConfigBuilder {

  private boolean serializeNulls = false;
  private boolean htmlSafe = true;
  private boolean serializeSpecialFloats = false;
  private boolean generateNonExecutable = false;
  private FormattingStyle formattingStyle = FormattingStyle.COMPACT;
  private LongSerializationPolicy longPolicy = LongSerializationPolicy.DEFAULT;

  SerializationConfigBuilder() {}

  /** Copy constructor used by {@link GsonBuilder#newBuilder()}. */
  SerializationConfigBuilder(SerializationConfigBuilder src) {
    this.serializeNulls = src.serializeNulls;
    this.htmlSafe = src.htmlSafe;
    this.serializeSpecialFloats = src.serializeSpecialFloats;
    this.generateNonExecutable = src.generateNonExecutable;
    this.formattingStyle = src.formattingStyle;
    this.longPolicy = src.longPolicy;
  }

  public SerializationConfigBuilder serializeNulls() {
    this.serializeNulls = true;
    return this;
  }

  public SerializationConfigBuilder disableHtmlEscaping() {
    this.htmlSafe = false;
    return this;
  }

  public SerializationConfigBuilder serializeSpecialFloatingPointValues() {
    this.serializeSpecialFloats = true;
    return this;
  }

  public SerializationConfigBuilder generateNonExecutableJson() {
    this.generateNonExecutable = true;
    return this;
  }

  public SerializationConfigBuilder setPrettyPrinting() {
    return setFormattingStyle(FormattingStyle.PRETTY);
  }

  public SerializationConfigBuilder setFormattingStyle(FormattingStyle style) {
    this.formattingStyle = Objects.requireNonNull(style, "style");
    return this;
  }

  public SerializationConfigBuilder setLongSerializationPolicy(LongSerializationPolicy policy) {
    this.longPolicy = Objects.requireNonNull(policy, "policy");
    return this;
  }

  // --- package-private accessors used by GsonBuilder ---

  boolean isSerializeNulls() {
    return serializeNulls;
  }

  boolean isHtmlSafe() {
    return htmlSafe;
  }

  boolean isSerializeSpecialFloats() {
    return serializeSpecialFloats;
  }

  boolean isGenerateNonExecutable() {
    return generateNonExecutable;
  }

  FormattingStyle getFormattingStyle() {
    return formattingStyle;
  }

  LongSerializationPolicy getLongPolicy() {
    return longPolicy;
  }
}
