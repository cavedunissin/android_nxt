package com.cavedu.android.Bluetooth_2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import kevin.ch6_Bluetooth_2.R;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Bluetooth_2 extends Activity {
    /** Called when the activity is first created. */
	Button btn_scanMode;
    Button btn_sent;
	TextView tv_condition;
	EditText edt_message;
	BluetoothAdapter BTAdapter = null;
	BluetoothDevice BTDevice = null;
	Set<BluetoothDevice> pairedDevices ;
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null; 
	final int REQUEST_ENABLE_BT = 1;
	String WORD ;
	private ArrayAdapter<String> conditionArrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("ch6_Bluetooth_2");
        
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
            CreatConnection(BTDevice);
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
    		CommandNXT("Hello Robot!");
    		ConnectLayout();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //�ǥX��ƪ���k
    public void ConnectLayout() {
    	//�e��������ܿ�J����e��
        setContentView(R.layout.send_message);
        edt_message = (EditText)findViewById(R.id.edt_message);
        btn_sent = (Button)findViewById(R.id.btn_send);
        btn_sent.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//�I�����s��e�X��r�T��
				WORD = edt_message.getText().toString();
				try {
					CommandNXT(WORD);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}        	
        });
    }
    //�e�X��r�T������k
    public void CommandNXT(String word) throws IOException{
    	//��write��k�g�X���s�`�N�C�Ӹ�Ƴ᳣̫�n�[����r��
    	DATAOu.writeChars(word+"\n");
    	//flush��k�|�M�����s
    	DATAOu.flush();
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
    public void onDestroy() {
    	//������X��y
        super.onDestroy();
        if(BTSocket!=null){
        	try {
        		DATAOu.close();
    			BTSocket.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        
    }

    
}