package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.model.Dosen;
import com.project.thesisguidance.model.Lecturer;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AddThesisActivity extends AppCompatActivity {

    String[] JENIS = new String[]{"Skripsi", "Tugas Akhir"};
    String[] STATUS = new String[]{"Pending", "Diterima", "Ditolak"};
    ArrayList<Dosen> lecturerList = new ArrayList<>();

    TextInputLayout inputLayoutJudul;
    TextInputLayout inputLayoutMasalah;
    TextInputLayout inputLayoutPenguji;
    AutoCompleteTextView dropdownJenis;
    AutoCompleteTextView dropdownStatus;
    AutoCompleteTextView dropdownPembimbing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thesis);

        initView();
    }

    private void initView() {
        initDropdownJenis();
        initDropdownStatus();
        initDropdownPembimbing();

        inputLayoutJudul = findViewById(R.id.inputLayoutJudul);
        inputLayoutMasalah = findViewById(R.id.inputLayoutMasalah);
        inputLayoutPenguji = findViewById(R.id.inputLayoutPenguji);
        dropdownJenis = findViewById(R.id.dropdown_jenis_skripsi);
        dropdownStatus = findViewById(R.id.dropdown_status);
        dropdownPembimbing = findViewById(R.id.dropdown_pembimbing);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });
    }

    private void saveThesis(String jenis, String judul, String masalah, String pembimbing, String penguji, String nik, String npm, String status) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        HashMap<String, Object> formData = new HashMap<>();
        formData.put("jenis_skripsi", jenis);
        formData.put("judul", judul);
        formData.put("masalah", masalah);
        formData.put("nama_pembimbing", pembimbing);
        formData.put("nama_penguji", penguji);
        formData.put("nik", nik);
        formData.put("npm", npm);
        formData.put("status", status);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("skripsi")
                .add(formData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddThesisActivity.this, "Failed add new task, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateForm() {
        String jenis = dropdownJenis.getText().toString();
        String judul = inputLayoutJudul.getEditText().getText().toString();
        String masalah = inputLayoutMasalah.getEditText().getText().toString();
        String nama_pembimbing = dropdownPembimbing.getText().toString();
        String nama_penguji = inputLayoutPenguji.getEditText().getText().toString();
        String npm = SharedPreferenceHelper.getString(this, Constant.LOGGED_STUDENT_ID);
        String status = dropdownStatus.getText().toString();

        String nik = "";
        for (Dosen lecturer : lecturerList) {
            if (lecturer.getNama_dosen().equals(nama_pembimbing)) {
                nik = lecturer.getNik();
            }
        }

        boolean isValid = true;

        if (judul.isEmpty()) {
            inputLayoutJudul.setError("Judul harus diisi");
            isValid = false;
        }

        if (masalah.isEmpty()) {
            inputLayoutMasalah.setError("Permasalahan harus diisi");
            isValid = false;
        }

        if (nama_penguji.isEmpty()) {
            inputLayoutPenguji.setError("Nama Penguji harus diisi");
        }

        if (isValid) {
            saveThesis(jenis, judul, masalah, nama_pembimbing, nama_penguji, nik, npm, status);
        }
    }

    private void initDropdownJenis() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        JENIS);

        dropdownJenis =
                findViewById(R.id.dropdown_jenis_skripsi);
        dropdownJenis.setAdapter(adapter);
        dropdownJenis.setListSelection(0);
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


    private void initDropdownPembimbing() {
        dropdownPembimbing = findViewById(R.id.dropdown_pembimbing);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dosen")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                lecturerList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Dosen lecturer = document.toObject(Dosen.class);
                                    lecturerList.add(lecturer);
                                }

                                bindDropdownPembimbing();
                            }
                        }
                    }
                });
    }

    private void bindDropdownPembimbing() {
        ArrayList<String> lectureNames = new ArrayList<>();
        for (Dosen lecturer : lecturerList) {
            lectureNames.add(lecturer.getNama_dosen());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        lectureNames);

        dropdownPembimbing.setAdapter(adapter);

        if (lectureNames.size() > 0) {
            dropdownPembimbing.setText(adapter.getItem(0));
        }

    }
}