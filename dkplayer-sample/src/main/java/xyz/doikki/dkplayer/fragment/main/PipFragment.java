package xyz.doikki.dkplayer.fragment.main;

import android.view.View;
import android.widget.Toast;

import xyz.doikki.dkplayer.R;
import xyz.doikki.dkplayer.fragment.BaseFragment;

public class PipFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_pip;
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.test1).setOnClickListener(this);
        findViewById(R.id.test2).setOnClickListener(this);
        findViewById(R.id.test3).setOnClickListener(this);
        findViewById(R.id.test4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test1:
                Toast.makeText(getContext(),"test1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.test2:
                Toast.makeText(getContext(),"test2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.test3:
                Toast.makeText(getContext(),"test3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.test4:
                Toast.makeText(getContext(),"test4", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
