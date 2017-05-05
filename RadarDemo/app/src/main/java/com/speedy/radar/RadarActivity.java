package com.speedy.radar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Speedy on 2017/5/4.
 */
public class RadarActivity extends AppCompatActivity implements IScanView{

    private static final int SCAN_COUNT = 9;   //扫描次数

    private Activity mActivity;

    private int width;
    private int height;

    private Button btnExit;
    private TextView tvCount;

    private ImageView ivWave;
    private ImageView ivCirle;
    private ImageView ivRadarScan;

    private FrameLayout friendLayoutContainer;

    private ObjectAnimator radarScanAnim;   // 扫描动画

    private ArrayList<Friend> friends  = new ArrayList<>();;

    private IScanPresenter scanPresenter;

    private Random random = new Random();

    private boolean isEndScan;

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);
        mActivity = this;
        scanPresenter = new ScanPresenterImpl(this);
        mCompositeDisposable = new CompositeDisposable();

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCount = (TextView) findViewById(R.id.tvCount);

        ivWave = (ImageView) findViewById(R.id.ivWave);
        ivCirle = (ImageView) findViewById(R.id.ivCirle);
        ivRadarScan = (ImageView) findViewById(R.id.ivRadarScan);

        friendLayoutContainer = (FrameLayout) findViewById(R.id.match_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(height == 0 || width == 0){

            //根据屏幕尺寸设置扫描范围
            height  = getWindow().getDecorView().getHeight();
            width  = getWindow().getDecorView().getWidth();
            int diameter= (int) Math.sqrt(Math.pow(height,2)+ Math.pow(width,2));

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(diameter,diameter);
            ivRadarScan.setLayoutParams(layoutParams);
            ivRadarScan.setX((width-diameter)/2);
            ivRadarScan.setY((height-diameter)/2);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        ivRadarScan.setVisibility(View.GONE);
        radarScanAnim.end();
        scanPresenter.stopScan();
        mCompositeDisposable.clear();
    }


    /**
     * 开始扫描
     */
    public void startScan(){
        //设置扫描动画
        radarScanAnim = ObjectAnimator.ofFloat(ivRadarScan,"rotation",0f,360f);
        radarScanAnim.setDuration(2000);    //2秒扫描一圈
        radarScanAnim.setInterpolator(new LinearInterpolator());
        radarScanAnim.setRepeatCount(ObjectAnimator.INFINITE);//循环扫描

        ivRadarScan.setVisibility(View.VISIBLE);
        radarScanAnim.start();
        startCount();
        scanPresenter.startScan();
    }

    //开始计时
    private void startCount() {
        Disposable disposable = Observable.intervalRange(1L,SCAN_COUNT,0L,1L,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        int number = SCAN_COUNT - aLong.intValue();
                        tvCount.setText(String.valueOf(number));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        endScan();
                    }
                });

        mCompositeDisposable.add(disposable);
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

        ivRadarScan.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
        btnExit.setVisibility(View.GONE);

        mCompositeDisposable.clear();

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
            animatorSet.setDuration(400);
            animatorSet.playTogether(objectAnimator1,objectAnimator2,objectAnimator3,
                    objectAnimator4,objectAnimator5,objectAnimator6,objectAnimator7);
            animatorSet.start();
        }

        // 设置中心圆圈退出动画
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivCirle,"scaleX",1f,0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivCirle,"scaleY",1f,0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1,animator2);
        animatorSet.setDuration(500);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if(friends.size() > 0){
                    Intent intent = new Intent(mActivity,ScanResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("friends",friends);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.open,0);
                    finish();
                }
            }
        });
        animatorSet.start();

    }

    //创建扫描到的好友 View
    private View createResultItemView(Friend friend){
        View decorView = mActivity.getWindow().getDecorView();
        int width =  decorView.getWidth();
        int height = decorView.getHeight();

        View view = getLayoutInflater().inflate(R.layout.radar_result_item,friendLayoutContainer,false);
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



}
