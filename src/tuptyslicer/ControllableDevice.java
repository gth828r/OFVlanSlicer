package tuptyslicer;

import java.util.Set;

public class ControllableDevice {

	//FIXME: does this need to be thread safe?
	protected Set<ControllableDevicePort> ports;
	
	public ControllableDevice(String identifier, Set<Integer> portNumbers) {
		
	}
	
	protected boolean containsPort(ControllableDevicePort port) {
		return ports.contains(port);
	}
}
