package com.west2ol.april.module;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.adapter.PrizeListAdapter;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.send.TokenInfo;
import com.west2ol.april.entity.receive.PrizeListInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.ErrorUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrizeActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.prizes)
    RecyclerView prizes;
    boolean isPrize;
    private PreferenceUtil user;
    int id;
    String token;
    List<PrizeListInfo.PrizeBean> prizeBeans = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_prize;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        token = user.get("token", null);
        id = user.get("id", 0);
        initToolBar();
        loadData();
    }

    @Override
    public void loadData() {
        TokenInfo questions = new TokenInfo();
        questions.setToken(token);
        questions.setUid(id);
        String str = new Gson().toJson(questions);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .getPrizeList(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prizeListInfo -> {
                    switch (prizeListInfo.getStatus()) {
                        case 0:
                            if (prizeListInfo.getPrize().size() == 0) {
                                new AlertDialog.Builder(this)
                                        .setTitle("非常抱歉!")
                                        .setMessage("虽然你答对了全部题目,但是,,,没有奖品了T_T")
                                        .setPositiveButton("确定", (dialogInterface, i) -> finish())
                                        .setCancelable(false)
                                        .show();
                            } else {
                                prizeBeans.addAll(prizeListInfo.getPrize());
                                finishTask();
                            }
                            break;
                        case 5:
                            new AlertDialog.Builder(this)
                                    .setTitle("非常抱歉!")
                                    .setMessage("虽然你答对了全部题目,但是,,,没有奖品了T_T")
                                    .setPositiveButton("确定", (dialogInterface, i) -> finish())
                                    .setCancelable(false)
                                    .show();
                            break;
                        default:
                            ErrorUtil.error(prizeListInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    @Override
    public void finishTask() {
        prizes.setLayoutManager(new LinearLayoutManager(this));
        prizes.setAdapter(new PrizeListAdapter(this, prizeBeans));
    }

    @OnClick(R.id.btn_go)
    void post() {
        TokenInfo questions = new TokenInfo();
        questions.setToken(token);
        questions.setUid(id);
        String str = new Gson().toJson(questions);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .getPrize(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prizeInfo -> {
                    switch (prizeInfo.getStatus()) {
                        case 0:
                            new AlertDialog.Builder(this)
                                    .setTitle("抽奖")
                                    .setMessage("恭喜你获得了" + prizeInfo.getDescription() + "!")
                                    .setPositiveButton("确定", (a, b) -> {
                                        finish();
                                    })
                                    .setCancelable(false)
                                    .show();
                            isPrize = true;
                            break;
                        default:
                            ErrorUtil.error(prizeInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("抽奖页面");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isPrize) {
            new AlertDialog.Builder(this)
                    .setTitle("退出抽奖")
                    .setMessage("确定要退出抽奖吗?退出后将无法抽奖!")
                    .setPositiveButton("确定", (dialogInterface, i) -> finish())
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
