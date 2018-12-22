package com.example.tech2k8.musicplayerdemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView musicList;
    private ArrayList<String> nameArr,pathArr;
    private Button save;
    private EditText msg;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicList=findViewById(R.id.music_list);
        save=findViewById(R.id.save);
        msg=findViewById(R.id.msg);
        preferences =getSharedPreferences("settings",MODE_PRIVATE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMsg =msg.getText().toString();

                SharedPreferences.Editor editor =preferences.edit();
                editor.putString("welcome_message",userMsg).commit();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            showPremissionDialog();
        }
        else
        {
            loadAllMusicFiles();
        }

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent playerAct =new Intent(MainActivity.this,PlayerActivity.class);
                playerAct.putExtra("music_path",pathArr.get(position));
                startActivity(playerAct);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWelcomeDialog();
    }

    private void showPremissionDialog(){
        String permissionList[]={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(MainActivity.this,
                permissionList,1001
                );
    }


    private void showWelcomeDialog()
    {
        final Dialog welcomeDialog = new Dialog(MainActivity.this);
        View dialogView=LayoutInflater.from(MainActivity.this).inflate(R.layout.welcome_dialog,null,false);
        welcomeDialog.setContentView(dialogView);
        Button cancel =dialogView.findViewById(R.id.cancel);
        TextView label =dialogView.findViewById(R.id.label);
        String prefData =preferences.getString("welcome_message","No offer found");
        label.setText(prefData);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeDialog.dismiss();
            }
        });

        welcomeDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1001)
        {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                loadAllMusicFiles();
            }
            else {
                Toast.makeText(this, "Access restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAllMusicFiles()
    {
         nameArr = new ArrayList();
         pathArr = new ArrayList();
        String projection[]={MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA
        };

        Cursor cursorData=getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,null
        );

        if (cursorData!=null)
        {
            if (cursorData.moveToFirst())
            {
                do{
                    nameArr.add(cursorData.getString(0));
                    pathArr.add(cursorData.getString(1));
                }while (cursorData.moveToNext());
                ArrayAdapter adapter =new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1,nameArr);
                musicList.setAdapter(adapter);
            }else
            {
                Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
