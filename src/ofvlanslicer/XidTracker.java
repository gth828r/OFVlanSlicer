package ofvlanslicer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Random;

public class XidTracker {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected ConcurrentHashMap<Integer, Slice> slicerXidToSlice;
	protected ConcurrentHashMap<Integer, Integer> slicerXidToControllerXid;
	
	private static int NEXT_LOCALLY_GENERATED_XID = Math.abs(new Random().nextInt());
	
	public XidTracker() {
		LOGGER.finest("Creating new XID tracker");
		slicerXidToSlice = new ConcurrentHashMap<Integer, Slice>();
		slicerXidToControllerXid = new ConcurrentHashMap<Integer, Integer>();
	}
	
	public synchronized int reserveXid(Slice slice) {
		int myXid = NEXT_LOCALLY_GENERATED_XID++;
		slicerXidToSlice.put(myXid, slice);
		return myXid;
	}
	
	public synchronized int reserveXid(Slice slice, int controllerXid) {
		int slicerXid = reserveXid(slice);
		slicerXidToControllerXid.put(slicerXid, controllerXid);
		return slicerXid;
	}
	
	public synchronized void releaseXid(int xid) {
		slicerXidToSlice.remove(xid);
	}
	
	public Slice getSliceByXid(int xid) {
		return slicerXidToSlice.get(xid);
	}
	
	public synchronized int getControllerXid(int slicerXid) {
		return slicerXidToControllerXid.get(slicerXid);
	}
}