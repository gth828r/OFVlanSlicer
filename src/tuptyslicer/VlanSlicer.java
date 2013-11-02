package tuptyslicer;

import java.util.Set;
import java.util.TreeSet;

import org.openflow.protocol.OFFlowMod;
import org.opendaylight.controller.sal.packet.Packet;

/**
 * @author tupty
 *
 * Perform slicing actions on control plane traffic based on VLAN IDs
 */
public class VlanSlicer extends Slicer implements FlowmodVerifiable, 
	FlowmodVirtualizable, PacketInSliceable, PacketOutVerifiable, 
	PacketOutVirtualizable {
	
	protected Set<Slice> slices;
	
	public VlanSlicer() {
		slices = new TreeSet<Slice>();
	}
	
	private Slicelet mapDownstreamToSlicelet(Packet packet, ControllableDevice device, ControllableDevicePort port) {
		for (Slice slice : slices) {
			if (sl)
		}
	}
	
	@Override
	public Packet virtualizePacketOut(Packet packet, Slicelet slicelet) {
		Packet newPacket = packet;
		VlanDiscriminant discriminant = slicelet.getDiscriminant();

		if (!discriminator.containsDiscriminantField(packet)) {
			newPacket = discriminator.insertDiscriminantField(packet, discriminant);
		}
		
		return newPacket;
	}

	@Override
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

	@Override
	public Slice getSliceFromPacket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyPacketOut(Packet packet, Slicelet slicelet) {
		if (!discriminator.containsDiscriminantField(packet)) {
			/* Untagged traffic will be virtualized, so it is OK */
			return true;
		} else {
			if (discriminator.matchesDiscriminant(packet, slicelet.getDiscriminant())) {
				/* Traffic is tagged with the appropriate VLAN ID */
				return true;
			} else {
				/* Traffic is tagged with an incorrect VLAN ID */
				return false;
			}
		}
	}

	@Override
	public Slice slicePacketIn(Packet packet, Slicelet slicelet) {
		//FIXME not really implemented
		return sliceletToSlice.get(slicelet);
	}

	@Override
	public OFFlowMod virtualizeFlowmod(OFFlowMod flowmod, Slicelet slicelet) {
		OFFlowMod newFlowmod;
		
		// get discriminant for slice/device (we store this)
		Discriminant discriminant = slicelet.getDiscriminant();

		if (discriminator.containsDiscriminantField(flowmod)) {
			newFlowmod = discriminator.replaceDiscriminantField(flowmod, discriminant);
		} else {
			newFlowmod = discriminator.insertDiscriminantField(flowmod, discriminant);
		}
		
		return newFlowmod;
	}

	@Override
	public boolean verifyFlowmod(OFFlowMod flowmod, Slicelet slicelet) {		
		if (discriminator.containsDiscriminantField(flowmod)) {
			if (discriminator.matchesDiscriminant(flowmod, slicelet.getDiscriminant())) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void handlePacketFromControllableDevice(Packet packet,
			ControllableDevice device, ControllableDevicePort port) {
		

		
	}
}
