package com.example.hwayoung.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class MyAdapter extends  RecyclerView.Adapter<MyAdapter.ViewHolder>{

    List<Chat> mChat;
    String usr;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.mTextView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Chat> mChat,String usr) {
        this.mChat = mChat;
        this.usr = usr;
    }

    @Override
    public int getItemViewType(int position) {

        if (mChat.get(position).getUsr().equals(usr)){
            // 같은 사용자의 대화내용이면 return 1
            return 1;
        } else {
            return 2;
        }
    }
    // Create new views (invoked by the layout manager)
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v;
        if (viewType == 1) {  //내꺼. 오른쪽에 출력
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_text_view, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_text_view, parent, false);
        }

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (mChat.get(position).getUsr().equals(usr)){  // 대화내용 == 내가보낸거
            holder.mTextView.setText(mChat.get(position).getText());
        }else{
            holder.mTextView.setText(mChat.get(position).getUsr()+": "+mChat.get(position).getText());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
