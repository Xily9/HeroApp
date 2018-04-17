package com.west2ol.april.module;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.send.TokenInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
            jumpToLogin();
        }else{
            loadData();
        }
    }

    @Override
    public void loadData() {
        TokenInfo questions = new TokenInfo();
        questions.setToken(user.get("token", null));
        questions.setUid(user.get("id", 0));
        String str = new Gson().toJson(questions);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .refreshToken(requestBody)
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
                            jumpToLogin();
                    }
                },throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    private void jumpToMain() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void jumpToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
