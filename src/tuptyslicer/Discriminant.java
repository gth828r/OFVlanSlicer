package tuptyslicer;

import org.opendaylight.controller.sal.packet.Packet;
import org.openflow.protocol.OFFlowMod;

public abstract class Discriminant<Value> {
	private Value value;
	
	public Discriminant(Value value) {
		this.value = value;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	public abstract boolean contains(Packet packet);
	
	public abstract boolean matches(Packet packet);

	public abstract Packet insert(Packet packet);
	
	public abstract Packet delete(Packet packet);
	
	public abstract Packet replace(Packet packet);
	
	public abstract boolean contains(OFFlowMod flowmod);
	
	public abstract boolean matches(OFFlowMod flowmod);

	public abstract OFFlowMod insert(OFFlowMod flowmod);
	
	public abstract OFFlowMod delete(OFFlowMod flowmod);
	
	public abstract OFFlowMod replace(OFFlowMod flowmod);
}
