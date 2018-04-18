package com.west2ol.april.module;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
    }
    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("关于");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.cheat)
    void cheat() {
        PreferenceUtil user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        boolean isCheat = user.get("cheat", false);
        user.put("cheat", !isCheat);
        ToastUtil.ShortToast((isCheat ? "关闭" : "开启") + "作弊模式成功!!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
