package xyz.doikki.dkplayer.bean;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> videoPath = new MutableLiveData<>();

    public void setVideoPath(String path) {
        videoPath.setValue(path);
    }

    public LiveData<String> getVideoPath() {
        return videoPath;
    }
}
