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
import com.project.thesisguidance.model.ChatBimbingan;
import com.project.thesisguidance.model.Lecturer;
import com.project.thesisguidance.model.Mahasiswa;
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class LecturerTaskDetailActivity extends AppCompatActivity {

    private static String TAG = "GuidanceDetailActivity";
    private String taskId;
    private String lecturerId;
    private String taskDocumentPath;

    private TextInputLayout textInputComment;
    private NestedScrollView nestedScrollView;

    private Lecturer lecturer = new Lecturer();
    private ArrayList<String> statusList;
    private Mahasiswa mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for ui scrolling up when keyboard is appear
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_lecturer_task_detail);


        taskId = getIntent().getStringExtra(Constant.GUIDANCE_ID);
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
        comment.put("isi_chat", textInputComment.getEditText().getText().toString());
        comment.put("tanggal", FieldValue.serverTimestamp());
        comment.put("nik", SharedPreferenceHelper.getString(this, Constant.LOGGED_LECTURER_ID));
        comment.put("nama", lecturer.getNama_dosen());
        comment.put("npm", "");
        comment.put("id_bimbingan", taskId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat_bimbingan")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot Writter with ID : " + documentId);
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
        Query getTaskByIdQuery = db.collection("bimbingan")
                .whereEqualTo(FieldPath.documentId(), taskId)
                .limit(1);

        getTaskByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                dialog.dismiss();

                                Bimbingan studentTask = task.getResult().getDocuments().get(0).toObject(Bimbingan.class);
                                if (studentTask != null) {
                                    taskDocumentPath = task.getResult().getDocuments().get(0).getReference().getPath();
                                    bindUIStudentTask(studentTask);
                                }
                            } else {
                                Toast.makeText(LecturerTaskDetailActivity.this, "Failed get task", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudentTask(final Bimbingan studentTask) {
        TextView tvTaskName = findViewById(R.id.tvTaskName);
        TextView tvDate = findViewById(R.id.tvDate);
        Spinner spinnerStatus = findViewById(R.id.spinnerStatus);
        initSpinnerStatus(spinnerStatus);

        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvAttachmentStatus = findViewById(R.id.tvAttachmentStatus);
        Button buttonDownloadAttachment = findViewById(R.id.buttonDownloadAttachment);


        tvTaskName.setText(studentTask.getBab());
        if (studentTask.getStatus_bab().equals(Constant.ON_PROGRESS)){
            spinnerStatus.setSelection(0);
        }else if (studentTask.getStatus_bab().equals(Constant.ACC)){
            spinnerStatus.setSelection(1);
        }
        tvDescription.setText(studentTask.getKeterangan_bab());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.US);
        String dateString = simpleDateFormat.format(studentTask.getTgl_kirim().toDate());
        tvDate.setText(dateString);

        if (!studentTask.getUrl_dokumen().isEmpty()) {
            buttonDownloadAttachment.setVisibility(View.VISIBLE);
            tvAttachmentStatus.setText(getResources().getString(R.string.attachment));

            buttonDownloadAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(studentTask.getUrl_dokumen());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                studentTask.setStatus_bab(statusList.get(i));
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

    private void updateStudentTask(Bimbingan studentTask){
        HashMap<String, Object> studentTaskDocument = new HashMap<>();
        studentTaskDocument.put("status", studentTask.getStatus_bab());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(taskDocumentPath)
                .update(studentTaskDocument)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Mahasiswa Task Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Mahasiswa Task failed to update");
                    }
                });
    }

    private void getStudentById(String studentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getStudentByIdQuery = db.collection("mahasiswa")
                .whereEqualTo("npm", studentId)
                .limit(1);

        getStudentByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                mahasiswa = task.getResult().getDocuments().get(0).toObject(Mahasiswa.class);
                                if (mahasiswa != null) {
                                    bindUIStudent(mahasiswa);
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

    private void bindUIStudent(Mahasiswa mahasiswa) {
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        TextView tvToolbarSubtitle = findViewById(R.id.tvToolbarSubtitle);

        tvToolbarTitle.setText(mahasiswa.getNama());
        tvToolbarSubtitle.setText(mahasiswa.getNpm());
    }

    private void getCommentByTaskId(final String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat_bimbingan")
                //.whereEqualTo("taskId", taskId)
                .orderBy("tanggal", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Liesten:error", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            ArrayList<ChatBimbingan> chatBimbingans = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                ChatBimbingan chatBimbingan = document.toObject(ChatBimbingan.class);
                                if (chatBimbingan.getId_bimbingan().equals(taskId)){
                                    //chatBimbingan.setTaskId(document.getId());
                                    chatBimbingans.add(chatBimbingan);
                                }
                            }
                            bindUIComments(chatBimbingans);
                        }
                    }
                });
    }

    private void bindUIComments(ArrayList<ChatBimbingan> chatBimbingans) {
        RecyclerView rvComment = findViewById(R.id.rvComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setNestedScrollingEnabled(false);

        CommentAdapter adapter = new CommentAdapter(this);
        adapter.setChatBimbingans(chatBimbingans);
        rvComment.setAdapter(adapter);

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                // scroll nestedscrollview to bottom
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void getLecturerById(String nik){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query getLecturerByIdQuery = db.collection("dosen")
                .whereEqualTo("nik", nik)
                .limit(1);

        getLecturerByIdQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                lecturer = task.getResult().getDocuments().get(0).toObject(Lecturer.class);
                            } else {
                                Toast.makeText(LecturerTaskDetailActivity.this, "Failed Get Lecturer Data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
