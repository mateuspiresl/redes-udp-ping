package reliableprotocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

import reliableprotocol.ReliableProtocol.Packet;

public class ReliableUdpReceiver
{
	private static final double LOSS_RATE = 0.5;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length != 1) {
			System.out.println("Required arguments: port");
			return;
		}
		 
		int port = Integer.parseInt(args[0]);
		
		@SuppressWarnings("resource")
		DatagramSocket socket = new DatagramSocket(port);
		Random random = new Random();
		 
		while (true)
		{
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
			socket.receive(request);
			
			Packet packet = ReliableProtocol.parseRequest(request.getData());
			
			System.out.println("\nReceived...");
			System.out.println("\tPacket ID: " + packet.id);
			System.out.println("\tPacket body: " + new String(packet.body));
			
			if (random.nextDouble() < LOSS_RATE)
			{
				System.out.println("Reply not sent.");
				continue;
			}
			
			byte[] response = ReliableProtocol.createResponse(packet.id);
			socket.send(new DatagramPacket(response, response.length, request.getAddress(), request.getPort()));
			System.out.println("Reply sent.");
		}
	}
}
