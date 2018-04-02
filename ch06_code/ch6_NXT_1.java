import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

class ch6_NXT_1{
    public static void main(String args[]){
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		System.out.println("Waiting");
		//µ¥«Ý¿é¤J¦ê¬y
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		DataInputStream dis = btc.openDataInputStream();
		while(true){}
	}//main
}//class		
