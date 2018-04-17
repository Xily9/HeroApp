package com.west2ol.april.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.send.LoginRegInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.ErrorUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends RxBaseActivity {
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.user)
    EditText username;
    @BindView(R.id.pass)
    EditText password;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private PreferenceUtil user;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("登陆");
    }

    @OnClick(R.id.btn_login)
    void login() {
        DeviceUtil.hideSoftInput(this);
        LoginRegInfo sendLoginInfo = new LoginRegInfo();
        sendLoginInfo.setUsername(username.getText().toString());
        sendLoginInfo.setPassword(password.getText().toString());
        String str = new Gson().toJson(sendLoginInfo);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .login(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginInfo -> {
                    switch (loginInfo.getStatus()) {
                        case 0:
                            user.put("name", username.getText().toString())
                                    .put("id", loginInfo.getUid())
                                    .put("token", loginInfo.getToken());
                            jumpToMain();
                            break;
                        default:
                            ErrorUtil.error(loginInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    @OnClick(R.id.guest)
    void guestLogin() {
        DeviceUtil.hideSoftInput(this);
        RetrofitHelper.getBwyxApi()
                .guestLogin()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginInfo -> {
                    switch (loginInfo.getStatus()) {
                        case 0:
                            user.put("id", loginInfo.getUid())
                                    .put("token", loginInfo.getToken());
                            jumpToMain();
                            break;
                        default:
                            ErrorUtil.error(loginInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });

    }

    @OnClick(R.id.reg)
    void reg() {
        startActivity(new Intent(this, RegActivity.class));
    }

    private void jumpToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
