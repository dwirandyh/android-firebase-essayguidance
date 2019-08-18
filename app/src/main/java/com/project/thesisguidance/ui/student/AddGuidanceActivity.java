package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.thesisguidance.R;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.HashMap;
import java.util.Map;

public class AddGuidanceActivity extends AppCompatActivity {

    private static final String TAG = "AddGuidanceActivity";
    TextInputLayout textInputLayoutTaskName;
    TextInputLayout textInputLayoutDescription;
    TextInputLayout textInputLayoutAttachment;
    Button buttonAddTask;

    String nik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guidance);

        Intent intent = getIntent();
        nik = intent.getStringExtra(Constant.LECTURER_ID);

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

        textInputLayoutTaskName = findViewById(R.id.textInputLayoutTaskName);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        textInputLayoutAttachment = findViewById(R.id.textInputLayoutPassword);

        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTaskForm();
            }
        });
    }

    private void validateTaskForm() {
        String taskName = textInputLayoutTaskName.getEditText().getText().toString();
        String description = textInputLayoutDescription.getEditText().getText().toString();
        String attachmentUrl = textInputLayoutAttachment.getEditText().getText().toString();

        if (taskName.isEmpty()) {
            textInputLayoutTaskName.setError("Task name is required");
        } else if (description.isEmpty()) {
            textInputLayoutDescription.setError("Description is required");
        } else if (attachmentUrl.isEmpty()) {
            textInputLayoutAttachment.setError("Attachment file url is required");
        } else {
            addTask();
        }
    }

    private void addTask() {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> task = new HashMap<>();
        task.put("bab", textInputLayoutTaskName.getEditText().getText().toString());
        task.put("id_skripsi", SharedPreferenceHelper.getString(this, Constant.ID_SKRIPSI));
        task.put("nik", nik);
        task.put("npm", SharedPreferenceHelper.getString(this, Constant.LOGGED_STUDENT_ID));
        task.put("keterangan_bab", textInputLayoutDescription.getEditText().getText().toString());
        task.put("status_bab", Constant.ON_PROGRESS);
        task.put("tgl_kirim", FieldValue.serverTimestamp());
        task.put("tgl_selesai", FieldValue.serverTimestamp());
        task.put("url_dokumen", textInputLayoutAttachment.getEditText().getText().toString());

        db.collection("bimbingan")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dialog.dismiss();
                        String documentId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot Writter with ID : " + documentId);
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddGuidanceActivity.this, "Failed add new task, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
