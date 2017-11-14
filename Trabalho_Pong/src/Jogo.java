import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.FPSAnimator;

@SuppressWarnings("serial")
public class Jogo extends GLCanvas implements GLEventListener, KeyListener {

	private GL2 gl;
	private GLU glu;
	private GLUT glut;

	// TEXTURA
	private int idTextura[];
	private Texture texture;

	float xMin, xMax, yMin, yMax, zMin, zMax;

	float limiteEsq = -90;
	float limiteInf = -90;
	float limiteDir = 90;
	float limiteSup = 90;

	float angulo = 0;

	float txP = 0;
	float txB, tyB;

	double xVel, yVel;
	boolean subindo = true;

	private static String TITULO = "Pong";
	private static final int CANVAS_LARGURA = 800;
	private static final int CANVAS_ALTURA = 600;
	private static final int FPS = 60;
	int TONALIZACAO = GL2.GL_SMOOTH;
	int inicio = 0;
	int score = 0;
	int scHP1 = 1 , scHP2 = 1 , scHP3 = 1 , scHP4 = 1 , scHP5= 1 ; // Scale das vidas ... Perder primeira vida => scHP=0

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Cria a janela de renderizacao OpenGL
				GLCanvas canvas = new Jogo();
				canvas.setPreferredSize(new Dimension(CANVAS_LARGURA, CANVAS_ALTURA));
				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
				final JFrame frame = new JFrame();

