import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.Delay;

public class ch7_NXT_Acceler{
    public static final int START = 0;
	public static final int STOP = 1;
	public static final int ONESTEP = 2;
	public static final int NONESTEP = 3;
    public static void main(String args[]) throws Exception{
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
	    LCD.drawString("waiting",0,0);
        BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		LCD.clear();
		LCD.drawString("Connect success!",0,0);
		DataInputStream dis = btc.openDataInputStream();
		int readValue = 4;
		int count = 0 ;
        Motor.A.setSpeed(300);
		Motor.B.setSpeed(300);		
		while(readValue != START){
		    readValue = dis.readInt();
			}
		while(readValue != STOP){
		    readValue = dis.readInt();
			//當手機上下搖晃一次時機器人前進
			if(readValue == ONESTEP){
			    Motor.A.forward();
				Motor.B.forward();
				count++;
				//顯示計算的步伐
				System.out.println(count);
			    }
			else{
			    Motor.A.stop();
				Motor.B.stop();
			    }
		    }
		btc.close();
		dis.close();
		}
	}
		