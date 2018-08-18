package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchRoom extends AppCompatActivity {

    private RecyclerView listview;
    private RecyclerView.LayoutManager RManager;
    private SearchAdapter listAdapter;
    private ArrayList<Search> arraylist;
    private List<Search> list;          // 데이터를 넣은 리스트변수

    String search_departure, search_arrival;// 찾고자 하는 출발지와 목적지
    String usr;
    String[] map_departure;
    String[] map_arrival;
    int index1;
    int index2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        Intent intent = getIntent();
        usr = intent.getExtras().getString("usr");
        Toast.makeText(getApplicationContext(),usr, Toast.LENGTH_LONG).show();

        listview = (RecyclerView)findViewById(R.id.search_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        listview.setHasFixedSize(true);

        // use a linear layout manager
        RManager = new LinearLayoutManager(this);
        listview.setLayoutManager(RManager);
        list = new ArrayList<>();
        //list = new ArrayList<>();

        listAdapter = new SearchAdapter(list);
        listview.setAdapter(listAdapter);
        //arraylist.addAll(list);

        //-------------------------------------------------------------------//
        map_departure = getResources().getStringArray(R.array.map_departure);
        final String[] departure_data = getResources().getStringArray(R.array.map_departure); // 출발 spinner 목록을 array.xml에서 받아오기
        Spinner departureSpinner = (Spinner)findViewById(R.id.departure);//layout 과 연결
        ArrayAdapter departureAdapter = ArrayAdapter.createFromResource(this, R.array.map_departure, android.R.layout.simple_spinner_item);
        departureAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        departureSpinner.setAdapter(departureAdapter);// 설정 끝;

        departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //출발지로 선택된 역
                index1 = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        map_arrival = getResources().getStringArray(R.array.map_arrival);
        final String[] arrival_data = getResources().getStringArray(R.array.map_arrival);
        Spinner arrivalSpinner = (Spinner)findViewById(R.id.arrival);
        ArrayAdapter arrivalAdapter = ArrayAdapter.createFromResource(this, R.array.map_arrival, android.R.layout.simple_spinner_item);
        arrivalAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        arrivalSpinner.setAdapter(arrivalAdapter);

        arrivalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //도착지로 선택된 역
                index2 = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        //-------------------------------------------------------------------//

        //검색버튼 누르기
        Button search_btn = (Button)findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                search_departure = map_departure[index1];
                search_arrival = map_arrival[index2];
                //map_departure[index1] 에는 출발지 들어가있고, map_arrival[index2]에는 도착지가 들어가 있다.
                //Toast.makeText(getApplicationContext(),search_arrival, Toast.LENGTH_LONG).show();
                //선택하면요 -> SearchAdapter에 들어가서 filter가 됩니다요
                listAdapter.filter(search_departure, search_arrival);
            }
        });
    }
}

