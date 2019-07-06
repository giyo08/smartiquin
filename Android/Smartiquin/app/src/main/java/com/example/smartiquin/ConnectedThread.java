package com.example.smartiquin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class ConnectedThread extends Thread{

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BluetoothAdapter bluetoothAdapter;
    public BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    public ConnectedThread (){

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        String dirMAC = "00:18:E4:35:5A:64";
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        bluetoothDevice = bluetoothAdapter.getRemoteDevice(dirMAC);

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try
        {
            //Create I/O streams for connection
            tmpIn = bluetoothSocket.getInputStream();
            tmpOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

    }

    public void run()
    {

        //Si el bluetooth esta activado , inicio la conexion con el arduino

        if(bluetoothAdapter.isEnabled()) {

            try {
                bluetoothSocket.connect();
            } catch (Exception e) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

            try {
                Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(bluetoothDevice, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    public void write(String input) {
        byte[] msgBuffer = input.getBytes();           //converts entered String into bytes

        try {
            mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
