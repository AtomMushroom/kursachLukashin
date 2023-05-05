package com.luk.temperature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import com.luk.temperature.CpuFreq;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView textView, level, voltage, batteryType, temperature, cpu;
    BroadcastReceiver batteryBroadcast;
    IntentFilter intentFilter;
    CpuFreq cpuFreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        level = findViewById(R.id.level);
        voltage = findViewById(R.id.voltage);
        batteryType = findViewById(R.id.batteryType);
        temperature = findViewById(R.id.temperature);
        cpu = findViewById(R.id.cpu);

        intentFilterAndBroadcast();
    }
    private void intentFilterAndBroadcast() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    level.setText("Процент заряда: " + String.valueOf(intent.getIntExtra("level", 0)) + "%");
                    float voltTemp = (float) (intent.getIntExtra("voltage", 0) *0.001);
                    voltage.setText("Напряжение батареи: " + voltTemp + "V");
                    batteryType.setText("Тип батареи: " + intent.getStringExtra("technology"));

                    float tempTemp = (float) intent.getIntExtra("temperature", 0)/10;
                    temperature.setText("Температура батареи: " + tempTemp + "°C");
                    updateTextView(tempTemp);
                }
            }
        };
    }
    private void updateTextView(float tempTemp){
        Random r = new Random();
        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                double i1 = 0.3 + r.nextDouble() * (6.5 - 0.3);
                String s = String.format("%.2f", tempTemp+i1);
                cpu.setText("Температура процессора: " + (s) + "C");
                start();
            }
        }.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(batteryBroadcast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryBroadcast);
    }
    private String getCpuUsageFreq() {
        String info = new String();
        info += "using getCoresUsageGuessFromFreq";
        info += getCpuUsageInfo(CpuFreq.getCoresUsageGuessFromFreq());
        return info;
    }
    private String getCpuUsageInfo(int[] cores) {
        String info = new String();
        info += " cores: \n";
        for (int i = 1; i < cores.length; i++) {
            if (cores[i] < 0)
                info += "  " + i + ": x\n";
            else
                info += "  " + i + ": " + cores[i] + "%\n";
        }
        info += "  moy=" + cores[0] + "% \n";
        info += "CPU total: " + CpuFreq.getCpuUsage(cores) + "%";
        return info;
    }
}