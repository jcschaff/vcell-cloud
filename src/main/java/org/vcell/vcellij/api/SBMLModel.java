/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.vcell.vcellij.api;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-07-13")
public class SBMLModel implements org.apache.thrift.TBase<SBMLModel, SBMLModel._Fields>, java.io.Serializable, Cloneable, Comparable<SBMLModel> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SBMLModel");

  private static final org.apache.thrift.protocol.TField FILEPATH_FIELD_DESC = new org.apache.thrift.protocol.TField("filepath", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SBMLModelStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SBMLModelTupleSchemeFactory();

  public java.lang.String filepath; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FILEPATH((short)1, "filepath");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FILEPATH
          return FILEPATH;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FILEPATH, new org.apache.thrift.meta_data.FieldMetaData("filepath", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "FilePath")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SBMLModel.class, metaDataMap);
  }

  public SBMLModel() {
  }

  public SBMLModel(
    java.lang.String filepath)
  {
    this();
    this.filepath = filepath;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SBMLModel(SBMLModel other) {
    if (other.isSetFilepath()) {
      this.filepath = other.filepath;
    }
  }

  public SBMLModel deepCopy() {
    return new SBMLModel(this);
  }

  @Override
  public void clear() {
    this.filepath = null;
  }

  public java.lang.String getFilepath() {
    return this.filepath;
  }

  public SBMLModel setFilepath(java.lang.String filepath) {
    this.filepath = filepath;
    return this;
  }

  public void unsetFilepath() {
    this.filepath = null;
  }

  /** Returns true if field filepath is set (has been assigned a value) and false otherwise */
  public boolean isSetFilepath() {
    return this.filepath != null;
  }

  public void setFilepathIsSet(boolean value) {
    if (!value) {
      this.filepath = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case FILEPATH:
      if (value == null) {
        unsetFilepath();
      } else {
        setFilepath((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case FILEPATH:
      return getFilepath();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case FILEPATH:
      return isSetFilepath();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof SBMLModel)
      return this.equals((SBMLModel)that);
    return false;
  }

  public boolean equals(SBMLModel that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_filepath = true && this.isSetFilepath();
    boolean that_present_filepath = true && that.isSetFilepath();
    if (this_present_filepath || that_present_filepath) {
      if (!(this_present_filepath && that_present_filepath))
        return false;
      if (!this.filepath.equals(that.filepath))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetFilepath()) ? 131071 : 524287);
    if (isSetFilepath())
      hashCode = hashCode * 8191 + filepath.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(SBMLModel other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetFilepath()).compareTo(other.isSetFilepath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFilepath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.filepath, other.filepath);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("SBMLModel(");
    boolean first = true;

    sb.append("filepath:");
    if (this.filepath == null) {
      sb.append("null");
    } else {
      sb.append(this.filepath);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (filepath == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'filepath' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SBMLModelStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SBMLModelStandardScheme getScheme() {
      return new SBMLModelStandardScheme();
    }
  }

  private static class SBMLModelStandardScheme extends org.apache.thrift.scheme.StandardScheme<SBMLModel> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SBMLModel struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FILEPATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.filepath = iprot.readString();
              struct.setFilepathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SBMLModel struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.filepath != null) {
        oprot.writeFieldBegin(FILEPATH_FIELD_DESC);
        oprot.writeString(struct.filepath);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SBMLModelTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SBMLModelTupleScheme getScheme() {
      return new SBMLModelTupleScheme();
    }
  }

  private static class SBMLModelTupleScheme extends org.apache.thrift.scheme.TupleScheme<SBMLModel> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SBMLModel struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.filepath);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SBMLModel struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.filepath = iprot.readString();
      struct.setFilepathIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

