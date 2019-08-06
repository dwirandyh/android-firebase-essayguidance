package com.project.thesisguidance.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.thesisguidance.R;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.ui.student.TaskDetailActivity;
import com.project.thesisguidance.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static String TAG = "TaskAdapter";
    private ArrayList<StudentTask> studentTasks = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        StudentTask studentTask = studentTasks.get(position);
        holder.tvTaskName.setText(studentTask.getTaskName());
        holder.tvStatus.setText(studentTask.getStatus());
        holder.tvDescription.setText(studentTask.getDescription());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.US);
            String dateString = simpleDateFormat.format(studentTask.getCreatedAt().toDate());
            holder.tvDate.setText(dateString);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return studentTasks.size();
    }

    public void setStudentTasks(ArrayList<StudentTask> studentTasks) {
        this.studentTasks = studentTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskName;
        TextView tvDate;
        TextView tvStatus;
        TextView tvDescription;

        TaskViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StudentTask studentTask = studentTasks.get(getAdapterPosition());

                    Intent intent = new Intent(itemView.getContext(), TaskDetailActivity.class);
                    intent.putExtra(Constant.TASK_ID, studentTask.getTaskId());
                    intent.putExtra(Constant.STUDENT_ID, studentTask.getStudentId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
