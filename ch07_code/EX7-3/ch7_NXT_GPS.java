import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.Delay;

class ch7_NXT_GPS{
	public static final int START = 1,STOP = 2;
    public static void main(String args[]) throws Exception {
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		
		int command  = 3;
		System.out.println("Waiting");
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		LightSensor l1 = new LightSensor(SensorPort.S1);
		DataInputStream dis = btc.openDataInputStream();
		while(command != START){
			command = dis.readInt();
		}
		//收到STOP變數後機器人會停下，不然就一直前進
		while(command != STOP){
			command = dis.readInt();
			Motor.A.setSpeed(300);
			Motor.B.setSpeed(300);
			Motor.A.forward();
			Motor.B.forward();
		}
		Motor.A.stop();
		Motor.B.stop();
	}
		
}		
