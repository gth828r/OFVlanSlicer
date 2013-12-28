package ofvlanslicer;

import java.util.logging.Logger;

public class ConnectionManagerKey {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected Slice slice;
	
	protected Controller controller;
	
	protected ControllableDevice device;
	
	public ConnectionManagerKey(Slice slice, Controller controller, ControllableDevice device) {
		this.slice = slice;
		this.controller = controller;
		this.device = device;
	}
	
	public Slice getSlice() {
		return this.slice;
	}
	
	public Controller getController() {
		return this.controller;
	}
	
	public ControllableDevice getDevice() {
		return this.device;
	}
}
