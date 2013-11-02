package tuptyslicer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionType;
import org.opendaylight.controller.sal.packet.Packet;

/**
 * @author tupty
 *
 * Perform slicing actions on control plane traffic based on VLAN IDs
 */
public class Slicer {
	
	protected OpenFlowControllerHandler controllerHandler;
	
	protected OpenFlowDeviceHandler deviceHandler;
	
	//has a set of slices
	protected Set<Slice> slices;
	
	//has a map of slicelet-to-Slice
	protected ConcurrentHashMap<Slicelet, Slice> sliceletToSlice;
	
	//has a map of controller-to-slice
	protected ConcurrentHashMap<Controller, Slice> controllerToSlice;
	
	public Slicer() {
	
		
		slices = new TreeSet<Slice>();
	}
	
	/**
	 * Get the slice associated with a given controller
	 * @return slice associated with this controller
	 */
	public Slice getSliceFromController(Controller controller) {
		return controllerToSlice.get(controller);
	}
	
	private Slicelet mapDownstreamToSlicelet(Packet packet, ControllableDevice device, ControllableDevicePort port) {
		for (Slice slice : slices) {
			
		}
		
		//FIXME implement this
		return null;
	}
	
	public Packet virtualizePacketOut(Packet packet, Slicelet slicelet) {
		Packet newPacket = packet;
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();

		if (!virtualizer.contains(packet)) {
			newPacket = virtualizer.insert(packet);
		}
		
		return newPacket;
	}

	public void handlePacketFromController(Packet packet, Controller controller) {
		Slice slice = this.getSliceFromController(controller);
		//FIXME set this properly
		Slicelet slicelet = null;
		Packet newPacket;
		
		if (this.verifyPacketOut(packet, slicelet)) {
			newPacket = this.virtualizePacketOut(packet, slicelet);
			
			// send to ControllableDevice
		} else {
			// Drop this packet and log it because it was explicitly
			// outside of the sender's slice
		}
	}

	public Slice getSliceFromPacket() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean verifyPacketOut(Packet packet, Slicelet slicelet) {
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();
		
		if (!virtualizer.contains(packet)) {
			/* Untagged traffic will be virtualized, so it is OK */
			return true;
		} else {
			if (virtualizer.matches(packet)) {
				/* Traffic is tagged with the appropriate VLAN ID */
				return true;
			} else {
				/* Traffic is tagged with an incorrect VLAN ID */
				return false;
			}
		}
	}

	public Slice slicePacketIn(Packet packet, Slicelet slicelet) {
		//FIXME not really implemented
		return sliceletToSlice.get(slicelet);
	}

	public OFFlowMod virtualizeFlowmod(OFFlowMod flowmod, Slicelet slicelet) {
		OFFlowMod newFlowmod = flowmod;
		
		// get discriminant for slice/device (we store this)
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();

		if (!virtualizer.contains(flowmod)) {
			newFlowmod = virtualizer.insert(flowmod);
		}
		
		return newFlowmod;
	}

	public boolean verifyFlowmod(OFFlowMod flowmod, Slicelet slicelet) {
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();
		
		if (virtualizer.contains(flowmod)) {
			if (!virtualizer.matches(flowmod)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void handlePacketFromControllableDevice(OFMessage ofmessage, ControllableDevice device) {
		switch (ofmessage.getType()) {
		case FLOW_REMOVED:
			
			break;
			
		case PACKET_IN:
			
			break;
	
		case STATS_REPLY:
			
			break;
		
		case HELLO:	
			
			break;
			
		/* Pass all of these messages along without modification */
		case FEATURES_REPLY:
		case GET_CONFIG_REPLY:
		case BARRIER_REPLY:		
		case ECHO_REQUEST:
		case ECHO_REPLY:
		case VENDOR:
		case ERROR:
		case PORT_STATUS:
			
			break;
		
		default:
			// I don't know how to process this message in this context, log and drop
	
		}
	}
	
	public void handlePacketFromController(OFMessage ofmessage, Controller controller, ControllableDevice device) {
		Slice mySlice = this.getSliceFromController(controller);
		
		switch (ofmessage.getType()) {
		case FLOW_MOD:
			//FIXME there is probably a better way to do this
			OFFlowMod flowmod = (OFFlowMod) ofmessage;
			Slicelet slicelet = mySlice.getSlicelet(device, flowmod);
			
			if (this.verifyFlowmod(flowmod, slicelet)) {
				OFFlowMod newFlowmod = this.virtualizeFlowmod(flowmod, slicelet);
			} else {
				//log error, 
			}
			
			// send newFlowmod to device 
			
			break;
			
		case PACKET_OUT:
			//FIXME there is probably a better way to do this
			OFPacketOut pktOut = (OFPacketOut) ofmessage;
			List<OFAction> actions = pktOut.getActions();
			short port;
			
			for (OFAction action : actions) {
				if (action.getType() == OFActionType.OUTPUT) {
					OFActionOutput output = (OFActionOutput) action;
					port = output.getPort();
				}
			}
			
			//Slicelet slicelet = mySlice.getSlicelet(device, port, ofmessage);
			
			//if (this.verifyPacket(flowmod, slicelet)) {
			//	OFFlowMod newFlowmod = this.virtualizeFlowmod(flowmod, slicelet);
			//} else {
				//log error, 
			//}
			
			// send newFlowmod to device 
			break;
			
		case HELLO:	
			
			break;
			
		/* Pass all of these messages along without modification */
		case STATS_REQUEST:
		case FEATURES_REQUEST:
		case GET_CONFIG_REQUEST:
		case BARRIER_REQUEST:		
		case ECHO_REQUEST:
		case ECHO_REPLY:
		case VENDOR:
			
			break;
		
		default:
			// I don't know how to process this message in this context, log and drop
	
		}
	}
}
