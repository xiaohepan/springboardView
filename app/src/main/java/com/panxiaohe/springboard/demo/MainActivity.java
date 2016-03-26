package com.panxiaohe.springboard.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.panxiaohe.springboard.library.FavoritesItem;
import com.panxiaohe.springboard.library.MenuView;
import com.panxiaohe.springboard.library.SpringboardView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    private MenuView springboard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        springboard = (MenuView) findViewById(R.id.springboard);

        ArrayList<MyButtonItem> buttons = new ArrayList<MyButtonItem>();
        buttons.add(new MyButtonItem("0", "item0", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("1", "item1", R.drawable.detail_icon_fav_normal));
        buttons.add(new MyButtonItem("2", "item2", R.drawable.detail_icon_share_normal));
        buttons.add(new MyButtonItem("3", "item3", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("4", "item4", R.drawable.detail_icon_fav_normal));
        buttons.add(new MyButtonItem("5", "item5", R.drawable.detail_icon_share_normal));
        buttons.add(new MyButtonItem("6", "item6", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("7", "item7", R.drawable.detail_icon_fav_normal));
        buttons.add(new MyButtonItem("8", "item8", R.drawable.detail_icon_share_normal));
        buttons.add(new MyButtonItem("9", "item9", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("10", "item10", R.drawable.detail_icon_fav_normal));
        buttons.add(new MyButtonItem("11", "item11", R.drawable.detail_icon_share_normal));
        buttons.add(new MyButtonItem("12", "item12", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("13", "item13", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("14", "item14", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("15", "item15", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("16", "item16", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("17", "item17", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("18", "item18", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("19", "item19", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("20", "item20", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("21", "item21", R.drawable.detail_icon_back_normal));
        buttons.add(new MyButtonItem("22", "item22", R.drawable.detail_icon_back_normal));

        MyAdapter myAdapter = new MyAdapter(buttons);
        springboard.setAdapter(myAdapter);
        springboard.setOnItemClickListener(new SpringboardView.OnItemClickListener()
        {
            @Override
            public void onItemClick(FavoritesItem item)
            {
                MyButtonItem myItem = (MyButtonItem) item;
//                Toast.makeText(MainActivity.this, " button : " + myItem.getData().getName() + "is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        springboard.setOnPageChangedListener(new SpringboardView.OnPageChangedListener()
        {
            @Override
            public void onPageScroll(int from, int to)
            {
//                Toast.makeText(MainActivity.this, " springboardview scroll from page#" + from + " to page#" + to, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageCountChange(int oldCount, int newCount)
            {
//                Toast.makeText(MainActivity.this, "springboardview page count has changed from #" + oldCount + " to #" + newCount, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
