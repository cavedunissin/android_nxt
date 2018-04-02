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
//�إ�SoundSensor����A�N�n���P�������b��J��2
		
		Delay.msDelay(1000); //1���}�l�A�w���Ұʪ��n���v�T
		while(sound.readValue()<80) {} 
//�����n���Ȥj��80�A�_�h�|�@���d�b���j��
		while(sound.readValue()>=80) {} //�����n���ܤp
		
		//���l�e�i
		Motor.B.forward();
		Motor.C.forward();
		
		while(sound.readValue()<80) {} //�����n���Ȥj��80
		
		//���l�e�i
		Motor.B.stop();
		Motor.C.stop();
	}//main
}//Sample4_4
