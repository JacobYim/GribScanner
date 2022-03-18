package com.example.grabscanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText edit_name;
    private Button btn_next;
    private String str;

//    @override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        edit_name = findViewById(R.id.edit_name);
        btn_next = findViewById(R.id.btn_next);
        
        btn_next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                str = edit_name.getText().toString();
                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                intent.putExtra("name", str);
                startActivity(intent);
            }
        });
    }
}
