package xyz.doikki.dkplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.util.Log;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;

public class LoginActivity extends AppCompatActivity {

    DbContect helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper=new DbContect(  LoginActivity.this);

        EditText na = findViewById(R.id.name);
        EditText pass = findViewById(R.id.password);


        Button login_2 = findViewById(R.id.login_2);
        login_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(na.getText());
                String password = String.valueOf(pass.getText());

                Toast.makeText(getBaseContext(),"name:"+ name +"password:" + password,Toast.LENGTH_SHORT).show();
                User query = DbcUtils.query(helper, name);
                Log.d("Info", "User ID: " + query.getId() + ", Name: " + query.getName() + ", Password: " + query.getPassword());
                if(query == null || !query.getPassword().equals(password)){
                    Toast.makeText(getBaseContext(),"用户名不存在或密码错误",Toast.LENGTH_SHORT);
                }
                else{
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity_.class);
                    startActivity(intent);
                }
            }
        });
    }
}
