package com.example.ricardosantos.ledmatrix_rgb;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Ricardo Santos on 02/07/2018.
 */

public class listaDispositivos extends ListActivity {

    private BluetoothAdapter btAdapter2 = null;

    public static String MAC_ADDRESS = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1);

        btAdapter2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter2.getBondedDevices();

        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                String btName = device.getName();
                String btAddress = device.getAddress();
                ArrayBluetooth.add(btName +"\n" + btAddress);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String info = ((TextView) v).getText().toString();
        //Toast.makeText(getApplicationContext(), "info :" + info , Toast.LENGTH_SHORT).show();

        String addressMAC = info.substring(info.length() - 17);
        //Toast.makeText(getApplicationContext(), "MAC :" + MAC_address , Toast.LENGTH_SHORT).show();

        Intent returnMAC = new Intent();                                                     //pass information to MainActivity
        returnMAC.putExtra(MAC_ADDRESS , addressMAC);
        setResult(RESULT_OK , returnMAC);
        finish();

    }
}

