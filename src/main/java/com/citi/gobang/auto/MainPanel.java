package com.citi.gobang.auto;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/*
 *类MainPanel主要完成如下功能：
 *1、构建一个面板，在该面板上画上棋盘；
 *2、处理在该棋盘上的鼠标事件（如鼠标左键点击、鼠标右键点击、鼠标拖动等）
 **/
public class MainPanel extends JPanel 
    implements MouseListener,MouseMotionListener{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width,height;//棋盘的宽度和高度
    private ChessModel cm;
   
   //根据棋盘模式设定面板的大小
    MainPanel(final ChessModel mm){
        cm=mm;
       width=cm.getWidth();
        height=cm.getWidth();
       addMouseListener(this);
   }
   
   //根据棋盘模式设定棋盘的宽度和高度
   public void setModel(final ChessModel mm){
        cm = mm;
        width = cm.getWidth();
       height = cm.getWidth();
    }

    //根据坐标计算出棋盘方格棋子的信息（如白子还是黑子），
    //然后调用draw方法在棋盘上画出相应的棋子
    public void paintComponent(final Graphics g){
        super.paintComponent(g);
        for(int j = 0; j <=height; j++){
            for(int i = 0; i <=width; i++){
                final int v = cm.getarrMapShow()[i][j];
                draw(g, i, j, v);
           }
        }
    }

    //根据提供的棋子信息（颜色、坐标）画棋子
    public void draw(final Graphics g, final int i, final int j, final int v){
        final int x = 30 * i+30;
        final int y = 30 * j+30;
        
      //画棋盘
        if(i!=width && j!=height){
             g.setColor(Color.gray);
            g.drawRect(x,y,30,30);
            g.setColor(Color.black);
            g.fillRect(width*15+27, height*15+27, 7, 7);//画中间标志点
            g.fillRect(width*7+19, height*7+19, 7, 7); //画左上角标志点
            g.fillRect(width*23+35, height*23+35, 7, 7);
            g.fillRect(width*23+35, height*7+19, 7, 7);
            g.fillRect(width*7+19, height*23+35, 7, 7);
        }
        
        //画黑色棋子
        if(v == 1 ){
            g.setColor(Color.gray);
           g.drawOval(x-11,y-11,22,22);
            g.setColor(Color.black);
            g.fillOval(x-11,y-11,22,22);
        }
        //画白色棋子
       if(v == 2 ){
    	    g.setColor(Color.gray);
            g.drawOval(x-11,y-11,22,22);
            g.setColor(Color.white);
            g.fillOval(x-11,y-11,22,22);
        }
        if(v ==3){
           g.setColor(Color.cyan);
            g.drawOval(x-11,y-11,22,22);
       }
    }

   //响应鼠标的点击事件，根据鼠标的点击来下棋，
    //根据下棋判断胜负等
    public void mousePressed(final MouseEvent evt){
    	final int x = (evt.getX()-10) / 30;
        final int y = (evt.getY()-10) / 30;
        int flag=1;
        System.out.println(x+" "+y);
        if (evt.getModifiers()==MouseEvent.BUTTON1_MASK)
        {
        	for(int m=0;m<=cm.getWidth();m++)
        	{
    		 	for(int n=0;n<=cm.getWidth();n++)
    		 	{
    		 		ChessFrame.qipan[m][n]=cm.arrMapShow[m][n];
    		 	}
    	 	}
        	if(cm.arrMapShow[x][y]==-1)
        	{
            	cm.play(x,y);
            	System.out.println(cm.getisOdd()+" "+cm.getarrMapShow()[x][y]);
            	repaint();
            	//判断是否为人机对弈
            	if(ChessFrame.iscomputer&&!cm.getisExist())
            	{
            		if(cm.judgeSuccess(x,y,false)==true)
            		{
            			JPanel jp=new JPanel();
            			cm.showSuccess(jp);
            			flag=0;
            		}	
            		repaint();
            		if(flag==1)
            			cm.computerDo(0);             
            	}	
            	else if(!ChessFrame.iscomputer&&!cm.getisExist())
            	{
            		if(cm.judgeSuccess(x,y,false)==true)
            		{
            			JPanel jp=new JPanel();
            			cm.blackSuccess(jp);
            		}	
            		else 
            			if(cm.judgeSuccess(x,y,true)==true)
            			{
            				JPanel jp=new JPanel();
            				cm.whiteSuccess(jp);
            			}	
            		repaint();
            	}
        	}
        }
    }
    public void mouseClicked(final MouseEvent evt){}
    public void mouseReleased(final MouseEvent evt){}
    public void mouseEntered(final MouseEvent mouseevt){}
    public void mouseExited(final MouseEvent mouseevent){}
    public void mouseDragged(final MouseEvent evt){}
    
    //响应鼠标的拖动事件
        public void mouseMoved(final MouseEvent moveevt){
//        final int x = (moveevt.getX()-10) / 20;
//        final int y = (moveevt.getY()-10) / 20;
//        cm.readyplay(x,y);
//        repaint();
    } 
}
