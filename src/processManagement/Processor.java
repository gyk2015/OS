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
			case 0x20:{//操作码：0010---x=?赋值
					operand1 = (byte) (IR&0x1f);
					AX = operand1;
					view.getHR_activeComand().setText("正在执行指令：IR: X = "+ operand1);
					view.getHR_current_result().setText("进程中间结果：AX : "+ AX);
				};break;
			case 0x40:{//操作码：0100---x++自增
					AX++;
					view.getHR_activeComand().setText("正在执行指令：IR: X++ ");
					view.getHR_current_result().setText("进程中间结果：AX : "+ AX);
				};break;
			case 0x60:{//操作码：0110---x--自减
					AX--;
					view.getHR_activeComand().setText("正在执行指令：IR: X-- ");
					view.getHR_current_result().setText("进程中间结果：AX : "+ AX);
				};break;
			case (byte) 0x80:{//操作码：1000---!??访问I/O
					operand1 = (byte) (IR&0x18);//获取设备号 0--设备A、1---设备B、2---设备C
					operand2 = (byte)(IR&0x07);//获取使用设备的时间
					view.getHR_activeComand().setText("正在执行指令：IR: !"+(char)('A'+(operand1>>>3))+operand2);
					Process pro = new Process(0x00ff&PID,new Device(String.valueOf((char)('A'+(operand1>>>3)))), operand2);
//					pro.start();
					//pro.setName(PID);
					pro.useDevice();//申请设备使用权
					pControler.block();
					pControler.readyToRunning();
					view.getHR_quequeID().setText(pControler.getPCBsDetails());
				};break;
			case (byte) 0x00:{//end结束进程
					view.getHR_activeComand().setText("正在执行指令：IR: end ---进程"+(0x00ff&PID));
					view.getHR_result().setText("进程最终结果：AX : "+ AX);
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
			do{//指令的读取 解析 执行
				//System.out.println("进程 "+PID);
				view.getHR_osTimer().setText(TimeCounter.sysClock/60+":"+TimeCounter.sysClock%60);
				view.getHR_timerTricks().setText("时间片："+TimeCounter.timeSlice);
				if(PID != 0){
					view.getHR_procedingID().setText("正运行进程ID："+(0x00ff&PID));
					if(false==decodeAndExcute())
						return;
				}else{
					view.getHR_procedingID().setText("正运行进程ID："+(0x00ff&PID)+"（空闲进程）");
					view.getHR_activeComand().setText("正在执行指令：（空闲进程）");
					view.getHR_current_result().setText("进程中间结果：（空闲进程）");
					//view.getHR_result().setText("进程最终结果：（空闲进程）");
				}
//				view.getHR_activeComand().setText("IR: X = ");
//				view.getHR_current_result().setText("AX : "+ AX);
				try {
					timeCounter.getTimeBarrier().await();
					//System.out.println("11111111111");//等待系统时间增一，时间片减一
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}while(PSW==0x00);//PSW为PSW 0x00---无中断，正常执行程序
			if((PSW&0x01)==0x01){
				pControler.destroy();//进程结束中断
				pControler.readyToRunning();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
				Processor.setPSW(PSW&0xfe);
			}
			if((PSW&0x02)==0x02||(PSW&0x04)==0x04){
				pControler.runningToReady();//时间片结束中断（0x02）或者要中断空闲进程（0x04）
				pControler.readyToRunning();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
				Processor.setPSW(PSW&0xf9);
			}
			System.out.println("PSW "+PSW);
			if((PSW&0xf8)>=0x08){//I/O中断
				pControler.awake();
				view.getHR_quequeID().setText(pControler.getPCBsDetails());
			}
//			Processor.setPSW(PSW&0xf8);
		}
	}
}
