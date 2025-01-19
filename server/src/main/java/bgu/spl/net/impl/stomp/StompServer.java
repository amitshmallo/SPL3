package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.echo.EchoProtocol;
import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.srv.Server;
import main.java.bgu.spl.net.impl.stomp.MessageEncoderDecoderImpl;
import main.java.bgu.spl.net.impl.stomp.StompMessagingProtocolImpl;
import main.java.bgu.spl.net.impl.stomp.Message;

public class StompServer {

    public static void main(String[] args) {
        //int port = Integer.parseInt(args[0]);
        //String serverVersion = args[1];
        int port = 7777;
        String serverVersion = "tpc";

        if(serverVersion == "tpc"){
            System.out.println("Server version: tpc");
            System.out.println("Server is running on port: " + port);
            Server.threadPerClient(
                port, //port
                () -> new StompMessagingProtocolImpl<String>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
            ).serve();
        }
        else if(serverVersion == "reactor"){
            System.out.println("Server version: reactor");
            System.out.println("Server is running on port: " + port);
            Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                () -> new StompMessagingProtocolImpl<String>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
             ).serve();
        }
        else{
            System.out.println("Invalid server version");
            System.out.println("Please enter either 'tpc' or 'reactor'");
            System.out.println("Exiting.");
        }
    }
}
