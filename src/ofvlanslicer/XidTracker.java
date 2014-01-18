package ofvlanslicer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Random;

/**
 * Manages XIDs to ensure that each controller-to-switch
 * message has a unique XID without the controller being
 * responsible for maintaining uniqueness.
 * 
 * FIXME: map from deviceXid to slicerXid probably needs
 * to be per-device
 * 
 * @author tupty
 *
 */
public class XidTracker {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	/** Map of slicer-owned XID to the corresponding slice */
	protected ConcurrentHashMap<Integer, Slice> slicerXidToSlice;
	
	/** Map of slicer-owned XID to controller-generated XID */
	protected ConcurrentHashMap<Integer, Integer> slicerXidToControllerXid;
	
	/** Map of slicer-owned XID to device-generated XID */
	protected ConcurrentHashMap<Integer, Integer> slicerXidToDeviceXid;
	
	/** Unique XID generator */
	private static int NEXT_SLICER_XID = Math.abs(new Random().nextInt());
	
	public XidTracker() {
		LOGGER.finest("Creating new XID tracker");
		slicerXidToSlice = new ConcurrentHashMap<Integer, Slice>();
		slicerXidToControllerXid = new ConcurrentHashMap<Integer, Integer>();
		slicerXidToDeviceXid = new ConcurrentHashMap<Integer, Integer>();
	}
	
	/**
	 * Assign a unique XID to the slice for slicer-to-device messages.
	 * 
	 * Use this when a controller-generated non-response 
	 * message has been received by the slicer.
	 * 
	 * @param slice
	 * @return unique XID that has been assigned
	 */
	public synchronized int reserveSlicerXidFromControllerXid(Slice slice, int controllerXid) {
		int myXid = NEXT_SLICER_XID++;
		slicerXidToSlice.put(myXid, slice);
		slicerXidToControllerXid.put(myXid, controllerXid);
		return myXid;
	}

	/**
	 * Assign a unique XID to device-generated XID for for slicer-to-controller messages.
	 * 
	 * Use this when a device-generated non-response message has been received
	 * by the slicer.
	 * 
	 * @param slice
	 * @param controllerXid
	 * @return
	 */
	public synchronized int reserveSlicerXidFromDeviceXid(Slice slice, int deviceXid) {
		int myXid = NEXT_SLICER_XID++;
		slicerXidToSlice.put(myXid, slice);
		slicerXidToDeviceXid.put(myXid, deviceXid);
		return myXid;
	}
	
	/**
	 * Release an slicer-to-device XID after the device response has fully
	 * been received
	 * 
	 * @param xid slicer XID that is mapped to target controller XID
	 */
	public synchronized void releaseDeviceXidMapping(int slicerXid) {
		slicerXidToSlice.remove(slicerXid);
		slicerXidToDeviceXid.remove(slicerXid);
	}
	
	/**
	 * Release an slicer-to-controller XID after the device response has fully
	 * been received
	 * 
	 * @param xid slicer XID that is mapped to target controller XID
	 */
	public synchronized void releaseControllerXidMapping(int slicerXid) {
		slicerXidToSlice.remove(slicerXid);
		slicerXidToControllerXid.remove(slicerXid);
	}
	
	public synchronized int getControllerXidMapping(int slicerXid) {
		return slicerXidToControllerXid.get(slicerXid);
	}
	
	public synchronized int getDeviceXidMapping(int slicerXid) {
		return slicerXidToDeviceXid.get(slicerXid);
	}
	
	/**
	 * Return the slice by slicer XID
	 * @param xid key to look up slice
	 * @return Slice that this XID belongs to
	 */
	public synchronized Slice getSliceBySlicerXid(int slicerXid) {
		return slicerXidToSlice.get(slicerXid);
	}
}