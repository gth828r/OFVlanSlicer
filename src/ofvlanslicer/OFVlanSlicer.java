package ofvlanslicer;

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
		
		//Slice slice = new Slice();
		
		//FIXME: this is a hack to get things working
		while (true) {
			controllerManager.readAll();
			deviceManager.readAll();
		}
	}

}
