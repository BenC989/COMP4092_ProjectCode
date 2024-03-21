import java.io.*;

public class FileReadingTest {

    public static void main(String[] args) {
        try {
            // Initialise file writer
            FileWriter writer = new FileWriter("Data_Storage.txt");

            writer.write("|----------|----------------|----------|--------|----------------|");
            writer.write("\n");
            writer.write("| Image ID | Participant ID |   Date   |  Time  | Activity Class |");
            writer.write("\n");
            writer.write("|----------|----------------|----------|--------|----------------|");
            writer.write("\n");

            File participantsFolder = new File("C:\\Users\\User\\Documents\\COMP4092_ProjectCode\\Testing\\Files\\Participants");
            File[] participants = participantsFolder.listFiles();
  
            // Iterate Participants folder (containing the participants)
            for (int i = 0; i < participants.length; i++) { 

                // Iterate Participants -> Participant folder (containing the activities)
                File[] participantActivities = participants[i].listFiles();
                for (int j = 0; j < participantActivities.length; j++) {

                    // Iterate Participants -> Participant -> Activity (containing the images)
                    File[] participantActivityImages = participantActivities[j].listFiles();
                    for (int k = 0; k < participantActivityImages.length; k++) {
                        String fileName = participantActivityImages[k].getName();

                        int year = Integer.valueOf(fileName.substring(4, 8));
                        int month = Integer.valueOf(fileName.substring(8, 10));
                        int day = Integer.valueOf(fileName.substring(10, 12));
                        int hour = Integer.valueOf(fileName.substring(13, 15));
                        int minute = Integer.valueOf(fileName.substring(15, 17));
                        int second = Integer.valueOf(fileName.substring(17, 19));

                        writer.write("|" + fileName.substring(28, 31));
                        writer.write("       |");
                        writer.write(participants[i].getName());
                        writer.write("  |");
                        if (day < 10) {
                            writer.write("0" + day);
                        }
                        else {
                            writer.write("" + day);
                        }
                        writer.write("/");
                        if (month < 10) {
                            writer.write("0" + month);
                        }
                        else {
                            writer.write("" + month);
                        }
                        writer.write("/");
                        writer.write("" + year);
                        writer.write("|");
                        if (hour < 10) {
                            writer.write("0" + hour);
                        }
                        else {
                            writer.write("" + hour);
                        }
                        writer.write(":");
                        if (minute < 10) {
                            writer.write("0" + minute);
                        }
                        else {
                            writer.write("" + minute);
                        }
                        writer.write(":");
                        if (second < 10) {
                            writer.write("0" + second);
                        }
                        else {
                            writer.write("" + second);
                        }
                        writer.write("|");
                        writer.write(participantActivities[j].getName());
                        for (int l = (16-(16-(participantActivities[j].getName().length()))); l < 16; l++) {
                            writer.write(" ");
                        }
                        writer.write("|\n");
                    }
                }
            }
            writer.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage()); 
        }
    }
}