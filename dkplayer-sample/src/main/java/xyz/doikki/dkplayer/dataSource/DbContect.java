package com.example.test_3;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
public class DbContect extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DBNAME="user.db";   //  创建数据库名叫 Users
    private Context mContext;

    public DbContect(Context context){
        super(context,DBNAME,null,VERSION);
        mContext = context;
    }
    //创建数据库
    public void onCreate(SQLiteDatabase db){
        //创建收入表    user_tb
        db.execSQL("create table user_tb_test(id integer primary key autoincrement, name varchar(10)," +
                " password varchar(20))");
    }
    //数据库版本更新
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
        // db.execSQL("drop table if exists pwd_tb");
        db.execSQL("drop table if exists user_tb_test");
        onCreate(db);
    }


}

