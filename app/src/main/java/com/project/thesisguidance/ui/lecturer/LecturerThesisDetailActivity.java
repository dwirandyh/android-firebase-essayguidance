package com.project.thesisguidance.ui.lecturer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.model.Mahasiswa;
import com.project.thesisguidance.model.Skripsi;
import com.project.thesisguidance.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;

public class LecturerThesisDetailActivity extends AppCompatActivity {

    private static String TAG = "LecturerThesisDetail";

    String[] STATUS = new String[]{"Pending", "Diterima", "Ditolak"};

    private String taskDocumentPath = "";
    private AutoCompleteTextView dropdownStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_thesis_detail);

        String npm = getIntent().getStringExtra("npm");
        getThesisByNpm(npm);
        getStudentByNpm(npm);

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
        initDropdownStatus();

        TextView tvStatus = findViewById(R.id.dropdown_status);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskDocumentPath.isEmpty()) {
                    String status = dropdownStatus.getText().toString();
                    if (!status.isEmpty()){
                        save(status);
                    }
                }
            }
        });
    }

    private void save(String status) {
        HashMap<String, Object> studentTaskDocument = new HashMap<>();
        studentTaskDocument.put("status", status);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(taskDocumentPath)
                .update(studentTaskDocument)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LecturerThesisDetailActivity.this, "Gagal mengupdate data, coba lagi nanti", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Mahasiswa Task failed to update");
                    }
                });
    }


    private void getThesisByNpm(final String npm) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("skripsi")
                .whereEqualTo("npm", npm)
//                .whereEqualTo("status", Constant.PENDING)
                //.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                taskDocumentPath = task.getResult().getDocuments().get(0).getReference().getPath();

                                Skripsi skripsi = task.getResult().getDocuments().get(0).toObject(Skripsi.class);
                                if (skripsi != null) {
                                    bindThesisUI(skripsi);
                                }
                            } else {
                                Toast.makeText(LecturerThesisDetailActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });
    }

    private void getStudentByNpm(final String npm) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("mahasiswa")
                .whereEqualTo("npm", npm)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                Mahasiswa student = task.getResult().getDocuments().get(0).toObject(Mahasiswa.class);
                                if (student != null) {
                                    bindStudentUI(student);
                                }
                            } else {
                                Toast.makeText(LecturerThesisDetailActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
    }

    private void bindStudentUI(Mahasiswa student) {
        TextView tvNpm = findViewById(R.id.tvNpm);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvSemester = findViewById(R.id.tvSemester);

        tvNpm.setText(student.getNpm());
        tvName.setText(student.getNama());
        tvSemester.setText("" + student.getSemester());
    }

    private void bindThesisUI(Skripsi skripsi) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        TextView tvPembimbing = findViewById(R.id.tvPembimbing);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);

        tvTitle.setText(skripsi.getJudul());
        tvSubtitle.setText(skripsi.getJenis_skripsi());
        tvPembimbing.setText(skripsi.getNama_pembimbing());
        tvStatus.setText(skripsi.getStatus());
        tvDescription.setText(skripsi.getMasalah());
    }

    private void initDropdownStatus() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        STATUS);

        dropdownStatus =
                findViewById(R.id.dropdown_status);
        dropdownStatus.setAdapter(adapter);
        dropdownStatus.setText(adapter.getItem(0));
    }
}
