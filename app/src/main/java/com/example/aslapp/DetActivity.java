package com.example.aslapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//Front page asking user for ID
public class DetActivity extends AppCompatActivity {

    EditText id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_det);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
    }


    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(DetActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DetActivity.this,
                    new String[] { permission },
                    requestCode);
        }
//        else {
//            Toast.makeText(DetActivity.this,
//                    "Permission already granted",
//                    Toast.LENGTH_SHORT)
//                    .show();
//        }
    }

    public void openApp(View view) {
        id = findViewById(R.id.id);
        String uid = id.getText().toString();

        if(!uid.isEmpty()){
            Intent intent = new Intent(DetActivity.this, MainActivity.class);
            intent.putExtra("ID", uid);
            startActivity(intent);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You must enter an ID to proceed.").setPositiveButton("OK", null).show();
        }

    }
}
