package com.project.thesisguidance.ui.lecturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.GuidanceAdapter;
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;

public class LecturerGuidanceFragment extends Fragment {

    private static String TAG = "LecturerGuidance";

    RecyclerView rvGuidance;
    GuidanceAdapter adapter = new GuidanceAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        rvGuidance = view.findViewById(R.id.rvList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        bindUI();

        String nik = SharedPreferenceHelper.getString(getContext(), Constant.LOGGED_LECTURER_ID);
        getGuidanceByNik(nik);
    }


    private void getGuidanceByNik(final String nik) {
        final ProgressDialog dialog = new ProgressDialog(getContext()); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bimbingan")
                .whereEqualTo("nik", nik)
                //.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                ArrayList<Bimbingan> studentTasks = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Bimbingan studentTask = document.toObject(Bimbingan.class);
                                    studentTask.setId_bimbingan(document.getId());
                                    studentTasks.add(studentTask);
                                }
                                adapter.setStudentTasks(studentTasks);

                            } else {
                                //Toast.makeText(getContext(), "Data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
    }

    private void bindUI() {
        rvGuidance.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGuidance.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        String nik = SharedPreferenceHelper.getString(getContext(), Constant.LOGGED_LECTURER_ID);
        getGuidanceByNik(nik);
    }
}
