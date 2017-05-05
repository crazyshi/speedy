package com.speedy.radar;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

/**
 * Created by Speedy on 2017/5/4.
 */
public class MainActivity extends AppCompatActivity {

    private RadarPopupWindow mRadarPopupWindow;

    private Button btnScan;
    private Button btnLongPressScan;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity,RadarActivity.class));
            }
        });

        btnLongPressScan = (Button) findViewById(R.id.btnLongPressScan);

        btnLongPressScan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //隐藏导航栏
                WindowUtils.hideStatusBar(mActivity);

                mRadarPopupWindow = new RadarPopupWindow(mActivity);
                mRadarPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //显示导航栏
                        WindowUtils.showStatusBar(mActivity);
                    }
                });
                mRadarPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                mRadarPopupWindow.startScan();
                return true;
            }
        });

        btnLongPressScan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRadarPopupWindow != null && mRadarPopupWindow.isShowing()) {

                        mRadarPopupWindow.endScan();
                    }
                }
                return false;
            }
        });
    }
}
