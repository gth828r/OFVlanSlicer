package ofvlanslicer;

public class VlanOnDevice {

	protected int vlanId;
	
	protected ControllableDevice device;
	
	public VlanOnDevice(int vlanId, ControllableDevice device) {
		this.vlanId = vlanId;
		this.device = device;
	}
	
	public int getVlanId() {
		return this.vlanId;
	}
	
	public ControllableDevice getDevice() {
		return this.device;
	}
}
