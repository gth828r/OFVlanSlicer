package tuptyslicer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.match.Match;

public abstract class Slicer {
	
	//has a set of slices
	protected Set<Slice> slices;
	
	//has a map of slicelet-to-Slice
	protected ConcurrentHashMap<Slicelet, Slice> sliceletToSlice;
	
	//has a map of controller-to-slice
	protected ConcurrentHashMap<Controller, Slice> controllerToSlice;
	
	/**
	 * FIXME implement
	 * @return
	 */
	public abstract Slice getSliceFromPacket();
	
	/**
	 * Get the slice associated with a given controller
	 * @return slice associated with this controller
	 */
	public Slice getSliceFromController(Controller controller) {
		return controllerToSlice.get(controller);
	}
	
	public abstract void handlePacketFromController(Packet packet, Controller controller);
	
	public abstract void handlePacketFromControllableDevice(Packet packet, ControllableDevice device, ControllableDevicePort port);
	
	public abstract void handleMatchMessageFromController(Match match, Controller controller);
	
	public abstract void handleMatchMessageFromControllableDevice(Match match, Controller controller);
}
