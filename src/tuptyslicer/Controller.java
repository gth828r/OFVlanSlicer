package tuptyslicer;

public class Controller {

	protected String hostname;
	
	protected int port;
	
	public Controller(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	public int getPort() {
		return this.port;
	}
}
