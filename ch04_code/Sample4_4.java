import lejos.nxt.*;
import lejos.util.Delay;
class Sample4_4
{
	public static void main(String args[])
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		
		SoundSensor sound = new SoundSensor(SensorPort.S2); 
//建立SoundSensor物件，將聲音感應器接在輸入端2
		
		Delay.msDelay(1000); //1秒後開始，預防啟動的聲音影響
		while(sound.readValue()<80) {} 
//等待聲音值大於80，否則會一直留在本迴圈
		while(sound.readValue()>=80) {} //等待聲音變小
		
		//車子前進
		Motor.B.forward();
		Motor.C.forward();
		
		while(sound.readValue()<80) {} //等待聲音值大於80
		
		//車子前進
		Motor.B.stop();
		Motor.C.stop();
	}//main
}//Sample4_4
