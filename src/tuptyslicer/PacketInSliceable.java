package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;

/**
 * 
 * @author tupty
 *
 */
public interface PacketInSliceable {

	public Slice slicePacketIn(Packet packet, Slicelet slicelet);
}
