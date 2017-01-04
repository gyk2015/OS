package processManagement;

import deviceManagement.Process;
import deviceManagement.Device;
import memoryManage.Size;
import view.View;

import java.util.concurrent.BrokenBarrierException;

public class Processor {
	public static int pAddress=0;
	public static Size currentSize;
	public static byte PSW,AX,IR,PC,PID;
	private TimeCounter timeCounter;
	private ProcessControl pControler;
	private View view;
	public Processor(ProcessControl pControler,TimeCounter timeCounter,View view){
		this.pControler = pControler;
		this.timeCounter = timeCounter;
		this.view = view;
	}
	
	public static synchronized void setPSW(int PSW){
		Processor.PSW =(byte) PSW;
	}
	private boolean decodeAndExcute(){
		byte opcode,operand1,operand2;
		IR = currentSize.getIns(0x00ff&PC);
		System.out.println("pc: "+(0x00ff&PC));
		opcode = (byte) (IR&0xe0);
		PC++;
		switch(opcode){
			case 0x20:{//�����룺0010---x=?��ֵ
					operand1 = (byte) (IR&0x1f);
					AX = operand1;
					view.getHR_activeComand().setText("����ִ��ָ�IR: X = "+ operand1);
					view.getHR_current_result().setText("�����м�����AX : "+ AX);
				};break;
			case 0x40:{//�����룺0100---x++����
					AX++;
					view.getHR_activeComand().setText("����ִ��ָ�IR: X++ ");
					view.getHR_current_result().setText("�����м�����AX : "+ AX);
				};break;
			case 0x60:{//�����룺0110---x--�Լ�
					AX--;
					view.getHR_activeComand().setText("����ִ��ָ�IR: X-- ");
					view.getHR_current_result().setText("�����м�����AX : "+ AX);
				};break;
			case (byte) 0x80:{//�����룺1000---!??����I/O
					operand1 = (byte) (IR&0x18);//��ȡ�豸�� 0--�豸A��1---�豸B��2---�豸C
					operand2 = (byte)(IR&0x07);//��ȡʹ���豸��ʱ��
					view.getHR_activeComand().setText("����ִ��ָ�IR: !"+(char)('A'+(operand1>>>3))+operand2);
					Process pro = new Process(0x00ff&PID,new Device(String.valueOf((char)('A'+(operand1>>>3)))), operand2);
//					pro.start();
					//pro.setName(PID);
					pro.useDevice();//�����豸ʹ��Ȩ
					pControler.block();
					pControler.readyToRunning();
					view.getHR_quequeID().setText(pControler.getPCBsDetails());
				};break;
			case (byte) 0x00:{//end��������
					view.getHR_activeComand().setText("����ִ��ָ�IR: end ---����"+(0x00ff&PID));
					view.getHR_result().setText("�������ս����AX : "+ AX);
					Processor.setPSW(PSW|0x01);
				};break;
			case (byte) 0xe0:{
					System.out.println("fatal error!");
					return false;
				}
			}
		return true;
	}
	public void CPU(){
		
		while(true){
			do{//ָ��Ķ�ȡ ���� ִ��
				//System.out.println("���� "+PID);
				view.getHR_osTimer().setText(TimeCounter.sysClock/60+":"+TimeCounter.sysClock%60);
				view.getHR_timerTricks().setText("ʱ��Ƭ��"+TimeCounter.timeSlice);
				if(PID != 0){
					view.getHR_procedingID().setText("�����н���ID��"+(0x00ff&PID));
					if(false==decodeAndExcute())
						return;
				}else{
					view.getHR_procedingID().setText("�����н���ID��"+(0x00ff&PID)+"�����н��̣�");
					view.getHR_activeComand().setText("����ִ��ָ������н��̣�");
					view.getHR_current_result().setText("�����м����������н��̣�");
					//view.getHR_result().setText("�������ս���������н��̣�");
				}
//				view.getHR_activeComand().setText("IR: X = ");
//				view.getHR_current_result().setText("AX : "+ AX);
				try {
					timeCounter.getTimeBarrier().await();
					//System.out.println("11111111111");//�ȴ�ϵͳʱ����һ��ʱ��Ƭ��һ
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}while(PSW==0x00);//PSWΪPSW 0x00---���жϣ�����ִ�г���
			if((PSW&0x01)==0x01){
				pControler.destroy();//���̽����ж�
				pControler.readyToRunning();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
				Processor.setPSW(PSW&0xfe);
			}
			if((PSW&0x02)==0x02||(PSW&0x04)==0x04){
				pControler.runningToReady();//ʱ��Ƭ�����жϣ�0x02������Ҫ�жϿ��н��̣�0x04��
				pControler.readyToRunning();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
				Processor.setPSW(PSW&0xf9);
			}
			System.out.println("PSW "+PSW);
			if((PSW&0xf8)>=0x08){//I/O�ж�
				pControler.awake();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
			}
//			Processor.setPSW(PSW&0xf8);
		}
	}
}
