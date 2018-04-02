package com.cavedu.android.GPSCar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import kevin.ch7_GPSCar_1.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GPSCar extends Activity {
    /** Called when the activity is first created. */
	TextView tv_distance;
	EditText etv_nxtName;
	EditText etv_latitude;
	EditText etv_longtitude;
	Button btn_connect;
	LocationManager locationManager;
	MyLocationListener locationListener;
	BluetoothAdapter BTAdapter;
	BluetoothSocket BTSocket;
	DataOutputStream mOuStream;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_distance = (TextView)findViewById(R.id.tv_distance);
        //取得系統定位服務
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //實體化實做監聽事件的類別
        locationListener = new MyLocationListener();
        //註冊監聽事件並設定位置提供者的參數
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
        etv_latitude = (EditText)findViewById(R.id.tv_latitude);
        etv_longtitude = (EditText)findViewById(R.id.tv_longitude);
        etv_nxtName = (EditText)findViewById(R.id.nxtName);
        btn_connect = (Button)findViewById(R.id.buttonConnect);
        //藍牙連線按鈕
        btn_connect.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatNXTConnect();
				arg0.setVisibility(View.GONE);
			}   	
        });	
    }
    //建立藍牙連線
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
    		        if(TempoDevice.getName().equals(etv_nxtName.getText().toString())){
    			        BTDevice = TempoDevice;
    		        }
    	        }
    	    }
    		BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    		BTSocket.connect();
    		mOuStream =  new DataOutputStream(BTSocket.getOutputStream());
    	    } catch (IOException e) {
    		    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show();
    	    }
    }//creatNXTConnect
    public  void CommandNXT(int value){

    	if(mOuStream==null){
    		return;
    	}   	
    	try {
    		mOuStream.writeInt(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
    }//CommandNXT
    //MyLocationListener 內部類別實作藍牙事件
    private class MyLocationListener implements LocationListener{
    	private int count = 0;
    	private double latitude,star_lai;
    	private double longitude,star_lon;
    	private float[] results = new float[3];
    	public static final int STOP = 2;
    	public static final int START = 1;
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub	
			if (location != null){
				Toast.makeText( getApplicationContext(), "Changed", Toast.LENGTH_SHORT).show();
				//getLatitude和getLongitude方法取得位置經緯度
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				//紀錄機器人初始位置經緯度
				if(count == 0){
					CommandNXT(START);
					star_lai = latitude;
					star_lon = longitude;
					count++;
				}
				//計算機器人移動距離
				Location.distanceBetween(star_lai, star_lon, latitude, longitude, results);
                etv_latitude.setText(Double.toString(location.getLatitude()));
                etv_longtitude.setText(Double.toString(location.getLongitude()));
                tv_distance.setText("Distance is "+String.valueOf(results[0]));
                //機器人直線移動距離40公尺後停止
                if(results[0]>40.0){
                	CommandNXT(STOP);
                }
            }
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText( getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT).show(); 
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show(); 
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub	
		}
    }//MyLocationListener
    public void onPause(){
    	super.onPause();
    	//移除位置資訊更新
    	locationManager.removeUpdates(locationListener);
    	if(BTSocket!= null){
    		try {
				BTSocket.close();
				mOuStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}