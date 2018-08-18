package com.example.hwayoung.chatapp.model;

// 사용자 DB 모델
public class UserModel {
    public String userName; // 회원가입할 때 등록한 사용자의 닉네임
    public String room_id; // 방 고유번호
    public String num; // 학번
    public String warning_sign; // 경고 횟수

    public static String uid; // 파이어베이스 데이터베이스에서 가져올 유저의 이름

//    public void UserModl(String _room_id, String _uid){
//        room_id = _room_id;
//        uid = _uid;
//    }

    public String getUid(){
        return uid;
    }
}