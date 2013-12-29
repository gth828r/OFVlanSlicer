package ofvlanslicer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.factory.BasicFactory;

public class ControllerConnectionManager {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected ConcurrentHashMap<ConnectionManagerKey, OFConnection> connections;
	protected ConcurrentHashMap<OFConnection, ConnectionManagerKey> connectionManagerKeys;
	protected Slicer slicer;

	public ControllerConnectionManager() {
		connections = new ConcurrentHashMap<ConnectionManagerKey, OFConnection>();
		connectionManagerKeys = new ConcurrentHashMap<OFConnection, ConnectionManagerKey>();
	}
	
	public void setSlicer(Slicer slicer) {
		this.slicer = slicer;
	}
	
	// FIXME: this approach is naive and messy, but get something working for now
	public void readAll() {
		for (OFConnection connection : connectionManagerKeys.keySet()) {
			List<OFMessage> messages = connection.receive();
			
			if (messages != null && messages.size() > 0) {
				ConnectionManagerKey key = connectionManagerKeys.get(connection);
				Controller controller = key.getController();
				ControllableDevice device = key.getDevice();
				
				for (OFMessage message : messages) {
					slicer.handlePacketFromController(message, controller, device);
				}
			}
		}
	}
	
	public void createConnections(Slice slice) {
		for (ControllableDevice device : slice.getDevices()) {
			ConnectionManagerKey key = new ConnectionManagerKey(slice, slice.getController(), device);
			OFConnection connection = new OFConnection(slice.getController(), new BasicFactory());
			connections.put(key, connection);
			connectionManagerKeys.put(connection, key);
		}
		
		LOGGER.info("Created connections for slice " + slice);
	}
	
	public void deleteConnections(Slice slice) {
		for (ControllableDevice device : slice.getDevices()) {
			ConnectionManagerKey key = new ConnectionManagerKey(slice, slice.getController(), device);
			OFConnection connection = this.getConnection(slice, device);
			connections.remove(key);
			connectionManagerKeys.remove(connection);
			connection.close();
		}
		
		LOGGER.info("Deleted connections for slice " + slice);
	}
	
	public OFConnection getConnection(Slice slice, ControllableDevice device) {
		ConnectionManagerKey key = new ConnectionManagerKey(slice, slice.getController(), device);
		return connections.get(key);
	}
}
