/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.spandan.bitefast.gcmbackend;

import com.google.android.gcm.server.Constants;
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

import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

    @ApiMethod(name = "insertUser")
    public void insertUser(@Named("phoneNum") String phoneNum, @Named("isAdmin") boolean isAdmin){
        Entity customer = new Entity("UserList", phoneNum);
        customer.setProperty("Phoneno", phoneNum);
        customer.setProperty("Admin", isAdmin);
        customer.setProperty("TimeStamp", new Date());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(customer);

    }

    @ApiMethod(name = "insertAppUser")
    public void insertAppUser(@Named("regId") String regId, @Named("phoneNum") String phoneNum, @Named("name") String name, @Named("email") String email, @Named("addr") String addr, @Named("street") String street, @Named("landmark") String landmark, @Named("city") String city){
        Entity customer = new Entity("UserAppDetails");
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
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(customer);
    }

    @ApiMethod(name="findUser")
    public void findUser(@Named("regid") String regid,@Named("phoneNo") String phoneNo) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter heightMinFilter =
                new Query.FilterPredicate("Phoneno",
                        Query.FilterOperator.EQUAL,phoneNo
                        );
        Query q = new Query("UserList").setFilter(heightMinFilter);
        PreparedQuery pq = datastore.prepare(q);
        Map<String,String> data=new HashMap<String,String>();
        for (Entity result : pq.asIterable()) {
            data.put("Phoneno", (String) result.getProperty("Phoneno"));
            if((Boolean) result.getProperty("Admin"))
                data.put("Admin","1");
            else
                data.put("Admin","0");
        }
        try {
            data.put("ACTION","FINDUSER");
            send(regid,data);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Message msg = new Message.Builder().setData(message).build();
        Result result = sender.send(msg, regId, 5);
    }

    private static String createJsonMessage(String messageId,
                                            Map<String, String> jsonObject) {
        return JSONValue.toJSONString(jsonObject);
    }

    private String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }

    private String retreiveKey(String toUser){
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
        for (RegistrationRecord record : records) {
            if(toUser.equals(record.getPhoneNum())){
                return record.getRegId();
            }
        }
        return null;
    }

    private String extractUsers(){
        String users = "";
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).list();
        for (RegistrationRecord record : records) {
            users = users + record.getPhoneNum() + ":";
        }
        return users;
    }

    private void handleIncomingDataMessage(Map<String, String> jsonObject) throws IOException {
        @SuppressWarnings("unchecked")

        String action = jsonObject.get("ACTION");

        if ("USERLIST".equals(action)) {
            jsonObject.put("SM", "USERLIST");
            jsonObject.put("USERLIST", extractUsers());
            String message = createJsonMessage(getRandomMessageId(),jsonObject);
            send(retreiveKey("8885551544"),jsonObject);
        } else if ("CHAT".equals(action)) {
            jsonObject.put("SM", "CHAT");
            String toUser = jsonObject.get("TOUSER");
            String message = createJsonMessage(getRandomMessageId(),
                    jsonObject);
            send(retreiveKey(toUser),jsonObject);
        }
    }
    private static String createJsonAck(String to, String messageId) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_type", "ack");
        message.put("to", to);
        message.put("message_id", messageId);
        return JSONValue.toJSONString(message);
    }
}
