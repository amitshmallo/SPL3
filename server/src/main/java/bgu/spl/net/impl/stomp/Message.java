package bgu.spl.net.impl.stomp;
import java.util.HashMap;
import java.util.Map;

public class Message {
    private String type;
    private String body;
    private Map<String,String> headers;

    public Message(String type) {
        this.type = type;
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value){
        headers.put(key,value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String toString(){
        String msg = type + "\n";
        for (String key : headers.keySet()) {
            msg += key + ":" + headers.get(key) + "\n";
        }
        msg += "\n" + body + "\n";
        return msg;
    }
}
