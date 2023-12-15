package xyz.doikki.dkplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.util.Log;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.fragment.main.ExtensionFragment;

public class LoginActivity extends AppCompatActivity {

    DbContect helper;
    String name;
    String password;
    Boolean isSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new DbContect(LoginActivity.this);

        EditText na = findViewById(R.id.name);
        EditText pass = findViewById(R.id.password);

        Switch viewById = findViewById(R.id.remember);

        // 用来记录密码缓存
        SharedPreferences spf = getSharedPreferences("users", MODE_PRIVATE);
        name = spf.getString("loginname", "");
        password = spf.getString("loginpassword", "");
        isSave = spf.getBoolean("issave", false);

        // 自动填写用户名和密码
        na.setText(name);
        pass.setText(password);
        viewById.setChecked(isSave);

        Button login_2 = findViewById(R.id.login_2);
        login_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = String.valueOf(na.getText());
                password = String.valueOf(pass.getText());
                isSave = viewById.isChecked();

                // Toast.makeText(getBaseContext(), "name:" + name + " password:" + password, Toast.LENGTH_SHORT).show();

                User query = DbcUtils.query(helper, name);

                if (query == null || !query.getPassword().equals(password)) {
                    Toast.makeText(getBaseContext(), "用户名不存在或密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    // 登录成功后保存用户名和密码
                    if (isSave) {
                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("loginname", name);
                        editor.putString("loginpassword", password);
                        editor.putBoolean("issave", isSave);
                        editor.apply();
                    } else {
                        // 不记住密码，清除保存的用户名和密码
                        SharedPreferences.Editor editor = spf.edit();
                        editor.remove("loginname");
                        editor.remove("loginpassword");
                        editor.remove("issave");
                        editor.apply();
                    }
                    // 跳转到主界面
                    Intent intent = new Intent();
                    // 组件间信息传递，传递登录用户信息
                    Bundle bundle = new Bundle();
                    bundle.putString("username",name);
                    bundle.putString("pass",password);
                    intent.putExtra("login",bundle);
                    // 向fragment传递
                    ExtensionFragment fragment = new ExtensionFragment();
                    fragment.setArguments(bundle);

                    intent.setClass(LoginActivity.this, MainActivity_.class);
                    startActivity(intent);
                }
            }
        });
    }
}

