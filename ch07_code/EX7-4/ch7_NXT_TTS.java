import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.Delay;

class ch7_NXT_TTS{
    public static final int START = 0,STOP = 1;
    public static void main(String args[]) throws Exception {
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		int command ;
		int value = 0;
		int distance;
		System.out.println("Waiting");
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		UltrasonicSensor l1 = new UltrasonicSensor(SensorPort.S1);
		DataInputStream dis = btc.openDataInputStream();
		DataOutputStream dos = btc.openDataOutputStream();
		Sound.twoBeeps();
		while(true){
		    command = dis.readInt();
			switch(command){
		    case START:
		        distance = l1.getDistance();
				Delay.msDelay(2000);
				dos.writeInt(distance);
				dos.flush();
                break;
            case STOP:
			    dis.close();
                dos.close();
				btc.close();
                System.exit(1);
				break;
            }    				
			}//while
			
	}
		
}		
