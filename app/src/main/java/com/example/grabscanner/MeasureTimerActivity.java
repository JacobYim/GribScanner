package com.example.grabscanner;

import static android.os.Environment.getExternalStorageDirectory;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeasureTimerActivity extends AppCompatActivity {
    private Button btn_next;
    private TextView tv_part_num;
    private TextView tv_trials;
    private TextView tv_time;
    private ImageView iv_scene;

    // 책상 - 탑 강 - 탑 중 - 탑 약 - 미들 강 - 미들 중 - 미들 약 - 바텀 강 - 바텀 중 - 마텀 약 - 추가실험
    private String[] parts = {"Table", "Top-Strong", "Top-Medium", "Middle-Loose", "Middle-Strong", "Middle-Medium", "Bottom-Loose", "Bottom-Strong", "Bottom-Medium", "Top-Loose"};
    private String[] imgs  = {String.valueOf(R.drawable.table), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom)};
    private int length_of_parts = parts.length;
    private int total_trials = 5;
    private int time = 20;
    private boolean timeout = false;
    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;
    private SensorEventListener mGyroLis;
    private Sensor mGgyroSensor = null;

    // File
    private String contents = "X,Y,Z,pitch,roll,yaw,dt\n";
    private String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    private String FileName = "";

    private static final int CREATE_FILE = 1;

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/text");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.txt");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

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

        File appDirectory = new File( getExternalFilesDir(null)+"/data" );
        if ( !appDirectory.exists() ) {
            appDirectory.mkdirs();
            Log.e("INFO", "Created ... "+ appDirectory.getAbsolutePath());
        }

        tv_part_num.setText(parts[index]);
        tv_trials.setText(trial_str+" out of "+Integer.toString(total_trials));
        iv_scene.setImageResource(Integer.parseInt(imgs[index]));
        FileName = parts[index]+'_'+trial_str+"_"+timestamp+".csv";

        // vibration start
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // gyroscope start
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
                File file = new File(appDirectory, FileName);
                OutputStream myOutput;
                try {
                    myOutput = new BufferedOutputStream(new FileOutputStream(file,true));
                    myOutput.write(contents.getBytes());
                    myOutput.flush();
                    myOutput.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    Log.i("INFO", "Saved at "+file);
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

                contents = contents + String.format("%.8f", event.values[0])
                        + "," + String.format("%.8f", event.values[1])
                        + "," + String.format("%.8f", event.values[2])
                        + "," +  String.format("%.8f", pitch*RAD2DGR)
                        + "," + String.format("%.8f", roll*RAD2DGR)
                        + "," +  String.format("%.8f", yaw*RAD2DGR)
                        + "," +  String.format("%.8f", dt)+"\n";


//                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.8f", event.values[0])
//                        + "           [Y]:" + String.format("%.8f", event.values[1])
//                        + "           [Z]:" + String.format("%.8f", event.values[2])
//                        + "           [Pitch]: " + String.format("%.8f", pitch*RAD2DGR)
//                        + "           [Roll]: " + String.format("%.8f", roll*RAD2DGR)
//                        + "           [Yaw]: " + String.format("%.8f", yaw*RAD2DGR)
//                        + "           [dt]: " + String.format("%.8f", dt));

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}



