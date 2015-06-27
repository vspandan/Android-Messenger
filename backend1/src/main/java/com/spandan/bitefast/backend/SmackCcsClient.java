package com.spandan.bitefast.backend;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

public class SmackCcsClient {

    public static final String GCM_SERVER = "gcm.googleapis.com";
    public static final int GCM_PORT = 5235;
    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";
    static final String REG_ID_STORE = "gcmchat.txt";
    static final String MESSAGE_KEY = "SM";
    static Random random = new Random();
    Logger logger = Logger.getLogger("SmackCcsClient");
    XMPPConnection connection;
    ConnectionConfiguration config;

    public SmackCcsClient() {
        ProviderManager.getInstance().addExtensionProvider(GCM_ELEMENT_NAME,
                GCM_NAMESPACE, new PacketExtensionProvider() {

                    @Override
                    public PacketExtension parseExtension(XmlPullParser parser)
                            throws Exception {
                        String json = parser.nextText();
                        GcmPacketExtension packet = new GcmPacketExtension(json);
                        return packet;
                    }
                });
    }

    public static String createJsonMessage(String to, String messageId,
                                           Map<String, String> payload, String collapseKey, Long timeToLive,
                                           Boolean delayWhileIdle) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("to", to);
        if (collapseKey != null) {
            message.put("collapse_key", collapseKey);
        }
        if (timeToLive != null) {
            message.put("time_to_live", timeToLive);
        }
        if (delayWhileIdle != null && delayWhileIdle) {
            message.put("delay_while_idle", true);
        }
        message.put("message_id", messageId);
        message.put("data", payload);
        return JSONValue.toJSONString(message);
    }

    public static String createJsonAck(String to, String messageId) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_type", "ack");
        message.put("to", to);
        message.put("message_id", messageId);
        return JSONValue.toJSONString(message);
    }

    public static void main(String[] args) {
        final String userName = "281575560274" + "@gcm.googleapis.com";
        final String password = "AIzaSyDsGU1fLieYvAbLOH_W9voNBrs_q62dpmo";


        SmackCcsClient ccsClient = new SmackCcsClient();

        try {
            ccsClient.connect(userName, password);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }

    public void send(String jsonRequest) {
        Packet request = new GcmPacketExtension(jsonRequest).toPacket();
        connection.sendPacket(request);
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
            String echo = createJsonMessage(from, getRandomMessageId(),
                    payload, collapseKey, null, false);
            send(echo);
        } else if ("SIGNIN".equals(action)) {
                String userName = payload.get("USER_NAME");
                HashMap<String, String> regIdMap = readFromFile();
                System.out.println(regIdMap);

                if (regIdMap==null)
                    regIdMap=new HashMap<String, String>() ;
                else
                    if(regIdMap.keySet().contains(from))
                        regIdMap.remove(from);
                regIdMap.put(from,userName);
                System.out.println(regIdMap);
                writeToFile(regIdMap);
                payload.put("ACTION", "USERLIST");
                payload.put(MESSAGE_KEY, "USERLIST");
                payload.put("USERLIST", extractUsers(regIdMap));

                //TODO Hardcoded for timebeing
                String message = createJsonMessage(retreiveKey("8885551544",regIdMap), getRandomMessageId(),
                        payload, collapseKey, null, false);
                send(message);
        } else if ("USERLIST".equals(action)) {

            HashMap<String, String> regIdMap = readFromFile();

            payload.put(MESSAGE_KEY, "USERLIST");
            payload.put("USERLIST", extractUsers(regIdMap));

            String message = createJsonMessage(from, getRandomMessageId(),
                    payload, collapseKey, null, false);
            send(message);
        } else if ("CHAT".equals(action)) {

            HashMap<String, String> regIdMap = readFromFile();
            payload.put(MESSAGE_KEY, "CHAT");
            System.out.println(regIdMap);
            String toUser = payload.get("TOUSER");
            String toUserRegid = retreiveKey(toUser, regIdMap);
            String message = createJsonMessage(toUserRegid, getRandomMessageId(),
                    payload, collapseKey, null, false);
            send(message);
        }

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
    public void handleAckReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get("message_id").toString();
        String from = jsonObject.get("from").toString();
        logger.log(Level.INFO, "handleAckReceipt() from: " + from
                + ", messageId: " + messageId);
    }

    public void handleNackReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get("message_id").toString();
        String from = jsonObject.get("from").toString();
        logger.log(Level.INFO, "handleNackReceipt() from: " + from
                + ", messageId: " + messageId);
    }

    public void connect(String username, String password) throws XMPPException {
        config = new ConnectionConfiguration(GCM_SERVER, GCM_PORT);
        config.setSecurityMode(SecurityMode.enabled);
        config.setReconnectionAllowed(true);
        config.setRosterLoadedAtLogin(false);
        config.setSendPresence(false);
        config.setSocketFactory(SSLSocketFactory.getDefault());

        // NOTE: Set to true to launch a window with information about packets
        // sent and received
        config.setDebuggerEnabled(true);

        // -Dsmack.debugEnabled=true
        XMPPConnection.DEBUG_ENABLED = true;

        connection = new XMPPConnection(config);
        connection.connect();

        connection.addConnectionListener(new ConnectionListener() {

            @Override
            public void reconnectionSuccessful() {
                logger.info("Reconnecting..");
            }

            @Override
            public void reconnectionFailed(Exception e) {
                logger.log(Level.INFO, "Reconnection failed.. ", e);
            }

            @Override
            public void reconnectingIn(int seconds) {
                logger.log(Level.INFO, "Reconnecting in %d secs", seconds);
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                logger.log(Level.INFO, "Connection closed on error.");
            }

            @Override
            public void connectionClosed() {
                logger.info("Connection closed.");
            }
        });

        // Handle incoming packets
        connection.addPacketListener(new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                logger.log(Level.INFO, "Received: " + packet.toXML());
                Message incomingMessage = (Message) packet;
                GcmPacketExtension gcmPacket = (GcmPacketExtension) incomingMessage
                        .getExtension(GCM_NAMESPACE);
                String json = gcmPacket.getJson();
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> jsonObject = (Map<String, Object>) JSONValue
                            .parseWithException(json);

                    // present for "ack"/"nack", null otherwise
                    Object messageType = jsonObject.get("message_type");

                    if (messageType == null) {
                        // Normal upstream data message
                        handleIncomingDataMessage(jsonObject);

                        // Send ACK to CCS
                        String messageId = jsonObject.get("message_id")
                                .toString();
                        String from = jsonObject.get("from").toString();
                        String ack = createJsonAck(from, messageId);
                        send(ack);
                    } else if ("ack".equals(messageType.toString())) {
                        // Process Ack
                        handleAckReceipt(jsonObject);
                    } else if ("nack".equals(messageType.toString())) {
                        // Process Nack
                        handleNackReceipt(jsonObject);
                    } else {
                        logger.log(Level.WARNING,
                                "Unrecognized message type (%s)",
                                messageType.toString());
                    }
                } catch (ParseException e) {
                    logger.log(Level.SEVERE, "Error parsing JSON " + json, e);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Couldn't send echo.", e);
                }
            }
        }, new PacketTypeFilter(Message.class));

        // Log all outgoing packets
        connection.addPacketInterceptor(new PacketInterceptor() {
            @Override
            public void interceptPacket(Packet packet) {
                logger.log(Level.INFO, "Sent: {0}", packet.toXML());
            }
        }, new PacketTypeFilter(Message.class));

        connection.login(username, password);
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

    class GcmPacketExtension extends DefaultPacketExtension {
        String json;

        public GcmPacketExtension(String json) {
            super(GCM_ELEMENT_NAME, GCM_NAMESPACE);
            this.json = json;
        }

        public String getJson() {
            return json;
        }

        @Override
        public String toXML() {
            return String.format("<%s xmlns=\"%s\">%s</%s>", GCM_ELEMENT_NAME,
                    GCM_NAMESPACE, json, GCM_ELEMENT_NAME);
        }

        @SuppressWarnings("unused")
        public Packet toPacket() {
            return new Message() {
                // Must override toXML() because it includes a <body>
                @Override
                public String toXML() {

                    StringBuilder buf = new StringBuilder();
                    buf.append("<message");
                    if (getXmlns() != null) {
                        buf.append(" xmlns=\"").append(getXmlns()).append("\"");
                    }
                    if (getPacketID() != null) {
                        buf.append(" id=\"").append(getPacketID()).append("\"");
                    }
                    if (getTo() != null) {
                        buf.append(" to=\"")
                                .append(StringUtils.escapeForXML(getTo()))
                                .append("\"");
                    }
                    if (getFrom() != null) {
                        buf.append(" from=\"")
                                .append(StringUtils.escapeForXML(getFrom()))
                                .append("\"");
                    }
                    buf.append(">");
                    buf.append(GcmPacketExtension.this.toXML());
                    buf.append("</message>");
                    return buf.toString();
                }
            };
        }
    }
}