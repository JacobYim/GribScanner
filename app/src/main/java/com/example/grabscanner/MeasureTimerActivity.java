package com.example.grabscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
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
    private int total_trials = 10;
    private int time = 40;
    private boolean timeout = false;

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
        vibrator.vibrate((time+1)*1000);

        // gyroscope example https://mailmail.tistory.com/3

        new CountDownTimer(time*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_time.setText((int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                timeout = true;
                tv_time.setText("done!");
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
                        if (length_of_parts < index){
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
}

