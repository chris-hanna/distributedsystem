package com.command.chris.connector;

import android.util.Log;

import java.util.ArrayList;

public class Device extends Object {
    String name;
    String ip;
    String port;
    ArrayList<DeviceFunction> functions = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }

    public void addOp(String s) {
        DeviceFunction f = new DeviceFunction(s);
        functions.add(f);
    }
}

class DeviceFunction {
    String name, pType, params;

    public DeviceFunction(String function){
        String[] part = function.split("\\|");
        name = part[0];
        pType = part[1];
        params = part[2];
    }

    @Override
    public String toString() {
        return name;
    }
}
