import java.io.*;

public class FileReadingTest {

    public static void main(String[] args) {
        try {
            File f = new File("C:\\Users\\User\\Documents\\COMP4092_ProjectCode\\Testing\\Files");

            File[] files = f.listFiles();

            System.out.println("Files are:"); 
  
            // Display the names of the files 
            for (int i = 0; i < files.length; i++) { 
                System.out.println(files[i].getName()); 
            } 
        }
        catch (Exception e) {
            System.err.println(e.getMessage()); 
        }
    }
}