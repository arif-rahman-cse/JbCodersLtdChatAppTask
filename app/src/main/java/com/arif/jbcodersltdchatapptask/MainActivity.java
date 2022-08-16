package com.arif.jbcodersltdchatapptask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.arif.jbcodersltdchatapptask.adapter.ChatMessageRecyclerAdapter;
import com.arif.jbcodersltdchatapptask.databinding.ActivityMainBinding;
import com.arif.jbcodersltdchatapptask.model.ChatMessage;
import com.arif.jbcodersltdchatapptask.model.Chatroom;
import com.arif.jbcodersltdchatapptask.model.Users;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private Chatroom mChatroom;
    private ListenerRegistration mChatMessageEventListener, mUserListEventListener;
    private ChatMessageRecyclerAdapter mChatMessageRecyclerAdapter;
    private FirebaseFirestore mDb;
    private ArrayList<ChatMessage> mMessages = new ArrayList<>();
    private Set<String> mMessageIds = new HashSet<>();
    private ArrayList<Users> mUserList = new ArrayList<>();
    //private UserListFragment mUserListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mDb = FirebaseFirestore.getInstance();
        getIncomingIntent();
        initChatroomRecyclerView();
        getChatroomUsers();

        binding.checkmark.setOnClickListener(view -> {
            insertNewMessage();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatMessageEventListener != null) {
            mChatMessageEventListener.remove();
        }
        if (mUserListEventListener != null) {
            mUserListEventListener.remove();
        }
    }


    private void getChatMessages() {

        CollectionReference messagesRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom.getChatroom_id())
                .collection(getString(R.string.collection_chat_messages));

        mChatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ChatMessage message = doc.toObject(ChatMessage.class);
                                if (!mMessageIds.contains(message.getMessage_id())) {
                                    mMessageIds.add(message.getMessage_id());
                                    mMessages.add(message);
                                    binding.chatMessageRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
                                }

                            }
                            mChatMessageRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }

    private void getChatroomUsers() {


        CollectionReference usersRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list));

        mUserListEventListener = usersRef
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "onEvent: Listen failed.", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {

                        // Clear the list and add all the users again
                        mUserList.clear();
                        mUserList = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Users user = doc.toObject(Users.class);
                            mUserList.add(user);
                        }

                        Log.d(TAG, "onEvent: user list size: " + mUserList.size());
                    }
                });
    }

    private void initChatroomRecyclerView() {
        mChatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(mMessages, new ArrayList<Users>(), this);
        binding.chatMessageRecyclerView.setAdapter(mChatMessageRecyclerAdapter);
        binding.chatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.chatMessageRecyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                binding.chatMessageRecyclerView.postDelayed(() -> {
                    if (mMessages.size() > 0) {
                        binding.chatMessageRecyclerView.smoothScrollToPosition(
                                binding.chatMessageRecyclerView.getAdapter().getItemCount() - 1);
                    }

                }, 100);
            }
        });

    }


    private void insertNewMessage() {
        String message = binding.inputMessage.getText().toString();

        if (!message.equals("")) {
            message = message.replaceAll(System.getProperty("line.separator"), "");

            DocumentReference newMessageDoc = mDb
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom.getChatroom_id())
                    .collection(getString(R.string.collection_chat_messages))
                    .document();

            ChatMessage newChatMessage = new ChatMessage();
            newChatMessage.setMessage(message);
            newChatMessage.setMessage_id(newMessageDoc.getId());

            Users user = ((UserClient) (getApplicationContext())).getUser();
            Log.d(TAG, "insertNewMessage: retrieved user client: " + user.toString());
            newChatMessage.setUser(user);

            newMessageDoc.set(newChatMessage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    clearMessage();
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearMessage() {
        binding.inputMessage.setText("");
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra(getString(R.string.intent_chatroom))) {
            mChatroom = getIntent().getParcelableExtra(getString(R.string.intent_chatroom));
            setChatroomName();
            joinChatroom();
        }
    }

    private void joinChatroom() {

        DocumentReference joinChatroomRef = mDb
                .collection(getString(R.string.collection_chatrooms))
                .document(mChatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list))
                .document(FirebaseAuth.getInstance().getUid());

        Users user = ((UserClient) (getApplicationContext())).getUser();
        if (user != null) {
            joinChatroomRef.set(user); // Don't care about listening for completion.
        } else {

        }

    }

    private void setChatroomName() {
        getSupportActionBar().setTitle(mChatroom.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            UserListFragment fragment = (UserListFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_user_list));
            if (fragment != null) {
                if (fragment.isVisible()) {
                    getSupportFragmentManager().popBackStack();
                    return true;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}