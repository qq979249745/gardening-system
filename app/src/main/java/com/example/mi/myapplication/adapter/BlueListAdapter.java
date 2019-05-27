package com.example.mi.myapplication.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.mi.myapplication.Mfragment;
import com.example.mi.myapplication.R;

import java.util.ArrayList;

public class BlueListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BluetoothDevice> bluetoothList;
    private String[] mStateArray = {"未绑定", "绑定中", "已绑定", "已连接"};
    public BlueListAdapter(Context context, ArrayList<BluetoothDevice> bluetoothList){
        this.context=context;
        this.bluetoothList=bluetoothList;
    }
    @Override
    public int getCount() {
        return bluetoothList.size();
    }

    @Override
    public Object getItem(int i) {
        return bluetoothList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    private class ViewHolder{
        public TextView tv_blue_name;
        public TextView tv_blue_address;
        public TextView tv_blue_state;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.blue_item,null);
            viewHolder=new ViewHolder();
            viewHolder.tv_blue_name=(TextView)(view.findViewById(R.id.tv_blue_name));
            viewHolder.tv_blue_address=(TextView)(view.findViewById(R.id.tv_blue_address));
            viewHolder.tv_blue_state=(TextView)(view.findViewById(R.id.tv_blue_state));
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }

        viewHolder.tv_blue_name.setText(bluetoothList.get(i).getName());
        viewHolder.tv_blue_address.setText(bluetoothList.get(i).getAddress());
        viewHolder.tv_blue_state.setText(mStateArray[bluetoothList.get(i).getBondState()-10]);
        return view;
    }
}
