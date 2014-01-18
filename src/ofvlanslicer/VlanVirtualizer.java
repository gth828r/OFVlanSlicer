package ofvlanslicer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionType;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;

import edu.huji.cs.netutils.NetUtilsException;
import edu.huji.cs.netutils.parse.EthernetFrame;

public class VlanVirtualizer  {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected short vlanId;
	
	public VlanVirtualizer(short vlanId) {
		this.vlanId = vlanId;
	}

	public boolean matches(EthernetFrame frame) throws VirtualizationException {
		if (this.contains(frame)) {
			byte[] vlanBytes;
			try {
				vlanBytes = EthernetFrame.statGetVlan(frame.getRawBytes());
			} catch (NetUtilsException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
				LOGGER.log(Level.WARNING, e.getStackTrace().toString());
				throw new VirtualizationException();
			}
			
			if (vlanBytes.equals(this.vlanIdInBytes())) {
				return true;
			}
		}
		
		return false;
	}

	public boolean contains(EthernetFrame frame) {		
		int frameEthertype = frame.getPacketType();
		
		if (frameEthertype == EthernetFrame.ETHERNET_OVER_VLAN) {
			return true;
		}
		
		return false;
	}

	public EthernetFrame insert(EthernetFrame frame) throws VirtualizationException {
		try {
			byte[] rawNewFrame = EthernetFrame.statAddVlan(frame.getRawBytes(), this.vlanIdInBytes());
			return new EthernetFrame(rawNewFrame);
		} catch (NetUtilsException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			LOGGER.log(Level.WARNING, e.getStackTrace().toString());
			throw new VirtualizationException();
		}
	}

	public EthernetFrame delete(EthernetFrame frame) throws VirtualizationException {
		try {
			byte[] rawNewFrame = EthernetFrame.statStripVlan(frame.getRawBytes());
			return new EthernetFrame(rawNewFrame);
		} catch (NetUtilsException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			LOGGER.log(Level.WARNING, e.getStackTrace().toString());
			throw new VirtualizationException();
		}
	}

	public EthernetFrame replace(EthernetFrame frame) throws VirtualizationException {
		EthernetFrame frameNoVlan = this.delete(frame);
		EthernetFrame newFrame = this.insert(frameNoVlan);
		
		return newFrame;
	}

	public boolean matches(OFFlowMod flowmod) {
		if (this.contains(flowmod)) {
			return this.matches(flowmod.getMatch());
		}
		
		return false;
	}
	
	public boolean matches(OFFlowRemoved flowRemoved) {
		if (this.contains(flowRemoved)) {
			return this.matches(flowRemoved.getMatch());
		}
		
		return false;
	}
	
	public boolean matches(OFMatch match) {
		if (match.getDataLayerVirtualLan() == vlanId) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Verify that the action results in the packet staying within
	 * the same VLAN
	 * @param action
	 * @return
	 */
	public boolean staysWithinVlan(OFAction action) {
		// The user can set the VLAN ID to be the same VLAN
		if (action.getType() == OFActionType.SET_VLAN_VID) {
			OFActionVirtualLanIdentifier svidAction = (OFActionVirtualLanIdentifier) action;
			if (svidAction.getVirtualLanIdentifier() == this.getVlanId()) {
				return true;
			} else {
				return false;
			}
		} else if (action.getType() == OFActionType.STRIP_VLAN) {
			// Don't allow user to strip VLAN tag
			return false;
		} else {
			return true;
		}
	}

	public boolean contains(OFFlowMod flowmod) {
		return this.contains(flowmod.getMatch());
	}
	
	public boolean contains(OFFlowRemoved flowRemoved) {
		return this.contains(flowRemoved.getMatch());
	}
	
	public boolean contains(OFMatch match) {
		if ((match.getWildcards() & OFMatch.OFPFW_DL_VLAN) != OFMatch.OFPFW_DL_VLAN) {
			return true;
		} else {
			return false;
		}
	}

	public OFFlowMod insert(OFFlowMod flowmod) {
		OFMatch match = flowmod.getMatch();
		OFMatch newMatch;
		OFFlowMod newFlowmod;
		
		// Set the VLAN ID
		newMatch = match.setDataLayerVirtualLan(vlanId);
		
		// Set the wildcard bits
		newMatch = newMatch.setWildcards(match.getWildcards() | OFMatch.OFPFW_DL_VLAN);
		
		// Update the flowmod
		newFlowmod = flowmod.setMatch(newMatch);
		
		return newFlowmod;
	}
	
	public OFFlowRemoved insert(OFFlowRemoved flowRemoved) {
		OFMatch match = flowRemoved.getMatch();
		OFMatch newMatch;
		
		// Set the VLAN ID
		newMatch = match.setDataLayerVirtualLan(vlanId);
		
		// Set the wildcard bits
		newMatch = newMatch.setWildcards(match.getWildcards() | OFMatch.OFPFW_DL_VLAN);
		
		// Update the flowRemoved
		flowRemoved.setMatch(newMatch);
		
		return flowRemoved;
	}

	public OFFlowMod delete(OFFlowMod flowmod) {
		OFMatch match = flowmod.getMatch();
		OFMatch newMatch;
		OFFlowMod newFlowmod;
		
		// Set the VLAN ID to 0
		// FIXME: make sure 0 is the right number to use here
		newMatch = match.setDataLayerVirtualLan((short) 0);
		
		// Set the wildcard bits
		newMatch = newMatch.setWildcards(match.getWildcards() & (~OFMatch.OFPFW_DL_VLAN));
		
		// Update the flowmod
		newFlowmod = flowmod.setMatch(newMatch);
		
		return newFlowmod;
	}
	
	public OFFlowRemoved delete(OFFlowRemoved flowRemoved) {
		OFMatch match = flowRemoved.getMatch();
		OFMatch newMatch;
		
		// Set the VLAN ID to 0
		// FIXME: make sure 0 is the right number to use here
		newMatch = match.setDataLayerVirtualLan((short) 0);
		
		// Set the wildcard bits
		newMatch = newMatch.setWildcards(match.getWildcards() & (~OFMatch.OFPFW_DL_VLAN));
		
		// Update the flowmod
		flowRemoved.setMatch(newMatch);
		
		return flowRemoved;
	}

	public OFFlowMod replace(OFFlowMod flowmod) {
		// Delete and insert
		OFFlowMod newFlowmod = this.delete(flowmod);
		newFlowmod = this.insert(newFlowmod);
		
		return newFlowmod;
	}
	
	public OFFlowRemoved replace(OFFlowRemoved flowRemoved) {
		// Delete and insert
		OFFlowRemoved newFlowRemoved = this.delete(flowRemoved);
		newFlowRemoved = this.insert(newFlowRemoved);
		
		return newFlowRemoved;
	}
	
	public short getVlanId() {
		return vlanId;
	}

	private byte[] vlanIdInBytes() {
		return new byte[] {(byte) (vlanId & 0xFF), (byte) (vlanId >>> 8)};
	}
}
