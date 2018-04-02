import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
class Ch15_NXTBot{
	public static final int FWD = 1; 
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int BACK = 4; 
	public static final int MOVE_CAMERA = 6;
	public static final int STOP = 9;
public static void main(String args[]){
	Button.ESCAPE.addButtonListener(new ButtonListener() {
	public void buttonPressed(Button b) {System.exit(1);}
	public void buttonReleased(Button b) {}
	});
	int cmd;
	int count = 1;
	System.out.println("Waiting...");
	BTConnection btc = Bluetooth.waitForConnection();
	DataInputStream dataIn = btc.openDataInputStream();
	System.out.println("Connect Success!");
	Motor.A.setSpeed(300);
	Motor.B.setSpeed(300);
	Motor.C.setSpeed(200);
	Sound.twoBeeps();
	try{
		while(true){
			cmd = dataIn.readInt(); //±µ¦¬©R¥O
			switch(cmd){
				case 1:
					Motor.A.forward();
					Motor.B.forward();
					break;
				case 2:
					Motor.A.stop();
					Motor.B.forward();
					break;
				case 3:
					Motor.A.forward();
					Motor.B.stop();
					break;
				case 4:
					Motor.A.backward();
					Motor.B.backward();
					break;
				case MOVE_CAMERA:
					if(count>0){
					Motor.C.forward();
					}
					else{
					Motor.C.backward();
					}
					count = count*(-1);
					Sound.beep();
					break;
				case STOP:
					Motor.A.stop();
					Motor.B.stop();
					Motor.C.stop();
					break;
				default:
					break;
				}
			}//while
		} catch(IOException e) {System.exit(1);}
	}//main
}