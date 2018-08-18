package com.example.hwayoung.chatapp.model;

public class RoomModel {

    public String departure; // 회원가입할 때 등록한 사용자의 닉네임
    public String destination; // 방 고유번호
    public String num; // 학번 //화영쓰 - 이거 학번아니고 인원입니당! 수정해주십시오
    public String time; // 모임 시간
    public String room_id; // 경고 횟수
    public String usr1; // 경고 횟수
    public String usr2; // 경고 횟수
    public String usr3; // 경고 횟수
    public String usr4; // 경고 횟수
    public String final_uid;
    // 화영쓰 //
    public String cash; // 방장이 더 내는 금액
    // DB에 저장할 필요없는 데이터,,, 잠시 RoomModel 좀 빌려 사용하겟숩니당,,,
    public String fee; // 택시요금
    public String fee_l;  // 택시요금-방장


   // public static String uid; // 파이어베이스 데이터베이스에서 가져올 유저의 이름

//    public void RoomModl(String _room_id, String _uid){
//        room_id = _room_id;
//        uid = _uid;
//    }

   // public String getUid(){
     //   return uid;
    //}

}
