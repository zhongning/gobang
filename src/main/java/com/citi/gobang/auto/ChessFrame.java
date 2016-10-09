package com.citi.gobang.auto;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 *类ChessFrame主要功能是创建五子棋游戏主窗体和菜单
 **/
public class ChessFrame extends JFrame implements ActionListener 
{
    private static final long serialVersionUID = 2183726320279905885L;
    private String[] strmode={"人机对弈","人人对弈"};
    private String[] strorder={"玩家先手","计算机先手"};
    public static boolean iscomputer=true;
    public static boolean checkcomputer=true;
    public static boolean isorder=true;
    private int width=15,height=15;
    private ChessModel cm;
    private MainPanel mp;
    public static int qipan[][]=new int[15][15];
    
    //构造五子棋游戏的主窗体
        public ChessFrame()
        {        this.setTitle("五子棋游戏");
        cm=new ChessModel(1);
        mp=new MainPanel(cm);
        Container con=this.getContentPane();
        con.add(mp,"Center");
        this.setResizable(false);
        this.addWindowListener(new ChessWindowEvent());
        MapSize(14,14);
        setLocation(400,100);
        JMenuBar mbar = new JMenuBar();
        this.setJMenuBar(mbar);
        JMenu gameMenu = new JMenu("游戏");
        mbar.add(makeMenu(gameMenu, new Object[] {
            "开局","模式","先手顺序","悔棋", null, "退出"
                       }, this));
        JMenu lookMenu =new JMenu("视图");
        mbar.add(makeMenu(lookMenu,new Object[] {
            "Metal","Motif","Windows"
                       },this));
        JMenu helpMenu = new JMenu("帮助");
        mbar.add(makeMenu(helpMenu, new Object[] {
            "关于"
        		        }, this));
        }

    //构造五子棋游戏的主菜单
    public  JMenu makeMenu(Object parent, Object items[], Object target)
    {
        JMenu m = null;
        if(parent instanceof JMenu)
            m = (JMenu)parent;
        else if(parent instanceof String)
            m = new JMenu((String)parent);
        else
        	return null;
        for(int i = 0; i < items.length; i++)
            if(items[i] == null)
            	m.addSeparator();
           else if(items[i] == "模式")
           {
        	   JMenu jm = new JMenu("模式");
               ButtonGroup group=new ButtonGroup();
               JRadioButtonMenuItem rmenu;
               for(int h=0;h<strmode.length;h++)
               {
                    rmenu=makeRadioButtonMenuItem(strmode[h],target);
                    if(h==0)
                        rmenu.setSelected(true);
                    jm.add(rmenu);
                    group.add(rmenu);
                }
                m.add(jm);
            }
           else if(items[i]=="先手顺序")
           {
        	   JMenu jm = new JMenu("先手顺序");
               ButtonGroup group=new ButtonGroup();
               JRadioButtonMenuItem rmenu;
               for(int k=0;k<strorder.length;k++)
               {
                    rmenu=makeRadioButtonMenuItem(strorder[k],target);
                    if(k==0)
                        rmenu.setSelected(true);
                    jm.add(rmenu);
                    group.add(rmenu);
                }
                m.add(jm);
           }
           else
        	   m.add(makeMenuItem(items[i], target));
           return m;
    }
    
   //构造五子棋游戏的菜单项
    public  JMenuItem makeMenuItem(Object item, Object target){
        JMenuItem r = null;
        if(item instanceof String)
           r = new JMenuItem((String)item);
       else if(item instanceof JMenuItem)
           r = (JMenuItem)item;
       else
                return null;
        if(target instanceof ActionListener)
            r.addActionListener((ActionListener)target);
        return r;
    }
   
   //构造五子棋游戏的单选按钮式菜单项
      public  JRadioButtonMenuItem makeRadioButtonMenuItem(
       Object item, Object target){
        JRadioButtonMenuItem r = null;
        if(item instanceof String)
           r = new JRadioButtonMenuItem((String)item);
       else if(item instanceof JRadioButtonMenuItem)
           r = (JRadioButtonMenuItem)item;
        else
        	           return null;
        if(target instanceof ActionListener)
           r.addActionListener((ActionListener)target);
       return r;
   }
   
    public void MapSize(int w,int h){
        setSize(w * 30+67 , h * 30+110);
        if(!ChessFrame.checkcomputer) {
            ChessFrame.iscomputer=false;
        } else {
           ChessFrame.iscomputer=true;
        }
       mp.setModel(cm);
        mp.repaint();
    }
    
   public boolean getiscomputer(){
        return ChessFrame.iscomputer;
    }
    
   public void restart(){
       int modeChess = cm.getModeChess();
       if(modeChess <= 3 && modeChess >= 1){
           cm = new ChessModel(modeChess);
           MapSize(cm.getWidth(),cm.getHeight());
            System.out.println("/u81EA/u5B9A/u4E49");
        }
    }
    
   public void actionPerformed(ActionEvent e){
        String arg=e.getActionCommand();
        try{
           if (arg.equals("Windows"))
               UIManager.setLookAndFeel(
                   "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
           else if(arg.equals("Motif"))
               UIManager.setLookAndFeel(
                   "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
           else
        	   UIManager.setLookAndFeel(
                   "javax.swing.plaf.metal.MetalLookAndFeel" );
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception ee){}
        if(arg.equals("人机对弈")){
            ChessFrame.checkcomputer=true;
            ChessFrame.iscomputer=true;
            cm=new ChessModel(cm.getModeChess());
           MapSize(cm.getWidth(),cm.getHeight());
            SwingUtilities.updateComponentTreeUI(this);
        }
        if(arg.equals("人人对弈")){
            ChessFrame.checkcomputer=false;
            ChessFrame.iscomputer=false;
            cm=new ChessModel(cm.getModeChess());
            MapSize(cm.getWidth(),cm.getHeight());
            SwingUtilities.updateComponentTreeUI(this);
        }
        if(arg.equals("计算机先手"))
        {
        	ChessFrame.isorder=false;
        	cm=new ChessModel(cm.getModeChess());
        	MapSize(cm.getWidth(),cm.getHeight());
            SwingUtilities.updateComponentTreeUI(this);
        }
        if(arg.equals("玩家先手"))
        {
        	ChessFrame.isorder=true;
        	cm=new ChessModel(cm.getModeChess());
        	MapSize(cm.getWidth(),cm.getHeight());
            SwingUtilities.updateComponentTreeUI(this);
        }
        if(arg.equals("开局")){
        	restart();
            if(ChessFrame.isorder==false)
          	   cm.first();
        }
        if(arg.equals("悔棋")){
        	 int modeChess = cm.getModeChess();
       	     if(modeChess <= 3 && modeChess >= 1){
       	    	cm = new ChessModel(modeChess);
       	    	 for(int i=0;i<this.width;i++)
       	    	 {
       	    		 for(int j=0;j<this.height;j++)
       	    		 {
       	    			 cm.arrMapShow[i][j] =qipan[i][j];
       	    		 }
       	    	 }
       	    	 MapSize(cm.getWidth(),cm.getHeight());
       	     }  
        }
        if(arg.equals("关于"))
            JOptionPane.showMessageDialog(this, "五子棋游戏测试版本——By 江西财经大学08计一张帅", "关于", 0);
        if(arg.equals("退出"))
            System.exit(0);
    }
}

