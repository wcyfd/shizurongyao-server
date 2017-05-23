// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Match.proto

package com.randioo.shizurongyao_server.protocol;

public final class Match {
  private Match() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class SCMatchComplete extends
      com.google.protobuf.GeneratedMessage {
    // Use SCMatchComplete.newBuilder() to construct.
    private SCMatchComplete() {
      initFields();
    }
    private SCMatchComplete(boolean noInit) {}
    
    private static final SCMatchComplete defaultInstance;
    public static SCMatchComplete getDefaultInstance() {
      return defaultInstance;
    }
    
    public SCMatchComplete getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.randioo.shizurongyao_server.protocol.Match.internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.randioo.shizurongyao_server.protocol.Match.internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_fieldAccessorTable;
    }
    
    // optional int32 index = 1;
    public static final int INDEX_FIELD_NUMBER = 1;
    private boolean hasIndex;
    private int index_ = 0;
    public boolean hasIndex() { return hasIndex; }
    public int getIndex() { return index_; }
    
    // optional int32 mapsId = 2;
    public static final int MAPSID_FIELD_NUMBER = 2;
    private boolean hasMapsId;
    private int mapsId_ = 0;
    public boolean hasMapsId() { return hasMapsId; }
    public int getMapsId() { return mapsId_; }
    
