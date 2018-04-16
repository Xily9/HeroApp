package com.west2ol.april.module;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.west2ol.april.R;
import com.west2ol.april.base.RxBaseActivity;
import com.west2ol.april.entity.TimeInfo;
import com.west2ol.april.network.RetrofitHelper;
import com.west2ol.april.utils.DeviceUtil;
import com.west2ol.april.utils.PreferenceUtil;
import com.west2ol.april.utils.SnackbarUtil;
import com.west2ol.april.utils.ThemeUtil;
import com.west2ol.april.utils.ToastUtil;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class MainActivity extends RxBaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private AlertDialog dialog;
    private long exitTime,expTime;
    private PreferenceUtil user;
    private Fragment[] fragments;
    private int currentTabIndex,index;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        user = new PreferenceUtil(PreferenceUtil.FILE_USER);
        initToolBar();
        initNavigationView();
        initFragment();
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void initFragment() {
        HomeFragment homeFragment=HomeFragment.newInstance();
        PrizeListFragment prizeListFragment=PrizeListFragment.newInstance();
        fragments=new Fragment[]{
                homeFragment,
                prizeListFragment
        };
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, homeFragment)
                .show(homeFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                changeFragmentIndex(0);
                break;
            case R.id.nav_prize:
                changeFragmentIndex(1);
                break;
            case R.id.nav_theme:
                LinearLayout linearLayout = new LinearLayout(this);
                int padding = DeviceUtil.dip2px(20);
                linearLayout.setPadding(padding, padding, padding, padding);
                linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                FrameLayout frameLayout = new FrameLayout(this);
                int[] colorList = ThemeUtil.getColorList();
                int theme = ThemeUtil.getTheme();
                for (int i = 0; i < colorList.length; i++) {
                    Button button = new Button(this);
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setColor(getResources().getColor(colorList[i]));
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                    button.setBackground(gradientDrawable);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DeviceUtil.dip2px(40), DeviceUtil.dip2px(40));
                    layoutParams.leftMargin = DeviceUtil.dip2px(50) * (i % 5) + DeviceUtil.dip2px(5);
                    layoutParams.topMargin = DeviceUtil.dip2px(50) * (i / 5) + DeviceUtil.dip2px(5);
                    int finalI = i;
                    button.setOnClickListener(v -> {
                        new PreferenceUtil(PreferenceUtil.FILE_SETTING).put("theme", finalI);
                        dialog.dismiss();
                        recreate();
                    });
                    if (i == theme) {
                        button.setText("✔");
                        button.setTextColor(Color.parseColor("#ffffff"));
                        button.setTextSize(15);
                        button.setGravity(Gravity.CENTER);
                    }
                    frameLayout.addView(button, layoutParams);
                }
                linearLayout.addView(frameLayout);
                dialog = new AlertDialog.Builder(MainActivity.this).setTitle("设置主题").setView(linearLayout).show();
                break;
            case R.id.nav_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        } mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavigationView() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        View sideMenuView = mNavigationView.inflateHeaderView(R.layout.layout_side_menu);
        ((TextView)sideMenuView.findViewById(R.id.id)).setText("您的ID："+user.get("id",0));
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (System.currentTimeMillis() - exitTime < 2000) {
            super.onBackPressed();
        } else {
            ToastUtil.ShortToast("再按一次返回键退出程序");
            exitTime = System.currentTimeMillis();
        }
    }
    private void switchFragment() {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.fragment, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentTabIndex = index;
    }
    private void changeFragmentIndex( int currentIndex) {
        index = currentIndex;
        switchFragment();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
