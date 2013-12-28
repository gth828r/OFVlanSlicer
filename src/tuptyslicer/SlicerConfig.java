package tuptyslicer;

import java.util.logging.Level;

public class SlicerConfig {

	private static final boolean DEFAULT_UNKNOWN_MSG_STRICT = true;
	
	private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;
	
	private static final short DEFAULT_SERVER_LISTENER_PORT = 6653;
	
	private boolean unknownMsgStrict;
	
	private Level logLevel;
	
	private short serverListenerPort;
	
	public SlicerConfig () {
		unknownMsgStrict = DEFAULT_UNKNOWN_MSG_STRICT;
		logLevel = DEFAULT_LOG_LEVEL;
		serverListenerPort = DEFAULT_SERVER_LISTENER_PORT;
	}
	
	public SlicerConfig (String configFilePath) {
		this();
		this.readConfig(configFilePath);
	}
	
	public boolean getUnknownMsgStrict() {
		return this.unknownMsgStrict;
	}
	
	public Level getLogLevel() {
		return this.logLevel;
	}
	
	public short getServerListenerPort() {
		return this.serverListenerPort;
	}
	
	public void readConfig(String configFilePath) {
		
	}
	
}
