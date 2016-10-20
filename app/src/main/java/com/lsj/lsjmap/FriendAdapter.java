package com.lsj.lsjmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<Contacts> {

    private int resourceId;

    public FriendAdapter(Context context, int textViewResourceId, List<Contacts> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacts friend = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView friendName = (TextView) view.findViewById(R.id.friend_name);
        friendName.setText(friend.getName());
        return view;
    }
}
