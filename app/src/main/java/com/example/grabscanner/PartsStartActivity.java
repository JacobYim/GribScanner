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

    private String[] parts = {"1", "2-1", "2-2", "2-3", "3-1", "3-2", "3-3", "4-1", "4-2", "4-3", "Additional"};
    private String[] imgs  = {String.valueOf(R.drawable.table), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.top), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.middle), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom), String.valueOf(R.drawable.bottom), "Additional"};
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

        tv_part.setText("Part "+parts[index]);
        iv_scene.setImageResource(Integer.parseInt(imgs[index]));
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PartsStartActivity.this, MeasureTimerActivity.class);
                intent.putExtra("index", Integer.toString(index));
                intent.putExtra("trial", Integer.toString(0));
                startActivity(intent);
            }
        });
    }
}

