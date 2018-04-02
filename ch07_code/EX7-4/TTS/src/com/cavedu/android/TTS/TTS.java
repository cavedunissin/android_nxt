package com.cavedu.android.TTS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.cavedu.android.ch7_TTS.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TTS extends Activity implements TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null;
	DataInputStream DATAIn = null;
	readThread mThread;
	Handler handler = new Handler();
	EditText nxtName ;
	Button buttonConnect;
	TextToSpeech tts;
	public static final int START = 0;
	public static final int STOP = 1;
	public static final int MESSAGE_TRANSFERRING = 4;
	int readValue ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //��l��
        tts = new TextToSpeech(this, this);
        nxtName =(EditText)findViewById(R.id.nxtName);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        //�إ߫��s�ƥ�
        buttonConnect.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				creatNXTConnect();				
			}        	
        });//setOnClickListener           
    }//onCreat
    //�ǥX���O
    public  void CommandNXT(int ORDER){

    	if(DATAOu==null){
    		return;
    	}   	
    	try {
			DATAOu.writeInt(ORDER);
			DATAOu.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
    }//CommandNXT
    //�إ��Ť��s�u
    public void creatNXTConnect(){
    	try {
        	BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        	if(BTAdapter==null){
            	Toast.makeText(this,"No Device found!",Toast.LENGTH_SHORT).show();
            	finish();
            }
        	BluetoothDevice  BTDevice = null;
    	    Set<BluetoothDevice> BTList = BTAdapter.getBondedDevices();//���D�X�b�o��
    	    if(BTList.size()>0){
    	        for(BluetoothDevice TempoDevice : BTList){
    		        if(TempoDevice.getName().equals(nxtName.getText().toString())){
    			        BTDevice = TempoDevice;
    		        }
    	        }
    	    }
    		BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    		BTSocket.connect();
    		DATAOu = new DataOutputStream(BTSocket.getOutputStream());
    		CommandNXT(START);
    		mThread = new readThread();
    		mThread.start();
    	    } catch (IOException e) {
    		    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show();
    	    }
    }//creatNXTConnect	
	public void onPause(){
		super.onPause();
		//�Ѱ����U�P�����ƥ�
		CommandNXT(STOP);
		if(BTSocket != null){
			try {
				BTSocket.close();
				BTSocket = null;
				DATAOu = null;
				DATAIn = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}//Stop	
	//�}�ҷs��������A�ݩ�_�����O�H�K�@��Activity����Ʀ���
	class readThread extends Thread{	
	    //���椺�e�g�brun()��k��
		public void run(){
			try {
			DataInputStream mInStream =  new DataInputStream(BTSocket.getInputStream());
				while(true){
					readValue = mInStream.readInt();
					tts.speak(String.valueOf(readValue), TextToSpeech.QUEUE_FLUSH, null);
					CommandNXT(START);
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}//Thread
	@Override
	//��@OnInitListener ��ť�ƥ󤤪�onInit��k
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS){
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Toast.makeText(this, "Language is not available.",Toast.LENGTH_SHORT).show();
			}
		}
	}//onInit	
}//Activity