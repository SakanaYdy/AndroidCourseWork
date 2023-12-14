package xyz.doikki.dkplayer.dataModel;

public class CommentDataModel
{

    private String _authorDisplayName = "";
    private String _textDisplay = "";
    private String _publishedAt = "";

//    public CommentDataModel() {}

    public CommentDataModel(String authorDisplayName, String textDisplay, String publishedAt)
    {
        this._authorDisplayName = authorDisplayName;
        this._textDisplay = textDisplay;
        this._publishedAt = publishedAt;
    }

    public String getAuthorDisplayName()
    {
        return _authorDisplayName;
    }

//    public void setAuthorDisplayName(String authorDisplayName)
//    {
//        this._authorDisplayName = authorDisplayName;
//    }

    public String getTextDisplay()
    {
        return _textDisplay;
    }

//    public void setTextDisplay(String textDisplay)
//    {
//        this._textDisplay = textDisplay;
//    }

    public String getPublishedAt()
    {
        return _publishedAt;
    }

//    public void setPublishedAt(String publishedAt)
//    {
//        this._publishedAt = publishedAt;
//    }


    @Override
    public String toString()
    {
        return "CommentDataModel{" + "authorDisplayName='" + _authorDisplayName + '\''
                + ", textDisplay='" + _textDisplay + '\'' + ", publishedAt='" + _publishedAt + '\''
                + '}';
    }
}
