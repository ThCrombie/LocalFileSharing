package client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Phase2ClientMain extends Application {
	// monitor, for local opertations (displaying a list of filenames on local machine)
	Phase2ClientMonitor monitor = new Phase2ClientMonitor();
	
	DataOutputStream data_out = null;
	DataInputStream data_in = null;
	ObjectInputStream object_in = null;
	ObjectOutputStream object_out = null;
	
	// to send strings:
	OutputStream stringStream;
	PrintWriter stringOut;
	
	InputStream in;
	OutputStream out;
    FileOutputStream file_output = null;
    BufferedOutputStream buffered_out = null;
    
    // text area, so the gui can display information, or errors
    TextArea ta = new TextArea();
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		new File("CLIENT_FOLDER").mkdirs(); // creates a client folder if one isnt already made
		
		Text serverInfo = new Text("Files hosted on the server:");
		// server files (a query is made later on once a socket is connected
		ObservableList<String> serverObservableFiles = FXCollections.observableArrayList();
		ListView serverListView = new ListView(serverObservableFiles); // server file list
		
		Text clientInfo = new Text("Files in your local folder:");
		ObservableList<String> clientObservableFiles = FXCollections.observableArrayList(checkClientFolder());
		ListView clientListView = new ListView(clientObservableFiles); // client file list
		
		// the GUI is 200 by 250 pixels wide, no need for the user to be able to resize the window
		serverListView.setPrefSize(200, 250);
	    serverListView.setEditable(false); 
	    clientListView.setPrefSize(200, 250);
	    clientListView.setEditable(false);
	    
	    /*
	     * this will check the local, and server folders for updates.
	     */
	    Button update = new Button("Check for updates");
	    update.setOnAction((event) -> {
	     	try {
	     		ta.appendText("Updating folders...\n");
				serverObservableFiles.setAll(checkServerFolder());
				ta.appendText("Done.\n");
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	clientObservableFiles.setAll(checkClientFolder());
	        
	    });// when pressed, checks if the server's folder has any changes, and displays them on the GUI
	    
	    Button download = new Button("Download selected file");
	    /*
	     * this onAction will first send an int of value 1, then send a string of the filename.
	     * the server will respond by sending the file via a bytearray.
	     * Issue:  textfiles are corrupted (contents are garbled)
	     */
	    download.setOnAction((event) -> {
	    	String requestFile = (String) serverListView.getSelectionModel().getSelectedItem();
	    	ta.appendText("Requesting to download \""+requestFile+"\"...\n");
			
	    	try {
				askForFile(requestFile);
				
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	ta.appendText("Downloaded!\n");
	    	clientObservableFiles.setAll(checkClientFolder());
	    });
	    
	    /* 
	     * if an mp3 is in the local folder, this can be played.
	     */
	    Button playSoundFile = new Button("Play downloaded sound file");
	    playSoundFile.setOnAction((event)->{
	    	String playSong = (String) clientListView.getSelectionModel().getSelectedItem();
	    	try {
				monitor.playSelectedFile(playSong);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }); // runs JavaFX's Scene Mediaplayer to play MP3's etc on the selected file.
	    // (need to add button to pause and rewind)
	    
	    /*
	     * basically the download function but in reverse
	     * Issue: the server doesnt like it when a string is sent (???)
	     * still crashes
	     */
	    Button uploadToServer = new Button("Upload a file to the server");
	    uploadToServer.setOnAction((event)->{
	    	// upload a file to the server
	    	String uploadFile = (String) clientListView.getSelectionModel().getSelectedItem();
	    	try {
				data_out.writeInt(2); // 'im uploading a file'
				Thread.sleep(5);
				data_out.flush();
				object_out.writeObject(uploadFile); // this is the filename of the file i want to upload
				object_out.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	
        	ta.appendText("Uploading \""+uploadFile+"\"....\n");
        	File toSend = new File("CLIENT_FOLDER/"+uploadFile);
        	
        	// make a new thread to send a file
        	new Thread( () -> {
        		try {
        			FileInputStream file_in = null;
        			BufferedInputStream buffered_input = null;
					data_out.writeInt((int)toSend.length());
					byte[] byteArray = new byte[(int)toSend.length()];
	        		file_in = new FileInputStream(toSend);
	        		buffered_input = new BufferedInputStream(file_in);
	        		buffered_input.read(byteArray,0,byteArray.length);
	        		
	        		out.write(byteArray,0,byteArray.length);
	        		out.flush();
	        		byteArray = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		// send the file via a byte array
        		
        		ta.appendText("File sent!\n");
        		}).start();
	    }); // runs a method in the Monitor to open a filechooser, the user can upload a file to the "server folder" 

	    // this will have the server folder items on the left, and the client folder files on the right
		GridPane gridPane = new GridPane();
	    gridPane.setMinSize(400, 200);
	    gridPane.setPadding(new Insets(10, 10, 10, 10)); 
	    gridPane.setVgap(5); 
	    gridPane.setHgap(5);       
	    gridPane.setAlignment(Pos.CENTER);
	    
	    // gridPane.add(child, columnIndex, rowIndex);
	    gridPane.add(serverInfo, 0, 0);
	    gridPane.add(serverListView, 0, 1);
	    gridPane.add(update, 0, 2);
	    gridPane.add(download, 0, 3);
	    
	    gridPane.add(clientInfo, 1,0);
	    gridPane.add(clientListView, 1, 1);
	    
	    
	    gridPane.add(playSoundFile, 1, 2);
	    gridPane.add(uploadToServer, 1, 3);
	    
	    gridPane.add(ta, 0, 4);
	    
	     // this layout has the server's filenames displayed on the left, and the client's on the right.
	     // the buttons are underneath, and do what they say on the tin
	     
		Scene scene = new Scene(gridPane); 
	    primaryStage.setTitle("Phase 2 Client Sockets"); 
	    primaryStage.setScene(scene);
	    primaryStage.setMinHeight(600);
	    primaryStage.setMinWidth(500);
		primaryStage.show();
		try {
		      // socket on localhost, port 8000 seems to work
		      Socket socket = new Socket("localhost", 8000);
		      ta.appendText("Connecting to " + socket.getLocalAddress() + " on port: " + socket.getLocalPort()+"...\n");
		      // streams, via sockets
		      data_in = new DataInputStream(socket.getInputStream());
		      data_out = new DataOutputStream(socket.getOutputStream());
		      object_in = new ObjectInputStream(socket.getInputStream());
		      object_out = new ObjectOutputStream(socket.getOutputStream());
		      
		      // to ask for filenames:
		      stringStream = socket.getOutputStream();
		      stringOut = new PrintWriter(stringStream);
		      
		      in = socket.getInputStream();
		      out = socket.getOutputStream();
		      
		      // at startup, ask for the server folder (SERVER NEEDS TO BE RUNNING !)
		      ta.appendText("Getting the list of files from "+ socket.getLocalAddress()+ " on port: " +socket.getLocalPort() + "...\n");
		      try {
				serverObservableFiles.setAll(checkServerFolder());
				ta.appendText("List updated!\n");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		    catch (IOException ex) {
		      ta.appendText(ex.toString() + '\n');
		    }
		  }
		  
		  // locally done, no security issues here
		  public ArrayList<String> checkClientFolder(){
			  ArrayList<String> arrayOfNames = monitor.getClientFilenames();
			  return arrayOfNames;
			  }
		  // done via sockets
		  public ArrayList<String> checkServerFolder() throws ClassNotFoundException, IOException{
			  /*
			   * sends an int '0' to notify the server it wants the list of files on the server.
			   * when the server recieves a int 0 via the socket, it will send an arraylist over as an object.
			   */
			  data_out.writeInt(0);
			  ArrayList<String> updated;
			  Object obj = object_in.readObject();
			  updated = (ArrayList<String>) obj;
			  return updated;
			  }
		  public void askForFile(String filename) throws IOException, ClassNotFoundException {
			  data_out.writeInt(1); // if the server gets a int with value 1, it will send a file
			  object_out.writeObject(filename);
			  File to_download = new File("CLIENT_FOLDER/"+filename); // creates a 'file object' and gets ready to write to the client folder
			  object_out.flush();
			  int fileSize;
			  fileSize = data_in.readInt(); // the server sends back the size of the file in bytes. i thought this might fix the corruption error...
			  ta.appendText("The file \"" + filename+ "\" is "+fileSize+" bytes\n");
			 /* DOWNLOADING FILES NEED TO BE IN A THREAD! CRASHES OTHERWISE! */
			  /* THE FILES GET CORRUPTED - WRONG DATA STREAM TYPES FOR SERVER AND CLIENT ??? */
			  new Thread(()->{
				  /* 
				   * a thread for the client program to run once and download a file.  
				   */
				  int bytes_read;
				  int current = 0;
				  try {
					out = new FileOutputStream(to_download);
					byte[] buffer = new byte[fileSize+1];
					while((bytes_read = in.read(buffer)) != 0) {
						  out.write(buffer,0,bytes_read);
						  out.flush();
					  }
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			  }).start();
			  
			 
		  }
		  
		  public static void main(String[] args) {
		    launch(args);
		  }
		}
		
