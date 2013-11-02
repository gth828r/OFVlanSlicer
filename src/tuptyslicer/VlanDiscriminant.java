package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.packet.IEEE8021Q;
import org.openflow.protocol.OFFlowMod;

public class VlanDiscriminant extends Discriminant<Integer> {

	public VlanDiscriminant(Integer vlanId) {
		super(vlanId);
	}

	@Override
	public boolean matches(Packet packet) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean contains(Packet packet) {
		if (packet instanceof Ethernet) {
			short packetEthertype = ((Ethernet) packet).getEtherType();
			
			if (packetEthertype == EtherTypes.VLANTAGGED.shortValue()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Packet insert(Packet packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public Packet delete(Packet packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public Packet replace(Packet packet) {
		//FIXME set this based on discriminant
		short vid = 0;
		IEEE8021Q vlanPacket = (IEEE8021Q) packet;
		vlanPacket.setVid(vid);
		
		return (Packet) vlanPacket;
	}

	@Override
	public boolean matches(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OFFlowMod insert(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFFlowMod delete(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFFlowMod replace(OFFlowMod flowmod) {
		// TODO Auto-generated method stub
		return null;
	}

}
