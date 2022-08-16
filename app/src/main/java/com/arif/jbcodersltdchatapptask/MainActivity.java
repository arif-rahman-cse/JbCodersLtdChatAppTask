package com.arif.jbcodersltdchatapptask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import com.arif.jbcodersltdchatapptask.databinding.ActivityMainBinding;
import com.arif.jbcodersltdchatapptask.model.ChatMessage;
import com.arif.jbcodersltdchatapptask.model.Chatroom;
import com.arif.jbcodersltdchatapptask.model.Users;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


    }
}