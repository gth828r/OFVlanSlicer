package ofvlanslicer;

import java.util.logging.Logger;

public class Controller {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected String hostname;
	
	protected int port;
	
	public Controller(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		LOGGER.finest("Creating controller with host " + hostname + ", port " + port);
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public int getPort() {
		return this.port;
	}
}
