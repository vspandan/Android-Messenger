/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.spandan.bitefast.gcmbackend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.spandan.bitefast.gcmbackend.models.RegistrationRecord;
import com.spandan.bitefast.gcmbackend.models.User;

import org.json.simple.JSONValue;

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

import static com.spandan.bitefast.gcmbackend.OfyService.ofy;

@Api(name = "messaging", version = "v1", namespace = @ApiNamespace(ownerDomain = "gcmbackend.bitefast.spandan.com", ownerName = "gcmbackend.bitefast.spandan.com", packagePath = ""))

public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    private static Random random = new Random();
    private static final String API_KEY = System.getProperty("gcm.api.key");
    private Sender sender=null;

    @ApiMethod(name = "insertUser",httpMethod = ApiMethod.HttpMethod.POST)
    public void insertUser(@Named("regId") String regId, @Named("phoneNum") String phoneNum, @Named("name") String name, @Named("email") String email, @Named("addr") String addr, @Named("street") String street, @Named("landmark") String landmark, @Named("city") String city, @Named("isAdmin") boolean isAdmin){

        Entity customer = new Entity("UserList", phoneNum);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("Phoneno", phoneNum);
        customer.setProperty("Admin", isAdmin);
        customer.setProperty("TimeStamp", new Date());
        if(!findUser(phoneNum))
            datastore.put(customer);
        customer = new Entity("UserAppDetails");
        customer.setProperty("RegId", regId);
        customer.setProperty("isAdmin", false);
        customer.setProperty("PhoneNum", phoneNum);
        customer.setProperty("Email", email);
        customer.setProperty("Name", name);
        customer.setProperty("Address Line1", addr);
        customer.setProperty("Street", street);
        customer.setProperty("LandMark", landmark);
        customer.setProperty("City", city);
        customer.setProperty("TimeStamp",new Date());
        datastore.put(customer);
    }

    @ApiMethod(name = "isAdmin")
    public User isAdmin(@Named("phoneNo") String phoneNo){
        User user=new User();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL,phoneNo
                );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String,String> data=new HashMap<String,String>();
        for (Entity result : pq.asIterable()) {
            if(result.getProperty("Phoneno").equals(phoneNo))
            {
                user.setUserNum((String) result.getProperty("Phoneno"));
                user.setAdmin((boolean)result.getProperty("Admin"));
            }
        }
        return user;
    }

    @ApiMethod(name = "saveMessage")
    public void saveMessage(@Named("regId") String regId, @Named("from") String from, @Named("to") String to, @Named("message") String message)
    {
        Entity customer = new Entity("MessageLog");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("Source RegId", regId);
        customer.setProperty("Sender", from);
        customer.setProperty("TimeStamp", new Date());
        customer.setProperty("Receiver",to);
        customer.setProperty("Msessage",message);
        datastore.put(customer);
    }

    @ApiMethod(name = "saveOrder")
    public void saveOrder(@Named("order") String order, @Named("usr") String usr, @Named("message") String message)
    {
        Entity customer = new Entity("Orders");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("Order Id", order);
        customer.setProperty("TimeStamp", new Date());
        customer.setProperty("User Phone Num",usr);
        customer.setProperty("Msessage",message);
        datastore.put(customer);
    }


    private boolean findUser(String phoneNo) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL,phoneNo
                        );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String,String> data=new HashMap<String,String>();
        for (Entity result : pq.asIterable()) {
            if(result.getProperty("Phoneno").equals(phoneNo))
                return true;
        }
        return false;
    }


    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message,@Named("redId") String regId) throws IOException {
        if (message == null || message.length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        try {
            Map<String, String> jsonObject = (Map<String, String>) JSONValue
                    .parseWithException(message);
            handleIncomingDataMessage(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void send(String regId,Map<String, String>  message) throws  IOException{
        sender = new Sender(API_KEY);
        Message msg = new Message.Builder().timeToLive(0)
                .collapseKey("0")
                .delayWhileIdle(false).setData(message).build();
        sender.send(msg, regId, 5);
    }

    private void send(ArrayList<String> regIdArray,Map<String, String>  message) throws  IOException{
        sender = new Sender(API_KEY);
        Message msg = new Message.Builder().timeToLive(0)
                .collapseKey("0")
                .delayWhileIdle(false).setData(message).build();
        sender.send(msg, regIdArray, 5);
    }

    private static String createJsonMessage(String messageId,
                                            Map<String, String> jsonObject) {
        return JSONValue.toJSONString(jsonObject);
    }

    private String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }

    @ApiMethod(name = "register")
    public void register(@Named("regId") String regId, @Named("phn") String phn){
        Entity customer = new Entity("RegistrationDetails", regId);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        customer.setProperty("RegId", regId);
        customer.setProperty("Phoneno", phn);
        customer.setProperty("TimeStamp", new Date());
        datastore.put(customer);
    }

    private String retreiveKey_ (String toUser){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL,toUser
                );
        Query q = new Query("RegistrationDetails").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String,String> data=new HashMap<String,String>();
        HashSet<String> userRegIds=new HashSet<String>();
        for (Entity result : pq.asIterable()) {
            userRegIds.add((String) result.getProperty("RegId"));
            return(String) result.getProperty("RegId");
        }
        return "";
    }

    private HashSet<String> retreiveKey(String toUser){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL,toUser
                );
        Query q = new Query("RegistrationDetails").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String,String> data=new HashMap<String,String>();
        HashSet<String> userRegIds=new HashSet<String>();
        for (Entity result : pq.asIterable()) {
            userRegIds.add((String) result.getProperty("RegId"));
        }
        return userRegIds;
    }

    //TODO Implement this
    private String extractUsers(){
        String users = "";
        return users;
    }


    private void handleIncomingDataMessage(Map<String, String> jsonObject) throws IOException {
        @SuppressWarnings("unchecked")

        String action = jsonObject.get("ACTION");
        String toUser="";
        String message="";

        if ("USERLIST".equals(action)) {
            HashSet<String> admins = findAdminUsers();
            jsonObject.put("SM", "USERLIST");
            jsonObject.put("USERLIST", extractUsers());
            for(String admin:admins){
                HashSet<String> regIds=retreiveKey(admin);
                send(new ArrayList<String>(regIds), jsonObject);
            }
        } else if ("CHAT".equals(action)) {
            jsonObject.put("SM", "CHAT");
            toUser = jsonObject.get("SENDTO");
            message = createJsonMessage(getRandomMessageId(),
                    jsonObject);

            if (toUser.equals("BITEFAST_ADMIN")) {
                HashSet<String> admins = findAdminUsers();
                HashSet<String> regIds=new HashSet<String>();
                for (String admin: admins) {
                    regIds.addAll(retreiveKey(admin));
                }
                send(new ArrayList<String>(regIds), jsonObject);
            }
            else {
                HashSet<String> regIds=retreiveKey(toUser);
                send(new ArrayList<String>(regIds), jsonObject);
            }

        }
    }

    private HashSet<String> findAdminUsers() {
        HashSet<String> admins=new HashSet<String>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Admin",
                        Query.FilterOperator.EQUAL,true
                );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);

        for (Entity result : pq.asIterable()) {
            admins.add((String)result.getProperty("Phoneno"));
        }
        return admins;
    }
}
