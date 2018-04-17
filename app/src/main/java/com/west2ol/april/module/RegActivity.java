package com.west2ol.april.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.send.LoginRegInfo;
import com.west2ol.april.entity.send.TransferInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.ErrorUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegActivity extends RxBaseActivity {

    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.user)
    EditText username;
    @BindView(R.id.pass)
    EditText password;
    @BindView(R.id.passagain)
    EditText passAgain;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private PreferenceUtil user;
    private boolean isGuest;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reg;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        Intent intent = getIntent();
        isGuest = intent.getBooleanExtra("guest", false);
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("注册");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.btn_reg)
    void reg() {
        DeviceUtil.hideSoftInput(this);
        String userStr = username.getText().toString();
        String passStr = password.getText().toString();
        String passAgainStr = passAgain.getText().toString();
        Observable.just(null)
                .flatMap(o -> {
                    if (userStr.isEmpty() || passStr.isEmpty() || passAgainStr.isEmpty()) {
                        return Observable.error(new RuntimeException("输入不能为空!"));
                    } else if (!passStr.equals(passAgainStr)) {
                        return Observable.error(new RuntimeException("两次密码输入不一致!"));
                    } else if (isGuest) {
                        TransferInfo transferInfo = new TransferInfo();
                        transferInfo.setUid(user.get("id", 0));
                        transferInfo.setToken(user.get("token", null));
                        transferInfo.setPassword(passStr);
                        transferInfo.setUsername(userStr);
                        String str = new Gson().toJson(transferInfo);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
                        return RetrofitHelper.getBwyxApi()
                                .transfer(requestBody);
                    } else {
                        LoginRegInfo regInfo = new LoginRegInfo();
                        regInfo.setUsername(userStr);
                        regInfo.setPassword(passStr);
                        String str = new Gson().toJson(regInfo);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
                        return RetrofitHelper.getBwyxApi()
                                .reg(requestBody);
                    }
                }).compose(bindToLifecycle())
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
                        default:
                            ErrorUtil.error(loginInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    private void jumpToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
