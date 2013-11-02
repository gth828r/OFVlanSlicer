package tuptyslicer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.openflow.io.OFMessageAsyncStream;

public class OpenFlowControllerHandler {

	protected List<OFMessageAsyncStream> connections;
	
	// map of connection to ControllableDevice
	protected ConcurrentHashMap<OFMessageAsyncStream, ControllableDevice> controllableDevices;
	
	// map of connection to Controller
	protected ConcurrentHashMap<OFMessageAsyncStream, Controller> controllers;
	
	
	
}
