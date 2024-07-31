import java.io.File;
public class ImageRecord {
    String fileName;
    File file;
    String imageID;
    String participantID;
    String year;
    String month;
    String day;
    String hour;
    String minute;
    String second;
    String activity;

    private String sortingVariable = "";

    public ImageRecord(String sortingVariable) {
        this.sortingVariable = sortingVariable;
    }

    public String getSortingVariable() {
        return this.sortingVariable;
    }
}
