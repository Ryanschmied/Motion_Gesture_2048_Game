package ca.uwaterloo.ece155_nlab4;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

//Creating Accelermeter Sensor Listener

public class AccelerometerSensorEventListener implements SensorEventListener {
    TextView gestureTV;
    private GameLoopTask gameLoopTask;
    private boolean sendingInput = false;
    float[] filterValue = {0,0,0};
    float[] maxValue = {(float)0.0, (float)0.0, (float)0.0};
    static float[][] savedValues = new float[100][3];
    static int writeIndex = 0;
    final int c = 10;

    finiteStateMachine fsmX;
    finiteStateMachine fsmY;
    public AccelerometerSensorEventListener(
            finiteStateMachine stateMachineX,
            finiteStateMachine stateMachineY,
            TextView fsmResultTV,
            GameLoopTask _gameLoopTask){
        fsmX = stateMachineX;
        fsmY = stateMachineY;
        gestureTV = fsmResultTV;
        gameLoopTask = _gameLoopTask;
    }

    public void onAccuracyChanged(Sensor s, int i){}

    public void onSensorChanged(SensorEvent se){


        if(se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Calculate filtered value using new readings
           for(int i =0;i<3; i++) {
               filterValue[i] += (se.values[i] - filterValue[i]) / c;
           }

            // Update FSMs and check if they've determined an input gesture
            fsmX.update(filterValue[0]);
            fsmY.update(filterValue[1]);
            if ((fsmX.isDetermined() || fsmY.isDetermined()) && gameLoopTask.canReceiveInputs()) {
                determineGesture(fsmX.getType(), fsmY.getType());
            }

            //Check if Max Value must be updated
            if (vectorMagnitude(filterValue) > vectorMagnitude(maxValue)) {
                maxValue[0] = filterValue[0];
                maxValue[1] = filterValue[1];
                maxValue[2] = filterValue[2];
            }

            // Save filtered reading to data array
            saveValue(filterValue);
        }

    }

    private double vectorMagnitude (float[] vector1) {
        return Math.sqrt(Math.pow(vector1[0], 2) + Math.pow(vector1[1], 2) + Math.pow(vector1[2], 2));
    }

    void saveValue(float[] value) {
        for (int i=0; i<3; i++) {
            savedValues[writeIndex][i] = value[i];
        }
        writeIndex = ++writeIndex % 100;
    }

    public static int getWriteIndex() {
        return writeIndex;
    }

    public static float[][] getSavedValues() {
        return savedValues;
    }

    void determineGesture(finiteStateMachine.InputType X_TYPE, finiteStateMachine.InputType Y_TYPE) {
        switch (X_TYPE) {
            case TYPE_A:
                // Possible input to the right
//                Log.d("log", "FSMX type A");
                switch (Y_TYPE) {
                    case TYPE_X:
                        gestureTV.setText("RIGHT");
                        gameLoopTask.setDirection(GameLoopTask.DirectionType.RIGHT);
                        break;
                    default:
                        break;
                }
                break;
            case TYPE_B:
                // Possible input to the left
//                Log.d("log", "FSMX type B");

                switch (Y_TYPE) {
                    case TYPE_X:
                        gestureTV.setText("LEFT");
                        gameLoopTask.setDirection(GameLoopTask.DirectionType.LEFT);
                        break;
                    default:
                        break;
                }
                break;
            case TYPE_X:
                // Possible vertical input
//                Log.d("log", "FSMX type X");
                switch (Y_TYPE) {
                    case TYPE_A:
                        gestureTV.setText("UP");
                        gameLoopTask.setDirection(GameLoopTask.DirectionType.UP);
                        break;
                    case TYPE_B:
                        gestureTV.setText("DOWN");
                        gameLoopTask.setDirection(GameLoopTask.DirectionType.DOWN);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
