package kevin.orientation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

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

public class OrientationMain extends Activity implements SensorEventListener{
    /** Called when the activity is first created. */
	SensorManager sensorManager = null;
	TextView textView_2,textView_3,textView_4;
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null;
	DataInputStream DATAIn = null;
	readThread mThread;
	EditText editText ;
	Button btu_connect;
	final int STOP_ALL = 0,START = 6,RUN_PP = 2,RUN_PN = 3,RUN_NP = 4, RUN_NN = 5;
	int turn,forward,command;
	int order,count=0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //註冊感應器事件
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);      
        editText =(EditText)findViewById(R.id.edtText);
        textView_2 = (TextView)findViewById(R.id.textView_2);
        textView_3 = (TextView)findViewById(R.id.textView_3);
        textView_4 = (TextView)findViewById(R.id.textView_4);
        btu_connect = (Button)findViewById(R.id.btu_connect);
        
        //建立按鈕事件
        btu_connect.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatNXTConnect();				
			}        	
        });//setOnClickListener           
    }//onCreat
    
    public  void CommandNXT(int ORDER){
    	if(DATAOu==null){
    		return;
    	}   	
    	try {
			DATAOu.writeInt(ORDER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
    }//CommandNXT
    
    public void creatNXTConnect(){
    	try {
        	BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        	if(BTAdapter==null){
            	Toast.makeText(this,"No Device found!",Toast.LENGTH_SHORT).show();
            	finish();
            }
        	BluetoothDevice  BTDevice = null;
    	    Set<BluetoothDevice> BTList = BTAdapter.getBondedDevices();
    	    if(BTList.size()>0){
    	        for(BluetoothDevice TempoDevice : BTList){
    		        if(TempoDevice.getName().equals(editText.getText().toString())){
    			        BTDevice = TempoDevice;
    		        }
    	        }
    	    }	    
    		BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    		BTSocket.connect();
    		DATAOu = new DataOutputStream(BTSocket.getOutputStream());
    		CommandNXT(START*100000);
    		mThread = new readThread();
    		mThread.start();
    	    } catch (IOException e) {
    		    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show();
    	    }
    }//creatNXTConnect

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		    // TODO Auto-generated method stub			

	@Override
	//對感應器的讀值做處理
	public void onSensorChanged(SensorEvent event) {
		    // TODO Auto-generated method stub
		textView_2.setText("The forward is "+event.values[1]);
		textView_3.setText("The turn is    "+event.values[2]);
		textView_4.setText("The check is    "+count);
		turn = Math.abs(event.values[2])>5 ? Math.abs((int) (event.values[2])):0;
		forward = Math.abs(event.values[1])>5 ? Math.abs((int)(event.values[1])):0;
		if(BTSocket != null && count == 0){
				command  = ((event.values[1]>0)? ((event.values[2]>0)?RUN_PP : RUN_PN):((event.values[2]>0)? RUN_NP : RUN_NN));
				order = command*100000+turn*1000+forward*10+1;
				CommandNXT(order);
				count=1;								
		}
	}//onSensorChanged
	
	public void onStop(){
		super.onStop();
		CommandNXT(STOP_ALL*100000);
		//解除註冊感應器事件
		sensorManager.unregisterListener(this);
		try {
			BTSocket.close();
			BTSocket = null;
			DATAOu = null;
			DATAIn = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//Stop
	
	//開啟新的執行續，屬於巢狀類別以便共用Activity的資料成員
	class readThread extends Thread{	
		int check=0;
		public void run(){
			try {
				DATAIn = new DataInputStream(BTSocket.getInputStream());
				while(true){
					if(count == 1){
						check = DATAIn.readInt();
						count = (check == 1)?0:1;
						check=0;
					}
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}//Thread
	
}//Activity