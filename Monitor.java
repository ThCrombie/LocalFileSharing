package localFileSharing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Monitor implements monitorInterface {
	/*
	 * singleton pattern: only allows one instance of Monitor to run per client.
	 */
	ProjectSingleton instance = ProjectSingleton.getInstance();
	
	
	//for later:  FileSystemView.getFileSystemView().getDefaultDirectory().getPath()
	//(maps to Documents folder on most OS's)
	
	// creates a static "client folder" and a "server folder"
	String clientFolder = "CLIENT_FOLDER";
	String serverFolder = "SERVER_FOLDER"; // this would be done via socket
	
	public void init() {
		// creates the folders if they dont exist
		new File(clientFolder).mkdirs();
		new File(serverFolder).mkdirs();
	}

	/*
	 * (non-Javadoc)
	 * @see localFileSharing.monitorInterface#getServerFilenames()
	 * gets the names of the files in the server folder and returns them in a String arraylist
	 */
	public ArrayList<String> getServerFilenames() {
		File serverContent = new File(serverFolder);
		ArrayList<String> serverFilenames = new ArrayList<String>(Arrays.asList(serverContent.list()));
		return serverFilenames;
	}
	/*
	 * (non-Javadoc)
	 * @see localFileSharing.monitorInterface#getClientFilenames()
	 * does the same as the server version
	 */
	public ArrayList<String> getClientFilenames() {
		File clientFiles = new File(clientFolder);
		ArrayList<String> clientFilenames = new ArrayList<String>(Arrays.asList(clientFiles.list()));
		return clientFilenames;
	} 
	
	/*
	 * (non-Javadoc)
	 * @see localFileSharing.monitorInterface#download(java.lang.String)
	 * (will use sockets in week 12 deliverable)
	 * 'downloads' files from the server folder and puts them in the clients folder.
	 */
	public void download(String selectedFile) throws IOException {
		// filenames with a space in them crashed the program; could be because I'm running MacOS
		// this makes sure the space is the UTF-8 encoded space.
		selectedFile = URLEncoder.encode(selectedFile, "UTF-8").replace("+", " ");
		
		/*
		if(selectedFile.contains(" ")){
			selectedFile.replace(" ", "%20"); // makes sure java doesnt freak out when it sees a space
		}*/
		
		if(selectedFile == null) {
			System.out.println("WARNING, NO FILE SELECTED!");
		}else {
			InputStream getFromServer = new FileInputStream(serverFolder+"/"+selectedFile);
			byte []b=new byte[5000];
			OutputStream download = new FileOutputStream(clientFolder+"/"+selectedFile);
		
			getFromServer.read(b,0,b.length);
			download.write(b, 0, b.length);
		}
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see localFileSharing.monitorInterface#playSelectedFile(java.lang.String)
	 * uses Mediaplayer to play music if the file ends with the supported file extension
	 */
	public void playSelectedFile(String selectedSoundFile) throws UnsupportedEncodingException {
		/*
		String bip = "bip.mp3";
		Media hit = new Media(new File(bip).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
		 */
		
		selectedSoundFile = URLEncoder.encode(selectedSoundFile, "UTF-8").replace("+", " "); // space works now for some reason???
		
		if(!selectedSoundFile.contains(".mp3") || !selectedSoundFile.contains(".wav") || !selectedSoundFile.contains(".mp4")) {
			if(selectedSoundFile == null) {
				System.out.println("WARNING, NO FILE SELECTED!");
			}else {
				//Media sound = new Media(serverFolder+"/"+selectedSoundFile);
				
				Media sound = new Media(Paths.get(clientFolder+"/"+selectedSoundFile).toUri().toString());
				MediaPlayer playSound = new MediaPlayer(sound);
				
				playSound.play();
				// how to stop a stream?
			}
		}else {
			System.out.println("SELECTED FILETYPE IS NOT AN MP3, WAV, OR MP4.");
		}
		
		
		
		
	}

	@Override
	public void uploadFile(Stage programStage) throws IOException {
		// filechooser to select a file to upload to the program's "server" folder
		FileChooser selectFileToUpload = new FileChooser();
		selectFileToUpload.setTitle("Select a file to upload to the server folder");
		File defaultDirectory = new File(clientFolder);
		selectFileToUpload.setInitialDirectory(defaultDirectory);
		
		File selectedFile = selectFileToUpload.showOpenDialog(programStage);
		
		if(selectedFile == null) {
				System.out.println("WARNING, NO FILE SELECTED!");
			}else {
				System.out.println(selectedFile.toString());
				
				InputStream uploadToServer = new FileInputStream(selectedFile);
				
				byte []b=new byte[5000];
				OutputStream upload = new FileOutputStream(serverFolder+"/"+selectedFile.getName().toString());
				uploadToServer.read(b,0,b.length);
				upload.write(b, 0, b.length);
			}
		
		
		
	}
	
	
	

}
