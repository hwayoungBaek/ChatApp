package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class PopupActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_popup);  //화영쓰 - 주석처리함
    }

    public void onbtnOkClicked(){

        //1. 입력 필드 받기
        EditText et =(EditText)findViewById(R.id.etText_cost);
        //String cost = et.getText().toString();

        //2. 2번째 popup화면으로
        Intent intent = new Intent(getApplicationContext(),SearchRoom.class);
        //intent.putExtra("cost",cost);
        startActivity(intent);
    }
}