package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.model.Student;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private static String TAG = "TaskDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        String taskId = getIntent().getStringExtra(Constant.TASK_ID);
        String studentId = getIntent().getStringExtra(Constant.STUDENT_ID);

        getTaskById(taskId);
        getStudentById(studentId);

        initView();
    }

    private void initView(){
        LinearLayout buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setVisibility(View.VISIBLE);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getTaskById(String taskId) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getTaskByIdQuery = db.collection("task")
                .whereEqualTo(FieldPath.documentId(), taskId)
                .limit(1);

        getTaskByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                dialog.dismiss();

                                StudentTask studentTask = task.getResult().getDocuments().get(0).toObject(StudentTask.class);
                                if (studentTask != null) {
                                    bindUIStudentTask(studentTask);
                                }
                            } else {
                                Toast.makeText(TaskDetailActivity.this, "Failed get student task", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudentTask(StudentTask studentTask) {
        TextView tvTaskName = findViewById(R.id.tvTaskName);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);

        tvTaskName.setText(studentTask.getTaskName());
        tvStatus.setText(studentTask.getStatus());
        tvDescription.setText(studentTask.getDescription());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.US);
        String dateString = simpleDateFormat.format(studentTask.getCreatedAt().toDate());
        tvDate.setText(dateString);
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
                                Toast.makeText(TaskDetailActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudent(Student student){
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        TextView tvToolbarSubtitle = findViewById(R.id.tvToolbarSubtitle);

        tvToolbarTitle.setText(student.getName());
        tvToolbarSubtitle.setText(student.getNpm());
    }
}
