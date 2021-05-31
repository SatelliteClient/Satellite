package com.github.satellite.utils;

public final class WVec3 {
  private final double xCoord;
  
  private final double yCoord;
  
  private final double zCoord;
  
  public WVec3(double xCoord, double yCoord, double zCoord) {
    this.xCoord = xCoord;
    this.yCoord = yCoord;
    this.zCoord = zCoord;
  }
  
  public final double getXCoord() {
    return this.xCoord;
  }
  
  public final double getYCoord() {
    return this.yCoord;
  }
  
  public final double getZCoord() {
    return this.zCoord;
  }
  
  public final WVec3 addVector(double x, double y, double z) {
    return new WVec3(getXCoord() + x, getYCoord() + y, getZCoord() + z);
  }
  
  public final double distanceTo(WVec3 vec) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;
    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
    return Math.sqrt(d3);
  }
  
  public final double squareDistanceTo(WVec3 vec) {
    double d0 = vec.getXCoord() - getXCoord();
    double d1 = vec.getYCoord() - getYCoord();
    double d2 = vec.getZCoord() - getZCoord();
    return d0 * d0 + d1 * d1 + d2 * d2;
  }
  
  public final WVec3 add(WVec3 vec) {
    WVec3 wVec3 = this;
    double d1 = vec.getXCoord(), d2 = vec.getYCoord(), z$iv = vec.getZCoord();
    return 
      
      new WVec3(wVec3.getXCoord() + d1, wVec3.getYCoord() + d2, wVec3.getZCoord() + z$iv);
  }
  
  public final WVec3 rotatePitch(float pitch) {
    float f = (float)Math.cos(pitch);
    float f1 = (float)Math.sin(pitch);
    double d0 = this.xCoord;
    double d1 = this.yCoord * f + this.zCoord * f1;
    double d2 = this.zCoord * f - this.yCoord * f1;
    return new WVec3(d0, d1, d2);
  }
  
  public final WVec3 rotateYaw(float yaw) {
    float f = (float)Math.cos(yaw);
    float f1 = (float)Math.sin(yaw);
    double d0 = this.xCoord * f + this.zCoord * f1;
    double d1 = this.yCoord;
    double d2 = this.zCoord * f - this.xCoord * f1;
    return new WVec3(d0, d1, d2);
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (this.xCoord != ((WVec3)other).xCoord)
      return false; 
    if (this.yCoord != ((WVec3)other).yCoord)
      return false; 
    if (this.zCoord != ((WVec3)other).zCoord)
      return false; 
    return true;
  }
  
  public int hashCode() {
    int result = Double.hashCode(this.xCoord);
    result = 31 * result + Double.hashCode(this.yCoord);
    result = 31 * result + Double.hashCode(this.zCoord);
    return result;
  }
}
