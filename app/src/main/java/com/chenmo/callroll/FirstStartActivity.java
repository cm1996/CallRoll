package com.chenmo.callroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 第一次加载app时调用的开始界面
 */
public class FirstStartActivity extends AppCompatActivity{
    private ViewPager vp;
    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;
    private LinearLayout l;

    private int oldPosition = 0;
    private int newPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_app);

        vp = (ViewPager)findViewById(R.id.start_viewpager);
        dot1 = (ImageView)findViewById(R.id.start_dot1);
        dot2 = (ImageView)findViewById(R.id.start_dot2);
        dot3 = (ImageView)findViewById(R.id.start_dot3);
        l = (LinearLayout)findViewById(R.id.start_dot_linear);



        MyPagerAdapter mpa = new MyPagerAdapter();
        initPagerAdapter(mpa);
        initDot();

        vp.setAdapter(mpa);
        vp.addOnPageChangeListener(new MyPageChangedListener());


    }

    /**
     * 初始化底部的小点
     */
    public void initDot(){
        dot1.setImageResource(R.drawable.dot_cur);
        dot2.setImageResource(R.drawable.dot);
        dot3.setImageResource(R.drawable.dot);
    }
    /**
     *  初始化MyPagerAdapter中的图片
     */
    public void initPagerAdapter(MyPagerAdapter mpa){
        View linear1 = LayoutInflater.from(this).inflate(R.layout.start_view_pager,null);
        ImageView view1 = (ImageView) linear1.findViewById(R.id.start_image);
        view1.setBackgroundResource(R.mipmap.ic_launcher);
        // view1.setBackgroundResource(R.mipmap.pager1);
        mpa.myPagerList.add(linear1);

        View linear2 = LayoutInflater.from(this).inflate(R.layout.start_view_pager,null);
        ImageView view2 = (ImageView) linear2.findViewById(R.id.start_image);
        //view2.setBackgroundResource(R.mipmap.pager2);
        view2.setBackgroundResource(R.mipmap.ic_launcher);
        mpa.myPagerList.add(linear2);

        View linear3 = LayoutInflater.from(this).inflate(R.layout.start_view_pager,null);
        ImageView view3 = (ImageView) linear3.findViewById(R.id.start_image);
        view3.setBackgroundResource(R.mipmap.ic_launcher);
        //view3.setBackgroundResource(R.mipmap.pager3);
        mpa.myPagerList.add(linear3);

        View linear4 = LayoutInflater.from(this).inflate(R.layout.start_view_pager,null);
        ImageView view4 = (ImageView) linear4.findViewById(R.id.start_image);
        view4.setBackgroundResource(R.mipmap.ic_launcher);
        //view4.setBackgroundResource(R.mipmap.pager4);
        mpa.myPagerList.add(linear4);
    }
    /**
     *  页面滑动时更新小点
     */
    public class MyPageChangedListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageSelected(int position) {
            oldPosition = newPosition;
            newPosition = position;
            if(newPosition > 2){
                Intent intent = new Intent();
                intent.setClass(FirstStartActivity.this,Login.class);
                startActivity(intent);
                finish();
            }
            else
                changeDot(newPosition,oldPosition);
        }
    }
    /**
     *  更新小点
     */
    public void changeDot(int newPosition,int oldPosition){
        ImageView oldView = (ImageView)l.getChildAt(oldPosition);
        ImageView newView = (ImageView)l.getChildAt(newPosition);
        if(oldView != null && newView != null){
            oldView.setImageResource(R.drawable.dot);
            newView.setImageResource(R.drawable.dot_cur);
        }
    }
}
