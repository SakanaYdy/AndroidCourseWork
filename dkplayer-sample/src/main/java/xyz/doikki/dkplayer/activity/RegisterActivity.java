package xyz.doikki.dkplayer.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.bean.User;
import xyz.doikki.dkplayer.dataSource.DbContect;
import xyz.doikki.dkplayer.dataSource.DbcUtils;
import xyz.doikki.dkplayer.util.ToastUtil;

public class RegisterActivity extends AppCompatActivity {

    DbContect helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helper=new DbContect(  RegisterActivity.this);

        EditText na = findViewById(R.id.name_1);
        EditText pass = findViewById(R.id.password_1);
        EditText confirmed_pass = findViewById(R.id.password_2);
        Button register = findViewById(R.id.register_1);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(na.getText());
                String password = String.valueOf(pass.getText());
                String confirmedPassword = String.valueOf(confirmed_pass.getText());
                Toast.makeText(getBaseContext(),"name:"+ name +"password:" + password,Toast.LENGTH_SHORT).show();

                if(!check(helper,name)){
                    ToastUtil.ShowMsg(getBaseContext(),"用户名已经存在");
                }
                else if(!password.equals(confirmedPassword)){
                    ToastUtil.ShowMsg(getBaseContext(),"两次密码不一致");
                }else {
                    User user = new User(name,password);
                    DbcUtils.insert(helper,user);
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, MainActivity_.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 检查用户名是否存在
     * @return
     */
    public boolean check(SQLiteOpenHelper helper,String name){
        User query = DbcUtils.query(helper, name);
        if(query != null) return false;
        return true;
    }
}
