package com.sms.authlib;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link UserDTO}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code new ImmutableUserDTO.Builder()}.
 */
@Generated(from = "UserDTO", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
@CheckReturnValue
public final class ImmutableUserDTO implements UserDTO {
  private final String userName;
  private final String userId;
  private final String token;
  private final ImmutableSet<String> roles;

  private ImmutableUserDTO(
      String userName,
      String userId,
      String token,
      ImmutableSet<String> roles) {
    this.userName = userName;
    this.userId = userId;
    this.token = token;
    this.roles = roles;
  }

  /**
   * @return The value of the {@code userName} attribute
   */
  @JsonProperty("userName")
  @Override
  public String getUserName() {
    return userName;
  }

  /**
   * @return The value of the {@code userId} attribute
   */
  @JsonProperty("userId")
  @Override
  public String getUserId() {
    return userId;
  }

  /**
   * @return The value of the {@code token} attribute
   */
  @JsonProperty("token")
  @Override
  public String getToken() {
    return token;
  }

  /**
   * @return The value of the {@code roles} attribute
   */
  @JsonProperty("roles")
  @Override
  public ImmutableSet<String> getRoles() {
    return roles;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link UserDTO#getUserName() userName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for userName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableUserDTO withUserName(String value) {
    String newValue = Objects.requireNonNull(value, "userName");
    if (this.userName.equals(newValue)) return this;
    return new ImmutableUserDTO(newValue, this.userId, this.token, this.roles);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link UserDTO#getUserId() userId} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for userId
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableUserDTO withUserId(String value) {
    String newValue = Objects.requireNonNull(value, "userId");
    if (this.userId.equals(newValue)) return this;
    return new ImmutableUserDTO(this.userName, newValue, this.token, this.roles);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link UserDTO#getToken() token} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for token
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableUserDTO withToken(String value) {
    String newValue = Objects.requireNonNull(value, "token");
    if (this.token.equals(newValue)) return this;
    return new ImmutableUserDTO(this.userName, this.userId, newValue, this.roles);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link UserDTO#getRoles() roles}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableUserDTO withRoles(String... elements) {
    ImmutableSet<String> newValue = ImmutableSet.copyOf(elements);
    return new ImmutableUserDTO(this.userName, this.userId, this.token, newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link UserDTO#getRoles() roles}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of roles elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableUserDTO withRoles(Iterable<String> elements) {
    if (this.roles == elements) return this;
    ImmutableSet<String> newValue = ImmutableSet.copyOf(elements);
    return new ImmutableUserDTO(this.userName, this.userId, this.token, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableUserDTO} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableUserDTO
        && equalTo((ImmutableUserDTO) another);
  }

  private boolean equalTo(ImmutableUserDTO another) {
    return userName.equals(another.userName)
        && userId.equals(another.userId)
        && token.equals(another.token)
        && roles.equals(another.roles);
  }

  /**
   * Computes a hash code from attributes: {@code userName}, {@code userId}, {@code token}, {@code roles}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    @Var int h = 5381;
    h += (h << 5) + userName.hashCode();
    h += (h << 5) + userId.hashCode();
    h += (h << 5) + token.hashCode();
    h += (h << 5) + roles.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code UserDTO} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("UserDTO")
        .omitNullValues()
        .add("userName", userName)
        .add("userId", userId)
        .add("token", token)
        .add("roles", roles)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link UserDTO} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable UserDTO instance
   */
  public static ImmutableUserDTO copyOf(UserDTO instance) {
    if (instance instanceof ImmutableUserDTO) {
      return (ImmutableUserDTO) instance;
    }
    return new ImmutableUserDTO.Builder()
        .from(instance)
        .build();
  }

  /**
   * Builds instances of type {@link ImmutableUserDTO ImmutableUserDTO}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "UserDTO", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_USER_NAME = 0x1L;
    private static final long INIT_BIT_USER_ID = 0x2L;
    private static final long INIT_BIT_TOKEN = 0x4L;
    private long initBits = 0x7L;

    private @Nullable String userName;
    private @Nullable String userId;
    private @Nullable String token;
    private ImmutableSet.Builder<String> roles = ImmutableSet.builder();

    /**
     * Creates a builder for {@link ImmutableUserDTO ImmutableUserDTO} instances.
     * <pre>
     * new ImmutableUserDTO.Builder()
     *    .userName(String) // required {@link UserDTO#getUserName() userName}
     *    .userId(String) // required {@link UserDTO#getUserId() userId}
     *    .token(String) // required {@link UserDTO#getToken() token}
     *    .addRoles|addAllRoles(String) // {@link UserDTO#getRoles() roles} elements
     *    .build();
     * </pre>
     */
    public Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code UserDTO} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(UserDTO instance) {
      Objects.requireNonNull(instance, "instance");
      userName(instance.getUserName());
      userId(instance.getUserId());
      token(instance.getToken());
      addAllRoles(instance.getRoles());
      return this;
    }

    /**
     * Initializes the value for the {@link UserDTO#getUserName() userName} attribute.
     * @param userName The value for userName 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("userName")
    public final Builder userName(String userName) {
      this.userName = Objects.requireNonNull(userName, "userName");
      initBits &= ~INIT_BIT_USER_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link UserDTO#getUserId() userId} attribute.
     * @param userId The value for userId 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("userId")
    public final Builder userId(String userId) {
      this.userId = Objects.requireNonNull(userId, "userId");
      initBits &= ~INIT_BIT_USER_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link UserDTO#getToken() token} attribute.
     * @param token The value for token 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("token")
    public final Builder token(String token) {
      this.token = Objects.requireNonNull(token, "token");
      initBits &= ~INIT_BIT_TOKEN;
      return this;
    }

    /**
     * Adds one element to {@link UserDTO#getRoles() roles} set.
     * @param element A roles element
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addRoles(String element) {
      this.roles.add(element);
      return this;
    }

    /**
     * Adds elements to {@link UserDTO#getRoles() roles} set.
     * @param elements An array of roles elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addRoles(String... elements) {
      this.roles.add(elements);
      return this;
    }


    /**
     * Sets or replaces all elements for {@link UserDTO#getRoles() roles} set.
     * @param elements An iterable of roles elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("roles")
    public final Builder roles(Iterable<String> elements) {
      this.roles = ImmutableSet.builder();
      return addAllRoles(elements);
    }

    /**
     * Adds elements to {@link UserDTO#getRoles() roles} set.
     * @param elements An iterable of roles elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addAllRoles(Iterable<String> elements) {
      this.roles.addAll(elements);
      return this;
    }

    /**
     * Builds a new {@link ImmutableUserDTO ImmutableUserDTO}.
     * @return An immutable instance of UserDTO
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableUserDTO build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableUserDTO(userName, userId, token, roles.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_USER_NAME) != 0) attributes.add("userName");
      if ((initBits & INIT_BIT_USER_ID) != 0) attributes.add("userId");
      if ((initBits & INIT_BIT_TOKEN) != 0) attributes.add("token");
      return "Cannot build UserDTO, some of required attributes are not set " + attributes;
    }
  }
}
