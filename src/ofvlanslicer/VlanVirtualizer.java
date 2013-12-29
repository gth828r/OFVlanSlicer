package ofvlanslicer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;

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
			OFMatch match = flowmod.getMatch();
			if (match.getDataLayerVirtualLan() == vlanId) {
				return true;
			}
		}
		
		return false;
	}

	public boolean contains(OFFlowMod flowmod) {
		OFMatch match = flowmod.getMatch();
		if ((match.getWildcards() & OFMatch.OFPFW_DL_VLAN) != OFMatch.OFPFW_DL_VLAN) {
			return true;
		}		 
		
		return false;
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

	public OFFlowMod replace(OFFlowMod flowmod) {
		// Delete and insert
		OFFlowMod newFlowmod = this.delete(flowmod);
		newFlowmod = this.insert(newFlowmod);
		
		return newFlowmod;
	}
	
	public short getVlanId() {
		return vlanId;
	}

	private byte[] vlanIdInBytes() {
		return new byte[] {(byte) (vlanId & 0xFF), (byte) (vlanId >>> 8)};
	}
}
