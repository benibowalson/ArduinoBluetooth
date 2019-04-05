package com.example.arduinobluetooth.Adapter;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arduinobluetooth.Data.BTDevice;
import com.example.arduinobluetooth.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyCardView> {

    private Context mContext;
    private IClickHandler myClickHandler;
    private ArrayList<BluetoothDevice> myDeviceList = new ArrayList<>();

    public RecyclerAdapter (Context context, ArrayList<BluetoothDevice> deviceArrayList, IClickHandler clickHandler){
        this.mContext = context;
        this.myClickHandler = clickHandler;
        this.myDeviceList = deviceArrayList;
    }

    @NonNull
    @Override
    public MyCardView onCreateViewHolder(@NonNull ViewGroup parentView, int i) {
        View aCard = LayoutInflater.from(parentView.getContext()).inflate(R.layout.mycard_layout, parentView, false);
        return new MyCardView(aCard);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCardView myCardView, int i) {
        final BluetoothDevice aBTDevice = myDeviceList.get(i);
        String fullName = aBTDevice.getName();   //.getName() is a method in BluetoothDevice Class
        myCardView.tvName.setText(fullName);
        myCardView.tvMAC.setText(aBTDevice.getAddress());
    }

    @Override
    public int getItemCount() {
        return myDeviceList.size();
    }

    public interface IClickHandler {
        void onRecyclerItemClicked(int aPosition);
    }

    public void swapMyData(ArrayList<BluetoothDevice> newDataList){
        myDeviceList = newDataList;
        notifyDataSetChanged();
    }

    public class MyCardView extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvMAC;

        public MyCardView(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvBluetoothName);
            tvMAC = (TextView) itemView.findViewById(R.id.tvBluetoothMAC);
            itemView.setOnClickListener(this);  //Don't forget this, please or click on recycler items won't work
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            myClickHandler.onRecyclerItemClicked(clickedPosition);
        }
    }
}
