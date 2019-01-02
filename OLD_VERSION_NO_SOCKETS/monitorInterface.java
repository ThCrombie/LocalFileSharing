package localFileSharing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javafx.stage.Stage;

public interface monitorInterface {
	void init();
	ArrayList<String> getServerFilenames();
	ArrayList<String> getClientFilenames();
	void download(String selectedFile) throws IOException;
	void playSelectedFile(String selectedSoundFile) throws UnsupportedEncodingException;
	void uploadFile(Stage programStage) throws IOException;
	// ADD WAY TO UPLOAD, AND WAY TO PAUSE MUSIC
}
