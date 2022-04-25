package by.bsuir.relaxapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EyeTrainingActivity extends AppCompatActivity {

    public static int INACTIVITY_SECONDS = 5;
    Button beginButton;

    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRidOfTopBar();

        setContentView(R.layout.activity_eye_training);

        Spinner inactivitySpinner = findViewById(R.id.inactivitySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EyeTrainingActivity.this,
                R.array.inactivity_options_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inactivitySpinner.setAdapter(adapter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        beginButton = findViewById(R.id.beginButton);
        beginButton.setOnClickListener(lambda->{
            int pos = inactivitySpinner.getSelectedItemPosition();
            switch (pos){
                case 0:
                    INACTIVITY_SECONDS = 5;
                    break;
                case 1:
                    INACTIVITY_SECONDS = 10;
                    break;
                case 2:
                    INACTIVITY_SECONDS = 20;
                    break;
            }

            beginButton.setEnabled(false);

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(()->{
                startTracking();
            });
        });
    }

    private void startTracking() {
        long start = System.nanoTime();
        long elapsedTime = 0;
        while (true) {
            if (changeInAcceleration > 0.00000001) {
                start = System.nanoTime();
                elapsedTime = 0;
                continue;
            }
            long end = System.nanoTime();
            elapsedTime = end - start;


            long convert = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
            if (convert >= INACTIVITY_SECONDS) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EyeTrainingActivity.this, "Пора за тренировку глаз!", Toast.LENGTH_LONG).show();
                        beginButton.setEnabled(true);
                    }
                });
                return;
            }
        }
    }













    @Override
    protected  void onResume(){
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }





    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerationCurrentValue = Math.sqrt(x*x + y*y + z*z);
            changeInAcceleration = Math.abs(accelerationPreviousValue - accelerationCurrentValue);
            accelerationPreviousValue = accelerationCurrentValue;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {  }
    };


    public static double accelerationCurrentValue;
    public static double accelerationPreviousValue;
    public static double changeInAcceleration;

    SensorManager sensorManager;
    Sensor accelerometer;
}