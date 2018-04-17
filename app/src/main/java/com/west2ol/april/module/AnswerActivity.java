package com.west2ol.april.module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.west2ol.april.entity.send.TokenInfo;
import com.west2ol.april.entity.send.AnswerInfo;
import com.west2ol.april.entity.receive.QuestionsInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.ErrorUtil;
import com.west2ol.april.utils.LogUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;
import com.west2ol.april.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AnswerActivity extends RxBaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.before)
    ImageView before;
    @BindView(R.id.after)
    ImageView after;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.leftTime)
    TextView leftTime;
    private PreferenceUtil user;
    private int[] userAnswers;
    private int[] answers;
    private boolean isPosted;
    private int current, id, seconds;
    private String token;
    LinearLayout linearLayout;
    private int timeAll = 60;
    private Subscription subscription;
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
            viewPager.setCurrentItem(current);
        });
        after.setOnClickListener(view -> {
            current++;
            viewPager.setCurrentItem(current);
        });
        token = user.get("token", null);
        id = user.get("id", 0);
        loadData();
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("");
        number.setVisibility(View.VISIBLE);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void loadData() {
        TokenInfo questions = new TokenInfo();
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
                            userAnswers = new int[questionsBeans.size()];
                            answers = new int[questionsBeans.size()];
                            long curTime = System.currentTimeMillis() / 1000;
                            LogUtil.d("test", String.valueOf(questionsInfo.getTimestamp()));
                            seconds = (int) (questionsInfo.getTimestamp() + timeAll - curTime);
                            setupViewPager(questionsBeans.size() + 1);
                            if (seconds > 0) {
                                timer();
                            } else {
                                postAnswers();
                            }
                            break;
                        case 1:
                            throw new RuntimeException("抱歉,活动时间已过!");
                        case 2:
                            throw new RuntimeException("错误,token无效!");
                        default:
                            throw new RuntimeException("未知错误,错误码:" + questionsInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.ShortToast(throwable.getMessage());
                    finish();
                });
    }


    private void timer() {
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(seconds + 1)
                .map(l -> seconds - l)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    long minute = time / 60;
                    long second = time % 60;
                    leftTime.setText((minute < 10 ? "0" : "") + minute + ":" + (second < 10 ? "0" : "") + second);
                }, Throwable::printStackTrace, this::postAnswers);
    }

    public void finishTask(View view, int current) {
        LinearLayout liQA = view.findViewById(R.id.liQA);
        liQA.setVisibility(View.VISIBLE);
        LinearLayout liPost = view.findViewById(R.id.liPost);
        liPost.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.answers2);
        if (current == questionsBeans.size()) {
            liQA.setVisibility(View.GONE);
            liPost.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_go).setOnClickListener(view1 -> post());
        } else {
            QuestionsInfo.QuestionBean questionsBean = questionsBeans.get(current);
            TextView mTvQuestion = view.findViewById(R.id.question);
            mTvQuestion.setText(questionsBean.getDescription());
            RecyclerView recyclerView = view.findViewById(R.id.answers);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            AnswerAdapter adapter = new AnswerAdapter(this, questionsBean.getAnswers(), userAnswers[current], answers[current]);
            adapter.setOnItemClickListener((button, pos) -> {
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setBackgroundResource(R.drawable.bg_round_click);
                userAnswers[current] = pos + 1;
                adapter.setUserAnswer(userAnswers[current]);
                adapter.notifyDataSetChanged();
                Observable.just(null)
                        .delay(100, TimeUnit.MILLISECONDS)
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> viewPager.setCurrentItem(current + 1));
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void drawAnswers() {
        linearLayout.removeAllViews();
        FrameLayout frameLayout = new FrameLayout(this);
        for (int i = 0; i < userAnswers.length; i++) {
            Button button = new Button(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DeviceUtil.dip2px(40), DeviceUtil.dip2px(40));
            layoutParams.leftMargin = DeviceUtil.dip2px(50) * (i % 6) + DeviceUtil.dip2px(5);
            layoutParams.topMargin = DeviceUtil.dip2px(50) * (i / 6) + DeviceUtil.dip2px(5);
            int finalI = i;
            button.setOnClickListener(v -> {
                viewPager.setCurrentItem(finalI);
            });
            if (userAnswers[i] == 0) {
                button.setBackgroundResource(R.drawable.bg_round);
                button.setTextColor(Color.parseColor("#000000"));
            } else {
                button.setBackgroundResource(R.drawable.bg_round_click);
                button.setTextColor(Color.parseColor("#ffffff"));
            }
            button.setText(String.valueOf(i + 1));
            frameLayout.addView(button, layoutParams);
        }
        linearLayout.addView(frameLayout);
    }

    private void setupViewPager(int count) {
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return count;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = getLayoutInflater().inflate(R.layout.layout_item_viewpager, null);
                container.addView(view);
                finishTask(view, position);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                if (current == questionsBeans.size()) {
                    after.setVisibility(View.INVISIBLE);
                    before.setVisibility(View.VISIBLE);
                    drawAnswers();
                } else {
                    if (position == 0) {
                        before.setVisibility(View.INVISIBLE);
                        after.setVisibility(View.VISIBLE);
                    } else if (isPosted && position == questionsBeans.size() - 1) {
                        after.setVisibility(View.INVISIBLE);
                        before.setVisibility(View.VISIBLE);
                    } else {
                        before.setVisibility(View.VISIBLE);
                        after.setVisibility(View.VISIBLE);
                    }
                    number.setText((position + 1) + " / " + questionsBeans.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        if (!isPosted) {
            new AlertDialog.Builder(this)
                    .setTitle("退出答题")
                    .setMessage("确定要退出答题吗?")
                    .setPositiveButton("确定", (dialogInterface, i) -> finish())
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            finish();
        }
    }

    private void post() {
        int answerCount = 0;
        for (int i : userAnswers) {
            if (i > 0) {
                answerCount++;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("提交答案")
                .setMessage((answerCount == questionsBeans.size() ? "" : "你还有" + (questionsBeans.size() - answerCount) + "道题目没有完成!\n")
                        + "是否提交答案?")
                .setPositiveButton("确定", (a, b) -> postAnswers())
                .setNegativeButton("取消", null)
                .show();

    }

    private void postAnswers() {
        leftTime.setVisibility(View.GONE);
        if (subscription != null) {
            subscription.unsubscribe();
        }
        List<AnswerInfo.AnswersBean> list = new ArrayList<>();
        AnswerInfo postAnswerInfo = new AnswerInfo();
        postAnswerInfo.setToken(token);
        postAnswerInfo.setUid(id);
        for (int i = 0; i < questionsBeans.size(); i++) {
            AnswerInfo.AnswersBean answersBean = new AnswerInfo.AnswersBean();
            if (userAnswers[i] > 0) {
                answersBean.setAnswer_id(questionsBeans.get(i).getAnswers().get(userAnswers[i] - 1).getAid());
            }
            answersBean.setQuestion_id(questionsBeans.get(i).getQid());
            list.add(answersBean);
        }
        postAnswerInfo.setAnswers(list);
        String str = new Gson().toJson(postAnswerInfo);
        LogUtil.d("test", str);
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
                    switch (answerInfo.getStatus()) {
                        case 0:
                            LogUtil.d("test", new Gson().toJson(answerInfo));
                            List<com.west2ol.april.entity.receive.AnswerInfo.IncorrectBean> incorrectBeans = answerInfo.getIncorrect();
                            if (incorrectBeans.size() == 0) {
                                startActivity(new Intent(this, PrizeActivity.class));
                                finish();
                            } else {
                                new AlertDialog.Builder(this)
                                        .setTitle("答题结果!")
                                        .setMessage("非常遗憾,你有" + incorrectBeans.size() + "道问题回答错误!!\n按确定回顾,按取消退出")
                                        .setPositiveButton("确定", (c, d) -> {
                                            isPosted = true;
                                            current = 0;
                                            for (int i = 0; i < questionsBeans.size(); i++) {
                                                QuestionsInfo.QuestionBean questionBean = questionsBeans.get(i);
                                                for (com.west2ol.april.entity.receive.AnswerInfo.IncorrectBean incorrectBean : incorrectBeans) {
                                                    if (incorrectBean.getQid() == questionBean.getQid()) {
                                                        for (int j = 0; j < questionBean.getAnswers().size(); j++) {
                                                            QuestionsInfo.QuestionBean.AnswersBean answersBean = questionBean.getAnswers().get(j);
                                                            if (answersBean.getAid() == incorrectBean.getAid()) {
                                                                answers[i] = j + 1;
                                                                break;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                            setupViewPager(questionsBeans.size());
                                            viewPager.setCurrentItem(current);
                                        })
                                        .setNegativeButton("取消", (c, d) -> {
                                            finish();
                                        })
                                        .show();
                            }
                            break;
                        default:
                            ErrorUtil.error(answerInfo.getStatus());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
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
