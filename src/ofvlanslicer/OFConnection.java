package ofvlanslicer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflow.protocol.OFFeaturesRequest;
import org.openflow.protocol.OFHello;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.OFMessageFactory;

public class OFConnection {
	
	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	/** Socket for this connection */
	protected SocketChannel socket;
	
	protected ByteBuffer inBuffer;
	
	protected ByteBuffer outBuffer;
	
	protected OFMessageFactory factory;
	
	/**
	 * 
	 * @param controller
	 * @param factory
	 */
	public OFConnection(Controller controller, OFMessageFactory factory) {
		this.inBuffer = ByteBuffer.allocate(65536);
		this.factory = factory;
		
		try {
			this.socket = SocketChannel.open();
			this.socket.connect(new InetSocketAddress(controller.getHostname(), controller.getPort()));
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			LOGGER.log(Level.WARNING, e.getStackTrace().toString());
		}
	}
	
	/**
	 * Constructor when a socket already exists (such as one spawned by a 
	 * ServerSocketChannel event)
	 * @param socket socket that corresponds to this OF connection
	 * @param factory
	 */
	public OFConnection(SocketChannel socket, OFMessageFactory factory) {
		this.inBuffer = ByteBuffer.allocate(65536);
		this.factory = factory;
		this.socket = socket;
		
		// Switch initiates TCP session, but controller is responsible
		// For sending a features request to kick off the handshake
		this.send(new OFHello());
		this.send(new OFFeaturesRequest());
	}
	
	public void send(OFMessage message) {
		outBuffer = ByteBuffer.allocate(message.getLength());
		outBuffer.clear();
		message.writeTo(outBuffer);
		outBuffer.flip();
		
		try {
			while (outBuffer.hasRemaining()) {
				int numBytes = socket.write(this.outBuffer);
				LOGGER.info("Wrote " + numBytes + " bytes");
			}
			LOGGER.info("Sent message!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e.getMessage());
		}
	}

	public List<OFMessage> read() {
		List<OFMessage> messages = null;
		int numBytes;
		
		try {
			if ((numBytes = socket.read(inBuffer)) > 0) {
				inBuffer.flip();
				messages = factory.parseMessages(inBuffer, numBytes);
				inBuffer.clear();
			}
		} catch (IOException e) {
			LOGGER.info(e.getMessage());
		}
		
		return messages;
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			LOGGER.log(Level.WARNING, e.getStackTrace().toString());
		}
	}
	
	public SocketChannel getSocket() {
		return this.socket;
	}
}
