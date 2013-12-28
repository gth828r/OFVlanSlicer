package ofvlanslicer;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.openflow.protocol.OFEchoReply;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFHello;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionType;

import edu.huji.cs.netutils.NetUtilsException;
import edu.huji.cs.netutils.parse.EthernetFrame;

/**
 * @author tupty
 *
 * Perform slicing actions on control plane traffic based on VLAN IDs
 */
public class Slicer {
	
	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected DeviceConnectionManager deviceConnectionManager;
	
	protected ControllerConnectionManager controllerConnectionManager;
	
	//has a set of slices
	protected Set<Slice> slices;
	
	//has a map of slicelet-to-Slice
	protected ConcurrentHashMap<Slicelet, Slice> sliceletToSlice;
	
	//has a map of controller-to-slice
	protected ConcurrentHashMap<Controller, Slice> controllerToSlice;
	
	protected SlicerConfig config;
	
	public Slicer(DeviceConnectionManager deviceConnectionManager, ControllerConnectionManager controllerConnectionManager, SlicerConfig config) {
		this.deviceConnectionManager = deviceConnectionManager;
		this.controllerConnectionManager = controllerConnectionManager;
		this.slices = new TreeSet<Slice>();
		this.config = config;
	}
	
	/**
	 * Get the slice associated with a given controller
	 * @return slice associated with this controller
	 */
	public Slice getSliceFromController(Controller controller) {
		return controllerToSlice.get(controller);
	}
	
	private Slicelet mapDownstreamToSlicelet(EthernetFrame frame, ControllableDevice device, short port) {
		for (Slice slice : slices) {
			
		}
		
		//FIXME implement this
		return null;
	}
	
	public EthernetFrame virtualizePacketOut(EthernetFrame frame, Slicelet slicelet) {
		EthernetFrame newFrame = frame;
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();

		if (!virtualizer.contains(frame)) {
			try {
				newFrame = virtualizer.insert(frame);
			} catch (VirtualizationException e) {
				// Could not virtualize packet for some reason
				// log and continue
				LOGGER.warning(e.getMessage());
			}
		}
		
		return newFrame;
	}

	public Slicelet getSliceletFromPacket(EthernetFrame frame, ControllableDevice device, short port) {
		
		//int vlanId = this.getVlanId(frame);
		
		// FIXME this is dependent on the device port and the device as well
		
		return null;
	}

	public boolean verifyPacketOut(EthernetFrame frame, Slicelet slicelet) {
		VlanVirtualizer virtualizer = slicelet.getVlanVirtualizer();
		
		if (!virtualizer.contains(frame)) {
			/* Untagged traffic will be virtualized, so it is OK */
			return true;
		} else {
			try {
				if (virtualizer.matches(frame)) {
					/* Traffic is tagged with the appropriate VLAN ID */
					return true;
				} else {
					/* Traffic is tagged with an incorrect VLAN ID */
					return false;
				}
			} catch (VirtualizationException e) {
				// This might happen from time to time, so log it
				// just informationally
				LOGGER.fine(e.getMessage());
				return false;
			}
		}
	}

