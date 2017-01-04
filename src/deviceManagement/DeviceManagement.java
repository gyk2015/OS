package deviceManagement;

import java.util.LinkedList;

import processManagement.Processor;
import view.View;


//设备管理类
public class DeviceManagement {
	
	// 单例
	private static DeviceManagement instance;  
	private LinkedList<Process> finishedProcess;
	// 设备的数量
	private static final int TOTAL_A = 2;
	private static final int TOTAL_B = 3;
	private static final int TOTAL_C = 3;
	
	private LinkedList<DeviceStatus> usingList=new LinkedList<DeviceStatus>();
	private LinkedList<DeviceStatus> waitingList=new LinkedList<DeviceStatus>();
	
	private DeviceManagement (){
		finishedProcess = new LinkedList<Process>();
	}
	
	// 获取类的实例
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
	
	// 一个进程运行的时候
	public void OnProcessRun( Process pro, Device dev)
	{
		int devCount = 0;
		
		 DeviceStatus s = new DeviceStatus();
		 s.pro = pro;
		 s.dev = dev;
		
		// 遍历使用队列，如果有可用的设备，则将信息加入到usingQueue中，如果没有可用的设备，则把信息添加到waitingQueue中
		 for (DeviceStatus x : usingList) { 
          
			 if( x.dev.getName().compareTo(dev.getName())==0 )
			 {
				 devCount++;
				 
				 if( dev.getName().compareTo("A")==0 ) {
					 
					 // 如果大于了最大设备个数，则加入到等待队列
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
		 
		 // 如果没有超出最大数量，那么则使用此设备		 
		 usingList.offer(s);
		 
		 View.getView().getHR_deviceDeatail().setText(ShowStatus());
		 
		 s.pro.timer.schedule (s.pro.task, 2000L, 2000L);
	}
	
	// 一个进程结束的时候
	public void OnProcessStop( Process pro )
	{
		Device reDevice = null;
		
		// 设备使用完毕，清除相关条目信息，然后把等待队列中的进程加入到列表中
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
		// 从等待队列中把使用此设备的进程取出
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
	
	// 显示目前设备的占用情况
	public String ShowStatus() {
		
		System.out.println("【设备使用情况】");
		StringBuilder A = new StringBuilder("【设备使用情况】\n\n");
		StringBuilder B = new StringBuilder();
		StringBuilder C = new StringBuilder();
		
		int Aindex = 1,Bindex = 1,Cindex = 1;
		
		for (DeviceStatus x : usingList) { 
			switch(x.dev.getName())
			{
				case "A":A.append("A"+Aindex+" : "+String.format("%-6d", x.pro.getID())+"用时"+x.pro.getRunTime()+"\n");Aindex++;break;
				case "B":B.append("B"+Bindex+" : "+String.format("%-6d", x.pro.getID())+"用时"+x.pro.getRunTime()+"\n");Bindex++;break;
				case "C":C.append("C"+Cindex+" : "+String.format("%-6d", x.pro.getID())+"用时"+x.pro.getRunTime()+"\n");Cindex++;break;
			}
			
			System.out.println("进程：" + x.pro.getID() + " - 设备：" + x.dev.getName());		
		}
		
		while(Aindex<=TOTAL_A){
			A.append("A"+Aindex+" : 空闲\n");
			Aindex++;
		}
		
		while(Bindex<=TOTAL_B){
			B.append("B"+Bindex+" : 空闲\n");
			Bindex++;
		}
		
		while(Cindex<=TOTAL_C){
			C.append("C"+Cindex+" : 空闲\n");
			Cindex++;
		}
		
		System.out.println("【等待队列】");
		A.append("等待队列；");
		B.append("等待队列：");
		C.append("等待队列：");
		
		for (DeviceStatus x : waitingList) { 
			switch(x.dev.getName())
			{
				case "A":A.append(String.format("%-6d", x.pro.getID()));break;
				case "B":B.append(String.format("%-6d", x.pro.getID()));break;
				case "C":C.append(String.format("%-6d", x.pro.getID()));break;
			}
			System.out.println("进程：" + x.pro.getID() + " - 设备：" + x.dev.getName());
		}
		
		return A.toString()+"\n\n"+B.toString()+"\n\n"+C.toString();
	}

}

