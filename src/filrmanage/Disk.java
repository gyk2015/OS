package filrmanage;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import processManagement.Bootstrap;
import view.View;

/**
  2��ʾtxt�ļ���1��ʾĿ¼,3��ʾe�ļ�*
 **/
public class Disk {
	public static Disk disk=null;
	public static DiskBlock[] diskblocks=new DiskBlock[256];
	public static byte[] fileAllacationTable=new byte[256];//�ļ������
	public static String filepath="Disk.txt";
	public static byte diskblocknow=4;//��ǰ���̿��
	public static byte diskblocklast=4;//��һ���̿��
	public static byte site=0;//��һ�δ��̿��м�¼��һ���̿�ŵ����λ��
	public static String pathnow="root";
	public static ArrayList<Byte> diskblocklasts = new ArrayList<Byte>(); //��һ���̺���
	public static ArrayList<Byte> sites = new ArrayList<Byte>(); //��һ���̺Ŷ�Ӧ��һ���̺ŵ�����
	public static String filenow=null;//��ǰ�ļ���
	public static byte filepropsnow=0;//��ǰ�ļ�����
	public static HashMap hm=new HashMap();
	public static Disk getDisk() {
		if(disk==null){
			return disk=new Disk();
		}else{
			return disk;
		}
		
	}
	//��ʼ��
	public void init(){
		File file = new File(filepath);
        if (file.exists()) {
        	 try {  
               FileInputStream freader = new FileInputStream(filepath);  
               ObjectInputStream objectInputStream = new ObjectInputStream(freader); 
               diskblocks = (DiskBlock[])objectInputStream.readObject();
               hm = (HashMap)objectInputStream.readObject();
               for(int i=0;i<4;i++){
            	   byte[] by=new byte[64];
            	   by=diskblocks[i].getBy();
	           	   for(int j=0;j<64;j++){
	           			fileAllacationTable[i*64+j]=by[j];
	           	   }  		
           		}
               for(int j=0;j<256;j++){
            	   System.out.print(fileAllacationTable[j]);
               }

//               System.out.println((int)diskblocks[7].getBy()[0]);
               System.out.println("��ʼ�� �ɹ�");
           } catch (FileNotFoundException e) {  
               // TODO Auto-generated catch block  
               e.printStackTrace();  
           } catch (IOException e) {  
               // TODO Auto-generated catch block  
               e.printStackTrace();  
           } catch (ClassNotFoundException e) {  
               // TODO Auto-generated catch block  
               e.printStackTrace();  
           }  
        	 diskblocklasts.add((byte)4);
        	 sites.add((byte)0);
        }else{
        	//�ļ������ڣ��½��ļ�ģ����̣������ļ������Ϊǰ4�����̿飬Ĭ�ϴ���һ��aaaĿ¼�������ļ������ǰ5��ֵ
        	for(int i=0;i<256;i++){
        		byte[] b=new byte[0];
        		DiskBlock diskblock=new DiskBlock(b);
        		diskblocks[i]=diskblock;
        	}
        	for(int i=0;i<256;i++){
        		fileAllacationTable[i]=0;
        	}
        	fileAllacationTable[0]=1;
        	fileAllacationTable[1]=2;
        	fileAllacationTable[2]=3;
        	fileAllacationTable[3]=-1;
        	fileAllacationTable[4]=-1;
        	for(int i=0;i<4;i++){
        		byte[] by=new byte[64];
        		for(int j=0;j<64;j++){
        			by[j]=fileAllacationTable[i*64+j];
        		}
        		DiskBlock diskblock=new DiskBlock(by);
        		diskblocks[i]=diskblock;
        	}
	
        	byte[] by=new byte[8];
        	byte[] name="aaa".getBytes();
        	byte extendname=1;
        	byte props=1;
        	byte startdisk=0;
        	byte length=0;
        	by[0]=name[0];
        	by[1]=name[1];
        	by[2]=name[2];
        	by[3]=extendname;
        	by[4]=props;
        	by[5]=startdisk;
        	by[6]=length;
        	DiskBlock diskblock=new DiskBlock(by);
        	diskblocks[4]=diskblock;

        	diskblocklasts.add((byte)4);
        	sites.add((byte)0);
        	this.write();
        }
		
	}
	//����ĳ��Ŀ¼
	public void cdDir(String dir){
		
		
		if(dir.equals("..")){
			//������һ��
			if(diskblocklasts.size()==1){
				View.FileArea.setText("�Ƿ�����");
			}else{
				diskblocknow=diskblocklast;
				diskblocklasts.remove(diskblocklasts.size()-1);
				diskblocklast=(byte)diskblocklasts.get(diskblocklasts.size()-1);
				sites.remove(sites.size()-1);
				site=(byte)sites.get(sites.size()-1);
				String[] pathnows=pathnow.split(">");
				pathnow="root";
				for(int i=1;i<pathnows.length-1;i++){
					pathnow=pathnow+">"+pathnows[i];
				}
				System.out.println("��һ���̿�"+diskblocklast);
				System.out.println("��ǰ�̿�"+diskblocknow);
			}
			
		}else{
			boolean exist=true;//�жϵ�ǰҪ�����Ŀ¼�Ƿ����
			byte startDisk=diskblocknow;//��ǰ���̿��
			int sitenow=0;//��ǰĿ¼���м�¼��ʼλ��
			System.out.println("��ǰ�̿�"+startDisk);
			byte[] oldby=diskblocks[startDisk].getBy();
			if(dir.length()>3||dir.length()==0){
				System.out.println("�����Ϲ淶");
				View.FileArea.setText("�����Ϲ淶");
			}else{
				if(dir.length()==1){
					dir=dir+" "+" ";
				}else{
					if(dir.length()==2){
						dir=dir+" ";
					}
				}		
			}
			byte[] dirpath=dir.getBytes();
			System.out.println(new String(oldby));		
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==1){
					for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
					}
					if(exist==true){
						sitenow=i*8+5;
						startDisk=oldby[sitenow];
						break;
					}
				}
				else{
					exist=false;
				}
				
			}
			if(exist==false){
				System.out.println("Ŀ¼������");
				View.FileArea.setText("Ŀ¼������");
			}else{
				diskblocklast=diskblocknow;
				diskblocklasts.add(diskblocknow);
				diskblocknow=startDisk;
				site=(byte)sitenow;
				sites.add(site);
				pathnow=pathnow+">"+new String(oldby,sitenow-5,3);
			}


		}

		
		
	}

	
	//����Ŀ¼
	public void creatDir(String dir){
		//�ж�Ŀ¼�������Ƿ���ȷ
		if(dir.length()>3||dir.length()==0){
			System.out.println("Ŀ¼�������Ϲ淶");
			View.FileArea.setText("Ŀ¼�������Ϲ淶");
		}else{
			if(dir.length()==1){
				dir=dir+" "+" ";
			}else{
				if(dir.length()==2){
					dir=dir+" ";
				}
			}		
			boolean exist=false;//�ж�Ҫ������Ŀ¼�Ƿ��Ѿ�����
			byte[] dirpath=dir.getBytes();
			if(diskblocknow==4){
				//��Ŀ¼
				byte[] oldby=diskblocks[diskblocknow].getBy();
				for(int i=0;i<oldby.length/8;i++){
					exist=true;
					if(oldby[i*8+3]==1){
						for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
						}
						if(exist==true)
							break;		
					}else{
						exist=false;
					}
															
				}
				if(exist==true){
					System.out.println("��Ŀ¼�Ѵ���");
					View.FileArea.setText("��Ŀ¼�Ѵ���");
				}else{
					byte[] newby=new byte[oldby.length+8];
					byte[] name=dirpath;
					byte[] by=new byte[8];	
		        	byte extendname=1;
		        	byte props=1;
		        	byte startdisk=0;
		        	byte length=0;
		        	by[0]=name[0];
		        	by[1]=name[1];
		        	by[2]=name[2];
		        	by[3]=extendname;
		        	by[4]=props;
		        	by[5]=startdisk;
		        	by[6]=length;
		        	System.arraycopy(oldby,0,newby,0,oldby.length);
		        	System.arraycopy(by,0,newby,oldby.length,by.length);
		        	diskblocks[diskblocknow].setBy(newby);
				}
				
				
			}else{
				//�Ǹ�Ŀ¼
				if(diskblocknow==0){
					int start=256;
					for(int i=0;i<256;i++){
						if(fileAllacationTable[i]==0){
							start=i;
							break;
						}
					}//������һ�����д���
					System.out.println("��һ���д��̿�"+start);
					diskblocks[diskblocklast].getBy()[site]=(byte)start;
					diskblocknow=(byte)start;
					byte[] oldby=diskblocks[start].getBy();
					byte[] newby=new byte[oldby.length+8];
					byte[] by=new byte[8];	
					byte[] name=dirpath;
		        	byte extendname=1;
		        	byte props=1;
		        	byte startdisk=0;
		        	byte length=0;
		        	by[0]=name[0];
		        	by[1]=name[1];
		        	by[2]=name[2];
		        	by[3]=extendname;
		        	by[4]=props;
		        	by[5]=startdisk;
		        	by[6]=length;
		        	System.arraycopy(oldby,0,newby,0,oldby.length);
		        	System.arraycopy(by,0,newby,oldby.length,by.length);
		        	diskblocks[start].setBy(newby);
		        	System.out.println(new String(newby));
		        	fileAllacationTable[start]=-1;
				}else{

					byte[] oldby=diskblocks[diskblocknow].getBy();
					for(int i=0;i<oldby.length/8;i++){
						exist=true;
						if(oldby[i*8+3]==1){
							for(int j=0;j<3;j++){
							if(dirpath[j]!=oldby[i*8+j]){
								exist=false;
								break;
							}
							}
							if(exist==true)
								break;		
						}else{
							exist=false;
						}
																
					}
					if(exist==true){
						System.out.println("��Ŀ¼�Ѵ���");
						View.FileArea.setText("��Ŀ¼�Ѵ���");
					}else{
						byte[] newby=new byte[oldby.length+8];
						byte[] name=dirpath;
						byte[] by=new byte[8];	
			        	byte extendname=1;
			        	byte props=1;
			        	byte startdisk=0;
			        	byte length=0;
			        	by[0]=name[0];
			        	by[1]=name[1];
			        	by[2]=name[2];
			        	by[3]=extendname;
			        	by[4]=props;
			        	by[5]=startdisk;
			        	by[6]=length;
			        	System.arraycopy(oldby,0,newby,0,oldby.length);
			        	System.arraycopy(by,0,newby,oldby.length,by.length);
			        	System.out.println(new String(newby));
			        	diskblocks[diskblocknow].setBy(newby);
					}
				}
			}
		}
		//���½������ʹ��̿�
		Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
}
	

	//�����ļ�·��
	 public void creatFilepath(String filepath){
		 if(diskblocklasts.size()==1){
			 View.FileArea.setText("��Ŀ¼���ܴ����ļ�");
		 }else{
			 String[] dirs=filepath.split("\\.");
			 if(dirs[0].length()>3){
				 System.out.println("�ļ��������Ϲ淶");
	     		View.FileArea.setText("�ļ��������Ϲ淶");
			 }else{
				 if(dirs[0].length()==1){
					 dirs[0]=dirs[0]+" "+" ";
					}else{
						if(dirs[0].length()==2){
							dirs[0]=dirs[0]+" ";
						}
					}		
					 
					boolean exist=true;//�ж�Ҫ������Ŀ¼�Ƿ��Ѿ�����
					byte[] dirpath=dirs[0].getBytes();
//					int sitenow=0;//��ǰĿ¼���м�¼��ʼλ��
					 if(diskblocknow==0){
							int start=256;
							for(int i=0;i<256;i++){
								if(fileAllacationTable[i]==0){
									start=i;
									break;
								}
							}//������һ�����д���
							fileAllacationTable[start]=-1;
//							sitenow=this.creatFile();
							diskblocks[diskblocklast].getBy()[site]=(byte)start;
							diskblocknow=(byte)start;
							byte[] oldby=diskblocks[start].getBy();
							byte[] newby=new byte[oldby.length+8];
							byte[] by=new byte[8];	
							byte[] name=dirpath;
				        	byte extendname=2;
				        	byte props=2;
//				        	byte startdisk=(byte)sitenow;
				        	byte startdisk=0;
				        	byte length=0;
				        	by[0]=name[0];
				        	by[1]=name[1];
				        	by[2]=name[2];
				        	by[3]=extendname;
				        	by[4]=props;
				        	by[5]=startdisk;
				        	by[6]=length;
				        	System.arraycopy(oldby,0,newby,0,oldby.length);
				        	System.arraycopy(by,0,newby,oldby.length,by.length);
				        	diskblocks[start].setBy(newby);
				        	fileAllacationTable[start]=-1;
						}else{
							byte[] oldby=diskblocks[diskblocknow].getBy();
							for(int i=0;i<oldby.length/8;i++){
								exist=true;
								if(oldby[i*8+3]==2){
									for(int j=0;j<3;j++){
									if(dirpath[j]!=oldby[i*8+j]){
										exist=false;
										break;
									}
									}
									if(exist==true)
										break;		
								}else{
									exist=false;
								}
																		
							}
							if(exist==true){
								System.out.println("���ļ��Ѵ���");
								View.FileArea.setText("���ļ��Ѵ���");
							}else{
//								sitenow=this.creatFile();
								byte[] newby=new byte[oldby.length+8];
								byte[] name=dirpath;
								byte[] by=new byte[8];	
					        	byte extendname=2;
					        	byte props=2;
//					        	byte startdisk=(byte)sitenow;
					        	byte startdisk=0;
					        	byte length=0;
					        	by[0]=name[0];
					        	by[1]=name[1];
					        	by[2]=name[2];
					        	by[3]=extendname;
					        	by[4]=props;
					        	by[5]=startdisk;
					        	by[6]=length;
					        	System.arraycopy(oldby,0,newby,0,oldby.length);
					        	System.arraycopy(by,0,newby,oldby.length,by.length);
					        	diskblocks[diskblocknow].setBy(newby);
							}
						}
			 }
			//���½������ʹ��̿�
				Bootstrap.refreshDisk(disk);
				Bootstrap.refreshTree(disk);
				this.write();
		 }
		 
	 }
	 //�����ļ�����
	public void creatFile(){
		boolean exist=false;
	 	byte startDisk=4;
		int sitenow=0;//��ǰĿ¼���м�¼��ʼλ��
		byte[] dirpath=filenow.getBytes();
		byte[] oldby=diskblocks[diskblocknow].getBy();
		for(int i=0;i<oldby.length/8;i++){
			exist=true;
			if(oldby[i*8+3]==2||oldby[i*8+3]==3){
				for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
				}
				if(exist==true){
					sitenow=i*8+5;
					startDisk=oldby[i*8+5];
					break;
				}
							
			}else{
				exist=false;
			}
													
		}
		byte[] by;
		if(startDisk==0){
			String content=View.FileArea.getText();//��ȡ�ı����ֵ
			if(content.length()!=0){
				//�ļ�Ϊ����δ������̿�
				int start=256;
				for(int i=0;i<256;i++){
					if(fileAllacationTable[i]==0){
						start=i;
						break;
					}
				}//������һ�����д���
				diskblocks[diskblocknow].getBy()[sitenow]=(byte)start;

							
				if(filepropsnow==3){
					by=translateexe(content);
				}else{
					by=content.getBytes();//���ı�������ת��Ϊ�ֽ�����
				}
				
				
				if(by.length>64){
		    		int offset = 0;  //��ȡ���ֽڳ�ʼλ��
		            int length = by.length;//Ҫ�洢���ļ��Ĵ�С
		            int next=0;//��һ���̿�
		            byte[] b=new byte[64];

		            for(int j=0;j<by.length/64;j++){
		            	 b=new byte[64];
//		 				 DiskBlock diskblock=new DiskBlock(b);
//		 	        	 diskblocks[startDisk]=diskblock;
		            	 for(int i=0;i<64;i++){
		            		 b[i]=by[j*64+i];
		            	 }
		            	 diskblocks[start].setBy(b);
		            	 fileAllacationTable[start]=-1;//��ֹ���汻�ظ�����
		            	 for(int i=0;i<256;i++){
			            	if(fileAllacationTable[i]==0){
			            		next=i;
			            		break;
			            	}
		            	 }
		            	 fileAllacationTable[start]=(byte)next;
		            	 
		            	 start=next;
		            }
		            
		            if(by.length%64!=0){
		            	b=new byte[by.length%64];
		            	for(int i=0;i<by.length%64;i++){
		            		b[i]=by[(b.length/64)*64+i];
		            		
		            	}
		            	DiskBlock diskblock=new DiskBlock(b);
		            	diskblocks[start]=diskblock;
		            	fileAllacationTable[start]=-1;
		            
		            	System.out.println("����ɹ�");
		            }else{
		         //���ڴ���δ����
		            	System.out.println("����ɹ�");
		            	fileAllacationTable[start]=-1;
		            }
		            
		            
		          
		    	}else{
		    		//������������64�ֽ�
		    		diskblocks[start].setBy(by);
		    		fileAllacationTable[start]=-1;
		  
		    	}
				
				
			}else{
				
			}
			
		}else{
			//�ļ���Ϊ�գ��Ѿ�����ô��̿�����
			by=View.FileArea.getText().getBytes();//���ı�������ת��Ϊ�ֽ�����
			System.out.println("��ǰ�������ݹ���"+by.length);
			//����ԭ�����ļ��д洢���ݵĳ���
			int oldlong = 0;
			while(fileAllacationTable[startDisk]!=-1){
				oldlong+=diskblocks[startDisk].getBy().length;
				startDisk=fileAllacationTable[startDisk];
			}
			oldlong+=diskblocks[startDisk].getBy().length;
			if(by.length>oldlong){
				
			}
			if(by.length<oldlong){
				
			}
			if(by.length==oldlong){
			}
		}
		
		
		
		System.out.println((int)diskblocks[6].getBy()[0]);
		
		//���½������ʹ��̿�
		Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
	}
	//��ȡ�ļ�
	public void readFile(String filepath){
		String[] dirs=filepath.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("�ļ��������Ϲ淶");
			 View.FileArea.setText("�ļ��������Ϲ淶");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			 filenow=dirs[0];//����ǰ�ļ����׵�������
			boolean exist=true;//�ж�Ҫ��ȡ���ļ��Ƿ��Ѿ�����
		 	byte startDisk=4;
			byte[] dirpath=dirs[0].getBytes();
			byte[] oldby=diskblocks[diskblocknow].getBy();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==2){
					for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
					}
					if(exist==true){
						startDisk=oldby[i*8+5];
						filepropsnow=oldby[i*8+3];
						break;
					}
								
				}else{
					exist=false;
				}
														
			}
			if(!exist){
	 			System.out.println("�ļ�������");
	 			View.FileArea.setText("�ļ�������");
	 		}else{
	 			if(startDisk==0){
	 				View.FileArea.setText("");//���ļ�Ϊ�յ�ʱ��
					View.FileArea.setEnabled(true);//��������
					View.FileArea.setBackground(Color.white);
					View.jbutton.setEnabled(true);
	 			}else{
	 				//���ļ���Ϊ�յĵ�ʱ��
	 				String string="";
		 			while(fileAllacationTable[startDisk]!=-1){
		 				string+=new String(diskblocks[startDisk].getBy());
		 				System.out.println(string);
		 				startDisk=fileAllacationTable[startDisk];
		 			}
		 			string+=new String(diskblocks[startDisk].getBy());
					System.out.println(string);
					View.FileArea.setText(string);
					View.FileArea.setEnabled(true);//��������
					View.FileArea.setBackground(Color.white);
					View.jbutton.setEnabled(true);
	 			}
	 		
	 		}
		 }
		
	 	
	}
	
	
	//ɾ���ļ�
	public void deleteFile(String filepath){
		String[] dirs=filepath.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("�ļ��������Ϲ淶");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			boolean exist=true;//�ж�Ҫ��ȡ���ļ��Ƿ����
		 	byte startDisk=4;
			byte[] dirpath=dirs[0].getBytes();
			byte[] oldby=diskblocks[diskblocknow].getBy();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==2){
					for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
					}
					if(exist==true){
						startDisk=oldby[i*8+5];
	
						if(i*8+8>=oldby.length){
							byte[] newby=new byte[oldby.length-8];
							System.arraycopy(oldby,0,newby,0,i*8);
							diskblocks[diskblocknow].setBy(newby);
				
							if(newby.length==0){
								fileAllacationTable[diskblocknow]=0;
								oldby=diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].getBy();
								oldby[(int) sites.get(sites.size()-1)]=0;
								diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].setBy(oldby);
										
							}
							
						}else{
							byte[] newby=new byte[oldby.length-8];
							System.arraycopy(oldby,0,newby,0,i*8);
							System.arraycopy(oldby,i*8+8,newby,i*8,oldby.length-(i*8+8));
							diskblocks[diskblocknow].setBy(newby);
							if(newby.length==0){
								fileAllacationTable[diskblocknow]=0;
								oldby=diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].getBy();
								oldby[(int) sites.get(sites.size()-1)]=0;
								diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].setBy(oldby);
							}
						}
						
						
						
						break;
					}
								
				}else{
					exist=false;
				}
														
			}
			
			if(exist==false){
				System.out.println("·��������ļ�������");
			}else{
				if(startDisk!=0){
					while(fileAllacationTable[startDisk]!=-1){
						byte[] empty=new byte[0];
						diskblocks[startDisk].setBy(empty);
						byte n=fileAllacationTable[startDisk];
						fileAllacationTable[startDisk]=0;
						startDisk=n;
					}
					byte[] empty=new byte[0];
					diskblocks[startDisk].setBy(empty);
					fileAllacationTable[startDisk]=0;//���ļ�������еĶ�Ӧ���Ϊ0
				}else{
					
				}
			}
		
			this.write();
		 }
		 Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
	}
	//ɾ����Ŀ¼
	public void deletDir(String filepath){
		 if(filepath.length()>3){
			 System.out.println("Ŀ¼�������Ϲ淶");
			 View.FileArea.setText("Ŀ¼�������Ϲ淶");
		 }else{
			 if(filepath.length()==1){
				 filepath=filepath+" "+" ";
				}else{
					if(filepath.length()==2){
						filepath=filepath+" ";
					}
				}		
			boolean exist=true;//�ж�Ҫɾ����Ŀ¼�Ƿ����
		 	byte startDisk=4;
			byte[] dirpath=filepath.getBytes();
			byte[] oldby=diskblocks[diskblocknow].getBy();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==1){
					for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
					}
					if(exist==true){
						startDisk=oldby[i*8+5];
						if(startDisk==0){
							if(i*8+8>=oldby.length){
								byte[] newby=new byte[oldby.length-8];
								System.arraycopy(oldby,0,newby,0,i*8);
								diskblocks[diskblocknow].setBy(newby);
					
								if(newby.length==0){
									fileAllacationTable[diskblocknow]=0;
									oldby=diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].getBy();
									oldby[(int) sites.get(sites.size()-1)]=0;
									diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].setBy(oldby);
											
								}
								
							}else{
								byte[] newby=new byte[oldby.length-8];
								System.arraycopy(oldby,0,newby,0,i*8);
								System.arraycopy(oldby,i*8+8,newby,i*8,oldby.length-(i*8+8));
								diskblocks[diskblocknow].setBy(newby);
								if(newby.length==0){
									fileAllacationTable[diskblocknow]=0;
									oldby=diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].getBy();
									oldby[(int) sites.get(sites.size()-1)]=0;
									diskblocks[(int) diskblocklasts.get(diskblocklasts.size()-1)].setBy(oldby);
								}
							}
						}else{
							System.out.println("��Ŀ¼��Ϊ�գ�����ɾ��");
							View.FileArea.setText("��Ŀ¼��Ϊ�գ�����ɾ��");
						}
						
						break;
					}
								
				}else{
					exist=false;
				}
														
			}
			
			if(exist==false){
				System.out.println("Ŀ¼������");
				View.FileArea.setText("Ŀ¼������");
			}else{ 
	//				this.write();
				
			}
		 }
		 Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
	}
	
	//�����ļ�
	public void copyFile(String sourcefile,String targetfile){
		String[] dirs=sourcefile.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("�ļ��������Ϲ淶");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			boolean exist=true;//�ж�Ҫ���Ƶ��ļ��Ƿ��Ѿ�����
		 	byte startDisk=4;
			byte[] dirpath=dirs[0].getBytes();
			byte[] oldby=diskblocks[diskblocknow].getBy();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==2){
					for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
					}
					if(exist==true){
						startDisk=oldby[i*8+5];
						break;
					}
								
				}else{
					exist=false;
				}
														
			}
			if(exist==false){
				System.out.println("��ǰҪ���Ƶ��ļ������� ");
				View.FileArea.setText("��ǰҪ���Ƶ��ļ�������");
			}else{
				byte[] source=new byte[0];
				if(fileAllacationTable[startDisk]!=-1&&fileAllacationTable[startDisk]!=0){
					//��ǰ�ļ�����64�ֽ�
					System.out.println(fileAllacationTable[startDisk]);
					ArrayList<Byte> temporary = new ArrayList<Byte>();
					while(fileAllacationTable[startDisk]!=-1){					
						for(int i=0;i<diskblocks[startDisk].getBy().length;i++){
							temporary.add(diskblocks[startDisk].getBy()[i]);
						}
						startDisk=fileAllacationTable[startDisk];
						System.out.println(fileAllacationTable[startDisk]);
					}
					for(int i=0;i<diskblocks[startDisk].getBy().length;i++){
						temporary.add(diskblocks[startDisk].getBy()[i]);
					}
					
					System.out.println(temporary.size());
					source=new byte[temporary.size()];
					int i = 0;
					for(Byte data : temporary) {
						source[i] = data.byteValue();
					    i++;
					}
				}else{
					//��ǰ�ļ�С��64�ֽ�
					source=diskblocks[startDisk].getBy();
					
				}
	//			System.out.println(new String(source));
				this.copyto(targetfile,dirs[0],new String(source));
				
			}
		 }
	}
	public void copyto(String filepath,String filename,String content){
		String[] dirs=filepath.split("/");
		
		for(int q=0;q<dirs.length;q++){
			if(dirs[q].length()>3){
				 System.out.println("·�������Ϲ淶");
			 }else{
				 if(dirs[q].length()==1){
					 dirs[q]=dirs[q]+" "+" ";
					}else{
						if(dirs[q].length()==2){
							dirs[q]=dirs[q]+" ";
						}
					}
			 }
		}
		
		boolean exist=true;//�жϵ�ǰĿ¼�Ƿ����
		byte startDisk=4;//��ǰ���̿��
		int sitenow=0;//��ǰĿ¼���м�¼��һ���̿�ŵ���Ϣ
		byte lastDsik=0;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath=dirs[0].getBytes();
		for(int z=0;z<dirs.length;z++){				
			oldby=diskblocks[startDisk].getBy();//��ȡ��һ�����̿�
			dirpath=dirs[z].getBytes();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				if(oldby[i*8+3]==1){
					for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
					}
					if(exist==true){
						sitenow=i*8+5;
						lastDsik=startDisk;
						startDisk=oldby[sitenow];
						break;
					}
				}
				else{
					exist=false;
				}
				
			}			
		}//�ж�·���Ƿ����
		if(exist==false){
			System.out.println("·��������");
			View.FileArea.setText("·��������");
		}else{
			if(startDisk==0){
			//��ǰҪ���ƽ���Ŀ¼��û������ռ�
				System.out.println(filename);
				dirs=filename.split("\\.");
				System.out.println(dirs[0]);
				dirpath=dirs[0].getBytes();
				int start=256;
				for(int i=0;i<256;i++){
					if(fileAllacationTable[i]==0){
						start=i;
						break;
					}
				}//������һ�����д���
				fileAllacationTable[start]=-1;
				diskblocks[lastDsik].getBy()[sitenow]=(byte)start;
				sitenow=this.creatcopyFile(content);
				oldby=diskblocks[start].getBy();
				byte[] newby=new byte[oldby.length+8];
				byte[] by=new byte[8];	
				byte[] name=dirpath;
	        	byte extendname=2;
	        	byte props=2;
	        	byte startdisk=(byte)sitenow;
	        	byte length=0;
	        	by[0]=name[0];
	        	by[1]=name[1];
	        	by[2]=name[2];
	        	by[3]=extendname;
	        	by[4]=props;
	        	by[5]=startdisk;
	        	by[6]=length;
	        	System.arraycopy(oldby,0,newby,0,oldby.length);
	        	System.arraycopy(by,0,newby,oldby.length,by.length);
	        	diskblocks[start].setBy(newby);
			}else{
				//��ǰҪ���ƽ���Ŀ¼�Ѿ��пռ�
				System.out.println("��ǰҪ���ƽ���Ŀ¼�Ѿ��пռ�"+startDisk);
				oldby=diskblocks[startDisk].getBy();
				dirs=filename.split("\\.");
				dirpath=dirs[0].getBytes();
				for(int i=0;i<oldby.length/8;i++){
					exist=true;
					if(oldby[i*8+3]==2){
						for(int j=0;j<3;j++){
						if(dirpath[j]!=oldby[i*8+j]){
							exist=false;
							break;
						}
						}
						if(exist==true)
							break;		
					}else{
						exist=false;
					}
															
				}
				if(exist==true){
					System.out.println("���ļ��Ѵ���");
				}else{
					sitenow=this.creatcopyFile(content);
					byte[] newby=new byte[oldby.length+8];
					byte[] name=dirpath;
					byte[] by=new byte[8];	
		        	byte extendname=2;
		        	byte props=2;
		        	byte startdisk=(byte)sitenow;
		        	byte length=0;
		        	by[0]=name[0];
		        	by[1]=name[1];
		        	by[2]=name[2];
		        	by[3]=extendname;
		        	by[4]=props;
		        	by[5]=startdisk;
		        	by[6]=length;
		        	System.arraycopy(oldby,0,newby,0,oldby.length);
		        	System.arraycopy(by,0,newby,oldby.length,by.length);
		        	diskblocks[startDisk].setBy(newby);
				}
			}
		}
		 Bootstrap.refreshDisk(disk);
			Bootstrap.refreshTree(disk);
			this.write();
	}
	
	public int creatcopyFile(String content){
		int start=256;
		int sitenow=0;//��ǰĿ¼���м�¼��ʼλ��
		for(int i=0;i<256;i++){
			if(fileAllacationTable[i]==0){
				start=i;
				break;
			}
		}//������һ�����д���
		sitenow=start;
    	//������תΪ�ֽ�
		byte[] by=content.getBytes();
    	System.out.println("��ǰ�������ݹ���"+by.length);
    	//�ж�һ�������ܷ�ŵ���
    	if(by.length>64){
    		int offset = 0;  //��ȡ���ֽڳ�ʼλ��
            int length = by.length;//Ҫ�洢���ļ��Ĵ�С
            int next=0;//��һ���̿�
            byte[] b=new byte[64];

            for(int j=0;j<by.length/64;j++){
            	 b=new byte[64];
 				 DiskBlock diskblock=new DiskBlock(b);
 	        	 diskblocks[start]=diskblock;
            	 for(int i=0;i<64;i++){
            		 b[i]=by[j*64+i];
            	 }
            	 diskblocks[start].setBy(b);
            	 fileAllacationTable[start]=-1;//��ֹ���汻�ظ�����
            	 for(int i=0;i<256;i++){
	            	if(fileAllacationTable[i]==0){
	            		next=i;
	            		break;
	            	}
            	 }
            	 fileAllacationTable[start]=(byte)next;
            	 
            	 start=next;
            	 System.out.println("��һ���̿����"+start);
            }
            
            if(by.length%64!=0){
            	b=new byte[64];
            	for(int i=0;i<by.length%64;i++){
            		b[i]=by[(b.length/64)*64+i];
            		
            	}
            	DiskBlock diskblock=new DiskBlock(b);
            	diskblocks[start]=diskblock;
            	fileAllacationTable[start]=-1;
            
            	System.out.println("����ɹ�");
            }else{
         
            	System.out.println("����ɹ�");
            	fileAllacationTable[start]=-1;
            }
            
            
          
    	}else{
    		byte[] b=new byte[64];
			DiskBlock diskblock=new DiskBlock(b);
        	diskblocks[start]=diskblock;
    		diskblocks[start].setBy(by);
    		fileAllacationTable[start]=-1;
  
    	}
    	return sitenow;
	}
	//������ִ���ļ�
	public void newexe(String file){
		String[] dirs=file.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("�ļ��������Ϲ淶");
			 View.FileArea.setText("�ļ��������Ϲ淶");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			filenow=dirs[0];//����ǰ�ļ����׵�������
			filepropsnow=3;
			boolean exist=false;//�ж�Ҫ������Ŀ¼�Ƿ��Ѿ�����
			byte[] dirpath=dirs[0].getBytes();
		
	
					byte[] oldby=diskblocks[diskblocknow].getBy();
					for(int i=0;i<oldby.length/8;i++){
						exist=true;
						if(oldby[i*8+3]==3){
							for(int j=0;j<3;j++){
								if(dirpath[j]!=oldby[i*8+j]){
									exist=false;
									break;
								}
							}
							if(exist==true){
								filepropsnow=oldby[i*8+3];
								break;	
							}
									
						}else{
							exist=false;
						}
																
					}
					if(exist==true){
						System.out.println("�ÿ�ִ���ļ��Ѵ���");
						 View.FileArea.setText("�ÿ�ִ���ļ��Ѵ���");
					}else{
						String exepath=pathnow+">"+dirs[0];						
						hm.put(hm.size()+1,exepath);//����ǰexe�ļ���·����ӵ�hm�У������
						View.FileArea.setText("");//���ļ�Ϊ�յ�ʱ��
						View.FileArea.setEnabled(true);//��������
						View.FileArea.setBackground(Color.white);
						View.jbutton.setEnabled(true);
						 if(diskblocknow==0){
								int start=256;
								for(int i=0;i<256;i++){
									if(fileAllacationTable[i]==0){
										start=i;
										break;
									}
								}//������һ�����д���
								fileAllacationTable[start]=-1;
//								sitenow=this.creatFile();
								diskblocks[diskblocklast].getBy()[site]=(byte)start;
								diskblocknow=(byte)start;
								oldby=diskblocks[start].getBy();
								byte[] newby=new byte[oldby.length+8];
								byte[] by=new byte[8];	
								byte[] name=dirpath;
					        	byte extendname=3;
					        	byte props=3;
//					        	byte startdisk=(byte)sitenow;
					        	byte startdisk=0;
					        	byte length=0;
					        	by[0]=name[0];
					        	by[1]=name[1];
					        	by[2]=name[2];
					        	by[3]=extendname;
					        	by[4]=props;
					        	by[5]=startdisk;
					        	by[6]=length;
					        	System.arraycopy(oldby,0,newby,0,oldby.length);
					        	System.arraycopy(by,0,newby,oldby.length,by.length);
					        	diskblocks[start].setBy(newby);
					        	fileAllacationTable[start]=-1;
						 }else{					
//								sitenow=this.createxeFile();//�����ִ���ļ����ݲ��ҷ��ش�����ݵĿ�ʼ�̿�
//								System.out.println("���ļ����ݴ��λ��"+sitenow);
								byte[] newby=new byte[oldby.length+8];
								byte[] name=dirpath;
								byte[] by=new byte[8];	
					        	byte extendname=3;
					        	byte props=3;
					        	byte startdisk=0;
					        	byte length=0;
					        	by[0]=name[0];
					        	by[1]=name[1];
					        	by[2]=name[2];
					        	by[3]=extendname;
					        	by[4]=props;
					        	by[5]=startdisk;
					        	by[6]=length;
					        	System.out.println("startdisk"+startdisk);
					        	System.arraycopy(oldby,0,newby,0,oldby.length);
					        	System.arraycopy(by,0,newby,oldby.length,by.length);
					        	diskblocks[diskblocknow].setBy(newby);
						 }
				
				}
		 }
			//���½������ʹ��̿�
			Bootstrap.refreshDisk(disk);
			Bootstrap.refreshTree(disk);
			this.write();
	}
