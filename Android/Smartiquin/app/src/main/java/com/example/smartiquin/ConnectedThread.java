package com.example.smartiquin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectedThread extends Thread {

    private OutputStream mmOutStream;
    private BluetoothAdapter bluetoothAdapter;
    public BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream tmpOut = null;
    private String dirMAC = "00:18:E4:35:5A:64";
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectedThread() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothDevice = bluetoothAdapter.getRemoteDevice(dirMAC);

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Create I/O streams for connection
            tmpOut = bluetoothSocket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mmOutStream = tmpOut;

    }

    @Override
    public void run() {
        //Inicio la conexion con el arduino

        try {
            bluetoothSocket.connect();

        } catch (Exception e) {
            try {
                bluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
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