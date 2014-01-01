package ofvlanslicer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.BasicFactory;
import org.openflow.protocol.factory.OFMessageFactory;

public class DeviceConnectionManager implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected short serverPort;
	
	protected ServerSocketChannel serverSocketChannel;
	
	protected Selector selector;
	
	// each device has a connection
	

	// each slice has a controller connection
	protected ConcurrentHashMap<SocketChannel, OFConnection> connections;
	
	protected ConcurrentHashMap<ControllableDevice, OFConnection> connectionsByDevice;
	
	protected ConcurrentHashMap<OFConnection, ControllableDevice> devices;
	
	protected OFMessageFactory factory;
	
	protected Slicer slicer;

	public DeviceConnectionManager(short serverPort) {
		this.serverPort = serverPort;
		this.connections = new ConcurrentHashMap<SocketChannel, OFConnection>();
		this.connectionsByDevice = new ConcurrentHashMap<ControllableDevice, OFConnection>();
		this.devices = new ConcurrentHashMap<OFConnection, ControllableDevice>();
		this.factory = new BasicFactory();
	}
	
	public void setSlicer(Slicer slicer) {
		this.slicer = slicer;
	}
	
	public void run() {
		listenForConnections();
	}
	
	protected void listenForConnections() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
			selector = Selector.open();
			
			serverSocketChannel.socket().bind(new InetSocketAddress(this.serverPort));
			serverSocketChannel.configureBlocking(false);
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); 
			
			while(true){
				selector.select();
				
				for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
					SelectionKey key = i.next();
					i.remove();
					
					if (key.isConnectable()) {
						((SocketChannel) key.channel()).finishConnect();
					}
					
					if (key.isAcceptable()) {
						SocketChannel socket = serverSocketChannel.accept();
						socket.configureBlocking(false);
						socket.socket().setTcpNoDelay(true);
						socket.register(selector, SelectionKey.OP_READ);
						
						String deviceHostname = socket.socket().getInetAddress().getHostName();
						int deviceControlPort = socket.socket().getPort();
						ControllableDevice device = new ControllableDevice(deviceHostname, deviceControlPort);
						this.createConnection(socket, device);
					}
					
					if (key.isReadable()) {
						
						SocketChannel socket = (SocketChannel) key.channel();
						OFConnection connection = connections.get(socket);
						List<OFMessage> messages = connection.read();
						

						if (messages != null && messages.size() > 0) {
							ControllableDevice device = devices.get(connection);

							LOGGER.info("Got messages from device " + device);
							
							for (OFMessage message : messages) {
								slicer.handlePacketFromSwitch(message, device);
							}
						}
					}
					
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createConnection(SocketChannel socket, ControllableDevice device) {
		LOGGER.info("Creating connection for device " + device);
		OFConnection connection = new OFConnection(socket, new BasicFactory());
		connections.put(socket, connection);
		connectionsByDevice.put(device, connection);
		devices.put(connection, device);
		
		this.doHandshake(socket);
	}
	
	public void deleteConnection(ControllableDevice device) {
		LOGGER.info("Deleting connection for device " + device);
		
		OFConnection connection = this.getConnection(device);
		
		//FIXME close connection?
		connections.remove(connection.getSocket());
		connectionsByDevice.remove(device);
		devices.remove(connection);
		connection.close();
	}
	
	public OFConnection getConnection(ControllableDevice device) {
		return connectionsByDevice.get(device);
	}
	
	public void doHandshake(SocketChannel socket) {

	}
}
