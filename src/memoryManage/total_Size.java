/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package memoryManage;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author lenovo
 */
public class total_Size {
    //������
    private List<Size> block_list = new LinkedList<Size>();
    //min block
    private int minSize = 0;
    
    public total_Size(){
        Size size = new Size(0,512); // �����ڴ�
        block_list.add(size);//add
    }
    public List<Size> getList(){
        return block_list;
    }
    public void setList(List<Size> block_list){
        this.block_list = block_list;
    }
    public boolean JudgeSzie(int i ){
        return block_list.get(i).getState()<0 ? true : false;
    }
    
    public void alloc (int index , process job){
        Size size = block_list.get(index);
        if(size.getSpace() - job.getSize() > this.minSize){ 
        	int  newStart = size.getStart()+ job.getSize();
            int newSpace = size.getSpace() - job.getSize();
            Size newSize = new Size(newStart,newSpace);
            block_list.add(newSize);
        }  
        size.setSpace(job.getSize());
        size.setState(job.getIndex());
        size.setIns(job.getExeFileIndex());
    }
    //���̽��������շ���
    public void search(process job){
        Size size =null;
        int flag = -1 ;//��¼��ǰλ��
        for(int i=0; i<block_list.size();i++){
            //�ҵ������յķ���
            if(block_list.get(i).getState() ==job.getIndex()){
                // �����Լ�Ϊ����
                block_list.get(i).setState(-1);
                size = block_list.get(i);
                flag = i;
                break;
            }
        }
        if(flag == -1){
            return;
        }
        //��ǰ�����ĺ���ķ�����ַ ��ǰades+space
        //int nextAdrs = size.getStart() + size.getSpace();
        //�ϲ����з���
        for(int i=0; i<block_list.size() ; i++){
            if(JudgeSzie(i)){
                Size nowSize = block_list.get(i);
                if(nowSize.getStart()+nowSize.getSpace() == size.getStart()){
                    merge(i,flag);
	            }
	        }
            //if(block_list.size()>)
        
    }
}
    public void merge(int i, int j){
       Size i_size = block_list.get(i);
       Size j_size = block_list.get(j);
       i_size.setSpace(i_size.getSpace() + j_size.getSpace());
       block_list.remove(j);
    }
    
    public String print(){
    	StringBuilder details = new StringBuilder();
        for(Size size : block_list){
            System.out.println(size);
        	details.append(size.toString());
        }
        return details.toString();
    }
}
