package ofvlanslicer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.BasicFactory;

public class DeviceConnectionManager implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected short serverPort;
	
	// each device has a connection
	

	// each slice has a controller connection
	protected ConcurrentHashMap<ControllableDevice, OFConnection> connections;
	
	protected ConcurrentHashMap<OFConnection, ControllableDevice> devices;
	
	protected Slicer slicer;

	public DeviceConnectionManager(short serverPort) {
		this.serverPort = serverPort;
		this.connections = new ConcurrentHashMap<ControllableDevice, OFConnection>();
		this.devices = new ConcurrentHashMap<OFConnection, ControllableDevice>();
	}
	
	public void setSlicer(Slicer slicer) {
		this.slicer = slicer;
	}
	
	// FIXME: this approach is naive and messy, but get something working for now
	public void readAll() {
		for (OFConnection connection : devices.keySet()) {
			List<OFMessage> messages = connection.receive();
			
			LOGGER.info("Checking messaged for device connection " + connection.toString());
			
			if (messages != null && messages.size() > 0) {
				ControllableDevice device = devices.get(connection);
				
				for (OFMessage message : messages) {
					slicer.handlePacketFromSwitch(message, device);
				}
			}
		}
	}
	
	public void run() {
		listenForConnections();
	}
	
	protected void listenForConnections() {
		ServerSocketChannel serverSocketChannel;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(this.serverPort));
			
			while(true){
			    try {
					SocketChannel socket = serverSocketChannel.accept();
					String deviceHostname = socket.socket().getInetAddress().getHostName();
					int deviceControlPort = socket.socket().getPort();
					ControllableDevice device = new ControllableDevice(deviceHostname, deviceControlPort);
					this.createConnection(socket, device);
				} catch (IOException e) {
					LOGGER.finer(e.getMessage());
					LOGGER.finer(e.getStackTrace().toString());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createConnection(SocketChannel socket, ControllableDevice device) {
		OFConnection connection = new OFConnection(socket, new BasicFactory());
		connections.put(device, connection);
		devices.put(connection, device);
		LOGGER.info("Creating connection for device " + device);
	}
	
	public void deleteConnection(ControllableDevice device) {
		OFConnection connection = this.getConnection(device);
		
		//FIXME close connection?
		connections.remove(device);
		devices.remove(connection);
		connection.close();
		
		LOGGER.info("Deleting connection for device " + device);
	}
	
	public OFConnection getConnection(ControllableDevice device) {
		return connections.get(device);
	}
	
}
