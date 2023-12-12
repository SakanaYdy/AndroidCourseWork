package xyz.doikki.dkplayer.dataModel;

public class YoutubeDataModel
{
    private String _title = "";
    private String _description = "";
    private String _publishedAt = "";
    private String _thumbNail = "";

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String _title)
    {
        this._title = _title;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDescription(String _description)
    {
        this._description = _description;
    }

    public String getPublishedAt()
    {
        return _publishedAt;
    }

    public void setPublishedAt(String _publishedAt)
    {
        this._publishedAt = _publishedAt;
    }

    public String getThumbNail()
    {
        return _thumbNail;
    }

    public void setThumbNail(String _thumbNail)
    {
        this._thumbNail = _thumbNail;
    }

    // 覆盖toString方法
    @Override
    public String toString()
    {
        return "YoutubeDataModel{" + "_title='" + _title +
                '\'' + ", _description='" + _description +
                '\'' + ", _publishedAt='" + _publishedAt +
                '\'' + ", _thumbNail='" + _thumbNail +
                '\'' + '}';
    }

}
