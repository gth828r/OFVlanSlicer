package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;

/**
 * 
 * @author tupty
 *
 */
public interface PacketOutVerifiable {

	public boolean verifyPacketOut(Packet packet, Slicelet slicelet);
}
