package com.example.edi.onemoretry;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MyRunnable implements Runnable {
    public static final MainActivity2 LOCK = new MainActivity2();

    @Override
    public void run() {
        Log.d("TagRun","Running");
        synchronized (LOCK)
        {
            Log.d("TagSynch","StartSynch");
            try {
                LOCK.notify();
                Thread.currentThread().sleep(5000);
            } catch(Exception e)
            {
                Log.d("TagCeva","Catch: "+e.getMessage());
            }
        }

        Log.d("AfterWar","Endish");

    }
}
