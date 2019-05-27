package com.example.mi.myapplication;


import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.mi.myapplication.adapter.BlueListAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class Mfragment extends Fragment {
    private Switch mSwitch;
    private TextView tv;
    private View view;
    private Button btn_startDiscovery;
    private ListView lv;
    private BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> bluetoothList=new ArrayList<BluetoothDevice>();
    private BlueListAdapter blueListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.layout_home,container,false);
        mSwitch=(Switch)(view.findViewById(R.id.switch1));
        tv=(TextView)(view.findViewById(R.id.tv));
        btn_startDiscovery=(Button)(view.findViewById(R.id.btn_startDiscovery));
        lv=(ListView)(view.findViewById(R.id.lv));
        blueListAdapter=new BlueListAdapter(getContext(),bluetoothList);
        if(bluetoothAdapter!=null){
            if(getBluetoothState(bluetoothAdapter)) {
                mSwitch.setChecked(true);
                tv.setText("蓝牙已打开");
                btn_startDiscovery.setVisibility(View.VISIBLE);
            }else {
                mSwitch.setChecked(false);
                btn_startDiscovery.setVisibility(View.GONE);
            }
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        Toast.makeText(getContext(),"打开蓝牙"+(bluetoothAdapter.enable()?"成功":"失败"),Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(),"关闭蓝牙"+(bluetoothAdapter.disable()?"成功":"失败"),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(),"未找到蓝牙功能",Toast.LENGTH_SHORT).show();
        }
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                tv.setText("蓝牙正在打开");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                tv.setText("蓝牙已打开");
                                btn_startDiscovery.setVisibility(View.VISIBLE);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                tv.setText("蓝牙正在关闭");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                tv.setText("蓝牙已关闭");
                                btn_startDiscovery.setVisibility(View.GONE);
                                break;
                        }
                        break;
                    case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                        switch (blueState) {
                            case BluetoothAdapter.STATE_CONNECTED:
                                tv.setText("蓝牙已连接");
                                break;
                            case BluetoothAdapter.STATE_CONNECTING:
                                tv.setText("蓝牙正在连接");
                                break;
                            case BluetoothAdapter.STATE_DISCONNECTING:
                                tv.setText("蓝牙正在取消连接");
                                break;
                            case BluetoothAdapter.STATE_DISCONNECTED:
                                tv.setText("蓝牙已取消连接");
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        // 从Intent中获取设备对象
                            BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            //Toast.makeText(getContext(),device.getName()+":"+device.getAddress(),Toast.LENGTH_SHORT).show();
                            bluetoothList.add(device);
                            blueListAdapter.notifyDataSetChanged();
                            break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                         Toast.makeText(getContext(),"搜索完毕",Toast.LENGTH_SHORT).show();
                            break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        BluetoothDevice device1 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device1.getBondState() == BluetoothDevice.BOND_BONDING) {
                            tv.setText("正在配对" + device1.getName());
                        } else if (device1.getBondState() == BluetoothDevice.BOND_BONDED) {
                            tv.setText("完成配对" + device1.getName());
                        } else if (device1.getBondState() == BluetoothDevice.BOND_NONE) {
                            tv.setText("取消配对" + device1.getName());
                        }
                        break;
                }
            }
        },intentFilter);
        btn_startDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.startDiscovery();
                bluetoothList.clear();
                Toast.makeText(getContext(),"开始搜索"+(bluetoothAdapter.startDiscovery()?"成功":"失败"),Toast.LENGTH_SHORT).show();
            }
        });
        lv.setAdapter(blueListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                final TextView tv= view.<TextView>findViewById(R.id.tv_blue_address);
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(tv.getText().toString());

                if(!device.createBond()){
                    String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                    UUID uuid = UUID.fromString(SPP_UUID);
                    try {
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
                        socket.connect();
                        OutputStream os=socket.getOutputStream();
                        os.write(1);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });
        return view;
    }

    public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");//获取蓝牙的连接方法
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();//返回连接状态
    }

    private boolean getBluetoothState(BluetoothAdapter bluetoothAdapter) {
        switch (bluetoothAdapter.getState()){
            case BluetoothAdapter.STATE_ON:
                return true;
            case BluetoothAdapter.STATE_OFF:
                return false;
        }
        return false;
    }



}
