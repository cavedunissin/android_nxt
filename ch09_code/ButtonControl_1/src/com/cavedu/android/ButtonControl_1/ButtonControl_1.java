package com.cavedu.android.ButtonControl_1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.cavedu.android.ch9_ButtonControl_1.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ButtonControl_1 extends Activity {
    /** Called when the activity is first created. */
	Button button_connect;
	Button button_forward;
	Button button_turnl;
	Button button_turnr;
	Button button_backward;
	Button button_stop;
	EditText editText_name;
	BluetoothSocket btSocket = null;
	DataOutputStream dataOut = null;
	public static final int turnl = 200;
	public static final int turnr = -200;
	public static final int stop = 0;
    public static final int targetPower = 700;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        editText_name = (EditText)findViewById(R.id.editText_name);  
        button_connect = (Button)findViewById(R.id.button_connect);
        button_forward = (Button)findViewById(R.id.Button_forward);
    	button_backward = (Button)findViewById(R.id.Button_backward);
    	button_turnl = (Button)findViewById(R.id.Button_turnl);
    	button_turnr = (Button)findViewById(R.id.Button_turnr);
    	button_stop = (Button)findViewById(R.id.button_stop);      
        //建立連線
        button_connect.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(btSocket == null){
					ConnectToNXT();
				}
				else setTitle("NXT has been connected");
			}
        });
    	//前進按鈕事件
    	button_forward.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendNXTCommand(targetPower);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
    	});
    	//後退按鈕事件
    	button_backward.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendNXTCommand((-1)*targetPower);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
    	});
    	//左轉按鈕事件
    	button_turnl.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendNXTCommand(turnl);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  		
    	});
    	//右轉按鈕事件
    	button_turnr.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendNXTCommand(turnr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	});
    	//停止按鈕事件
    	button_stop.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					sendNXTCommand(stop);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	});
    }//onCreate()
    //與NXT建立連線
    public void ConnectToNXT(){
    	BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter == null){
			finish();
			return;
		}
		BluetoothDevice btDevice = null;
		Set<BluetoothDevice> BTList = btAdapter.getBondedDevices();
		if(BTList.size()>0){
			for(BluetoothDevice btTempDevice:BTList){
				if(btTempDevice.getName().equals(editText_name.getText().toString())){
					btDevice = btTempDevice;
				}
			}
		}
		if(BTList.size()==0){
			return;
		}
		try {
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
			btSocket.connect();
			dataOut = new DataOutputStream(btSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //傳送控制碼的方法
    public void sendNXTCommand(int value) throws IOException{
    	dataOut.writeInt(value);
    	dataOut.flush();
    }
}