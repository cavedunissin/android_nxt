package com.cavedu.android.DetecBot;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.cavedu.android.ch16_DetecBot.R;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DetecBot extends Activity  implements SurfaceHolder.Callback{
    /** Called when the activity is first created. */
	Button btn_connect;
	EditText edt_name;
	BluetoothAdapter BTAdapter = null;
	BluetoothSocket BTSocket = null;
	DataOutputStream DATAOu = null;
	DataInputStream mInStream;
	readThread mThread = null;
	Camera mCamera;
	SurfaceView mSurfaceView1;
	Context mContext = this;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	public static final int WIDTH = 320;
	public static final int HEIGH = 240;
	public static final int DETECTED = 5;
	int readValue;
	boolean Ifpreview = false;
	boolean isPreviewRunning = false;
    @Override
 
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�ù����|��ܼ��D(�ɮ׳]�w���D��ch7_camera)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		//���U�ù��WSurfaceView����
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView1);
		//���o�v���e���Ӹ˵e��
		mSurfaceHolder = mSurfaceView.getHolder();
		//���D�{����@Callback����
		mSurfaceHolder.addCallback(this);
		//�N��v����Camera�Ӵ��Ѭ����ƾ�
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		edt_name = (EditText)findViewById(R.id.edt_name);
		btn_connect = (Button)findViewById(R.id.btn_connect);
		btn_connect.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				creatPCConnect();
				v.setVisibility(View.GONE);
			}
		});
	}
    //������O�ΨӳB�z�Q��U���Ӥ�
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {
				//�x�s�Ӥ�����k
				StoreImage(mContext, imageData, 50,"ImageName");
				//�x�s���Ӥ����~��w���۾�
				mCamera.startPreview();
			}
		}
	};//PictureCallback

	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		//�˴��p�G�w�����b�i��h����w��
		if (isPreviewRunning) {
			mCamera.stopPreview();
		}
		//�B�z�ۤ����Ѽ�
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(WIDTH, HEIGH);
		mCamera.setParameters(p);
		try {
			//�]�w�v�����e��
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		isPreviewRunning = true;
	}//surfaceChanged
	
	//��w���e���Q�P���e�I�s����k
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		isPreviewRunning = false;
		mCamera.release();
	}//surfaceDestroyed
	
	public void StoreImage(Context mContext, byte[] imageData,int quality, String expName) {
		//File���O�A�ݭn�ǤJ�ɮ׳B�s�����|
		SendPictureToPC(imageData);
        File sdImageMainDirectory = new File("/sdcard/DCIM/Camera/image" + System.currentTimeMillis() + ".jpg");
		FileOutputStream fileOutputStream = null;
		try {
			BitmapFactory.Options options=new BitmapFactory.Options();
			//��inSampleSize�ӳB�z�Ӥ��j�p�A1�N��M�w��1��1�j�p
			options.inSampleSize = 1;
			//�N�ǤJStoreByteImage��k�����v���}�C�ѽX
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,imageData.length,options);
			//�إ��ɮ׿�X��y
			fileOutputStream = new FileOutputStream(sdImageMainDirectory);				
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			
			myImage.compress(CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//StoreImage	
    //�إ��Ť��s�u
    public void creatPCConnect(){
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
    		        if(TempoDevice.getName().equals(edt_name.getText().toString())){
    			        BTDevice = TempoDevice;
    		        }
    	        }
    	    }
    		BTSocket = BTDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    		BTSocket.connect();
    		mInStream =  new DataInputStream(BTSocket.getInputStream());
    		DATAOu = new DataOutputStream(BTSocket.getOutputStream());
    		mThread = new readThread();
    		mThread.start();
    	    } catch (IOException e) {
    		    Toast.makeText(this, "Wrong", Toast.LENGTH_LONG).show();
    	    }
    }//creatPCConnect
    public void SendPictureToPC(byte[] image){
    	try {
			DATAOu.write(image);
			DATAOu.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	//�}�ҷs��������A�ݩ�_�����O�H�K�@��Activity����Ʀ���
	class readThread extends Thread{	
	    //���椺�e�g�brun()��k��
		public void run(){
			try {
			
				while(true){
					//�p�GŪ����ȴN�|block�b����
					readValue = mInStream.readInt();
					if(readValue == DETECTED){
						Message message = new Message();
						message.what = 1;
						mHandler.sendMessage(message);
						Log.i("takepicture","f");
					}
					
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}//Thread 
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			 switch (msg.what) {
			 case 1: 
				mCamera.takePicture(null, mPictureCallback, mPictureCallback);		
	            break;
			 }
			 super.handleMessage(msg);
		}
	};//Handler
    public void onPause() {
        super.onPause();
        if(BTSocket!=null){
        	try {
    			BTSocket.close();
    			DATAOu.close();
    			Log.i("taken","onPause_DataOu");
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } 
    }
	public void onResume() {
		super.onResume();
	}
	
	public void onStop() {
		super.onStop();
	}

}