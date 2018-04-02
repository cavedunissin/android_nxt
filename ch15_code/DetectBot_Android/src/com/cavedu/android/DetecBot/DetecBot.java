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
		//螢幕不會顯示標題(檔案設定標題為ch7_camera)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		//註冊螢幕上SurfaceView物件
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView1);
		//取得影像容器來裝畫面
		mSurfaceHolder = mSurfaceView.getHolder();
		//讓主程式實作Callback介面
		mSurfaceHolder.addCallback(this);
		//代表影像由Camera來提供相關數據
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
    //此物件是用來處理被拍下的照片
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {
				//儲存照片的方法
				StoreImage(mContext, imageData, 50,"ImageName");
				//儲存完照片後繼續預覽相機
				mCamera.startPreview();
			}
		}
	};//PictureCallback

	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		//檢測如果預覽正在進行則停止預覽
		if (isPreviewRunning) {
			mCamera.stopPreview();
		}
		//處理相片的參數
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(WIDTH, HEIGH);
		mCamera.setParameters(p);
		try {
			//設定影像的容器
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		isPreviewRunning = true;
	}//surfaceChanged
	
	//當預覽畫面被銷毀前呼叫此方法
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		isPreviewRunning = false;
		mCamera.release();
	}//surfaceDestroyed
	
	public void StoreImage(Context mContext, byte[] imageData,int quality, String expName) {
		//File類別，需要傳入檔案處存的路徑
		SendPictureToPC(imageData);
        File sdImageMainDirectory = new File("/sdcard/DCIM/Camera/image" + System.currentTimeMillis() + ".jpg");
		FileOutputStream fileOutputStream = null;
		try {
			BitmapFactory.Options options=new BitmapFactory.Options();
			//用inSampleSize來處理照片大小，1代表和預覽1比1大小
			options.inSampleSize = 1;
			//將傳入StoreByteImage方法內的影像陣列解碼
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,imageData.length,options);
			//建立檔案輸出串流
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
    //建立藍牙連線
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
    
	//開啟新的執行續，屬於巢狀類別以便共用Activity的資料成員
	class readThread extends Thread{	
	    //執行內容寫在run()方法內
		public void run(){
			try {
			
				while(true){
					//如果讀不到值就會block在此行
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