package filrmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import view.View;
import javafx.stage.Stage;


public class FileOperation {
//	public static Disk disk=new Disk();

	public static void chooseOperation(String command,Disk disk){
//		System.out.println("=============欢迎使用文件管理系统2.0=============");
//		System.out.println("请输入命令");
//		System.out.println("cd：进入某个目录，makdir：创建目录，create：创建TXT文件，type：查看TXT文件，delete：删除TXT文件，rmdir：删除空目录，copy：复制TXT文件，newexe：创建exe文件");
//		System.out.println("请输入命令");

//		System.out.print(disk.pathnow+">");
//		Scanner input = new Scanner(System.in);
//    	String n = input.nextLine();
//    	String[] commands = n.split(" ");
		View.FileArea.setText("");
		String[] path=command.split(":");
		String[] commands=path[1].split(" ");
		
//    	while(!commands[0].equals("exit")){
    		switch(commands[0]){
    			case "cd":disk.cdDir(commands[1]);break;
	    		case "makdir":disk.creatDir(commands[1]);break;
	    		case "create":disk.creatFilepath(commands[1]);break;
	    		case "type":disk.readFile(commands[1]);break;
	    		case "delete":disk.deleteFile(commands[1]);break;
	    		case "rmdir":disk.deletDir(commands[1]);break;
	    		case "copy":disk.copyFile(commands[1],commands[2]);break;
	    		case "newexe":disk.newexe(commands[1]);break;
	    	}
//    		System.out.println("请输入命令");
//    		System.out.print(disk.pathnow+">");
//    		n = input.nextLine();
//    		commands = n.split(" ");
//    	}
    	
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		FileOperation fileoperation=new FileOperation();
//		disk.init();
//		fileoperation.chooseOperation();
//		disk.write();
//	}

}
