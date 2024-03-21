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
                        String fileName = participantActivityImages[k].getName();
                        System.out.print("                " + fileName + " - ");

                        System.out.print("Year: " + fileName.substring(4, 8) + " ");
                        System.out.print("Month: " + fileName.substring(8, 10) + " ");
                        System.out.print("Day: " + fileName.substring(10, 12) + " ");
                        System.out.print("Hour: " + fileName.substring(13, 15) + " ");
                        System.out.print("Minute: " + fileName.substring(15, 17) + " ");
                        System.out.print("Second: " + fileName.substring(17, 19) + " ");
                        System.out.println();
                    }
                }
            } 
        }
        catch (Exception e) {
            System.err.println(e.getMessage()); 
        }
    }
}