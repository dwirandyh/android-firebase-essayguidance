package com.project.thesisguidance.ui.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.TaskAdapter;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.ui.MainActivity;
import com.project.thesisguidance.ui.student.AddTaskActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TaskActivity extends AppCompatActivity {

    FloatingActionButton buttonAddTask;
    RecyclerView rvTask;
    TaskAdapter adapter = new TaskAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        String studentId = SharedPreferenceHelper.getString(this, Constant.STUDENT_ID);

        initView();
        getTaskByStudentId(studentId);
    }

    private void initView() {
        LinearLayout buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setVisibility(View.VISIBLE);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceHelper.removeString(TaskActivity.this, Constant.LOGGED_STUDENT_ID);
                SharedPreferenceHelper.removeString(TaskActivity.this, Constant.LOGGED_LECTURER_ID);
                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        initTask();
    }


    private void initTask() {
        rvTask = findViewById(R.id.rvTask);
        rvTask.setLayoutManager(new LinearLayoutManager(this));

        rvTask.setAdapter(adapter);
    }

    private void getTaskByStudentId(String studentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .whereEqualTo("studentId", studentId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                // event when document change for real time use
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null){
                            ArrayList<StudentTask> studentTasks = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                StudentTask studentTask = document.toObject(StudentTask.class);
                                studentTask.setTaskId(document.getId());
                                studentTasks.add(studentTask);
                            }
                            adapter.setStudentTasks(studentTasks);
                        }
                    }
                });
    }

    private void addTask(){
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}