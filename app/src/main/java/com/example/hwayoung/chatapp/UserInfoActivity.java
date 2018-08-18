package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        final TextView num = (TextView) findViewById(R.id.num); // 학번
        final TextView name = (TextView) findViewById(R.id.name); // 닉네임
        final TextView warning = (TextView) findViewById(R.id.warning); // 경고횟수
        final Button button_warning = (Button)findViewById(R.id.button_warning); // 경고주기 버튼

        Intent intent = getIntent();

        // item uid
        final String uid = intent.getExtras().getString("uid");
        // 방장 uid
        final String boss_uid = intent.getExtras().getString("boss_uid");
        //  Log.e("TAG22222222222222222", uid);

        // 계정 uid
        final String auth_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("users").child(uid);

        usersDB.child("num").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                num.setText( (String)dataSnapshot.getValue()); }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

       usersDB.child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText( (String)dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        usersDB.child("warning_sign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String warning_sign = (String) dataSnapshot.getValue();
                warning.setText(warning_sign);

                // 계정이 방장일 경우 && 방장의 프로필이 아닌 경우, 이 경우에 경고 버튼을 enable 시킨다
                if (auth_uid.equals(boss_uid)) {
                    if (!auth_uid.equals(uid)) {
                        // 버튼 활성화
                        button_warning.setEnabled(true);

                        // 경고 주기 버튼 눌렀을 때
                        button_warning.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int warning_sign_num = Integer.parseInt(warning_sign.toString());

                                // 경고 1회 추가
                                warning_sign_num = warning_sign_num + 1;
                                String warning_sign = Integer.toString(warning_sign_num);
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("warning_sign").setValue(warning_sign);
                                Toast.makeText(getApplicationContext(), "1회 경고주기 성공", Toast.LENGTH_LONG).show();

                                // 버튼 비활성화
                                button_warning.setEnabled(false);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}