/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.spandan.bitefast.gcmbackend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.spandan.bitefast.gcmbackend.models.User;
import com.spandan.bitefast.gcmbackend.models.UserDetails;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Named;

@Api(name = "messaging", version = "v1", namespace = @ApiNamespace(ownerDomain = "gcmbackend.bitefast.spandan.com", ownerName = "gcmbackend.bitefast.spandan.com", packagePath = ""))

public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    private static Random random = new Random();
    private static final String API_KEY = System.getProperty("gcm.api.key");
    private Sender sender = null;

    @ApiMethod(name = "insertUser", httpMethod = ApiMethod.HttpMethod.POST)
    public void insertUser(@Named("androidId") String androidId, @Named("regId") String regId, @Named("phoneNum") String phoneNum, @Named("name") String name, @Named("email") String email, @Named("addr") String addr, @Named("street") String street, @Named("landmark") String landmark, @Named("city") String city, @Named("isAdmin") boolean isAdmin) throws EntityNotFoundException, IOException {

        Entity customer = new Entity("UserList", phoneNum);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("Phoneno", phoneNum);
        customer.setProperty("Admin", isAdmin);
        customer.setProperty("TimeStamp", new Date());
        if (!findUser(phoneNum))
            datastore.put(customer);
        if (findDeviceId(androidId))
            customer = datastore.get(KeyFactory.createKey("UserAppDetails", androidId));
        else
            customer = new Entity("UserAppDetails", androidId);
        customer.setProperty("AndroidId", androidId);
        customer.setProperty("RegId", regId);
        customer.setProperty("PhoneNum", phoneNum);
        customer.setProperty("Email", email);
        customer.setProperty("Name", name);
        customer.setProperty("Address Line1", addr);
        customer.setProperty("Street", street);
        customer.setProperty("LandMark", landmark);
        customer.setProperty("City", city);
        customer.setProperty("TimeStamp", new Date());
        datastore.put(customer);
        if(findAdminUsers().contains(phoneNum))
            reSendMessages("BITEFAST_ADMIN");
        else
            reSendMessages(phoneNum);
    }

    @ApiMethod(name = "fetchAddress", httpMethod = ApiMethod.HttpMethod.POST)
    public UserDetails fetchDetails(@Named("androidId") String androidId) throws EntityNotFoundException {

        UserDetails user = new UserDetails();
        Entity customer = null;
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        if (findDeviceId(androidId))
            customer = datastore.get(KeyFactory.createKey("UserAppDetails", androidId));
        user.setAndroidID((String) customer.getProperty("AndroidId"));
        user.setRegId((String) customer.getProperty("RegId"));
        user.setPhoneNum((String) customer.getProperty("PhoneNum"));
        user.setEmail((String) customer.getProperty("Email"));
        user.setName((String) customer.getProperty("Name"));
        user.setAddr((String) customer.getProperty("Address Line1"));
        user.setStreet((String) customer.getProperty("Street"));
        user.setLandmark((String) customer.getProperty("LandMark"));
        user.setCity((String) customer.getProperty("City"));
        return user;
    }

    @ApiMethod(name = "updateUserRegid", httpMethod = ApiMethod.HttpMethod.POST)
    public void updateUserRegid(@Named("androidId") String androidId, @Named("regId") String regId, @Named("phoneNum") String phoneNum) throws EntityNotFoundException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity customer = datastore.get(KeyFactory.createKey("UserAppDetails", androidId));
        customer.setProperty("RegId", regId);
        datastore.put(customer);
    }

    @ApiMethod(name = "reSendMessages")
    public void reSendMessages(@Named("touser") String touser) throws IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Delivered",
                        Query.FilterOperator.EQUAL, false
                );

        Query.Filter heightMaxFilter =
                new Query.FilterPredicate("Receiver",
                        Query.FilterOperator.EQUAL, touser
                );
        Query.Filter heightRangeFilter =
                Query.CompositeFilterOperator.and(heightMinFilter, heightMaxFilter);


        Query q = new Query("MessageLog").setFilter(heightRangeFilter);
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(1));
        log.warning(results.toString());
        HashMap<String, String> dataBundle=null;
        for(Entity entity:results) {
            dataBundle = new HashMap<String, String>();
            dataBundle.put("SM", "CHAT");
            dataBundle.put("DEVICEID", (String)entity.getProperty("DeviceId"));
            dataBundle.put("ACTION", "CHAT");
            dataBundle.put("FROM", (String)entity.getProperty("Sender"));
            dataBundle.put("SENDTO", touser);
            dataBundle.put("CHATMESSAGE", (String)entity.getProperty("Msessage"));
            dataBundle.put("MSGTIMESTAMP", (String)entity.getProperty("MsgTimeStamp"));
            dataBundle.put("ID", "" + entity.getProperty("ID"));
            send(touser,dataBundle);
        }
    }

    @ApiMethod(name = "isAdmin")
    public User isAdmin(@Named("phoneNo") String phoneNo) throws IOException {
        User user = new User();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL, phoneNo
                );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String, String> data = new HashMap<String, String>();
        for (Entity result : pq.asIterable()) {
            if (result.getProperty("Phoneno").equals(phoneNo)) {
                user.setUserNum((String) result.getProperty("Phoneno"));
                user.setAdmin((boolean) result.getProperty("Admin"));
            }
        }
        if(user.isAdmin())
            reSendMessages("BITEFAST_ADMIN");
        else
            reSendMessages(phoneNo);
        return user;
    }

    private void saveMessage(@Named("deviceId") String deviceId, @Named("from") String from, @Named("to") String to, @Named("message") String message, @Named("id") String id, @Named("timeStamp") String timeStamp, boolean delivered) {
        Entity customer = new Entity("MessageLog", id);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("DeviceId", deviceId);
        customer.setProperty("Sender", from);
        customer.setProperty("TimeStamp", new Date());
        customer.setProperty("Receiver", to);
        customer.setProperty("Msessage", message);
        customer.setProperty("ID", id);
        customer.setProperty("MsgTimeStamp", timeStamp);
        customer.setProperty("Delivered", delivered);
        datastore.put(customer);
    }

    private void updateMessageStatus(String id) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity customer = datastore.get(KeyFactory.createKey("MessageLog", id));
        customer.setProperty("Delivered", true);
        datastore.put(customer);
    }

    @ApiMethod(name = "saveOrder")
    public void saveOrder(@Named("order") String order, @Named("usr") String usr, @Named("message") String message) {
        Entity customer = new Entity("Orders");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("Order Id", order);
        customer.setProperty("TimeStamp", new Date());
        customer.setProperty("User Phone Num", usr);
        customer.setProperty("Msessage", message);
        datastore.put(customer);
    }


    private boolean findUser(String phoneNo) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL, phoneNo
                );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String, String> data = new HashMap<String, String>();
        for (Entity result : pq.asIterable()) {
            if (result.getProperty("Phoneno").equals(phoneNo))
                return true;
        }
        return false;
    }

    private boolean findDeviceId(String androidId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("AndroidId",
                        Query.FilterOperator.EQUAL, androidId
                );
        Query q = new Query("UserAppDetails").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(1));
        return !results.isEmpty();
    }


    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message) throws IOException, ParseException, EntityNotFoundException {
        if (message == null || message.length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        Map<String, String> jsonObject = (Map<String, String>) JSONValue
                .parseWithException(message);

        String action = jsonObject.get("ACTION");
        String from = jsonObject.get("FROM");
        String devideId = jsonObject.get("DEVICEID");
        String msgId = jsonObject.get("ID");
        String msgTS = jsonObject.get("MSGTIMESTAMP");
        String toUser = jsonObject.get("SENDTO");

        jsonObject.put("ID", msgId);
        jsonObject.put("MSGTIMESTAMP", msgTS);

        if ("ACK".equals(action)) {
            jsonObject.put("SM", "ACK");
            /*saveMessage(devideId, from, toUser, "Ack Msg", msgId, msgTS, true);*/
            //TODO Ack the toUser-DeliveryStatus double tip is updated to actual creator of message(touser);
            updateMessageStatus(msgId);
        } else if ("CHAT".equals(action)) {
            jsonObject.put("SM", "CHAT");
            saveMessage(devideId, from, toUser, jsonObject.get("CHATMESSAGE"), msgId, msgTS, false);

            //Sending ACK to received msg
            HashMap<String,String> dataBundle = new HashMap<String,String>();
            dataBundle.put("DEVICEID", devideId);
            dataBundle.put("ACTION", "ACK");
            dataBundle.put("FROM", "GCM");
            dataBundle.put("SENDTO", from);
            dataBundle.put("ID", ""+msgId);
            dataBundle.put("MSGTIMESTAMP", "" + msgTS);
            send(from,dataBundle);


        }
        send(toUser,jsonObject);
    }

    private void send(String toUser, Map<String, String> jsonObject) throws IOException {
        sender = new Sender(API_KEY);
        HashSet<String> regIds = new HashSet<String>();
        if (toUser.equals("BITEFAST_ADMIN")) {
            HashSet<String> admins = findAdminUsers();
            for (String admin : admins) {
                regIds.addAll(retreiveKey(admin));
            }
        } else {
            regIds.addAll(retreiveKey(toUser));
        }
        Message msg = new Message.Builder().timeToLive(86400)
                .delayWhileIdle(true).setData(jsonObject).build();
        sender.send(msg, new ArrayList<String>(regIds), 5);
    }


    private HashSet<String> retreiveKey(String toUser) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("PhoneNum",
                        Query.FilterOperator.EQUAL, toUser
                );
        Query q = new Query("UserAppDetails").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String, String> data = new HashMap<String, String>();
        HashSet<String> userRegIds = new HashSet<String>();
        for (Entity result : pq.asIterable()) {
            userRegIds.add((String) result.getProperty("RegId"));
        }
        return userRegIds;
    }

    private HashSet<String> findAdminUsers() {
        HashSet<String> admins = new HashSet<String>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Admin",
                        Query.FilterOperator.EQUAL, true
                );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);

        for (Entity result : pq.asIterable()) {
            admins.add((String) result.getProperty("Phoneno"));
        }
        return admins;
    }
}
