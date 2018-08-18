package com.example.hwayoung.chatapp;

public class ChatListItem {
    public String room_id; // 고유번호 - 방 생성 날짜시간
    public String time;
    public String departure; //출발지
    public String destination; //목적지
    public String num; //인원

    public String usr1; // 사용자1
    public String usr2; // 사용자2
    public String usr3; // 사용자3
    public String usr4; // 사용자4

    public String cash; // 방장이 더 내는 금액 - 화영쓰
    // 화영쓰 - cash, 혹은 _cash 다 추가한 부분. ChatListItem 매개변수에서 String_cash 까먹지말기

    public ChatListItem(){

    }

    //public void ChatListItem(String _room_id,String _time,String _dep, String _des,int _num)  // time 추가해서 다시 만들기
    public ChatListItem(String _room_id,String _dep, String _des,String _num,
                        String _usr1,String _usr2,String _usr3,String _usr4,String _cash){
        this.room_id=_room_id;
        //this.time=_time;
        this.departure=_dep;
        this.destination=_des;
        this.num = _num;
        this.usr1 = _usr1;
        this.usr2 = _usr2;
        this.usr3 = _usr3;
        this.usr4 = _usr4;
        this.cash = _cash;
    }

    public String getCash  (){
        return cash;
    }
    public String getRoomId() {
        return room_id;
    }
    public String getTime() {
        return time;
    }
    public String getDep() {
        return departure;
    }
    public String getDes() {
        return destination;
    }
    public String getNum() {
        return num;
    }
    public String[] getAllUsr(){
        String allUsr[] = {usr1, usr2, usr3, usr4};
        return allUsr;
    }



    public void setRoomId(String _room_id) {
        this.room_id=_room_id;
    }
    public void setTime(String _time) {
        this.time=_time;
    }
    public void setDep(String _dep) {
        this.departure=_dep;
    }
    public void setDes(String _des) {
        this.destination=_des;
    }
    public void setCash(String _cash){
        this.cash = _cash;
    }
    /*
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getWrite_date() {
        return write_date;
    }

    public void setWrite_date(Date write_date) {
        this.write_date = write_date;
    }
    */
}

