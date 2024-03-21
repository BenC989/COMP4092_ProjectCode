import java.io.*;

public class FileReadingTest {

    public static void main(String[] args) {
        try {
            File participantsFolder = new File("C:\\Users\\User\\Documents\\COMP4092_ProjectCode\\Testing\\Files\\Participants");
            File[] participants = participantsFolder.listFiles();
  
            // Iterate Participants folder (containing the participants)
            for (int i = 0; i < participants.length; i++) { 
                System.out.println(participants[i].getName()); 

                // Iterate Participants -> Participant folder (containing the activities)
                File[] participantActivities = participants[i].listFiles();
                for (int j = 0; j < participantActivities.length; j++) {
                    System.out.println("        " + participantActivities[j].getName());

                    // Iterate Participants -> Participant -> Activity (containing the images)
                    File[] participantActivityImages = participantActivities[j].listFiles();
                    for (int k = 0; k < participantActivityImages.length; k++) {
                        System.out.println("                " + participantActivityImages[k].getName());
                    }
                }
            } 
        }
        catch (Exception e) {
            System.err.println(e.getMessage()); 
        }
    }
}