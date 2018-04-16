package com.west2ol.april.module;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.west2ol.april.R;
import com.west2ol.april.adapter.AnswerAdapter;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.AnswerInfo;
import com.west2ol.april.entity.PostTokenInfo;
import com.west2ol.april.entity.PostAnswerInfo;
import com.west2ol.april.entity.QuestionsInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.LogUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;
import com.west2ol.april.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AnswerActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.question)
    TextView mTvQuestion;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.before)
    ImageView before;
    @BindView(R.id.after)
    ImageView after;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.liQA)
    LinearLayout liQA;
    @BindView(R.id.answers)
    RecyclerView recyclerView;
    @BindView(R.id.liPost)
    LinearLayout liPost;
    @BindView(R.id.answers2)
    LinearLayout linearLayout;
    private PreferenceUtil user;
    private int[] userAnswers;
    private int[] answers;
    private boolean isPosted;
    private int current,id;
    private String token;
    private List<QuestionsInfo.QuestionBean> questionsBeans = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_answer;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        before.setOnClickListener(view -> {
            current--;
            finishTask();
        });
        after.setOnClickListener(view -> {
            current++;
            finishTask();
        });
        token = user.get("token", null);
        id = user.get("id", 0);
        loadData();
    }

    @Override
    public void loadData() {
        PostTokenInfo questions = new PostTokenInfo();
        questions.setToken(token);
        questions.setUid(id);
        String str = new Gson().toJson(questions);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
        RetrofitHelper.getBwyxApi()
                .getQuestion(requestBody)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questionsInfo -> {
                    switch (questionsInfo.getStatus()) {
                        case 0:
                            questionsBeans.addAll(questionsInfo.getQuestion());
                            userAnswers=new int[questionsBeans.size()];
                            answers=new int[questionsBeans.size()];
                            finishTask();
                            break;
                        case -1:
                            throw new RuntimeException("错误,token无效!");
                        default:
                            throw new RuntimeException("未知错误!");
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void finishTask() {
        before.setVisibility(View.VISIBLE);
        after.setVisibility(View.VISIBLE);
        number.setVisibility(View.VISIBLE);
        liQA.setVisibility(View.VISIBLE);
        liPost.setVisibility(View.GONE);
        if (current == questionsBeans.size()) {
            liQA.setVisibility(View.GONE);
            after.setVisibility(View.INVISIBLE);
            liPost.setVisibility(View.VISIBLE);
            linearLayout.removeAllViews();
            FrameLayout frameLayout = new FrameLayout(this);
            for (int i = 0; i < userAnswers.length; i++) {
                Button button = new Button(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DeviceUtil.dip2px(40), DeviceUtil.dip2px(40));
                layoutParams.leftMargin = DeviceUtil.dip2px(50) * (i % 6) + DeviceUtil.dip2px(5);
                layoutParams.topMargin = DeviceUtil.dip2px(50) * (i / 6) + DeviceUtil.dip2px(5);
                int finalI = i;
                button.setOnClickListener(v -> {
                    current= finalI;
                    finishTask();
                });
                if(userAnswers[i]==0) {
                    button.setBackgroundResource(R.drawable.bg_round);
                    button.setTextColor(Color.parseColor("#000000"));
                }else {
                    button.setBackgroundResource(R.drawable.bg_round_click);
                    button.setTextColor(Color.parseColor("#ffffff"));
                }
                button.setText(String.valueOf(i+1));
                frameLayout.addView(button, layoutParams);
            }
            linearLayout.addView(frameLayout);
        } else {
            QuestionsInfo.QuestionBean questionsBean = questionsBeans.get(current);
            if (current == 0) {
                before.setVisibility(View.INVISIBLE);
            }if(isPosted&&current==questionsBeans.size()-1){
                after.setVisibility(View.INVISIBLE);
            }
            number.setText((current+1)+" / "+questionsBeans.size());
            mTvQuestion.setText(questionsBean.getDescription());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            AnswerAdapter adapter=new AnswerAdapter(this, questionsBean.getAnswers(),userAnswers[current],answers[current]);
            adapter.setOnItemClickListener((button,pos)->{
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setBackgroundResource(R.drawable.bg_round_click);
                userAnswers[current]=pos+1;
                current++;
                finishTask();
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        if(!isPosted) {
            new AlertDialog.Builder(this)
                    .setTitle("退出答题")
                    .setMessage("确定要退出答题吗?退出后答过的题目将作废!")
                    .setPositiveButton("确定", (dialogInterface, i) -> finish())
                    .setNegativeButton("取消", null)
                    .show();
        }else{
            finish();
        }
    }

    @OnClick(R.id.btn_go)
    void post(){
        int answerCount=0;
        for(int i:userAnswers){
            if(i>0){
                answerCount++;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("提交答案")
                .setMessage((answerCount==questionsBeans.size()?"":"你还有"+(questionsBeans.size()-answerCount)+"道题目没有完成!\n")
                        +"是否提交答案?")
                .setPositiveButton("确定",(a,b)->{
                    List<PostAnswerInfo.AnswersBean> list=new ArrayList<>();
                    PostAnswerInfo postAnswerInfo=new PostAnswerInfo();
                    postAnswerInfo.setToken(token);
                    postAnswerInfo.setUid(id);
                    for(int i=0;i<questionsBeans.size();i++){
                        PostAnswerInfo.AnswersBean answersBean=new PostAnswerInfo.AnswersBean();
                        answersBean.setAnswer_id(questionsBeans.get(i).getAnswers().get(userAnswers[i]-1).getAid());
                        answersBean.setQuestion_id(questionsBeans.get(i).getQid());
                        list.add(answersBean);
                    }
                    postAnswerInfo.setAnswers(list);
                    String str=new Gson().toJson(postAnswerInfo);
                    LogUtil.d("test",str);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), str);
                    RetrofitHelper.getBwyxApi()
                            .postQuestion(requestBody)
                            .compose(bindToLifecycle())
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                            .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(answerInfo -> {
                                switch (answerInfo.getStatus()){
                                    case 0:
                                        LogUtil.d("test",new Gson().toJson(answerInfo));
                                        List<AnswerInfo.IncorrectBean> incorrectBeans=answerInfo.getIncorrect();
                                        if(incorrectBeans.size()==0){
                                            startActivity(new Intent(this,PrizeActivity.class));
                                            finish();
                                        }else{
                                            new AlertDialog.Builder(this)
                                                    .setTitle("答题结果!")
                                                    .setMessage("非常遗憾,你有"+incorrectBeans.size()+"道问题回答错误!!\n按确定回顾,按取消退出")
                                                    .setPositiveButton("确定",(c,d)->{
                                                        isPosted=true;
                                                        current=0;
                                                        for(int i=0;i<questionsBeans.size();i++){
                                                            QuestionsInfo.QuestionBean questionBean=questionsBeans.get(i);
                                                            for(AnswerInfo.IncorrectBean incorrectBean:incorrectBeans){
                                                                if(incorrectBean.getQid()==questionBean.getQid()){
                                                                    for(int j=0;j<questionBean.getAnswers().size();j++){
                                                                        QuestionsInfo.QuestionBean.AnswersBean answersBean=questionBean.getAnswers().get(j);
                                                                        if(answersBean.getAid()==incorrectBean.getAid()){
                                                                            answers[i]=j+1;
                                                                            break;
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        finishTask();
                                                    })
                                                    .setNegativeButton("取消",(c,d)->{
                                                        finish();
                                                    })
                                            .show();
                                        }
                                        break;
                                    case 1:

                                        break;
                                }
                            },throwable -> {
                                throwable.printStackTrace();
                                SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                            });
                })
                .setNegativeButton("取消",null)
                .show();

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
