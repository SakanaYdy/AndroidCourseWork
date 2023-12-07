package xyz.doikki.dkplayer.fragment.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.dataSource.DBOpenHelper;
import xyz.doikki.dkplayer.fragment.BaseFragment;

public class ExtensionFragment extends BaseFragment implements View.OnClickListener {

    // 建立数据库连接
    Connection conn = (Connection) DBOpenHelper.getConn();

    String sql = "select user_id from video where id=1";

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_extension;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // 检查连接对象是否为null
        if (conn == null) {
            // 处理连接对象为null的情况，可能需要进行日志记录或其他操作
            Toast.makeText(getContext(),"空指针",Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()){
            case R.id.update:
                Toast.makeText(getContext(),"更新信息", Toast.LENGTH_SHORT).show();
                Statement st = null;
                try {
                    st = (Statement) conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    Toast.makeText(getContext(),"获取成功",Toast.LENGTH_SHORT).show();
                    System.out.println("----------------");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.upload:
                Intent myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent,10);
                Toast.makeText(getContext(),"上传视频", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                Toast.makeText(getContext(),"用户登出", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    public void upload(){

    }
}
