package com.chenmo.callroll;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * MyViewPager的适配器
 */
public class MyPagerAdapter extends PagerAdapter {
    public List<View> myPagerList;

    public MyPagerAdapter(){
        myPagerList = new ArrayList<View>();
    }

    //返回可用的view的数量
    @Override
    public int getCount() {
        return myPagerList.size();
    }
    //判断页面是否跟指定的key对象关联，key对象由instantiateItem(ViewGroup, int)返回
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    //删除指定位置的页面；适配器负责从view容器中删除view，然而它只保证在finishUpdate(ViewGroup)返回时才完成。
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(myPagerList.get(position));
    }
    //在指定的位置创建页面；适配器负责添加view到这个容器中，然而它只保证在finishUpdate(ViewGroup)返回时才完成
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(myPagerList.get(position));
        return myPagerList.get(position);
    }
}
