package com.iisc.xyz.test13.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iisc.xyz.test13.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileActivity extends AppCompatActivity {

    TextView tv_content;
    //Button openBtn;
    //TextInputEditText filename;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        tv_content =findViewById(R.id.tv_content);
        tv_content.setMovementMethod(new ScrollingMovementMethod());

        //filename=findViewById(R.id.et_fileName);

        //openBtn = findViewById(R.id.openBtn);

        Intent intent = getIntent();
        username=intent.getExtras().getString("name");

        readFile();
    }

    public void readFile() {
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder text = new StringBuilder();
        //String fileN = filename.getText().toString().trim()+".txt";
        String fileN="data.txt";

        int k=0;

//Get the text file
        String filepath= "/Unilever/ReceivedData/"+username+"/";
        File folder = new File(sdcard+filepath);
        if(folder.exists()) {
            Toast.makeText(this, "Directory exists!", Toast.LENGTH_SHORT).show();
//Read text from file
            File file = new File(folder, fileN);
            if(file.exists()) {
                Toast.makeText(this, "File found!", Toast.LENGTH_SHORT).show();


                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                       /* if(k==-1) {
                        k++;
                        continue;
                        }*/
                        String[] words = line.split("\\s+");
                       // for(int j=0;j<words.length;j++) {
                            text.append("X="+k+" and Y="+words[0]+" "+words[1]);
                       // }
                        text.append('\n');
                        k++;
                    }
                    br.close();
                } catch (IOException e) {
                    Toast.makeText(this, "IOError occurred", Toast.LENGTH_SHORT).show();
                    //You'll need to add proper error handling here
                }
            }
            else
            {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, "Directory not found", Toast.LENGTH_SHORT).show();
        }



//Set the text
        tv_content.setText(text.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1000:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}

