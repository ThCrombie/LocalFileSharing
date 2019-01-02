package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Phase2ServerMain extends Application {
  @Override 
  public void start(Stage primaryStage) {
    //  textarea for displaying information
    TextArea ta = new TextArea();
    // gui for the server application
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    ta.autosize();
    primaryStage.setTitle("Server phase 2 Sockets");
    File serverFolder = new File("SERVER_FOLDER");
    serverFolder.mkdirs(); // creates a folder called 'SERVER_FOLDER' in the program's directory
    primaryStage.setScene(scene);
    primaryStage.show();
    
    // thread, for each client joining the server
    /*
     * CANT SEEM TO HANDLE MORE THAN ONE CLIENT.  WHY?
     */
    new Thread( () -> {
      try {
        // server socket:
        ServerSocket servSoc = new ServerSocket(8000);
        Platform.runLater(() ->
          ta.appendText("Server is hosting a folder in " + serverFolder.getAbsolutePath() +".\nStarted running at: " + new Date() + '\n'));
  
        // allows a client to connect, and simply allows it
        Socket socket = servSoc.accept();
  
        // data input and output streams:
        DataInputStream data_in = new DataInputStream(socket.getInputStream());
        DataOutputStream data_out = new DataOutputStream(socket.getOutputStream());
        
        // for sending out and recieving objects when needed:
        ObjectOutputStream object_out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream object_in = new ObjectInputStream(socket.getInputStream());
        
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        //FileInputStream file_in = null;
        //FileOutputStream file_out = null;
        //BufferedInputStream buffered_input = null;
        
        // ^ these are created on the go in a new thread
        
        
        // my monitor class, for the local functions in this program:
        Phase2Monitor serverMonitor = new Phase2Monitor();
        
        // server functionality:
        while (true) {
        	
        	int clientCommand = data_in.readInt();
        	/* 
        	 * depending on what int data is streamed in, the server thread will respond accordingly.
        	 * if an int with the value 0 is streamed in, the server will send an arraylist of
        	 * files over to that client.
        	 * if a 1 is streamed in, it will wait for a string to be sent by the client - this is the filename 
        	 * of a file on the server.  this file is then sent to the client.
        	 * if a 2 is streamed in, it expects a file for upload.
        	 */
        	if(clientCommand == 0) {
        		ta.appendText("Client is requesting a list of server files.\n");
        		// send an arraylist of the filenames to the client:
        		ArrayList<String>filesOnServer = new ArrayList<String>();
            	filesOnServer = serverMonitor.serverFileNames();
            	object_out.writeObject(filesOnServer);
            	object_out.flush();
        		ta.appendText("Done.\n");
        	} else if(clientCommand == 1) {
        		String wantedFile;
        		wantedFile = (String) object_in.readObject();
        		ta.appendText("Client wants to download file \""+wantedFile+"\".\n");
        		File toSend = new File("SERVER_FOLDER/"+wantedFile); // prepares a File object to create
        		ta.appendText("Sending \""+wantedFile+"\"\n");
        		
        		/*
        		 * SEND BACK FILE SIZE
        		 */
        		new Thread( () -> {
        		try {
        			FileInputStream file_in = null;
        			BufferedInputStream buffered_input = null;
					data_out.writeInt((int)toSend.length()); // sends back the byte filesize as an INT - tried to fix the file corruption
					byte[] byteArray = new byte[(int)toSend.length()]; // creates a byte array with the needed size.
	        		file_in = new FileInputStream(toSend);
	        		buffered_input = new BufferedInputStream(file_in);
	        		buffered_input.read(byteArray,0,byteArray.length);
	        		
	        		out.write(byteArray,0,byteArray.length);
	        		buffered_input.close();
	        		file_in.close();
	        		out.flush();
	        		byteArray = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		// send the file via a byte array
        		
        		ta.appendText("File sent!\n");
        		}).start();
        	}else if (clientCommand == 2) {
        		/*
        		 * SEEMS TO CRASH ONCE THE CLIENT SENDS THE FILENAME (STRING ???)
        		 */
  			  int fileSize;
  			  fileSize = data_in.readInt();
  			  //data_in.close();
  			  String uploadedFilename; // it doesnt like recieving a string, why?
  			  try {
				Thread.sleep(10); // wait the client to send the filename
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
  			  uploadedFilename = (String) object_in.readObject(); // crashes here when an upload is attempted
  			  ta.appendText("Client is uploading \"" + uploadedFilename+ "\" which is " + fileSize+" bytes\n");
  			 /* DOWNLOADING FILES NEED TO BE IN A THREAD! CRASHES OTHERWISE! */
  			  /* THE FILES GET CORRUPTED - WRONG DATA STREAM TYPES FOR SERVER AND CLIENT ??? */
  			  new Thread(()->{
  				  int bytes_read;
  				  int current = 0;
  				  File to_download = new File("CLIENT_FOLDER/"+uploadedFilename);
  				  try {
  					FileOutputStream file_out = null;
  					file_out = new FileOutputStream(to_download);
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
        	
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }).start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}