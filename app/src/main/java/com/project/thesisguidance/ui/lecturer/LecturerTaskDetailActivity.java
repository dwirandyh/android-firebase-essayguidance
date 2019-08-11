package com.project.thesisguidance.ui.lecturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.CommentAdapter;
import com.project.thesisguidance.model.Comment;
import com.project.thesisguidance.model.Lecturer;
import com.project.thesisguidance.model.Student;
import com.project.thesisguidance.model.StudentTask;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class LecturerTaskDetailActivity extends AppCompatActivity {

    private static String TAG = "TaskDetailActivity";
    private String taskId;
    private String lecturerId;
    private String taskDocumentPath;

    private TextInputLayout textInputComment;
    private NestedScrollView nestedScrollView;

    private Lecturer lecturer = new Lecturer();
    private ArrayList<String> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for ui scrolling up when keyboard is appear
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_lecturer_task_detail);


        taskId = getIntent().getStringExtra(Constant.TASK_ID);
        getTaskById(taskId);
        getCommentByTaskId(taskId);

        lecturerId = SharedPreferenceHelper.getString(this, Constant.LOGGED_LECTURER_ID);
        getLecturerById(lecturerId);

        String studentId = getIntent().getStringExtra(Constant.STUDENT_ID);
        getStudentById(studentId);

        initView();
    }

    private void initView() {
        LinearLayout buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setVisibility(View.VISIBLE);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputComment = findViewById(R.id.textInputComment);

        FloatingActionButton buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });


    }

    private void addComment() {
        Map<String, Object> comment = new HashMap<>();
        comment.put("comment", textInputComment.getEditText().getText().toString());
        comment.put("createdAt", FieldValue.serverTimestamp());
        comment.put("studentId", "");
        comment.put("name", lecturer.getName());
        comment.put("lecturerId", lecturerId);
        comment.put("taskId", taskId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot Writer with ID : " + documentId);
                        textInputComment.getEditText().setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LecturerTaskDetailActivity.this, "Failed add new comment, try again later", Toast.LENGTH_SHORT).show();
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
                                    taskDocumentPath = task.getResult().getDocuments().get(0).getReference().getPath();
                                    bindUIStudentTask(studentTask);
                                }
                            } else {
                                Toast.makeText(LecturerTaskDetailActivity.this, "Failed get student task", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudentTask(final StudentTask studentTask) {
        TextView tvTaskName = findViewById(R.id.tvTaskName);
        TextView tvDate = findViewById(R.id.tvDate);
        Spinner spinnerStatus = findViewById(R.id.spinnerStatus);
        initSpinnerStatus(spinnerStatus);

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvAttachmentStatus = findViewById(R.id.tvAttachmentStatus);
        Button buttonDownloadAttachment = findViewById(R.id.buttonDownloadAttachment);


        tvTaskName.setText(studentTask.getTaskName());
        if (studentTask.getStatus().equals(Constant.ON_PROGRESS)){
            spinnerStatus.setSelection(0);
        }else if (studentTask.getStatus().equals(Constant.ACC)){
            spinnerStatus.setSelection(1);
        }
        tvDescription.setText(studentTask.getDescription());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.US);
        String dateString = simpleDateFormat.format(studentTask.getCreatedAt().toDate());
        tvDate.setText(dateString);

        if (!studentTask.getAttachmentUrl().isEmpty()) {
            buttonDownloadAttachment.setVisibility(View.VISIBLE);
            tvAttachmentStatus.setText(getResources().getString(R.string.attachment));

            buttonDownloadAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(studentTask.getAttachmentUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                studentTask.setStatus(statusList.get(i));
                updateStudentTask(studentTask);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSpinnerStatus(Spinner spinner){
        statusList = new ArrayList<>();
        statusList.add(Constant.ON_PROGRESS);
        statusList.add(Constant.ACC);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private void updateStudentTask(StudentTask studentTask){
        HashMap<String, Object> studentTaskDocument = new HashMap<>();
        studentTaskDocument.put("status", studentTask.getStatus());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(taskDocumentPath)
                .update(studentTaskDocument)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Student Task Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Student Task failed to update");
                    }
                });
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
                                Toast.makeText(LecturerTaskDetailActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudent(Student student) {
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        TextView tvToolbarSubtitle = findViewById(R.id.tvToolbarSubtitle);

        tvToolbarTitle.setText(student.getName());
        tvToolbarSubtitle.setText(student.getNpm());
    }

    private void getCommentByTaskId(final String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments")
                //.whereEqualTo("taskId", taskId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Liesten:error", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            ArrayList<Comment> comments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Comment comment = document.toObject(Comment.class);
                                if (comment.getTaskId().equals(taskId)){
                                    comment.setTaskId(document.getId());
                                    comments.add(comment);
                                }
                            }
                            bindUIComments(comments);
                        }
                    }
                });
    }

    private void bindUIComments(ArrayList<Comment> comments) {
        RecyclerView rvComment = findViewById(R.id.rvComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setNestedScrollingEnabled(false);

        CommentAdapter adapter = new CommentAdapter(this);
        adapter.setComments(comments);
        rvComment.setAdapter(adapter);

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                // scroll nestedscrollview to bottom
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void getLecturerById(String lecturerId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getStudentByIdQuery = db.collection("lecturers")
                .whereEqualTo(FieldPath.documentId(), lecturerId)
                .limit(1);

        getStudentByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                lecturer = task.getResult().getDocuments().get(0).toObject(Lecturer.class);
                            } else {
                                Toast.makeText(LecturerTaskDetailActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
