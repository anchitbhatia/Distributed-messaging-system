// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: protos/node.proto

package messages;

public final class Node {
  private Node() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface NodeDetailsOrBuilder extends
      // @@protoc_insertion_point(interface_extends:NodeDetails)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string hostName = 1;</code>
     * @return The hostName.
     */
    java.lang.String getHostName();
    /**
     * <code>string hostName = 1;</code>
     * @return The bytes for hostName.
     */
    com.google.protobuf.ByteString
        getHostNameBytes();

    /**
     * <code>int32 port = 2;</code>
     * @return The port.
     */
    int getPort();

    /**
     * <code>int32 id = 3;</code>
     * @return The id.
     */
    int getId();
  }
  /**
   * Protobuf type {@code NodeDetails}
   */
  public static final class NodeDetails extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:NodeDetails)
      NodeDetailsOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use NodeDetails.newBuilder() to construct.
    private NodeDetails(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private NodeDetails() {
      hostName_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new NodeDetails();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private NodeDetails(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              hostName_ = s;
              break;
            }
            case 16: {

              port_ = input.readInt32();
              break;
            }
            case 24: {

              id_ = input.readInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return messages.Node.internal_static_NodeDetails_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return messages.Node.internal_static_NodeDetails_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              messages.Node.NodeDetails.class, messages.Node.NodeDetails.Builder.class);
    }

    public static final int HOSTNAME_FIELD_NUMBER = 1;
    private volatile java.lang.Object hostName_;
    /**
     * <code>string hostName = 1;</code>
     * @return The hostName.
     */
    @java.lang.Override
    public java.lang.String getHostName() {
      java.lang.Object ref = hostName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        hostName_ = s;
        return s;
      }
    }
    /**
     * <code>string hostName = 1;</code>
     * @return The bytes for hostName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getHostNameBytes() {
      java.lang.Object ref = hostName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        hostName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int PORT_FIELD_NUMBER = 2;
    private int port_;
    /**
     * <code>int32 port = 2;</code>
     * @return The port.
     */
    @java.lang.Override
    public int getPort() {
      return port_;
    }

    public static final int ID_FIELD_NUMBER = 3;
    private int id_;
    /**
     * <code>int32 id = 3;</code>
     * @return The id.
     */
    @java.lang.Override
    public int getId() {
      return id_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(hostName_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, hostName_);
      }
      if (port_ != 0) {
        output.writeInt32(2, port_);
      }
      if (id_ != 0) {
        output.writeInt32(3, id_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(hostName_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, hostName_);
      }
      if (port_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, port_);
      }
      if (id_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, id_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof messages.Node.NodeDetails)) {
        return super.equals(obj);
      }
      messages.Node.NodeDetails other = (messages.Node.NodeDetails) obj;

      if (!getHostName()
          .equals(other.getHostName())) return false;
      if (getPort()
          != other.getPort()) return false;
      if (getId()
          != other.getId()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + HOSTNAME_FIELD_NUMBER;
      hash = (53 * hash) + getHostName().hashCode();
      hash = (37 * hash) + PORT_FIELD_NUMBER;
      hash = (53 * hash) + getPort();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getId();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static messages.Node.NodeDetails parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.Node.NodeDetails parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.Node.NodeDetails parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.Node.NodeDetails parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.Node.NodeDetails parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.Node.NodeDetails parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.Node.NodeDetails parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static messages.Node.NodeDetails parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static messages.Node.NodeDetails parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static messages.Node.NodeDetails parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static messages.Node.NodeDetails parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static messages.Node.NodeDetails parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(messages.Node.NodeDetails prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code NodeDetails}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:NodeDetails)
        messages.Node.NodeDetailsOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return messages.Node.internal_static_NodeDetails_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return messages.Node.internal_static_NodeDetails_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                messages.Node.NodeDetails.class, messages.Node.NodeDetails.Builder.class);
      }

      // Construct using messages.Node.NodeDetails.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        hostName_ = "";

        port_ = 0;

        id_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return messages.Node.internal_static_NodeDetails_descriptor;
      }

      @java.lang.Override
      public messages.Node.NodeDetails getDefaultInstanceForType() {
        return messages.Node.NodeDetails.getDefaultInstance();
      }

      @java.lang.Override
      public messages.Node.NodeDetails build() {
        messages.Node.NodeDetails result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public messages.Node.NodeDetails buildPartial() {
        messages.Node.NodeDetails result = new messages.Node.NodeDetails(this);
        result.hostName_ = hostName_;
        result.port_ = port_;
        result.id_ = id_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof messages.Node.NodeDetails) {
          return mergeFrom((messages.Node.NodeDetails)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(messages.Node.NodeDetails other) {
        if (other == messages.Node.NodeDetails.getDefaultInstance()) return this;
        if (!other.getHostName().isEmpty()) {
          hostName_ = other.hostName_;
          onChanged();
        }
        if (other.getPort() != 0) {
          setPort(other.getPort());
        }
        if (other.getId() != 0) {
          setId(other.getId());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        messages.Node.NodeDetails parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (messages.Node.NodeDetails) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object hostName_ = "";
      /**
       * <code>string hostName = 1;</code>
       * @return The hostName.
       */
      public java.lang.String getHostName() {
        java.lang.Object ref = hostName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          hostName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string hostName = 1;</code>
       * @return The bytes for hostName.
       */
      public com.google.protobuf.ByteString
          getHostNameBytes() {
        java.lang.Object ref = hostName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          hostName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string hostName = 1;</code>
       * @param value The hostName to set.
       * @return This builder for chaining.
       */
      public Builder setHostName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        hostName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string hostName = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearHostName() {
        
        hostName_ = getDefaultInstance().getHostName();
        onChanged();
        return this;
      }
      /**
       * <code>string hostName = 1;</code>
       * @param value The bytes for hostName to set.
       * @return This builder for chaining.
       */
      public Builder setHostNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        hostName_ = value;
        onChanged();
        return this;
      }

      private int port_ ;
      /**
       * <code>int32 port = 2;</code>
       * @return The port.
       */
      @java.lang.Override
      public int getPort() {
        return port_;
      }
      /**
       * <code>int32 port = 2;</code>
       * @param value The port to set.
       * @return This builder for chaining.
       */
      public Builder setPort(int value) {
        
        port_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 port = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearPort() {
        
        port_ = 0;
        onChanged();
        return this;
      }

      private int id_ ;
      /**
       * <code>int32 id = 3;</code>
       * @return The id.
       */
      @java.lang.Override
      public int getId() {
        return id_;
      }
      /**
       * <code>int32 id = 3;</code>
       * @param value The id to set.
       * @return This builder for chaining.
       */
      public Builder setId(int value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 id = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearId() {
        
        id_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:NodeDetails)
    }

    // @@protoc_insertion_point(class_scope:NodeDetails)
    private static final messages.Node.NodeDetails DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new messages.Node.NodeDetails();
    }

    public static messages.Node.NodeDetails getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<NodeDetails>
        PARSER = new com.google.protobuf.AbstractParser<NodeDetails>() {
      @java.lang.Override
      public NodeDetails parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NodeDetails(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<NodeDetails> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<NodeDetails> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public messages.Node.NodeDetails getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_NodeDetails_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_NodeDetails_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021protos/node.proto\"9\n\013NodeDetails\022\020\n\010ho" +
      "stName\030\001 \001(\t\022\014\n\004port\030\002 \001(\005\022\n\n\002id\030\003 \001(\005B\020" +
      "\n\010messagesB\004Nodeb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_NodeDetails_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_NodeDetails_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_NodeDetails_descriptor,
        new java.lang.String[] { "HostName", "Port", "Id", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
