package xyz.doikki.dkplayer.bean;

public class Video {

    private int id;
    private int userId;
    // private String name;
    private String imgUrl;
    private String videoUrl;

    public Video(int userId, int id, String imgUrl, String videoUrl) {
        this.userId = userId;
        this.id = id;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
