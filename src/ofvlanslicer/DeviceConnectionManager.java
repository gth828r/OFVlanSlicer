package ofvlanslicer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.protocol.OFMessage;

public class DeviceConnectionManager {

	
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
		this.listenForConnections();
	}
	
	public void setSlicer(Slicer slicer) {
		this.slicer = slicer;
	}
	
	// FIXME: this approach is naive and messy, but get something working for now
	public void readAll() {
		for (OFConnection connection : devices.keySet()) {
			List<OFMessage> messages = connection.receive();
			
			if (messages != null && messages.size() > 0) {
				ControllableDevice device = devices.get(connection);
				
				for (OFMessage message : messages) {
					slicer.handlePacketFromSwitch(message, device);
				}
			}
		}
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			    //do something with socketChannel...
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createConnection(SocketChannel socket, ControllableDevice device) {
		//FIXME implement this
		//OFConnection connection = new OFConnection(socket, new OFMessageFactory());
		//connections.put(device, connection);
		//devices.put(connection, device);
	}
	
	public void deleteConnection(ControllableDevice device) {
		OFConnection connection = this.getConnection(device);
		
		//FIXME close connection?
		connections.remove(device);
		devices.remove(connection);
		connection.close();
	}
	
	public OFConnection getConnection(ControllableDevice device) {
		return connections.get(device);
	}
	
}
