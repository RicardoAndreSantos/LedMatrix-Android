package com.example.ricardosantos.ledmatrix_rgb;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter = null;
    BluetoothDevice btDevice = null;
    BluetoothSocket btSocket = null;

    ConnectedThread connectedThread;

    Boolean connected = false;

    private static final int activation_Request = 1;
    private static final int activation_Connection = 2;
    private static final int MESSAGE_READ = 3;

    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();

    private static String MAC = null;

    //communication channel
    UUID my_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set bluetooth active
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }

        if(!btAdapter.isEnabled()){
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);                  //response to the main activity
            startActivityForResult(enableAdapter,activation_Request);
        }
    }

    //default color
    String color = "orange";

    //--choose color--
    public void color_pick(View v){
        color = v.getTag().toString();
        Toast.makeText(getApplicationContext(), "" + color , Toast.LENGTH_SHORT).show();
    }


    //--matrix button clicked--
    public void btn_click(View v){
        int i = Integer.parseInt(v.getTag().toString());
        //Log.i("teste","COR - " +color);
        if(color.equals("orange")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        }
        if(color.equals("red")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }
        if(color.equals("blue")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP);
        }
        if(color.equals("green")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        }

        if(color.equals("yellow")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
        }

        if(color.equals("lightBlue")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.lightBue), PorterDuff.Mode.SRC_ATOP);
        }

        if(color.equals("pink")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_ATOP);
        }

        if(color.equals("purple")){
            v.getBackground().setColorFilter(getResources().getColor(R.color.purple), PorterDuff.Mode.SRC_ATOP);
        }



        if(color.equals("clear")){
            v.getBackground().clearColorFilter();
        }

        //Toast.makeText(getApplicationContext(), "Primido : " + i , Toast.LENGTH_SHORT).show();
        if(connected) {
            connectedThread.write(i + "-" + color);
        }
        else{
            //Toast.makeText(getApplicationContext(), "Bluetooth connection is needed...", Toast.LENGTH_SHORT).show();
        }
    }


    //--clear all--
    //to use normal button  -> public void clear(View v)
    public void clear(){
        Toast.makeText(getApplicationContext(), "Clear Matrix", Toast.LENGTH_SHORT).show();
        if(connected) {
            connectedThread.write("clearAll");
        }else {
            //Toast.makeText(getApplicationContext(), "Bluetooth : OFF", Toast.LENGTH_SHORT).show();
        }

        for(int i=1 ; i<=100 ; i++){
            int id = getResources().getIdentifier("btn"+i , "id" , getPackageName());
            Button btn_aux = (Button) findViewById(id);
            btn_aux.getBackground().clearColorFilter();
        }


        //if i want to receive data from the arduino, after send message
        //delimite message in the arduino with '{' and '}'

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_READ){

                    String recebido = (String) msg.obj;

                    dadosBluetooth.append(recebido);
                    //check if data was all received
                    int endInformation = dadosBluetooth.indexOf("}");

                    if(endInformation > 0){
                        String daddosCompletos = dadosBluetooth.substring(0, endInformation);

                        int informationSize = daddosCompletos.length();

                        if(dadosBluetooth.charAt(0) == '{'){
                            String finalData = dadosBluetooth.substring(1,informationSize);
                            Log.d("Recebido" , finalData);
                        }
                    }

                }

            }
        };

    }


    private Menu menu;

    //Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_BT:
                //Toast.makeText(getApplicationContext(), "BlueTooth btn", Toast.LENGTH_SHORT).show();
                if(connected){
                    //desconectar
                    try{
                        btSocket.close();
                        connected = false;
                        menu.getItem(1).setIcon(ContextCompat.getDrawable(this,R.mipmap.bt_icon));
                        Toast.makeText(getApplicationContext(), "Bluetooth Connection: OFF", Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException error){
                        Toast.makeText(getApplicationContext(), "Connection error, try again...", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //connect
                    Intent abreLista = new Intent(MainActivity.this , listaDispositivos.class);
                    startActivityForResult(abreLista , activation_Connection);
                }

                return true;


            case R.id.btn_clean:
                //Toast.makeText(getApplicationContext(), "Clear btn", Toast.LENGTH_SHORT).show();
                clear();

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case activation_Request:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "BlueTooth : ON", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "BlueTooth : OFF", Toast.LENGTH_SHORT).show();
                }
                break;

            case activation_Connection:{
                if(resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(listaDispositivos.MAC_ADDRESS);
                    //Toast.makeText(getApplicationContext(), "MAC Final : " + MAC, Toast.LENGTH_SHORT).show();
                    btDevice = btAdapter.getRemoteDevice(MAC);

                    try{
                        btSocket = btDevice.createRfcommSocketToServiceRecord(my_UUID);
                        btSocket.connect();
                        connected = true;

                        connectedThread = new ConnectedThread(btSocket);
                        connectedThread.start();

                        menu.getItem(1).setIcon(ContextCompat.getDrawable(this,R.mipmap.bt_disconnect));

                        Toast.makeText(getApplicationContext(), "Connected : " + MAC, Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException error){
                        connected = false;
                        Toast.makeText(getApplicationContext(), "Connection error, try again... ", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Failed to obtain MAC...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //-----------------------------------------------------------------------------------------------------------
    //-------BlueTooth Connection----------------
    //-----------------------------------------------------------------------------------------------------------
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream


        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("teste", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("teste", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        // Receive data to the remote device
        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    //bytes to String
                    String dadosBt = new String(mmBuffer , 0 , numBytes);

                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(MESSAGE_READ, numBytes, -1, dadosBt);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.d("teste", "Input stream was disconnected", e);
                    break;
                }
            }
        }


        // Send data to the remote device
        public void write(String sendData) {
            byte[] buffer = sendData.getBytes();
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.d("teste", "Error occurred when sending data", e);
            }
        }


    }
}




