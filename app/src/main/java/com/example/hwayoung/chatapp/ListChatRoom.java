package com.example.hwayoung.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwayoung.chatapp.model.RoomModel;
import com.example.hwayoung.chatapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ListChatRoom extends AppCompatActivity {

    TextView TxNotifi;
    Button btnSearch;
    Button btnMyroom;
    Button btnMakeNew;

    List<ChatListItem> ChatList;

    private RecyclerView chatListView;
    private RecyclerView.Adapter chatListAdapter;
    private RecyclerView.LayoutManager chatListLayoutManager;

    String usr; // 사용자
    String room_id; // 방번호
    String u_id; // 사용자 고유번호
    String cur_warning_sign; // 사용자 경고횟수
    int int_cur_warning_sign; // 사용자 경고횟수 int형

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chat_room);

        //MainActivity로부터 usr받기
        final Intent intent = getIntent();
        usr = intent.getExtras().getString("usr");
        u_id = intent.getExtras().getString("uid");
        room_id =" ";

        //Firebase연결 -> 나중에 채팅 리스트 불러오기
        database = FirebaseDatabase.getInstance();

        // UI
        TxNotifi = (TextView) findViewById(R.id.textNotifi);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnMyroom = (Button) findViewById(R.id.btnMyroom);
        btnMakeNew = (Button) findViewById(R.id.btnMakeNew);

        chatListView = (RecyclerView) findViewById(R.id.chat_list_view);
        chatListView.setHasFixedSize(false); // 이거 뭔지 모름 -> 안에 내용물이 recyclerView 사이즈에 영향줄껀지
        chatListLayoutManager = new LinearLayoutManager(this);
        chatListView.setLayoutManager(chatListLayoutManager);

        ChatList = new ArrayList<>();

        chatListAdapter = new ChatListAdapter(ChatList);
        chatListView.setAdapter(chatListAdapter);

        getListFrom();


        // 리스트 터치 먹도록
        chatListView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),chatListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {

                        // 3회 경고 확인하기
                        FirebaseDatabase.getInstance().getReference("users").child(u_id).child("warning_sign").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                cur_warning_sign = room_id = (String) dataSnapshot.getValue();
                                int_cur_warning_sign = Integer.parseInt(cur_warning_sign.toString());

                                // 경고 3회 이상이면 방 입장 불가
                                if(int_cur_warning_sign >= 3)
                                {
                                    Toast.makeText(getApplicationContext(), "경고가 3회이상 누적되어 방에 입장할 수 없습니다.", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    // 서버에서 룸아이디 읽어오기
                                    FirebaseDatabase.getInstance().getReference("users").child(u_id).child("room_id").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            room_id = (String) dataSnapshot.getValue();

                                            // 1. 서버 room 데이터 - 인원+1
                                            // -> 나중에 하기
                                            // 룸 아이디가 없을 때 룸 아이디 집어넣기
                                            if(room_id.equals(" ")){
                                                room_id = ChatList.get(position).getRoomId();

                                                // 서버에 저장하기

                                                //(2) chat Table
//                                                DatabaseReference myRef2 = database.getReference("chats").child(room_id);
//                                                Hashtable<String, String> Chat = new Hashtable<String, String>();
//
//                                                Chat.put("usr", usr);
//                                                myRef2.setValue(Chat);

                                                //(3) user Table

                                                FirebaseDatabase.getInstance().getReference("users").child(u_id).child("room_id").setValue(room_id);

                                                //(4) room Table
                                                // 1. 방 정보에 방 인원 수를 추가하여 업데이트 한다
                                                // 2. 방 정보에 새로운 인원의 닉네임을 넣어준다
//                            final DatabaseReference Roomdatabase = database.getReference("Room").child(room_id);

                                                // 1. 방 인원 수 업데이트
                                                final DatabaseReference Roomdatabase = database.getReference("Room").child(room_id);
                                                Roomdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        // 현재 방 인원을 받아온다
                                                        String room_num = (String) dataSnapshot.child("num").getValue();
                                                        int int_room_num = Integer.parseInt(room_num.toString())+1;

                                                        // 현재 방 인원을 업데이트 한다
                                                        room_num = Integer.toString(int_room_num);

                                                        // 2. 새로 들어간 사람의 닉네임을 추가한다
                                                        String child_usr_id = "usr"+room_num;

                                                        // 새로 들어간 사람 닉네임 업데이트
                                                        Roomdatabase.child(child_usr_id).setValue(u_id);
                                                        // 방 인원 업데이트
                                                        Roomdatabase.child("num").setValue(room_num);

                                                        Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                                                        intent.putExtra("departure", (String) dataSnapshot.child("departure").getValue());
                                                        intent.putExtra("destination", (String) dataSnapshot.child("destination").getValue());
                                                        intent.putExtra("usr", usr);
                                                        intent.putExtra("u_id", u_id);
                                                        intent.putExtra("room_id", room_id);
                                                        startActivity(intent);

                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            }else{
                                                Toast.makeText(getApplicationContext(), "이미 동행하려는 방이 있습니다.", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {  //position+"번 째 아이템 롱 클릭
                    }
                }));
    }

    void getListFrom() {

        //메시지 읽어오기
        DatabaseReference myRef2 = database.getReference("Room");
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatListItem _chatList = dataSnapshot.getValue(ChatListItem.class);
                ChatList.add(_chatList);
                chatListView.scrollToPosition(ChatList.size()-1);  // 화면 위에서부터 채워주기 - 화면 위에서부터 인덱스 1
                chatListAdapter.notifyItemInserted(ChatList.size()-1); // 화면 위에서부터 채워주기
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

    public void onBtnSearchClicked(View v) {
        Toast.makeText(getApplicationContext(),"검색 버튼", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),SearchRoom.class);
        intent.putExtra("usr",usr);
        startActivity(intent);
    }

    public void onBtnMyroomClicked(View v) {

        // 서버에서 room_id 찾아옴
        FirebaseDatabase.getInstance().getReference("users").child(u_id).child("room_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                room_id = (String) dataSnapshot.getValue();
                //room_id 아무것도 없으면 -> 내 방이 없는 거임
                if (room_id.equals(" ")) {
                    Toast.makeText(getApplicationContext(), "동행하려는 방이 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    FirebaseDatabase.getInstance().getReference("Room").child(room_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String departure = (String) dataSnapshot.child("departure").getValue();
                            String destination = (String) dataSnapshot.child("destination").getValue();

                            // room_id 값 있으면
                            // 서버에서 usr에 맞는 room_id 값 가져왓으면
                            Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                            intent.putExtra("departure", departure);
                            intent.putExtra("destination",destination);
                            intent.putExtra("usr", usr);
                            intent.putExtra("room_id", room_id);
                            intent.putExtra("uid",u_id);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 이거 너가 다시 수정해주는게 나을것가타!! 너가 한거랑 똑같이 주석하면 버튼자체가 안먹혀!!
    // 근데 이거 왜 수정하려고 하는지 잘 모르겠어!! 한 사람당 한 방만 들어가는 거 아니엇낭..?...

    public void onBtnMakeNewClicked(View v) {
        // 서버에서 자기 room 있는지 확인하고
        // 서버에서 room_id 찾아옴

        FirebaseDatabase.getInstance().getReference("users").child(u_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                room_id = (String) dataSnapshot.child("room_id").getValue();
                cur_warning_sign = (String) dataSnapshot.child("warning_sign").getValue();
                int_cur_warning_sign = Integer.parseInt(cur_warning_sign.toString());

                if(int_cur_warning_sign >= 3) {
                    FirebaseDatabase.getInstance().getReference("users").child(u_id).child("warning_sign").setValue("0");
                    Toast.makeText(getApplicationContext(), "경고가 3회이상 누적되어 방을 생성할 수 없습니다", Toast.LENGTH_LONG).show();
                }
                else {

                    //room_id 아무것도 없으면 -> 내 방이 없는 거임
                    if (room_id.equals(" ")) { // 방없음

                        Intent intent = new Intent(getApplicationContext(), MakeNewRoom.class);
                        intent.putExtra("usr", usr);
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("uid", u_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "이미 동행하려는 방이 있습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
