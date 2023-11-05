package com.example.androidprogrammingfinalassignment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LogViewHolder extends RecyclerView.ViewHolder
{

    TextView logTextView;

    public LogViewHolder(@NonNull View itemView)
    {
        super(itemView);
        logTextView = itemView.findViewById(R.id.logTextView);
    }
}
