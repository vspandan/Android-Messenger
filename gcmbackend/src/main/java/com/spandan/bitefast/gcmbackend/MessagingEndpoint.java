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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.spandan.bitefast.gcmbackend.models.RegistrationRecord;

import org.json.simple.JSONValue;

import java.io.IOException;
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
    public void insertUser(@Named("phoneNum") int phoneNum, @Named("isAdmin") boolean isAdmin){
        Key customerKey = KeyFactory.createKey("UserDetails", phoneNum);
        Entity customer = new Entity("Customer", customerKey);
        customer.setProperty("phone no", phoneNum);
        customer.setProperty("isadmin", isAdmin);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(customer);
    }

    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message,@Named("redId") String regId) throws IOException {
        if (message == null || message.length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }

        sender = new Sender(API_KEY);

        try {
            Map<String, String> jsonObject = (Map<String, String>) JSONValue
                    .parseWithException(message);
            handleIncomingDataMessage(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void send(String regId,Map<String, String>  message) throws  IOException{
        Message msg = new Message.Builder().setData(message).build();

        Result result = sender.send(msg, regId, 5);
        if (result.getMessageId() != null) {
            log.info("Message sent to " + regId);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                //TODO re-impl for timebeing commenting
                    /*log.info("Registration Id changed for " + regId + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();*/
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                //TODO re-impl for timebeing commenting
                    /*log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    ofy().save().entity(record).now();*/
            } else {
                log.warning("Error when sending message : " + error);
            }
        }
    }

    private static String createJsonMessage(String messageId,
                                            Map<String, String> jsonObject) {
        /*Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_id", messageId);
        message.put("data", jsonObject);
        return JSONValue.toJSONString(message);*/
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
