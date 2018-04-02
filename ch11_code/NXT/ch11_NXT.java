import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
public class ch11_NXT
{
	public static void main(String args[])
	{
		Button.ESCAPE.addButtonListener(new ButtonListener(){
				public void buttonPressed(Button b){System.exit(0);}
				public void buttonReleased(Button b){}
		});
		NXTConnection conn;
		DataInputStream dataIn;
		DataOutputStream dataOut;
		int cmd, speedL, speedR;
		
		LCD.drawString("Waiting...", 0, 0);
		conn = Bluetooth.waitForConnection();
		dataIn = conn.openDataInputStream();
		dataOut = conn.openDataOutputStream();
		LCD.drawString("Connected", 0, 0);
		
		try{
		while(true)
		{
			cmd = dataIn.readInt();
			speedL = cmd/10000%1000 * (cmd/10000000==1 ? -1 : 1 );
			speedR = cmd%1000 * (cmd/1000%10==1 ? -1 : 1 );
			
			Motor.B.setSpeed(Math.abs(speedL));
			Motor.C.setSpeed(Math.abs(speedR));
			
			if(speedL==0)
				Motor.B.stop();
			else if(speedL>0)
				Motor.B.forward();
			else
				Motor.B.backward();
			
			if(speedR==0)
				Motor.C.stop();
			else if(speedR>0)
				Motor.C.forward();
			else
				Motor.C.backward();
		}
		} catch(IOException e) {System.exit(1);}
	}
}