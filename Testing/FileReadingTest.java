// Import all required libraries
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

public class FileReadingTest {

    // Create the lists of records
    static ArrayList<ImageRecord> imageTableRecords = new ArrayList<>();
    static ArrayList<ActivityRecord> activityTableRecords = new ArrayList<>();

    // Create a file writer for the data output
    static FileWriter writer;

    /*
     * This is the main function used to execute the entire program
     */
    public static void main(String[] args) {

        // Initialise the file writer
        try {
            writer = new FileWriter("Data_Storage.txt");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }

        // Record the data from all the images
        recordImageData();

        // Sort the data in chronological order for each participant
        sortImageTable(imageTableRecords);

        // Write all Image Table records to text file
        fillImageTable(writer);

        /*
         * 1. Calculate duration for participant activities
         * 2. Record the data 
         * 3. Write all Activity Table records to text file
         * 4. Repeat for all participants
         */ 
        int numberOfParticipants = Integer.valueOf(imageTableRecords.get(imageTableRecords.size()-1).participantID);
        for (int i = 1; i <= numberOfParticipants; i++) {
            String ID = "";
            if (i < 10) {
                ID = "0";
            }
            ID += String.valueOf(i);
            participantActivities(writer, ID, imageTableRecords);
            fillActivityTable(writer);
        }

        // Close the file writer
        try {
            writer.close();
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function iterates through all Image records in the dataset and creates a consistent list
     */
    public static void recordImageData() {

        // Set the file path for getting image data
        File projectFolder = new File("Z:\\Patient work project\\Body Camera Data");
        File[] activityClasses = projectFolder.listFiles();

        // Iterate through all activity classes
        for (int i = 0; i < activityClasses.length; i++) { 

            // Do not look through the Representative Images folder
            if ((activityClasses[i].getName().equals("Representative Images")) == false) {
                File[] activityClass = activityClasses[i].listFiles();

                // Only consider folders that have the "Processed" sub-folder
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

                                // Ensure that the type of file is correct
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
                                    String[] adjustedTimestamp = adjustTimestamp(imageID, day, hour, minute, second);
                                    day = adjustedTimestamp[0];
                                    hour = adjustedTimestamp[1];
                                    minute = adjustedTimestamp[2];
                                    second = adjustedTimestamp[3];

                                    // Add this image information to the table records
                                    String sortingVariable = participantID + year + month + day + hour + minute + second + imageID;
                                    ImageRecord record = new ImageRecord(sortingVariable);
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
    }

    /*
     * This function sorts the Image Table records in chronological order for each participant
     */
    public static void sortImageTable(ArrayList<ImageRecord> list) {
        list.sort((record1, record2) -> record1.getSortingVariable().compareTo(record2.getSortingVariable()));
    }

    /*
     * This function considers one participant and calculates the time duration for each of their activities
     */
    public static ArrayList<ActivityRecord> participantActivities(Writer writer, String participant, ArrayList<ImageRecord> tableRecords) {

        // Store the participant's activities
        activityTableRecords = new ArrayList<>();

        // Define variables
        int index = 0;
        boolean newActivity = true;
        ActivityRecord activity = new ActivityRecord();
        boolean inBounds = ((index + 1) != tableRecords.size());
        boolean foundParticipant = (tableRecords.get(index).participantID.equals(participant));
        boolean nextActivitySame = (tableRecords.get(index).activity).equals(tableRecords.get(index + 1).activity);
        boolean nextParticipantSame = (tableRecords.get(index+1).participantID).equals(participant);

        // Search the list of Image records until the correct participant is found
        while ((inBounds == true) && (foundParticipant) == false) {
            index++;

            // Update loop guard
            inBounds = ((index + 1) != tableRecords.size());
            foundParticipant = (tableRecords.get(index).participantID.equals(participant));
            if (inBounds == false) {
                nextActivitySame = false;
                nextParticipantSame = false;
            }
            else {
                nextActivitySame = (tableRecords.get(index).activity).equals(tableRecords.get(index + 1).activity);
                nextParticipantSame = (tableRecords.get(index+1).participantID).equals(participant);
            }
        }

        // Get all the records for this specific participant
        while ((inBounds == true) && (foundParticipant) == true) {

            // When there is a new activity, record start date/time
            if (newActivity == true) {
                activity.participant = participant;
                activity.name = tableRecords.get(index).activity;
                activity.startDay = tableRecords.get(index).day;
                activity.startMonth = tableRecords.get(index).month;
                activity.startYear = tableRecords.get(index).year;
                activity.startHour = tableRecords.get(index).hour;
                activity.startMinute = tableRecords.get(index).minute;
                activity.startSecond = tableRecords.get(index).second;
                newActivity = false;
            }

            // When it's the end of an activity, record end date/time
            if ((inBounds == false) || (nextActivitySame == false) || (nextParticipantSame == false)) {

                if ((inBounds == false) || (nextParticipantSame == false)) {
                    activity.endDay = tableRecords.get(index).day;
                    activity.endMonth = tableRecords.get(index).month;
                    activity.endYear = tableRecords.get(index).year;
                    activity.endHour = tableRecords.get(index).hour;
                    activity.endMinute = tableRecords.get(index).minute;
                    activity.endSecond = tableRecords.get(index).second;
                }
                else {
                    activity.endDay = tableRecords.get(index+1).day;
                    activity.endMonth = tableRecords.get(index+1).month;
                    activity.endYear = tableRecords.get(index+1).year;
                    activity.endHour = tableRecords.get(index+1).hour;
                    activity.endMinute = tableRecords.get(index+1).minute;
                    activity.endSecond = tableRecords.get(index+1).second;
                }

                // Adjust the duration for an activity between two days
                if (Integer.valueOf(activity.endDay) > Integer.valueOf(activity.startDay)) {
                    int temp = Integer.valueOf(activity.endHour) + 24;
                    activity.endHour = String.valueOf(temp);
                }
                
                // Calculate activity duration in seconds only
                int durationInSeconds =
                    (3600 * (Integer.valueOf(activity.endHour) - Integer.valueOf(activity.startHour)))
                    +
                    (60 * (Integer.valueOf(activity.endMinute) - Integer.valueOf(activity.startMinute)))
                    +
                    (Integer.valueOf(activity.endSecond) - Integer.valueOf(activity.startSecond));

                // Calculate activity duration in hours, minutes, and seconds
                int hours = durationInSeconds / 3600;
                durationInSeconds = durationInSeconds % 3600;
                int minutes = durationInSeconds / 60;
                durationInSeconds = durationInSeconds % 60;
                int seconds = durationInSeconds;
                activity.durationHours = String.valueOf(hours);
                activity.durationMinutes = String.valueOf(minutes);
                activity.durationSeconds = String.valueOf(seconds);

                // Add this record to the participant's activity list
                activityTableRecords.add(activity);

                // Identify that a new activity has begun
                if (inBounds == true) {
                    activity = new ActivityRecord();
                    newActivity = true;
                }
            }
            index++;

            // Update loop guard
            inBounds = ((index + 1) != tableRecords.size());
            foundParticipant = (tableRecords.get(index).participantID.equals(participant));
            if (inBounds == false) {
                nextActivitySame = false;
                nextParticipantSame = false;
            }
            else {
                nextActivitySame = (tableRecords.get(index).activity).equals(tableRecords.get(index + 1).activity);
                nextParticipantSame = (tableRecords.get(index+1).participantID).equals(participant);
            }
        }
        return activityTableRecords;
    }

    /*
     * This function writes all Image Table data to the text file
     */
    public static void fillImageTable(Writer writer) {
        try {
            // Write the Image Table headings
            writer.write("|----------|----------------|----------|--------|-------------------|");
            writer.write("\n");
            writer.write("| Image ID | Participant ID |   Date   |  Time  |  Activity Class   |");
            writer.write("\n");
            writer.write("|----------|----------------|----------|--------|-------------------|");
            writer.write("\n");

            // Write the Image Table records
            for (ImageRecord i : imageTableRecords) {
                writeImageTableImageID(writer, i.imageID);
                writeImageTableParticipantID(writer, i.participantID);
                writeImageTableDay(writer, i.day);
                writeImageTableMonth(writer, i.month);
                writeImageTableYear(writer, i.year);
                writeImageTableHour(writer, i.hour);
                writeImageTableMinute(writer, i.minute);
                writeImageTableSecond(writer, i.second);
                writeImageTableActivity(writer, i.activity);
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
            // Write the Activity Table headings
            writer.write("|----------------|-------------------|---------------------|---------------------|------------|");
            writer.write("\n");
            writer.write("| Participant ID |  Activity Class   |  Start Date / Time  |   End Date / Time   |  Duration  |");
            writer.write("\n");
            writer.write("|----------------|-------------------|---------------------|---------------------|------------|");
            writer.write("\n");

            // Write the Activity Table records
            for (ActivityRecord i : activityTableRecords) {
                writeActivityTableParticipantID(writer, i.participant);
                writeActivityTableActivity(writer, i.name);
                writeActivityTableStartDT(writer, i);
                writeActivityTableEndDT(writer, i);
                writeActivityTableDuration(writer, i);
            }

            // Complete the table
            writer.write("|----------------|-------------------|---------------------|---------------------|------------|");
            writer.write("\n \n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function adjusts the timestamp to ensure data correctness
     */
    public static String[] adjustTimestamp(String imageID, String day, String hour, String minute, String second) {
        
        // Define variables
        int intImageID = Integer.valueOf(imageID);
        int intDay = Integer.valueOf(day);
        int intHour = Integer.valueOf(hour);
        int intMinute = Integer.valueOf(minute);
        int intSecond = Integer.valueOf(second);

        // Adjust the timestamp information based on the Image ID
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

        // Transform the new timestamp data into Strings
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
        String[] answer = new String[4];
        day += intDay;
        hour += intHour;
        minute += intMinute;
        second += intSecond;

        // Store the correct timestamp information in an array
        answer[0] = day;
        answer[1] = hour;
        answer[2] = minute;
        answer[3] = second;
        return answer;
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
     * This function writes the Start date and time for an activity in the Activity Table in the data storage file
     */
    public static void writeActivityTableStartDT(Writer writer, ActivityRecord activity) {
        try {
            writer.write(activity.startDay);
            writer.write("/");
            writer.write(activity.startMonth);
            writer.write("/");
            writer.write(activity.startYear);
            writer.write(" - ");
            writer.write(activity.startHour);
            writer.write(":");
            writer.write(activity.startMinute);
            writer.write(":");
            writer.write(activity.startSecond);
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the End date and time for an activity in the Activity Table in the data storage file
     */
    public static void writeActivityTableEndDT(Writer writer, ActivityRecord activity) {
        try {
            writer.write(activity.endDay);
            writer.write("/");
            writer.write(activity.endMonth);
            writer.write("/");
            writer.write(activity.endYear);
            writer.write(" - ");
            writer.write(activity.endHour);
            writer.write(":");
            writer.write(activity.endMinute);
            writer.write(":");
            writer.write(activity.endSecond);
            writer.write("|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the Duration data in the Activity Table in the data storage file
     */
    public static void writeActivityTableDuration(Writer writer, ActivityRecord activity) {
        try {
            String answer = "";
            if (Integer.valueOf(activity.durationHours) < 10) {
                answer += 0;
            }
            answer += activity.durationHours;
            answer += ":";
            if (Integer.valueOf(activity.durationMinutes) < 10) {
                answer += 0;
            }
            answer += activity.durationMinutes;
            answer += ":";
            if (Integer.valueOf(activity.durationSeconds) < 10) {
                answer += 0;
            }
            answer += activity.durationSeconds;

            writer.write(answer);
            writer.write("    |\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }
}