package com.example.aslapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

//Page showing gesture video and allowing users to practice and upload
public class LearnActivity extends AppCompatActivity {

    VideoView video;
    String path;//To get video from list
    String uid;

    File to;
    String p;//To store path of recorded video

    String g;//Stores the gesture chosen by the user

    Button update;
    String status;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static int num;//To store number of attempts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        uid = getIntent().getStringExtra("ID");

        TextView ges = findViewById(R.id.gesture);
        g = getIntent().getStringExtra("gesture");
        ges.setText("Showing gesture: " + g);

        play_video(g);

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(to == null)
            play_video(g);
        else{
            video.setVideoPath(to.getAbsolutePath());
            video.start();
        }
    }

    //Choose video to play
    public void play_video(String text) {
        //old_text = text;
        video = findViewById(R.id.vv_video_learn);
        if(text.equals("Buy")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.buy;
        } else if(text.equals("House")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.house;
        } else if (text.equals("Fun")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.fun;
        }else if (text.equals("Hope")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.hope;
        }else if (text.equals("Arrive")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.arrive;
        }else if (text.equals("Really")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.really;
        }else if (text.equals("Read")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.read;
        }else if (text.equals("Lip")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.lip;
        }else if (text.equals("Mouth")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.mouth;
        }else if (text.equals("Some")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.some;
        }else if (text.equals("Communicate")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.communicate;
        }else if (text.equals("Write")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.write;
        }else if (text.equals("Create")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.create;
        }else if (text.equals("Pretend")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.pretend;
        }else if (text.equals("Sister")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.sister;
        }else if (text.equals("Man")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.man;
        }else if (text.equals("One")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.one;
        }else if (text.equals("Drive")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.drive;
        }else if (text.equals("Perfect")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.perfect;
        }else if (text.equals("Mother")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.mother;
        }
        if(!path.isEmpty()) {
            Uri uri = Uri.parse(path);
            video.setVideoURI(uri);
            video.start();
        }
    }

    //Get path of file from content URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void pracGes(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            update = findViewById(R.id.uploadBut);
            update.setVisibility(View.VISIBLE);

            Uri videoUri = intent.getData();
            p = getRealPathFromURI(videoUri);

            String g = getIntent().getStringExtra("gesture");

            File from = new File(p);
            String onlyPath = from.getParentFile().getAbsolutePath();
            SharedPreferences mPrefs = getPreferences(0);
            String key = uid + "_" + g;
            num = mPrefs.getInt(key, 1);
            String newPath = onlyPath + "/" + g + "_" + num + "_" + uid + ".mp4";
            num++;
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putInt(key, num).commit();
            to = new File(newPath);
            Boolean re = from.renameTo(to);

            TextView vid = findViewById(R.id.recFile);
            vid.setText(to.toString());
            video.setVideoPath(to.getAbsolutePath());
            video.start();

        }
    }

    //Function for video upload
    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(LearnActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                Log.i("Info", status);
                if(status == "Could not upload"){
                    Toast.makeText(getApplicationContext(), "Failed to upload the video. Please check internet connection or try again.", Toast.LENGTH_LONG).show();
                }
                else{
                    //Redirect to MainActivity
                    Intent intent = new Intent(LearnActivity.this, MainActivity.class);
                    intent.putExtra("ID", uid);
                    Toast.makeText(getApplicationContext(),"Upload successful. Returning to main page", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(to.getAbsolutePath(), uid);
                Log.i("Info", "Upload status: " + msg);
                status = msg;
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }


    public void upSer(View view) {
        String fileName = to.getAbsolutePath().substring(to.getAbsolutePath().lastIndexOf("/"));
        fileName = fileName.substring(1);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            //@Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        uploadVideo();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to upload the file " + fileName + "?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}


