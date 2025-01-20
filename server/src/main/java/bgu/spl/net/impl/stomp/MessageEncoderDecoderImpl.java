package bgu.spl.net.impl.stomp;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl.net.api.MessageEncoderDecoder;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000') {
            return popMessage();
        }
        pushByte(nextByte);
        return null; //not a full message yet
    }

    @Override
    public byte[] encode(Message message) {
        return messageToBytes(message); 
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private Message popMessage() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        int lastLineIndex = 0;
        String[] lines = result.split("\n");
        Message message = new Message(lines[0]);
        for (int i = 1; i < lines.length && !lines[i].equals(""); i++) {
            String[] header = lines[i].split(":");
            message.addHeader(header[0], header[1]);
            lastLineIndex = i;
        }
        StringBuilder body = new StringBuilder();
        for (int i = lastLineIndex + 1; i < lines.length; i++) {
            body = body.append(lines[i]);
        }
        message.setBody(body.toString());
        return message;
    }

    private byte[] messageToBytes(Message message) {
        String msg = message.toString() + "\u0000";
        return msg.getBytes();
    }

}
    
    

