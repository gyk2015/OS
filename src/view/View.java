package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.ScrollPaneConstants;

import filrmanage.Disk;
import filrmanage.FileOperation;

import java.awt.ScrollPane;
import java.awt.Panel;
import java.awt.SystemColor;

public class View extends JFrame {
	private static View view;
	private JLabel HR_osTimer;///
	private JLabel HR_timerTricks;///
	private JLabel HR_procedingID;///
	private JPanel contentPane;
	private JTextField HR_comandInterference;
	public static JTextArea FileArea;
	private JScrollPane jscrollPane;
	public static JButton jbutton;
	private JTextArea HR_willActiveProcedure;///
	private JTextArea HR_quequeID ;///
	private JLabel HR_activeComand ;///
	private  JTextArea HR_mainMemoryDetail;///
	private JTextArea HR_deviceDeatail ;///
	private JLabel HR_result ;
	private JLabel HR_current_result;
	private Panel [] HRPanels;
	private JTree HR_tree;
	private Panel panel;
	private Font font_en;
	private Font font_cn;
	private Border border;
	public void setHR_tree(JTree hR_tree) {
		HR_tree = hR_tree;
	}
	public static View getView(){
		if(view==null){
			return view = new View();
		}else{
			return view;
		}
	}
	private View() {
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 10, 1340,1000);
		contentPane = new JPanel();
		contentPane.setName("cp");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		setSize(new Dimension((int)(screenSize.width * 0.6),(int)(screenSize.height *0.6)));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		
		int y0=20,y1=355,y2=700;
		border = BorderFactory.createEmptyBorder(10,18,10,10);
		font_en = new Font("Dialog",0, 20);
		font_cn = new Font("",0, 18);
		
		HR_osTimer = new JLabel("0:0");
		//HR_osTimer.setBorder(new LineBorder(Color.DARK_GRAY));
		HR_osTimer.setHorizontalAlignment(SwingConstants.CENTER);
		HR_osTimer.setAlignmentX(Component.CENTER_ALIGNMENT);
		HR_osTimer.setToolTipText("");
		HR_osTimer.setFont(new Font("Dialog",1, 20));
		//HR_osTimer.setForeground(Color.BLACK);
		HR_osTimer.setOpaque(true);
		HR_osTimer.setBackground(Color.white);
		HR_osTimer.setName("mp1.1");
		HR_osTimer.setAlignmentY(Component.TOP_ALIGNMENT);
		HR_osTimer.setBounds(y0, 20, 140, 60);
		HR_osTimer.setRequestFocusEnabled(false);
		contentPane.add(HR_osTimer);
		
		
		HR_procedingID = new JLabel("【正运行进程ID】");
		HR_procedingID.setName("mp1.2");
		HR_procedingID.setFont(font_cn);
		HR_procedingID.setHorizontalAlignment(SwingConstants.LEFT);
		HR_procedingID.setOpaque(true);
		HR_procedingID.setBackground(Color.white);
		HR_procedingID.setBorder(border);
		//HR_procedingID.setBorder(new LineBorder(Color.MAGENTA));
		HR_procedingID.setBounds(y0,115,310,60);
		contentPane.add(HR_procedingID);
		
		HR_timerTricks = new JLabel("时间片：6");
		HR_timerTricks.setName("mp1.3");
		HR_timerTricks.setFont(font_cn);
		HR_timerTricks.setHorizontalAlignment(SwingConstants.LEFT);
		HR_timerTricks.setOpaque(true);
		HR_timerTricks.setBackground(Color.white);
		HR_timerTricks.setBorder(border);
		//HR_timerTricks.setBorder(border);
		//HR_timerTricks.setBorder(new LineBorder(Color.ORANGE));
		//HR_timerTricks.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		HR_timerTricks.setBounds(180, 20, 150, 60);
		contentPane.add(HR_timerTricks);
		
