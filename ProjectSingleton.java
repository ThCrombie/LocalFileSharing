package localFileSharing;

/**
 * 
 * This singleton will make sure only one instance of Monitor is instantiated in myMediaPlayer.java
 *
 */
public class ProjectSingleton {

    private static ProjectSingleton instance;
    
    private ProjectSingleton(){}
    
    public static synchronized ProjectSingleton getInstance(){
        if(instance == null){
            instance = new ProjectSingleton();
        }
        return instance;
    }
    
}