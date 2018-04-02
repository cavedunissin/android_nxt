package com.cavedu.android.Bluetooth_3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import kevin.ch6_Bluetooth_3.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ch6_Bluetooth_3 extends Activity {
    /** Called when the activity is first created. */
	Button btn_scanMode;
	TextView tv_readValue;
	TextView tv_condition;
	ProgressBar progressBarHorizon;
	BluetoothAdapter BTAdapter = null;
	BluetoothDevice BTDevice = null;
	Set<BluetoothDevice> pairedDevices ;
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null;
	DataInputStream DATAIn = null;
	readThread mthread;
	int readValue;
	final int REQUEST_ENABLE_BT = 1;
	final int START = 0;
	final int STOP = 1;
	private ArrayAdapter<String> conditionArrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("ch6_Bluetooth_3");
        
        conditionArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_layout);
        ListView conditionListView = (ListView) findViewById(R.id.paired_devices);
        conditionListView.setAdapter(conditionArrayAdapter);
        conditionListView.setOnItemClickListener(mDeviceClickListener);
        
        tv_condition = (TextView)findViewById(R.id.tv_condition);
        btn_scanMode = (Button)findViewById(R.id.btn_scan);
        
        btn_scanMode.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tv_condition.setText("Please press the device to connect");
				GetPairedDevices();
				//按下搜尋的按鈕後就不能再按
				arg0.setVisibility(View.GONE);
			}
        	
        });
        
        
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果沒有藍芽設備
        if (BTAdapter == null) {
            Toast.makeText(this, "No Bluetooth Device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (!BTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // 執行到startActivityForResult方法時系統會跑出視窗來詢問是否啟動藍牙
        }
    //OnCreate()結束
    }
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 確認清單上的設備名稱與該設備的名稱相同
        	for (BluetoothDevice btdevice : pairedDevices) {
    			//如果有以配對好的設備，則放入清單中供選取
    			if(btdevice.getName().equals(((TextView) v).getText().toString())){
    				BTDevice = btdevice;
    			}
    		}
        	if(BTDevice!=null){
        	    CreatConnection(BTDevice);
        	}
        	else{
        		return;
        	}
        }
    };
    
    public void GetPairedDevices(){
    	//取得已配對的遠端藍牙設備
        pairedDevices = BTAdapter.getBondedDevices();
    	if (pairedDevices.size() > 0) {      		   
    		for (BluetoothDevice device : pairedDevices) {
    			//如果有以配對好的設備，則放入清單中供選取
    			conditionArrayAdapter.add(device.getName());
    		}
    	}else {
    		//狀態顯示沒有配對的設備，並放入清單中
            String noDevices = getResources().getText(R.string.no_paired_devices).toString();
            conditionArrayAdapter.add(noDevices);
        }  	
    }
    
    public void CreatConnection(BluetoothDevice BTD){
    	try {
			BTSocket = BTD.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
			BTSocket.connect();
    		DATAOu = new DataOutputStream(BTSocket.getOutputStream());
    		Monitor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //連線成功後會進入監控畫面
    public void Monitor(){
    	//顯示monitor_layout畫面
    	setContentView(R.layout.monitor_layout);
    	progressBarHorizon = (ProgressBar)findViewById(R.id.progressBarH);
    	tv_readValue =(TextView)findViewById(R.id.tv_lightValue);
    	//宣告一個內部類別的子執行緒(Inner class)
    	mthread = new readThread();
    	//啟動子執行緒
    	mthread.start();	
    }   
  //送出文字訊息的方法
    public void CommandNXT(int command) throws IOException{
    	//用write方法寫出內存注意每個資料最後都要加換行字元
    	if(DATAOu==null){
    		return;
    	}
    	else{
    		DATAOu.writeInt(command);
        	//flush方法會清除內存
        	DATAOu.flush();
    	}
    	
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case REQUEST_ENABLE_BT:
    		if (resultCode == Activity.RESULT_OK) {
    			GetPairedDevices();
    			//手動啟動藍牙後位於main.xml畫面上的按鈕會消失
    			findViewById(R.id.btn_scan).setVisibility(View.GONE);
            } else {
                // 無法開啟藍牙設備
            	Toast.makeText(this,"Disable to open Bluetooth device", Toast.LENGTH_SHORT).show();
                finish();
            }
    	}
    }
    public void onStop() {
        super.onStop();
        try {
			CommandNXT(STOP);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(BTSocket!=null){
        	try {
    			BTSocket.close();
    			DATAOu.close();
    			DATAIn.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } 
    } 
  //開啟新的執行續，屬於巢狀類別以便共用Activity的資料成員
	class readThread extends Thread{	
		public void run(){
			try {
				CommandNXT(START);
			    DATAIn =  new DataInputStream(BTSocket.getInputStream());
				while(true){
					
					readValue = DATAIn.readInt();
					
					Message message = new Message();
					
					message.what = 1;
					
					mHandler.sendMessage(message);
					
					CommandNXT(START);
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}//Thread
	//實體化Handler物件
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			 switch (msg.what) {
			 case 1:
				
				tv_readValue.setText(String.valueOf(readValue));
				progressBarHorizon.setProgress(readValue);
	            break;
			 }
			 super.handleMessage(msg);
		}
	};
//activity	
}