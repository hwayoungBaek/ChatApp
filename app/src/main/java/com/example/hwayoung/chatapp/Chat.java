package com.example.hwayoung.chatapp;

public class Chat {

    public String usr;
    public String text;

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Chat(String usr,String text) {

        this.usr = usr;
        this.text = text;
    }


    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
