package tuptyslicer;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.protocol.factory.OFMessageFactory;

public class ControllerConnectionManager {

	// each slice has a controller connection
	protected ConcurrentHashMap<Slice, OFConnection> connections;
	
	protected ConcurrentHashMap<OFConnection, Slice> slices;

	public ControllerConnectionManager() {
		connections = new ConcurrentHashMap<Slice, OFConnection>();
		slices = new ConcurrentHashMap<OFConnection, Slice>();
	}
	
	public void createConnection(Slice slice) {
		//FIXME implement this
		//OFConnection connection = new OFConnection(slice.getController(), new OFMessageFactory());
	}
	
	public void deleteConnection(Slice slice) {
		OFConnection connection = this.getConnection(slice);
		
		//FIXME close connection?
		connections.remove(slice);
		slices.remove(connection);
		connection.close();
	}
	
	public OFConnection getConnection(Slice slice) {
		return connections.get(slice);
	}
}
