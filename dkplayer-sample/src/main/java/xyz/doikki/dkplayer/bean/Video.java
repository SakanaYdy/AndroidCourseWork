package xyz.doikki.dkplayer.bean;

public class Video {

    private int userId;
    private String name;
    private String imgUrl;
    private String videoUrl;

    public Video(int userId, String name, String imgUrl, String videoUrl) {
        this.userId = userId;
        this.name = name;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
