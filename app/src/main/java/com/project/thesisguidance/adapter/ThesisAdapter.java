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
import com.project.thesisguidance.model.Skripsi;
import com.project.thesisguidance.ui.lecturer.LecturerGuidanceDetailActivity;
import com.project.thesisguidance.ui.lecturer.LecturerThesisDetailActivity;
import com.project.thesisguidance.ui.student.GuidanceDetailActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ThesisAdapter extends RecyclerView.Adapter<ThesisAdapter.TaskViewHolder> {

    private static String TAG = "ThesisAdapter";
    private ArrayList<Skripsi> skripsiArrayList = new ArrayList<>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thesis, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Skripsi skripsi = skripsiArrayList.get(position);
        holder.tvTitle.setText(skripsi.getJudul());
        holder.tvSubtitle.setText(skripsi.getJenis_skripsi());
        holder.tvStatus.setText(skripsi.getStatus());
        holder.tvDescription.setText(skripsi.getMasalah());
        holder.tvPembimbing.setText(skripsi.getNama_pembimbing());
    }


    @Override
    public int getItemCount() {
        return skripsiArrayList.size();
    }

    public void setSkripsiArrayList(ArrayList<Skripsi> skripsiArrayList) {
        this.skripsiArrayList = skripsiArrayList;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvStatus;
        TextView tvDescription;
        TextView tvPembimbing;

        TaskViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvPembimbing = itemView.findViewById(R.id.tvPembimbing);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Skripsi skripsi = skripsiArrayList.get(getAdapterPosition());

                    Intent intent = new Intent(itemView.getContext(), LecturerThesisDetailActivity.class);
                    intent.putExtra("npm", skripsi.getNpm());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
