package com.project.thesisguidance.ui.student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.thesisguidance.R;
import com.project.thesisguidance.adapter.GuidanceAdapter;
import com.project.thesisguidance.adapter.StudentGuidanceAdapter;
import com.project.thesisguidance.model.Mahasiswa;
import com.project.thesisguidance.model.Bimbingan;
import com.project.thesisguidance.model.Skripsi;
import com.project.thesisguidance.ui.MainActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

import java.util.ArrayList;

public class GuidanceActivity extends AppCompatActivity {

    private static final String TAG = "GuidanceActivity";

    Skripsi skripsi = new Skripsi();

    StudentGuidanceAdapter adapter = new StudentGuidanceAdapter();

    FloatingActionButton buttonAddTask;
    LinearLayout layoutAddThesis;
    CardView cardViewSkripsi;
    CardView cardViewBimbingan;

    ProgressDialog dialog;
    RecyclerView rvTask;

    View layoutThesis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        String studentId = SharedPreferenceHelper.getString(this, Constant.STUDENT_ID);

        initView();
        getSkripsiByNpm(studentId);
        getStudentById(studentId);
    }

    private void initView() {
        LinearLayout buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setVisibility(View.VISIBLE);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceHelper.removeString(GuidanceActivity.this, Constant.LOGGED_STUDENT_ID);
                SharedPreferenceHelper.removeString(GuidanceActivity.this, Constant.LOGGED_LECTURER_ID);
                Intent intent = new Intent(GuidanceActivity.this, MainActivity.class);
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

        layoutAddThesis = findViewById(R.id.layoutAddThesis);
        layoutAddThesis.setVisibility(View.INVISIBLE);

        layoutThesis = findViewById(R.id.layoutThesis);
        layoutThesis.setVisibility(View.INVISIBLE);

        cardViewSkripsi = findViewById(R.id.cardViewSkripsi);
        cardViewSkripsi.setVisibility(View.INVISIBLE);

        cardViewBimbingan = findViewById(R.id.cardViewBimbingan);
        cardViewBimbingan.setVisibility(View.INVISIBLE);

        Button btnAddThesis = findViewById(R.id.btnAddThesis);
        btnAddThesis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addThesisActivity();
            }
        });


        initTask();
    }


    private void initTask() {
        rvTask = findViewById(R.id.rvTask);
        rvTask.setVisibility(View.INVISIBLE);
        rvTask.setLayoutManager(new LinearLayoutManager(this));

        rvTask.setAdapter(adapter);
    }

    private void addThesisActivity() {
        Intent intent = new Intent(this, AddThesisActivity.class);
        startActivityForResult(intent, Constant.REQUEST_ADD_THESIS);
    }

    private void getSkripsiByNpm(String npm) {
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Loading");
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("skripsi")
                .whereEqualTo("npm", npm)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                skripsi = task.getResult().getDocuments().get(0).toObject(Skripsi.class);

                                if (skripsi.getStatus().equals(Constant.PENDING)) {
                                    dialog.dismiss();
                                    bindThesisUI(skripsi);
                                } else {
                                    String idSkripsi = task.getResult().getDocuments().get(0).getId();
                                    skripsi.setId_skripsi(idSkripsi);

                                    SharedPreferenceHelper.putString(GuidanceActivity.this, Constant.ID_SKRIPSI, idSkripsi);

                                    bindAcceptedThesisUI(skripsi);

                                    getBimbinganByIdSkripsi(idSkripsi, skripsi.getNik(), skripsi.getNpm());
                                }
                            } else {
                                dialog.dismiss();

                                buttonAddTask.setVisibility(View.INVISIBLE);
                                rvTask.setVisibility(View.INVISIBLE);
                                layoutAddThesis.setVisibility(View.VISIBLE);
                            }
                        } else {
                            dialog.dismiss();

                            buttonAddTask.setVisibility(View.INVISIBLE);
                            rvTask.setVisibility(View.INVISIBLE);
                            layoutAddThesis.setVisibility(View.VISIBLE);
                            String errorMessage = task.getException().getMessage();
                            Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    private void bindAcceptedThesisUI(Skripsi skripsi){
        cardViewSkripsi.setVisibility(View.VISIBLE);
        cardViewBimbingan.setVisibility(View.VISIBLE);
        buttonAddTask.setVisibility(View.VISIBLE);
        layoutThesis.setVisibility(View.INVISIBLE);
        layoutAddThesis.setVisibility(View.VISIBLE);

        TextView tvJudul = findViewById(R.id.tvJudul);
        TextView tvPembimbing = findViewById(R.id.tvPembimbing);
        tvJudul.setText(skripsi.getJudul());
        tvPembimbing.setText(skripsi.getNama_pembimbing());
    }


    private void bindThesisUI(Skripsi skripsi) {
        cardViewSkripsi.setVisibility(View.INVISIBLE);
        cardViewBimbingan.setVisibility(View.INVISIBLE);
        buttonAddTask.setVisibility(View.INVISIBLE);
        layoutThesis.setVisibility(View.VISIBLE);
        layoutAddThesis.setVisibility(View.INVISIBLE);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvPembimbing = findViewById(R.id.tvPembimbing);

        if (skripsi.getJudul().length() <= 1){
            tvTitle.setText("BAB " + skripsi.getJudul());
        }else{
            tvTitle.setText(skripsi.getJudul());
        }
        tvSubtitle.setText(skripsi.getJenis_skripsi());
        tvStatus.setText(skripsi.getStatus());
        tvDescription.setText(skripsi.getMasalah());
        tvPembimbing.setText(skripsi.getNama_pembimbing() + " (" + skripsi.getNik() + ")");
    }

    private void getBimbinganByIdSkripsi(final String idSkripsi, final String nik, final String npm) {
        cardViewBimbingan.setVisibility(View.VISIBLE);
        rvTask.setVisibility(View.VISIBLE);
        buttonAddTask.setVisibility(View.VISIBLE);
        layoutAddThesis.setVisibility(View.INVISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bimbingan")
                .whereEqualTo("id_skripsi", idSkripsi)
                //.orderBy("createdAt", Query.Direction.DESCENDING)
                // event when document change for real time use
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                ArrayList<Bimbingan> studentTasks = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Bimbingan bimbingan = document.toObject(Bimbingan.class);
                                    bimbingan.setId_bimbingan(document.getId());
                                    bimbingan.setNik(nik);
                                    bimbingan.setNpm(npm);
                                    studentTasks.add(bimbingan);
                                }
                                adapter.setStudentTasks(studentTasks);

                            }
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    private void addTask() {
        Intent intent = new Intent(this, AddGuidanceActivity.class);
        intent.putExtra(Constant.LECTURER_ID, skripsi.getNik());
        startActivityForResult(intent, Constant.REQUEST_ADD_GUIDANCE);
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
                                Mahasiswa mahasiswa = task.getResult().getDocuments().get(0).toObject(Mahasiswa.class);
                                if (mahasiswa != null) {
                                    bindUIStudent(mahasiswa);
                                }
                            } else {
                                Toast.makeText(GuidanceActivity.this, "Failed get student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bindUIStudent(Mahasiswa mahasiswa) {
//        ImageButton buttonCloseMessage = findViewById(R.id.buttonCloseMessage);
//        buttonCloseMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //findViewById(R.id.includeWelcome).setVisibility(View.GONE);
//            }
//        });

        TextView tvNpm = findViewById(R.id.tvNpm);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvSemester = findViewById(R.id.tvSemester);

        tvNpm.setText(mahasiswa.getNpm());
        tvName.setText(mahasiswa.getNama());
        tvSemester.setText("" + mahasiswa.getSemester());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_ADD_GUIDANCE) {
            if (resultCode == RESULT_OK) {
                getBimbinganByIdSkripsi(skripsi.getId_skripsi(), skripsi.getNik(), skripsi.getNpm());
            }
        }

        if (requestCode == Constant.REQUEST_ADD_THESIS) {
            if (resultCode == RESULT_OK) {
                String npm = SharedPreferenceHelper.getString(this, Constant.STUDENT_ID);
                getSkripsiByNpm(npm);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}
