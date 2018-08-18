package com.example.hwayoung.chatapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>
{
    private List<Search> items;
    private ArrayList<Search> arrayList;

    public SearchAdapter(List<Search> items)
    {
        this.items=items;
        arrayList = new ArrayList<Search>();
        //  arrayList.addAll(items);
    }

    public void filter(String departure, String arrival) {

        departure = departure.toLowerCase(Locale.getDefault());
        arrival = arrival.toLowerCase(Locale.getDefault());

        items.clear();
        if(items.isEmpty())
        {
            Log.i("제발","비어있어");
        }
        else {
            for (Search search : arrayList) {
                String departure1 = search.getDeparture();
                String arrival1 = search.getArrival();
                if (departure1.contains(departure) && arrival1.contains(arrival)) {
                    items.add(search); }
            }
        }
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView listname;

        public ViewHolder(View itemView) {
            super(itemView);
            listname = (TextView) itemView.findViewById(R.id.listname);

        }
    }

    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_search_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final Search item=items.get(position);
        holder.listname.setText("출발지 : "+item.getDeparture()+"->"+"목적지 : "+item.getArrival());
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
