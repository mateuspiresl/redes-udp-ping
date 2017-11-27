package reliableprotocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import reliableprotocol.ReliableProtocol.Packet;

public class ReliableProtocolTest
{
	@Test
	public void testRequest()
	{
		int id = 1;
		String body = "test";
		
		byte[] request = ReliableProtocol.createRequest(id, body.getBytes());
		Packet requestPacket = ReliableProtocol.parseRequest(request);
		
		assertEquals(id, requestPacket.id);
		assertEquals(body, new String(requestPacket.body));
		
		byte[] response = ReliableProtocol.createResponse(id);
		int responsePacketId = ReliableProtocol.parseResponse(response);
		
		assertEquals(id, responsePacketId);
	}
}
