package com.whu.myh2o;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<Fragment>fragments;

    private LinearLayout tabMap;
    private LinearLayout tabUpload;
    private LinearLayout tabComment;
    private LinearLayout tabPersonal;

    private ImageButton btnMap;
    private ImageButton btnUpload;
    private ImageButton btnComment;
    private ImageButton btnPersonal;

    private TextView tvMap;
    private TextView tvUpload;
    private TextView tvComment;
    private TextView tvPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ArcGISRuntimeEnvironment.setClientId("N7JPBdBkT209Rw80");
        initView();
        initEvent();
        setSelect(0);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setCustomView(R.layout.titlebar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    private void initEvent() {
        tabMap.setOnClickListener(this);
        tabUpload.setOnClickListener(this);
        tabComment.setOnClickListener(this);
        tabPersonal.setOnClickListener(this);
    }

    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.id_viewpager);

        tabMap = (LinearLayout) findViewById(R.id.id_map);
        tabUpload = (LinearLayout) findViewById(R.id.id_upload);
        tabComment = (LinearLayout) findViewById(R.id.id_comment);
        tabPersonal = (LinearLayout) findViewById(R.id.id_personal);

        btnMap = (ImageButton) findViewById(R.id.id_map_img);
        btnUpload = (ImageButton) findViewById(R.id.id_upload_img);
        btnComment = (ImageButton) findViewById(R.id.id_comment_img);
        btnPersonal = (ImageButton) findViewById(R.id.id_personal_img);

        tvMap=(TextView) findViewById(R.id.id_map_text);
        tvUpload=(TextView) findViewById(R.id.id_upload_text);
        tvComment=(TextView) findViewById(R.id.id_comment_text);
        tvPersonal=(TextView) findViewById(R.id.id_personal_text);

        fragments=new ArrayList<>();
        Fragment mapFragment=new MapFragment();
        Fragment uploadFragment=new UploadFragment();
        Fragment commentFragment=new CommentFragment();
        Fragment personalFragment=new PersonalFragment();
        fragments.add(mapFragment);
        fragments.add(uploadFragment);
        fragments.add(commentFragment);
        fragments.add(personalFragment);

        fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem=viewPager.getCurrentItem();
                setTab(currentItem);
               // displayTitle(currentItem);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.id_map:
                setSelect(0);
                break;
            case R.id.id_upload:
                setSelect(1);
                break;
            case R.id.id_comment:
                setSelect(2);
                break;
            case R.id.id_personal:
                setSelect(3);
                break;

            default:
                break;
        }
    }

    private void resetImgs()
    {
        btnMap.setImageResource(R.drawable.placeholder_normal);
        btnUpload.setImageResource(R.drawable.upload_normal);
        btnComment.setImageResource(R.drawable.edit_normal);
        btnPersonal.setImageResource(R.drawable.avatar_normal);

        tvMap.setTextColor(Color.parseColor("#000000"));
        tvUpload.setTextColor(Color.parseColor("#000000"));
        tvComment.setTextColor(Color.parseColor("#000000"));
        tvPersonal.setTextColor(Color.parseColor("#000000"));
    }

    public void setSelect(int i) {
        //改变图片颜色、切换内容区域
        setTab(i);
       // displayTitle(i);
        viewPager.setCurrentItem(i);
    }

    private void displayTitle(int i){
//        String title="";
//        switch(i){
//            case 0:
//                title=getString(R.string.map_ac);
//                break;
//            case 1:
//                title=getString(R.string.upload_ac);
//                break;
//            case 2:
//                title=getString(R.string.comment_ac);
//                break;
//            case 3:
//                title=getString(R.string.user_info);
//                break;
//            default:
//                break;
//        }
        ActionBar actionBar=getSupportActionBar();
        //actionBar.setTitle(title);
        actionBar.setCustomView(R.layout.titlebar_layout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    private void setTab(int i) {
        resetImgs();
        switch (i){
            case 0:
                btnMap.setImageResource(R.drawable.placeholder_pressed);
                tvMap.setTextColor(Color.parseColor("#6DC0F5"));
                break;
            case 1:
                btnUpload.setImageResource(R.drawable.upload_pressed);
                tvUpload.setTextColor(Color.parseColor("#6DC0F5"));
                break;
            case 2:
                btnComment.setImageResource(R.drawable.edit_pressed);
                tvComment.setTextColor(Color.parseColor("#6DC0F5"));
                break;
            case 3:
                btnPersonal.setImageResource(R.drawable.avatar_pressed);
                tvPersonal.setTextColor(Color.parseColor("#6DC0F5"));
                break;
        }
    }
}
