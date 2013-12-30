package ofvlanslicer;

import java.util.logging.Logger;

public class XidTracker {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	//protected ConcurrentHashmap<Long, Slice>
	
	public XidTracker() {
		LOGGER.finest("Creating new XID tracker");
	}
}
