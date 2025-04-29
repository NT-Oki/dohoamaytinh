package draw;

import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class RenderRoom extends GLJPanel implements GLEventListener, KeyListener,MouseMotionListener {
	int weight=800;
	int height=800;
	float eyeX=0f;
	float eyeY=0f;
	float eyeZ=4.5f;//4.5
	float centerX=0f;
	float centerY=0.5f;
	float centerZ=-10f;
	float upX=0f;
	float upY=1f;
	float upZ=-10f;
	GL2 gl;
	GLU glu=new GLU();
	GLUT glut=new GLUT();
	float add_all=7f;
	float[][] back_wall = {//goc duoi trai,goc duoi phai,goc tren phai,goc tren trai
		    {-1f-add_all, -1f-add_all/2, -10f},
		    {1f+add_all, -1f-add_all/2, -10f},
		    {1f+add_all, 1f+add_all/2, -10f},
		    {-1f-add_all, 1f+add_all/2, -10f}
		};
	float[][] right_wall = {//dưới trái,trên trái, trên phải,dưới phải
			  {1f+add_all, -1f-add_all/2, -10f},
			  {1f+add_all, 1f+add_all/2, -10f},
		    {1f+add_all, 1f+add_all/2, -8f+add_all},
		    {1f+add_all, -1f-add_all/2, -8f+add_all}
		};
	float[][] left_wall = {//goc phai dưới, goc phải trên. góc trái trên, góc trái dưới
			 {-1f-add_all, -1f-add_all/2, -10f},
			 {-1f-add_all, 1f+add_all/2, -10f},
		    {-1f-add_all, 1f+add_all/2, -8f+add_all},
		    {-1f-add_all, -1f-add_all/2, -8f+add_all}
		};
	float[][] under_wall = {//tren trai,tren phai, duoi phai,duoi trai
			  {-1f-add_all, -1f-add_all/2, -10f},
			  {1f+add_all, -1f-add_all/2, -10f},
			  {1f+add_all, -1f-add_all/2, -8f+add_all},
			  {-1f-add_all, -1f-add_all/2, -8f+add_all}
		};
	float[][] roof_wall = {//tren trai,tren phai, duoi phai,duoi trai
			  {-1f-add_all, 1f+add_all/2, -8f+add_all},
			   {1f+add_all, 1f+add_all/2, -8f+add_all},
			  {1f+add_all, 1f+add_all/2, -10f},
			  {-1f-add_all, 1f+add_all/2, -10f}
		};
	
	float[][] TV = {//tren trai,tren phai, duoi phai,duoi trai//
			{(-1f-add_all+(1f+add_all))/2, (1f+add_all/2+-1f-add_all/2)/2, -9f},
			{-1f-add_all, (1f+add_all/2+-1f-add_all/2)/2, -9f},
			{-1f-add_all, 1f+add_all/2, -9f},
			{(-1f-add_all+(1f+add_all))/2, 1f+add_all/2, -9f}
			
			
		};
	float[][] contain_TV_roof = {//tren trai,tren phai, duoi phai,duoi trai
			  {(-1f-add_all)*(3/2), 1f+add_all/2, -9f},
			   {(1f+add_all)/2, 1f+add_all/2,-9f},
			  {(1f+add_all)/2, 1f+add_all/2, -10f},
			  {(-1f-add_all)*(3/2), 1f+add_all/2, -10f}
		};
	float[][] contain_TV_roof_front = {//goc duoi trai,goc duoi phai,goc tren phai,goc tren trai
		    {(-1f-add_all)*(3/2), 1.5f, -9f},
		    {(1f+add_all)/2, 1.5f, -9f},
		    {(1f+add_all)/2, 1f+add_all/2, -9f},
		    {(-1f-add_all)*(3/2), 1f+add_all/2, -9f}
		};
	float[][] table_back = {//goc duoi trai,goc duoi phai,goc tren phai,goc tren trai
		    {(-1f-add_all)/4, 3.3f, -7f},
		    {(1f+add_all)/3, 3.3f, -7f},
		    {(1f+add_all)/3, 1f+add_all/2, -7f},
		    {(-1f-add_all)/4, 1f+add_all/2, -7f}
		};
	float[][] table_roof = {//tren trai,tren phai, duoi phai,duoi trai
			  {(-1f-add_all)/4, 1f+add_all/2, -4f},
			   {(1f+add_all)/3, 1f+add_all/2,-4f},
			  {(1f+add_all)/3, 1f+add_all/2, -7f},
			  {(-1f-add_all)/4, 1f+add_all/2, -7f}
		};
	
	float[] position_teapot = {0.2f, -2.5f, -2f};
	float[] position_fireAlarm= {1f+add_all, 1f+add_all/2, -8f};
	private static Clip clipFireAlarm;	// Biến để lưu trữ clip âm thanh
	private static Clip clipTV;
	static boolean isFireALarm=false;//false(tắt)
	float speed_fireAlarm=0.02f;
	private float yaw = 0.0f;   // Góc xoay ngang (trục Y)
	private float pitch = 0.0f; // Góc xoay dọc (trục X)
	private int lastMouseX;     // Vị trí chuột X lần cuối
	private int lastMouseY;     // Vị trí chuột Y lần cuối
	static boolean hasOnTV=false;
	public RenderRoom() throws GLException {
		yaw = yaw % 360;
		if (yaw < 0) yaw += 360;
		this.addGLEventListener(this);
		this.addKeyListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		gl = arg0.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		 applyCamera(gl); 
		glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		drawAllWall( gl);
		System.out.println("eyeZ"+eyeZ);
		drawFireAlarm(gl,isFireALarm);
		drawGlobe(gl,glut,0.75f);
		drawTV(gl,hasOnTV);
		drawContainTV(gl);
		drawTable(gl);
		drawTeaPot(gl, glut, 0.4f, loadTexture("img/teapot.jpg"), position_teapot);
	
		
		
	}
	   private void applyCamera(GL2 gl2) {
	        gl2.glRotatef(-pitch, 1, 0, 0); // Xoay dọc
	        gl2.glRotatef(-yaw, 0, 1, 0);   // Xoay ngang
	        gl2.glTranslatef(-eyeX, -eyeY, -eyeZ);
	    }



	private void drawAllWall(GL2 gl) {//1
		 gl.glPushMatrix();
		draw_wall(gl,back_wall,loadTexture("img/back_wall.png"));
		draw_wall(gl,left_wall,loadTexture("img/left_wall.png"));
		draw_wall(gl,right_wall,loadTexture("img/left_wall.png"));
		draw_wall(gl,under_wall,loadTexture("img/under_wall.jpg"));
		draw_wall(gl,roof_wall,loadTexture("img/roof_wall.jpg"));
		gl.glPopMatrix();

	}
	private void drawTeaPot(GL2 gl,GLUT glut,float size,int idtexture,float[] position) {//2
		
		gl.glTranslatef(position[0], position[1], position[2]);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, idtexture);
		glut.glutSolidTeapot(size);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	
		
	}
	private void drawGlobe(GL2 gl2, GLUT glut, float radius) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, loadTexture("img/map.jpg"));
		
	    gl2.glPushMatrix();
	    gl2.glTranslatef(5f, -2.2f, -9.0f);

	    gl2.glBegin(GL2.GL_QUADS);

	    int stacks = 25;  
	    int slices = 25;  

	    for (int i = 0; i < stacks; i++) {
	        double lat0 = Math.PI * (-0.5 + (double) (i) / stacks);
	        double z0 = Math.sin(lat0);
	        double zr0 = Math.cos(lat0);

	        double lat1 = Math.PI * (-0.5 + (double) (i + 1) / stacks);
	        double z1 = Math.sin(lat1);
	        double zr1 = Math.cos(lat1);

	        for (int j = 0; j <= slices; j++) {
	            double lng = 2 * Math.PI * (double) (j - 1) / slices;
	            double x = Math.cos(lng);
	            double y = Math.sin(lng);

	            gl2.glTexCoord2d((j - 1) / (double) slices, (i) / (double) stacks); 
	            gl2.glVertex3d(x * zr0 * radius, y * zr0 * radius, z0 * radius);

	            gl2.glTexCoord2d((j - 1) / (double) slices, (i + 1) / (double) stacks);
	            gl2.glVertex3d(x * zr1 * radius, y * zr1 * radius, z1 * radius);
	        }
	    }

	    gl2.glEnd();
	    gl2.glPopMatrix();

	    // Tắt texture
	    gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
	private void drawTV(GL2 gl2,boolean hasOnTV) {
		gl2.glPushMatrix();
		 gl2.glTranslatef(4.1f, -2.4f, 0f);
		 if(hasOnTV) {
			 draw_wall(gl,TV,loadTexture("img/tivi_on.jpg"));
		 }else {
			 draw_wall(gl,TV,loadTexture("img/TV.jpg"));
		 }

		gl2.glPopMatrix();
		
		
	}
	private void drawContainTV(GL2 gl2) {
		gl2.glPushMatrix();
		 gl2.glTranslatef(2f, -7.5f, 0f);
		draw_wall(gl,contain_TV_roof,loadTexture("img/containTV_roof.png"));
		draw_wall(gl,contain_TV_roof_front,loadTexture("img/containTV_front.jpg"));
		gl2.glPopMatrix();
		
		
	}
	private void drawTable(GL2 gl2) {
		gl2.glPushMatrix();
		 gl2.glTranslatef(0f, -7.7f, 2f);
		draw_wall(gl,table_back,loadTexture("img/table_back.png"));
		draw_wall(gl,table_roof,loadTexture("img/table_back.jpg"));
		gl2.glTranslatef(-0f, 0f, 3f);
		draw_wall(gl,table_back,loadTexture("img/table_back.png"));
		gl2.glPopMatrix();
		
	}
	private void drawFireAlarm(GL2 gl2, boolean isOn) {

	    float no_mat[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	    float mat_diffuse[] = { 1f, 0f, 0f, 1.0f };  // Màu đỏ
	    float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	    float no_shininess[] = { 0.0f };
	    float high_shininess[] = { 100.0f };
	    
	    // Đèn đỏ chói khi bật và đỏ trầm khi tắt
	    float mat_emission_on[] = { 1.0f, 0.0f, 0.0f, 0.822f }; // Màu đỏ chói
	    float mat_emission_off[] = { 0.7f, 0.0f, 0.0f, 0.8f }; // Màu đỏ trầm

	    gl2.glPushMatrix();
	    gl2.glTranslatef(position_fireAlarm[0], position_fireAlarm[1], position_fireAlarm[2]);
	    
	    // Ánh sáng phát ra từ chuông
	    if (isOn) {
	        // Ánh sáng đỏ chói khi bật
	        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, mat_emission_on, 0);
	        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, high_shininess, 0);
	        // Bật ánh sáng
	        gl2.glEnable(GL2.GL_LIGHT1);
	        updateFireAlarmPosition();  // Cập nhật vị trí chuông
	    } else {
	        // Ánh sáng đỏ trầm khi tắt
	    	gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, no_shininess, 0);
	        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, mat_emission_off, 0);
	     // Tắt ánh sáng
	        gl2.glDisable(GL2.GL_LIGHT1);
	    }
	    
	    gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, no_mat, 0);
	    gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
	    gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_specular, 0);
	    
	    glut.glutSolidSphere(0.25f, 25, 25);  // Vẽ chuông báo cháy

	   
	    
	    gl2.glPopMatrix();
	}

	private void updateFireAlarmPosition() {
	    // Cập nhật vị trí và thay đổi chiều di chuyển
	    position_fireAlarm[2] += speed_fireAlarm;
	    if (position_fireAlarm[2] >= -8 || position_fireAlarm[2] <= -8) {
	        speed_fireAlarm = -speed_fireAlarm; // Đảo chiều di chuyển
	    }
	    repaint(); // Vẽ lại màn hình sau khi thay đổi vị trí
	}
	public static int loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		} catch (Exception e) {
			System.out.println("khong the load texture"+ textureFileName);
			e.printStackTrace();
		}
		int textureID = tex.getTextureObject();
		return textureID;
	}



	 private static Clip createClip(String filePath) {
	        try {
	            File soundFile = new File(filePath);
	            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
	            Clip clip = AudioSystem.getClip();
	            clip.open(audioStream);
	            return clip;
	        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 //âm thanh báo cháy
	 public static void toggleSoundFireAlarm(String soundFilePath) {
	        try {
	            if (isFireALarm) {
	            	clipFireAlarm.stop(); // Dừng nếu đang chạy
	            	clipFireAlarm.close(); // Giải phóng tài nguyên
	            	isFireALarm=false;
	              
	            } else {
	            	clipFireAlarm = createClip(soundFilePath);
	                if (clipFireAlarm != null) {
	                	clipFireAlarm.loop(Clip.LOOP_CONTINUOUSLY); // Lặp liên tục
	                	clipFireAlarm.start();
	                	isFireALarm=true;
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	 //âm thanh TV
	    public static void toggleSoundTV(String soundFilePath) {
	        try {
	            if (hasOnTV) {
	                clipTV.stop(); // Dừng nếu đang chạy
	                clipTV.close(); // Giải phóng tài nguyên
	                hasOnTV=false;
	            } else {
	            	clipTV = createClip(soundFilePath);
	                if (clipTV != null) {
	                	clipTV.loop(Clip.LOOP_CONTINUOUSLY); // Lặp liên tục
	                	clipTV.start();
	                	hasOnTV=true;
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }


	private void draw_wall(GL2 gl,float[][] vertex,int idTexture) {//vẽ hình tứ giác theo một danh sách tọa độ (float3) cho trước
		
		float[][] texture= {
				{0.0f, 0.0f},
				{1.0f, 0.0f},
				{1.0f, 1.0f},
				{0.0f, 1.0f}
		};
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, idTexture);
//		gl.glOrthof(-1f, 1f, 1f, -1f,1f,-20f);
		
		gl.glBegin(gl.GL_QUADS);
		
		for (int i=0;i<vertex.length;i++) {
			gl.glTexCoord2f(texture[i][0], texture[i][1]);
		    gl.glVertex3f(vertex[i][0], vertex[i][1], vertex[i][2]);
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL2 gl = arg0.getGL().getGL2();
		float ambient[] = { 1.0f, 0.1f, 0f, 1.0f };
		//float ambient1[] = { 0.8f, 1f, 1.0f, 1.0f };
		float ambient1[] = { 0f, 10f, 0f, 1.0f };
		float ambient2[] = { 0.3f, 1f, 1.0f, 1.0f };
		float diffuse[] = { 1.0f, 1.0f, 1.0f, 0.2f };
		float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float position[] = { 0f, 1.5f, -8.0f, 0.0f };
		float position_1[] = {1f+add_all, 1f+add_all/2, -8f, 1.0f };
		//float position_1[] = {0.5f, 0.5f, -8f, 0.0f };
		float position_2[] = {0f, 1.5f, -8f, 0.0f };

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular,0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
		
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular,0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient1, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, position_1, 0);
		
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, specular,0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, ambient2, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, position, 0);
		
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, specular,0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, position, 0);
		
		
		
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glClearColor(0.0f, 0.8f, 0.8f, 0.0f); 
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int w, int h) {
		  gl = arg0.getGL().getGL2();

		    if (h == 0) h = 1; // Ngăn chia cho 0
		    float aspect = (float) w / h;

		    // Set viewport
		    gl.glViewport(0, 0, w, h);

		    // Setup projection matrix
		    gl.glMatrixMode(GL_PROJECTION);
		    gl.glLoadIdentity();
		    glu.gluPerspective(45.0, aspect, 0.1, 100.0);

		    // Reset model-view matrix
		    gl.glMatrixMode(GL_MODELVIEW);
		    gl.glLoadIdentity();
		    glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		    repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		        float speed = 0.05f;
		        switch (e.getKeyCode()) {
		            case KeyEvent.VK_A:{
		            	yaw-=speed;
		                break;}
		            case KeyEvent.VK_D:{
		            	yaw+=speed;
		                break;}
		            case KeyEvent.VK_W:{//tiến lên trước
		            	eyeX += Math.sin(Math.toRadians(yaw)) * speed;
		            	if(eyeZ>0f) {
		                eyeZ -= Math.cos(Math.toRadians(yaw)) * speed;
		            	}
		                break;}
		            case KeyEvent.VK_S:{
		            	 eyeX -= Math.sin(Math.toRadians(yaw)) * speed;
		                 
		                 if(eyeZ<4.5f) {
		                	 eyeZ += Math.cos(Math.toRadians(yaw)) * speed;
		                 }
		                break;}
		            case KeyEvent.VK_SPACE:{ // Lên trên
		                eyeY += speed;
		                break;}
		            case KeyEvent.VK_SHIFT:{ // Xuống dưới
		                eyeY -= speed;
		                break;}
		            case KeyEvent.VK_U:{//bật /tắt chuông báo cháy
		            	toggleSoundFireAlarm("sound/firealarm.wav");
		                break;}
		            case KeyEvent.VK_T:{//bật /tắt TV
		            	toggleSoundTV("sound/doraemon.wav");
			                break;}
		            default:
		                break;
		        }
		        repaint();
		   
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseDragged(MouseEvent e) {
		int deltaX = e.getX() - lastMouseX;
        int deltaY = e.getY() - lastMouseY;

        yaw += deltaX * 0.1f;
        pitch -= deltaY * 0.1f;

        pitch = Math.max(-90, Math.min(90, pitch)); // Giới hạn góc pitch

        lastMouseX = e.getX();
        lastMouseY = e.getY();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		lastMouseX = e.getX(); // Cập nhật vị trí chuột lần đầu
        lastMouseY = e.getY();
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create the OpenGL rendering canvas
				GLJPanel panel = new RenderRoom();
				panel.setPreferredSize(new Dimension(800, 800));

				// Create a animator that drives canvas' display() at the specified FPS.
				final FPSAnimator animator = new FPSAnimator(panel, 60, true);

				// Create the top-level container
				final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
				frame.getContentPane().add(panel);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						// Use a dedicate thread to run the stop() to ensure
						// that the animator stops before program exits.
						new Thread() {
							public void run() {
								if (animator.isStarted())
									animator.stop();
								System.exit(0);
							}
						}.start();
					}
				});
				frame.setTitle("roo ");
				frame.pack();
				frame.setVisible(true);
			animator.start(); // start the animation loop
			}
		});
//		RenderRoom renderRoom = new RenderRoom();
		//playSound("sound/firealarm.wav",false);
		 
	}

	

	

}