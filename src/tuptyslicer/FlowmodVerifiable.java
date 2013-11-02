package tuptyslicer;

import org.openflow.protocol.OFFlowMod;

/**
 * 
 * @author tupty
 *
 */
public interface FlowmodVerifiable {

	public boolean verifyFlowmod(OFFlowMod flowmod, Slicelet slicelet);
}
