package com.west2ol.april.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.west2ol.april.utils.ThemeUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RxBaseActivity extends RxAppCompatActivity {
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主题
        ThemeUtil.setTheme(this);
        //设置布局内容
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        bind = ButterKnife.bind(this);
        //初始化控件
        initViews(savedInstanceState);
    }

    public abstract
    @LayoutRes
    int getLayoutId();

    public abstract void initViews(Bundle savedInstanceState);

    public void initToolBar() {
    }

    public void loadData() {
    }

    public void finishTask() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
