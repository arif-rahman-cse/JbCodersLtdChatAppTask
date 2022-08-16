package com.arif.jbcodersltdchatapptask.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arif.jbcodersltdchatapptask.R;
import com.arif.jbcodersltdchatapptask.model.Chatroom;

import java.util.ArrayList;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<ChatRoomRecyclerAdapter.ViewHolder>{

    private ArrayList<Chatroom> mChatRooms;
    private ChatroomRecyclerClickListener mChatroomRecyclerClickListener;

    public ChatRoomRecyclerAdapter(ArrayList<Chatroom> chatRooms, ChatroomRecyclerClickListener chatroomRecyclerClickListener) {
        this.mChatRooms = chatRooms;
        mChatroomRecyclerClickListener = chatroomRecyclerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chatroom_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mChatroomRecyclerClickListener);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chatroomTitle.setText(mChatRooms.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {
        TextView chatroomTitle;
        ChatroomRecyclerClickListener clickListener;

        public ViewHolder(View itemView, ChatroomRecyclerClickListener clickListener) {
            super(itemView);
            chatroomTitle = itemView.findViewById(R.id.chatroom_title);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onChatroomSelected(getAdapterPosition());
        }
    }

    public interface ChatroomRecyclerClickListener {
        public void onChatroomSelected(int position);
    }
}
















