package tuptyslicer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

public class OFConnection {
	
	/** OF message stream for this connection */
	protected OFMessageAsyncStream connection;
	
	/** Socket for this connection */
	protected SocketChannel socket;
	
	/**
	 * 
	 * @param controller
	 * @param factory
	 */
	public OFConnection(Controller controller, OFMessageFactory factory) {
		try {
			socket = SocketChannel.open();
			socket.connect(new InetSocketAddress(controller.getHostname(), controller.getPort()));
			connection = new OFMessageAsyncStream(socket, factory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor when a socket already exists (such as one spawned by a 
	 * ServerSocketChannel event)
	 * @param socket socket that corresponds to this OF connection
	 * @param factory
	 */
	public OFConnection(SocketChannel socket, OFMessageFactory factory) {
		try {
			socket = SocketChannel.open();
			connection = new OFMessageAsyncStream(socket, factory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(OFMessage message) {
		try {
			connection.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<OFMessage> receive() {
		List<OFMessage> messages;
		
		try {
			messages = connection.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return messages;
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
