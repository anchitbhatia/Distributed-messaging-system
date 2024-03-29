// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: protos/heartbeat.proto

package messages;

public final class HeartBeat {
  private HeartBeat() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface HeartBeatMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:HeartBeatMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.NodeDetails node = 1;</code>
     * @return Whether the node field is set.
     */
    boolean hasNode();
    /**
     * <code>.NodeDetails node = 1;</code>
     * @return The node.
     */
    messages.Node.NodeDetails getNode();
    /**
     * <code>.NodeDetails node = 1;</code>
     */
    messages.Node.NodeDetailsOrBuilder getNodeOrBuilder();

    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    java.util.List<messages.Node.NodeDetails> 
        getMembersList();
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    messages.Node.NodeDetails getMembers(int index);
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    int getMembersCount();
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    java.util.List<? extends messages.Node.NodeDetailsOrBuilder> 
        getMembersOrBuilderList();
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    messages.Node.NodeDetailsOrBuilder getMembersOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code HeartBeatMessage}
   */
  public static final class HeartBeatMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:HeartBeatMessage)
      HeartBeatMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use HeartBeatMessage.newBuilder() to construct.
    private HeartBeatMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private HeartBeatMessage() {
      members_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new HeartBeatMessage();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private HeartBeatMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
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
              messages.Node.NodeDetails.Builder subBuilder = null;
              if (node_ != null) {
                subBuilder = node_.toBuilder();
              }
              node_ = input.readMessage(messages.Node.NodeDetails.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(node_);
                node_ = subBuilder.buildPartial();
              }

              break;
            }
            case 18: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                members_ = new java.util.ArrayList<messages.Node.NodeDetails>();
                mutable_bitField0_ |= 0x00000001;
              }
              members_.add(
                  input.readMessage(messages.Node.NodeDetails.parser(), extensionRegistry));
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
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          members_ = java.util.Collections.unmodifiableList(members_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return messages.HeartBeat.internal_static_HeartBeatMessage_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return messages.HeartBeat.internal_static_HeartBeatMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              messages.HeartBeat.HeartBeatMessage.class, messages.HeartBeat.HeartBeatMessage.Builder.class);
    }

    public static final int NODE_FIELD_NUMBER = 1;
    private messages.Node.NodeDetails node_;
    /**
     * <code>.NodeDetails node = 1;</code>
     * @return Whether the node field is set.
     */
    @java.lang.Override
    public boolean hasNode() {
      return node_ != null;
    }
    /**
     * <code>.NodeDetails node = 1;</code>
     * @return The node.
     */
    @java.lang.Override
    public messages.Node.NodeDetails getNode() {
      return node_ == null ? messages.Node.NodeDetails.getDefaultInstance() : node_;
    }
    /**
     * <code>.NodeDetails node = 1;</code>
     */
    @java.lang.Override
    public messages.Node.NodeDetailsOrBuilder getNodeOrBuilder() {
      return getNode();
    }

    public static final int MEMBERS_FIELD_NUMBER = 2;
    private java.util.List<messages.Node.NodeDetails> members_;
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    @java.lang.Override
    public java.util.List<messages.Node.NodeDetails> getMembersList() {
      return members_;
    }
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    @java.lang.Override
    public java.util.List<? extends messages.Node.NodeDetailsOrBuilder> 
        getMembersOrBuilderList() {
      return members_;
    }
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    @java.lang.Override
    public int getMembersCount() {
      return members_.size();
    }
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    @java.lang.Override
    public messages.Node.NodeDetails getMembers(int index) {
      return members_.get(index);
    }
    /**
     * <code>repeated .NodeDetails members = 2;</code>
     */
    @java.lang.Override
    public messages.Node.NodeDetailsOrBuilder getMembersOrBuilder(
        int index) {
      return members_.get(index);
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
      if (node_ != null) {
        output.writeMessage(1, getNode());
      }
      for (int i = 0; i < members_.size(); i++) {
        output.writeMessage(2, members_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (node_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getNode());
      }
      for (int i = 0; i < members_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, members_.get(i));
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
      if (!(obj instanceof messages.HeartBeat.HeartBeatMessage)) {
        return super.equals(obj);
      }
      messages.HeartBeat.HeartBeatMessage other = (messages.HeartBeat.HeartBeatMessage) obj;

      if (hasNode() != other.hasNode()) return false;
      if (hasNode()) {
        if (!getNode()
            .equals(other.getNode())) return false;
      }
      if (!getMembersList()
          .equals(other.getMembersList())) return false;
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
      if (hasNode()) {
        hash = (37 * hash) + NODE_FIELD_NUMBER;
        hash = (53 * hash) + getNode().hashCode();
      }
      if (getMembersCount() > 0) {
        hash = (37 * hash) + MEMBERS_FIELD_NUMBER;
        hash = (53 * hash) + getMembersList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static messages.HeartBeat.HeartBeatMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static messages.HeartBeat.HeartBeatMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static messages.HeartBeat.HeartBeatMessage parseFrom(
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
    public static Builder newBuilder(messages.HeartBeat.HeartBeatMessage prototype) {
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
     * Protobuf type {@code HeartBeatMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:HeartBeatMessage)
        messages.HeartBeat.HeartBeatMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return messages.HeartBeat.internal_static_HeartBeatMessage_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return messages.HeartBeat.internal_static_HeartBeatMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                messages.HeartBeat.HeartBeatMessage.class, messages.HeartBeat.HeartBeatMessage.Builder.class);
      }

      // Construct using messages.HeartBeat.HeartBeatMessage.newBuilder()
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
          getMembersFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (nodeBuilder_ == null) {
          node_ = null;
        } else {
          node_ = null;
          nodeBuilder_ = null;
        }
        if (membersBuilder_ == null) {
          members_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          membersBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return messages.HeartBeat.internal_static_HeartBeatMessage_descriptor;
      }

      @java.lang.Override
      public messages.HeartBeat.HeartBeatMessage getDefaultInstanceForType() {
        return messages.HeartBeat.HeartBeatMessage.getDefaultInstance();
      }

      @java.lang.Override
      public messages.HeartBeat.HeartBeatMessage build() {
        messages.HeartBeat.HeartBeatMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public messages.HeartBeat.HeartBeatMessage buildPartial() {
        messages.HeartBeat.HeartBeatMessage result = new messages.HeartBeat.HeartBeatMessage(this);
        int from_bitField0_ = bitField0_;
        if (nodeBuilder_ == null) {
          result.node_ = node_;
        } else {
          result.node_ = nodeBuilder_.build();
        }
        if (membersBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            members_ = java.util.Collections.unmodifiableList(members_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.members_ = members_;
        } else {
          result.members_ = membersBuilder_.build();
        }
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
        if (other instanceof messages.HeartBeat.HeartBeatMessage) {
          return mergeFrom((messages.HeartBeat.HeartBeatMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(messages.HeartBeat.HeartBeatMessage other) {
        if (other == messages.HeartBeat.HeartBeatMessage.getDefaultInstance()) return this;
        if (other.hasNode()) {
          mergeNode(other.getNode());
        }
        if (membersBuilder_ == null) {
          if (!other.members_.isEmpty()) {
            if (members_.isEmpty()) {
              members_ = other.members_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureMembersIsMutable();
              members_.addAll(other.members_);
            }
            onChanged();
          }
        } else {
          if (!other.members_.isEmpty()) {
            if (membersBuilder_.isEmpty()) {
              membersBuilder_.dispose();
              membersBuilder_ = null;
              members_ = other.members_;
              bitField0_ = (bitField0_ & ~0x00000001);
              membersBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getMembersFieldBuilder() : null;
            } else {
              membersBuilder_.addAllMessages(other.members_);
            }
          }
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
        messages.HeartBeat.HeartBeatMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (messages.HeartBeat.HeartBeatMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private messages.Node.NodeDetails node_;
      private com.google.protobuf.SingleFieldBuilderV3<
          messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder> nodeBuilder_;
      /**
       * <code>.NodeDetails node = 1;</code>
       * @return Whether the node field is set.
       */
      public boolean hasNode() {
        return nodeBuilder_ != null || node_ != null;
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       * @return The node.
       */
      public messages.Node.NodeDetails getNode() {
        if (nodeBuilder_ == null) {
          return node_ == null ? messages.Node.NodeDetails.getDefaultInstance() : node_;
        } else {
          return nodeBuilder_.getMessage();
        }
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public Builder setNode(messages.Node.NodeDetails value) {
        if (nodeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          node_ = value;
          onChanged();
        } else {
          nodeBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public Builder setNode(
          messages.Node.NodeDetails.Builder builderForValue) {
        if (nodeBuilder_ == null) {
          node_ = builderForValue.build();
          onChanged();
        } else {
          nodeBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public Builder mergeNode(messages.Node.NodeDetails value) {
        if (nodeBuilder_ == null) {
          if (node_ != null) {
            node_ =
              messages.Node.NodeDetails.newBuilder(node_).mergeFrom(value).buildPartial();
          } else {
            node_ = value;
          }
          onChanged();
        } else {
          nodeBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public Builder clearNode() {
        if (nodeBuilder_ == null) {
          node_ = null;
          onChanged();
        } else {
          node_ = null;
          nodeBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public messages.Node.NodeDetails.Builder getNodeBuilder() {
        
        onChanged();
        return getNodeFieldBuilder().getBuilder();
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      public messages.Node.NodeDetailsOrBuilder getNodeOrBuilder() {
        if (nodeBuilder_ != null) {
          return nodeBuilder_.getMessageOrBuilder();
        } else {
          return node_ == null ?
              messages.Node.NodeDetails.getDefaultInstance() : node_;
        }
      }
      /**
       * <code>.NodeDetails node = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder> 
          getNodeFieldBuilder() {
        if (nodeBuilder_ == null) {
          nodeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder>(
                  getNode(),
                  getParentForChildren(),
                  isClean());
          node_ = null;
        }
        return nodeBuilder_;
      }

      private java.util.List<messages.Node.NodeDetails> members_ =
        java.util.Collections.emptyList();
      private void ensureMembersIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          members_ = new java.util.ArrayList<messages.Node.NodeDetails>(members_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder> membersBuilder_;

      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public java.util.List<messages.Node.NodeDetails> getMembersList() {
        if (membersBuilder_ == null) {
          return java.util.Collections.unmodifiableList(members_);
        } else {
          return membersBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public int getMembersCount() {
        if (membersBuilder_ == null) {
          return members_.size();
        } else {
          return membersBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public messages.Node.NodeDetails getMembers(int index) {
        if (membersBuilder_ == null) {
          return members_.get(index);
        } else {
          return membersBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder setMembers(
          int index, messages.Node.NodeDetails value) {
        if (membersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureMembersIsMutable();
          members_.set(index, value);
          onChanged();
        } else {
          membersBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder setMembers(
          int index, messages.Node.NodeDetails.Builder builderForValue) {
        if (membersBuilder_ == null) {
          ensureMembersIsMutable();
          members_.set(index, builderForValue.build());
          onChanged();
        } else {
          membersBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder addMembers(messages.Node.NodeDetails value) {
        if (membersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureMembersIsMutable();
          members_.add(value);
          onChanged();
        } else {
          membersBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder addMembers(
          int index, messages.Node.NodeDetails value) {
        if (membersBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureMembersIsMutable();
          members_.add(index, value);
          onChanged();
        } else {
          membersBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder addMembers(
          messages.Node.NodeDetails.Builder builderForValue) {
        if (membersBuilder_ == null) {
          ensureMembersIsMutable();
          members_.add(builderForValue.build());
          onChanged();
        } else {
          membersBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder addMembers(
          int index, messages.Node.NodeDetails.Builder builderForValue) {
        if (membersBuilder_ == null) {
          ensureMembersIsMutable();
          members_.add(index, builderForValue.build());
          onChanged();
        } else {
          membersBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder addAllMembers(
          java.lang.Iterable<? extends messages.Node.NodeDetails> values) {
        if (membersBuilder_ == null) {
          ensureMembersIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, members_);
          onChanged();
        } else {
          membersBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder clearMembers() {
        if (membersBuilder_ == null) {
          members_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          membersBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public Builder removeMembers(int index) {
        if (membersBuilder_ == null) {
          ensureMembersIsMutable();
          members_.remove(index);
          onChanged();
        } else {
          membersBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public messages.Node.NodeDetails.Builder getMembersBuilder(
          int index) {
        return getMembersFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public messages.Node.NodeDetailsOrBuilder getMembersOrBuilder(
          int index) {
        if (membersBuilder_ == null) {
          return members_.get(index);  } else {
          return membersBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public java.util.List<? extends messages.Node.NodeDetailsOrBuilder> 
           getMembersOrBuilderList() {
        if (membersBuilder_ != null) {
          return membersBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(members_);
        }
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public messages.Node.NodeDetails.Builder addMembersBuilder() {
        return getMembersFieldBuilder().addBuilder(
            messages.Node.NodeDetails.getDefaultInstance());
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public messages.Node.NodeDetails.Builder addMembersBuilder(
          int index) {
        return getMembersFieldBuilder().addBuilder(
            index, messages.Node.NodeDetails.getDefaultInstance());
      }
      /**
       * <code>repeated .NodeDetails members = 2;</code>
       */
      public java.util.List<messages.Node.NodeDetails.Builder> 
           getMembersBuilderList() {
        return getMembersFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder> 
          getMembersFieldBuilder() {
        if (membersBuilder_ == null) {
          membersBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              messages.Node.NodeDetails, messages.Node.NodeDetails.Builder, messages.Node.NodeDetailsOrBuilder>(
                  members_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          members_ = null;
        }
        return membersBuilder_;
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


      // @@protoc_insertion_point(builder_scope:HeartBeatMessage)
    }

    // @@protoc_insertion_point(class_scope:HeartBeatMessage)
    private static final messages.HeartBeat.HeartBeatMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new messages.HeartBeat.HeartBeatMessage();
    }

    public static messages.HeartBeat.HeartBeatMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<HeartBeatMessage>
        PARSER = new com.google.protobuf.AbstractParser<HeartBeatMessage>() {
      @java.lang.Override
      public HeartBeatMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new HeartBeatMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<HeartBeatMessage> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<HeartBeatMessage> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public messages.HeartBeat.HeartBeatMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_HeartBeatMessage_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_HeartBeatMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026protos/heartbeat.proto\032\021protos/node.pr" +
      "oto\"M\n\020HeartBeatMessage\022\032\n\004node\030\001 \001(\0132\014." +
      "NodeDetails\022\035\n\007members\030\002 \003(\0132\014.NodeDetai" +
      "lsB\025\n\010messagesB\tHeartBeatb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          messages.Node.getDescriptor(),
        });
    internal_static_HeartBeatMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_HeartBeatMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_HeartBeatMessage_descriptor,
        new java.lang.String[] { "Node", "Members", });
    messages.Node.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
