package tuptyslicer;

import org.openflow.protocol.OFFlowMod;

public interface FlowmodVirtualizable {

	public OFFlowMod virtualizeFlowmod(OFFlowMod flowmod, Slicelet slicelet);
}
