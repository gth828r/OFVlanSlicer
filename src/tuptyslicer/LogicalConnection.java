package tuptyslicer;

import java.io.IOException;

import org.openflow.io.OFMessageAsyncStream;
import org.openflow.protocol.OFMessage;

public class LogicalConnection {
	protected Controller controller;
	
	protected ControllableDevice device;
	
	protected OFMessageAsyncStream controllerConnection;
	
	protected OFMessageAsyncStream deviceConnection;
	
	protected void sendToController(OFMessage message) {
		try {
			controllerConnection.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void sendToDevice(OFMessage message) {
		try {
			deviceConnection.write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
