package com.example.smartiquin;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;

public class ConnectedThread extends Thread{

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    private Handler bluetoothIn;
    private Handler handler;

    public ConnectedThread (BluetoothSocket socket){

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try
        {
            //Create I/O streams for connection
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

    }

    public void run()
    {
    }

    public void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes

        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
        } catch (IOException e) {
        }

    }
}
