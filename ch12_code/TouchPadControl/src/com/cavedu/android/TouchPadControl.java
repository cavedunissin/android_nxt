package com.cavedu.android;

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
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TouchPadControl extends Activity
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
        controlPanel = new ControlPanel(this);
        
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
    
    public synchronized void writeSpeed(int speedL, int speedR)
    {
    	//要送出的byte陣列
    	byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, (byte)(speedL/9), 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                		0x0c, 0x00, (byte) 0x80, 0x04, 0x01, (byte)(speedR/9), 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
    	
        try
        {
			nxtDataOut.write(data);
		}
        catch (IOException e)
		{
			e.printStackTrace();
        	Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        	setMode(MODE_CONNECT_NXT);
		}
    }
}

class ControlPanel extends View implements OnTouchListener 
{
	double x, y, centerX, centerY;
	int speed, angel, speedL, speedR;
	Paint paint = new Paint();
	TouchPadControl core;
	
	//建構子
	public ControlPanel(TouchPadControl _core)
	{
		super((Context)_core);
		core = _core;
		
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        
        //設定paint
        paint.setTextSize(20);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
	}
	
	//螢幕尺寸變更事件
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w/2;
		centerY = h/2;
	}
	
    public void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	
    	//畫控制邊界
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
    	canvas.drawCircle((float)centerX, (float)centerY, (float) Math.min(centerX, centerY), paint);
    	
    	//畫控制點
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	canvas.drawCircle((float)x, (float)y, 10, paint);
    	
    	//畫表示轉速文字
        paint.setColor(Color.WHITE);
    	canvas.drawText("("+speedL+","+speedR+")", 20, 20, paint);
    }
    
    //觸控事件
    public boolean onTouch(View view, MotionEvent event)
    {
    	
    	if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE) //壓下或移動動作
    	{
        	x = event.getX();
        	y = event.getY();
    	}
    	else if(event.getAction()==MotionEvent.ACTION_UP) //放開動作
    	{
    		x = centerX;
    		y = centerY;
    	}
    	
    	//計算speed
    	speed = (int)( Math.sqrt(Math.pow(x-centerX, 2) + Math.pow(y-centerY, 2))*900d/Math.min(centerX, centerY) );
    	if(speed>900)
    		speed = 900;
    	
    	//計算angel
    	angel = (int) (Math.atan2(centerY-y, x-centerX)*180/Math.PI);
    	if(angel<0)
    		angel = 360+angel;
    	
    	if(angel>=0 && angel<=90) //第一象限
    	{
    		speedL = speed;
    		speedR = speed/45*angel-speed;
    	}
    	else if(angel>90 && angel<=180) //第二象限
    	{
    		speedL = -speed/45*angel+3*speed;
    		speedR = speed;
    	}
    	else if(angel>180 && angel <=270) //第三象限
    	{
    		speedL = -speed;
    		speedR = -speed/45*angel+5*speed;
    	}
    	else //第四象限
    	{
    		speedL = speed/45*angel-7*speed;
    		speedR = -speed;
    	}
    	
    	core.writeSpeed(speedL, speedR); //送出速度給NXT   
    	
    	invalidate(); //重繪螢幕
		return true;
    }
}