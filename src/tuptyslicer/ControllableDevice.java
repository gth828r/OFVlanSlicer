package tuptyslicer;

import java.util.Set;

public class ControllableDevice {


	protected String hostname;
	
	protected short controlPort;
	
	//FIXME: does this need to be thread safe?
	protected Set<Short> ports;
	
	public ControllableDevice(String identifier, Set<Short> portNumbers) {
		
	}
	
	protected boolean containsPort(Short port) {
		return ports.contains(port);
	}
}
