import lejos.nxt.*; 
import lejos.util.Delay; 
class Sample4_2
{
	public static void main(String args[])
	{
		//�w�q������i�H�����{��
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(0);}
			public void buttonReleased(Button b){}
		});
		
		//�]�w�t��
		Motor.B.setSpeed(600); //�]�wB���F�t�׬�600(��/��) 
		Motor.C.setSpeed(600); //�]�wC���F�t�׬�600(��/��)
		
		//���l�e�i3.5��
		Motor.B.forward(); //B���F����
		Motor.C.forward(); //C���F����
		Delay.msDelay(3500); //����3.5��
		
		//���l����2.5��
		Motor.B.stop(); //B���F����
		Delay.msDelay(2500); //����2.5��
		
		//���l�˰h2��
		Motor.B.backward(); //B���F����
		Motor.C.backward(); //C���F����
		Delay.msDelay(2000); //����2��
	}//main
}//Sampl4_2
