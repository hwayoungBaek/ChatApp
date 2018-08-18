package com.example.hwayoung.chatapp;

public class Search {
    public String departure;
    public String arrival;

    public Search()
    {

    }
    public Search(String _departure,String _arrival) {
        this.departure =_departure;
        this.arrival = _arrival;
    }

    public String getDeparture() {
        return this.departure;
    }
    public String getArrival() {
        return this.arrival;
    }

}
