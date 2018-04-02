package com.cavedu.android.computerBT;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.IOException;


import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class ComputerBT extends JFrame{
	
	static StreamConnectionNotifier stcNo = null;
	static StreamConnection stconnection = null;
	static int command ;
    public static final String myUUID = "04c6093b00001000800000805f9b34fb";
    public static final int FORWARD = 1;
    public static final int TURN_LEFT = 2;
    public static final int TURN_RIGHT = 3;
    public static final int BACKWARD = 4;
    public static final int TAKE_PICTURE = 5;
    public static final int MOVE_CAMERA = 6;
    public static final int SAY_HELLO = 7;
    static DataOutputStream mOutputStream;
    static DataOutputStream nxtOuputStream;

    JButton btnForward;
    JButton btnBackward;
    JButton btnLeft;
    JButton btnRight;
    JButton btnPicture;
    JButton connectToNXT;
    JButton sayHello;
	/**
	 * @param args
	 */
    public ComputerBT(){
    	
    	this.setSize(320,340);
    	this.setResizable(false);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.getContentPane().setLayout(null);
    	this.setLocationRelativeTo(null);
    	
    	connectToNXT = new JButton("NXT"); //�ŧiJButton
    	connectToNXT.setBounds(0,0, 100, 100); //�]�w�j�p��m
    	connectToNXT.addMouseListener(new MouseAction(nxtOuputStream,MOVE_CAMERA));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(connectToNXT); //�N���s�[�J����
    	
    	btnPicture = new JButton("Click"); //�ŧiJButton
    	btnPicture.setBounds(200, 0, 100, 100); //�]�w�j�p��m
    	btnPicture.addMouseListener(new MouseAction(mOutputStream,TAKE_PICTURE ));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(btnPicture); //�N���s�[�J����
    	
    	btnBackward = new JButton("��"); //�ŧiJButton
    	btnBackward.setBounds(100, 0, 100, 100); //�]�w�j�p��m
    	btnBackward.addMouseListener(new MouseAction(nxtOuputStream,FORWARD));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(btnBackward); //�N���s�[�J����
    	
    	btnForward = new JButton("��"); //�ŧiJButton
    	btnForward.setBounds(100,100, 100, 100); //�]�w�j�p��m
    	btnForward.addMouseListener(new MouseAction(nxtOuputStream,BACKWARD));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(btnForward); //�N���s�[�J����
    	
    	btnLeft = new JButton("��"); //�ŧiJButton
    	btnLeft.setBounds(0,100, 100, 100); //�]�w�j�p��m
    	btnLeft.addMouseListener(new MouseAction(nxtOuputStream,TURN_LEFT));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(btnLeft); //�N���s�[�J����
    	
    	btnRight = new JButton("��"); //�ŧiJButton
    	btnRight.setBounds(200,100, 100, 100); //�]�w�j�p��m
    	btnRight.addMouseListener(new MouseAction(nxtOuputStream,TURN_RIGHT));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(btnRight); //�N���s�[�J����
    	
    	sayHello = new JButton("Say Hello"); //�ŧiJButton
    	sayHello.setBounds(0,200, 300, 100); //�]�w�j�p��m
    	sayHello.addMouseListener(new MouseAction(mOutputStream,SAY_HELLO));//�[�J�ƹ��ƥ�
    	this.getContentPane().add(sayHello); //�N���s�[�J����
    	
    	this.setVisible(true);
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConnectWitAndroid();
		//��new�Ӱ���غc�l�A���n�b�P����s�u�إ߫�~�|�X�{
		ComputerBT CBT = new ComputerBT();
	}//main	
	public static void ConnectWitAndroid(){
		//�D�{���إ߻PAndroid������Ū޳s�u
		LocalDevice localDevice ;
		try {
			localDevice = LocalDevice.getLocalDevice();
			//�]�w access code �� General/Unlimited Inquiry Access Code (GIAC).
			localDevice.setDiscoverable(DiscoveryAgent.GIAC);
			UUID uuid = new UUID(myUUID, false);
			String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
			System.out.println("Waiting for Android to connect...");
			stcNo = (StreamConnectionNotifier) Connector.open(url);
			//�{���|�b�����ݤ���s�u���
			stconnection = stcNo.acceptAndOpen();
			System.out.println("Connect to Android Success!");
			mOutputStream = stconnection.openDataOutputStream();
			//����s�u�إߧ���
			System.out.println("Connect to NXT ...");
			NXTConnector conn = new NXTConnector();
			if(!conn.connectTo("kevin", "", NXTCommFactory.BLUETOOTH)){
				JOptionPane.showMessageDialog(null, "Cannot connect to NXT","Error", JOptionPane.ERROR_MESSAGE); //�s�u���ѰT��
				System.exit(1);
			}//if
			nxtOuputStream = conn.getDataOut();
			System.out.println("Connect to NXT success!!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}//JRFrame
//�w�q���OĲ�o�ƥ�
class MouseAction implements MouseListener{
	DataOutputStream mmOutputStream = null;
	DataOutputStream mNXTDataOu = null;
	int commandNumber;
	public static final int STOP = 9;
	
	MouseAction(DataOutputStream DATAOu,int cmd){
			commandNumber = cmd;
			if(commandNumber == 5 ||commandNumber == 7){
				mmOutputStream = DATAOu;
			}
			else{
				mNXTDataOu = DATAOu;
			}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		//�I�����s��� 
		if(commandNumber == 5 || commandNumber == 7){
			try {
				//�����������y
				mmOutputStream.writeInt(commandNumber);
				mmOutputStream.flush();
				if(commandNumber == 5){
					System.out.println("Take the picture");
				}
				else{
					System.out.println("Say Hello");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(commandNumber!=5 && commandNumber!=7){
			try {
				//����NXT����y
				mNXTDataOu.writeInt(commandNumber);
				mNXTDataOu.flush();
				System.out.println("Robot move, action number is "+commandNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}//mousePressed
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(commandNumber!=5 && commandNumber!=7){
			try {
				//����NXT����y
				mNXTDataOu.writeInt(STOP);
				mNXTDataOu.flush();
				System.out.println("Robot Stop");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}//MouseListener()

