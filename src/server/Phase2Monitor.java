package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Phase2Monitor implements Phase2MonitorInterface{

	@Override
	public ArrayList<String> serverFileNames() {
		/*
		 * this simply gets an arraylist of all the items in the server folder.
		 * the arraylist will get sent back to the client, updating their display
		 * on the gui.
		 * the client will use this list to display the contents, but not download the actual files.
		 */
		
		File serverContent = new File("SERVER_FOLDER");
		ArrayList<String> serverFilenames = new ArrayList<String>(Arrays.asList(serverContent.list()));
		return serverFilenames; // returns an arraylist 
	}

}
