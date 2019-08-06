package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    TextInputLayout textInputLayoutTaskName;
    TextInputLayout textInputLayoutDescription;
    TextInputLayout textInputLayoutAttachment;
    Button buttonAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

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

    private void validateTaskForm(){
        String taskName = textInputLayoutTaskName.getEditText().getText().toString();
        String description = textInputLayoutDescription.getEditText().getText().toString();
        String attachmentUrl = textInputLayoutAttachment.getEditText().getText().toString();

        if (taskName.isEmpty()){
            textInputLayoutTaskName.setError("Task name is required");
        }else if (description.isEmpty()){
            textInputLayoutDescription.setError("Description is required");
        } else if (attachmentUrl.isEmpty()){
            textInputLayoutAttachment.setError("Attachment file url is required");
        }else{
            addTask();
        }
    }

    private void addTask(){
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> task = new HashMap<>();
        task.put("attachmentUrl", textInputLayoutAttachment.getEditText().getText().toString());
        task.put("createdAt", FieldValue.serverTimestamp());
        task.put("description", textInputLayoutDescription.getEditText().getText().toString());
        task.put("lecturerId", SharedPreferenceHelper.getString(this, Constant.LECTURER_ID));
        task.put("status", "On Progress");
        task.put("studentId", SharedPreferenceHelper.getString(this, Constant.STUDENT_ID));
        task.put("taskName", textInputLayoutTaskName.getEditText().getText().toString());

        db.collection("task")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dialog.dismiss();
                        String documentId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot Writter with ID : " + documentId);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddTaskActivity.this , "Failed add new task, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
