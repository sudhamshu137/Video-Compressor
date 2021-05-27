package com.example.compressvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    VideoView v1, v2;
    TextView tv1, tv2, tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
    }

    public void button(View view){

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            selectVideo();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


    public void selectVideo(){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select a video"),100);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectVideo();
        }
        else{
            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null){

            Uri uri = data.getData();
            v1.setVideoURI(uri);

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

            new CompressVideo().execute("false",uri.toString(),file.getAbsolutePath());

        }

    }

    private class CompressVideo extends AsyncTask<String, String, String> {

        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(MainActivity.this, "", "Compressing... ");
        }

        @Override
        protected String doInBackground(String... strings) {

            String videoPath = null;

            Uri uri = Uri.parse(strings[1]);
            try {
                videoPath = SiliCompressor.with(MainActivity.this).compressVideo(uri, strings[2]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return videoPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();

            v1.setVisibility(View.VISIBLE);
            v2.setVisibility(View.VISIBLE);
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);

            File file = new File(s);

            Uri uri = Uri.fromFile(file);

            v2.setVideoURI(uri);

            v1.start();
            v2.start();

            float size = file.length()/1024f;
            tv3.setText(String.format("Size: %.2f KB", size));
        }
    }
}