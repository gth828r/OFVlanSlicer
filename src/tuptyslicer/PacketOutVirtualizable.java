package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;

/**
 * 
 * @author tupty
 *
 */
public interface PacketOutVirtualizable {

	public Packet virtualizePacketOut(Packet packet, Slicelet slicelet);
}
