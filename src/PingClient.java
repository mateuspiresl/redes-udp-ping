import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PingClient
{
    private static final int CLIENT_PORT = 3030;
    private static final int PING_REQUEST = 10;
    
    private static int id = 0, count = 0;
    private static long min = Integer.MAX_VALUE;
    private static long max = Integer.MIN_VALUE;
    private static long sum = 0;
    
    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception
    {
        if (args.length != 2) {
            System.out.println("Requerid Arguments: host port");
            return;
        }

        // Captura o host e o numero da porta
        InetAddress ipAddress = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        
        // Create a datagram socket for receiving and sending UDP packets
        // through the port specified on the command line.
        final DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
    	socket.setSoTimeout(1000);

    	// Runs 10 times in intervals of 1 second
    	Timer timer = new Timer();
    	timer.schedule(new TimerTask() {
			@Override
			public void run()
			{
				long time = new Date().getTime();

                // Mensagem padrão que será enviada ao server
                String message = String.format("PING %d %d \r\n", id++, time);
                byte[] buffer = message.getBytes();

                // Envia datagram
                try {
					socket.send(new DatagramPacket(buffer, buffer.length, ipAddress, port));
					
					// Recebe resposta do servidor
                	DatagramPacket reply = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(reply);
                    
                    long diff = new Date().getTime() - time;
                    
                    count++;
                    sum += diff;
                    
                    if (diff < min) min = diff;
                    else if (diff > max) max = diff;
                    
                    printData(reply, diff);
				}
				catch (SocketTimeoutException e) {
                    System.out.println("Pacote perdido: " + message);
                }
                catch (Exception e) {
					e.printStackTrace();
				}
                
                // If it's the last packet
                if (id == PING_REQUEST)
                {
                	timer.cancel();
                	
                	System.out.println(String.format("\nMínimo: %d ms\nMáximo: %d ms\nMédio: %.3f ms",
                    		min, max, sum / (float) count));
                }
			}
		}, 0, 1000);
    }

    /*
	 * Print ping data to the standard output stream.
	 */
	private static void printData(DatagramPacket request, long time) throws IOException
	{
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();
		
		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		
		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);
		
		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r and \n.)
		BufferedReader br = new BufferedReader(isr);
		
		// The message data is contained in a single line, so read this line.
		String line = br.readLine();
		
		// Print host address and data received from it.
		String address = request.getAddress().getHostAddress();
		System.out.println(String.format("Received from %s: %s", address, new String(line)));
		System.out.println(String.format("Round-trip: %d ms\n", time));
	}
}