			/**---------------------------命令--------------------------------**/
		HR_comandInterference = new JTextField();
		HR_comandInterference.setName("mp1.4");
		HR_comandInterference.setFont(font_en);
		HR_comandInterference.setHorizontalAlignment(JTextField.LEFT);
		HR_comandInterference.setOpaque(true);
		HR_comandInterference.setBackground(Color.white);
		HR_comandInterference.setBorder(border);
		HR_comandInterference.setText(Disk.pathnow+":");
		HR_comandInterference.setBounds(y2, 20, 495,60);
		
		HR_comandInterference.addKeyListener(new KeyListener() {  
		    public void keyPressed(KeyEvent e) {  
		        if (e.getKeyCode() == KeyEvent.VK_ENTER) {  
		            FileOperation.chooseOperation(HR_comandInterference.getText(),Disk.getDisk()); 
		        		HR_comandInterference.setText(Disk.pathnow+":");

		        }  

		    }  
		    public void keyReleased(KeyEvent e) {  
		    }  
		    public void keyTyped(KeyEvent e) {  
		    }  
		});
		contentPane.add(HR_comandInterference);
		HR_comandInterference.setColumns(10);

		/**--------------------------------------文本----------------------------**/
		FileArea = new JTextArea();
		FileArea.setTabSize(4);
		FileArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		FileArea.setLineWrap(true);
		FileArea.setWrapStyleWord(true);
		FileArea.setBackground(new Color(220,220,220));
		FileArea.setEnabled(false);
//		FileArea.setBounds(y2,100,500,60);
		
		jbutton = new JButton("保存");
		jbutton.setBounds(y2,390,250,20);
		jbutton.setEnabled(false);
		jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Disk.getDisk().creatFile();
				FileArea.setText("");
				FileArea.setEnabled(false);
				FileArea.setBackground(new Color(220,220,220));
				jbutton.setEnabled(false);
            }
        });
		jscrollPane = new JScrollPane(FileArea);
		jscrollPane.setBounds(y2,100,250,290);
		contentPane.add(jscrollPane);
		contentPane.add(jbutton);
		
		
		HR_willActiveProcedure = new JTextArea("操作系统");
		HR_willActiveProcedure.setName("mp2.1");
		HR_willActiveProcedure.setFont(font_cn);
		HR_willActiveProcedure.setEditable(false);
		HR_willActiveProcedure.setOpaque(true);
		HR_willActiveProcedure.setBackground(Color.white);
		HR_willActiveProcedure.setBorder(border);
		HR_willActiveProcedure.setBounds(y1, 20, 330, 80);
		contentPane.add(HR_willActiveProcedure);

		
		HR_quequeID = new JTextArea("【PCB状态】\n就绪进程\n空\n\n\n阻塞进程\n空");
		HR_quequeID.setName("mp2.2");
		HR_quequeID.setFont(font_cn);
//		HR_quequeID.setAutoscrolls(true);
		HR_quequeID.setEditable(false);
		HR_quequeID.setOpaque(true);
		HR_quequeID.setBackground(Color.white);
		HR_quequeID.setBorder(border);
		HR_quequeID.setBounds(y1, 110,330, 220);
		contentPane.add(HR_quequeID);

		HR_activeComand = new JLabel("正在执行指令：");
		HR_activeComand.setName("mp3.1");
		HR_activeComand.setFont(font_cn);
//		HR_activeComand.setAutoscrolls(true);
		HR_activeComand.setHorizontalAlignment(SwingConstants.LEFT);
		HR_activeComand.setOpaque(true);
		HR_activeComand.setBackground(Color.white);
		HR_activeComand.setBorder(border);
		HR_activeComand.setBounds(y0, 195, 310, 60);
		contentPane.add(HR_activeComand);
		
		HR_mainMemoryDetail = new  JTextArea("【内存分区情况】\n起始地址---终止地址---情况\n");
		HR_mainMemoryDetail.setFont(font_cn);
		HR_mainMemoryDetail.setEditable(false);
