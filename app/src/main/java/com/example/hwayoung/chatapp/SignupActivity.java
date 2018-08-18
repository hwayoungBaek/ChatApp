package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hwayoung.chatapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    private EditText email;
    private EditText num;
    private EditText name;
    private EditText password;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // 회원가입에 필요한 email, num, name, password와 signup 버튼을 불러온다.
        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        num = (EditText)findViewById(R.id. signupActivity_edittext_num);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);

        // signup button을 누르면 작성된 이메일, 비밀번호로 회원가입이 된다.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // text가 비워져 있는 란이 있다면 return한다. => 회원가입이 되지 않는다.
                // password가 6자리 미만일 때에는 에러가 난다.
                if (email.getText().toString() == null || num.getText().toString() == null || name.getText().toString() == null
                        || password.getText().toString() == null){
                    return; }

                    // 이메일과 패스워드에 대해 firebase에 사용자 정보를 등록한다.
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                            // 회원가입이 완료되면 => firebase에 권한이 올라가고 나서 complete으로 넘어온다
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // 사용자마다 고유의 uid값이 나오기 때문에 중복될 일이 없다.
                                final String uid = task.getResult().getUser().getUid();

                                        // 새로운 유저모델을 만들어 사용자의 uid으로 ui를 저장한다.
                                        UserModel userModel = new UserModel();
                                        userModel.userName = name.getText().toString(); // 닉네임
                                        userModel.num = num.getText().toString(); // 학번
                                        userModel.room_id = " "; // 방 고유번호 초기화
                                        userModel.warning_sign = "0"; // 경고 횟수 초기화
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // firebaseauth에서 자체적으로 받아오는 고유 uid

                                        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                                        // 사용자 모델에 담긴 값들을 DB의 users의 사용자의 uid 하위 값으로 등록한다.
                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {

                                            // 회원가입에 성공하면 창을 지운다.
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                                                // 회원가입 창을 종료한다
                                                SignupActivity.this.finish();
                                            }
                                        });

                                    }
                                });

                            }
                        });
            }
    }