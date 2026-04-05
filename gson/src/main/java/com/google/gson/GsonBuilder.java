package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class GsonBuilder {

  final SerializationConfigBuilder serConfig;
  final DeserializationConfigBuilder deserConfig;
  final AdapterRegistrationBuilder adapterReg;

  Excluder excluder = Excluder.DEFAULT;
  FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
  final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<>();
  boolean complexMapKeySerialization = false;
  boolean useJdkUnsafe = true;
  final ArrayDeque<ReflectionAccessFilter> reflectionFilters = new ArrayDeque<>();

  static final ConstructorConstructor DEFAULT_CONSTRUCTOR_CONSTRUCTOR =
      new ConstructorConstructor(Collections.emptyMap(), true, Collections.emptyList());

  static final JsonAdapterAnnotationTypeAdapterFactory
      DEFAULT_JSON_ADAPTER_ANNOTATION_TYPE_ADAPTER_FACTORY =
          new JsonAdapterAnnotationTypeAdapterFactory(DEFAULT_CONSTRUCTOR_CONSTRUCTOR);

  static final GsonBuilder DEFAULT = new GsonBuilder();

  static final List<TypeAdapterFactory> DEFAULT_TYPE_ADAPTER_FACTORIES =
      new GsonBuilder()
          .createFactories(
              DEFAULT_CONSTRUCTOR_CONSTRUCTOR,
              DEFAULT_JSON_ADAPTER_ANNOTATION_TYPE_ADAPTER_FACTORY);

  public GsonBuilder() {
    this.serConfig = new SerializationConfigBuilder();
    this.deserConfig = new DeserializationConfigBuilder();
    this.adapterReg = new AdapterRegistrationBuilder();
  }

  GsonBuilder(Gson gson) {

    this.serConfig = new SerializationConfigBuilder();
    this.deserConfig = new DeserializationConfigBuilder();
    this.adapterReg = new AdapterRegistrationBuilder();

    if (gson.serializeNulls) serConfig.serializeNulls();
    if (!gson.htmlSafe) serConfig.disableHtmlEscaping();
    if (gson.serializeSpecialFloatingPointValues) serConfig.serializeSpecialFloatingPointValues();
    if (gson.generateNonExecutableJson) serConfig.generateNonExecutableJson();
    serConfig.setFormattingStyle(gson.formattingStyle);
    serConfig.setLongSerializationPolicy(gson.longSerializationPolicy);

    deserConfig.setObjectToNumberStrategy(gson.objectToNumberStrategy);
    deserConfig.setNumberToNumberStrategy(gson.numberToNumberStrategy);
    if (gson.strictness != null) deserConfig.setStrictness(gson.strictness);
    if (gson.datePattern != null) deserConfig.setDateFormat(gson.datePattern);
    else deserConfig.setDateFormat(gson.dateStyle, gson.timeStyle);

    gson.builderFactories.forEach(adapterReg::registerTypeAdapterFactory);
    gson.builderHierarchyFactories.forEach(adapterReg::addHierarchyFactory);

    this.excluder = gson.excluder;
    this.fieldNamingPolicy = gson.fieldNamingStrategy;
    this.instanceCreators.putAll(gson.instanceCreators);
    this.complexMapKeySerialization = gson.complexMapKeySerialization;
    this.useJdkUnsafe = gson.useJdkUnsafe;
    this.reflectionFilters.addAll(gson.reflectionFilters);
  }

  private GsonBuilder(
      SerializationConfigBuilder serConfig,
      DeserializationConfigBuilder deserConfig,
      AdapterRegistrationBuilder adapterReg,
      Excluder excluder,
      FieldNamingStrategy fieldNamingPolicy,
      Map<Type, InstanceCreator<?>> instanceCreators,
      boolean complexMapKeySerialization,
      boolean useJdkUnsafe,
      ArrayDeque<ReflectionAccessFilter> reflectionFilters) {
    this.serConfig = serConfig;
    this.deserConfig = deserConfig;
    this.adapterReg = adapterReg;
    this.excluder = excluder;
    this.fieldNamingPolicy = fieldNamingPolicy;
    this.instanceCreators.putAll(instanceCreators);
    this.complexMapKeySerialization = complexMapKeySerialization;
    this.useJdkUnsafe = useJdkUnsafe;
    this.reflectionFilters.addAll(reflectionFilters);
  }

  public GsonBuilder newBuilder() {
    return new GsonBuilder(
        new SerializationConfigBuilder(serConfig),
        new DeserializationConfigBuilder(deserConfig),
        new AdapterRegistrationBuilder(adapterReg),
        excluder,
        fieldNamingPolicy,
        instanceCreators,
        complexMapKeySerialization,
        useJdkUnsafe,
        reflectionFilters);
  }

  @CanIgnoreReturnValue
  public GsonBuilder serializeNulls() {
    serConfig.serializeNulls();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder disableHtmlEscaping() {
    serConfig.disableHtmlEscaping();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder serializeSpecialFloatingPointValues() {
    serConfig.serializeSpecialFloatingPointValues();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder generateNonExecutableJson() {
    serConfig.generateNonExecutableJson();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setPrettyPrinting() {
    serConfig.setPrettyPrinting();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setFormattingStyle(FormattingStyle formattingStyle) {
    serConfig.setFormattingStyle(formattingStyle);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
    serConfig.setLongSerializationPolicy(serializationPolicy);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setObjectToNumberStrategy(ToNumberStrategy objectToNumberStrategy) {
    deserConfig.setObjectToNumberStrategy(objectToNumberStrategy);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setNumberToNumberStrategy(ToNumberStrategy numberToNumberStrategy) {
    deserConfig.setNumberToNumberStrategy(numberToNumberStrategy);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setStrictness(Strictness strictness) {
    deserConfig.setStrictness(strictness);
    return this;
  }

  @Deprecated
  @InlineMe(
      replacement = "this.setStrictness(Strictness.LENIENT)",
      imports = "com.google.gson.Strictness")
  @CanIgnoreReturnValue
  public GsonBuilder setLenient() {
    return setStrictness(Strictness.LENIENT);
  }

  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(String pattern) {
    if (pattern != null) {
      try {
        new SimpleDateFormat(pattern);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("The date pattern '" + pattern + "' is not valid", e);
      }
    }
    deserConfig.setDateFormat(pattern);
    return this;
  }

  @Deprecated
  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(int dateStyle) {
    deserConfig.setDateFormat(dateStyle, deserConfig.getTimeStyle());
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setDateFormat(int dateStyle, int timeStyle) {
    deserConfig.setDateFormat(dateStyle, timeStyle);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
    Objects.requireNonNull(type);
    Objects.requireNonNull(typeAdapter);

    if (hasNonOverridableAdapter(type)) {
      throw new IllegalArgumentException("Cannot override built-in adapter for " + type);
    }
    // InstanceCreator is NOT in AdapterRegistrationBuilder — it lives here
    if (typeAdapter instanceof InstanceCreator<?>) {
      instanceCreators.put(type, (InstanceCreator<?>) typeAdapter);
    }
    adapterReg.registerTypeAdapter(type, typeAdapter);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
    adapterReg.registerTypeAdapterFactory(factory);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
    adapterReg.registerTypeHierarchyAdapter(baseType, typeAdapter);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setVersion(double version) {
    if (Double.isNaN(version) || version < 0.0) {
      throw new IllegalArgumentException("Invalid version: " + version);
    }
    excluder = excluder.withVersion(version);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder excludeFieldsWithModifiers(int... modifiers) {
    Objects.requireNonNull(modifiers);
    excluder = excluder.withModifiers(modifiers);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
    excluder = excluder.excludeFieldsWithoutExposeAnnotation();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder disableInnerClassSerialization() {
    excluder = excluder.disableInnerClassSerialization();
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setExclusionStrategies(ExclusionStrategy... strategies) {
    Objects.requireNonNull(strategies);
    for (ExclusionStrategy strategy : strategies) {
      excluder = excluder.withExclusionStrategy(strategy, true, true);
    }
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
    Objects.requireNonNull(strategy);
    excluder = excluder.withExclusionStrategy(strategy, true, false);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
    Objects.requireNonNull(strategy);
    excluder = excluder.withExclusionStrategy(strategy, false, true);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
    return setFieldNamingStrategy(namingConvention);
  }

  @CanIgnoreReturnValue
  public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
    this.fieldNamingPolicy = Objects.requireNonNull(fieldNamingStrategy);
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder enableComplexMapKeySerialization() {
    this.complexMapKeySerialization = true;
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder disableJdkUnsafe() {
    this.useJdkUnsafe = false;
    return this;
  }

  @CanIgnoreReturnValue
  public GsonBuilder addReflectionAccessFilter(ReflectionAccessFilter filter) {
    Objects.requireNonNull(filter);
    reflectionFilters.addFirst(filter);
    return this;
  }

  public Gson create() {
    return new Gson(this);
  }

  List<TypeAdapterFactory> createFactories(
      ConstructorConstructor constructorConstructor,
      JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
    ArrayList<TypeAdapterFactory> factories = new ArrayList<>();

    factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
    factories.add(ObjectTypeAdapter.getFactory(deserConfig.getObjectToNumberStrategy()));
    factories.add(excluder);

    addUserDefinedAdapters(factories);
    addDateTypeAdapters(factories);

    factories.add(TypeAdapters.STRING_FACTORY);
    factories.add(TypeAdapters.INTEGER_FACTORY);
    factories.add(TypeAdapters.BOOLEAN_FACTORY);
    factories.add(TypeAdapters.BYTE_FACTORY);
    factories.add(TypeAdapters.SHORT_FACTORY);
    TypeAdapter<Number> longAdapter = serConfig.getLongPolicy().typeAdapter();
    factories.add(TypeAdapters.newFactory(long.class, Long.class, longAdapter));
    factories.add(TypeAdapters.newFactory(double.class, Double.class, doubleAdapter()));
    factories.add(TypeAdapters.newFactory(float.class, Float.class, floatAdapter()));
    factories.add(NumberTypeAdapter.getFactory(deserConfig.getNumberToNumberStrategy()));
    factories.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
    factories.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
    factories.add(
        TypeAdapters.newFactory(AtomicLong.class, TypeAdapters.atomicLongAdapter(longAdapter)));
    factories.add(
        TypeAdapters.newFactory(
            AtomicLongArray.class, TypeAdapters.atomicLongArrayAdapter(longAdapter)));
    factories.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
    factories.add(TypeAdapters.CHARACTER_FACTORY);
    factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
    factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
    factories.add(TypeAdapters.BIG_DECIMAL_FACTORY);
    factories.add(TypeAdapters.BIG_INTEGER_FACTORY);
    factories.add(TypeAdapters.LAZILY_PARSED_NUMBER_FACTORY);
    factories.add(TypeAdapters.URL_FACTORY);
    factories.add(TypeAdapters.URI_FACTORY);
    factories.add(TypeAdapters.UUID_FACTORY);
    factories.add(TypeAdapters.CURRENCY_FACTORY);
    factories.add(TypeAdapters.LOCALE_FACTORY);
    factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
    factories.add(TypeAdapters.BIT_SET_FACTORY);
    factories.add(DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY);
    factories.add(TypeAdapters.CALENDAR_FACTORY);
    TypeAdapterFactory javaTimeFactory = TypeAdapters.javaTimeTypeAdapterFactory();
    if (javaTimeFactory != null) {
      factories.add(javaTimeFactory);
    }
    factories.addAll(SqlTypesSupport.SQL_TYPE_FACTORIES);
    factories.add(ArrayTypeAdapter.FACTORY);
    factories.add(TypeAdapters.CLASS_FACTORY);
    factories.add(new CollectionTypeAdapterFactory(constructorConstructor));
    factories.add(new MapTypeAdapterFactory(constructorConstructor, complexMapKeySerialization));
    factories.add(jsonAdapterFactory);
    factories.add(TypeAdapters.ENUM_FACTORY);
    factories.add(
        new ReflectiveTypeAdapterFactory(
            constructorConstructor,
            fieldNamingPolicy,
            excluder,
            jsonAdapterFactory,
            newImmutableList(reflectionFilters)));

    factories.trimToSize();
    return Collections.unmodifiableList(factories);
  }

  private static boolean hasNonOverridableAdapter(Type type) {
    return type == Object.class;
  }

  private TypeAdapter<Number> doubleAdapter() {
    return serConfig.isSerializeSpecialFloats() ? TypeAdapters.DOUBLE : TypeAdapters.DOUBLE_STRICT;
  }

  private TypeAdapter<Number> floatAdapter() {
    return serConfig.isSerializeSpecialFloats() ? TypeAdapters.FLOAT : TypeAdapters.FLOAT_STRICT;
  }

  private void addUserDefinedAdapters(List<TypeAdapterFactory> all) {
    List<TypeAdapterFactory> userFactories = new ArrayList<>(adapterReg.getFactories());
    Collections.reverse(userFactories);
    all.addAll(userFactories);

    List<TypeAdapterFactory> userHierarchyFactories =
        new ArrayList<>(adapterReg.getHierarchyFactories());
    Collections.reverse(userHierarchyFactories);
    all.addAll(userHierarchyFactories);
  }

  private void addDateTypeAdapters(List<TypeAdapterFactory> factories) {
    String datePattern = deserConfig.getDatePattern();
    int dateStyle = deserConfig.getDateStyle();
    int timeStyle = deserConfig.getTimeStyle();
    boolean sqlSupported = SqlTypesSupport.SUPPORTS_SQL_TYPES;

    TypeAdapterFactory dateFactory;
    TypeAdapterFactory sqlTimestampFactory = null;
    TypeAdapterFactory sqlDateFactory = null;

    if (datePattern != null && !datePattern.trim().isEmpty()) {
      dateFactory = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(datePattern);
      if (sqlSupported) {
        sqlTimestampFactory = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(datePattern);
        sqlDateFactory = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(datePattern);
      }
    } else if (dateStyle != DateFormat.DEFAULT || timeStyle != DateFormat.DEFAULT) {
      dateFactory = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(dateStyle, timeStyle);
      if (sqlSupported) {
        sqlTimestampFactory =
            SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(dateStyle, timeStyle);
        sqlDateFactory = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(dateStyle, timeStyle);
      }
    } else {
      return;
    }

    factories.add(dateFactory);
    if (sqlSupported) {
      factories.add(sqlTimestampFactory);
      factories.add(sqlDateFactory);
    }
  }

  static <E> List<E> newImmutableList(Collection<E> collection) {
    if (collection.isEmpty()) return Collections.emptyList();
    if (collection.size() == 1) {
      return Collections.singletonList(
          collection instanceof List
              ? ((List<E>) collection).get(0)
              : collection.iterator().next());
    }
    @SuppressWarnings("unchecked")
    List<E> list = (List<E>) Collections.unmodifiableList(Arrays.asList(collection.toArray()));
    return list;
  }
}