//		HR_mainMemoryDetail.setAutoscrolls(true);
		HR_mainMemoryDetail.setOpaque(true);
		HR_mainMemoryDetail.setBackground(Color.white);
		HR_mainMemoryDetail.setBorder(border);
		HR_mainMemoryDetail.setBounds(y0, 420, 325, 450);
		contentPane.add(HR_mainMemoryDetail);
		
		HR_deviceDeatail = new JTextArea("【设备使用情况】\n");
		HR_deviceDeatail.setName("mp3.2");
		HR_deviceDeatail.setFont(font_cn);
		HR_deviceDeatail.setEditable(false);
		HR_deviceDeatail.setOpaque(true);
		HR_deviceDeatail.setBackground(Color.white);
		HR_deviceDeatail.setBorder(border);
		HR_deviceDeatail.setBounds(y1, 340, 310, 500);
		contentPane.add(HR_deviceDeatail);
		
		HR_current_result = new JLabel("进程中间结果：");
		HR_current_result.setName("mp3.4");
		HR_current_result.setFont(font_cn);
		HR_current_result.setHorizontalAlignment(SwingConstants.LEFT);
		HR_current_result.setOpaque(true);
		HR_current_result.setBackground(Color.white);
		HR_current_result.setBorder(border);
		HR_current_result.setBounds(y0, 275, 310, 60);
		contentPane.add(HR_current_result);
		
		HR_result = new JLabel("进程最终结果：");
		HR_result.setName("mp3.3");
		HR_result.setFont(font_cn);
		HR_result.setHorizontalAlignment(SwingConstants.LEFT);
		HR_result.setOpaque(true);
		HR_result.setBackground(Color.white);
		HR_result.setBorder(border);
		HR_result.setBounds(y0, 355, 310, 60);
		contentPane.add(HR_result);
		
		

		panel = new Panel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(1000, 100, 180, 310);
		contentPane.add(panel);
		panel.setLayout(null);
	    
		HR_tree = new JTree();
		HR_tree.setBounds(10, 10, 485, 400);
		panel.add(HR_tree);
		HR_tree.setName("mp2.4");
		HR_tree.setFont(font_cn);
		
	
		Panel HR_GYK = new Panel();
		HR_GYK.setBackground(Color.LIGHT_GRAY);
		HR_GYK.setBounds(y2, 430, 495, 410);
		contentPane.add(HR_GYK);
		HR_GYK.setLayout(null);

		HRPanels = new Panel[256];
		int marginX = 10;
		int marginY = 5;
		for(int i = 0 ; i<256; i++)
		{
			int X = i%16 * 30 +15;
			int Y = i/16 * 25 + 10;
			Panel panelOne = new Panel();
			panelOne.setBounds(X, Y, 15, 15);
			panelOne.setBackground(Color.RED);
			HR_GYK.add(panelOne);
			HRPanels[i] = panelOne;
		}
		
	}

	public Panel getPanel() {
		return panel;
	}

	public void setPanel(Panel panel) {
		this.panel = panel;
	}


	public Panel[] getHRPanels()
	{
		return HRPanels;
	}
	
	public Panel getHR_tree_Pane()
	{
		return panel;
	}
	
	public JTree getHR_tree()
	{
		return HR_tree;
	}
	
	public JLabel getHR_current_result()
	{
		return HR_current_result;
	}
	
	public JLabel getHR_result()
	{
		return HR_result;
	}
	
	public JTextArea getHR_deviceDeatail()
	{
		return HR_deviceDeatail;
	}
	
	public  JTextArea getHR_mainMemoryDetail()
	{
		return HR_mainMemoryDetail;
	}
	
	public JLabel getHR_activeComand()
	{
		return HR_activeComand;
	}
	
	public JTextArea getHR_quequeID()
	{
		return HR_quequeID;
	}
	
	public JTextArea getHR_willActiveProcedure()
	{
		return HR_willActiveProcedure;
	}
	
	public JLabel getHR_procedingID()
	{
		return HR_procedingID;
	}
	
	public JLabel getHR_osTimer()
	{
		return HR_osTimer;
	}
	
	public JLabel getHR_timerTricks()
	{
		return HR_timerTricks;
	}
	
	public JTextField gettextField()
	{
		return HR_comandInterference;
	}
	
	public void refresh(){
		 //tree.updateUI();  
	}
}
