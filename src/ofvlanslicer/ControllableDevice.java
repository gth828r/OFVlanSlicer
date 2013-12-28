package ofvlanslicer;

import java.util.Set;

public class ControllableDevice {

	protected String id;

	protected String hostname;
	
	protected int controlPort;
	
	//FIXME: does this need to be thread safe?
	protected Set<Short> ports;
	
	public ControllableDevice(String hostname, int controlPort) {
		this.hostname = hostname;
		this.controlPort = controlPort;
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
