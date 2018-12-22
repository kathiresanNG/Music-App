package com.example.tech2k8.musicplayerdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    String musicPath;
    int i=0;
    private Button stopBtn,camBtn;
    private ImageView camImage;
    private SeekBar playingIndicator;
    private Handler handler;
    private Runnable runnable;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        stopBtn=findViewById(R.id.stop_player);
        camBtn=findViewById(R.id.cam);
        camImage=findViewById(R.id.cam_output);
        playingIndicator=findViewById(R.id.player_progress);
        musicPath=getIntent().getStringExtra("music_path");
        Log.i("PlayerActivity","path "+musicPath);


        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,1002);
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1002)
        {
            if (resultCode==RESULT_OK)
            {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                camImage.setImageBitmap(image);
            }
        }
    }

    private void showDialog()
    {
        AlertDialog.Builder builder =new AlertDialog.Builder(PlayerActivity.this);
        builder.setTitle("Conformation Dialog");
        builder.setMessage("Are you sure. Do you really want to stop awesome music");
        builder.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                player.pause();
                handler.removeCallbacks(runnable);
            }
        });


        builder.setNegativeButton("No, Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PlayerActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();

    }
    @Override
    protected void onResume() {
        super.onResume();


        handler =new Handler();
        runnable =new Runnable() {
            @Override
            public void run() {


                playingIndicator.setProgress(i);
                i++;
                handler.postDelayed(runnable,1000);
            }
        };

        initMusicPlayer();
    }

    private void initMusicPlayer()
    {
        player =new MediaPlayer();
        try {
            player.setDataSource(musicPath);
            player.prepare();
            player.start();
            handler.post(runnable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("PlayerActivity","error "+e.getMessage());
            Toast.makeText(this, "Unable to play music files", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleProgress()
    {

    }
}
