package com.iisc.xyz.test13;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "UnileverAPP";

    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter bluetoothAdapter;
    Context context;

    private AcceptThread acceptThread;

    private ConnectThread connectThread;
    private BluetoothDevice device;
    private UUID deviceUUID;
    ProgressDialog progressDialog;

    ConnectedThread connectedThread;


    public BluetoothConnectionService(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        start();
    }



    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "Setting upp the server socket using UUID: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException. " + e.getMessage());
            }
            serverSocket = tmp;

        }

        public void run() {
            Log.d(TAG, "run(): AcceptThread running.");

            BluetoothSocket socket = null;
            try {
                Log.d(TAG, "run(): RFCOM server socket start..");

                socket = serverSocket.accept();

                Log.d(TAG, "run(): RFCOM server accepted connection.");
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException." + e.getMessage());
            }

            if (socket != null) {
                connected(socket, device);
            }

            Log.d(TAG, "AcceptThread: END");
        }

        public void cancel() {
            Log.d(TAG, "cancel(): Cancelling AcceptThread.");
            try {
                serverSocket.close();

            } catch (IOException e) {

                Log.d(TAG, "AcceptThread: IOException." + e.getMessage());
            }
        }
    }


    private class ConnectThread extends Thread{

            private BluetoothSocket socket;

            public ConnectThread(BluetoothDevice devicex, UUID uuid)
            {
                Log.d(TAG, "ConnectThread: STARTED");
                device= devicex;
                deviceUUID=uuid;
            }

            public void run() {
                BluetoothSocket tmp = null;
                Log.d(TAG, "ConnectThread: RUN");

                try {
                    Log.d(TAG, "ConnectThread: Trying to create insecure RFCOM socket ");
                    tmp = device.createRfcommSocketToServiceRecord(deviceUUID);
                } catch (IOException e) {
                    Log.d(TAG, "ConnectThread: Could not create Insecure RFCOM socket." + e.getMessage());

                }
                socket = tmp;

                bluetoothAdapter.cancelDiscovery();

                try {

                    socket.connect();
                    Log.d(TAG, "run(): Connected Successfully.");
                } catch (IOException e) {
                    try {
                        socket.close();
                        Log.d(TAG, "run(): Socket closed");
                    } catch (IOException e1) {
                        Log.d(TAG, "run: IOException. " + e1.getMessage());

                    }
                    Log.d(TAG, "run(): Could not connect.");
                }
                connected(socket, device);
            }

            public void cancel(){
                Log.d(TAG, "cancel(): Closing Client socket.");
                try {
                    socket.close();

                } catch (IOException e) {

                    Log.d(TAG, "cancel(): close() of  socket in ConnectThread failed." + e.getMessage());
                }
            }

        }

        public synchronized void start()
        {
            Log.d(TAG, "start.");

            if(connectThread != null)
            {
                connectThread.cancel();
                connectThread=null;
            }
            if(acceptThread == null){
                acceptThread = new AcceptThread();
                acceptThread.start();
            }

        }

        public void startClient(BluetoothDevice devicex, UUID uuid)
        {
            Log.d(TAG, "startClient(): Started.");

            progressDialog = ProgressDialog.show(context,"Connecting Bluetooth",
                    "Please Wait...",true);

            connectThread = new ConnectThread(devicex, uuid);
            connectThread.start();


        }

    private class ConnectedThread extends Thread{

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socketx) {
            Log.d(TAG, "ConnectedThread: Starting.");

            socket = socketx;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progressdialog when connection is established
            try{
                progressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }


            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(incomingMessageIntent);

                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        connectedThread.write(out);
    }
}









