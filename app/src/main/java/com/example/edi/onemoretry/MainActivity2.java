package com.example.edi.onemoretry;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity2 extends Activity {
    public static final Object LOCK = new Object();
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Thread myThread;
    byte[] readBuffer;
    int readBufferPosition;
    ArrayList<String> lista;
    volatile boolean stopWorker;
    boolean rezultat;
    int i;
    int h, j;
    byte vector[];
    ArrayList<Byte> biti1;
    boolean boolRead;
    boolean stare;
    final Handler handler = new Handler();
    final Handler handler1 = new Handler();
    Handler h2 = new Handler();
    Handler h3 = new Handler();
    int timp = 100;
    CountDownTimer ct1;
    Thread t1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
        Button rezultat = findViewById(R.id.rezultat);
        Button show = findViewById(R.id.showme);
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);
        vector = new byte[300];
        i = 0;
        boolRead = false;
        lista = new ArrayList<>();

        biti1 = new ArrayList<>();
        ct1 = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                timp = 0;
            }
        };


        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (Looper.myLooper() != Looper.getMainLooper()) {
                                Log.d("Looper", "nu este in acelasi thread");
                            }
                            findBT();
                            // openBT();
                        } catch (Exception ex) {
                        }
                    }
                }).start();
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mmDevice != null)
                            myLabel.setText("Connected to " + mmDevice.getName());
                    }
                }, 200);

            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              t1 = new Thread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      try {


                                                          enterServiceMode();
                                                      } catch (IOException ex) {
                                                      }
                                                  }
                                              });
                                              t1.start();


                                          }

                                      }
        );

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMe();
            }
        });


        rezultat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainBoolean();
            }
        });


    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null) // bluedevice device , declarat dincolo
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        // myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        // myLabel.setText("Bluetooth Opened");

    }

    public synchronized void beginListenForData() {


        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        workerThread = new Thread(new Runnable() {
            public void run() {

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];

                            mmInputStream.read(packetBytes);


                            for (Byte b : packetBytes) {

                                biti1.add(b);


                            }

                            Log.d("da", "obtain bool" + (h++) + "ori");
                            // obtainBoolean
                            //lista.add(sb.toString());
//
//                             Log.d("thrad", ""+bytesAvailable + "[" + sb.toString() + "]");
//                            Log.d("thrd","[" + biti1+ "]");

//                            for(int i=0;i<bytesAvailable;i++)
//                            {
//                                byte b = packetBytes[i];
//                                if(b == delimiter)
//                                {
//                                    byte[] encodedBytes = new byte[readBufferPosition];
//                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                    final String data = new String(encodedBytes, "US-ASCII");
//                                    readBufferPosition = 0;
//
//                                    handler.post(new Runnable()
//                                    {
//                                        public void run()
//                                        {
//                                            myLabel.setText(data);
//                                        }
//                                    });
//                                }
//                                else
//                                {
//                                    readBuffer[readBufferPosition++] = b;
//                                }
//                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public synchronized void sendData(byte[] data) throws IOException {

        try {
            mmOutputStream.write(data);
        } catch (Exception e) {
            Log.e("sendData with " + data + "failed : ", e.getMessage());
        }


//

    }

    void showMe() throws IndexOutOfBoundsException {
//        if(biti1!=null)
//       for (int i = 0; i < Constants_EnterServiceMode_TX_RX.rxEE1.length; i++)
//            if (Constants_EnterServiceMode_TX_RX.rxEE1[i]!=(biti1.get(i)))
//                Toast.makeText(this, "nu sunt egale", Toast.LENGTH_SHORT).show();
//
//            else
//                Toast.makeText(this, "sunt egale", Toast.LENGTH_LONG).show();
        //   ArrayList<Byte> listabiti = new ArrayList<>(Arrays.asList(Constants_EnterServiceMode_TX_RX.rxEE1));
//        if(biti1!=null){
//            boolean b = Arrays.equals(Constants_EnterServiceMode_TX_RX.rxEE1, new ArrayList[]{biti1});
//            Toast.makeText(this,""+b,Toast.LENGTH_LONG).show();}
        Toast.makeText(this, "" + boolRead, Toast.LENGTH_LONG).show();

    }

    boolean obtainBoolean() {
        boolRead = false;


        h2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!biti1.isEmpty())
                    if (biti1.get(0) == (byte) 0x7E && biti1.get(biti1.size() - 1) == 0x7E && biti1.size() == Constants_EnterServiceMode_TX_RX.rxEE1.size())
                        boolRead = true;
                Toast.makeText(MainActivity2.this, "" + boolRead + " " + biti1.size() + " " + Constants_EnterServiceMode_TX_RX.rxEE1.size(), Toast.LENGTH_LONG).show();

            }
        }, 300);


        return boolRead;
    }


    void enterServiceMode() throws IOException {
        biti1 = new ArrayList<>();
        MyRunnable r = new MyRunnable();
        Thread t = new Thread(r);
        t.start();

        synchronized (LOCK) {
            try {
                sendData(Constants_EnterServiceMode_TX_RX.txEE11);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendData(Constants_EnterServiceMode_TX_RX.txEE12);
                        } catch (IOException e) {
                            Toast.makeText(MainActivity2.this, "Data Could not be send", Toast.LENGTH_LONG).show();
                        }
                    }
                }, 450);

            } catch (Exception e) {

            }
            Log.d("SendLog", "Now we need to recieve.");
            try {
                int bytesAvailable = mmInputStream.available();
                if (bytesAvailable > 0) {
                    byte[] packetBytes = new byte[bytesAvailable];

                    mmInputStream.read(packetBytes);


                    for (Byte b : packetBytes) {

                        biti1.add(b);

                    }
                }
            } catch (Exception e) {
                Log.d("FinalLogish", "Some final message");
            }

