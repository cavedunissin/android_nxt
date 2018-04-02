import lejos.nxt.*;
import lejos.util.Delay;
class Sample4_3
{
	public static void main(String args[]) throws Exception
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		
		TouchSensor touch = new TouchSensor(SensorPort.S1); 
//�إ�TouchSensor����A�NĲ�I�P�������b��J��1
		
		Motor.B.setSpeed(360);
		Motor.C.setSpeed(360);
		
		while(true)
		{
			if(touch.isPressed()) //�����ê��
			{
				//�٨�
				Motor.B.stop();
				Motor.C.stop();
				
				//�˰h0.8��
				Motor.B.backward();
				Motor.C.backward();
				Delay.msDelay(800);
				
				//���s0.5��
				Motor.B.forward();
				Motor.C.backward();
				Delay.msDelay(500);
			}//if
			else //�S���쪫��
			{
				//�~��樫
				Motor.B.forward();
				Motor.C.forward();
			}//else
		}//while
	}//main
}//Sample4_3
