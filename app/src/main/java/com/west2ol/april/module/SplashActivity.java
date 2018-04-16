package com.west2ol.april.module;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends RxBaseActivity{
    private PreferenceUtil user;
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        DeviceUtil.hideStatusBar(getWindow(),true);
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        if(TextUtils.isEmpty(user.get("token",null))){
            loadData();
        }else{
            jumpToMain();
        }
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBwyxApi()
                .getToken()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginInfo -> {
                    switch (loginInfo.getStatus()) {
                        case 0:
                            user.put("id", loginInfo.getUid());
                            user.put("token", loginInfo.getToken());
                            jumpToMain();
                            break;
                        default:
                            throw new RuntimeException("未知错误!");

                    }
                },throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }
    void jumpToMain(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
