package com.speedy.radar;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static android.R.attr.x;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


/**
 * Created by Speedy on 2017/5/4.
 */

public class ScanPresenterImpl implements IScanPresenter {

    private IScanView scanView;

    private CompositeDisposable mCompositeDisposable;



    public ScanPresenterImpl(IScanView scanView) {
        this.scanView = scanView;
        mCompositeDisposable = new CompositeDisposable();
    }


    @Override
    public void startScan() {

        /*
         * 模拟网络请求，获取同时长按的好友
         *
         * 每一秒执行一次扫描，扫描时长6秒钟
         */
        Disposable disposable = Observable.intervalRange(1L,8L,0L,1L,TimeUnit.SECONDS)
                .map(new Function<Long, Friend>() {
                    @Override
                    public Friend apply(@NonNull Long aLong) throws Exception {

                        int index =(int) aLong.longValue() ;

                        switch (index){
                            case 1:
                                return new Friend("马云",R.mipmap.friend1);
                            case 2:
                                return new Friend("比尔盖茨",R.mipmap.friend2);
                            case 3:
                                return new Friend("巴菲特",R.mipmap.friend3);
                            case 4:
                                return new Friend("雷军",R.mipmap.friend4);
                            case 5:
                                return new Friend("奥巴马",R.mipmap.friend5);
                            case 6:
                                return new Friend("刘亦菲",R.mipmap.friend6);
                            case 7:
                                return new Friend("刘德华",R.mipmap.friend7);
                            case 8:
                                return new Friend("张学友",R.mipmap.friend8);
                            case 9:
                                return new Friend("凤姐",R.mipmap.friend9);
                            default:
                                return new Friend("犀利哥",R.mipmap.friend10);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Friend>() {
                    @Override
                    public void accept(@NonNull Friend friend) throws Exception {
                        scanView.findTheFriend(friend);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e("ScanPresenterImpl", "startScan: ",throwable );
                    }
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void stopScan() {
        mCompositeDisposable.clear();
    }
}
