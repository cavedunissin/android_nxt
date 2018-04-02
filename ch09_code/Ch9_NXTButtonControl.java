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
        //�p�G�s�u�S�����A�{���|�@�����d�b�a16��   
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		LCD.clear();
		System.out.println("Connected");
		//�إ߿�J��y
		DataInputStream dis = btc.openDataInputStream();//�إ߿�X��y
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
					else{//�t�~����turn�p�G���t�Ȱ��F����A���M���F�̵M����
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