//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        sendData(Constants_EnterServiceMode_TX_RX.txEE12);
//                    } catch (Exception e) {
//                        Log.e("sendData1", e.getMessage());
//                    }
//
//                    myLabel.setText("Data Sent");
//                }
//
//            }, 1000);   // 1 sec in plm
//
//
//            handler1.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        stare = obtainBoolean();
//
//                    } catch (Exception e) {
//                        Log.e("sendData1", e.getMessage());
//                    }
////
//
//                }
//
//            }, 1000);   // 1 sec in plm
//
//            //thread pt while
//
//            while(stare){}
//            {
//
//
//                h2.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            if (stare == true) {
//                                sendData(Constants_EnterServiceMode_TX_RX.txEE2);
//                                Toast.makeText(MainActivity2.this, "data 2 a fost trimisa", Toast.LENGTH_LONG).show();
//                                stare = false;
//                            }
//                        } catch (Exception e) {
//                            Log.e("sendData1", e.getMessage());
//                        }
//                    }
//                }, 1000);
//
//
//            }


            ct1.start();
            stare = obtainBoolean();
// countdown timer in while  , de 5 secunde pe care il pornesc cand dupa ultima comanda


            while (timp == 0 || !stare) {
                enterServiceMode();
            }


//            handler1.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stare = obtainBoolean();
//                    if (stare == true) {
//                        Toast.makeText(MainActivity2.this,"correct",Toast.LENGTH_LONG).show();
////                        stare = false;
////                        try {
////                            sendData(Constants_EnterServiceMode_TX_RX.txEE2);
////                        } catch (IOException e) {
////                            Toast.makeText(MainActivity2.this, "txee2 nu s-a trimis", Toast.LENGTH_LONG).show();
////                        }
//                    }
//                }
//            }, 1300);
//
//            if(stare == false){
//                sendData(Constants_EnterServiceMode_TX_RX.txEE11);
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            sendData(Constants_EnterServiceMode_TX_RX.txEE12);
//                        } catch (IOException e) {
//                            Toast.makeText(MainActivity2.this, "Data Could not be send", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }, 1300);
//            }
//
//            handler1.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stare = obtainBoolean();
//                    if (stare == true) {
//                        Toast.makeText(MainActivity2.this,"correct",Toast.LENGTH_LONG).show();
////                        stare = false;
////                        try {
////                            sendData(Constants_EnterServiceMode_TX_RX.txEE2);
////                        } catch (IOException e) {
////                            Toast.makeText(MainActivity2.this, "txee2 nu s-a trimis", Toast.LENGTH_LONG).show();
////                        }
//                    }
//                }
//            }, 1300);

        }}


        void closeBT () throws IOException
        {

            stopWorker = true;
            if (mmOutputStream != null)
                mmOutputStream.close();
            if (mmInputStream != null)
                mmInputStream.close();
            if (mmSocket != null)
                mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        }
    }