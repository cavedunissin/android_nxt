import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.Delay;

class ch6_NXT_3{
    public static void main(String args[]) throws Exception {
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		final int START = 0,STOP = 1;
		int command ;
		int value;
		System.out.println("Waiting");
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		LightSensor l1 = new LightSensor(SensorPort.S1);
		DataOutputStream dos = btc.openDataOutputStream();
		DataInputStream dis = btc.openDataInputStream();
		while(true){
		    command = dis.readInt();
			switch(command){
		    case START:
		        value = l1.readValue();
			    dos.writeInt(value);
			    System.out.println(value);
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
