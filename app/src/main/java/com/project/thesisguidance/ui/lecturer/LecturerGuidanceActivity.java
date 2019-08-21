package com.project.thesisguidance.ui.lecturer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.GuidanceAdapter;
import com.project.thesisguidance.adapter.ViewPagerAdapter;
import com.project.thesisguidance.model.Lecturer;
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.ui.MainActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;

public class LecturerGuidanceActivity extends AppCompatActivity {

    private GuidanceAdapter adapter = new GuidanceAdapter();
    private String TAG = "LecturerGuidanceActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_task);

        String lecturerId = SharedPreferenceHelper.getString(this, Constant.LOGGED_LECTURER_ID);

        initView();

        getLecturerById(lecturerId);
    }

    private void initView() {
        LinearLayout buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setVisibility(View.VISIBLE);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceHelper.removeString(LecturerGuidanceActivity.this, Constant.LOGGED_STUDENT_ID);
                SharedPreferenceHelper.removeString(LecturerGuidanceActivity.this, Constant.LOGGED_LECTURER_ID);
                Intent intent = new Intent(LecturerGuidanceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        initTabs();
    }

    private void initTabs() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LecturerGuidanceFragment(), "Bimbingan");
        adapter.addFragment(new LecturerThesisFragment(), "Pengajuan Judul");

        viewPager.setAdapter(adapter);
    }


    private void getLecturerById(String lecturerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getStudentByIdQuery = db.collection("dosen")
                .whereEqualTo("nik", lecturerId)
                .limit(1);

        getStudentByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                Lecturer lecturer = task.getResult().getDocuments().get(0).toObject(Lecturer.class);
                                binUILecturer(lecturer);
                            } else {
                                Toast.makeText(LecturerGuidanceActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void binUILecturer(Lecturer lecturer) {
        ImageButton buttonCloseMessage = findViewById(R.id.buttonCloseMessage);
        buttonCloseMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includeWelcome).setVisibility(View.GONE);
            }
        });

        TextView tvUser = findViewById(R.id.tvUserInfo);
        tvUser.setText(lecturer.getNama_dosen() + " (" + lecturer.getNik() + ")");
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
