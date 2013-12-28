package ofvlanslicer;

public class ConnectionManagerKey {

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
