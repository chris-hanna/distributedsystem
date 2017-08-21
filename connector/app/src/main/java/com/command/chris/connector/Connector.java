package com.command.chris.connector;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.ArrayAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Connector extends Activity {
    ListView lv;
    ArrayAdapter<Device> adapter;
    ArrayList<Device> listItems;
    ArrayList<String> listOps;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_broadcast);

        lv = findViewById(R.id.listview);
        listItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
                Device item = (Device)adapter.getItemAtPosition(position);
                Log.d("list", item.name + position);

                //expand the list here to clickable items that popup

                //EditText ops = findViewById(R.id.ops);
                //ops.setText(item.list.toString());

            }
        });

        TCPReceiver asyncTask1 = new TCPReceiver();
        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Button button = findViewById(R.id.button_broadcast);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UDPCommunication asyncTask2 = new UDPCommunication("255.255.255.255", 50000, "hello");
                asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //EditText ops = findViewById(R.id.ops);
                //ops.setText(e.toString());
                //setContentView(R.layout.content_manager);
            }
        });
    }

    private class UDPCommunication extends AsyncTask<Void, Void, Void>{
        private String addr, message;
        int port;

        public UDPCommunication(String ip, int p, String msg){
            addr = ip;
            port = p;
            message = msg;
        }

        @Override
        protected Void doInBackground(Void... params){
            try {
                InetAddress address = InetAddress.getByName(addr);

                byte[] buffer = message.getBytes();

                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setReuseAddress(true);
                socket.connect(address, port);

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
                Log.d("broadcast", "sent");
                socket.send(packet);
                socket.close();
            } catch (Exception e){
                Log.d("broadcast", e.toString());
            }
            return null;
        }
    }

    private class TCPReceiver extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try {
                ServerSocket infoSocket = new ServerSocket(50001);
                while(true){
                    Socket conSocket = infoSocket.accept();
                    BufferedReader info =
                            new BufferedReader(
                                    new InputStreamReader(
                                            (conSocket.getInputStream())));
                    InetSocketAddress addr = (InetSocketAddress) conSocket.getRemoteSocketAddress();

                    Device d = new Device();
                    d.ip = addr.getAddress().toString();
                    d.port = Integer.toString(addr.getPort());
                    d.name = info.readLine();
                    String line;
                    while((line = info.readLine()) != null) {
                        d.addOp(line);
                    }
                    addItems(d);
                    Log.d("receive", d.toString());
                    conSocket.close();
                }
                //infoSocket.close();
            } catch (Exception e){
                Log.d("receive", e.toString());
            }
            return null;
        }
    }

    public void addItems(final Device d){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                boolean found = false;
                for(Device dItem : listItems){
                    if(dItem.ip.equals(d.ip)){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    listItems.add(d);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private class Device extends Object{
        String name = "";
        String ip = "";
        String port = "";
        ArrayList<String> list = new ArrayList<>();

        @Override
        public String toString(){
            return name;
        }

        public void addOp(String op){
            list.add(op);
        }
    }
}
