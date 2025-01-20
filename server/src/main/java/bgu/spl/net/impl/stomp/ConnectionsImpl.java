package bgu.spl.net.impl.stomp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

public class ConnectionsImpl<T> implements Connections<T>{
    private Map<String, Connection<T>> users;
    private Map<Integer,ConnectionHandler<T>> idToHandler;
    private Map<String,List<Integer>> channels;
    private Map<Integer, String> idToUsername;

    public ConnectionsImpl(){
        users = new ConcurrentHashMap<String,Connection<T>>();
        channels = new ConcurrentHashMap<>();
        idToHandler = new ConcurrentHashMap<>();
        idToUsername = new ConcurrentHashMap<>();
    }

    public void addHandler(int id,ConnectionHandler<T> handler){
        idToHandler.put(id, handler);
    }

    public void connect(int id, String username, String passcode){
        if(users.containsKey(username)){
            users.get(username).setId(id);
            users.get(username).setHandler(idToHandler.get(id));
        }
        else{
            Connection<T> connection = new Connection<>(id, idToHandler.get(id), username, passcode);
            users.put(username, connection);
        }
        idToUsername.put(id, username);
    }

    public boolean checkUserPass(String username, String passcode){
        if(!users.containsKey(username)) return true;
        else if(users.get(username).getPassword().equals(passcode)) return true;
        else return false;
    }

    public boolean isLoggedIn(String username){
        if(users.containsKey(username) && (users.get(username).getHandler() != null)) return true;
        else return false;
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
        if (users.containsKey(username)) {
            users.get(username).setId(-1);
            users.get(username).setHandler(null);
            users.get(username).clearChannels();
        }
        for(String channel : channels.keySet()){
            if(channels.get(channel).contains(id)) channels.get(channel).remove(channels.get(channel).indexOf(id));
        }
    }

    public void subscribe(int connectionId,String destination,int SubId){
        destination = "/" + destination;
        if(!channels.containsKey(destination)){
            channels.put(destination, new CopyOnWriteArrayList<>());
            channels.get(destination).add(connectionId);
        }
        else channels.get(destination).add(connectionId);
        String username = idToUsername.get(connectionId);
        users.get(username).addChannel(SubId, destination);   
    }

    public void unsubscribe(int connectionId, int subId){
        String username = idToUsername.get(connectionId);
        String channel = users.get(username).removeChannel(subId);
        channels.get(channel).remove(channels.get(channel).indexOf(connectionId));
    }

    public String getUsername(int connectionId){
        return idToUsername.get(connectionId);
    }

    public boolean isSubscribed(int connectionId, String channel){
        if(!channels.containsKey(channel)) return false;
        else if(channels.get(channel).contains(connectionId)) return true;
        else return false;
    }

    public int getSubId(int connectionId, String channel){
        String username = idToUsername.get(connectionId);
        for(int subId : users.get(username).getChannels().keySet()){
            if(users.get(username).getChannels().get(subId).equals(channel)) return subId;
        }
        return -1;
    }
}
