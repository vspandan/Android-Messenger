package com.spandan.bitefast.gcmbackend;

/**
 * Created by spandanv on 6/26/2015.
 */
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.MessageType;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler class for all XMPP messages.
 */
public class XmppReceiverServlet extends HttpServlet {
    static Random random = new Random();
    public static final String GCM_SERVER = "gcm.googleapis.com";
    public static final int GCM_PORT = 5235;
    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";
    static final String REG_ID_STORE = "gcmchat.txt";
    static final String MESSAGE_KEY = "SM";
    private static final XMPPService xmppService =
            XMPPServiceFactory.getXMPPService();
    private HashMap<String, String> regIdMap = null;
    private ServletContext application =null;
    public String toXML(String json) {
        return String.format("<%s xmlns=\"%s\">%s</%s>", GCM_ELEMENT_NAME,
                GCM_NAMESPACE, json, GCM_ELEMENT_NAME);
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Message message = xmppService.parseMessage(req);

        application = getServletConfig().getServletContext();
        regIdMap=(HashMap<String,String>)application.getAttribute("regIdMap");

        try {
            MessageType messageType=message.getMessageType();
            Map<String, Object> jsonObject = (Map<String, Object>) JSONValue
                    .parseWithException(message.getBody());
            if (messageType == null) {
                handleIncomingDataMessage(jsonObject);
                String messageId = jsonObject.get("message_id")
                        .toString();
                String from = jsonObject.get("from").toString();
            }
        } catch (ParseException e) {
        } catch (Exception e) {
        }
    }

    private void sendMessage(String recipient, String body) {
        Message message = new MessageBuilder()
                .withRecipientJids(new JID(recipient))
                .withMessageType(MessageType.NORMAL)
                .withBody(body)
                .build();
        xmppService.sendMessage(message);
    }


    public static String createJsonMessage(String messageId,
                                           Map<String, String> payload) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_id", messageId);
        message.put("data", payload);
        return JSONValue.toJSONString(message);
    }

    public String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }
    public String retreiveKey(String toUser,HashMap<String ,String> regIdMap){

        for(Map.Entry<String,String> entry: regIdMap.entrySet()){
            if(toUser.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }
    public String extractUsers(HashMap<String,String> regIdMap){
        String users = "";
        for (Map.Entry<String, String> entry : regIdMap.entrySet()) {
            users = users + entry.getValue() + ":";
        }
        return users;
    }
    public void writeToFile(HashMap regIdMap) {
        try {
            FileOutputStream f = new FileOutputStream(REG_ID_STORE);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(regIdMap);
            s.close();
            f.close();
        }
        catch (Exception e){

        }
    }

    public HashMap<String, String> readFromFile() {
        HashMap<String, String> regIdMap = null;
        try {
            FileInputStream f = new FileInputStream(REG_ID_STORE);
            ObjectInputStream s = new ObjectInputStream(f);
            regIdMap = (HashMap<String, String>) s.readObject();
            s.close();
            f.close();
        } catch (Exception e) {
            System.out.println("Exception");
        }
        return regIdMap;
    }
    public void handleIncomingDataMessage(Map<String, Object> jsonObject) {
        String from = jsonObject.get("from").toString();

        // PackageName of the application that sent this message.
        String category = jsonObject.get("category").toString();

        // Use the packageName as the collapseKey in the echo packet
        String collapseKey = "echo:CollapseKey";
        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) jsonObject
                .get("data");

        String action = payload.get("ACTION");

        if ("ECHO".equals(action)) {

            String clientMessage = payload.get("CLIENT_MESSAGE");
            payload.put(MESSAGE_KEY, "ECHO: " + clientMessage);

            // Send an ECHO response back
            String echo = createJsonMessage(getRandomMessageId(),
                    payload);
            sendMessage(from,echo);
        } else if ("SIGNIN".equals(action)) {
            String userName = payload.get("USER_NAME");
            /*HashMap<String, String> regIdMap = readFromFile();*/
            System.out.println(regIdMap);

            if (regIdMap==null)
                regIdMap=new HashMap<String, String>() ;
            else
            if(regIdMap.keySet().contains(from))
                regIdMap.remove(from);
            regIdMap.put(from,userName);
            System.out.println(regIdMap);
            application.setAttribute("regIdMap",regIdMap);
            //writeToFile(regIdMap);

            payload.put("ACTION", "USERLIST");
            payload.put(MESSAGE_KEY, "USERLIST");
            payload.put("USERLIST", extractUsers(regIdMap));

            //TODO Hardcoded for timebeing
            String message = createJsonMessage(getRandomMessageId(), payload);
            sendMessage(retreiveKey("8885551544",regIdMap),message);
        } else if ("USERLIST".equals(action)) {

            /*HashMap<String, String> regIdMap = readFromFile();*/

            payload.put(MESSAGE_KEY, "USERLIST");
            payload.put("USERLIST", extractUsers(regIdMap));

            String message = createJsonMessage(getRandomMessageId(),payload);
            sendMessage(from,message);
        } else if ("CHAT".equals(action)) {

            /*HashMap<String, String> regIdMap = readFromFile();*/
            payload.put(MESSAGE_KEY, "CHAT");
            System.out.println(regIdMap);
            String toUser = payload.get("TOUSER");
            String toUserRegid = retreiveKey(toUser, regIdMap);
            String message = createJsonMessage(getRandomMessageId(),
                    payload);
            sendMessage(toUserRegid,message);
        }

    }



}