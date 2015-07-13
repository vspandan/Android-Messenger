/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-06-30 18:20:40 UTC)
 * on 2015-07-13 at 17:16:40 UTC 
 * Modify at your own risk.
 */

package com.spandan.bitefast.gcmbackend.messaging;

/**
 * Service definition for Messaging (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MessagingRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Messaging extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.20.0 of the messaging library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://bitefastmobileapp.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "messaging/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Messaging(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Messaging(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "fetchAddress".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link FetchAddress#execute()} method to invoke the remote operation.
   *
   * @param androidId
   * @return the request
   */
  public FetchAddress fetchAddress(java.lang.String androidId) throws java.io.IOException {
    FetchAddress result = new FetchAddress(androidId);
    initialize(result);
    return result;
  }

  public class FetchAddress extends MessagingRequest<com.spandan.bitefast.gcmbackend.messaging.model.UserDetails> {

    private static final String REST_PATH = "fetchDetails/{androidId}";

    /**
     * Create a request for the method "fetchAddress".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link FetchAddress#execute()} method to invoke the remote
     * operation. <p> {@link
     * FetchAddress#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param androidId
     * @since 1.13
     */
    protected FetchAddress(java.lang.String androidId) {
      super(Messaging.this, "POST", REST_PATH, null, com.spandan.bitefast.gcmbackend.messaging.model.UserDetails.class);
      this.androidId = com.google.api.client.util.Preconditions.checkNotNull(androidId, "Required parameter androidId must be specified.");
    }

    @Override
    public FetchAddress setAlt(java.lang.String alt) {
      return (FetchAddress) super.setAlt(alt);
    }

    @Override
    public FetchAddress setFields(java.lang.String fields) {
      return (FetchAddress) super.setFields(fields);
    }

    @Override
    public FetchAddress setKey(java.lang.String key) {
      return (FetchAddress) super.setKey(key);
    }

    @Override
    public FetchAddress setOauthToken(java.lang.String oauthToken) {
      return (FetchAddress) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchAddress setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchAddress) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchAddress setQuotaUser(java.lang.String quotaUser) {
      return (FetchAddress) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchAddress setUserIp(java.lang.String userIp) {
      return (FetchAddress) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String androidId;

    /**

     */
    public java.lang.String getAndroidId() {
      return androidId;
    }

    public FetchAddress setAndroidId(java.lang.String androidId) {
      this.androidId = androidId;
      return this;
    }

    @Override
    public FetchAddress set(String parameterName, Object value) {
      return (FetchAddress) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertUser".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link InsertUser#execute()} method to invoke the remote operation.
   *
   * @param androidId
   * @param regId
   * @param phoneNum
   * @param name
   * @param email
   * @param addr
   * @param street
   * @param landmark
   * @param city
   * @param isAdmin
   * @return the request
   */
  public InsertUser insertUser(java.lang.String androidId, java.lang.String regId, java.lang.String phoneNum, java.lang.String name, java.lang.String email, java.lang.String addr, java.lang.String street, java.lang.String landmark, java.lang.String city, java.lang.Boolean isAdmin) throws java.io.IOException {
    InsertUser result = new InsertUser(androidId, regId, phoneNum, name, email, addr, street, landmark, city, isAdmin);
    initialize(result);
    return result;
  }

  public class InsertUser extends MessagingRequest<Void> {

    private static final String REST_PATH = "void/{androidId}/{regId}/{phoneNum}/{name}/{email}/{addr}/{street}/{landmark}/{city}/{isAdmin}";

    /**
     * Create a request for the method "insertUser".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link InsertUser#execute()} method to invoke the remote
     * operation. <p> {@link
     * InsertUser#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param androidId
     * @param regId
     * @param phoneNum
     * @param name
     * @param email
     * @param addr
     * @param street
     * @param landmark
     * @param city
     * @param isAdmin
     * @since 1.13
     */
    protected InsertUser(java.lang.String androidId, java.lang.String regId, java.lang.String phoneNum, java.lang.String name, java.lang.String email, java.lang.String addr, java.lang.String street, java.lang.String landmark, java.lang.String city, java.lang.Boolean isAdmin) {
      super(Messaging.this, "POST", REST_PATH, null, Void.class);
      this.androidId = com.google.api.client.util.Preconditions.checkNotNull(androidId, "Required parameter androidId must be specified.");
      this.regId = com.google.api.client.util.Preconditions.checkNotNull(regId, "Required parameter regId must be specified.");
      this.phoneNum = com.google.api.client.util.Preconditions.checkNotNull(phoneNum, "Required parameter phoneNum must be specified.");
      this.name = com.google.api.client.util.Preconditions.checkNotNull(name, "Required parameter name must be specified.");
      this.email = com.google.api.client.util.Preconditions.checkNotNull(email, "Required parameter email must be specified.");
      this.addr = com.google.api.client.util.Preconditions.checkNotNull(addr, "Required parameter addr must be specified.");
      this.street = com.google.api.client.util.Preconditions.checkNotNull(street, "Required parameter street must be specified.");
      this.landmark = com.google.api.client.util.Preconditions.checkNotNull(landmark, "Required parameter landmark must be specified.");
      this.city = com.google.api.client.util.Preconditions.checkNotNull(city, "Required parameter city must be specified.");
      this.isAdmin = com.google.api.client.util.Preconditions.checkNotNull(isAdmin, "Required parameter isAdmin must be specified.");
    }

    @Override
    public InsertUser setAlt(java.lang.String alt) {
      return (InsertUser) super.setAlt(alt);
    }

    @Override
    public InsertUser setFields(java.lang.String fields) {
      return (InsertUser) super.setFields(fields);
    }

    @Override
    public InsertUser setKey(java.lang.String key) {
      return (InsertUser) super.setKey(key);
    }

    @Override
    public InsertUser setOauthToken(java.lang.String oauthToken) {
      return (InsertUser) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertUser setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertUser) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertUser setQuotaUser(java.lang.String quotaUser) {
      return (InsertUser) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertUser setUserIp(java.lang.String userIp) {
      return (InsertUser) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String androidId;

    /**

     */
    public java.lang.String getAndroidId() {
      return androidId;
    }

    public InsertUser setAndroidId(java.lang.String androidId) {
      this.androidId = androidId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String regId;

    /**

     */
    public java.lang.String getRegId() {
      return regId;
    }

    public InsertUser setRegId(java.lang.String regId) {
      this.regId = regId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String phoneNum;

    /**

     */
    public java.lang.String getPhoneNum() {
      return phoneNum;
    }

    public InsertUser setPhoneNum(java.lang.String phoneNum) {
      this.phoneNum = phoneNum;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String name;

    /**

     */
    public java.lang.String getName() {
      return name;
    }

    public InsertUser setName(java.lang.String name) {
      this.name = name;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String email;

    /**

     */
    public java.lang.String getEmail() {
      return email;
    }

    public InsertUser setEmail(java.lang.String email) {
      this.email = email;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String addr;

    /**

     */
    public java.lang.String getAddr() {
      return addr;
    }

    public InsertUser setAddr(java.lang.String addr) {
      this.addr = addr;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String street;

    /**

     */
    public java.lang.String getStreet() {
      return street;
    }

    public InsertUser setStreet(java.lang.String street) {
      this.street = street;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String landmark;

    /**

     */
    public java.lang.String getLandmark() {
      return landmark;
    }

    public InsertUser setLandmark(java.lang.String landmark) {
      this.landmark = landmark;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String city;

    /**

     */
    public java.lang.String getCity() {
      return city;
    }

    public InsertUser setCity(java.lang.String city) {
      this.city = city;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Boolean isAdmin;

    /**

     */
    public java.lang.Boolean getIsAdmin() {
      return isAdmin;
    }

    public InsertUser setIsAdmin(java.lang.Boolean isAdmin) {
      this.isAdmin = isAdmin;
      return this;
    }

    @Override
    public InsertUser set(String parameterName, Object value) {
      return (InsertUser) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "isAdmin".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link IsAdmin#execute()} method to invoke the remote operation.
   *
   * @param phoneNo
   * @return the request
   */
  public IsAdmin isAdmin(java.lang.String phoneNo) throws java.io.IOException {
    IsAdmin result = new IsAdmin(phoneNo);
    initialize(result);
    return result;
  }

  public class IsAdmin extends MessagingRequest<com.spandan.bitefast.gcmbackend.messaging.model.User> {

    private static final String REST_PATH = "isAdmin/{phoneNo}";

    /**
     * Create a request for the method "isAdmin".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link IsAdmin#execute()} method to invoke the remote operation.
     * <p> {@link
     * IsAdmin#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param phoneNo
     * @since 1.13
     */
    protected IsAdmin(java.lang.String phoneNo) {
      super(Messaging.this, "POST", REST_PATH, null, com.spandan.bitefast.gcmbackend.messaging.model.User.class);
      this.phoneNo = com.google.api.client.util.Preconditions.checkNotNull(phoneNo, "Required parameter phoneNo must be specified.");
    }

    @Override
    public IsAdmin setAlt(java.lang.String alt) {
      return (IsAdmin) super.setAlt(alt);
    }

    @Override
    public IsAdmin setFields(java.lang.String fields) {
      return (IsAdmin) super.setFields(fields);
    }

    @Override
    public IsAdmin setKey(java.lang.String key) {
      return (IsAdmin) super.setKey(key);
    }

    @Override
    public IsAdmin setOauthToken(java.lang.String oauthToken) {
      return (IsAdmin) super.setOauthToken(oauthToken);
    }

    @Override
    public IsAdmin setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (IsAdmin) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public IsAdmin setQuotaUser(java.lang.String quotaUser) {
      return (IsAdmin) super.setQuotaUser(quotaUser);
    }

    @Override
    public IsAdmin setUserIp(java.lang.String userIp) {
      return (IsAdmin) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String phoneNo;

    /**

     */
    public java.lang.String getPhoneNo() {
      return phoneNo;
    }

    public IsAdmin setPhoneNo(java.lang.String phoneNo) {
      this.phoneNo = phoneNo;
      return this;
    }

    @Override
    public IsAdmin set(String parameterName, Object value) {
      return (IsAdmin) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "reSendMessages".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link ReSendMessages#execute()} method to invoke the remote operation.
   *
   * @param touser
   * @return the request
   */
  public ReSendMessages reSendMessages(java.lang.String touser) throws java.io.IOException {
    ReSendMessages result = new ReSendMessages(touser);
    initialize(result);
    return result;
  }

  public class ReSendMessages extends MessagingRequest<Void> {

    private static final String REST_PATH = "reSendMessages/{touser}";

    /**
     * Create a request for the method "reSendMessages".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link ReSendMessages#execute()} method to invoke the remote
     * operation. <p> {@link ReSendMessages#initialize(com.google.api.client.googleapis.services.Abstr
     * actGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param touser
     * @since 1.13
     */
    protected ReSendMessages(java.lang.String touser) {
      super(Messaging.this, "POST", REST_PATH, null, Void.class);
      this.touser = com.google.api.client.util.Preconditions.checkNotNull(touser, "Required parameter touser must be specified.");
    }

    @Override
    public ReSendMessages setAlt(java.lang.String alt) {
      return (ReSendMessages) super.setAlt(alt);
    }

    @Override
    public ReSendMessages setFields(java.lang.String fields) {
      return (ReSendMessages) super.setFields(fields);
    }

    @Override
    public ReSendMessages setKey(java.lang.String key) {
      return (ReSendMessages) super.setKey(key);
    }

    @Override
    public ReSendMessages setOauthToken(java.lang.String oauthToken) {
      return (ReSendMessages) super.setOauthToken(oauthToken);
    }

    @Override
    public ReSendMessages setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ReSendMessages) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ReSendMessages setQuotaUser(java.lang.String quotaUser) {
      return (ReSendMessages) super.setQuotaUser(quotaUser);
    }

    @Override
    public ReSendMessages setUserIp(java.lang.String userIp) {
      return (ReSendMessages) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String touser;

    /**

     */
    public java.lang.String getTouser() {
      return touser;
    }

    public ReSendMessages setTouser(java.lang.String touser) {
      this.touser = touser;
      return this;
    }

    @Override
    public ReSendMessages set(String parameterName, Object value) {
      return (ReSendMessages) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "saveOrder".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link SaveOrder#execute()} method to invoke the remote operation.
   *
   * @param order
   * @param usr
   * @param message
   * @return the request
   */
  public SaveOrder saveOrder(java.lang.String order, java.lang.String usr, java.lang.String message) throws java.io.IOException {
    SaveOrder result = new SaveOrder(order, usr, message);
    initialize(result);
    return result;
  }

  public class SaveOrder extends MessagingRequest<Void> {

    private static final String REST_PATH = "saveOrder/{order}/{usr}/{message}";

    /**
     * Create a request for the method "saveOrder".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link SaveOrder#execute()} method to invoke the remote
     * operation. <p> {@link
     * SaveOrder#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param order
     * @param usr
     * @param message
     * @since 1.13
     */
    protected SaveOrder(java.lang.String order, java.lang.String usr, java.lang.String message) {
      super(Messaging.this, "POST", REST_PATH, null, Void.class);
      this.order = com.google.api.client.util.Preconditions.checkNotNull(order, "Required parameter order must be specified.");
      this.usr = com.google.api.client.util.Preconditions.checkNotNull(usr, "Required parameter usr must be specified.");
      this.message = com.google.api.client.util.Preconditions.checkNotNull(message, "Required parameter message must be specified.");
    }

    @Override
    public SaveOrder setAlt(java.lang.String alt) {
      return (SaveOrder) super.setAlt(alt);
    }

    @Override
    public SaveOrder setFields(java.lang.String fields) {
      return (SaveOrder) super.setFields(fields);
    }

    @Override
    public SaveOrder setKey(java.lang.String key) {
      return (SaveOrder) super.setKey(key);
    }

    @Override
    public SaveOrder setOauthToken(java.lang.String oauthToken) {
      return (SaveOrder) super.setOauthToken(oauthToken);
    }

    @Override
    public SaveOrder setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SaveOrder) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SaveOrder setQuotaUser(java.lang.String quotaUser) {
      return (SaveOrder) super.setQuotaUser(quotaUser);
    }

    @Override
    public SaveOrder setUserIp(java.lang.String userIp) {
      return (SaveOrder) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String order;

    /**

     */
    public java.lang.String getOrder() {
      return order;
    }

    public SaveOrder setOrder(java.lang.String order) {
      this.order = order;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String usr;

    /**

     */
    public java.lang.String getUsr() {
      return usr;
    }

    public SaveOrder setUsr(java.lang.String usr) {
      this.usr = usr;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String message;

    /**

     */
    public java.lang.String getMessage() {
      return message;
    }

    public SaveOrder setMessage(java.lang.String message) {
      this.message = message;
      return this;
    }

    @Override
    public SaveOrder set(String parameterName, Object value) {
      return (SaveOrder) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "sendMessage".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link SendMessage#execute()} method to invoke the remote operation.
   *
   * @param message
   * @return the request
   */
  public SendMessage sendMessage(java.lang.String message) throws java.io.IOException {
    SendMessage result = new SendMessage(message);
    initialize(result);
    return result;
  }

  public class SendMessage extends MessagingRequest<Void> {

    private static final String REST_PATH = "sendMessage/{message}";

    /**
     * Create a request for the method "sendMessage".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link SendMessage#execute()} method to invoke the remote
     * operation. <p> {@link
     * SendMessage#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param message
     * @since 1.13
     */
    protected SendMessage(java.lang.String message) {
      super(Messaging.this, "POST", REST_PATH, null, Void.class);
      this.message = com.google.api.client.util.Preconditions.checkNotNull(message, "Required parameter message must be specified.");
    }

    @Override
    public SendMessage setAlt(java.lang.String alt) {
      return (SendMessage) super.setAlt(alt);
    }

    @Override
    public SendMessage setFields(java.lang.String fields) {
      return (SendMessage) super.setFields(fields);
    }

    @Override
    public SendMessage setKey(java.lang.String key) {
      return (SendMessage) super.setKey(key);
    }

    @Override
    public SendMessage setOauthToken(java.lang.String oauthToken) {
      return (SendMessage) super.setOauthToken(oauthToken);
    }

    @Override
    public SendMessage setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (SendMessage) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public SendMessage setQuotaUser(java.lang.String quotaUser) {
      return (SendMessage) super.setQuotaUser(quotaUser);
    }

    @Override
    public SendMessage setUserIp(java.lang.String userIp) {
      return (SendMessage) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String message;

    /**

     */
    public java.lang.String getMessage() {
      return message;
    }

    public SendMessage setMessage(java.lang.String message) {
      this.message = message;
      return this;
    }

    @Override
    public SendMessage set(String parameterName, Object value) {
      return (SendMessage) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateUserRegid".
   *
   * This request holds the parameters needed by the messaging server.  After setting any optional
   * parameters, call the {@link UpdateUserRegid#execute()} method to invoke the remote operation.
   *
   * @param androidId
   * @param regId
   * @param phoneNum
   * @return the request
   */
  public UpdateUserRegid updateUserRegid(java.lang.String androidId, java.lang.String regId, java.lang.String phoneNum) throws java.io.IOException {
    UpdateUserRegid result = new UpdateUserRegid(androidId, regId, phoneNum);
    initialize(result);
    return result;
  }

  public class UpdateUserRegid extends MessagingRequest<Void> {

    private static final String REST_PATH = "void/{androidId}/{regId}/{phoneNum}";

    /**
     * Create a request for the method "updateUserRegid".
     *
     * This request holds the parameters needed by the the messaging server.  After setting any
     * optional parameters, call the {@link UpdateUserRegid#execute()} method to invoke the remote
     * operation. <p> {@link UpdateUserRegid#initialize(com.google.api.client.googleapis.services.Abst
     * ractGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param androidId
     * @param regId
     * @param phoneNum
     * @since 1.13
     */
    protected UpdateUserRegid(java.lang.String androidId, java.lang.String regId, java.lang.String phoneNum) {
      super(Messaging.this, "POST", REST_PATH, null, Void.class);
      this.androidId = com.google.api.client.util.Preconditions.checkNotNull(androidId, "Required parameter androidId must be specified.");
      this.regId = com.google.api.client.util.Preconditions.checkNotNull(regId, "Required parameter regId must be specified.");
      this.phoneNum = com.google.api.client.util.Preconditions.checkNotNull(phoneNum, "Required parameter phoneNum must be specified.");
    }

    @Override
    public UpdateUserRegid setAlt(java.lang.String alt) {
      return (UpdateUserRegid) super.setAlt(alt);
    }

    @Override
    public UpdateUserRegid setFields(java.lang.String fields) {
      return (UpdateUserRegid) super.setFields(fields);
    }

    @Override
    public UpdateUserRegid setKey(java.lang.String key) {
      return (UpdateUserRegid) super.setKey(key);
    }

    @Override
    public UpdateUserRegid setOauthToken(java.lang.String oauthToken) {
      return (UpdateUserRegid) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateUserRegid setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateUserRegid) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateUserRegid setQuotaUser(java.lang.String quotaUser) {
      return (UpdateUserRegid) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateUserRegid setUserIp(java.lang.String userIp) {
      return (UpdateUserRegid) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String androidId;

    /**

     */
    public java.lang.String getAndroidId() {
      return androidId;
    }

    public UpdateUserRegid setAndroidId(java.lang.String androidId) {
      this.androidId = androidId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String regId;

    /**

     */
    public java.lang.String getRegId() {
      return regId;
    }

    public UpdateUserRegid setRegId(java.lang.String regId) {
      this.regId = regId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String phoneNum;

    /**

     */
    public java.lang.String getPhoneNum() {
      return phoneNum;
    }

    public UpdateUserRegid setPhoneNum(java.lang.String phoneNum) {
      this.phoneNum = phoneNum;
      return this;
    }

    @Override
    public UpdateUserRegid set(String parameterName, Object value) {
      return (UpdateUserRegid) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Messaging}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Messaging}. */
    @Override
    public Messaging build() {
      return new Messaging(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MessagingRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMessagingRequestInitializer(
        MessagingRequestInitializer messagingRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(messagingRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
