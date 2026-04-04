package com.google.gson.internal.bind;

public class BoundFieldConfig {
  private String serializedName;
  private boolean serialize;
  private boolean blockInaccessible;

  public BoundFieldConfig(String serializedName, boolean serialize, boolean blockInaccessible) {
    this.serializedName = serializedName;
    this.serialize = serialize;
    this.blockInaccessible = blockInaccessible;
  }

  public String getSerializedName() {
    return serializedName;
  }

  public void setSerializedName(String serializedName) {
    this.serializedName = serializedName;
  }

  public boolean isSerialize() {
    return serialize;
  }

  public void setSerialize(boolean serialize) {
    this.serialize = serialize;
  }

  public boolean isBlockInaccessible() {
    return blockInaccessible;
  }

  public void setBlockInaccessible(boolean blockInaccessible) {
    this.blockInaccessible = blockInaccessible;
  }
}
