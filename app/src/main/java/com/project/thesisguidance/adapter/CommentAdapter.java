package com.project.thesisguidance.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.thesisguidance.R;
import com.project.thesisguidance.model.Comment;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.ui.student.TaskDetailActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.TaskViewHolder> {

    private static String TAG = "TaskAdapter";

    private Context context;
    private ArrayList<Comment> comments = new ArrayList<>();

    private static int MY_MESSAGE = 1;
    private static int OTHER_MESSAGE = 0;

    public CommentAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MY_MESSAGE){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_comment, parent, false);
            return new TaskViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new TaskViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvName.setText(comment.getName());
        holder.tvComment.setText(comment.getComment());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
            String dateString = simpleDateFormat.format(comment.getCreatedAt().toDate());
            holder.tvDate.setText(dateString);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Comment comment = comments.get(position);
        String loggedStudentId = SharedPreferenceHelper.getString(context, Constant.LOGGED_STUDENT_ID);
        if (comment.getStudentId().equals(loggedStudentId)){
            return MY_MESSAGE;
        }else{
            return OTHER_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvComment;
        TextView tvDate;

        TaskViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
