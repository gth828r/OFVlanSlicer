package ofvlanslicer;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;

public class ManagementConnectionManager {

	private static final Logger LOGGER = Logger.getLogger(
		    Thread.currentThread().getStackTrace()[0].getClassName() );
	
	protected Slicer slicer;
	
	public ManagementConnectionManager(Slicer slicer) {
		this.slicer = slicer;
	}
	
	@GET
	public String getSlice() {
		//FIXME implement this
		return "";
	}
	
	@POST
	public String createSlice() {
		//FIXME implement this
		
		LOGGER.info("Creating slice");
		
		return "";
	}

	@PUT
	public String updateteSlice() {
		//FIXME implement this
		
		LOGGER.info("Updating slice");
		
		return "";
	}
	
	@DELETE
	public String deleteSlice() {
		//FIXME implement this
		
		LOGGER.info("Deleting slice");
		
		return "";
	}
	
}