	public Slice slicePacketIn(EthernetFrame frame, Slicelet slicelet) {
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
			
		/* Pass all of these messages along without modification */
		case FEATURES_REPLY:
		case GET_CONFIG_REPLY:
		case BARRIER_REPLY:	
		case VENDOR:
		case ERROR:
		case PORT_STATUS:
			
			break;
		
		/* Locally deal with these messages */
		case HELLO:	
		case ECHO_REQUEST:
		case ECHO_REPLY:
			
		default:
			// I don't know how to process this message in this context, log and drop
	
		}
	}
	
	private void virtualizeAndSendFlowmod(OFFlowMod flowmod, Controller controller, ControllableDevice device) {
		Slice mySlice = this.getSliceFromController(controller);
		Slicelet slicelet;
		
		slicelet = mySlice.getSlicelet(device, flowmod);
		
		if (this.verifyFlowmod(flowmod, slicelet)) {
			// virtualize flowmod
			OFFlowMod newFlowmod = this.virtualizeFlowmod(flowmod, slicelet);
			
			// send newFlowmod to device 
			OFConnection connection = deviceConnectionManager.getConnection(slicelet.getDevice());
			connection.send(newFlowmod);
		} else {
			//log error
		}
	}
	
	private void virtualizeAndSendPacketOut(OFPacketOut pktOut, Controller controller, ControllableDevice device) {
		Slice mySlice = this.getSliceFromController(controller);
		Slicelet slicelet;
		
		EthernetFrame frame = new EthernetFrame(pktOut.getPacketData());
		
		List<OFAction> actions = pktOut.getActions();
		short port = -1;
		
		for (OFAction action : actions) {
			if (action.getType() == OFActionType.OUTPUT) {
				OFActionOutput output = (OFActionOutput) action;
				port = output.getPort();
			}
		}
		
		// This code assumes port must have been set
		slicelet = mySlice.getSlicelet(device, port, frame);
		
		if (this.verifyPacketOut(frame, slicelet)) {
			EthernetFrame newFrame = this.virtualizePacketOut(frame, slicelet);
			OFPacketOut newPktOut = pktOut;
			try {
				newPktOut.setPacketData(newFrame.getRawBytes());
			} catch (NetUtilsException e) {
				// I don't know what can happen here, so log it as a warning
				LOGGER.warning(e.getMessage());
			}
			
			// send newPacketOut to device 
			OFConnection connection = deviceConnectionManager.getConnection(slicelet.getDevice());
			connection.send(newPktOut);
		} else {
			// FIXME set vlanId correctly using \
			// EthernetFrame.statGetVlan(frame.getRawBytes())
			int vlanId = 0;
			LOGGER.warning("Frame has VLAN ID " + vlanId + " but slice needs " + slicelet.getVlanVirtualizer().getVlanId()); 
		}
	}
	
	public void handlePacketFromController(OFMessage ofmessage, Controller controller, ControllableDevice device) {

		// FIXME: this code currently assumes only 1.0 is supported
		
		switch (ofmessage.getType()) {
		case FLOW_MOD:
			//FIXME there is probably a better way to do this instead of casting
			OFFlowMod flowmod = (OFFlowMod) ofmessage;
			this.virtualizeAndSendFlowmod(flowmod, controller, device);
			break;
			
		case PACKET_OUT:
			//FIXME there is probably a better way to do this instead of casting
			OFPacketOut pktOut = (OFPacketOut) ofmessage;
			this.virtualizeAndSendPacketOut(pktOut, controller, device);
			break;
						
		// Pass all of these messages along without modification
		case STATS_REQUEST:
		case FEATURES_REQUEST:
		case GET_CONFIG_REQUEST:
		case BARRIER_REQUEST:
			// hope that controllers don't use the same xid to the same device at the same time		
			OFConnection deviceConnection = deviceConnectionManager.getConnection(device);
			deviceConnection.send(ofmessage);
			break;
			
		case VENDOR:
			if (config.getUnknownMsgStrict()) {
				LOGGER.warning("Vendor messages are not suppoted when UNKNOWN_MSG_STRICT is enabled");
			} else {
				OFConnection vendorDeviceConnection = deviceConnectionManager.getConnection(device);
				vendorDeviceConnection.send(ofmessage);
			}
			
			break;
			
		// Ignore these for now
		case ECHO_REPLY:
			break;
			
		// FIXME:  will need to deal with hello to support 1.1+
		case HELLO:
			break;
		
		// Reply to an echo request from a controller with an echo reply
		case ECHO_REQUEST:
			// FIXME: get the correct connection
			Slice echoRequestSlice = this.getSliceFromController(controller);
			OFEchoReply echoReply = new OFEchoReply();
			OFConnection echoReplyConnection = controllerConnectionManager.getConnection(echoRequestSlice, device);
			echoReplyConnection.send(echoReply);
			break;
		
		default:
			// I don't know how to process this message in this context, log and drop
	
		}
	}
	
	private void virtualizeAndSendPacketIn(OFPacketIn pktIn, ControllableDevice device, short inPort) {
		
		EthernetFrame frame = new EthernetFrame(pktIn.getPacketData());
		
		// FIXME: Jump straight to slicelet
		Slicelet slicelet = this.getSliceletFromPacket(frame, device, inPort);
		
		// Modify packet for controller
		
		// Create new packet in from new packet
		
		// Send packet to controller
	}
	
	
	public void handlePacketFromSwitch(OFMessage ofmessage, ControllableDevice device) {
		switch (ofmessage.getType()) {
		
		case PACKET_IN:
			//FIXME there is probably a better way to do this instead of casting
			OFPacketIn pktIn = (OFPacketIn) ofmessage;
			short inPort = pktIn.getInPort();
			this.virtualizeAndSendPacketIn(pktIn, device, inPort);
			break;
			// Slice by packet VLAN ID
			
		case FLOW_REMOVED:
			// Slice by match space
			
		case PORT_STATUS:
			// Update controller if a given slice has this port in it
			
		
		case ERROR:
		case FEATURES_REPLY:
		case GET_CONFIG_REPLY:
			// slice these by XID
			break;
		
		case VENDOR:
			if (config.getUnknownMsgStrict()) {
				LOGGER.warning("Vendor messages are not suppoted when UNKNOWN_MSG_STRICT is enabled");
			} else {
				//Get slice from XID
				//OFConnection vendorControllerConnection = controllerConnectionManager.getConnection(slice);
				//vendorControllerConnection.send(ofmessage);
			}
		
			break;
		
		// Ignore these for now
		case ECHO_REPLY:
			break;
		
		// FIXME:  will need to deal with hello to support 1.1+
		case HELLO:
			// Send HELLO back
			OFConnection helloConnection = deviceConnectionManager.getConnection(device);
			OFHello hello = new OFHello();
			helloConnection.send(hello);
			break;
	
		// Reply to an echo request from a controller with an echo reply
		case ECHO_REQUEST:
			// FIXME: get the correct connection
			
			// First, reply to the switch
			//Slice echoRequestSlice = this.getSliceFromController(controller);
			OFEchoReply echoReply = new OFEchoReply();
			//OFConnection echoReplyConnection = controllerConnectionManager.getConnection(echoRequestSlice, device);
			//echoReplyConnection.send(echoReply);
			
			// Then send an echo request to the controllers that connect to this device
			
			break;
	
		default:
			// I don't know how to process this message in this context, log and drop
		}
	}
	
	private int getVlanId(EthernetFrame frame) {
		// FIXME better error handling here
		try {
			byte[] vlanIdBytes = EthernetFrame.statGetVlan(frame.getRawBytes());
			int vlanId = ((int) vlanIdBytes[1]) << 8 | (int) vlanIdBytes[0];
			return vlanId;
		} catch (NetUtilsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// FIXME return something sane
			return -4;
		}
	}
}