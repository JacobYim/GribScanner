package com.example.grabscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PartsStartActivity extends AppCompatActivity {
    private Button btn_next;
    private TextView tv_part;
    private ImageView iv_scene;

    private String[] parts = {"Table", "Top-Strong", "Top-Medium", "Middle-Loose", "Middle-Strong", "Middle-Medium", "Bottom-Loose", "Bottom-Strong", "Bottom-Medium", "Top-Loose"};
    private String[] imgs  = {String.valueOf(R.drawable.table), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom)};
    private int length_of_parts = parts.length;
    //    @override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parts_start);

        tv_part = findViewById(R.id.tv_part);
        iv_scene = findViewById(R.id.iv_scene);
        btn_next = findViewById(R.id.btn_next);

        Intent intent = getIntent();
        String str = intent.getStringExtra("index");
        int index = Integer.parseInt(str);

        tv_part.setText(parts[index]);
        iv_scene.setImageResource(Integer.parseInt(imgs[index]));
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PartsStartActivity.this, MeasureTimerActivity.class);
                intent.putExtra("index", Integer.toString(index));
                intent.putExtra("trial", Integer.toString(1));
                startActivity(intent);
            }
        });
    }
}

