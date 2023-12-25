package xyz.doikki.dkplayer.fragment.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploadService extends Service {

    public static final String EXTRA_FILE_PATH = "file_path";
    private static final String SERVER_URL = "your_server_url"; // 替换为你的服务器地址

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
            if (filePath != null) {
                uploadFile(filePath);
            }
        }
        return START_NOT_STICKY;
    }

    private void uploadFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream outputStream = connection.getOutputStream();
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 文件上传成功
                Log.d("FileUploadService", "File uploaded successfully");
            } else {
                // 文件上传失败
                Log.e("FileUploadService", "File upload failed. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
            Log.e("FileUploadService", "Error uploading file: " + e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
