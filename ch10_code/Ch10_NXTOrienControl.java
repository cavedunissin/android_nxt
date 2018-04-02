import lejos.nxt.*;
import lejos.util.Delay;
import lejos.nxt.comm.*;
import java.io.*;

class Ch10_NXTOrienControl{
    
    public static void main(String args[]){
	    Button.ESCAPE.addButtonListener(new ButtonListener(){
		    public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
			});
		int order,command,turn,forward,check;
		System.out.println("Connecting");
		BTConnection BTC = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		Sound.beep();
		System.out.println("Connect success!");
		DataInputStream datain = BTC.openDataInputStream();
		DataOutputStream dos = BTC.openDataOutputStream();
		try{
		    while(true){
			    command = datain.readInt();
				order = command/100000;
				command = command%100000;
				turn = (command/1000)*10;
				command = command%1000;
				forward = (command/10)*10;
				command = command%10;
				check = command;
			if(check==1){
				switch(order){
				    case 0:
					    Motor.A.stop();
						Motor.B.stop();
						System.exit(1);
						break;
				    case 1: 
					    Motor.A.setSpeed(0);
						Motor.B.setSpeed(0);
						break;
					case 2:  //pp
					    Motor.A.setSpeed(forward+turn);
						Motor.B.setSpeed(forward-turn);
					    Motor.A.forward();
						Motor.B.forward();
						break;
					case 3:  //pn
					    Motor.A.setSpeed(forward-turn);
						Motor.B.setSpeed(forward+turn);
					    Motor.A.forward();
						Motor.B.forward();
						break;
					case 4:  //np
					    Motor.A.setSpeed(forward+turn);
						Motor.B.setSpeed(forward-turn);
					    Motor.A.backward();
						Motor.B.backward();
						break;
					case 5:  //nn
					    Motor.A.setSpeed(forward-turn);
						Motor.B.setSpeed(forward+turn);
					    Motor.A.backward();
						Motor.B.backward();
						break;
					default:
					    System.out.println("No command");
					    break;
					}
				}
				    dos.writeInt(1);
				    dos.flush();
				    check=0;
		        }//while
		    }catch(IOException e){
			    System.out.println("Wrong connection during command"+e.toString());
				Sound.buzz();
				Delay.msDelay(4000);
				}
			
		}
	}