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
  2表示txt文件，1表示目录,3表示e文件*
 **/
public class Disk {
	public static Disk disk=null;
	public static DiskBlock[] diskblocks=new DiskBlock[256];
	public static byte[] fileAllacationTable=new byte[256];//文件分配表
	public static String filepath="Disk.txt";
	public static byte diskblocknow=4;//当前磁盘块号
	public static byte diskblocklast=4;//上一磁盘块号
	public static byte site=0;//上一次磁盘块中记录下一磁盘块号的项的位置
	public static String pathnow="root";
	public static ArrayList<Byte> diskblocklasts = new ArrayList<Byte>(); //上一磁盘号组
	public static ArrayList<Byte> sites = new ArrayList<Byte>(); //上一磁盘号对应下一磁盘号的项组
	public static String filenow=null;//当前文件名
	public static byte filepropsnow=0;//当前文件属性
	public static HashMap hm=new HashMap();
	public static Disk getDisk() {
		if(disk==null){
			return disk=new Disk();
		}else{
			return disk;
		}
		
	}
	//初始化
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
               System.out.println("初始化 成功");
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
        	//文件不存在，新建文件模拟磁盘，设置文件分配表为前4个磁盘块，默认存在一个aaa目录，设置文件分配表前5项值
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
	//进入某个目录
	public void cdDir(String dir){
		
		
		if(dir.equals("..")){
			//返回上一级
			if(diskblocklasts.size()==1){
				View.FileArea.setText("非法操作");
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
				System.out.println("上一个盘块"+diskblocklast);
				System.out.println("当前盘块"+diskblocknow);
			}
			
		}else{
			boolean exist=true;//判断当前要进入的目录是否存在
			byte startDisk=diskblocknow;//当前磁盘块号
			int sitenow=0;//当前目录项中记录起始位置
			System.out.println("当前盘块"+startDisk);
			byte[] oldby=diskblocks[startDisk].getBy();
			if(dir.length()>3||dir.length()==0){
				System.out.println("不符合规范");
				View.FileArea.setText("不符合规范");
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
				System.out.println("目录不存在");
				View.FileArea.setText("目录不存在");
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

	
	//创建目录
	public void creatDir(String dir){
		//判断目录名输入是否正确
		if(dir.length()>3||dir.length()==0){
			System.out.println("目录名不符合规范");
			View.FileArea.setText("目录名不符合规范");
		}else{
			if(dir.length()==1){
				dir=dir+" "+" ";
			}else{
				if(dir.length()==2){
					dir=dir+" ";
				}
			}		
			boolean exist=false;//判断要创建的目录是否已经存在
			byte[] dirpath=dir.getBytes();
			if(diskblocknow==4){
				//根目录
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
					System.out.println("该目录已存在");
					View.FileArea.setText("该目录已存在");
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
				//非根目录
				if(diskblocknow==0){
					int start=256;
					for(int i=0;i<256;i++){
						if(fileAllacationTable[i]==0){
							start=i;
							break;
						}
					}//查找下一个空闲磁盘
					System.out.println("下一空闲磁盘块"+start);
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
						System.out.println("该目录已存在");
						View.FileArea.setText("该目录已存在");
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
		//更新界面树和磁盘块
		Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
}
	

	//创建文件路径
	 public void creatFilepath(String filepath){
		 if(diskblocklasts.size()==1){
			 View.FileArea.setText("根目录不能创建文件");
		 }else{
			 String[] dirs=filepath.split("\\.");
			 if(dirs[0].length()>3){
				 System.out.println("文件名不符合规范");
	     		View.FileArea.setText("文件名不符合规范");
			 }else{
				 if(dirs[0].length()==1){
					 dirs[0]=dirs[0]+" "+" ";
					}else{
						if(dirs[0].length()==2){
							dirs[0]=dirs[0]+" ";
						}
					}		
					 
					boolean exist=true;//判断要创建的目录是否已经存在
					byte[] dirpath=dirs[0].getBytes();
//					int sitenow=0;//当前目录项中记录起始位置
					 if(diskblocknow==0){
							int start=256;
							for(int i=0;i<256;i++){
								if(fileAllacationTable[i]==0){
									start=i;
									break;
								}
							}//查找下一个空闲磁盘
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
								System.out.println("该文件已存在");
								View.FileArea.setText("该文件已存在");
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
			//更新界面树和磁盘块
				Bootstrap.refreshDisk(disk);
				Bootstrap.refreshTree(disk);
				this.write();
		 }
		 
	 }
	 //填入文件内容
	public void creatFile(){
		boolean exist=false;
	 	byte startDisk=4;
		int sitenow=0;//当前目录项中记录起始位置
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
			String content=View.FileArea.getText();//获取文本域的值
			if(content.length()!=0){
				//文件为空暂未分配磁盘块
				int start=256;
				for(int i=0;i<256;i++){
					if(fileAllacationTable[i]==0){
						start=i;
						break;
					}
				}//查找下一个空闲磁盘
				diskblocks[diskblocknow].getBy()[sitenow]=(byte)start;

							
				if(filepropsnow==3){
					by=translateexe(content);
				}else{
					by=content.getBytes();//将文本域内容转换为字节数组
				}
				
				
				if(by.length>64){
		    		int offset = 0;  //截取的字节初始位置
		            int length = by.length;//要存储的文件的大小
		            int next=0;//下一磁盘块
		            byte[] b=new byte[64];

		            for(int j=0;j<by.length/64;j++){
		            	 b=new byte[64];
//		 				 DiskBlock diskblock=new DiskBlock(b);
//		 	        	 diskblocks[startDisk]=diskblock;
		            	 for(int i=0;i<64;i++){
		            		 b[i]=by[j*64+i];
		            	 }
		            	 diskblocks[start].setBy(b);
		            	 fileAllacationTable[start]=-1;//防止下面被重复计算
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
		            
		            	System.out.println("保存成功");
		            }else{
		         //存在错误，未处理
		            	System.out.println("保存成功");
		            	fileAllacationTable[start]=-1;
		            }
		            
		            
		          
		    	}else{
		    		//输入内容少于64字节
		    		diskblocks[start].setBy(by);
		    		fileAllacationTable[start]=-1;
		  
		    	}
				
				
			}else{
				
			}
			
		}else{
			//文件不为空，已经分配好磁盘块的情况
			by=View.FileArea.getText().getBytes();//将文本域内容转换为字节数组
			System.out.println("当前输入内容共有"+by.length);
			//计算原来该文件中存储内容的长度
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
		
		//更新界面树和磁盘块
		Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
	}
	//读取文件
	public void readFile(String filepath){
		String[] dirs=filepath.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("文件名不符合规范");
			 View.FileArea.setText("文件名不符合规范");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			 filenow=dirs[0];//将当前文件名抛到公共区
			boolean exist=true;//判断要读取的文件是否已经存在
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
	 			System.out.println("文件不存在");
	 			View.FileArea.setText("文件不存在");
	 		}else{
	 			if(startDisk==0){
	 				View.FileArea.setText("");//当文件为空的时候
					View.FileArea.setEnabled(true);//允许输入
					View.FileArea.setBackground(Color.white);
					View.jbutton.setEnabled(true);
	 			}else{
	 				//当文件不为空的的时候
	 				String string="";
		 			while(fileAllacationTable[startDisk]!=-1){
		 				string+=new String(diskblocks[startDisk].getBy());
		 				System.out.println(string);
		 				startDisk=fileAllacationTable[startDisk];
		 			}
		 			string+=new String(diskblocks[startDisk].getBy());
					System.out.println(string);
					View.FileArea.setText(string);
					View.FileArea.setEnabled(true);//允许输入
					View.FileArea.setBackground(Color.white);
					View.jbutton.setEnabled(true);
	 			}
	 		
	 		}
		 }
		
	 	
	}
	
	
	//删除文件
	public void deleteFile(String filepath){
		String[] dirs=filepath.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("文件名不符合规范");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			boolean exist=true;//判断要读取的文件是否存在
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
				System.out.println("路径错误或文件不存在");
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
					fileAllacationTable[startDisk]=0;//将文件分配表中的对应项改为0
				}else{
					
				}
			}
		
			this.write();
		 }
		 Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
	}
	//删除空目录
	public void deletDir(String filepath){
		 if(filepath.length()>3){
			 System.out.println("目录名不符合规范");
			 View.FileArea.setText("目录名不符合规范");
		 }else{
			 if(filepath.length()==1){
				 filepath=filepath+" "+" ";
				}else{
					if(filepath.length()==2){
						filepath=filepath+" ";
					}
				}		
			boolean exist=true;//判断要删除的目录是否存在
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
							System.out.println("该目录不为空，不能删除");
							View.FileArea.setText("该目录不为空，不能删除");
						}
						
						break;
					}
								
				}else{
					exist=false;
				}
														
			}
			
			if(exist==false){
				System.out.println("目录不存在");
				View.FileArea.setText("目录不存在");
			}else{ 
	//				this.write();
				
			}
		 }
		 Bootstrap.refreshDisk(disk);
		Bootstrap.refreshTree(disk);
		this.write();
	}
	
	//复制文件
	public void copyFile(String sourcefile,String targetfile){
		String[] dirs=sourcefile.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("文件名不符合规范");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			boolean exist=true;//判断要复制的文件是否已经存在
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
				System.out.println("当前要复制的文件不存在 ");
				View.FileArea.setText("当前要复制的文件不存在");
			}else{
				byte[] source=new byte[0];
				if(fileAllacationTable[startDisk]!=-1&&fileAllacationTable[startDisk]!=0){
					//当前文件超过64字节
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
					//当前文件小于64字节
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
				 System.out.println("路径不符合规范");
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
		
		boolean exist=true;//判断当前目录是否存在
		byte startDisk=4;//当前磁盘块号
		int sitenow=0;//当前目录项中记录下一磁盘块号的信息
		byte lastDsik=0;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath=dirs[0].getBytes();
		for(int z=0;z<dirs.length;z++){				
			oldby=diskblocks[startDisk].getBy();//获取下一个磁盘块
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
		}//判断路径是否存在
		if(exist==false){
			System.out.println("路径不存在");
			View.FileArea.setText("路径不存在");
		}else{
			if(startDisk==0){
			//当前要复制进的目录还没有申请空间
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
				}//查找下一个空闲磁盘
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
				//当前要复制进的目录已经有空间
				System.out.println("当前要复制进的目录已经有空间"+startDisk);
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
					System.out.println("该文件已存在");
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
		int sitenow=0;//当前目录项中记录起始位置
		for(int i=0;i<256;i++){
			if(fileAllacationTable[i]==0){
				start=i;
				break;
			}
		}//查找下一个空闲磁盘
		sitenow=start;
    	//将内容转为字节
		byte[] by=content.getBytes();
    	System.out.println("当前输入内容共有"+by.length);
    	//判断一个磁盘能否放得下
    	if(by.length>64){
    		int offset = 0;  //截取的字节初始位置
            int length = by.length;//要存储的文件的大小
            int next=0;//下一磁盘块
            byte[] b=new byte[64];

            for(int j=0;j<by.length/64;j++){
            	 b=new byte[64];
 				 DiskBlock diskblock=new DiskBlock(b);
 	        	 diskblocks[start]=diskblock;
            	 for(int i=0;i<64;i++){
            		 b[i]=by[j*64+i];
            	 }
            	 diskblocks[start].setBy(b);
            	 fileAllacationTable[start]=-1;//防止下面被重复计算
            	 for(int i=0;i<256;i++){
	            	if(fileAllacationTable[i]==0){
	            		next=i;
	            		break;
	            	}
            	 }
            	 fileAllacationTable[start]=(byte)next;
            	 
            	 start=next;
            	 System.out.println("下一磁盘块号是"+start);
            }
            
            if(by.length%64!=0){
            	b=new byte[64];
            	for(int i=0;i<by.length%64;i++){
            		b[i]=by[(b.length/64)*64+i];
            		
            	}
            	DiskBlock diskblock=new DiskBlock(b);
            	diskblocks[start]=diskblock;
            	fileAllacationTable[start]=-1;
            
            	System.out.println("保存成功");
            }else{
         
            	System.out.println("保存成功");
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
	//创建可执行文件
	public void newexe(String file){
		String[] dirs=file.split("\\.");
		 if(dirs[0].length()>3){
			 System.out.println("文件名不符合规范");
			 View.FileArea.setText("文件名不符合规范");
		 }else{
			 if(dirs[0].length()==1){
				 dirs[0]=dirs[0]+" "+" ";
				}else{
					if(dirs[0].length()==2){
						dirs[0]=dirs[0]+" ";
					}
				}		
			filenow=dirs[0];//将当前文件名抛到公共区
			filepropsnow=3;
			boolean exist=false;//判断要创建的目录是否已经存在
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
						System.out.println("该可执行文件已存在");
						 View.FileArea.setText("该可执行文件已存在");
					}else{
						String exepath=pathnow+">"+dirs[0];						
						hm.put(hm.size()+1,exepath);//将当前exe文件的路径添加到hm中，并编号
						View.FileArea.setText("");//当文件为空的时候
						View.FileArea.setEnabled(true);//允许输入
						View.FileArea.setBackground(Color.white);
						View.jbutton.setEnabled(true);
						 if(diskblocknow==0){
								int start=256;
								for(int i=0;i<256;i++){
									if(fileAllacationTable[i]==0){
										start=i;
										break;
									}
								}//查找下一个空闲磁盘
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
//								sitenow=this.createxeFile();//填入可执行文件内容并且返回存放内容的开始盘块
//								System.out.println("该文件内容存放位置"+sitenow);
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
			//更新界面树和磁盘块
			Bootstrap.refreshDisk(disk);
			Bootstrap.refreshTree(disk);
			this.write();
	}
//	
//	public int createxeFile(){
//		boolean exist=false;//判断要创建的目录是否已经存在
//		byte[] dirpath=exenow.getBytes();
//		int sitenow=0;//当前目录项中记录起始位置
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
//		}//查找下一个空闲磁盘
//		diskblocks[5].getBy()[sitenow]=(byte)start;
//		
//		
//		
//
//		byte[] result=this.translateexe();//将输入的字符串转换为字节数组并返回
//		if(result.length>64){
//			byte[] b=new byte[64];
//			 for(int j=0;j<result.length/64;j++){
//				 for(int i=0;i<64;i++){
//					b[i]=result[j*64+i];
//            	 }
//				 diskblocks[start].setBy(b);
//				 fileAllacationTable[start]=-1;//防止下面被重复计算
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
//            	System.out.println("保存成功");
//            }else{
//            	//刚好为64的倍数的时候会错，暂时不做处理。
//            	System.out.println("保存成功");
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
		//输入内容
//		Scanner input = new Scanner(System.in);
//    	String content = input.nextLine();
    	String[] contents=content.split("\n");//将输入的指令按回车进行拆分
    	for(int i=0;i<contents.length;i++){
    		//对每条指令进行判断，转化为字节
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
    			String equipment=contents[i].substring(1,2);//取出设备号
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
    			String time=contents[i].substring(2,contents[i].length());//取出时间
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
	
	
	//写入磁盘模拟文件disk
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
	//返回exe文件大小
	public static int getexesize(int num){
		System.out.println("返回exe文件大小的num"+num);
		String exepath=(String) hm.get(num);//得到对应num的可执行文件路径
		System.out.println(exepath);
		String[] exepaths=exepath.split(">");	
		int size=0;
		byte startDisk=4;
		boolean exist;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath;
		for(int z=1;z<exepaths.length-1;z++){				
			oldby=diskblocks[startDisk].getBy();//获取下一个磁盘块
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
			oldby=diskblocks[startDisk].getBy();//获取下一个磁盘块
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
		System.out.println("返回exe文件大小的size"+size);
		return size;
	}
	public static byte[] getexefile(int num){
		System.out.println("返回exe文件内容的num"+num);
		
		String exepath=(String) hm.get(num);//得到对应num的可执行文件路径
		String[] exepaths=exepath.split(">");	
		int size=0;
		byte startDisk=4;
		boolean exist;
		byte[] oldby=diskblocks[startDisk].getBy();
		byte[] dirpath;
		for(int z=1;z<exepaths.length-1;z++){				
			oldby=diskblocks[startDisk].getBy();//获取下一个磁盘块
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
			oldby=diskblocks[startDisk].getBy();//获取下一个磁盘块
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
			System.out.println("当前可执行文件内容第一位"+(int)content[0]);
		}else{
				//exe文件长度大于64字节
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