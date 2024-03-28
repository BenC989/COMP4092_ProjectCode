import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

public class FileReadingTest {
    static ArrayList<Record> imageTableRecords = new ArrayList<>();
    static ArrayList<Activity> activityTableRecords = new ArrayList<>();
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

        // Record the data from the images
        recordImageData();

        // Sort the data in chronological order for each participant
        sortImageTable(imageTableRecords);

        // Draw the Image Table in the data storage file
        fillImageTable(writer);

        // EXAMPLE: Make an example call to participant activities function
        participantActivities(writer, "01", imageTableRecords);

        // Draw the Activity Table in the data storage file
        fillActivityTable(writer);
    }

    /*
     * This function iterates through all records in the dataset and creates a list of image records
     */
    public static void recordImageData() {

        // Set the file path for getting image data
        File projectFolder = new File("Z:\\Patient work project\\Body Camera Data");
        File[] activityClasses = projectFolder.listFiles();

        // Iterate through all activity classes
        for (int i = 0; i < activityClasses.length; i++) { 
            File[] activityClass = activityClasses[i].listFiles();

            // Only consider folders that have the "Processed" folder
            if (activityClass != null && activityClass.length > 3) {

                // Only look at files from the "Processed" folder
                File processed = activityClass[3];

                // Search through the activity folder
                File[] processedSubFolders = processed.listFiles();
                File activityName = processedSubFolders[0];
                if (activityName.getName().equals(".DS_Store")) {
                    activityName = processedSubFolders[1];
                }

                // Iterate through all participants
                File[] participants = activityName.listFiles();
                for (int j = 0; j < participants.length; j++) { 

                    File[] images = participants[j].listFiles();

                    // Iterate through each participant's image files
                    if (images != null) {
                        for (int k = 0; k < images.length; k++) { 
                            String fileName = images[k].getName();

                            if (fileName.startsWith("PSS")) {

                                // Extract all timestamp data from image
                                String year = fileName.substring(4, 8);
                                String month = fileName.substring(8, 10);
                                String day = fileName.substring(10, 12);
                                String hour = fileName.substring(13, 15);
                                String minute = fileName.substring(15, 17);
                                String second = fileName.substring(17, 19);

                                // Get other image information
                                String imageID = fileName.substring(28, 31);
                                String participantID = participants[j].getName();
                                String activity = activityName.getName();

                                // Adjust timestamp information
                                int intImageID = Integer.valueOf(imageID);
                                int intDay = Integer.valueOf(day);
                                int intHour = Integer.valueOf(hour);
                                int intMinute = Integer.valueOf(minute);
                                int intSecond = Integer.valueOf(second);
                                int secondsToAdd = intImageID * 10;

                                for (int l = 0; l < (secondsToAdd - 5); l++) {
                                    if (intSecond == 60) {
                                        intSecond = 0;
                                        intMinute++;
                                    }
                                    if (intMinute == 60) {
                                        intMinute = 0;
                                        intHour++;
                                    }
                                    if (intHour == 24) {
                                        intHour = 0;
                                        intDay++;
                                    }
                                    intSecond++;
                                }

                                day = "";
                                hour = "";
                                minute = "";
                                second = "";

                                if (intDay < 10) {
                                    day = "0";
                                }
                                if (intHour < 10) {
                                    hour = "0";
                                }
                                if (intMinute < 10) {
                                    minute = "0";
                                }
                                if (intSecond < 10) {
                                    second = "0";
                                }
                                day += intDay;
                                hour += intHour;
                                minute += intMinute;
                                second += intSecond;

                                // Add this image information to the table records
                                String sortingVariable = participantID + year + month + day + hour + minute + second + imageID;
                                Record record = new Record(sortingVariable);
                                record.imageID = imageID;
                                record.participantID = participantID;
                                record.day = day;
                                record.month = month;
                                record.year = year;
                                record.hour = hour;
                                record.minute = minute;
                                record.second = second;
                                record.activity = activity;
                                imageTableRecords.add(record);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * This function sorts the Image Table records in chronological order for each participant
     */
    public static void sortImageTable(ArrayList<Record> list) {
        list.sort((record1, record2) -> record1.getSortingVariable().compareTo(record2.getSortingVariable()));
    }

    /*
     * This function considers one participant and calculates the time duration for each of their activities
     */
    public static ArrayList<Activity> participantActivities(Writer writer, String participant, ArrayList<Record> tableRecords) {

        // Store the participant's activities
        activityTableRecords = new ArrayList<>();

        // Define variables
        int index = 0;
        boolean newActivity = true;
        Activity activity = new Activity();

        // Find the records for the specific participant
        while ((tableRecords.get(index).participantID.equals(participant)) == false) {
            index++;
        }

        // Get all the records for this specific participant
        while (tableRecords.get(index).participantID.equals(participant)) {

            // When there a new activity is found
            if (newActivity == true) {
                activity.participant = participant;
                activity.name = tableRecords.get(index).activity;
                activity.startHour = tableRecords.get(index).hour;
                activity.startMinute = tableRecords.get(index).minute;
                activity.startSecond = tableRecords.get(index).second;
                newActivity = false;
            }
            else {

                // When it's the end of an activity
                if (((tableRecords.get(index).activity).equals(tableRecords.get(index - 1).activity)) == false) {
                    activity.endHour = tableRecords.get(index).hour;
                    activity.endMinute = tableRecords.get(index).minute;
                    activity.endSecond = tableRecords.get(index).second;
                    
                    activity.duration =
                        (3600 * (Integer.valueOf(activity.endHour) - Integer.valueOf(activity.startHour)))
                        +
                        (60 * (Integer.valueOf(activity.endMinute) - Integer.valueOf(activity.startMinute)))
                        +
                        (Integer.valueOf(activity.endSecond) - Integer.valueOf(activity.startSecond));

                    // Add this record to the participant's activity list
                    activityTableRecords.add(activity);

                    // Identify that a new activity has begun
                    activity = new Activity();
                    newActivity = true;
                    index--;
                }
            }
            index++;
        }
        return activityTableRecords;
    }

    /*
     * This function writes all Image Table data to the text file
     */
    public static void fillImageTable(Writer writer) {
        try {
            writeImageTableHeadings(writer);

            for (Record i : imageTableRecords) {
                // Write all data in data storage file
                writeImageTableImageID(writer, i.imageID);
                writeImageTableParticipantID(writer, i.participantID);
                writeImageTableDay(writer, i.day);
                writeImageTableMonth(writer, i.month);
                writeImageTableYear(writer, i.year);
                writeImageTableHour(writer, i.hour);
                writeImageTableMinute(writer, i.minute);
                writeImageTableSecond(writer, i.second);
                writeImageTableActivity(writer, i.activity);
                //System.out.println(i.imageID + " " + i.participantID + " " + i.day + " " + i.month + " " + i.year + " " + i.hour + " " + i.minute+ " " + i.second + " " + i.activity);
            }

            // Complete the table
            writer.write("|----------|----------------|----------|--------|-------------------|");
            writer.write("\n \n \n \n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes all Activity Table data to the text file
     */
    public static void fillActivityTable(Writer writer) {
        try {
            writeActivityTableHeadings(writer);

            for (Activity i : activityTableRecords) {
                writeActivityTableParticipantID(writer, i.participant);
                writeActivityTableActivity(writer, i.name);
                writeActivityTableDuration(writer, i.duration);
            }

            // Complete the table
            writer.write("|----------------|-------------------|------------|");
            writer.close();
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes each of the Image Table headings in data storage file
     */
    public static void writeImageTableHeadings(Writer writer) {
        try {
            writer.write("|----------|----------------|----------|--------|-------------------|");
            writer.write("\n");
            writer.write("| Image ID | Participant ID |   Date   |  Time  |  Activity Class   |");
            writer.write("\n");
            writer.write("|----------|----------------|----------|--------|-------------------|");
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
            writer.write(participantID + "            ");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Day date data in the Image Table in the data storage file
     */
    public static void writeImageTableDay(Writer writer, String day) {
        try {
            writer.write("  |");
            writer.write(day);
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Month date data in the Image Table in the data storage file
     */
    public static void writeImageTableMonth(Writer writer, String month) {
        try {
            writer.write("/");
            writer.write(month);
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Year date data in the Image Table in the data storage file
     */
    public static void writeImageTableYear(Writer writer, String year) {
        try {
            writer.write("/");
            writer.write(year);
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Hour time data in the Image Table in the data storage file
     */
    public static void writeImageTableHour(Writer writer, String hour) {
        try {
            writer.write(hour);
            writer.write(":");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Minute time data in the Image Table in the data storage file
     */
    public static void writeImageTableMinute(Writer writer, String minute) {
        try {
            writer.write(minute);
            writer.write(":");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Second time data in the Image Table in the data storage file
     */
    public static void writeImageTableSecond(Writer writer, String second) {
        try {
            writer.write(second);
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
            for (int l = (19-(19-(activity.length()))); l < 19; l++) {
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
            writer.write("|----------------|-------------------|------------|");
            writer.write("\n");
            writer.write("| Participant ID |  Activity Class   |  Duration  |");
            writer.write("\n");
            writer.write("|----------------|-------------------|------------|");
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
            writer.write("              |");
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
            for (int l = (19-(19-(activity.length()))); l < 19; l++) {
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
            if (duration < 100000) {
                answer = answer + 0;
            }
            if (duration < 10000) {
                answer = answer + 0;
            }
            if (duration < 1000) {
                answer = answer + 0;
            }
            if (duration < 100) {
                answer = answer + 0;
            }
            if (duration < 10) {
                answer = answer + 0;
            }
            answer = answer + duration;
            writer.write(answer);
            writer.write(" secs |\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }
}