				frame.getContentPane().add(canvas);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						new Thread() {
							@Override
							public void run() {
								if (animator.isStarted())
									animator.stop();
								System.exit(0);
							}
						}.start();
					}
				});
				frame.setTitle(TITULO);
				frame.pack();
				// frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // FullScreen
				frame.setLocationRelativeTo(null); // Center frame
				frame.setVisible(true);
				animator.start(); // inicia o loop de animacao
			}
		});
	}

	/** Construtor da classe */
	public Jogo() {
		this.addGLEventListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		glu = new GLU();

		xMin = -100;
		xMax = 100;
		yMin = -100;
		yMax = 100;
		zMin = -100;
		zMax = 100;

		// txB=Math.random()*90;
		// tyB = Math.random() * 90;

		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		glut = new GLUT();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Limpa a janela de visualizacao com a cor de fundo especificada
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// Redefine a matriz atual com a matriz "identidade"
		gl.glLoadIdentity();

		// criar a cena aqui....

		// MENU
		// desenhaLogo();

		// desenhaRegras();
		// FIM MENU

		// ----------------------JOGO--------------------

		// CENARIO FASE 1
		 //iluminacaoAmbiente();
		 //ligaLuz();
		// HUD
		desenhaHUD();

		// BASTAO
		gl.glPushMatrix();
		gl.glTranslatef(txP, 0, 0);
		desenhaPlayer();
		gl.glPopMatrix();
		// BOLA
		gl.glPushMatrix();
		lancaBola();
		desenhaBola();
		gl.glPopMatrix();

		// CENARIO FASE 2 - ADD Objeto Centro Da tela
		// Quadrado
		// gl.QUADS
		// ----------------------------------------------

		// Executa os comandos OpenGL
		gl.glFlush();
	}

	/**
	 * Chamado quando o contexto OpenGL eh destruido
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;

		case KeyEvent.VK_ENTER:
			inicio = 1;
			break;

		case KeyEvent.VK_LEFT:
			updateLeft();
			break;

		case KeyEvent.VK_RIGHT:

			updateRight();
			break;

		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	// ---------------------

	// Interface

	public void desenhaLogo() {
		gl.glPushMatrix();

		gl.glScalef(.5f, .5f, .5f);
		gl.glTranslatef(-30, 90, 0);

		// P
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-50, 95);
		gl.glVertex2f(-50, 35);
		gl.glVertex2f(-45, 35);
		gl.glVertex2f(-45, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-50, 95);
		gl.glVertex2f(-50, 90);
		gl.glVertex2f(-25, 90);
		gl.glVertex2f(-25, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-50, 65);
		gl.glVertex2f(-50, 60);
		gl.glVertex2f(-25, 60);
		gl.glVertex2f(-25, 65);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-30, 95);
		gl.glVertex2f(-30, 65);
		gl.glVertex2f(-25, 65);
		gl.glVertex2f(-25, 95);
		gl.glEnd();

		// O
		gl.glBegin(GL2.GL_QUADS); // h
		gl.glVertex2f(-10, 95);
		gl.glVertex2f(-10, 90);
		gl.glVertex2f(10, 90);
		gl.glVertex2f(10, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// h
		gl.glVertex2f(-10, 40);
		gl.glVertex2f(-10, 35);
		gl.glVertex2f(10, 35);
		gl.glVertex2f(10, 40);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(-10, 35);
		gl.glVertex2f(-07, 35);
		gl.glVertex2f(-07, 95);
		gl.glVertex2f(-10, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(10, 35);
		gl.glVertex2f(13, 35);
		gl.glVertex2f(13, 95);
		gl.glVertex2f(10, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// Ponto1
		gl.glVertex2f(-5, 60);
		gl.glVertex2f(8, 60);
		gl.glVertex2f(8, 63);
		gl.glVertex2f(-5, 63);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// Ponto2
		gl.glVertex2f(0, 70);
		gl.glVertex2f(2, 70);
		gl.glVertex2f(2, 73);
		gl.glVertex2f(0, 73);

		gl.glEnd();

		// N
		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(25, 95);
		gl.glVertex2f(25, 35);
		gl.glVertex2f(30, 35);
		gl.glVertex2f(30, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(45, 95);
		gl.glVertex2f(45, 35);
		gl.glVertex2f(50, 35);
		gl.glVertex2f(50, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// d
		gl.glVertex2f(25, 95);
		gl.glVertex2f(45, 35);
		gl.glVertex2f(50, 35);
		gl.glVertex2f(30, 95);
		gl.glEnd();

		// G

		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(65, 95);
		gl.glVertex2f(65, 35);
		gl.glVertex2f(70, 35);
		gl.glVertex2f(70, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);// v
		gl.glVertex2f(85, 65);
		gl.glVertex2f(85, 35);
		gl.glVertex2f(90, 35);
		gl.glVertex2f(90, 65);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS); // h
		gl.glVertex2f(65, 95);
		gl.glVertex2f(65, 90);
		gl.glVertex2f(90, 90);
		gl.glVertex2f(90, 95);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS); // h
		gl.glVertex2f(75, 65);
		gl.glVertex2f(75, 70);
		gl.glVertex2f(90, 70);
		gl.glVertex2f(90, 65);
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS); // h
		gl.glVertex2f(65, 40);
		gl.glVertex2f(65, 35);
		gl.glVertex2f(90, 35);
		gl.glVertex2f(90, 40);
		gl.glEnd();

		gl.glPopMatrix();

	}

	public void desenhaRegras() {

		gl.glRasterPos2f(-50, 30);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24,
				"A cada rebatida você ganha 20 pontos!!! " + "  Mas você possui apenas 5 vidas... CUIDADO!");

		gl.glRasterPos2f(-90, -40);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "SETAS - Movem o bloco");

		gl.glRasterPos2f(-90, -60);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "ESC - Pausa o jogo");

		gl.glRasterPos2f(-90, -80);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "F10 - Fecha o jogo");

		gl.glRasterPos2f(-27, -10);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "PRESSIONE ENTER PARA INICIAR !!!");

		gl.glBegin(GL2.GL_LINES); // Destaque
		gl.glVertex2f(-27, -15);
		gl.glVertex2f(25, -15);
		gl.glEnd();

	}

	public void desenhaHUD() {

		// HP
		gl.glPushMatrix();
		
		gl.glTranslatef(60, 90, 0);
		gl.glPushMatrix();
		gl.glScaled(scHP1, scHP1, scHP1);
		glut.glutSolidSphere(1, 200, 200);
		gl.glPopMatrix();
		
		gl.glTranslatef(5, 0, 0);
		gl.glPushMatrix();
		gl.glScaled(scHP2, scHP2, scHP2);
		glut.glutSolidSphere(1, 200, 200);
		gl.glPopMatrix();
		
		gl.glTranslatef(5, 0, 0);
		gl.glPushMatrix();
		gl.glScaled(scHP3, scHP3, scHP3);
		glut.glutSolidSphere(1, 200, 200);
		gl.glPopMatrix();
		
		gl.glTranslatef(5, 0, 0);
		gl.glPushMatrix();
		gl.glScaled(scHP4, scHP4, scHP4);
		glut.glutSolidSphere(1, 200, 200);
		gl.glPopMatrix();
		
		gl.glTranslatef(5, 0, 0);		
		gl.glPushMatrix();
		gl.glScaled(scHP5, scHP5, scHP5);
		glut.glutSolidSphere(1, 200, 200);
		gl.glPopMatrix();		
		
		
		gl.glPopMatrix();

		// SCORE
		gl.glPushMatrix();
		gl.glColor3f(1, 1, 1);
		gl.glRasterPos2f(58, 80);
		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "SCORE: " + score);
		gl.glPopMatrix();
	}

	public void iluminacaoAmbiente() {
		float luzAmbiente[] = { 0.4f, 0.0f, 0.0f, 1.0f };

		float posicaoLuz[] = { 40.0f, 0.0f, 0.0f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);
	}

	public void ligaLuz() {
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glShadeModel(TONALIZACAO);
	}

	public void limpaTela() {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		System.out.println(inicio);
	}

	

	// Objetos de cena

	// Bastao
	public void desenhaPlayer() {
		gl.glPushMatrix();
		// gl.glTranslatef(arg0, arg1, arg2);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-10, -95);
		gl.glVertex2f(10, -95);
		gl.glVertex2f(10, -90);
		gl.glVertex2f(-10, -90);
		gl.glEnd();
		gl.glPopMatrix();
	}

	public void desenhaBola() {
		glut.glutSolidSphere(2, 200, 200);
	}

	public void desenhaObstaculo() {
		gl.glPushMatrix();
		gl.glColor3f(1, 1, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-2, -2);
		gl.glVertex2f(0, -2);
		gl.glVertex2f(0, 2);
		gl.glVertex2f(-2, 2);
		gl.glEnd();
		gl.glPopMatrix();
	}

	

	// Movimentacao
	
	// Mov Player

	public void updateRight() {
		if (txP < limiteDir - 10)
			txP += 3;
		// System.out.println(txP);
	}

	public void updateLeft() {
		if (txP > limiteEsq + 10)
			txP -= 3;
	}

	// Fim Mov Player

	// Mov Bola
	
	public void lancaBola() {
		if (txB > limiteEsq - 2 && txB < limiteDir - 2 && tyB > limiteInf - 2 && tyB < limiteSup - 2 && subindo) {
			txB = txB + 6;
			tyB = tyB + 6;

			movimentoY(tyB, subindo);
			System.out.println(tyB); // tyB travado em 89
			//colisaoX(txB, limiteDir, limiteEsq);

		} else {
			subindo = false;
			movimentoY(tyB, subindo);
			// System.out.println(subindo);
			//colisaoX(txB, limiteDir, limiteEsq);
		}

	}

	public void movimentoY(float tyB, boolean subindo) {
		if (subindo) {
			gl.glTranslatef(0, tyB, 0);
			tyB++;
		} else {
			gl.glTranslatef(0, tyB, 0);
			tyB--;
			System.out.println(tyB);

		}
	}

	public void colisaoX(float txB, float limiteDir, float limiteEsq) {
		if (txB == limiteDir) {
			if (txB > -txB) {
				gl.glTranslatef(txB, 0, 0);
				txB--;
			}

		} else {
			if (txB == limiteEsq) {
				if (-txB < txB) {
					gl.glTranslatef(txB, 0, 0);
					txB++;
				}
			}
		}

	}

}

// ----------- TEXTURA ------------

