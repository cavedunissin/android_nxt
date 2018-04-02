import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class Ch9_NXTButtonControl{
	public static void main(String args[]) throws Exception{
		int turn;
		int targetPower;
		Button.ESCAPE.addButtonListener(new ButtonListener(){
            public void buttonPressed(Button b){System.exit(1);}
            public void buttonReleased(Button b){}
        });
		
		System.out.println("Connecting");
        //如果連線沒完成，程式會一直停留在地16行   
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		LCD.clear();
		System.out.println("Connected");
		//建立輸入串流
		DataInputStream dis = btc.openDataInputStream();//建立輸出串流
		Sound.twoBeeps();
		action:
		while(true){
			turn = dis.readInt();
			switch(Math.abs(turn)){
				case 700:
					if(turn>0){
						Motor.A.forward();
						Motor.B.forward();
					}
					else{//另外指示turn如果為負值馬達反轉，不然馬達依然正轉
						Motor.A.backward();
						Motor.B.backward();
					}
					Motor.A.setSpeed(turn);
					Motor.B.setSpeed(turn);
					break;
				case 200:
					Motor.A.setSpeed(turn);
					Motor.B.setSpeed(turn);
					if(turn>0){
                        Motor.A.forward();
						Motor.B.backward();					
					}	
					else{
						Motor.A.backward();
						Motor.B.forward();						
					}						
					break;
				case 0:
					Motor.A.stop();
					Motor.B.stop();
					break;
                default :
                    break action;
            }//switch						
		}//while(action)						
	}//main			
}//class					
