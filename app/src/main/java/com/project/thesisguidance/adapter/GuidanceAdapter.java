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
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.ui.lecturer.LecturerGuidanceDetailActivity;
import com.project.thesisguidance.ui.student.GuidanceDetailActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GuidanceAdapter extends RecyclerView.Adapter<GuidanceAdapter.TaskViewHolder> {

    private static String TAG = "GuidanceAdapter";
    private ArrayList<Bimbingan> studentTasks = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Bimbingan studentTask = studentTasks.get(position);
        holder.tvTaskName.setText(studentTask.getBab());
        holder.tvStatus.setText(studentTask.getStatus_bab());
        holder.tvDescription.setText(studentTask.getKeterangan_bab());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.US);
            String dateString = simpleDateFormat.format(studentTask.getTgl_kirim().toDate());
            holder.tvDate.setText(dateString);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return studentTasks.size();
    }

    public void setStudentTasks(ArrayList<Bimbingan> studentTasks) {
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

            tvTaskName = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvSubtitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();

                    Bimbingan guidance = studentTasks.get(getAdapterPosition());

                    String loggedStudentId = SharedPreferenceHelper.getString(itemView.getContext(), Constant.LOGGED_STUDENT_ID);
                    if (!loggedStudentId.isEmpty()){
                        intent = new Intent(itemView.getContext(), GuidanceDetailActivity.class);
                    }

                    String loggedLecturerId = SharedPreferenceHelper.getString(itemView.getContext(), Constant.LOGGED_LECTURER_ID);
                    if (!loggedLecturerId.isEmpty()){
                        intent = new Intent(itemView.getContext(), LecturerGuidanceDetailActivity.class);
                    }

                    intent.putExtra(Constant.GUIDANCE_ID, guidance.getId_bimbingan());
                    intent.putExtra(Constant.STUDENT_ID, guidance.getNpm()); //TODO: Change with real data
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
