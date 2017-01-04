/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
	 * 
	 * @param index ���̿��ƿ��ʶ��
	 * @param size  �ڴ��С
	 * @param fileName �ļ�����·��
	 * @return
	 * @throws IOException
	 */
package memoryManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import view.View;
/**
 *
 * @author lenovo
 */
public class MemoryManage {

    private static total_Size total = new total_Size();
    private static byte[][] PCBsArea = new byte[10][6];
    private static List<process> jobs = new ArrayList<process>();
    private static View view;
    public MemoryManage(View view){
        this.view = view;
    }
    public static total_Size getTotal_Size(){
    	return total;
    }
    public static byte[][] getPCBsArea(){
        return PCBsArea;
    }
    //�����ڴ�
    public static int reqMemory(int index,int size,int exeFileIndex){
        process job = new process(index,size,exeFileIndex);
        List<Size> lsize = total.getList();
        sort(lsize);
        int i;
        for(i=0;i< lsize.size();i++){
            Size asize  = lsize.get(i);
            if(asize.getState()<=-1){
                if(asize.getSpace()>=job.getSize()){
                    System.out.println("�ڴ�����ɹ�");
                    jobs.add(job);
                    total.alloc(i, job);
                    view.getHR_mainMemoryDetail().setText(memoryList());
                    break;
                }
            }
        }
        if(i>=lsize.size()){
            System.out.println("�ڴ�����ʧ��");
            return -1;
        }
        return lsize.get(i).getStart();
    }
    //�ͷ��ڴ�
    public static void relMemory(int start){
    	memoryList();
        List<Size> lsize = total.getList();
        int i ;
        int index = -1;
        for(i=0;i<lsize.size();i++){
            if(lsize.get(i).getStart()== start){
                int j ;
                for(j=0;j<jobs.size();j++){
                    if(jobs.get(j).getIndex()==lsize.get(i).getState()){
                        index = jobs.get(j).getIndex();
                        total.search(jobs.get(j));
                        jobs.remove(j);
                        System.out.println(index+"�ڴ��ͷųɹ�");
                        view.getHR_mainMemoryDetail().setText(memoryList());
                        return;
                    }
                }
            }
        }
        System.out.println(index+"�ڴ��ͷ�ʧ��");
    }
   
    public static void sort(List<Size> lsize){
        Collections.sort(lsize, new Comparator<Size>(){
	        @Override 
	        public int compare(Size t, Size t1){
	            return t.getStart()-t1.getStart();
	        }
	    });
    }
    
    public static String memoryList(){
    	String title = "���ڴ������Ϣ��\n��ʼ��ַ---��ֹ��ַ---ʹ�����---\n";
        System.out.println("�ڴ������Ϣ��");
        System.out.println("��ʼ��ַ---��ֹ��ַ---״̬---");
        if(total.getList().size()!=0)
        {
            sort(total.getList());
            return title+total.print();
        }
        else{
        	System.out.println("��ǰ�û���û�н���");
        	return title+"\n";
        }
            
        
    }
  
    
}

