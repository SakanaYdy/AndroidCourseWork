package xyz.doikki.dkplayer.dataSource;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.bean.Video;

public class DbcUtils {

    /**
     * 新用户创建 插入数据
     * @param helper
     * @param user
     */
    public static void insert(SQLiteOpenHelper helper, User user) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put("id",user.getId());
        values.put("name", user.getName());  // 用实际的列名替换 "column_name1"
        values.put("password", user.getPassword());  // 用实际的列名替换 "column_name2"
        // 继续添加其他列...

        db.insert("user", null, values);
    }

    /**
     * 获取用户信息，进行判断
     */
    public static User query(SQLiteOpenHelper helper, String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (TextUtils.isEmpty(username)) {
            return null;
        }
        String[] projection = {"id", "name", "password"}; // 列名数组，你需要查询的列
        String selection = "name=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("user", projection, selection, selectionArgs, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int passwordIndex = cursor.getColumnIndex("password");

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String password = cursor.getString(passwordIndex);

            user = new User(id, name, password);
        }

        if (cursor != null) {
            cursor.close();
        }

        return user;
    }

    /**
     * 插入视频数据
     * @param helper
     * @param
     */
    public static void insertVideo(SQLiteOpenHelper helper, Video video) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put("id",user.getId());
        values.put("user_id",video.getUserId() );  // 用实际的列名替换 "column_name1"
        values.put("thumb", video.getImgUrl());  // 用实际的列名替换 "column_name2"
        values.put("video",video.getVideoUrl());


        db.insert("video", null, values);
    }

    /**
     * 获取用户所属的所有视频
     * @param helper
     * @param userId
     * @return
     */
    public static List<Video> queryVideo(SQLiteOpenHelper helper, Integer userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Video> ans = new ArrayList<>();

        String[] projection = {"id", "user_id", "thumb", "video"};
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};

        try {
            Cursor cursor;
            if(userId == 0) {
                cursor = db.query("video", projection, null, null, null, null, null);
            }else{
                cursor = db.query("video", projection, selection, selectionArgs, null, null, null);
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex("id");
                    int userIdIndex = cursor.getColumnIndex("user_id");
                    int thumbIndex = cursor.getColumnIndex("thumb");
                    int videoIndex = cursor.getColumnIndex("video");

                    int id = cursor.getInt(idIndex);
                    int fetchedUserId = cursor.getInt(userIdIndex);
                    String thumb = cursor.getString(thumbIndex);
                    String video = cursor.getString(videoIndex);

                    // 根据你的 Video 类的构造函数创建 Video 对象
                    Video videoItem = new Video(id, fetchedUserId, thumb, video);
                    ans.add(videoItem);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Video video : ans){
            Log.d("Video",video.toString());
        }

        return ans;
    }


}
