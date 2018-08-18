package com.example.hwayoung.chatapp;

import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class MakeNewRoom extends AppCompatActivity  implements OnMapReadyCallback {
    //출발지, 도착지 각각의 경,위도
    double departure_array[][] = {{37.385544, 37.378293, 37.374524, 37.372279, 37.388221, 37.378304, 37.381828, 37.398329, 37.426790, 37.394130},
            {126.638828, 126.634662, 126.636432, 126.634326, 126.662329, 126.645557, 126.656146, 126.672888, 126.698602, 126.651186}};
    double arrival_array[][] = {{37.385056, 37.378093, 37.374576, 37.372279, 37.387465, 37.378132, 37.381908, 37.398117, 37.426768, 37.394081},
            {126.639382, 126.634412, 126.636340, 126.634177, 126.662739, 126.645013, 126.656979, 126.673299, 126.699196, 126.651269}};

    String[] marker_departure;
    String[] marker_arrival;


    int index2 = 0;
    int index1 = 0;
    int index3 = 0; // 더 내는 금액
    int isdeparture = 0;

    int cash=0; //방장이 더 내는 금액

    String usr; // 사용자
    String u_id; // 사용자 정보 테이블 - 고유번호
    String room_id;
    EditText EtDep;  // 출발지
    EditText EtDes;  // 목적지
    FirebaseDatabase database;

    int t_hour = 0;
    int t_min = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_new_room);

        // 사용자 이름 받아와야됨
        usr = getIntent().getExtras().getString("usr");
        u_id = getIntent().getExtras().getString("uid");
        room_id = " ";

        //Firebase연결
        database = FirebaseDatabase.getInstance();

        //---------------------------출발----------------------------------//
        /* spinner 설정 */
        marker_departure = getResources().getStringArray(R.array.departure_marker);
        final String[] departure_data = getResources().getStringArray(R.array.map_departure); // 출발 spinner 목록을 array.xml에서 받아오기
        Spinner departureSpinner = (Spinner) findViewById(R.id.departure);//layout 과 연결
        ArrayAdapter departureAdapter = ArrayAdapter.createFromResource(this, R.array.map_departure, android.R.layout.simple_spinner_item);
        departureAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        departureSpinner.setAdapter(departureAdapter);// 설정 끝

        /* Spinner 리스너 - 스피너에서 선택된 값을 이용 */
        departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) // position = 위에 spinner 목록의 index 값
            {
                index1 = position; // 전역변수에 값 옮겨주기.
                isdeparture = 0; // 지금은 출발지만 선택되었음을 표시.
                gogoMap(); //값 변화 줬으니까 map update 해주기.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //---------------------------도착----------------------------------//
        /* spinner 설정 */
        marker_arrival = getResources().getStringArray(R.array.arrival_marker);
        final String[] arrival_data = getResources().getStringArray(R.array.map_arrival);
        Spinner arrivalSpinner = (Spinner) findViewById(R.id.arrival);
        ArrayAdapter arrivalAdapter = ArrayAdapter.createFromResource(this, R.array.map_arrival, android.R.layout.simple_spinner_item);
        arrivalAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        arrivalSpinner.setAdapter(arrivalAdapter);

        /* Spinner 리스너 - 스피너에서 선택된 값을 이용 */
        arrivalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index2 = position;// 전역변수에 값 옮겨주기.
                isdeparture = 1; // 지금은 출발지 & 도착지 선택되었음을 표시.
                gogoMap(); //값 변화 줬으니까 map update 해주기.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //**********이어 보기전에 아래 gogoMap()과 onMapReady()를 보시오*********//

        //---------------------------더내는금액----------------------------------//
        /* spinner 설정 */
        //marker_departure = getResources().getStringArray(R.array.departure_marker);
        final String[] cash_data = getResources().getStringArray(R.array.cash); // 금액 spinner 목록을 array.xml에서 받아오기
        Spinner cacheSpinner = (Spinner) findViewById(R.id.cashSpinner);//layout 과 연결
        ArrayAdapter cacheAdapter = ArrayAdapter.createFromResource(this, R.array.cash, android.R.layout.simple_spinner_item);
        cacheAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        cacheSpinner.setAdapter(cacheAdapter);// 설정 끝

        /* Spinner 리스너 - 스피너에서 선택된 값을 이용 */
        cacheSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) // position = 위에 spinner 목록의 index 값
            {
                // 화영쓰 - 수정 아래 한줄
                index3 = position; // 전역변수에 값 저장. 더 내는 금액
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ---------------------------------버튼------------------------------- //
        /*시간 설정 버튼*/
        TextView time_btn = (TextView) findViewById(R.id.tv);
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_TimePicker();
            }
        });


        // 새로운 방 만들기 버튼 클릭했을 시
        /*Create 버튼*/
        Button create_btn = (Button) findViewById(R.id.create_button);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView CheckAll = (TextView) findViewById(R.id.tv);
                if (CheckAll.getText() == "") {
                    (Toast.makeText(getApplicationContext(), "시간을 선택해주세요!", Toast.LENGTH_LONG)).show();
                } else {
                    // *초까지 같은 시간에 채팅방이 생성된다는 가능성은 적으니까,,,,이걸로,,
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    // 내 방 번호 저장하고 있기
                    room_id = formattedDate;

                    int time = t_hour*100+t_min;
                    String s_time =  Integer.toString(time);

                    //(1) Room Table
                    DatabaseReference myRef = database.getReference("Room").child(room_id);
                    Hashtable<String, String> Room = new Hashtable<String, String>();
                    Room.put("departure", departure_data[index1]);
                    Room.put("destination", arrival_data[index2]);
                    Room.put("room_id", room_id);
                    Room.put("time",s_time);
                    Room.put("usr1",u_id);
                    Room.put("usr2"," ");
                    Room.put("usr3"," ");
                    Room.put("usr4"," ");
                    Room.put("num", "1");
                    // 방장이 더 내는 금액 처리  //  화영쓰 원빼기
                    String[] temp = cash_data[index3].split("원");
                    Room.put("cash",temp[0]);
                    //Room.put("cash","1000"); // 우선 보영언니 주려고 1000으로 고정-> 화영쓰 ->이거 지워주세요!!

                    myRef.setValue(Room);

                    //(2) chat Table
                    DatabaseReference myRef2 = database.getReference("chats").child(room_id);
                    Hashtable<String, String> Chat = new Hashtable<String, String>();
                    Chat.put("room_id", room_id);
                    Chat.put("usr", usr);
                    myRef2.setValue(Chat);

                    //(3) user Table
                    FirebaseDatabase.getInstance().getReference("users").child(u_id).child("room_id").setValue(formattedDate);

                    //Intent intent = new Intent(
                     //       getApplicationContext(), // 현재 화면의 제어권자
                      //      ChatRoom.class); // 다음 넘어갈 클래스 지정
                    Intent intent = new Intent(MakeNewRoom.this, ChatRoom.class);

                    //필요한 intent를 다음 activity에 전달
                    intent.putExtra("usr", usr);
                    intent.putExtra("Time_hour", t_hour);
                    intent.putExtra("Time_min", t_min);
                    intent.putExtra("departure", departure_data[index1]);
                    intent.putExtra("destination", arrival_data[index2]);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("cash",cash_data[index3]); // 화영쓰
                    startActivity(intent);
                }
            }
        });
    }
    // --------------------------------- 시간 ------------------------------- //
    private void Dialog_TimePicker() {

        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TextView tv = (TextView)findViewById(R.id.tv);
                tv.setText("");//clear
                tv.clearComposingText();
                int currentHour;
                if(hourOfDay > 12)
                {
                    currentHour = hourOfDay-12;
                    tv.setText(tv.getText() + "오후 "+ String.valueOf(currentHour) + "시" + String.valueOf(minute)+"분");
                    t_hour = currentHour+12; //시
                    t_min = minute; // 분
                }
                else if (hourOfDay == 12)
                {
                    currentHour = hourOfDay;
                    tv.setText(tv.getText() + "오후 "+ String.valueOf(currentHour) + "시" + String.valueOf(minute)+"분");
                    t_hour = currentHour; //시
                    t_min = minute; // 분
                }
                else//오전
                {
                    currentHour = hourOfDay;
                    tv.setText(tv.getText() + "오전 " + String.valueOf(currentHour) + "시" + String.valueOf(minute)+"분");
                    t_hour = currentHour; //시
                    t_min = minute; // 분
                }
                //필요한 변수들 전역 변수에 입히기
            }
        };
        final Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog alert = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog, mTimeSetListener,hour,minute, false);
        alert.show();
    }

    /* google map 업데이트 해주려고 내가 만든 함수*/
    public void gogoMap() //여기서는 layout에 있는 fragment와 연결해주는데 그걸 함으로써 onMapReady를 실행시켜줘
    {
        FragmentManager fragmentManager = getFragmentManager();
        final MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//
    }

    /* 구글맵 갱신 */
    public void onMapReady(final GoogleMap map)
    {
        if(isdeparture==0) // 출발지만 선택되었을때
        {
            map.clear(); // 전에 선택되었던 마커 제거
            LatLng place = new LatLng(departure_array[0][index1], departure_array[1][index1]); // 경도위도 설정

            Marker Mplace = map.addMarker(new MarkerOptions().position(place).title(marker_departure[index1])); // 마커 추가

            map.moveCamera(CameraUpdateFactory.newLatLng(place)); // 출발지를 중심으로 지도 보여주기
            map.animateCamera(CameraUpdateFactory.zoomTo(18)); // 18 줌 인 -> 클수록 확대되어 보이는 효과
        }
        else // 도착지도 선택되었을 경우
        {
            map.clear(); // 전에 선택되었던 마커 제거
            LatLng place1 = new LatLng(departure_array[0][index1], departure_array[1][index1]); // 출발지 - 경도위도 설정
            LatLng place2 = new LatLng(arrival_array[0][index2], arrival_array[1][index2]); // 도착지 - 경도위도 설정

            /* 출발지와 도착지 각각의 마커 */
            Marker Mplace1 = map.addMarker(new MarkerOptions().position(place1).title(marker_departure[index1]));
            Marker Mplace2 = map.addMarker(new MarkerOptions().position(place2).title((marker_arrival[index2])));

            /* 출발지와 도착지 각각의 정보를 한곳에 넣어두기
                 -> 이것을 해야 지도의 view를 마커 기준으로해서 자동으로 설정이 가능   */
            LatLngBounds.Builder builder;
            builder = new LatLngBounds.Builder();
            builder.include(place1); // 설정해줬던 각각의 위치를 추가.
            builder.include(place2);

            LatLngBounds bounds = builder.build(); // 추가된 위치를 계산해서 중심을 잡아준다.

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100)); // 위의 bounds 값을 기준으로 지도 그려주기
            // 100 -> 모서리에서 마커를 얼마나 띄어줄지
        }
    }
}