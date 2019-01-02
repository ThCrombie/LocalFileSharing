package localFileSharing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.Observable;

public class myMediaPlayer extends Application {
	Monitor mainMonitor = new Monitor(); // only once instance of Monitor allowed due to singleton pattern
	// the mainMonitor instance scope allows access to this Main's methods
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainMonitor.init(); // this creates the client folder if it doesnt exist already
		
		Text serverInfo = new Text("Files in the server's folder:");
		ObservableList<String> observableFiles = FXCollections.observableArrayList(checkServerFolder());
		ListView listView = new ListView(observableFiles); // server file list
		
		Text clientInfo = new Text("Files in the your local folder:");
		ObservableList<String> clientObservableFiles = FXCollections.observableArrayList(checkClientFolder());
		ListView clientListView = new ListView(clientObservableFiles); // client file list
		
		listView.setPrefSize(200, 250);
        listView.setEditable(false);
        
        Button update = new Button("Check folder for updates");
        update.setOnAction((event) -> {
        	observableFiles.setAll(checkServerFolder());
        	clientObservableFiles.setAll(checkClientFolder());
            
        }); // when pressed, checks if the server's folder has any changes
        
        Button download = new Button("Download selected file");
        download.setOnAction((event) -> {
        	String downloadItem = (String) listView.getSelectionModel().getSelectedItem();
            try {
				mainMonitor.download(downloadItem);
				clientObservableFiles.setAll(checkClientFolder());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }); // (when using sockets, currently only uses in- and outputstream) will contact the server and download the selected file
        
        Button playSoundFile = new Button("Play downloaded sound file");
        playSoundFile.setOnAction((event)->{
        	String playSong = (String) listView.getSelectionModel().getSelectedItem();
        	try {
				mainMonitor.playSelectedFile(playSong);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }); // runs JavaFX's Scene Mediaplayer to play MP3's etc on the selected file.
        
        Button uploadToServer = new Button("Upload a file to the server");
        uploadToServer.setOnAction((event)->{
        	try {
				mainMonitor.uploadFile(primaryStage);
				observableFiles.setAll(checkServerFolder());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        
        
        // ADD COMMENTS LATER
		GridPane gridPane = new GridPane();
	    gridPane.setMinSize(400, 200);
	    gridPane.setPadding(new Insets(10, 10, 10, 10)); 
	    gridPane.setVgap(5); 
	    gridPane.setHgap(5);       
	    gridPane.setAlignment(Pos.CENTER);
	    
	    
	   // gridPane.add(child, columnIndex, rowIndex);
	    gridPane.add(serverInfo, 0, 0);
	    gridPane.add(listView, 0, 1);
	    gridPane.add(update, 0, 2);
	    gridPane.add(download, 0, 3);
	    
	    gridPane.add(clientInfo, 1,0);
	    gridPane.add(clientListView, 1, 1);
	    
	    
	    gridPane.add(playSoundFile, 1, 2);
	    gridPane.add(uploadToServer, 1, 3);
	    /*
	     * this layout has the server's filenames displayed on the left, and the client's on the right.
	     * the buttons are underneath and do what they say on the tin
	     */
		Scene scene = new Scene(gridPane); 
	    primaryStage.setTitle("Week 6 Distributed Systems"); 
	    primaryStage.setScene(scene);
	    primaryStage.setMinHeight(600);
	    primaryStage.setMinWidth(500);
		primaryStage.show();
	}
	
	/*
	 * updates the ObservableLists via the monitor class
	 */
	public ArrayList<String> checkServerFolder(){
		ArrayList<String> arrayOfNames = mainMonitor.getServerFilenames();
		return arrayOfNames; 
		}
	
	public ArrayList<String> checkClientFolder(){
		ArrayList<String> arrayOfNames = mainMonitor.getClientFilenames();
		return arrayOfNames;
		}
	
	
	
}