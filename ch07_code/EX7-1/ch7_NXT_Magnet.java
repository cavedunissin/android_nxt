import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.Delay;

public class ch7_NXT_Acceler{
    public static final int START = 0;
	public static final int STOP = 10000;
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
		int readValue = 2;
		while(readValue != START){
		    readValue = dis.readInt();
			}
		while(readValue != STOP){
		    readValue = dis.readInt();
			if(readValue<1500 && readValue>30){
			    Sound.playTone(readValue,40);
			    }
		    }
		btc.close();
		dis.close();
		}
	}
		