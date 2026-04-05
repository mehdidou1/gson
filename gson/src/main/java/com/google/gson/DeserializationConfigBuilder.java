package com.google.gson;

import java.text.DateFormat;
import java.util.Objects;

public final class DeserializationConfigBuilder {

  private ToNumberStrategy objectToNumberStrategy = ToNumberPolicy.DOUBLE;
  private ToNumberStrategy numberToNumberStrategy = ToNumberPolicy.LAZILY_PARSED_NUMBER;
  private Strictness strictness = null;
  private String datePattern = null;
  private int dateStyle = DateFormat.DEFAULT;
  private int timeStyle = DateFormat.DEFAULT;

  DeserializationConfigBuilder() {}

  /** Copy constructor. */
  DeserializationConfigBuilder(DeserializationConfigBuilder src) {
    this.objectToNumberStrategy = src.objectToNumberStrategy;
    this.numberToNumberStrategy = src.numberToNumberStrategy;
    this.strictness = src.strictness;
    this.datePattern = src.datePattern;
    this.dateStyle = src.dateStyle;
    this.timeStyle = src.timeStyle;
  }

  /**
   * Sets the strategy used when deserializing a JSON number into an {@link Object}-typed field.
   *
   * @param strategy the strategy; must not be {@code null}
   * @return this builder for chaining
   * @see ToNumberPolicy#DOUBLE the default
   */
  public DeserializationConfigBuilder setObjectToNumberStrategy(ToNumberStrategy strategy) {
    this.objectToNumberStrategy = Objects.requireNonNull(strategy, "strategy");
    return this;
  }

  /**
   * Sets the strategy used when deserializing a JSON number into a {@link Number}-typed field.
   *
   * @param strategy the strategy; must not be {@code null}
   * @return this builder for chaining
   * @see ToNumberPolicy#LAZILY_PARSED_NUMBER the default
   */
  public DeserializationConfigBuilder setNumberToNumberStrategy(ToNumberStrategy strategy) {
    this.numberToNumberStrategy = Objects.requireNonNull(strategy, "strategy");
    return this;
  }

  /**
   * Sets how strictly the JSON specification is enforced while reading.
   *
   * @param strictness the strictness level; must not be {@code null}
   * @return this builder for chaining
   */
  public DeserializationConfigBuilder setStrictness(Strictness strictness) {
    this.strictness = Objects.requireNonNull(strictness, "strictness");
    return this;
  }

  /**
   * Sets the date pattern used when parsing {@link java.util.Date} fields.
   *
   * @param pattern a {@link java.text.SimpleDateFormat} pattern; may be {@code null} to reset to
   *     the default
   * @return this builder for chaining
   */
  public DeserializationConfigBuilder setDateFormat(String pattern) {
    this.datePattern = pattern;
    return this;
  }

  /**
   * Sets the date and time styles used when parsing {@link java.util.Date} fields via {@link
   * DateFormat#getDateTimeInstance(int, int)}.
   *
   * @param dateStyle one of the {@link DateFormat} style constants
   * @param timeStyle one of the {@link DateFormat} style constants
   * @return this builder for chaining
   */
  public DeserializationConfigBuilder setDateFormat(int dateStyle, int timeStyle) {
    this.dateStyle = checkStyle(dateStyle);
    this.timeStyle = checkStyle(timeStyle);
    this.datePattern = null;
    return this;
  }

  private static int checkStyle(int style) {
    if (style < 0 || style > 3) {
      throw new IllegalArgumentException("Invalid style: " + style);
    }
    return style;
  }

  // --- package-private accessors ---

  ToNumberStrategy getObjectToNumberStrategy() {
    return objectToNumberStrategy;
  }

  ToNumberStrategy getNumberToNumberStrategy() {
    return numberToNumberStrategy;
  }

  Strictness getStrictness() {
    return strictness;
  }

  String getDatePattern() {
    return datePattern;
  }

  int getDateStyle() {
    return dateStyle;
  }

  int getTimeStyle() {
    return timeStyle;
  }
}
