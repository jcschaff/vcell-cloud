/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.vcell.vcellij.api;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum SimulationState implements org.apache.thrift.TEnum {
  running(0),
  done(1),
  failed(2);

  private final int value;

  private SimulationState(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static SimulationState findByValue(int value) { 
    switch (value) {
      case 0:
        return running;
      case 1:
        return done;
      case 2:
        return failed;
      default:
        return null;
    }
  }
}
