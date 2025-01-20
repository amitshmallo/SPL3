package bgu.spl.net.impl.stomp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

public class ConnectionsImpl<T> implements Connections<T>{
    private Map<String, Connection<T>> loggedInUsers;
    private Map<Integer,ConnectionHandler<T>> idToHandler;
    private Map<String,List<Integer>> channels;
    private Map<Integer, String> idToUsername;

    public ConnectionsImpl(){
        loggedInUsers = new ConcurrentHashMap<String,Connection<T>>();
        channels = new ConcurrentHashMap<>();
        idToHandler = new ConcurrentHashMap<>();
    }

    public void addHandler(int id,ConnectionHandler<T> handler){
        idToHandler.put(id, handler);
    }

    public void connect(int id, String username, String passcode){
        if(loggedInUsers.containsKey(username)){
            loggedInUsers.get(username).setId(id);
            loggedInUsers.get(username).setHandler(idToHandler.get(id));
        }
        else{
            Connection<T> connection = new Connection<>(id, idToHandler.get(id), username, passcode);
            loggedInUsers.put(username, connection);
        }
        idToUsername.put(id, username);
    }

    public boolean checkUserPass(String username, String passcode){
        if(loggedInUsers.get(username).getPassword().equals(passcode)) return true;
        else return false;
    }

    public boolean isLoggedIn(String username){
        return loggedInUsers.containsKey(username) && loggedInUsers.get(username).getConnectionId() != -1;
    }

    public boolean send(int connectionId, T msg){
        if(idToHandler.containsKey(connectionId)){
            idToHandler.get(connectionId).send(msg);
            return true;
        }
        return false;
    }
    
    public void send(String channel, T msg){
        for(int connectionId : channels.get(channel)){
            send(connectionId, msg);
        }
    }
    
    public void disconnect(int id){
        String username = idToUsername.get(id);
        idToHandler.remove(id);
        idToUsername.remove(id);
        loggedInUsers.get(username).setId(-1);
        loggedInUsers.get(username).setHandler(null);
        loggedInUsers.get(username).clearChannels();
        for(String channel : channels.keySet()){
            channels.get(channel).remove(id);
        }
    }

    public void subscribe(int connectionId,String destination,int SubId){
        if(!channels.containsKey(destination)){
            channels.put(destination, new ArrayList<>());
        }
        else channels.get(destination).add(connectionId);
        String username = idToUsername.get(connectionId);
        loggedInUsers.get(username).addChannel(SubId, destination);   
    }

    public void unsubscribe(int subId, int connectionId){
        String username = idToUsername.get(connectionId);
        String channel = loggedInUsers.get(username).removeChannel(subId);
        channels.get(channel).remove(connectionId);
    }

    public String getUsername(int connectionId){
        return idToUsername.get(connectionId);
    }
}