//	
//	public int createxeFile(){
//		boolean exist=false;//�ж�Ҫ������Ŀ¼�Ƿ��Ѿ�����
//		byte[] dirpath=exenow.getBytes();
//		int sitenow=0;//��ǰĿ¼���м�¼��ʼλ��
//		byte[] oldby=diskblocks[5].getBy();
//		for(int i=0;i<oldby.length/8;i++){
//			exist=true;
//			if(oldby[i*8+3]==3){
//				for(int j=0;j<3;j++){
//					if(dirpath[j]!=oldby[i*8+j]){
//						exist=false;
//						break;
//					}
//				}
//				if(exist==true){
//					sitenow=i*8+5;
//					break;
//				}		
//			}else{
//				exist=false;
//			}
//													
//		}
//		int start=256;
//		for(int i=0;i<256;i++){
//			if(fileAllacationTable[i]==0){
//				start=i;
//				break;
//			}
//		}//������һ�����д���
//		diskblocks[5].getBy()[sitenow]=(byte)start;
//		
//		
//		
//
//		byte[] result=this.translateexe();//��������ַ���ת��Ϊ�ֽ����鲢����
//		if(result.length>64){
//			byte[] b=new byte[64];
//			 for(int j=0;j<result.length/64;j++){
//				 for(int i=0;i<64;i++){
//					b[i]=result[j*64+i];
//            	 }
//				 diskblocks[start].setBy(b);
//				 fileAllacationTable[start]=-1;//��ֹ���汻�ظ�����
//            	 for(int i=0;i<256;i++){
//	            	if(fileAllacationTable[i]==0){
//	            		next=i;
//	            		break;
//	            	}
//            	 }
//            	 fileAllacationTable[start]=(byte)next;
//            	 
//            	 start=next;
//			 }			
//         
//            
//            if(result.length%64!=0){
//            	b=new byte[result.length%64];
//            	for(int i=0;i<result.length%64;i++){
//            		 b[i]=result[(result.length/64)*64+i];
//            		
//            	}
//            	diskblocks[start].setBy(b);
//            	fileAllacationTable[start]=-1;
//            
//            	System.out.println("����ɹ�");
//            }else{
//            	//�պ�Ϊ64�ı�����ʱ������ʱ��������
//            	System.out.println("����ɹ�");
//            	fileAllacationTable[start]=0;
//            }
//            
//            
//          
//    	}else{
//    		byte[] b=new byte[result.length];
//    		 for(int i=0;i<result.length;i++){
//					b[i]=result[i];
//         	 }
//    		diskblocks[start].setBy(b);
//    		fileAllacationTable[start]=-1;
//  
//    	}
//    	return sitenow;
//
//	}
	public byte[] translateexe(String content){

		ArrayList<Byte> temporary = new ArrayList<Byte>();
		//��������
//		Scanner input = new Scanner(System.in);
//    	String content = input.nextLine();
    	String[] contents=content.split("\n");//�������ָ��س����в��
    	for(int i=0;i<contents.length;i++){
    		//��ÿ��ָ������жϣ�ת��Ϊ�ֽ�
    		if(contents[i].contains("x=")){
    			String[] c=contents[i].split("=");
    			int ci=32+Integer.parseInt(c[1]);
    			temporary.add((byte) ci);
    		}
    		if(contents[i].contains("x+")){
    			temporary.add((byte) 64);
    		}
    		if(contents[i].contains("x-")){
    			temporary.add((byte) 96);
    		}
    		if(contents[i].contains("!")){
    			String equipment=contents[i].substring(1,2);//ȡ���豸��
    			int equipmentnum=0;
    			if(equipment.equals("A")){
    				equipmentnum=0;
    			}else{
    				if(equipment.equals("B")){
        				equipmentnum=8;
        			}else{
        				equipmentnum=16;
        			}
    			}
    			String time=contents[i].substring(2,contents[i].length());//ȡ��ʱ��
    			temporary.add((byte) (128+equipmentnum+Integer.parseInt(time)));
    		}
    		if(contents[i].contains("end")){
    			temporary.add((byte) 0);
    		}
    	}
    	byte[] result=new byte[temporary.size()];
		int i = 0;
		for(Byte data : temporary) {
			result[i] = data.byteValue();
		    i++;
		}
    	return result;
	}
	
	
	//д�����ģ���ļ�disk
	public void write(){
		try {  
	    	 File file = new File(filepath);
	         if (file.exists()) {
	             file.delete();
	         }  
           FileOutputStream outStream = new FileOutputStream(filepath);  
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);  
             
           for(int i=0;i<4;i++){
        	   byte[] by=new byte[64];
        	   by=diskblocks[i].getBy();
           	   for(int j=0;j<64;j++){
           			by[j]=fileAllacationTable[i*64+j];
           	   }  		
       		}
           
           objectOutputStream.writeObject(diskblocks);  
           objectOutputStream.writeObject(hm);  
           outStream.close();  
           System.out.println("write successful");  
       } catch (FileNotFoundException e) {  
           e.printStackTrace();  
       } catch (IOException e) {  
           e.printStackTrace();  
       }  
	}
	//����exe�ļ���С
	public static int getexesize(int num){
		System.out.println("����exe�ļ���С��num"+num);
		String exepath=(String) hm.get(num);//�õ���Ӧnum�Ŀ�ִ���ļ�·��
		System.out.println(exepath);
		String[] exepaths=exepath.split(">");	
		int size=0;
		byte startDisk=4;
		boolean exist;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath;
		for(int z=1;z<exepaths.length-1;z++){				
			oldby=diskblocks[startDisk].getBy();//��ȡ��һ�����̿�
			dirpath=exepaths[z].getBytes();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
				}
				if(exist==true){
					startDisk=oldby[i*8+5];
					break;
				}
				
			}
		}
		String fp=exepaths[exepaths.length-1];
		for(int z=0;z<1;z++){				
			oldby=diskblocks[startDisk].getBy();//��ȡ��һ�����̿�
			dirpath=fp.getBytes();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
				}
				if(exist==true){
					startDisk=oldby[i*8+5];
					break;
				}
				
			}
		}
		if(fileAllacationTable[startDisk]==-1){
			size=diskblocks[startDisk].getBy().length;
		}else{
			while(fileAllacationTable[startDisk]!=-1){
				size+=64;
				startDisk=fileAllacationTable[startDisk];
			}
			size+=diskblocks[startDisk].getBy().length;
		}
		System.out.println("����exe�ļ���С��size"+size);
		return size;
	}
	public static byte[] getexefile(int num){
		System.out.println("����exe�ļ����ݵ�num"+num);
		
		String exepath=(String) hm.get(num);//�õ���Ӧnum�Ŀ�ִ���ļ�·��
		String[] exepaths=exepath.split(">");	
		int size=0;
		byte startDisk=4;
		boolean exist;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath;
		for(int z=1;z<exepaths.length-1;z++){				
			oldby=diskblocks[startDisk].getBy();//��ȡ��һ�����̿�
			dirpath=exepaths[z].getBytes();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
				}
				if(exist==true){
					startDisk=oldby[i*8+5];
					break;
				}
				
			}
		}
		String fp=exepaths[exepaths.length-1];
		for(int z=0;z<1;z++){				
			oldby=diskblocks[startDisk].getBy();//��ȡ��һ�����̿�
			dirpath=fp.getBytes();
			for(int i=0;i<oldby.length/8;i++){
				exist=true;
				for(int j=0;j<3;j++){
					if(dirpath[j]!=oldby[i*8+j]){
						exist=false;
						break;
					}
				}
				if(exist==true){
					startDisk=oldby[i*8+5];
					break;
				}
				
			}
		}

		byte[] content=new byte[0];
		if(fileAllacationTable[startDisk]==-1){
			content=diskblocks[startDisk].getBy();
			System.out.println("��ǰ��ִ���ļ����ݵ�һλ"+(int)content[0]);
		}else{
				//exe�ļ����ȴ���64�ֽ�
			ArrayList<Byte> temporary = new ArrayList<Byte>();
			while(fileAllacationTable[startDisk]!=-1){					
				for(int i=0;i<diskblocks[startDisk].getBy().length;i++){
					temporary.add(diskblocks[startDisk].getBy()[i]);
				}
				startDisk=fileAllacationTable[startDisk];
			}
			for(int i=0;i<diskblocks[startDisk].getBy().length;i++){
				temporary.add(diskblocks[startDisk].getBy()[i]);
			}
			content=new byte[temporary.size()];
			int i = 0;
			for(Byte data : temporary) {
				content[i] = data.byteValue();
			    i++;
			}
		}
		
		
		return content;
	}
}