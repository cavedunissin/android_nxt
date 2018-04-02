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
				//���U�j�M�����s��N����A��
				arg0.setVisibility(View.GONE);
			}
        	
        });
        
        
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //�p�G�S���Ū޳]��
        if (BTAdapter == null) {
            Toast.makeText(this, "No Bluetooth Device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (!BTAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // �����startActivityForResult��k�ɨt�η|�]�X�����Ӹ߰ݬO�_�Ұ��Ť�
        }
    //OnCreate()����
    }
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // �T�{�M��W���]�ƦW�ٻP�ӳ]�ƪ��W�٬ۦP
        	for (BluetoothDevice btdevice : pairedDevices) {
    			//�p�G���H�t��n���]�ơA�h��J�M�椤�ѿ��
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
    	//���o�w�t�諸�����Ť��]��
        pairedDevices = BTAdapter.getBondedDevices();
    	if (pairedDevices.size() > 0) {      		   
    		for (BluetoothDevice device : pairedDevices) {
    			//�p�G���H�t��n���]�ơA�h��J�M�椤�ѿ��
    			conditionArrayAdapter.add(device.getName());
    		}
    	}else {
    		//���A��ܨS���t�諸�]�ơA�é�J�M�椤
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
    //�s�u���\��|�i�J�ʱ��e��
    public void Monitor(){
    	//���monitor_layout�e��
    	setContentView(R.layout.monitor_layout);
    	progressBarHorizon = (ProgressBar)findViewById(R.id.progressBarH);
    	tv_readValue =(TextView)findViewById(R.id.tv_lightValue);
    	//�ŧi�@�Ӥ������O���l�����(Inner class)
    	mthread = new readThread();
    	//�Ұʤl�����
    	mthread.start();	
    }   
  //�e�X��r�T������k
    public void CommandNXT(int command) throws IOException{
    	//��write��k�g�X���s�`�N�C�Ӹ�Ƴ᳣̫�n�[����r��
    	if(DATAOu==null){
    		return;
    	}
    	else{
    		DATAOu.writeInt(command);
        	//flush��k�|�M�����s
        	DATAOu.flush();
    	}
    	
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case REQUEST_ENABLE_BT:
    		if (resultCode == Activity.RESULT_OK) {
    			GetPairedDevices();
    			//��ʱҰ��Ť�����main.xml�e���W�����s�|����
    			findViewById(R.id.btn_scan).setVisibility(View.GONE);
            } else {
                // �L�k�}���Ť��]��
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
  //�}�ҷs��������A�ݩ�_�����O�H�K�@��Activity����Ʀ���
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
	//�����Handler����
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