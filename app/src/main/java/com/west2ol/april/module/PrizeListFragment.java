package com.west2ol.april.module;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.adapter.OwnPrizeAdapter;
import com.west2ol.april.adapter.PrizeListAdapter;
import com.west2ol.april.base.RxBaseFragment;
import com.west2ol.april.entity.PostTokenInfo;
import com.west2ol.april.entity.PrizeListInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.ColorUtil;
import com.west2ol.april.utils.LogUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrizeListFragment extends RxBaseFragment {
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.prizes)
    RecyclerView prizes;
    private PreferenceUtil user;
    List<PrizeListInfo.PrizeBean> prizeBeans=new ArrayList<>();
    int id;
    String token;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_prizelist;
    }

    public static PrizeListFragment newInstance() {
        return new PrizeListFragment();
    }

    @Override
    public void finishCreateView(Bundle state) {
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        token = user.get("token", null);
        id = user.get("id", 0);
        initToolbar();
        swipeRefreshLayout.setColorSchemeColors(ColorUtil.getAttrColor(getActivity(),R.attr.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        loadData();
    }
    @Override
    protected void initToolbar() {
        getActivity().setTitle("奖品列表");
    }

    @Override
    protected void loadData() {
        PostTokenInfo questions = new PostTokenInfo();
        questions.setToken(token);
        questions.setUid(id);
        String str = new Gson().toJson(questions);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .getOwnPrize(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> swipeRefreshLayout.setRefreshing(true))
                .doOnUnsubscribe(() -> swipeRefreshLayout.setRefreshing(false))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prizeListInfo -> {
                    switch (prizeListInfo.getStatus()) {
                        case 0:
                            if(prizeListInfo.getPrize().size()==0){
                                throw new RuntimeException("大小为空!!");
                            }else {
                                prizeBeans.clear();
                                prizeBeans.addAll(prizeListInfo.getPrize());
                                finishTask();
                            }break;
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
        prizes.setLayoutManager(new LinearLayoutManager(getActivity()));
        prizes.setAdapter(new OwnPrizeAdapter(getActivity(),prizeBeans));
    }
}
