package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hwayoung.chatapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Hashtable;


public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase database;

    private EditText id;
    private EditText password;

    private Button login;
    private Button signup;

    private String uid;

    private String usrname;
    private String warning_sign;
    private int int_warning_sign;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // auth를 받아온다. <- login 위함
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signOut();

        // login과 password에 해당하는 text를 불러온다.
        id = (EditText) findViewById(R.id.loginActivity_edittext_id);
        password = (EditText) findViewById(R.id.loginActivity_edittext_password);


        // login과 signup에 해당하는 버튼을 불러온다.
        login = (Button) findViewById(R.id.loginActivity_button_login);
        signup = (Button) findViewById(R.id.loginActivity_button_signup);

//        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // login button을 click했을 때 loginEvent method가 호출된다.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent();
            }
        });

        //signup button을 click했을 때 signupActivity가 호출된다.
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        // 로그인 인터페이스 리스너
        // 리스너를 액티비티에 붙여주어야 로그인을 확인할 수 있다.
        // onstart()와 onstop()을 이용한다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // 로그인이 되었을 때
                // uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (user != null) {
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //  Log.e("TAG", "onAuthStateChanged:signed_in:" + user.getUid());

                    //getRoomId();

                    //메시지 읽어오기
                    FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //for(){
                            //  Log.e("TGGGGG", "Sing : " + dataSnapshot.getValue());
                            warning_sign = (String) dataSnapshot.child("warning_sign").getValue();
                            int_warning_sign = Integer.parseInt(warning_sign.toString());

                            // 경고 3회 이상이면 로그아웃
                            if(int_warning_sign >= 3)
                            {
                                // 경고 없애기 (6개월 조건 x)
                                FirebaseDatabase.getInstance().getReference("users").child(uid).child("warning_sign").setValue("0");
                                Toast.makeText(getApplicationContext(), "경고가 3회이상 누적되어 로그인 할 수 없습니다", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                            // 경고 3회 미만이면 로그인
                            else {
                                usrname = (String) dataSnapshot.child("userName").getValue();


                                //로그인
                                //로그인이 되었을 때 새로운 액티비티가 실행된다.
                                Intent intent = new Intent(LoginActivity.this, ListChatRoom.class);
                                //  Log.e("TAG1111111111111111111", usrname);
                                intent.putExtra("usr", usrname);
                                intent.putExtra("uid", uid);
                                //  Log.e("TAG22222222222222222", usrname);
                                startActivity(intent);
                                // 자신을 받아주면서 MainAcitivity를 연다.
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    //   Log.e("TAG", "onAuthStateChanged:signed_out");
                    //로그아웃
                }
            }
        };
    }

    // 이 부분은 로그인이 정상적으로 됐는지 안됐는지 확인해주는 부분이다.
    // 로그인이 되어서 다음 화면으로 넘어가는 것은 authstatelistener로 진행한다.

    //이 이벤트는 로그인 버튼에 달아준다.
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인이 실패했을 때 작동하는 부분
                        if (!task.isSuccessful()) {
                            // 어떤 에러로 로그인에 실패했는지 띄워주는 부분
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // listner부착
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

}