    // repeated .com.randioo.shizurongyao_server.protocol.MatchRoleInfo matchRoleInfos = 3;
    public static final int MATCHROLEINFOS_FIELD_NUMBER = 3;
    private java.util.List<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo> matchRoleInfos_ =
      java.util.Collections.emptyList();
    public java.util.List<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo> getMatchRoleInfosList() {
      return matchRoleInfos_;
    }
    public int getMatchRoleInfosCount() { return matchRoleInfos_.size(); }
    public com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo getMatchRoleInfos(int index) {
      return matchRoleInfos_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasIndex()) {
        output.writeInt32(1, getIndex());
      }
      if (hasMapsId()) {
        output.writeInt32(2, getMapsId());
      }
      for (com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo element : getMatchRoleInfosList()) {
        output.writeMessage(3, element);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasIndex()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, getIndex());
      }
      if (hasMapsId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, getMapsId());
      }
      for (com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo element : getMatchRoleInfosList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, element);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete result;
      
      // Construct using com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete();
        return builder;
      }
      
      protected com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.getDescriptor();
      }
      
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete getDefaultInstanceForType() {
        return com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.matchRoleInfos_ != java.util.Collections.EMPTY_LIST) {
          result.matchRoleInfos_ =
            java.util.Collections.unmodifiableList(result.matchRoleInfos_);
        }
        com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete) {
          return mergeFrom((com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete other) {
        if (other == com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.getDefaultInstance()) return this;
        if (other.hasIndex()) {
          setIndex(other.getIndex());
        }
        if (other.hasMapsId()) {
          setMapsId(other.getMapsId());
        }
        if (!other.matchRoleInfos_.isEmpty()) {
          if (result.matchRoleInfos_.isEmpty()) {
            result.matchRoleInfos_ = new java.util.ArrayList<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo>();
          }
          result.matchRoleInfos_.addAll(other.matchRoleInfos_);
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 8: {
              setIndex(input.readInt32());
              break;
            }
            case 16: {
              setMapsId(input.readInt32());
              break;
            }
            case 26: {
              com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo.Builder subBuilder = com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addMatchRoleInfos(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // optional int32 index = 1;
      public boolean hasIndex() {
        return result.hasIndex();
      }
      public int getIndex() {
        return result.getIndex();
      }
      public Builder setIndex(int value) {
        result.hasIndex = true;
        result.index_ = value;
        return this;
      }
      public Builder clearIndex() {
        result.hasIndex = false;
        result.index_ = 0;
        return this;
      }
      
      // optional int32 mapsId = 2;
      public boolean hasMapsId() {
        return result.hasMapsId();
      }
      public int getMapsId() {
        return result.getMapsId();
      }
      public Builder setMapsId(int value) {
        result.hasMapsId = true;
        result.mapsId_ = value;
        return this;
      }
      public Builder clearMapsId() {
        result.hasMapsId = false;
        result.mapsId_ = 0;
        return this;
      }
      
      // repeated .com.randioo.shizurongyao_server.protocol.MatchRoleInfo matchRoleInfos = 3;
      public java.util.List<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo> getMatchRoleInfosList() {
        return java.util.Collections.unmodifiableList(result.matchRoleInfos_);
      }
      public int getMatchRoleInfosCount() {
        return result.getMatchRoleInfosCount();
      }
      public com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo getMatchRoleInfos(int index) {
        return result.getMatchRoleInfos(index);
      }
      public Builder setMatchRoleInfos(int index, com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.matchRoleInfos_.set(index, value);
        return this;
      }
      public Builder setMatchRoleInfos(int index, com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo.Builder builderForValue) {
        result.matchRoleInfos_.set(index, builderForValue.build());
        return this;
      }
      public Builder addMatchRoleInfos(com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.matchRoleInfos_.isEmpty()) {
          result.matchRoleInfos_ = new java.util.ArrayList<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo>();
        }
        result.matchRoleInfos_.add(value);
        return this;
      }
      public Builder addMatchRoleInfos(com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo.Builder builderForValue) {
        if (result.matchRoleInfos_.isEmpty()) {
          result.matchRoleInfos_ = new java.util.ArrayList<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo>();
        }
        result.matchRoleInfos_.add(builderForValue.build());
        return this;
      }
      public Builder addAllMatchRoleInfos(
          java.lang.Iterable<? extends com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo> values) {
        if (result.matchRoleInfos_.isEmpty()) {
          result.matchRoleInfos_ = new java.util.ArrayList<com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo>();
        }
        super.addAll(values, result.matchRoleInfos_);
        return this;
      }
      public Builder clearMatchRoleInfos() {
        result.matchRoleInfos_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.randioo.shizurongyao_server.protocol.SCMatchComplete)
    }
    
    static {
      defaultInstance = new SCMatchComplete(true);
      com.randioo.shizurongyao_server.protocol.Match.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.randioo.shizurongyao_server.protocol.SCMatchComplete)
  }
  
  public static final class SCMatchCancel extends
      com.google.protobuf.GeneratedMessage {
    // Use SCMatchCancel.newBuilder() to construct.
    private SCMatchCancel() {
      initFields();
    }
    private SCMatchCancel(boolean noInit) {}
    
    private static final SCMatchCancel defaultInstance;
    public static SCMatchCancel getDefaultInstance() {
      return defaultInstance;
    }
    
    public SCMatchCancel getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.randioo.shizurongyao_server.protocol.Match.internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.randioo.shizurongyao_server.protocol.Match.internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_fieldAccessorTable;
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel result;
      
      // Construct using com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel();
        return builder;
      }
      
      protected com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.getDescriptor();
      }
      
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel getDefaultInstanceForType() {
        return com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel) {
          return mergeFrom((com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel other) {
        if (other == com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.getDefaultInstance()) return this;
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
          }
        }
      }
      
      
      // @@protoc_insertion_point(builder_scope:com.randioo.shizurongyao_server.protocol.SCMatchCancel)
    }
    
    static {
      defaultInstance = new SCMatchCancel(true);
      com.randioo.shizurongyao_server.protocol.Match.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.randioo.shizurongyao_server.protocol.SCMatchCancel)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Match.proto\022(com.randioo.shizurongyao_" +
      "server.protocol\032\014Entity.proto\"\201\001\n\017SCMatc" +
      "hComplete\022\r\n\005index\030\001 \001(\005\022\016\n\006mapsId\030\002 \001(\005" +
      "\022O\n\016matchRoleInfos\030\003 \003(\01327.com.randioo.s" +
      "hizurongyao_server.protocol.MatchRoleInf" +
      "o\"\017\n\rSCMatchCancel"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_randioo_shizurongyao_server_protocol_SCMatchComplete_descriptor,
              new java.lang.String[] { "Index", "MapsId", "MatchRoleInfos", },
              com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.class,
              com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete.Builder.class);
          internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_randioo_shizurongyao_server_protocol_SCMatchCancel_descriptor,
              new java.lang.String[] { },
              com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.class,
              com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.randioo.shizurongyao_server.protocol.Entity.getDescriptor(),
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
