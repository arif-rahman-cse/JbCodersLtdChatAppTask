package com.arif.jbcodersltdchatapptask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arif.jbcodersltdchatapptask.adapter.ChatRoomRecyclerAdapter;
import com.arif.jbcodersltdchatapptask.databinding.ActivityChatRoomBinding;
import com.arif.jbcodersltdchatapptask.model.Chatroom;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class ChatRoomActivity extends AppCompatActivity implements ChatRoomRecyclerAdapter.ChatroomRecyclerClickListener {
    private static final String TAG = "ChatRoomActivity";

    private ActivityChatRoomBinding binding;
    private ArrayList<Chatroom> mChatRooms = new ArrayList<>();
    private Set<String> mChatroomIds = new HashSet<>();
    private ChatRoomRecyclerAdapter mChatroomRecyclerAdapter;
    private ListenerRegistration mChatroomEventListener;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room);

        mDb = FirebaseFirestore.getInstance();

        //initSupportActionBar();
        initChatroomRecyclerView();

        binding.fabCreateChatroom.setOnClickListener(view -> {
            newChatroomDialog();
        });
    }

    private void newChatroomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a chatroom name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("CREATE", (dialog, which) -> {
            if (!input.getText().toString().equals("")) {
                buildNewChatroom(input.getText().toString());
            } else {
                Toast.makeText(ChatRoomActivity.this, "Enter a chatroom name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void buildNewChatroom(String chatroomName) {

        final Chatroom chatroom = new Chatroom();
        chatroom.setTitle(chatroomName);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        DocumentReference newChatroomRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document();

        chatroom.setChatroom_id(newChatroomRef.getId());

        newChatroomRef.set(chatroom).addOnCompleteListener(task -> {
            hideDialog();

            if (task.isSuccessful()) {
                navChatroomActivity(chatroom);
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void navChatroomActivity(Chatroom chatroom) {
        Intent intent = new Intent(ChatRoomActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.intent_chatroom), chatroom);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatroomEventListener != null) {
            mChatroomEventListener.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatrooms();
    }

    private void getChatrooms() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mDb.setFirestoreSettings(settings);

        CollectionReference chatroomsCollection = mDb
                .collection(getString(R.string.collection_chatrooms));

        mChatroomEventListener = chatroomsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.d(TAG, "onEvent: called.");

                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        Chatroom chatroom = doc.toObject(Chatroom.class);
                        if (!mChatroomIds.contains(chatroom.getChatroom_id())) {
                            mChatroomIds.add(chatroom.getChatroom_id());
                            mChatRooms.add(chatroom);
                        }
                    }
                    Log.d(TAG, "onEvent: number of chatrooms: " + mChatRooms.size());
                    mChatroomRecyclerAdapter.notifyDataSetChanged();
                }

            }
        });
    }


    private void initSupportActionBar() {
        setTitle("Chat Rooms");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initChatroomRecyclerView() {
        mChatroomRecyclerAdapter = new ChatRoomRecyclerAdapter(mChatRooms, this);
        binding.chatRoomsRecyclerView.setAdapter(mChatroomRecyclerAdapter);
        binding.chatRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onChatroomSelected(int position) {
        Log.d(TAG, "onChatroomSelected: selected a chatroom at position: " + position);
        navChatroomActivity(mChatRooms.get(position));
    }

    private void showDialog() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        binding.progressBar.setVisibility(View.GONE);
    }
}