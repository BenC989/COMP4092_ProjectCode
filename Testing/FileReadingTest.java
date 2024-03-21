import java.io.*;
import java.util.ArrayList;

public class FileReadingTest {

    /*
     * This is the main function used to execute the program
     */
    public static void main(String[] args) {
        try {
            // Initialise variables
            FileWriter writer = new FileWriter("Data_Storage.txt");
            ArrayList<Record> tableRecords = new ArrayList<>();

            // Write table headings in data storage file
            writeHeadings(writer);

            // File participantsFolder = new File("C:\\Users\\User\\Documents\\COMP4092_ProjectCode\\Testing\\Files\\Participants");
            File participantsFolder = new File("C:\\Users\\benca\\Documents\\COMP4092_ProjectCode\\Testing\\Files\\Participants");
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

                        // Extract all timestamp data from image
                        int year = Integer.valueOf(fileName.substring(4, 8));
                        int month = Integer.valueOf(fileName.substring(8, 10));
                        int day = Integer.valueOf(fileName.substring(10, 12));
                        int hour = Integer.valueOf(fileName.substring(13, 15));
                        int minute = Integer.valueOf(fileName.substring(15, 17));
                        int second = Integer.valueOf(fileName.substring(17, 19));

                        // Get other image information
                        String imageID = fileName.substring(28, 31);
                        String participantID = participants[i].getName();
                        String activity = participantActivities[j].getName();

                        // Add this image information to the table records
                        Record record = new Record();
                        record.imageID = imageID;
                        record.participantID = participantID;
                        record.day = day;
                        record.month = month;
                        record.year = year;
                        record.hour = hour;
                        record.minute = minute;
                        record.second = second;
                        record.activity = activity;
                        tableRecords.add(record);

                        // Write all data in data storage file
                        writeImageID(writer, imageID);
                        writeParticipantID(writer, participantID);
                        writeDay(writer, day);
                        writeMonth(writer, month);
                        writeYear(writer, year);
                        writeHour(writer, hour);
                        writeMinute(writer, minute);
                        writeSecond(writer, second);
                        writeActivity(writer, activity);
                    }
                }
            }
            writer.close();

            for (Record i : tableRecords) {
                System.out.println(i.imageID + " " + i.participantID + " " + i.day + " " + i.month + " " + i.year + " " + i.hour + " " + i.minute + " " + i.second + " " + i.activity);
            }
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes each of the table headings in data storage file
     */
    public static void writeHeadings(Writer writer) {
        try {
            writer.write("|----------|----------------|----------|--------|----------------|");
            writer.write("\n");
            writer.write("| Image ID | Participant ID |   Date   |  Time  | Activity Class |");
            writer.write("\n");
            writer.write("|----------|----------------|----------|--------|----------------|");
            writer.write("\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the ImageID data in the data storage file
     */
    public static void writeImageID(Writer writer, String imageID) {
        try {
            writer.write("|" + imageID);
            writer.write("       |");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the ParticipantID data in the data storage file
     */
    public static void writeParticipantID(Writer writer, String participantID) {
        try {
            writer.write(participantID);
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Day date data in the data storage file
     */
    public static void writeDay(Writer writer, int day) {
        try {
            writer.write("  |");
            if (day < 10) {
                writer.write("0" + day);
            }
            else {
                writer.write("" + day);
            }
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Month date data in the data storage file
     */
    public static void writeMonth(Writer writer, int month) {
        try {
            writer.write("/");
            if (month < 10) {
                writer.write("0" + month);
            }
            else {
                writer.write("" + month);
            }
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Year date data in the data storage file
     */
    public static void writeYear(Writer writer, int year) {
        try {
            writer.write("/");
            writer.write("" + year);
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Hour time data in the data storage file
     */
    public static void writeHour(Writer writer, int hour) {
        try {
            if (hour < 10) {
                writer.write("0" + hour);
            }
            else {
                writer.write("" + hour);
            }
            writer.write(":");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Minute time data in the data storage file
     */
    public static void writeMinute(Writer writer, int minute) {
        try {
            if (minute < 10) {
                writer.write("0" + minute);
            }
            else {
                writer.write("" + minute);
            }
            writer.write(":");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Second time data in the data storage file
     */
    public static void writeSecond(Writer writer, int second) {
        try {
            if (second < 10) {
                writer.write("0" + second);
            }
            else {
                writer.write("" + second);
            }
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Activity data in the data storage file
     */
    public static void writeActivity(Writer writer, String activity) {
        try {
            writer.write(activity);
            for (int l = (16-(16-(activity.length()))); l < 16; l++) {
                writer.write(" ");
            }
            writer.write("|\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }
}