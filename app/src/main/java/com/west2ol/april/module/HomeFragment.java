package com.west2ol.april.module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseFragment;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.ErrorUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeFragment extends RxBaseFragment {
    @BindView(R.id.btn_go)
    Button mBtnGo;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.time)
    TextView mTvtime;
    @BindView(R.id.rules)
    TextView mTvRules;
    private long expTime;
    private PreferenceUtil user;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void finishCreateView(Bundle state) {
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        initToolbar();
        loadData();
    }

    @Override
    protected void initToolbar() {
        getActivity().setTitle("百万英雄");
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBwyxApi()
                .getTime()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    mBtnGo.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                })
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeInfo -> {
                    switch (timeInfo.getStatus()) {
                        case 0:
                            expTime = timeInfo.getTime();
                            finishTask();
                            break;
                        default:
                            ErrorUtil.error(timeInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getActivity().getWindow().getDecorView(), throwable.getMessage());
                });

    }

    @Override
    public void finishTask() {
        //String strTime=String.valueOf(System.currentTimeMillis());
        //long curTime=Long.valueOf(strTime.substring(0,strTime.length()-3));
        String rules = "规则：\n"
                + "* 在奖品未抽取完且未达到活动期限，您可以无限次参与答题，答题时限为1分钟\n"
                + "* 每次题目为随机抽取的6道问题，全部答对均可参与抽奖\n"
                + "* 尽情愉快的答题吧!";
        mTvRules.setText(rules);
        long curTime = System.currentTimeMillis() / 1000;
        if (expTime > curTime) {
            mBtnGo.setVisibility(View.VISIBLE);
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timeText = time.format(expTime * 1000);
            mTvtime.setText("活动时间：\n即日起至" + timeText);
        } else {
            mTvtime.setText("非常抱歉，当前不在活动时间内！");
        }
    }

    @OnClick(R.id.btn_go)
    void go() {
        startActivity(new Intent(getActivity(), AnswerActivity.class));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initToolbar();
        }
    }
}
