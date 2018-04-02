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
        
        //��l�Ƭɭ�
        controlPanel = new ControlPanel(this);
        
        //��l���Ť�
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
    
    //�s��NXT
    private void connectNxt()
    {
    	if(mode!=MODE_CONNECT_NXT) //�ˬd�Ҧ�
    		throw new IllegalArgumentException();
    	
    	String name;
    	BluetoothDevice nxt = null;
    	
    	if((name = ((EditText) findViewById(R.id.editNxtName)).getText().toString()).equals("")) //�ˬd�O�_���Ŧr��
    	{
    		Toast.makeText(this, "Please provide the name of your NXT", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
        Set<BluetoothDevice> devicesSet = adapter.getBondedDevices(); //���o�˸m�M��
        
        if(devicesSet.size()==0) //�䤣��˸m
        {
        	Toast.makeText(this, "No devices found", Toast.LENGTH_SHORT).show();
        	return;
        }
        
        for (BluetoothDevice device : devicesSet) //�j�M�˸m
        {
            if (device.getName().equals(name))
            {
            	nxt = device;
                break;
            }
        }
        
        if(nxt==null) //�䤣��˸m
        {
        	Toast.makeText(this, "NXT not found", Toast.LENGTH_SHORT).show();
        	return;
        }
        
        try
        {
        	//�إ�nxt socket
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
    
    //�]�w�Ҧ�
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
    	else //�D�k�Ѽ�
    		throw new IllegalArgumentException();
    }
    
    public synchronized void writeSpeed(int speedL, int speedR)
    {
    	//�n�e�X��byte�}�C
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
	
	//�غc�l
	public ControlPanel(TouchPadControl _core)
	{
		super((Context)_core);
		core = _core;
		
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        
        //�]�wpaint
        paint.setTextSize(20);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
	}
	
	//�ù��ؤo�ܧ�ƥ�
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w/2;
		centerY = h/2;
	}
	
    public void onDraw(Canvas canvas)
    {
    	super.onDraw(canvas);
    	
    	//�e�������
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
    	canvas.drawCircle((float)centerX, (float)centerY, (float) Math.min(centerX, centerY), paint);
    	
    	//�e�����I
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	canvas.drawCircle((float)x, (float)y, 10, paint);
    	
    	//�e�����t��r
        paint.setColor(Color.WHITE);
    	canvas.drawText("("+speedL+","+speedR+")", 20, 20, paint);
    }
    
    //Ĳ���ƥ�
    public boolean onTouch(View view, MotionEvent event)
    {
    	
    	if(event.getAction()==MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE) //���U�β��ʰʧ@
    	{
        	x = event.getX();
        	y = event.getY();
    	}
    	else if(event.getAction()==MotionEvent.ACTION_UP) //��}�ʧ@
    	{
    		x = centerX;
    		y = centerY;
    	}
    	
    	//�p��speed
    	speed = (int)( Math.sqrt(Math.pow(x-centerX, 2) + Math.pow(y-centerY, 2))*900d/Math.min(centerX, centerY) );
    	if(speed>900)
    		speed = 900;
    	
    	//�p��angel
    	angel = (int) (Math.atan2(centerY-y, x-centerX)*180/Math.PI);
    	if(angel<0)
    		angel = 360+angel;
    	
    	if(angel>=0 && angel<=90) //�Ĥ@�H��
    	{
    		speedL = speed;
    		speedR = speed/45*angel-speed;
    	}
    	else if(angel>90 && angel<=180) //�ĤG�H��
    	{
    		speedL = -speed/45*angel+3*speed;
    		speedR = speed;
    	}
    	else if(angel>180 && angel <=270) //�ĤT�H��
    	{
    		speedL = -speed;
    		speedR = -speed/45*angel+5*speed;
    	}
    	else //�ĥ|�H��
    	{
    		speedL = speed/45*angel-7*speed;
    		speedR = -speed;
    	}
    	
    	core.writeSpeed(speedL, speedR); //�e�X�t�׵�NXT   
    	
    	invalidate(); //��ø�ù�
		return true;
    }
}