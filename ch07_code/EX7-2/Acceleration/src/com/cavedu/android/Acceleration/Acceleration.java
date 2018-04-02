package com.cavedu.android.Acceleration;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.cavedu.android.ch7_Acceleration_1.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Acceleration extends Activity implements SensorEventListener {
    /** Called when the activity is first created. */
	EditText et_nxtName;
	TextView tv_hint;
	Button btn_connect;
	SensorManager sensorManager;
	BluetoothAdapter BTAdapter = null;
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null;
	final float offsetG = (float)9.815;
	final int START = 0;
	final int STOP = 1;
	final int ONESTEP = 2;
	final int NONESTEP = 3;
	float differenceG;
	float oldValueG = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_hint = (TextView)findViewById(R.id.tv_hint);
        et_nxtName = (EditText)findViewById(R.id.et_nxtName);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatNXTConnect();
				arg0.setVisibility(View.GONE);
			}
        	
        });
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER ), SensorManager.SENSOR_DELAY_UI);
    }
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		ComputeTheDifference(event.values[1]);
	}
	//處裡感應器讀值
	public void ComputeTheDifference(float readValue){
		tv_hint.setText(String.valueOf(readValue));
		differenceG = readValue - offsetG;
		//每搖晃手機一次傳出ONESTEP值
		if((differenceG*oldValueG)<0){
			CommandNXT(ONESTEP);
		}
		else{
			CommandNXT(NONESTEP);
		}
		oldValueG = differenceG;
	}
	
	public void creatNXTConnect(){
    	try {
        	BTAdapter = BluetoothAdapter.getDefaultAdapter();
        	if(BTAdapter==null){
            	Toast.makeText(this,"No Device found!",Toast.LENGTH_SHORT).show();
            	finish();
            }
        	BluetoothDevice  BTDevice = null;
    	    Set<BluetoothDevice> BTList = BTAdapter.getBondedDevices();
    	    if(BTList.size()>0){
    	        for(BluetoothDevice TempoDevice : BTList){
    		        if(TempoDevice.getName().equals(et_nxtName.getText().toString())){
    			        BTDevice = TempoDevice;
    		        }
    	        }
    	    }	    
    		BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    		BTSocket.connect();
    		DATAOu = new DataOutputStream(BTSocket.getOutputStream());
    		CommandNXT(START);
    		tv_hint.setText("Please shack the phone");
    	    } catch (IOException e) {
    		    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show();
    	    }
    }//creatNXTConnect
	//傳出指令
    public  void CommandNXT(int value){

    	if(DATAOu==null){
    		return;
    	}   	
    	try {
			DATAOu.writeInt(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
    }//CommandNXT
    
    public void onPause(){
    	super.onPause();
    	sensorManager.unregisterListener(this);
    	CommandNXT(STOP);
    	if(BTSocket!=null){
    		try {
				BTSocket.close();
				DATAOu.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}//if
    }

}