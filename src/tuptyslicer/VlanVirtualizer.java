package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.packet.IEEE8021Q;
import org.openflow.protocol.OFFlowMod;

public class VlanVirtualizer  {

	short vlanId;
	
	public VlanVirtualizer(short vlanId) {
		this.vlanId = vlanId;
	}

	public boolean matches(Packet packet) {
		// TODO Auto-generated method stub
		
		return false;
	}

	public boolean contains(Packet packet) {
		if (packet instanceof Ethernet) {
			short packetEthertype = ((Ethernet) packet).getEtherType();
			
			if (packetEthertype == EtherTypes.VLANTAGGED.shortValue()) {
				return true;
			}
		}
		
		return false;
	}

	public Packet insert(Packet packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	public Packet delete(Packet packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	public Packet replace(Packet packet) {
		//FIXME set this based on discriminant
		short vid = 0;
		IEEE8021Q vlanPacket = (IEEE8021Q) packet;
		vlanPacket.setVid(vid);
		
		return (Packet) vlanPacket;
	}

	public boolean matches(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return false;
	}

	public OFFlowMod insert(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

	public OFFlowMod delete(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

	public OFFlowMod replace(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

}
