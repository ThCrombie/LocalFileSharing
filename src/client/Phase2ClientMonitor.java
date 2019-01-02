package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Phase2ClientMonitor implements Phase2ClientMonitorInterface{
	File clientFiles = new File("CLIENT_FOLDER");
	/*
	 * (non-Javadoc)
	 * @see client.Phase2ClientMonitorInterface#getClientFilenames()
	 * this simply gets a string arraylist of filenames in CLIENT_FOLDER and displays them on the gui
	 */
	public ArrayList<String> getClientFilenames() {
		ArrayList<String> clientFilenames = new ArrayList<String>(Arrays.asList(clientFiles.list()));
		return clientFilenames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see client.Phase2ClientMonitorInterface#playSelectedFile(java.lang.String)
	 * this method will play an .mp3 or .wav sound file. basic stuff. Still needs a means of pausing or stopping.
	 */
	public void playSelectedFile(String selectedSoundFile) throws UnsupportedEncodingException {
		selectedSoundFile = URLEncoder.encode(selectedSoundFile, "UTF-8").replace("+", " "); // space works now for some reason???
		
		if(!selectedSoundFile.contains(".mp3") || !selectedSoundFile.contains(".wav") || !selectedSoundFile.contains(".mp4")) {
			if(selectedSoundFile == null) {
				System.out.println("WARNING, NO FILE SELECTED!");
			}else {
				//Media sound = new Media(serverFolder+"/"+selectedSoundFile);
				
				Media sound = new Media(Paths.get(clientFiles+"/"+selectedSoundFile).toUri().toString());
				MediaPlayer playSound = new MediaPlayer(sound);
				
				playSound.play();
				// how to stop a stream?
			}
		}else {
			System.out.println("SELECTED FILETYPE IS NOT AN MP3, WAV, OR MP4.");
		}	
	}
}
