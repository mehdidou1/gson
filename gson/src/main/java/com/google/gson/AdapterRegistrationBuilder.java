package com.google.gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages registration of custom {@link TypeAdapter}s, {@link TypeAdapterFactory}s, {@link
 * JsonSerializer}s, and {@link JsonDeserializer}s.
 *
 * <p>Extracted from {@link GsonBuilder} as part of decomposing that god class. All adapter-registry
 * concerns live here; {@code GsonBuilder} delegates every {@code register*} call to an instance of
 * this class.
 *
 * @see SerializationConfigBuilder
 * @see DeserializationConfigBuilder
 */
public final class AdapterRegistrationBuilder {

  private final List<TypeAdapterFactory> factories = new ArrayList<>();
  private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<>();

  AdapterRegistrationBuilder() {}

  /** Copy constructor. */
  AdapterRegistrationBuilder(AdapterRegistrationBuilder src) {
    this.factories.addAll(src.factories);
    this.hierarchyFactories.addAll(src.hierarchyFactories);
  }

  /**
   * Registers a type adapter for exact type {@code type}.
   *
   * <p>The {@code typeAdapter} argument must implement at least one of {@link TypeAdapter}, {@link
   * JsonSerializer}, {@link JsonDeserializer}, or {@link InstanceCreator}.
   *
   * @param type the type for which the adapter is registered
   * @param typeAdapter the adapter implementation
   * @return this builder for chaining
   * @throws IllegalArgumentException if {@code typeAdapter} does not implement a recognised adapter
   *     interface
   */
  public AdapterRegistrationBuilder registerTypeAdapter(Type type, Object typeAdapter) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(typeAdapter, "typeAdapter");
    validateAdapter(typeAdapter);
    addFactoriesFromAdapter(TypeToken.get(type), typeAdapter, false);
    return this;
  }

  /**
   * Registers a {@link TypeAdapterFactory}.
   *
   * @param factory the factory; must not be {@code null}
   * @return this builder for chaining
   */
  public AdapterRegistrationBuilder registerTypeAdapterFactory(TypeAdapterFactory factory) {
    factories.add(Objects.requireNonNull(factory, "factory"));
    return this;
  }

  /**
   * Registers a type adapter for {@code baseType} and all its subtypes.
   *
   * @param baseType the base class or interface
   * @param typeAdapter the adapter implementation
   * @return this builder for chaining
   */
  public AdapterRegistrationBuilder registerTypeHierarchyAdapter(
      Class<?> baseType, Object typeAdapter) {
    Objects.requireNonNull(baseType, "baseType");
    Objects.requireNonNull(typeAdapter, "typeAdapter");
    validateAdapter(typeAdapter);
    addFactoriesFromAdapter(TypeToken.get(baseType), typeAdapter, true);
    return this;
  }

  // --- private helpers ---

  private static void validateAdapter(Object adapter) {
    if (!(adapter instanceof TypeAdapter<?>
        || adapter instanceof JsonSerializer<?>
        || adapter instanceof JsonDeserializer<?>
        || adapter instanceof InstanceCreator<?>
        || adapter instanceof TypeAdapterFactory)) {
      throw new IllegalArgumentException(
          "Class "
              + adapter.getClass().getName()
              + " does not implement any supported type adapter class or interface");
    }
  }

  private void addFactoriesFromAdapter(TypeToken<?> typeToken, Object adapter, boolean hierarchy) {
    if (adapter instanceof JsonSerializer<?> || adapter instanceof JsonDeserializer<?>) {
      if (hierarchy) {
        hierarchyFactories.add(
            com.google.gson.internal.bind.TreeTypeAdapter.newTypeHierarchyFactory(
                typeToken.getRawType(), adapter));
      } else {
        factories.add(
            com.google.gson.internal.bind.TreeTypeAdapter.newFactoryWithMatchRawType(
                typeToken, adapter));
      }
    }
    if (adapter instanceof TypeAdapter<?>) {
      @SuppressWarnings({"unchecked", "rawtypes"})
      TypeAdapterFactory factory =
          hierarchy
              ? com.google.gson.internal.bind.TypeAdapters.newTypeHierarchyFactory(
                  typeToken.getRawType(), (TypeAdapter) adapter)
              : com.google.gson.internal.bind.TypeAdapters.newFactory(
                  typeToken, (TypeAdapter) adapter);
      factories.add(factory);
    }
  }

  // --- package-private accessors ---

  List<TypeAdapterFactory> getFactories() {
    return new ArrayList<>(factories); // copy, not a view
  }

  List<TypeAdapterFactory> getHierarchyFactories() {
    return new ArrayList<>(hierarchyFactories); // copy, not a view
  }

  void addHierarchyFactory(TypeAdapterFactory factory) {
    hierarchyFactories.add(factory);
  }
}
