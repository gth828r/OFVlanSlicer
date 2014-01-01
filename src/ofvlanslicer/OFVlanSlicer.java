package ofvlanslicer;

import java.util.Set;
import java.util.TreeSet;
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
		Thread deviceManagerListener = new Thread(deviceManager, "Device Manager New Connection Listener");
		
		deviceManagerListener.start();
		
		Slicer slicer = new Slicer(deviceManager, controllerManager, config);
		
		controllerManager.setSlicer(slicer);
		deviceManager.setSlicer(slicer);
		
		runUnitTests(slicer, controllerManager, deviceManager);
	}
	
	private static void runUnitTests(Slicer slicer, ControllerConnectionManager controllerManager, DeviceConnectionManager deviceManager) {
		
		LOGGER.info("Beginning unit test");
		
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
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LOGGER.severe("Couldn't sleep on unit test, so just bail");
				System.exit(1);
			}
		}
	}

}
