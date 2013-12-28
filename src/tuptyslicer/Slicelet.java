package tuptyslicer;

import java.util.Set;

import org.openflow.protocol.OFFlowMod;

import edu.huji.cs.netutils.parse.EthernetFrame;

public class Slicelet {

	/** Discriminant associated with this slicelet */
	protected VlanVirtualizer virtualizer;
	
	/** Controllable device associated with this slicelet */
	protected ControllableDevice device;
	
	/** Set of ports in this slicelet */
	protected Set<Short> ports;
	
	/** Slice that this slicelet belongs to */
	protected Slice parent;
	
	public Slicelet(Slice parent, short vlanId,
			ControllableDevice device, Set<Short> ports) {
		this.parent = parent;
		this.device = device;
		this.ports = ports;
		this.virtualizer = new VlanVirtualizer(vlanId);
	}
	
	public boolean matches(ControllableDevice device, Short port, EthernetFrame frame) {
		if (this.containsPort(device, port)) {
			try {
				if (virtualizer.matches(frame)) {
					return true;
				}
			} catch (VirtualizationException e) {
				// FIXME do logging
				e.printStackTrace();
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
			if (virtualizer.matches(flowmod)) {
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
	protected boolean containsPort(ControllableDevice device, Short port) {
		return (this.device.equals(device) && ports.contains(port));
	}
	
	public VlanVirtualizer getVlanVirtualizer() {
		return virtualizer;
	}
	
	public ControllableDevice getDevice() {
		return this.device;
	}
}
