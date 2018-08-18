package com.example.hwayoung.chatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwayoung.chatapp.model.RoomModel;
import com.example.hwayoung.chatapp.model.UserModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class ChatRoom extends AppCompatActivity implements OnMapReadyCallback {

    //출발지 도착지 각각의 위 경도.
    double departure_array[][] = {{37.385544, 37.378293, 37.374524, 37.372279, 37.388221, 37.378304, 37.381828, 37.398329, 37.426790, 37.394130},
            {126.638828, 126.634662, 126.636432, 126.634326, 126.662329, 126.645557, 126.656146,126.672888, 126.698602,126.651186}};
    double arrival_array[][] = {{37.385056, 37.378093, 37.374576, 37.372279, 37.387465, 37.378132, 37.381908, 37.398117, 37.426768, 37.394081},
            {126.639382, 126.634412,126.636340, 126.634177, 126.662739, 126.645013, 126.656979, 126.673299, 126.699196, 126.651269 }};


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DrawerLayout mDrawerLayout; // 현재 채팅창의 레이아웃
    private ListView mDrawerList; // 서랍 리스트뷰 - mDatas 배열이 들어감
    private View drawerView; // 서랍의 뷰

    //UI
    EditText etText;
    Button btnSend;
    Button btnMenu;
    String email;
    List<Chat> mChat;
    TextView dep;
    TextView des;
    // 지우가 추가한 UI
    Button btnroomout; //화영쓰 - ButtonImage->Button으로 잠시수정함
    ArrayList<String> mDatas = new ArrayList<String>(); //tap menu에 들어갈 닉네임 배열리스트
    Button btncal;

    // 필요한 정보
    int index1;
    int index2;
    String Departure;
    String Arrival;
    String room_id;
    EditText DepAri;
    String usr; // 사용자
    String u_id;
    //화영쓰
    int flag_cal=0;// 요금 계산기 사용했는지 플래그 -> 공유하기버튼 사용가능
    // 지우가 추가한 정보 3개
    final RoomModel roomModel = new RoomModel(); // 방모델
    private String uid; // 현재 계정
    int flag_roomout = 0; // 방나가기 버튼을 눌렀는지 flag
    int min_time; // 만나는시간과 현재시간의 차이

    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        //MainActivity로부터 usr받기
        final Intent intent = getIntent();
        usr = intent.getExtras().getString("usr");
        index1 = intent.getExtras().getInt("index1");
        index2 = intent.getExtras().getInt("index2");
        u_id = intent.getExtras().getString("uid");
        Departure = intent.getExtras().getString("departure");
        Arrival = intent.getExtras().getString("destination");
        room_id = intent.getExtras().getString("room_id");

        //탭창에 출발지 목적지 입력하기 위한 edittext창
        DepAri = (EditText) findViewById(R.id.DepartArrival);
        //drawer 연결 chatroom의 layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        //메뉴버튼 아래 화면
        btnMenu = (Button) findViewById(R.id.btnMenu);
        dep = (TextView) findViewById(R.id.tv_dep);
        des = (TextView) findViewById(R.id.tv_des);
        btnroomout = (Button) findViewById(R.id.room_out); //화영쓰 잠시수정함
        btncal = (Button)findViewById(R.id.calculator);// 화영쓰

        // 화면 상단에 출발지와 목적지를 출력
        dep.setText(Departure); // 출발지
        des.setText(Arrival); // 목적지
        dep.bringToFront();

        //Firebase연결
        database = FirebaseDatabase.getInstance();

        etText = (EditText) findViewById(R.id.etText);
        btnSend = (Button) findViewById(R.id.btnSend);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // 그냥 그 전 값들 다 읽어오기
        readAllMsg();

        //*(2). FireBase에서 대화내용 읽어와서 보여주기 - RoomId에 맞는
        // 새로운 값이 추가될때만
        getListFrom();

        //UI뿌려주기
        mChat = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mChat, usr);
        mRecyclerView.setAdapter(mAdapter);


        /* ------------------------------------------------ 지우가 추가한 부분 서랍장 열기 --------------------------------------------------- */
        // 오른쪽 위 서랍 버튼 눌렀을 때 ////// 지우가 수정, 추가한 부분
        // DB를 접근해서 바꿔줘야하는거랑 함수의 POSITION 사용하는 거 있어서 따로 함수로 빼지 않고 만들었는데
        // 빼야할 것 같으면 어떤 부분 뺴는 게 좋을지 알려줘엉..ㅠ

        final UserModel userModel = new UserModel(); // 사용자모델
