package com.command.chris.connector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Connector extends Activity {
    private MyListAdapter adapter;
    private ArrayList<Device> listItems = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connector_main);

        ExpandableListView listView = findViewById(R.id.list_view);
        adapter = new MyListAdapter(this, listItems);
        listView.setAdapter(adapter);

        listView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Device d = listItems.get(groupPosition);
                DeviceFunction df = d.functions.get(childPosition);

                Log.d("childClick", df.name + " " + df.pType + " " + df.params);

                switch(df.pType){
                    case "c":
                        UDPCommunication asyncTask1 = new UDPCommunication(d.ip, 50000, df.params);
                        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    case "v":
                        Log.d("params", df.params);

                        if(listItems.size() < 2){
                            break;
                        }

                        Device d2;
                        if(groupPosition == 0){
                            d2 = listItems.get(1);
                        }else{
                            d2 = listItems.get(0);
                        }

                        String msg = d2.ip + "|50000|" + df.params.split(",")[2];
                        UDPCommunication asyncTask2 = new UDPCommunication(d.ip, 50000, msg);
                        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                    default:
                }
                return false;
            }
        });

        TCPReceiver asyncTask3 = new TCPReceiver();
        asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Button refresh = findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UDPCommunication asyncTask4 = new UDPCommunication("255.255.255.255", 50000, "hello");
                asyncTask4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public void addDevice(Device d) {
        boolean found = false;
        for (Device dItem : listItems) {
            if (dItem.ip.equals(d.ip)) {
                found = true;
                break;
            }
        }
        if (!found) {
            listItems.add(d);
            adapter.notifyDataSetChanged();
            Log.d("addItems", d.toString());
        }
    }

    private class TCPReceiver extends AsyncTask<Void, Device, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ServerSocket infoSocket = new ServerSocket(50001);
                while (true) {
                    Socket conSocket = infoSocket.accept();
                    BufferedReader info =
                            new BufferedReader(
                                    new InputStreamReader(
                                            (conSocket.getInputStream())));
                    InetSocketAddress addr = (InetSocketAddress) conSocket.getRemoteSocketAddress();

                    Device d = new Device();
                    d.ip = addr.getHostString();
                    d.port = Integer.toString(addr.getPort());
                    d.name = info.readLine();
                    Log.d("TCP receive",  d.name + " (" +  d.ip + ":" + d.port + ") ");

                    String line;
                    while ((line = info.readLine()) != null) {
                        Log.d("device function",  line);
                        d.addOp(line);
                    }
                    conSocket.close();

                    publishProgress(d);
                }
                //infoSocket.close();
            } catch (Exception e) {
                Log.d("receive", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Device... devices) {
            addDevice(devices[0]);
        }
    }
}
