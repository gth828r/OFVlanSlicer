package ofvlanslicer;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OFVlanSlicer {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SlicerConfig config = new SlicerConfig();
		
		ControllerConnectionManager controllerManager = new ControllerConnectionManager();
		DeviceConnectionManager deviceManager = new DeviceConnectionManager(config.getServerListenerPort());
		
		Slicer slicer = new Slicer(deviceManager, controllerManager, config);
		controllerManager.setSlicer(slicer);
		deviceManager.setSlicer(slicer);
		
		runUnitTests(slicer, controllerManager, deviceManager);
	}
	
	private static void runUnitTests(Slicer slicer, ControllerConnectionManager controllerManager, DeviceConnectionManager deviceManager) {
		
		LOGGER.log(Level.ALL, "Beginning unit test");
		
		Controller controller = new Controller("localhost", 6654);
		Slice slice = new Slice(controller);
		ControllableDevice device = new ControllableDevice("localhost", 123);
		short vlanId = 10;
		Set<Short> ports = new TreeSet<Short>();
		ports.add((short) 1);
		ports.add((short) 2);
		
		Slicelet slicelet = new Slicelet(slice, vlanId, device, ports);
		slice.addSlicelet(slicelet);
		
		slicer.addSlice(slice);
		
		//FIXME: this is a hack to get things working
		while (true) {
			controllerManager.readAll();
			deviceManager.readAll();
		}
	}

}
