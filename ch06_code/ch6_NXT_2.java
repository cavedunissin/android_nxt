import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

class ch6_NXT_2{
    public static void main(String args[]){
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		char command = 'a';
		System.out.println("Waiting");
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		LightSensor l1 = new LightSensor(SensorPort.S1);
		DataInputStream dis = btc.openDataInputStream();
		LCD.clear();
		while(true){
		    try{
		        command = dis.readChar();
				}catch(IOException e){
				    System.exit(1);
				}//=a=a=a=  aaa
			System.out.print(command);
			}//while
	}//main
}//class		
