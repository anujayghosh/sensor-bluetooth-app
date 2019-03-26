package com.iisc.xyz.test13.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iisc.xyz.test13.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveDataActivity extends AppCompatActivity {

    //TextInputEditText et_filename;
    TextView tv_fileBody;
    //Button saveFileBtn;
    String mess;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        //et_filename=findViewById(R.id.et_fileName);
        tv_fileBody=findViewById(R.id.tv_fileBody);
       //saveFileBtn=findViewById(R.id.saveFileBtn);

        Intent intent= getIntent();
        mess= intent.getExtras().getString("msg");
        username=intent.getExtras().getString("name");
        System.out.println(mess);
        tv_fileBody.setText(mess);

        tv_fileBody.setMovementMethod(new ScrollingMovementMethod());


        //Toast.makeText(this, "File will be saved to root directory.", Toast.LENGTH_SHORT).show();

        savefile();

    }

    private void saveTextAsFile(String fileName, String fileBody)
    {
        //fileName = fileName.trim()+".txt";
        fileBody=fileBody;

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Unilever/ReceivedData/"+username);
        boolean success;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }else {
            try {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());
                formattedDate="\t"+formattedDate+"\n";
                File file = new File(folder, fileName);
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(fileBody.getBytes());
                fos.write(formattedDate.getBytes());
                fos.close();
                Toast.makeText(this, "Entry Saved inside /Unilever/ReceivedData/"+username+"!", Toast.LENGTH_SHORT).show();
                finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: File not Found", Toast.LENGTH_SHORT).show();

            } catch (IOException e1) {
                e1.printStackTrace();
                Toast.makeText(this, "Error: IO Error saving", Toast.LENGTH_SHORT).show();
            }
        }

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
    float max,min,conc;
    public void savefile() {

        //String fileName=et_filename.getText().toString();
        String fileName="data.txt";

        String[] str = mess.split("\\n");
        String[] str1=str[0].split("\\s+");
        max=Float.parseFloat(str1[1]);
        min=Float.parseFloat(str1[1]);
        conc=max-min;

        for(int hh=1;hh<str.length;hh++)
        {
            String[] strn = str[hh].split("\\s+");
            float val=Float.parseFloat(strn[1]);
            if(val>max)
            {
                max=val;
            }
            if(val<min)
            {
                min=val;
            }
        }
        conc=max-min;
        String concStr = Float.toString(conc)+"  ppm";


            saveTextAsFile(fileName, concStr);


        /*if(!(fileName.equals("")) && !(mess.equals(""))) {
            saveTextAsFile(fileName, mess);
        }*/

    }
}
