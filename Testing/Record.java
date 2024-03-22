public class Record {
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

    public Record(String sortingVariable) {
        this.sortingVariable = sortingVariable;
    }

    public String getSortingVariable() {
        return this.sortingVariable;
    }
}
