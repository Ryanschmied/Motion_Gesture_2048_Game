package ca.uwaterloo.ece155_nlab4;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

import lab4_204_01.uwaterloo.ca.lab4_204_01.R;

public class Lab4_204_01 extends AppCompatActivity {
    // THRESHOLD Constants stored in arrays
    //Index[0]=THRES_A1
    //Index[1]=THRES_A2
    //Index[2]=THRES_A3
    //Index[3]=THRES_B1
    //Index[4]=THRES_B2
    //Index[5]=THRES_B3

    final float[] X_THRES = {(float)0.7,(float)3,(float)-0.3,(float)-0.7,(float)-3,(float)0.3};
    final float[] Y_THRES = {(float)1,(float)2,(float)-0.2,(float)-1,(float)-2,(float)0.2};
    final public int GAMEBOARD_DIMENSION = 1000;

    finiteStateMachine fsmX;
    finiteStateMachine fsmY;

    File file = null;
    PrintWriter prt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2_204_01);

        //Get the layout object
        RelativeLayout r1 = (RelativeLayout) findViewById(R.id.label2);

        r1.setBackgroundResource(R.drawable.gameboard);
        r1.getLayoutParams().width = GAMEBOARD_DIMENSION; //gameboard size
        r1.getLayoutParams().height = GAMEBOARD_DIMENSION;

        TextView latestGestureTV = new TextView(getApplicationContext());
        latestGestureTV.setText("LATEST GESTURE");
        latestGestureTV.setTextSize(50);
        r1.addView(latestGestureTV);
        latestGestureTV.setTextColor(Color.CYAN);

        TextView winLossTV = new TextView(getApplicationContext());
        winLossTV.setText("Game in progress");
        winLossTV.setTextSize(50);
        r1.addView(winLossTV);
        winLossTV.setY(500);
        winLossTV.setTextColor(Color.CYAN);

        //Create instance of the Finite State Machine
        fsmX = new finiteStateMachine(X_THRES);
        fsmY = new finiteStateMachine(Y_THRES);

        // Create instance of a timer and a gamelooptask
        Timer gameLoopTimer = new Timer();
        GameLoopTask gameLoopTask = new GameLoopTask(this, getApplicationContext(), r1, winLossTV);

        //Initializing Sensors and registering them as listeners
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        SensorEventListener sel_accelerometer = new AccelerometerSensorEventListener(fsmX, fsmY, latestGestureTV, gameLoopTask);
        sensorManager.registerListener(sel_accelerometer,accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        gameLoopTimer.schedule(gameLoopTask, 50, 25);

    }
    //Write the past 100 accelerometer readigs to  Output.txt once button is pressed
    private void writeToFile(float[][] savedValues, int startIndex){

        try{
            file = new File(getExternalFilesDir("AccelerometerReadings"), "Output.csv");
            prt = new PrintWriter(file);

            for (int i=0; i<100; i++) {
                prt.println(String.format("%f,%f,%f", savedValues[(startIndex + i) % 100][0], savedValues[(startIndex + i) % 100][1], savedValues[(startIndex + i) % 100][2]));
            }
        }catch(IOException e){
            //Log.e("Failed");
        }finally{
            if(prt != null){
                prt.close();
            }
            //Log.d("Ended");

        }
    }
}
