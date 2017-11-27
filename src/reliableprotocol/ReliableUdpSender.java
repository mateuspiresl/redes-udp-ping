package reliableprotocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Random;

public class ReliableUdpSender
{
	private static final int CLIENT_PORT = 3030;
    
    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception
    {
        if (args.length != 3) {
            System.out.println("Requerid Arguments: host port body");
            return;
        }

        InetAddress ipAddress = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        String body = args[2];
        
        final DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
    	socket.setSoTimeout(1000);

    	int packetId = new Random().nextInt();
    	System.out.println("Random ID: " + packetId);
    	System.out.println("Body: " + body);
    	
    	long time = new Date().getTime();
        byte[] buffer = ReliableProtocol.createRequest(packetId, body.getBytes());

        System.out.println("Enviando...");
        while (true) try
        {
			socket.send(new DatagramPacket(buffer, buffer.length, ipAddress, port));
			
        	DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);
            socket.receive(reply);
            
            long diff = new Date().getTime() - time;
            
            int responsePacketId = ReliableProtocol.parseResponse(reply.getData());
            if (responsePacketId == packetId)
            	System.out.println(String.format("Resposta recebida com sucesso (%d)!", diff));
            
            break;
		}
		catch (SocketTimeoutException e) {
			long diff = new Date().getTime() - time;
            System.out.println(String.format("Timeout (%d). Tentando novamente...", diff));
        }
    }
}
