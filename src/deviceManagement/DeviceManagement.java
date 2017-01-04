package deviceManagement;

import java.util.LinkedList;

import processManagement.Processor;
import view.View;


//�豸������
public class DeviceManagement {
	
	// ����
	private static DeviceManagement instance;  
	private LinkedList<Process> finishedProcess;
	// �豸������
	private static final int TOTAL_A = 2;
	private static final int TOTAL_B = 3;
	private static final int TOTAL_C = 3;
	
	private LinkedList<DeviceStatus> usingList=new LinkedList<DeviceStatus>();
	private LinkedList<DeviceStatus> waitingList=new LinkedList<DeviceStatus>();
	
	private DeviceManagement (){
		finishedProcess = new LinkedList<Process>();
	}
	
	// ��ȡ���ʵ��
	public static synchronized DeviceManagement getInstance() {  
		if (instance == null) {  
			instance = new DeviceManagement(); 
			
		}  
		return instance;  
	}  
	
	private synchronized void addFinishedProcess(Process finishedp){
		finishedProcess.add(finishedp);
	}
	
	public int deleteFinishedProcess(){
		return finishedProcess.remove(0).getID();
	}
	
	// һ���������е�ʱ��
	public void OnProcessRun( Process pro, Device dev)
	{
		int devCount = 0;
		
		 DeviceStatus s = new DeviceStatus();
		 s.pro = pro;
		 s.dev = dev;
		
		// ����ʹ�ö��У�����п��õ��豸������Ϣ���뵽usingQueue�У����û�п��õ��豸�������Ϣ��ӵ�waitingQueue��
		 for (DeviceStatus x : usingList) { 
          
			 if( x.dev.getName().compareTo(dev.getName())==0 )
			 {
				 devCount++;
				 
				 if( dev.getName().compareTo("A")==0 ) {
					 
					 // �������������豸����������뵽�ȴ�����
					 if( devCount>=TOTAL_A ) {
						 
						 waitingList.offer(s);
						 
						 View.getView().getHR_deviceDeatail().setText(ShowStatus());
						 
						 return;
					 }					 
				 }
				 else {
					 
					 if( devCount>=TOTAL_B || devCount>=TOTAL_C ) {
					 
						 waitingList.offer(s);
						 
						 View.getView().getHR_deviceDeatail().setText(ShowStatus());
						 
						 return;						 
					 }					 
				 }
			 }			 
		 }
		 
		 // ���û�г��������������ô��ʹ�ô��豸		 
		 usingList.offer(s);
		 
		 View.getView().getHR_deviceDeatail().setText(ShowStatus());
		 
		 s.pro.timer.schedule (s.pro.task, 2000L, 2000L);
	}
	
	// һ�����̽�����ʱ��
	public void OnProcessStop( Process pro )
	{
		Device reDevice = null;
		
		// �豸ʹ����ϣ���������Ŀ��Ϣ��Ȼ��ѵȴ������еĽ��̼��뵽�б���
		for (DeviceStatus x : usingList) { 
			
			if( x.pro.getID()==pro.getID()) {
				
				reDevice = x.dev;
				
				usingList.remove(x);
				
				break;
			}			
		}
		addFinishedProcess(pro);
		
		System.out.println("d1"+Processor.PSW);
		
		Processor.setPSW((0x00ff&Processor.PSW)+0x08);
		
		System.out.println("d2"+Processor.PSW);
		// �ӵȴ������а�ʹ�ô��豸�Ľ���ȡ��
		for (DeviceStatus x : waitingList) { 
			
			if( x.dev.getName().compareTo(reDevice.getName())==0 ) {
				
				waitingList.remove(x);
				
				usingList.offer(x);
				
				x.pro.timer.schedule (x.pro.task, 2000L, 2000L);
				
				break;
			}			
		}
		View.getView().getHR_deviceDeatail().setText(ShowStatus());
		
	}
	
	// ��ʾĿǰ�豸��ռ�����
	public String ShowStatus() {
		
		System.out.println("���豸ʹ�������");
		StringBuilder A = new StringBuilder("���豸ʹ�������\n\n");
		StringBuilder B = new StringBuilder();
		StringBuilder C = new StringBuilder();
		
		int Aindex = 1,Bindex = 1,Cindex = 1;
		
		for (DeviceStatus x : usingList) { 
			switch(x.dev.getName())
			{
				case "A":A.append("A"+Aindex+" : "+String.format("%-6d", x.pro.getID())+"��ʱ"+x.pro.getRunTime()+"\n");Aindex++;break;
				case "B":B.append("B"+Bindex+" : "+String.format("%-6d", x.pro.getID())+"��ʱ"+x.pro.getRunTime()+"\n");Bindex++;break;
				case "C":C.append("C"+Cindex+" : "+String.format("%-6d", x.pro.getID())+"��ʱ"+x.pro.getRunTime()+"\n");Cindex++;break;
			}
			
			System.out.println("���̣�" + x.pro.getID() + " - �豸��" + x.dev.getName());		
		}
		
		while(Aindex<=TOTAL_A){
			A.append("A"+Aindex+" : ����\n");
			Aindex++;
		}
		
		while(Bindex<=TOTAL_B){
			B.append("B"+Bindex+" : ����\n");
			Bindex++;
		}
		
		while(Cindex<=TOTAL_C){
			C.append("C"+Cindex+" : ����\n");
			Cindex++;
		}
		
		System.out.println("���ȴ����С�");
		A.append("�ȴ����У�");
		B.append("�ȴ����У�");
		C.append("�ȴ����У�");
		
		for (DeviceStatus x : waitingList) { 
			switch(x.dev.getName())
			{
				case "A":A.append(String.format("%-6d", x.pro.getID()));break;
				case "B":B.append(String.format("%-6d", x.pro.getID()));break;
				case "C":C.append(String.format("%-6d", x.pro.getID()));break;
			}
			System.out.println("���̣�" + x.pro.getID() + " - �豸��" + x.dev.getName());
		}
		
		return A.toString()+"\n\n"+B.toString()+"\n\n"+C.toString();
	}

}

