package com.example.hwayoung.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ChatListAdapter extends  RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    List<ChatListItem> listChat;
    String dep;
    String des;
//화영쓰 "tv_cash"가 포함된 줄 모두 추가
    public ChatListAdapter(List<ChatListItem> listChat){
        this.listChat = listChat;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_dep;
        public TextView tv_des;
        public TextView tv_cash;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_dep = (TextView)itemView.findViewById(R.id.tv_departure);
            tv_des = (TextView)itemView.findViewById(R.id.tv_destination);
            tv_cash = (TextView)itemView.findViewById(R.id.tv_cash);
        }
    }


    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /*
    public ChatListAdapter(List<ChatListItem> listChat, String dep, String des){
        this.listChat = listChat;
        this.dep = dep;
        this.des = des;
    }*/

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /*
    // Create new views (invoked by the layout manager)
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }*/

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.tv_dep.setText(listChat.get(position).getDep());
        holder.tv_des.setText(listChat.get(position).getDes());
        holder.tv_cash.setText("방장이 더 내는 금액 "+listChat.get(position).getCash()+"원");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listChat.size();
    }
}


