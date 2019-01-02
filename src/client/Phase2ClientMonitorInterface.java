package client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/*
 * interface for the Monitor class
 */
public interface Phase2ClientMonitorInterface {
	ArrayList<String> getClientFilenames();
	void playSelectedFile(String selectedSoundFile) throws UnsupportedEncodingException;
}
