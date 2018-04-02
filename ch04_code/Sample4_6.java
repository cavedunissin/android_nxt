import lejos.nxt.*;
class Sample4_6
{
	public static void main(String args[]) throws Exception
	{ 
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4); 
//�إ�UltrasonicSensor����A�N�W���i�P�������b��J��4
		int distance, limit = 15; //�]�w�O�����Z����15����
		
		Motor.B.setSpeed(200);
		Motor.C.setSpeed(200);
		
		while(true)
		{
			distance = us.getDistance();
			if(distance==255 || distance==limit) //�S������ζZ����n
			{
				Motor.B.stop();
				Motor.C.stop();
			}//if
			else if(distance>limit) //����ӻ��A�����H�V�e��
			{
				Motor.B.forward();
				Motor.C.forward();
			}//else if
			else if(distance<limit) //����Ӫ�A�����H�V��h
			{
				Motor.B.backward();
				Motor.C.backward();
			}//else if
		}//while
	}//main
}//Sample4_6
