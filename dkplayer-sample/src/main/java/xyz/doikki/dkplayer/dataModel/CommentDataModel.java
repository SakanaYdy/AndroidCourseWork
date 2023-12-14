package xyz.doikki.dkplayer.dataModel;

public class CommentDataModel {

    private String authorDisplayName;
    private String textDisplay;
    private String publishedAt;

    public CommentDataModel(String authorDisplayName, String textDisplay, String publishedAt) {
        this.authorDisplayName = authorDisplayName;
        this.textDisplay = textDisplay;
        this.publishedAt = publishedAt;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getTextDisplay() {
        return textDisplay;
    }

    public void setTextDisplay(String textDisplay) {
        this.textDisplay = textDisplay;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    // Override toString method
    @Override
    public String toString() {
        return "CommentDataModel{" +
                "authorDisplayName='" + authorDisplayName + '\'' +
                ", textDisplay='" + textDisplay + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }
}
