package com.speedy.radar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Speedy on 2017/5/4.
 */
public class ScanResultActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;

    private MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        initToolbar();
        initRecyclerView();
        loadData();
    }


    private void loadData() {
        List<Friend> friends = getIntent().getExtras().getParcelableArrayList("friends");
        myAdapter.addFriends(friends);
        myAdapter.notifyDataSetChanged();
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.scan_result);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        ArrayList<Friend> friends = new ArrayList<>();

        int lastAnimatedPosition = -1;

        public void addFriends(List<Friend> list){
            if(list != null){
                friends.addAll(list);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_result_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Friend friend = friends.get(position);
            holder.bindData(friend);
            viewHolderAnimate(holder,position);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        private void viewHolderAnimate(MyViewHolder viewHolder,int position){
            //已经显示过的Item 不再执行动画
            if(position > lastAnimatedPosition){
                lastAnimatedPosition = position;
                viewHolder.itemView.setAlpha(0f);//初始状态完全透明
                viewHolder.itemView.setTranslationY(2000f); //相对于原始位置下方平移1000

                viewHolder.itemView.animate()
                        .translationY(0)
                        .alpha(1.f)
                        .setStartDelay(100 * position )//根据item的位置设置延迟时间，达到依次动画一个接一个进行的效果
                        .setInterpolator(new AccelerateInterpolator())//设置动画效果为加速
                        .setDuration(800)
                        .start();
            }
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivAvtar;
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivAvtar = (ImageView) itemView.findViewById(R.id.ivAvtar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }

        public void bindData(final Friend friend){
            ivAvtar.setImageResource(friend.imageResId);
            tvName.setText(friend.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"你好，我是 "+friend.name,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
