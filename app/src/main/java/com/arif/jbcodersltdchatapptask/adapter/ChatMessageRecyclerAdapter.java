package com.arif.jbcodersltdchatapptask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arif.jbcodersltdchatapptask.R;
import com.arif.jbcodersltdchatapptask.model.ChatMessage;
import com.arif.jbcodersltdchatapptask.model.Users;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>{

    private ArrayList<ChatMessage> mMessages;
    private ArrayList<Users> mUsers;
    private Context mContext;

    public ChatMessageRecyclerAdapter(ArrayList<ChatMessage> messages,
                                      ArrayList<Users> users,
                                      Context context) {
        this.mMessages = messages;
        this.mUsers = users;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_message_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        if(FirebaseAuth.getInstance().getUid().equals(mMessages.get(position).getUser().getUser_id())){
            holder.username.setTextColor(ContextCompat.getColor(mContext, R.color.green1));
        }
        else{
            holder.username.setTextColor(ContextCompat.getColor(mContext, R.color.blue2));
        }

        holder.username.setText(mMessages.get(position).getUser().getName());
        holder.message.setText(mMessages.get(position).getMessage());
    }



    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message, username;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_message);
            username = itemView.findViewById(R.id.chat_message_username);
        }
    }


}
















