package processManagement;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.TimerTask; 
public class TimeCounter extends TimerTask {
	public static int sysClock,timeSlice,randomTime;
	private ProcessControl pControler;
	private CyclicBarrier timeBarrier;
	public TimeCounter(ProcessControl p){
		sysClock = 0;
		timeSlice = 6;
		randomTime=2;
		pControler = p;
		timeBarrier = new CyclicBarrier(2);
	}
	public CyclicBarrier getTimeBarrier(){
		return timeBarrier;
	}
	@Override
	public void run() {
				sysClock++;
				timeSlice--;
				randomTime--;
				if(timeSlice==0){
					Processor.setPSW(Processor.PSW|0x02);//时间片中断，设置PSW
					timeSlice = 6;
				}
				if(randomTime==0){
					pControler.create();
					randomTime = (int)(Math.random()*30+1);
//					randomTime = 40;
				}
				try {
					timeBarrier.await();
					//System.out.println("222222222");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				timeBarrier.reset();
		}
		
}
