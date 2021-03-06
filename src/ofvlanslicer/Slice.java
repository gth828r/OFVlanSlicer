package ofvlanslicer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFFlowRemoved;

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

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	//has a controller
	protected Controller controller;
	
	// Map of device port to slicelet
	protected Set<Slicelet> slicelets;
	
	protected Set<ControllableDevice> devices;
	
	protected Set<VlanOnDevice> vlansOnDevices;
	
	//has a flow count
	protected int flowCount;
	
	public Slice(Controller controller) {
		this.controller = controller;
		this.slicelets = new HashSet<Slicelet>();
		LOGGER.finest("Creating slice for controller " + controller);
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
	
	protected Slicelet getSlicelet(ControllableDevice device, OFFlowRemoved flowRemoved) {
		for (Slicelet slicelet : slicelets) {
			if (slicelet.matches(device, flowRemoved)) {
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
	
	public Set<Slicelet> getSlicelets() {
		return this.slicelets;
	}
	
	/**
	 * Check whether or not a slice includes a given controllable device port
	 * @param port controllable port to check for within slicelet
	 * @return whether or not the slicelet contains the port
	 */
	protected boolean containsPort(ControllableDevice device, Short port) {
		for (Slicelet slicelet : this.slicelets) {
			if (slicelet.containsPort(device, port)) {
				return true;
			}
		}

		return false;
	}
}
