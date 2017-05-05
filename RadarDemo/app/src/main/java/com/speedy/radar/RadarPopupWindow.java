package com.speedy.radar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;



/**
 * Created by Speedy on 2017/5/4.
 */
public class RadarPopupWindow extends PopupWindow implements IScanView{

    private Activity mActivity;

    private int width;
    private int height;

    private LayoutInflater mLayoutInflater;
    private View contentView;

    private ImageView ivWave;
    private ImageView ivCirle;
    private ImageView ivRadarScan;

    private FrameLayout friendLayoutContainer;

    private ObjectAnimator radarScanAnim;   // 扫描动画

    private ArrayList<Friend> friends ;

    private IScanPresenter scanPresenter;

    private Random random = new Random();

    private boolean isEndScan;

    public RadarPopupWindow(Activity ativity) {

        this.mActivity = ativity;
        friends = new ArrayList<>();

        scanPresenter = new ScanPresenterImpl(this);

        mLayoutInflater = LayoutInflater.from(ativity.getApplicationContext());
        contentView = mLayoutInflater.inflate(R.layout.radar_popup_window,null);
        setContentView(contentView);

        //全屏显示
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        ivWave = (ImageView) contentView.findViewById(R.id.ivWave);
        ivCirle = (ImageView) contentView.findViewById(R.id.ivCirle);
        ivRadarScan = (ImageView) contentView.findViewById(R.id.ivRadarScan);

        friendLayoutContainer = (FrameLayout) contentView.findViewById(R.id.match_container);

        height  = mActivity.getWindow().getDecorView().getHeight();
        width  = mActivity.getWindow().getDecorView().getWidth();
        int diameter= (int) Math.sqrt(Math.pow(height,2)+ Math.pow(width,2));

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(diameter,diameter);
        ivRadarScan.setLayoutParams(layoutParams);
        ivRadarScan.setX((width-diameter)/2);
        ivRadarScan.setY((height-diameter)/2);

        radarScanAnim = ObjectAnimator.ofFloat(ivRadarScan,"rotation",0f,360f);
        radarScanAnim.setDuration(2000);
        radarScanAnim.setInterpolator(new LinearInterpolator());
        radarScanAnim.setRepeatCount(ObjectAnimator.INFINITE);//循环扫描
    }

    /**
     * 开始扫描
     */
    public void startScan(){
        ivRadarScan.setVisibility(View.VISIBLE);
        radarScanAnim.start();

        scanPresenter.startScan();
    }




    @Override
    public void findTheFriend(Friend friend) {

        friends.add(friend);

        View view = createResultItemView(friend);
        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500).start();
        friendLayoutContainer.addView(view);
    }

    @Override
    public void endScan() {

        if(isEndScan){  //防止重复
            return;
        }
        isEndScan = true;
        radarScanAnim.end();
        ivRadarScan.setVisibility(View.GONE);
        scanPresenter.stopScan();

        final int count = friendLayoutContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = friendLayoutContainer.getChildAt(i);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view,"alpha",1f,0f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view,"translationX",
                    view.getX(),width/2-view.getWidth()/2);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view,"translationY",
                    view.getY(),height/2 - view.getHeight()/2);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view,"scaleX",1f,0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(view,"scaleY",1f,0f);
            ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(ivWave,"scaleX",1f,0f);
            ObjectAnimator objectAnimator7 = ObjectAnimator.ofFloat(ivWave,"scaleY",1f,0f);

            AnimatorSet animatorSet=new AnimatorSet();
            animatorSet.setDuration(500);
            animatorSet.playTogether(objectAnimator1,objectAnimator2,objectAnimator3,
                    objectAnimator4,objectAnimator5,objectAnimator6,objectAnimator7);
            animatorSet.start();
        }
        dismiss();
    }


    private View createResultItemView(Friend friend){

        View decorView = mActivity.getWindow().getDecorView();
        int width =  decorView.getWidth();
        int height = decorView.getHeight();

        View view = mLayoutInflater.inflate(R.layout.radar_result_item,friendLayoutContainer,false);
        TextView tvName = (TextView) view.findViewById(R.id.name);
        ImageView avatar = (ImageView) view.findViewById(R.id.avatar);

        avatar.setImageResource(friend.imageResId);
        tvName.setText(friend.name);

        //获取随机坐标位置
        int x  = random.nextInt(width);
        int y  = random.nextInt(height);

        //越界处理，
        final int pad = 100;
        if(x < pad){
            x += pad;
        }
        if(y < pad){
            y +=pad;
        }
        if(x + 2* pad > width){
            x -= 2*pad;
        }
        if(y +2* pad > width){
            y -= 2*pad;
        }
        view.setX(x);
        view.setY(y);
        return view;
    }


    @Override
    public void dismiss() {

        // 设置退出动画
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivCirle,"scaleX",1f,0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivCirle,"scaleY",1f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(contentView,"alpha",1f,0.5f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2,animator3);
        animatorSet.setDuration(500);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                RadarPopupWindow.super.dismiss();

                if(friends.size() > 0){
                    Intent intent = new Intent(mActivity,ScanResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("friends",friends);
                    intent.putExtras(bundle);

                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(0,R.anim.open);

//                    mActivity.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle());
                }
            }
        });
        animatorSet.start();
    }
}
