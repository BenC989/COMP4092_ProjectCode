import java.io.*;
import java.util.ArrayList;

public class FileReadingTest {
    static ArrayList<Record> tableRecords = new ArrayList<>();
    static FileWriter writer;

    /*
     * This is the main function used to execute the program
     */
    public static void main(String[] args) {

        // Initialise the file writer
        try {
            writer = new FileWriter("Data_Storage.txt");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }

        // Tabularise the data from the images
        tabulariseData();

        // Make an example call to participant activities function
        participantActivities(writer, "Participant_01", tableRecords);
    }

    public static void tabulariseData() {
        try {
            // Write image data table headings in data storage file
            writeImageTableHeadings(writer);

            // File participantsFolder = new File("C:\\Users\\User\\Documents\\COMP4092_ProjectCode\\Testing\\Files\\Participants");
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
                        writeImageTableImageID(writer, imageID);
                        writeImageTableParticipantID(writer, participantID);
                        writeImageTableDay(writer, day);
                        writeImageTableMonth(writer, month);
                        writeImageTableYear(writer, year);
                        writeImageTableHour(writer, hour);
                        writeImageTableMinute(writer, minute);
                        writeImageTableSecond(writer, second);
                        writeImageTableActivity(writer, activity);
                    }
                }
            }

            // Complete the table
            writer.write("|----------|----------------|----------|--------|----------------|");
            writer.write("\n \n \n \n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function considers one participant and calculates the time duration for each of their activities
     */
    public static ArrayList<Activity> participantActivities(Writer writer, String participant, ArrayList<Record> tableRecords) {

        // Store the participant's activities
        ArrayList<Activity> participantActivities = new ArrayList<>();

        try {
            // Define variables
            int index = 0;
            boolean newActivity = true;
            Activity activity = new Activity();

            // Write participant activity data table headings in data storage file
            writeActivityTableHeadings(writer);

            // Find the records for the specific participant
            while ((tableRecords.get(index).participantID.equals(participant)) == false) {
                index++;
            }

            // Get all the records for this specific participant
            while (tableRecords.get(index).participantID.equals(participant)) {

                // When there a new activity is found
                if (newActivity == true) {
                    activity.name = tableRecords.get(index).activity;
                    activity.startHour = tableRecords.get(index).hour;
                    activity.startMinute = tableRecords.get(index).minute;
                    activity.startSecond = tableRecords.get(index).second;
                    newActivity = false;
                }
                else {

                    // When it's the end of an activity
                    if (((tableRecords.get(index).activity).equals(tableRecords.get(index - 1).activity)) == false) {
                        activity.endHour = tableRecords.get(index - 1).hour;
                        activity.endMinute = tableRecords.get(index - 1).minute;
                        activity.endSecond = tableRecords.get(index - 1).second;

                        // Calculate the activity time duration in minutes
                        activity.duration = 
                            (60*(activity.endHour - activity.startHour)) + (activity.endMinute - activity.startMinute);

                        // Add this record to the participant's activity list
                        participantActivities.add(activity);

                        // Write all data in data storage file
                        writeActivityTableParticipantID(writer, participant);
                        writeActivityTableActivity(writer, activity.name);
                        writeActivityTableDuration(writer, activity.duration);

                        // Identify that a new activity has begun
                        activity = new Activity();
                        newActivity = true;
                        index--;
                    }
                }
                index++;
            }

            // Complete the table
            writer.write("|----------------|----------------|----------|");
            writer.close();
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
        return participantActivities;
    }

    /*
     * This function writes each of the Image Table headings in data storage file
     */
    public static void writeImageTableHeadings(Writer writer) {
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
     * This function writes the ImageID data in the Image Table in the data storage file
     */
    public static void writeImageTableImageID(Writer writer, String imageID) {
        try {
            writer.write("|" + imageID);
            writer.write("       |");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the ParticipantID data in the Image Table in the data storage file
     */
    public static void writeImageTableParticipantID(Writer writer, String participantID) {
        try {
            writer.write(participantID);
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Day date data in the Image Table in the data storage file
     */
    public static void writeImageTableDay(Writer writer, int day) {
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
     * This function writes the Month date data in the Image Table in the data storage file
     */
    public static void writeImageTableMonth(Writer writer, int month) {
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
     * This function writes the Year date data in the Image Table in the data storage file
     */
    public static void writeImageTableYear(Writer writer, int year) {
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
     * This function writes the Hour time data in the Image Table in the data storage file
     */
    public static void writeImageTableHour(Writer writer, int hour) {
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
     * This function writes the Minute time data in the Image Table in the data storage file
     */
    public static void writeImageTableMinute(Writer writer, int minute) {
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
     * This function writes the Second time data in the Image Table in the data storage file
     */
    public static void writeImageTableSecond(Writer writer, int second) {
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
     * This function writes the Activity data in the Image Table in the data storage file
     */
    public static void writeImageTableActivity(Writer writer, String activity) {
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

    /*
     * This function writes each of the table headings in the Activity Table in the data storage file
     */
    public static void writeActivityTableHeadings(Writer writer) {
        try {
            writer.write("|----------------|----------------|----------|");
            writer.write("\n");
            writer.write("| Participant ID | Activity Class | Duration |");
            writer.write("\n");
            writer.write("|----------------|----------------|----------|");
            writer.write("\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the ParticipantID data in the Activity Table in the data storage file
     */
    public static void writeActivityTableParticipantID(Writer writer, String participantID) {
        try {
            writer.write("|" + participantID);
            writer.write("  |");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Activity data in the Activity Table in the data storage file
     */
    public static void writeActivityTableActivity(Writer writer, String activity) {
        try {
            writer.write(activity);
            for (int l = (16-(16-(activity.length()))); l < 16; l++) {
                writer.write(" ");
            }
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Duration data in the Activity Table in the data storage file
     */
    public static void writeActivityTableDuration(Writer writer, int duration) {
        try {
            String answer = "";
            if (duration < 100) {
                answer = answer + 0;
            }
            if (duration < 10) {
                answer = answer + 0;
            }
            answer = answer + duration;
            writer.write(answer);
            writer.write(" mins  |\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }
}