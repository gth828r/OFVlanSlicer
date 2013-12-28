package ofvlanslicer;
import java.util.Set;
import java.util.TreeSet;

import org.openflow.protocol.OFFlowMod;

import edu.huji.cs.netutils.parse.EthernetFrame;

/**
 * @author tupty
 *
 * A slice is a piece of the entire network that belongs to a single entity.
 * 
 * It includes a single controller, and a set of slicelets, as well as
 * some other meta-information 
 */
public class Slice {

	//has a controller
	protected Controller controller;
	
	// Map of device port to slicelet
	protected Set<Slicelet> slicelets;
	
	protected Set<ControllableDevice> devices;
	
	//has a flow count
	protected int flowCount;
	
	public Slice(Controller controller) {
		this.controller = controller;
		this.slicelets = new TreeSet<Slicelet>();
	}
	
	protected void addSlicelet(Slicelet slicelet) {
		slicelets.add(slicelet);
	}
	
	/**
	 * Check if incoming topology information matches a slicelet in this slice 
     */
	protected Slicelet getSlicelet(ControllableDevice device, Short port, EthernetFrame frame) {
		for (Slicelet slicelet : slicelets) {
			if (slicelet.matches(device, port, frame)) {
				return slicelet;
			}
		}
		
		return null;
	}
	
	/**
	 * Check if incoming topology information matches a slicelet in this slice 
     */
	protected Slicelet getSlicelet(ControllableDevice device, OFFlowMod flowmod) {
		for (Slicelet slicelet : slicelets) {
			if (slicelet.matches(device, flowmod)) {
				return slicelet;
			}
		}
		
		return null;
	}
	
	public Controller getController() {
		return this.controller;
	}
	
	public Set<ControllableDevice> getDevices() {
		return this.devices;
	}
}
