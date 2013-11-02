package tuptyslicer;

import java.util.Set;

public class VlanSlicelet extends Slicelet {

	public VlanSlicelet(Slice parent, int vlanId,
			ControllableDevice device, Set<ControllableDevicePort> ports) {
		super(parent, device, ports);
		super.discriminant = new VlanDiscriminant(vlanId);
	}

}
