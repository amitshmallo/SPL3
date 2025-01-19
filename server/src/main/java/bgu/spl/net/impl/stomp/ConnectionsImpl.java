package main.java.bgu.spl.net.impl.stomp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

public class ConnectionsImpl implements Connections<T>{
    private Map<String, Connection> loggedInUsers;
    private Map<Integer,ConnectionHandler<T>> idToHandler;
    private Map<String,List<Integer>> channels;
    private Map<Integer, String> idToUsername;

    public ConnectionsImpl(){
        loggedInUsers = new ConcurrentHashMap<>();
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
            Connection connection = new Connection(id, idToHandler.get(id), username, passcode);
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
        idToHandler.remove(id);
        idToUsername.remove(id);
        loggedInUsers.get(id).setId(-1);
        loggedInUsers.get(id).setHandler(null);
        loggedInUsers.get(id).clearChannels();
        for(String channel : channels.keySet()){
            channels.get(channel).remove(id);
        }
    }

    public void subscribe(int connectionId,String destination,int SubId){
        if(!channels.containsKey(destination)){
            channels.put(destination, new ArrayList<>());
        }
        else channels.get(destination).add(connectionId);
        loggedInUsers.get(connectionId).addChannel(SubId, destination);   
    }

    public void unsubscribe(int subId, int connectionId){
        String channel = loggedInUsers.get(connectionId).removeChannel(subId);
        channels.get(channel).remove(connectionId);
    }
}
