package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

public class StudentLoginActivity extends AppCompatActivity {

    private static final String TAG = "StudentLoginActivity";
    TextInputEditText textInputNpm;
    TextInputEditText textInputPassword;
    TextInputLayout textInputLayoutNpm;
    TextInputLayout textInputLayoutPassword;
    MaterialButton buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_student_login);

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

        textInputLayoutNpm = findViewById(R.id.textInputLayoutNik);
        textInputNpm = findViewById(R.id.textInputNpm);

        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputPassword = findViewById(R.id.textInputPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLoginForm();
            }
        });
    }

    private void validateLoginForm() {
        String npm = textInputNpm.getText().toString();
        String password = textInputPassword.getText().toString();
        if (npm.isEmpty()) {
            textInputLayoutNpm.setError("NPM harus diisi");
        } else if (password.isEmpty()) {
            textInputLayoutPassword.setError("Password harus diisi");
        } else {
            doLogin(npm, password);
        }
    }


    private void doLogin(String npm, String password) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query loginQuery = db.collection("students")
                .whereEqualTo("npm", npm)
                .whereEqualTo("password", password);

        loginQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {

                                String studentId = task.getResult().getDocuments().get(0).getId();
                                String lecturerId = task.getResult().getDocuments().get(0).getString("lecturerId");
                                saveLoginSession(studentId, lecturerId);
                                openTaskActivity();
                            } else {
                                Toast.makeText(StudentLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void saveLoginSession(String studentId, String lecturerId) {
        SharedPreferenceHelper.putString(this, Constant.LOGGED_STUDENT_ID, studentId);
        SharedPreferenceHelper.putString(this, Constant.STUDENT_ID, studentId);
        SharedPreferenceHelper.putString(this, Constant.LECTURER_ID, lecturerId);
    }

    private void openTaskActivity() {
        Intent intent = new Intent(StudentLoginActivity.this, TaskActivity.class);
        startActivity(intent);
    }
}
