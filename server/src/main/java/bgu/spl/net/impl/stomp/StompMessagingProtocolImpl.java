package bgu.spl.net.impl.stomp;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Connections;

public class StompMessagingProtocolImpl implements StompMessagingProtocol<Message> {
    private boolean shouldTerminate;
    private ConnectionsImpl<Message> connections;
    private int connectionId;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl)connections;
        shouldTerminate = false;
    }

    @Override
    public Message process(Message msg) {
        String type = msg.getType();
        switch (type) {
            case "CONNECT":
                return processConnect(msg);
            case "SEND":
                return processSend(msg);
            case "SUBSCRIBE":
                return processSubscribe(msg);
            case "UNSUBSCRIBE":
                return processUnsubscribe(msg);
            case "DISCONNECT":
                return processDisconnect(msg);
            default:
                return createError(msg, "Invalid command");
        }
    }

    public Message processConnect(Message msg) {
        //Process the CONNECT command
        String username = msg.getHeader("login");
        String password = msg.getHeader("passcode");
        if (username == null || password == null) {
            return createError(msg, "Missing username or password header");
        }
        if (connections.isLoggedIn(username)){
            return createError(msg, "User already logged in");
        }
        if (!connections.checkUserPass(username, password)) {
            return createError(msg, "Wrong password");
        }
        //Connect the user
        connections.connect(connectionId, username, password);
        //Send a CONNECTED message
        Message response = new Message("CONNECTED");
        response.addHeader("version", "1.2");
        return response;
    }

    public Message processSend(Message msg) {
        //Process the SEND command
        String destination = msg.getHeader("destination");
        String body = msg.getBody();
        if (destination == null || body == null) {
            return createError(msg, "Missing destination or body header");
        }
        if (!connections.isLoggedIn(connections.getUsername(connectionId))) {
            return createError(msg, "User not logged in");
        }
        //Send the message to all subscribers of the destination
        Message toSend = new Message("MESSAGE");
        toSend.addHeader("destination", destination);
        toSend.setBody(body);
        connections.send(destination,toSend);
        //Send a RECEIPT to the sender
        Message response = new Message("RECEIPT");
        response.addHeader("receipt-id", msg.getHeader("receipt"));
        return response;
    }

    public Message processSubscribe(Message msg) {
        //Process the SUBSCRIBE command
        String destination = msg.getHeader("destination");
        String id = msg.getHeader("id");
        if (destination == null || id == null) {
            return createError(msg, "Missing destination or id header");
        }
        if (!connections.isLoggedIn(connections.getUsername(connectionId))) {
            return createError(msg, "User not logged in");
        }
        //Subscribe the user to the destination
        int subId = Integer.parseInt(id);
        connections.subscribe(connectionId, destination, subId);
        //Send a RECEIPT to the sender
        Message response = new Message("RECEIPT");
        response.addHeader("receipt-id", msg.getHeader("receipt"));
        return response;
    }

    public Message processUnsubscribe(Message msg) {
        //Process the UNSUBSCRIBE command
        String id = msg.getHeader("id");
        if (id == null) {
            return createError(msg, "Missing id header");
        }
        if (!connections.isLoggedIn(connections.getUsername(connectionId))) {
            return createError(msg, "User not logged in");
        }
        //Unsubscribe the user from the destination
        int subId = Integer.parseInt(id);
        connections.unsubscribe(connectionId, subId);
        //Send a RECEIPT to the sender
        Message response = new Message("RECEIPT");
        response.addHeader("receipt-id", msg.getHeader("receipt"));
        return response;
    }

    public Message processDisconnect(Message msg) {
        //Process the DISCONNECT command
        if (!connections.isLoggedIn(connections.getUsername(connectionId))) {
            return createError(msg, "User not logged in");
        }
        //Disconnect the user
        connections.disconnect(connectionId);
        shouldTerminate = true;
        //Send a RECEIPT to the sender
        Message response = new Message("RECEIPT");
        response.addHeader("receipt-id", msg.getHeader("receipt"));
        return response;
    }

    public Message createError(Message msg, String description) {
        Message error = new Message("ERROR");
        error.addHeader("receipt-id", msg.getHeader("receipt"));
        error.addHeader("message", "melformed frame");
        String body = "The message:\n-----\n" + msg.toString() + "\n-----\n" + description +"\n";
        error.setBody(body);
        return error;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
    
}
