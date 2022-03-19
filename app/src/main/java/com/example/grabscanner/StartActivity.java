package com.example.grabscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    private Button btn_next;
    private TextView tv_name;

    //    @override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        tv_name = findViewById(R.id.tv_name);
        btn_next = findViewById(R.id.btn_next);

        Intent intent = getIntent();
        String str = intent.getStringExtra("name");
        tv_name.setText(str);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, PartsStartActivity.class);
                intent.putExtra("index", Integer.toString(0));
                startActivity(intent);
            }
        });
    }
}

