package com.project.thesisguidance.ui.lecturer;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

public class LecturerLoginActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutNik;
    private static String TAG = "LecturerLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_login);

        initView();
    }

    private void initView(){
        LinearLayout btnBack = findViewById(R.id.buttonBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textInputLayoutNik = findViewById(R.id.textInputLayoutNik);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        MaterialButton btnSignIn = findViewById(R.id.buttonSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLoginForm();
            }
        });
    }

    private void validateLoginForm() {
        String npm = textInputLayoutNik.getEditText().getText().toString();
        String password = textInputLayoutPassword.getEditText().getText().toString();
        if (npm.isEmpty()) {
            textInputLayoutNik.setError("NPM harus diisi");
        } else if (password.isEmpty()) {
            textInputLayoutPassword.setError("Password harus diisi");
        }else{
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
        Query loginQuery = db.collection("dosen")
                .whereEqualTo("nik", npm)
                .whereEqualTo("password", password);

        loginQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                String lecturerId = task.getResult().getDocuments().get(0).getString("nik");
                                saveLoginSession(lecturerId);
                                openTaskActivity();
                            } else {
                                Toast.makeText(LecturerLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void saveLoginSession(String lecturerId) {
        SharedPreferenceHelper.putString(this, Constant.LECTURER_ID, lecturerId);
        SharedPreferenceHelper.putString(this, Constant.LOGGED_LECTURER_ID, lecturerId);
    }

    private void openTaskActivity() {
        Intent intent = new Intent(LecturerLoginActivity.this, LecturerTaskActivity.class);
        startActivity(intent);
    }
}
