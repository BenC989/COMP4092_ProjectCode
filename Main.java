// Import all required libraries
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.FileWriter;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {

    // Create the lists of records
    static ArrayList<ImageRecord> imageTableRecords = new ArrayList<>();
    static ArrayList<ActivityRecord> activityTableRecords = new ArrayList<>();
    static ArrayList<String> preselectedRepresenativeImages = new ArrayList<>();

    // Create a file writer for the data output
    static FileWriter writer;

    // Define Y-position to draw images
    static int currentYPosition;

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

        // Remove duplicate image records
        removeDuplicates(imageTableRecords);

        // Write all Image Table records to text file
        fillImageTable(writer);

        /*
         * 1. Calculate duration for participant activities
         * 2. Record the data 
         * 3. Write all Activity Table records to text file
         * 4. Write the combined total activity duration for all types of activity classes
         * 5. Repeat for all participants
         */ 
        int numberOfParticipants = Integer.valueOf(imageTableRecords.get(imageTableRecords.size()-1).participantID);
        for (int i = 1; i <= numberOfParticipants; i++) {
            String ID = "";
            if (i < 10) {
                ID = "0";
            }
            ID += String.valueOf(i);

            // Define participant activities
            currentYPosition = 60;
            participantActivities(writer, ID, imageTableRecords);

            // Write all Activity Table records to text file
            fillActivityTable(writer);

            // Write the combined total activity duration for all types of activity classes
            participantActivitiesTotalDuration(writer, ID, activityTableRecords);
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

            // Ignore the Representative Images folder
            if ((activityClasses[i].getName().equals("Representative Images")) == false) {
                File[] activityClass = activityClasses[i].listFiles();

                // Only consider folders that have the "Processed" sub-folder
                if (activityClass != null && activityClass.length >= 3) {

                    // Only look at files from the "Processed" folder
                    File processed = null;
                    if (activityClass.length > 3) {
                        processed = activityClass[3];
                    }
                    else {
                        processed = activityClass[2];
                    }

                    // Search through the activity folder
                    File[] processedSubFolders = processed.listFiles();
                    for (int index = 0; index < processedSubFolders.length; index++) {
                    
                        File activityName = processedSubFolders[index];
                        if ((activityName.getName().equals(".DS_Store")) == false) {
                            
                            // Iterate through all participants
                            File[] participants = activityName.listFiles();
                            for (int j = 0; j < participants.length; j++) { 

                                File[] images = participants[j].listFiles();

                                // Iterate through each participant's image files
                                if (images != null) {
                                    for (int k = 0; k < images.length; k++) { 
                                        String fileName = images[k].getName();

                                        // Ensure that the type of file is correct
                                        if (fileName.startsWith("PSS")
                                            && (fileName.contains("Copy") == false)) {

                                            // Extract all timestamp data from image
                                            String year = fileName.substring(4, 8);
                                            String month = fileName.substring(8, 10);
                                            String day = fileName.substring(10, 12);
                                            String hour = fileName.substring(13, 15);
                                            String minute = fileName.substring(15, 17);
                                            String second = fileName.substring(17, 19);

                                            // Get other image data
                                            File file = images[k];
                                            String imageID = fileName.substring(28, 31);
                                            String participantID = participants[j].getName();
                                            String activity = activityName.getName();

                                            // Adjust timestamp data
                                            String[] adjustedTimestamp = adjustTimestamp(imageID, day, hour, minute, second);
                                            day = adjustedTimestamp[0];
                                            hour = adjustedTimestamp[1];
                                            minute = adjustedTimestamp[2];
                                            second = adjustedTimestamp[3];

                                            // Add this image data to the Image table records
                                            String sortingVariable = participantID + year + month + day + hour + minute + second + imageID;
                                            ImageRecord record = new ImageRecord(sortingVariable);
                                            record.fileName = fileName;
                                            record.file = file;
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

            // Look through the Representative Images folder
            else {
                File[] repImageActivityClasses = activityClasses[i].listFiles();
                for (int j = 0; j < repImageActivityClasses.length; j++) {

                    File[] repImageParticipants = repImageActivityClasses[j].listFiles();
                    for (int k = 0; k < repImageParticipants.length; k++) {

                        File[] repImageParticipantFiles = repImageParticipants[k].listFiles();
                        if (repImageParticipantFiles != null) {

                            for (int l = 0; l < repImageParticipantFiles.length; l++) {

                                String fileName = repImageParticipantFiles[l].getName();
                                if (fileName.startsWith("PSS")) {

                                    // Add every "preselected image" to a set of records
                                    preselectedRepresenativeImages.add(fileName);
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
     * This function removes all duplicate image records found in the Image table
     */
    public static void removeDuplicates(ArrayList<ImageRecord> tableRecords) {
        for (int i = 0; i < tableRecords.size() - 1; i++) {
            if ((tableRecords.get(i+1).fileName).equals(tableRecords.get(i).fileName)) {
                tableRecords.remove(i+1);
            }
        }
        imageTableRecords = tableRecords;
    }

    /*
     * This function considers one participant only. For this participant, the function defines their
     * activities. It calculates the time duration and defines a representative image for each 
     * of their activities 
     */
    public static ArrayList<ActivityRecord> participantActivities(Writer writer, String participant, ArrayList<ImageRecord> tableRecords) {

        // Store the participant's activities
        activityTableRecords = new ArrayList<>();

        // Define required variables
        int index = 0;
        boolean newActivity = true;
        ActivityRecord activity = new ActivityRecord();
        boolean inBounds = ((index + 1) != tableRecords.size());
        boolean foundParticipant = (tableRecords.get(index).participantID.equals(participant));
        boolean nextActivitySame = (tableRecords.get(index).activity).equals(tableRecords.get(index + 1).activity);
        boolean nextParticipantSame = (tableRecords.get(index+1).participantID).equals(participant);
        int locationIndex = -1;
        int representativeImageIndex = 1;
        File representativeImage = null;
        boolean foundBetterImage = false;

        // Search the list of Image records until the correct participant is found
        while ((inBounds == true) && (foundParticipant) == false) {
            index++;

            // Update loop guard
            inBounds = ((index + 1) != tableRecords.size());
            foundParticipant = (tableRecords.get(index).participantID.equals(participant));

            // Case where the end of the image records is reached
            if (inBounds == false) {
                nextActivitySame = false;
                nextParticipantSame = false;
            }

            // Check whether the next activity is the same and the next participant is the same
            else {
                nextActivitySame = (tableRecords.get(index).activity).equals(tableRecords.get(index + 1).activity);
                nextParticipantSame = (tableRecords.get(index+1).participantID).equals(participant);
            }
        }

        // Get all the records for this specific participant
        while ((inBounds == true) && (foundParticipant) == true) {

            // When there is a new activity, record start date/time
            if (newActivity == true) {
                locationIndex++;
                representativeImageIndex = 1;
                activity.participant = participant;
                activity.name = tableRecords.get(index).activity;
                activity.startDay = tableRecords.get(index).day;
                activity.startMonth = tableRecords.get(index).month;
                activity.startYear = tableRecords.get(index).year;
                activity.startHour = tableRecords.get(index).hour;
                activity.startMinute = tableRecords.get(index).minute;
                activity.startSecond = tableRecords.get(index).second;
                foundBetterImage = false;
                newActivity = false;
            }

            /*
             * If an activity has an associated image in the Representative Images folder, use that
             * image instead of the middle image
             */ 

            if (foundBetterImage == false) {
                for (int i = 0; i < preselectedRepresenativeImages.size(); i++) {
                    if (tableRecords.get(index).fileName.equals(preselectedRepresenativeImages.get(i))) {
                        foundBetterImage = true;
                        activity.representative = tableRecords.get(index).fileName;
                        representativeImage = tableRecords.get(index).file;
                    }
                }
            }

            // When it's the end of an activity, record end date/time
            if ((inBounds == false) || (nextActivitySame == false) || (nextParticipantSame == false)) {

                /*
                 * If there is no preselected representative image for this specific activity, then
                 * use the image from the middle of the activity as the representative
                 */
                if (foundBetterImage == false) {
                    activity.representative = tableRecords.get(index - (representativeImageIndex / 2)).fileName;
                    representativeImage = tableRecords.get(index - (representativeImageIndex / 2)).file;
                }

                // Record the end date and time
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

                // Re-correct the activity end hour
                if (Integer.valueOf(activity.endDay) > Integer.valueOf(activity.startDay)) {
                    int temp = Integer.valueOf(activity.endHour) - 24;
                    String result = "";
                    if (temp < 10) {
                        result = "0";
                    }
                    activity.endHour = result + temp;
                }

                // Add this record to the participant's activity list
                activity.location = getLocationData(new File("C:\\Users\\benca\\Documents\\COMP4092_ProjectCode\\LocationData\\" + participant + ".txt"), locationIndex);
                activityTableRecords.add(activity);

                // Add this record to the participant's data visualisation file
                try {
                    BufferedImage participantFile = ImageIO.read(new File("C:\\Users\\benca\\Documents\\COMP4092_ProjectCode\\DataVisualisation\\" + participant + ".png"));
                    BufferedImage repImage = ImageIO.read(representativeImage);
                    String startTime = activity.startDay + "/" + activity.startMonth + "/" + activity.startYear + ", " + activity.startHour + ":" + activity.startMinute + ":" + activity.startSecond;
                    String endTime = activity.endDay + "/" + activity.endMonth + "/" + activity.endYear + ", " + activity.endHour + ":" + activity.endMinute + ":" + activity.endSecond;
                    addVisualisationEntry(participantFile, repImage, startTime, endTime, activity.location, activity.name, participant);
                }
                catch (Exception e) {}

                // Identify that a new activity has begun
                if (inBounds == true) {
                    activity = new ActivityRecord();
                    newActivity = true;
                }
            }
            else {
                representativeImageIndex++;
            }
            index++;

            // Update loop guard
            inBounds = ((index + 1) != tableRecords.size());
            foundParticipant = (tableRecords.get(index).participantID.equals(participant));

            // Case where the end of the image records is reached
            if (inBounds == false) {
                nextActivitySame = false;
                nextParticipantSame = false;
            }

            // Check whether the next activity is the same and the next participant is the same
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
            writer.write("|----------------|-------------------|---------------------|");
            writer.write("---------------------|------------|-----------------------------------|");
            writer.write("-------------------|");
            writer.write("\n");
            writer.write("| Participant ID |  Activity Class   |  Start Date / Time  |   End Date / Time   |  Duration  |   Representative Image File Name  |      Location     |");
            writer.write("\n");
            writer.write("|----------------|-------------------|---------------------|");
            writer.write("---------------------|------------|-----------------------------------|");
            writer.write("-------------------|");
            writer.write("\n");

            // Write the Activity Table records
            for (ActivityRecord i : activityTableRecords) {
                writeActivityTableParticipantID(writer, i.participant);
                writeActivityTableActivity(writer, i.name);
                writeActivityTableStartDT(writer, i);
                writeActivityTableEndDT(writer, i);
                writeActivityTableDuration(writer, i);
                writeActivityTableRepresentative(writer, i);
                writeActivityTableLocation(writer, i);
            }

            // Complete the table
            writer.write("|----------------|-------------------|---------------------|");
            writer.write("---------------------|------------|-----------------------------------|");
            writer.write("-------------------|");
            writer.write("\n");
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
            intSecond++;

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
     * This function writes the combined total activity duration for every type of activity class
     */
    public static void participantActivitiesTotalDuration(Writer writer, String participant, ArrayList<ActivityRecord> tableRecords) {
        participantActivityTotalDuration(writer, participant, tableRecords, "Socializing");
        participantActivityTotalDuration(writer, participant, tableRecords, "Electronic Devices");
        participantActivityTotalDuration(writer, participant, tableRecords, "Food Related");
        participantActivityTotalDuration(writer, participant, tableRecords, "Managing Health");
        participantActivityTotalDuration(writer, participant, tableRecords, "Indoor");
        participantActivityTotalDuration(writer, participant, tableRecords, "Outdoor");
        participantActivityTotalDuration(writer, participant, tableRecords, "Deliberate Exercise");
        participantActivityTotalDuration(writer, participant, tableRecords, "Driving");
        participantActivityTotalDuration(writer, participant, tableRecords, "Shopping");
        participantActivityTotalDuration(writer, participant, tableRecords, "Sleeping");
        participantActivityTotalDuration(writer, participant, tableRecords, "Watching Tv");
    }

    /*
     * This function writes the combined total activity duration for one specific activity class
     */
    public static void participantActivityTotalDuration(Writer writer, String participant, ArrayList<ActivityRecord> tableRecords, String activityName) {
        int durationInSeconds = 0;

        // Iterate through each activity record and sum the duration for the specific activity class
        for (ActivityRecord i : activityTableRecords) {
            if (i.name.equals(activityName)) {
                durationInSeconds += (Integer.parseInt(i.durationHours) * 3600);
                durationInSeconds += (Integer.parseInt(i.durationMinutes) * 60);
                durationInSeconds += (Integer.parseInt(i.durationSeconds));
            }
        }

        // Convert seconds to hours, minutes, and seconds
        int hours = durationInSeconds / 3600;
        durationInSeconds = durationInSeconds % 3600;
        int minutes = durationInSeconds / 60;
        durationInSeconds = durationInSeconds % 60;
        int seconds = durationInSeconds;
        
        // Format for presentation
        String hoursString = "";
        String minutesString = "";
        String secondsString = "";
        if (hours < 10) {
            hoursString = "0";
        }
        if (minutes < 10) {
            minutesString = "0";
        }
        if (seconds < 10) {
            secondsString = "0";
        }
        hoursString += hours;
        minutesString += minutes;
        secondsString += seconds;

        // Write the combined duration for the activity class
        try {
            writer.write ("|" + participant + " TOTALS:      |" + activityName);
            for (int i = (19 - (19 - (activityName.length()))); i < 19; i++) {
                writer.write(" ");
            }
            writer.write ("|" + hoursString + ":" + minutesString + ":" + secondsString + "|\n");
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
            writer.write("    |");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the representative image in the Activity Table in the data storage file
     */
    public static void writeActivityTableRepresentative(Writer writer, ActivityRecord activity) {
        try {
            writer.write(activity.representative + "|");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function writes the location in the Activity Table in the data storage file
     */
    public static void writeActivityTableLocation(Writer writer, ActivityRecord activity) {
        try {
            if (activity.location == null) {
                activity.location = "";
            }
            writer.write(activity.location);
            for (int l = (19-(19-(activity.location.length()))); l < 19; l++) {
                writer.write(" ");
            }
            writer.write("|\n");
        }
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function visualises participant activity data, printing text and a representative
     * image onto a png file to show the time and location of a participant's activities
     */
    public static void addVisualisationEntry(BufferedImage participantFile, BufferedImage repImage, String startTime, String endTime, String location, String activityName, String participant) {
        try {
            Graphics2D graphics = participantFile.createGraphics();

            // Define font and draw heading participant text
            Font headingFont = new Font("Arial", Font.BOLD, 20);
            Font timeFont = new Font("Arial", Font.BOLD, 10); 
            Font locationFont = new Font("Arial", Font.BOLD, 9);
            graphics.setFont(headingFont);
            graphics.setColor(Color.BLACK); 
            graphics.drawString("Participant " + participant, 140, 30);
            
            // Calculate and define size and position for representative image
            double scale;
            if (participant.equals("15")
             || participant.equals("16")
             || participant.equals("17")
             || participant.equals("19")) { 
                scale = 0.15; 
            }
            else {
                scale = 0.3; 
            }
            int repImageWidth = (int) (repImage.getWidth() * scale);
            int repImageHeight = (int) (repImage.getHeight() * scale);
            int x = 110; 
            int y = currentYPosition; 

            // Draw the representative image
            graphics.drawImage(repImage, x, y, repImageWidth, repImageHeight, null);
            currentYPosition += 120;

            // Draw the date and time text
            graphics.setFont(timeFont);
            graphics.drawString("TIME:    " + startTime.substring(12,20), x - 106, y - 5);
            graphics.drawString("TIME:    " + endTime.substring(12,20), x - 106, y + 115);
            graphics.drawString("DATE:   " + startTime.substring(0,10), x - 105, y + 5);
            graphics.drawString("DATE:   " + endTime.substring(0,10), x - 105, y + 125);

            // Draw the location and activity text
            graphics.setFont(locationFont);
            graphics.drawString("LOCATION", x + 199, y + 35);
            graphics.drawString(location, x + 200, y + 45);
            graphics.drawString("ACTIVITY", x + 200, y + 75);
            graphics.drawString(activityName, x + 199, y + 85);
            graphics.dispose();

            // Save the changes to the file
            File outputfile = new File("C:\\Users\\benca\\Documents\\COMP4092_ProjectCode\\DataVisualisation\\" + participant + ".png");
            ImageIO.write(participantFile, "png", outputfile);
            // System.out.println("participant: " + participant);
        } 
        catch (Exception e) {
            System.err.println("Error! " + e.getMessage()); 
        }
    }

    /*
     * This function retrieves location data from a certain participant's text file
     */
    public static String getLocationData(File file, int index) {
        String locationData = "";
        BufferedReader reader;

        // Read the file line-by-line until the intended data is found
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            for (int i = 0; i < index; i++) {
                line = reader.readLine();
            }
            locationData = line;
        }
        catch (Exception e) {}

        return locationData;
    }
}