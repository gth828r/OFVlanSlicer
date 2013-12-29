package ofvlanslicer;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllableDevice {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected String id;

	protected String hostname;
	
	protected int controlPort;
	
	//FIXME: does this need to be thread safe?
	protected Set<Short> ports;
	
	public ControllableDevice(String hostname, int controlPort) {
		this.hostname = hostname;
		this.controlPort = controlPort;
		
		String msg = "Creating new controllable device with host " + hostname + ", port " + controlPort;
		LOGGER.log(Level.FINEST, msg);
	}
	
	protected void setId(String id) {
		this.id = id;
	}
	
	protected void addPorts(Set<Short> ports) {
		this.ports.addAll(ports);
	}
	
	protected boolean containsPort(Short port) {
		return ports.contains(port);
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public int getControlPort() {
		return this.controlPort;
	}
}