//        final RoomModel roomModel = new RoomModel(); // 방모델 86줄에 추가하였다. 지도 불러올 때 ONCREATE 바깥에서도 필요행

        final String[] room_num = new String[1]; // 방 인원
        final int[] int_room_num = new int[1]; // 방 인원 int형 변환된 것

        final String[] warning_sign = new String[1]; // 경고 횟수
        final int[] int_warning_sign = new int[1]; // 경고 횟수 int형 변환된 것

        final String[] final_usr = new String[1]; // 방의 마지막인원의 닉네임

        // users의 DB참조
        final DatabaseReference Usersdb = FirebaseDatabase.getInstance().getReference("users");
        // Room의 DB참조
        final DatabaseReference Roomdb = FirebaseDatabase.getInstance().getReference("Room");
        // chat의 DB참조
        final DatabaseReference Chatdb = FirebaseDatabase.getInstance().getReference("chats");

        // 오른쪽 위 서랍 버튼 눌렀을 때 ////// 지우가 수정, 추가한 부분
        // 서랍 버튼 누르기 => drawer이 열린다
        // 서랍에서 보여야 할 것
        // 1. 지도
        // 2. 출발지 -> 도착지
        // 3. 사용자 정보 // 닉네임 배열
        btnMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서랍이 열려있지 않으면 서랍을 연다.
                if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerView = (View) findViewById(R.id.drawer); // 서랍의 view
                    mDrawerList = (ListView) findViewById(R.id.tap_menu); // 오른쪽 위 서랍 버튼 눌렀을 때 나오는 사용자 정보 listview

                    // 서랍을 연다
                    mDrawerLayout.openDrawer(drawerView);

                    // ------------------------------ 1. 지도를 출력 --------------------------------
                    gogoMap();

                    // 2. 출발지->도착지와
                    // 3. 사용자 정보 // 닉네임 배열을 출력하기 위해선
                    // Roomdb에 접근하여 정보를 가져와야 한다. 필요한 모든 정보를 roomModel에 넣는다.
                    Roomdb.child(room_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            roomModel.departure = (String) dataSnapshot.child("departure").getValue(); // 출발지
                            roomModel.destination = (String) dataSnapshot.child("destination").getValue(); // 목적지
                            roomModel.num = (String) dataSnapshot.child("num").getValue(); // 방인원
                            roomModel.room_id = (String) dataSnapshot.child("room_id").getValue(); // 방 고유 아이디
                            roomModel.time = (String) dataSnapshot.child("time").getValue(); // 만나는 시간
                            roomModel.usr1= (String) dataSnapshot.child("usr1").getValue(); // usr1의 uid
                            roomModel.usr2= (String) dataSnapshot.child("usr2").getValue(); // usr2의 uid
                            roomModel.usr3= (String) dataSnapshot.child("usr3").getValue(); // usr3의 uid
                            roomModel.usr4= (String) dataSnapshot.child("usr4").getValue(); // usr4의 uid
                            roomModel.cash = (String) dataSnapshot.child("cash").getValue(); // 방장이 더 내는 금액 - 화영쓰

                            // 방나가기를 할 때 필요한 정보 ( 마지막 인원의 uid ) 를 다시 디비참조 하지않게 미리 접근해 놓는다.
                            room_num[0] = roomModel.num;
                            int_room_num[0] = Integer.parseInt(room_num[0].toString());

                            // 방의 마지막인원의 uid
                            final_usr[0] = "usr" + room_num[0];

                            // 방의 마지막인원 uid를 roomModel에 넣어놓는다.
                            roomModel.final_uid = (String) dataSnapshot.child(final_usr[0]).getValue();

                            // --------------------------------------------- 2.  출발지->목적지를 출력 -------------------------------------------
                            DepAri.setText(roomModel.departure + "->" + roomModel.destination);

                            // ------------------------------------------- 3. 방 참가자 닉네임 출력 --------------------------------------------
                            // 3. 사용자 정보 - 닉네임 배열 출력
                            // 탭을 열었을 때 사용자 닉네임을 넣어 실시간으로 사용자 목록을 받아온다.
                            // Roomdb에는 사용자 닉네임이 아닌 uid를 넣어놓았는데
                            // 후에 프로필 정보를 받아오기 수월하도록 uid를 넣어놓았다.
                            // Roomdb에서 방 참가자들의 uid를 가져와 그것의 닉네임을 mDatas에 넣어준다.

                            final String usr_init = " "; // usr가 유무를 판단하기 위한 변수

                            Usersdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // 현재 사용자 계정의 uid를 받아온다.
                                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    // 사용자의 경고 횟수를 받아와 나중에도 사용하기 위해 userModel에 넣는다.
                                    userModel.warning_sign = (String) dataSnapshot.child(uid).child("warning_sign").getValue();

                                    // 배열에 값 추가할 때 num받아서 해도 될 것 같은뎅 어짜피 4명이고 계산하는 데 크게 안 다를 것 같아성
                                    // 우선 하드코딩으로...

                                    // 무조건 usr1에는 방장의 uid가 들어있기 때문에
                                    // usr1에 값이 있으니 배열에 넣는다.
                                    // 배열의 맨위에는 항상 방장의 닉네임이 들어가게 된다.
                                    String usr1_name = (String)dataSnapshot.child(roomModel.usr1).child("userName").getValue();

                                    // 탭을 열때마다 배열값을 새로 생성해야 하기 때문에 초기화해준다.
                                    mDatas.clear();
                                    mDatas.add(usr1_name);

                                    // 사용자가 들어왔는지 순차적으로 확인하여 배열에 방 참가자의 닉네임을 넣어준다
                                    // 이 부분은 이 때에만 사용하는 부분이라 userModel을 여럿 활용하여 만들지 않고 바로 값을 받아와 넣어주었다.
                                    // 방 참가자들은 roomdb에 usr1, usr2, usr3, usr4 순차적으로 들어있기 떄문에
                                    // usr2이 있을 때에만 usr3 확인, usr3이 있을 때에만 usr4 확인하여 배열에 넣어준다.
                                    if (!roomModel.usr2.equals(usr_init)) {
                                        String usr2_name = (String)dataSnapshot.child(roomModel.usr2).child("userName").getValue();
                                        mDatas.add(usr2_name);
                                        if (!roomModel.usr3.equals(usr_init)) {
                                            String usr3_name = (String)dataSnapshot.child(roomModel.usr3).child("userName").getValue();
                                            mDatas.add(usr3_name);
                                            if (!roomModel.usr4.equals(usr_init)) {
                                                String usr4_name = (String)dataSnapshot.child(roomModel.usr4).child("userName").getValue();
                                                mDatas.add(usr4_name);
                                            }
                                        }
                                    }
                                    ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter(mDrawerLayout.getContext(), android.R.layout.simple_list_item_1, mDatas);
                                    // 배열의 어댑터로 리스트뷰에 방 참가자 닉네임 배열을 출력한다
                                    mDrawerList.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });

                    // ---------------------------------- 프로필 액티비티 출력 -------------------------------------
                    // 방 참가자 닉네임 배열의 item을 눌렀을 때 -> 프로필 activity를 연다.
                    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            // 현재 사용자 정보 배열의 item에 있는 position으로 uid를 찾고 그 uid를 프로필 액티비티에 넘겨준다.

                            String _uid = " ";

                            String _boss_uid = roomModel.usr1;

                            // 배열 앞부터 순차적으로 usr1, usr2, usr3, usr4의 uid가 들어있다.
                            // ex, item의 position이 0이면 usr1의 정보를 넘겨준다.
                            if (position == 0) { String usr1 = roomModel.usr1; _uid = usr1; }
                            if (position == 1) { String usr2 = roomModel.usr2; _uid = usr2; }
                            if (position == 2) { String usr3 = roomModel.usr3; _uid = usr3; }
                            if (position == 3) { String usr4 = roomModel.usr4; _uid = usr4; }

                            Intent intent = new Intent(ChatRoom.this, UserInfoActivity.class);
                            // 현재 계정의 uid (uid) 와 방장의 uid (boss_uid) 를 프로필 액티비티에 넘겨준다.
                            intent.putExtra("uid", _uid);
                            intent.putExtra("boss_uid",_boss_uid);
                            startActivity(intent);
                        }
                    });


                    // 내가 짠 거 너무 하드코딩이라.. 우선 추가하려면
                    // 이곳에 카카오택시 연동버튼이랑 요금분배버튼을 넣는게 좋을 것 같우 ㅎ....

                    //오키도키요 -화영쓰
                    // ------------------------------------------- 분배 결제 버튼 ----------------------------------------------------
                    btncal.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("TAG", "버튼 누르면 더내는 금액이?! "+roomModel.cash);
                            showpop();
                            //Toast.makeText(getApplicationContext(), " 요금 :" +roomModel.fee+"/방장 :"+roomModel.fee_l, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // tap menu를 눌렀을 때에 방나가기 버튼이 활성화 된다!_!

                    // 방 나가기버튼 클릭 시

                    // 방장이 나갔을 경우, 1. 30분 이내일 경우 경고를 2회 부여한다. 2. 현재 Room의 모든 참가자들의 usersDB의 room_id를 초기화한다.
                    // 3. roomDB를 삭제한다. 4. chatDB를 삭제한다.

                    // 방참가자가 나갔을 경우, 1. 30분 이내일 경우 경고를 1회 부여한다. 2. 현재 계정의 usersDB의 room_id를 초기화한다.
                    // 3. RoomDB의 현재 계정 usr를 삭제한다. 4. RoomDB의 num (방 인원)을 하나 감소시켜 업데이트한다.

                    // 이전 화면으로 돌아간다 - (이 부분은 이전 화면이 아니라 방 리스트 화면이었으면 좋겠는데
                    // 방 만들기를 하고 바로 나갔을 경우 방 만들기 화면이 나와버려서 flag로 기억했다가 finish를 한번 더 해주는게 가장 효율적인걸까..?..

                    // ------------------------------------------- 방 나가기 버튼 ----------------------------------------------------
                    btnroomout.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            warning_sign[0] = userModel.warning_sign; // 사용자의 경고횟수
                            int_warning_sign[0] = Integer.parseInt(warning_sign[0].toString()); // 사용자의 경고횟수를 int형으로 변환한 것

                            // 현재 계정의 uid가 Room DB의 usr1과 같으면 방장, 그렇지 않으면 방 참가자이다.
                            // ------------------------------------------------------- 방장 -----------------------------------------------------
                            if (uid.equals(roomModel.usr1)) {

                                // 1. 30분 이내일 경우 경고를 2회 부여한다.
                                if(calculate_30min()) {
                                    int_warning_sign[0] = int_warning_sign[0] + 2;
                                    Toast.makeText(getApplicationContext(), "방장이" + min_time + "분 시간 차로 방을 나갔으므로 경고를 2회 부여합니다", Toast.LENGTH_LONG).show();
                                }
                                else Toast.makeText(getApplicationContext(), "방장이"+min_time+"분 시간 차로 방을 나갔으므로 경고를 부여하지 않습니다", Toast.LENGTH_LONG).show();
                                // 2. 현재 Room의 모든 참가자들의 usersDB의 room_id를 초기화한다.
                                if (int_room_num[0] > 3) Usersdb.child(roomModel.usr4).child("room_id").setValue(" ");
                                if (int_room_num[0] > 2) Usersdb.child(roomModel.usr3).child("room_id").setValue(" ");
                                if (int_room_num[0] > 1) Usersdb.child(roomModel.usr2).child("room_id").setValue(" ");

                                // 3. roomDB를 삭제한다.
                                // 4. chatDB를 삭제한다.

                                // 왜 room 전체가 다 없어질까...?
                                 Roomdb.child(room_id).setValue(null);
                                 Chatdb.child(room_id).setValue(null);
                            }

                            // ---------------------------------------------- 방 참가자 -------------------------------------------------
                            else {

                                // 1. 30분 이내일 경우 경고를 1회 부여한다.
                                if(calculate_30min()) {
                                    int_warning_sign[0] = int_warning_sign[0] + 1;
                                    Toast.makeText(getApplicationContext(), "방참가자가" + min_time + "분 시간 차로 방을 나갔으므로 경고를 1회 부여합니다", Toast.LENGTH_LONG).show();
                                }
                               else Toast.makeText(getApplicationContext(), "방참가자가"+min_time+"분 시간 차로 방을 나갔으므로 경고를 부여하지 않습니다", Toast.LENGTH_LONG).show();
                                // 2. 현재 계정의 usersDB의 room_id를 초기화한다. = Roomdb의 usr정보를 재정렬한다.

                                // 4명이 있는 방이었다. 1. usr3이 나가면 usr4가 usr3자리에 간다. 2. usr2가 나가면 usr4가 usr2자리에 간다.

                                // db 빈자리를 순차적으로 땡겨서 매꾸기가 새롭게 함수 짜야할 것 같아서 그냥 이렇게 만들었당..
                                // swap하는 형식으로.. 4명이라 가능한 하드코딩이당...
                                if (room_num[0].equals("4")) {
                                    if (uid.equals(roomModel.usr3)) { Roomdb.child(room_id).child("usr3").setValue(roomModel.final_uid); }
                                    else if (uid.equals(roomModel.usr2)) { Roomdb.child(room_id).child("usr2").setValue(roomModel.final_uid); }
                                    }
                                    // 3명이 있는 방이었다. usr2가 나가면 usr3이 usr2자리에 간다.
                                else if (room_num[0].equals("3")) {
                                    if (uid.equals(roomModel.usr2)) { Roomdb.child(room_id).child("usr2").setValue(roomModel.final_uid); }
                                    }

                                    // 마지막 자리는 지운다.
                                Roomdb.child(room_id).child(final_usr[0]).setValue(" ");

                                // 한명이 나가고 난 현재 방 인원을 업데이트 한다
                                int_room_num[0] = Integer.parseInt(room_num[0].toString()) - 1;
                                room_num[0] = Integer.toString(int_room_num[0]);
                                Roomdb.child(room_id).child("num").setValue(room_num[0]);
                            }

                            // 방장, 방 참가자 공통 해당 부분
                            // 현재 사용자의 사용자 db의 room_id를 삭제한다.
                            Usersdb.child(uid).child("room_id").setValue(" ");
                            String warning_sign_update = Integer.toString(int_warning_sign[0]);
                            // 현재 사용자의 사용자 db에 할당된 경고를 업데이트한다.
                            Usersdb.child(uid).child("warning_sign").setValue(warning_sign_update);

                            // 이전화면으로 돌아간다. 방 나가기 버튼을 눌렀을 때 tap menu를 나간다.
                            flag_roomout = 1;
                            finish();
                        }
                    });
                    // 이전화면으로 돌아간다. 방나가기 버튼을 눌렀을 때 채팅방을 나간다
                    if (flag_roomout == 1) {
                        flag_roomout = 0;
                        finish();
                    }
                }
            }
        });
    }

    /* 여기까지가 지우가 추가한 부분 */

    /*
    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if((KeyCode==KeyEvent.KEYCODE_VOLUME_DOWN)){
            Toast.makeText(ChatRoom.this, "외부 버튼 눌림", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),PopupActivity.class);
            startActivity(intent);

        }return true;
    }
    */

    // 현재 시간과 타겟 시간 차가 얼마나 나는지 확인
    // 30분 이상 차이면 false
    // 30분 미만 차이면 true
    private boolean calculate_30min()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");

        String curtime = df.format(c.getTime());
        int int_curtime = Integer.parseInt(curtime.toString());

        String targettime = roomModel.time;
        int int_targettime = Integer.parseInt(targettime.toString());

        int cur_hour = int_curtime/100;
        int cur_min = int_curtime%100;

        int tar_hour = int_targettime/100;
        int tar_min = int_targettime%100;

