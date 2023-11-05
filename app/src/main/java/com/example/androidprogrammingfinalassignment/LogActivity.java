package com.example.androidprogrammingfinalassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Log> logs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        recyclerView = findViewById(R.id.logRecyclerView);
        logs = MainActivity.getLogs();

        LogAdapter logAdapter = new LogAdapter(getApplicationContext(), logs);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(logAdapter);
        recyclerView.smoothScrollToPosition(logAdapter.getItemCount());
    }

    public void onBackButton(View v)
    {
        Intent intent = new Intent(LogActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
