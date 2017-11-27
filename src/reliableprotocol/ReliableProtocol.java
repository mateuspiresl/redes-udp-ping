package reliableprotocol;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ReliableProtocol
{
	private static final String ID = "REPR";
	private static final String REQ = "REQ";
	private static final String RES = "RES";
	
	public static byte[] createRequest(int packetId, byte[] body)
	{
		byte[] packetIdBytes = ByteBuffer.allocate(4).putInt(packetId).array();
		return concat(concat((ID + REQ).getBytes(), packetIdBytes), body);
	}
	
	public static Packet parseRequest(byte[] packet)
	{
		byte[] idBytes = Arrays.copyOfRange(packet, 0, 4);
		if (!new String(idBytes).equals(ID)) throw new RuntimeException("It's not a REPR packet"); 
		
		byte[] reqBytes = Arrays.copyOfRange(packet, 4, 7);
		if (!new String(reqBytes).equals(REQ)) throw new RuntimeException("It's not a request packet");
		
		byte[] packetIdBytes = Arrays.copyOfRange(packet, 7, 11);
		ByteBuffer packetIdBuffer = ByteBuffer.wrap(packetIdBytes);
		int packetId = packetIdBuffer.getInt();

		byte[] body = Arrays.copyOfRange(packet, 11, packet.length);
		return new Packet(packetId, body);
	}
	
	public static byte[] createResponse(int packetId)
	{
		byte[] packetIdBytes = ByteBuffer.allocate(4).putInt(packetId).array();
		return concat((ID + RES).getBytes(), packetIdBytes);
	}
	
	public static int parseResponse(byte[] packet)
	{
		byte[] idBytes = Arrays.copyOfRange(packet, 0, 4);
		if (!new String(idBytes).equals(ID)) throw new RuntimeException("It's not a REPR packet"); 
		
		byte[] reqBytes = Arrays.copyOfRange(packet, 4, 7);
		if (!new String(reqBytes).equals(RES)) throw new RuntimeException("It's not a response packet");
		
		byte[] packetIdBytes = Arrays.copyOfRange(packet, 7, 11);
		ByteBuffer packetIdBuffer = ByteBuffer.wrap(packetIdBytes);
		return packetIdBuffer.getInt();
	}
	
	public static byte[] concat(byte[] a, byte[] b)
	{
		byte[] joined = new byte[a.length + b.length];
		System.arraycopy(a, 0, joined, 0, a.length);
		System.arraycopy(b, 0, joined, a.length, b.length);
		return joined;
	}
	
	
	public static class Packet
	{
		public final int id;
		public final byte[] body;
		
		public Packet(int id, byte[] body)
		{
			this.id = id;
			this.body = body;
		}
	}
}
