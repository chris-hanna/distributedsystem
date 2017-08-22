package com.command.chris.connector;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPCommunication extends AsyncTask<Void, Void, Void> {
    private String addr, message;
    private int port;

    public UDPCommunication(String ip, int p, String msg) {
        addr = ip;
        port = p;
        message = msg;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            InetAddress address = InetAddress.getByName(addr);

            byte[] buffer = message.getBytes();

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            socket.connect(address, port);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            Log.d("UDP send", address.getHostAddress() + ":" + port + " " + message);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            Log.d("broadcast", e.toString());
        }
        return null;
    }
}
