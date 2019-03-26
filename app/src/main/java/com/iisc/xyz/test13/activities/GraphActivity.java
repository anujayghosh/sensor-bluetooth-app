package com.iisc.xyz.test13.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.iisc.xyz.test13.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    BarChart barChart;
    //TextInputEditText fileName_et;
    //Button createGraphBtn;
    ArrayList<String> xAXES;
    ArrayList<BarEntry> yAXES;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        barChart = findViewById(R.id.chart1);
        barChart.getDescription().setEnabled(false);

        //fileName_et=findViewById(R.id.et_fileName);

        //createGraphBtn = findViewById(R.id.createGraphBtn);

        xAXES=new ArrayList<>();
        yAXES=new ArrayList<>();

        Intent intent = getIntent();
        username = intent.getExtras().getString("name");

        makeGraph();

    }

    //TextView tv;

    public void makeGraph() {

       // tv=findViewById(R.id.textView);
       // tv.setMovementMethod(new ScrollingMovementMethod());

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder text = new StringBuilder();
        //String fileN = fileName_et.getText().toString().trim()+".txt";
        int k=0;boolean flag=false;



//Get the text file
        String filepath = "/Unilever/ReceivedData/"+username+"/";
        File folder = new File(sdcard+filepath);
        if(folder.exists()) {
            //Toast.makeText(this, "Directory exists!", Toast.LENGTH_SHORT).show();
//Read text from file
            File file = new File(folder, "data.txt");
            if(file.exists()) {
                //Toast.makeText(this, "File found!", Toast.LENGTH_SHORT).show();
                flag=true;

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {

                        /*if(k==-1)
                        {
                            k++;
                            continue;
                        }*/
                        String[] words=line.split("\\s+");
                        float yvalues=Float.parseFloat(words[0]);

                        yAXES.add(new BarEntry(Float.parseFloat(String.valueOf(k)), yvalues));
                        xAXES.add(k, String.valueOf(k));




                        text.append("X="+k+" and Y="+words[1]);
                        text.append('\n');

                        k++;
                    }
                    br.close();
                } catch (IOException e) {
                    Toast.makeText(this, "Error: IOException occurred", Toast.LENGTH_SHORT).show();
                    //You'll need to add proper error handling here
                }
            }
            else
            {
                Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, "Directory Not Found", Toast.LENGTH_SHORT).show();
        }
        //tv.setText(text);
        if(flag) {
            String[] xaxes = new String[xAXES.size()];
            for (int i = 0; i < xAXES.size(); i++) {
                xaxes[i] = xAXES.get(i);
            }

            BarDataSet dataset = new BarDataSet(yAXES, "Concentration");
            BarData barData = new BarData(dataset);

            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.invalidate();

          /*  ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            LineDataSet lineDataSet = new LineDataSet(yAXES, "Sensor Data");
            lineDataSet.setDrawCircles(false);
            lineDataSet.setColor(Color.BLUE);

            dataSets.add(lineDataSet);

            lineChart.setData(new LineData(dataSets));
            lineChart.invalidate();*/
        }
        //lineChart.setVisibleXRangeMaximum(100f);

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
