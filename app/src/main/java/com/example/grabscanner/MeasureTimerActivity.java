package com.example.grabscanner;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MeasureTimerActivity extends AppCompatActivity {
    private Button btn_next;
    private TextView tv_part_num;
    private TextView tv_trials;
    private TextView tv_time;
    private ImageView iv_scene;

    // 책상 - 탑 강 - 탑 중 - 탑 약 - 미들 강 - 미들 중 - 미들 약 - 바텀 강 - 바텀 중 - 마텀 약 - 추가실험
    private String[] parts = {"1", "2-1", "2-2", "2-3", "3-1", "3-2", "3-3", "4-1", "4-2", "4-3"};
    private String[] imgs  = {String.valueOf(R.drawable.table), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom)};
    private int length_of_parts = parts.length;
    private int total_trials = 5;
    private int time = 20;
    private boolean timeout = false;
    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;
    private SensorEventListener mGyroLis;
    private Sensor mGgyroSensor = null;

    //    @override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure_timer);

        tv_part_num = findViewById(R.id.tv_part_num);
        tv_trials = findViewById(R.id.tv_trials);
        tv_time = findViewById(R.id.tv_time);

        iv_scene = findViewById(R.id.iv_scene);
        btn_next = findViewById(R.id.btn_next);

        Intent intent = getIntent();
        String index_str = intent.getStringExtra("index");
        int index = Integer.parseInt(index_str);
        String trial_str = intent.getStringExtra("trial");
        int trial = Integer.parseInt(trial_str);

        tv_part_num.setText("Part "+parts[index]);
        tv_trials.setText(trial_str+" out of "+Integer.toString(total_trials));
        iv_scene.setImageResource(Integer.parseInt(imgs[index]));

        // vibration start
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(4*(time+1)*1000);

        // gyroscope example https://mailmail.tistory.com/3

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroLis = new GyroscopeListener();

        mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);
        new CountDownTimer((time+1)*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_time.setText(String.valueOf((int)(millisUntilFinished / 1000)));
                if ((int)(millisUntilFinished / 1000)%10 == 0 && (int)(millisUntilFinished / 1000)!=0){
                    vibrator.vibrate(1000*10);
                }
            }
            public void onFinish() {
                timeout = true;
                tv_time.setText("done!");
                mSensorManager.unregisterListener(mGyroLis);
            }
        }.start();

        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (timeout){
                    Intent intent;
                    if (trial < total_trials) {
                        intent = new Intent(MeasureTimerActivity.this, MeasureTimerActivity.class);
                        intent.putExtra("index", Integer.toString(index));
                        intent.putExtra("trial", Integer.toString(trial+1));
                    }else{
                        if (index+1 < length_of_parts){
                            intent = new Intent(MeasureTimerActivity.this, PartsStartActivity.class);
                            intent.putExtra("index", Integer.toString(index + 1));
                        }else{
                            intent = new Intent(MeasureTimerActivity.this, EndActivity.class);
                        }
                    }
                    startActivity(intent);
                }
            }
        });
    }

    private class GyroscopeListener implements SensorEventListener {

        //Roll and Pitch
        private double pitch;
        private double roll;
        private double yaw;

        //timestamp and dt
        private double timestamp;
        private double dt;

        // for radian -> dgree
        private double RAD2DGR = 180 / Math.PI;
        private static final float NS2S = 1.0f/1000000000.0f;

        @Override
        public void onSensorChanged(SensorEvent event) {

            /* 각 축의 각속도 성분을 받는다. */
            double gyroX = event.values[0];
            double gyroY = event.values[1];
            double gyroZ = event.values[2];

            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
             * dt : 센서가 현재 상태를 감지하는 시간 간격
             * NS2S : nano second -> second */
            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
            if (dt - timestamp*NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                pitch = pitch + gyroY*dt;
                roll = roll + gyroX*dt;
                yaw = yaw + gyroZ*dt;

                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                        + "           [Y]:" + String.format("%.4f", event.values[1])
                        + "           [Z]:" + String.format("%.4f", event.values[2])
                        + "           [Pitch]: " + String.format("%.1f", pitch*RAD2DGR)
                        + "           [Roll]: " + String.format("%.1f", roll*RAD2DGR)
                        + "           [Yaw]: " + String.format("%.1f", yaw*RAD2DGR)
                        + "           [dt]: " + String.format("%.4f", dt));

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}



