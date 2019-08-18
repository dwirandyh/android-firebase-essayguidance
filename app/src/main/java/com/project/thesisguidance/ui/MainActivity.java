package com.project.thesisguidance.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.thesisguidance.R;
import com.project.thesisguidance.ui.lecturer.LecturerLoginActivity;
import com.project.thesisguidance.ui.lecturer.LecturerTaskActivity;
import com.project.thesisguidance.ui.student.GuidanceActivity;
import com.project.thesisguidance.ui.student.StudentLoginActivity;
import com.project.thesisguidance.utils.Constant;
import com.project.thesisguidance.utils.SharedPreferenceHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        checkLoginState();
    }

    private void initView(){
        Button btnStudent = findViewById(R.id.btnStudent);
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
                startActivity(intent);
            }
        });

        Button btnLecturer = findViewById(R.id.btnLecture);
        btnLecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LecturerLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkLoginState(){
        String loggedStudent = SharedPreferenceHelper.getString(this, Constant.LOGGED_STUDENT_ID);
        if (!loggedStudent.isEmpty()){
            Intent intent = new Intent(this, GuidanceActivity.class);
            startActivity(intent);
        }

        String loggedLecturer = SharedPreferenceHelper.getString(this, Constant.LOGGED_LECTURER_ID);
        if (!loggedLecturer.isEmpty()){
            Intent intent = new Intent(this, LecturerTaskActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}
