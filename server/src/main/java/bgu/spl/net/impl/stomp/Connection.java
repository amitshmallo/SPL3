package main.java.bgu.spl.net.impl.stomp;

import java.util.Map;

public class Connection {
    private final int connectionId;
    private final ConnectionHandler<T> handler;
    private final String username;
    private final String password;
    private Map<Integer, String> idToChannel;

    public Connection(int connectionId, ConnectionHandler<T> handler, String username, String password) {
        this.connectionId = connectionId;
        this.handler = handler;
        this.username = username;
        this.password = password;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public ConnectionHandler<T> getHandler() {
        return handler;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addChannel(int subscriptionId, String channel) {
        idToChannel.put(subscriptionId, channel);
    }

    public void removeChannel(int subscriptionId) {
        idToChannel.remove(subscriptionId);
    }

    public boolean isSubscribed(int subscriptionId) {
        return idToChannel.containsKey(subscriptionId);
    }

    public Map<Integer, String> getChannels() {
        return idToChannel;
    }

    public void setId(int id) {
        this.connectionId = id;
    }

    public void setHandler(ConnectionHandler<T> handler) {
        this.handler = handler;
    }

    public void clearChannels() {
        idToChannel.clear();
    }
}
