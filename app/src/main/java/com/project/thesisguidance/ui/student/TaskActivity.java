package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.TaskAdapter;
import com.project.thesisguidance.model.Student;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.ui.MainActivity;
import com.project.thesisguidance.ui.student.AddTaskActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TaskActivity extends AppCompatActivity {

    private static final String TAG = "TaskActivity";
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
        getStudentById(studentId);
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

    private void getTaskByStudentId(final String studentId) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .whereEqualTo("studentId", studentId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                // event when document change for real time use
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                ArrayList<StudentTask> studentTasks = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    StudentTask studentTask = document.toObject(StudentTask.class);
                                    studentTask.setTaskId(document.getId());
                                    studentTasks.add(studentTask);
                                }
                                adapter.setStudentTasks(studentTasks);
                            }
                        }else{
                            String errorMessage = task.getException().getMessage();
                            Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    private void addTask() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }


    private void getStudentById(String studentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getStudentByIdQuery = db.collection("students")
                .whereEqualTo(FieldPath.documentId(), studentId)
                .limit(1);

        getStudentByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                Student student = task.getResult().getDocuments().get(0).toObject(Student.class);
                                if (student != null) {
                                    bindUIStudent(student);
                                }
                            } else {
                                Toast.makeText(TaskActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudent(Student student) {
        ImageButton buttonCloseMessage = findViewById(R.id.buttonCloseMessage);
        buttonCloseMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includeWelcome).setVisibility(View.GONE);
            }
        });

        TextView tvUser = findViewById(R.id.tvUserInfo);
        tvUser.setText(student.getName() + " (" + student.getNpm() + ")");
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}
