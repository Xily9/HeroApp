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
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    mBtnGo.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                })
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeInfo->{
                    switch (timeInfo.getStatus()){
                        case 0:
                            expTime=timeInfo.getTime();
                            finishTask();
                            break;
                        default:
                            throw new RuntimeException("未知错误!");
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
        long curTime=System.currentTimeMillis()/1000;
        if(expTime>curTime) {
            mBtnGo.setVisibility(View.VISIBLE);
            SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String timeText=time.format(expTime*1000);
            mTvtime.setText("活动时间：即日起至"+timeText);
        }else{
            mTvtime.setText("非常抱歉，当前不在活动时间内！");
        }
    }
    @OnClick(R.id.btn_go)
    void go() {
        startActivity(new Intent(getActivity(), AnswerActivity.class));
    }

}
