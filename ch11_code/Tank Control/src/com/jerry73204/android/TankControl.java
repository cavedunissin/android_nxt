package com.jerry73204.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TankControl extends Activity
{
    private ControlPanel controlPanel;
	private BluetoothAdapter adapter;
	private BluetoothSocket nxtSocket;
	public DataInputStream nxtDataIn;
	public DataOutputStream nxtDataOut;
    public final int MODE_CONNECT_NXT = 0, MODE_CONTROL = 1;
    private int mode;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //初始化界面
        controlPanel = new ControlPanel(this, this);
        
        //初始化藍牙
    	adapter = BluetoothAdapter.getDefaultAdapter();
    	if(adapter==null)
        {
        	Toast.makeText(this, "No Bluetooth adapter found", Toast.LENGTH_SHORT).show();
        	this.finish();
        }
    	if(!adapter.isEnabled())
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
    	
    	setMode(MODE_CONNECT_NXT);
    	
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
    	
    }
    
    //連結NXT
    private void connectNxt()
    {
    	if(mode!=MODE_CONNECT_NXT) //檢查模式
    		throw new IllegalArgumentException();
    	
    	String name;
    	BluetoothDevice nxt = null;
    	
    	if((name = ((EditText) findViewById(R.id.editNxtName)).getText().toString()).equals("")) //檢查是否為空字串
    	{
    		Toast.makeText(this, "Please provide the name of your NXT", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
        Set<BluetoothDevice> devicesSet = adapter.getBondedDevices(); //取得裝置清單
        
        if(devicesSet.size()==0) //找不到裝置
        {
        	Toast.makeText(this, "No devices found", Toast.LENGTH_SHORT).show();
        	return;
        }
        
        for (BluetoothDevice device : devicesSet) //搜尋裝置
        {
            if (device.getName().equals(name))
            {
            	nxt = device;
                break;
            }
        }
        
        if(nxt==null) //找不到裝置
        {
        	Toast.makeText(this, "NXT not found", Toast.LENGTH_SHORT).show();
        	return;
        }
        
        try
        {
        	//建立nxt socket
			nxtSocket = nxt.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			nxtSocket.connect();
			nxtDataOut = new DataOutputStream(nxtSocket.getOutputStream());
			nxtDataIn = new DataInputStream(nxtSocket.getInputStream());
		}
        catch(IOException e)
		{
        	Toast.makeText(this, "Connection failure", Toast.LENGTH_SHORT).show();
        	return;
		}

    	Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		setMode(MODE_CONTROL);
    }
    
    
    //設定模式
    public void setMode(int _mode)
    {
    	mode = _mode;
    	if(mode==MODE_CONNECT_NXT)
    	{
    		setContentView(R.layout.main);
    		((Button) findViewById(R.id.buttonConnect)).setOnClickListener(new Button.OnClickListener() {
    			public void onClick(View arg0) {connectNxt();}
            });
    	}
    	else if(mode==MODE_CONTROL)
    	{
    		setContentView(controlPanel);
    	}
    	else //非法參數
    		throw new IllegalArgumentException();
    }
    
}

class ControlPanel extends View implements OnTouchListener 
{
	double x, y, centerX, centerY;
	int speed, angle, speedL, speedR;
	Paint paint = new Paint();
	TankControl core;
	
	public ControlPanel(Context context, TankControl _core)
	{
		super(context);
		core = _core;
		
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w/2;
		centerY = h/2;
	}
	
    public void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	canvas.drawCircle((float)x, (float)y, 5, paint);
    	canvas.drawText("("+speed+","+angle+")", 20, 20, paint);
    	//core.setMode(core.MODE_CONNECT_NXT);
    }
    
    public boolean onTouch(View view, MotionEvent event)
    {
    	
    	if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE)
    	{
        	x = event.getX();
        	y = event.getY();
    	}
    	else if(event.getAction()==MotionEvent.ACTION_UP)
    	{
    		x = centerX;
    		y = centerY;
    	}
    	
    	speed = (int)( Math.sqrt(Math.pow(x-centerX, 2) + Math.pow(y-centerY, 2))*900d/Math.min(centerX, centerY) );
    	//speed = event.getAction();
    	
    	if(speed>900)
    		speed = 900;
    	
    	angle = (int) (Math.atan2(centerY-y, x-centerX)*180/Math.PI);
    	if(angle<0)
    		angle = 360+angle;
    	
    	if(angle>=0 && angle<=90)
    	{
    		speedL = speed;
    		speedR = speed/45*angle-speed;
    	}
    	else if(angle>90 && angle<=180)
    	{
    		speedL = -speed/45*angle+3*speed;
    		speedR = speed;
    	}
    	else if(angle>180 && angle <=270)
    	{
    		speedL = -speed;
    		speedR = -speed/45*angle+5*speed;
    	}
    	else
    	{
    		speedL = speed/45*angle-7*speed;
    		speedR = -speed;
    	}
    	
    	try {
			core.nxtDataOut.writeInt((speedL<0 ? 1 : 0 )*10000000 + speedL*10000 + (speedR<0 ? 1 : 0 )*1000 + Math.abs(speedR));
		} catch (IOException e) {}
    	
    	invalidate();
		return true;
    }
}