//        Log.e("TAG", "tar_time:"+tar_hour+ "시"+tar_min+"분" );
//        Log.e("TAG", "cur_time:"+cur_hour+ "시"+cur_min+"분" );

        min_time = (tar_hour*60+tar_min)-(cur_hour*60+cur_min);

//        Log.e("TAG", "차이"+min_time );

        // 30분 미만일 경우
        if(min_time < 30)
        {
            return true;
        }
        return false;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();
        if ( key== KeyEvent.KEYCODE_BACK) { // 백 버튼
            finish();
        }
        if((key==KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Toast.makeText(ChatRoom.this, "외부 버튼 눌림", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),PopupActivity.class);
            startActivity(intent);

        }return true;
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            //팝업 메뉴
            popMessage(position);

        }
    };

    //private
    private void popMessage(int i)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필");
        builder.setMessage(String.format("E-mail : %s\n학과 : \n학번 : \n",usr));
        builder.setIcon(android.R.drawable.ic_dialog_info);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //화영쓰 - 분배결제 pop 띄워주기위해
    private void showpop(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_popup,null); // activity_popup.xml 을 커스텀 다이얼로그로 해주기위해
        builder.setView(view);

        // UI
        final Button btnOK = (Button)view.findViewById(R.id.btnOK);
        final EditText fee = (EditText)view.findViewById(R.id.etText_cost);
        final Button btnCan = (Button)view.findViewById(R.id.btnCancel);
        final Button btnShare = (Button)view.findViewById(R.id.btnShare);

        final AlertDialog dialog = builder.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fee_s = fee.getText().toString(); // editText에서 값 읽어옴
                if(fee_s.replace(" ","").equals("")){ // 공백
                    Toast.makeText(getApplicationContext(), "요금을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    int fee_i = Integer.parseInt(fee_s);
                    // 채팅장 옆 메뉴 눌리면서 서버 DB에서 정보 가져와서 roomModel에 있으니까 가져와서 쓰면 돼
                    int cash_leader = Integer.parseInt(roomModel.cash); // 방장이 더 내는 금액
                    int total_fee = (fee_i -cash_leader)/Integer.parseInt(roomModel.num); // 총 내는 금액
                    int total_fee_l = total_fee + cash_leader; //총방장이 내는 금액
                    // 택시요금 계산
                    roomModel.fee = Integer.toString(total_fee);
                    roomModel.fee_l = Integer.toString(total_fee_l);
                    flag_cal=1; // 택시요금 계산됐으니까 메시지 보내기
                    Toast.makeText(getApplicationContext(), "계산완료! 요금 : " +roomModel.fee + "원 / 방장이 내는 금액 : "+roomModel.fee_l+"원", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 계산하고 채팅방에 자동으로 보내주기!  //
                if(flag_cal==0){
                    Toast.makeText(getApplicationContext(), "택시요금을 기입하고 확인버튼을 눌러주세요." , Toast.LENGTH_SHORT).show();
                }else{
                    // 데이터 firebase 업로드
                    // 채팅 주고 받는 시간
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    DatabaseReference myRef = database.getReference("chats").child(room_id).child("comment").child(formattedDate);
                    Hashtable<String, String> chat = new Hashtable<String, String>();
                    chat.put("usr", usr);
                    chat.put("text", "요금 : "+roomModel.fee + "원 / 방장이 내는 금액 : "+roomModel.fee_l+"원");
                    myRef.setValue(chat);

                    flag_cal =0; // 최초 한번만 보내주기
                    dialog.dismiss();
                }
            }
        });

        btnCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Dialog 사이즈 조절 시도
        //Dialog dialog2 = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 600;
        lp.height = 800;
        dialog.show();

        //builder.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);

    }

    void getListFrom() {

        //메시지 읽어오기
        DatabaseReference myRef2 = database.getReference("chats").child(room_id).child("comment");
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Chat chat = dataSnapshot.getValue(Chat.class);
                mChat.add(chat);
                mRecyclerView.scrollToPosition(mChat.size()-1);
                mAdapter.notifyItemInserted(mChat.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    public void onBtnSendClicked(View v) {

        String stText = etText.getText().toString();

        if (stText.equals("") || stText.isEmpty()) {
            Toast.makeText(ChatRoom.this, "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
        } else {

            // 데이터 firebase 업로드

            // 채팅 주고 받는 시간
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            DatabaseReference myRef = database.getReference("chats").child(room_id).child("comment").child(formattedDate);
            Hashtable<String, String> chat = new Hashtable<String, String>();
            chat.put("usr", usr);
            chat.put("text", stText);
            myRef.setValue(chat);

            etText.setText("");
        }
    }
    /* google map 업데이트 해주려고 내가 만든 함수*/
    public void gogoMap() //여기서는 layout에 있는 fragment와 연결해주는데 그걸 함으로써 onMapReady를 실행시켜줘
    {
        FragmentManager fragmentManager = getFragmentManager();
        final MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//
    }

    public void onMapReady(final GoogleMap map)
    {
        map.clear();
        LatLng place1 = new LatLng(departure_array[0][index1], departure_array[1][index1]); // 출발지 - 경도위도 설정
        LatLng place2 = new LatLng(arrival_array[0][index2], arrival_array[1][index2]); // 도착지 - 경도위도 설정

        /* 출발지와 도착지 각각의 마커 */
        Marker Mplace1 = map.addMarker(new MarkerOptions().position(place1).title(Departure));
        Marker Mplace2 = map.addMarker(new MarkerOptions().position(place2).title(Arrival));

        /* 출발지와 도착지 각각의 정보를 한곳에 넣어두기
                -> 이것을 해야 지도의 view를 마커 기준으로해서 자동으로 설정이 가능   */
        LatLngBounds.Builder builder;
        builder = new LatLngBounds.Builder();
        builder.include(place1); // 설정해줬던 각각의 위치를 추가.
        builder.include(place2);

        LatLngBounds bounds = builder.build(); // 추가된 위치를 계산해서 중심을 잡아준다.
        map.animateCamera(CameraUpdateFactory.zoomTo(18)); // 18 줌 인 -> 클수록 확대되어 보이는 효과
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100)); // 위의 bounds 값을 기준으로 지도 그려주기

    }

    void readAllMsg(){
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Toast.makeText(getApplicationContext(), (String)dataSnapshot.getValue() , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}
