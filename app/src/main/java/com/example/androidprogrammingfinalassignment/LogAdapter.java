package com.example.androidprogrammingfinalassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogViewHolder>
{

    Context context;
    List<Log> logs;

    public LogAdapter(Context context, List<Log> logs)
    {
        this.context = context;
        this.logs = MainActivity.getLogs();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new LogViewHolder(LayoutInflater.from(context).inflate(R.layout.log_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position)
    {
        holder.logTextView.setText(logs.get(position).getMessage());
    }

    @Override
    public int getItemCount()
    {
        return logs.size();
    }
}
