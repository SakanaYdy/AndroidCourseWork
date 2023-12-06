package xyz.doikki.dkplayer.fragment.main;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.fragment.BaseFragment;

public class ExtensionFragment extends BaseFragment implements View.OnClickListener {
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

        switch (v.getId()){
            case R.id.update:
                Toast.makeText(getContext(),"更新信息", Toast.LENGTH_SHORT).show();
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
