package tuptyslicer;

import java.util.Set;

import org.opendaylight.controller.sal.packet.Packet;
import org.openflow.protocol.OFFlowMod;

/**
 * @author tupty
 *
 * A slicelet is a piece of a single controllable device.  A single
 * slice contains many sliclets.  The piece of the controllable
 * device is defined by a set of ports on that device as well as
 * a discriminant associated with those ports on the device.
 * 
 * Slicelets do not understand control or data plane information, and
 * therefore do not match against things like packets or flowmods.
 */
public abstract class Slicelet {
	
	/** Discriminant associated with this slicelet */
	protected Discriminant discriminant;
	
	/** Controllable device associated with this slicelet */
	protected ControllableDevice device;
	
	/** Set of ports in this slicelet */
	protected Set<ControllableDevicePort> ports;
	
	/** Slice that this slicelet belongs to */
	protected Slice parent;
	
	/**
	 * Create a new slicelet 
	 * @param parent
	 * @param device
	 * @param ports
	 */
	public Slicelet(Slice parent, ControllableDevice device, 
			        Set<ControllableDevicePort> ports) {
		this.parent = parent;
		this.device = device;
		this.ports = ports;
	}
	
	public boolean matches(ControllableDevice device, ControllableDevicePort port, Packet packet) {
		if (this.containsPort(device, port)) {
			if (discriminant.matches(packet)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check if control plane information matches this slicelet
	 * @param device device on which a packet was received or on which a packet
	 *               will enter the data plane
	 * @param flowmod
	 * @return
	 */
	public boolean matches(ControllableDevice device, OFFlowMod flowmod) {
		// FIXME this needs to check ports in the flowmod against ports in the port set
		if (this.device.equals(device)) {
			if (discriminant.matches(flowmod)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check whether or not a slicelet includes a given controllable device port
	 * @param port controllable port to check for within slicelet
	 * @return whether or not the slicelet contains the port
	 */
	protected boolean containsPort(ControllableDevice device, ControllableDevicePort port) {
		return (this.device.equals(device) && ports.contains(port));
	}
	
}
