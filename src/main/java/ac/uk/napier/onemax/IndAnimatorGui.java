package ac.uk.napier.onemax;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.icafe4j.image.gif.GIFTweaker;
import com.icafe4j.image.writer.GIFWriter;


/**
 * 
 * @author 40004938
 *
 */
public class IndAnimatorGui extends JFrame implements Observer{

	private JPanel contentPane;
	Individual ind = null;
	static int factor = 1;
	static final int everyNPic = 1;
	int delayStart = 0;//20
	int delayTheRest = 0;//10
	boolean outputGif = true;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IndAnimatorGui frame = new IndAnimatorGui();
					frame.setVisible(true);					
					frame.contentPane.repaint();								
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IndAnimatorGui() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 800);
		contentPane = new JPanel(){
			@Override
			public void paint(Graphics g) {							
				super.paint(g);
				
				if(ind != null){
					//clear and draw
					ind.draw(g);																					
				}
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				PngLoader loader = new PngLoader(IndAnimatorGui.this);
				Thread t = new Thread(loader);
				t.start();
			}			
		});
	}

	static int pngSeqNo = 0;
	
	public static void ouputImg(Individual ind, int factor) {				
		BufferedImage img = new BufferedImage(ind.getWidth() * factor, ind.getHeight() * factor, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		ind.draw(g);
		pngSeqNo ++;		
		try {						
			String filename = "" + NumberFormat.formatNumber(pngSeqNo, 4) + ".png";											
			File outputfile = new File(filename);
			ImageIO.write(img, "png", outputfile);
			System.out.println(filename + " output");
		} catch (IOException e) {

		}
	}		

	
	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof Individual){
			ind = (Individual)arg1;
		}
		contentPane.repaint();		
	}	
	
	public static void outputGif() {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("1.gif");
			File[] files = FileGetter.getFiles("", "", ".png");
	        BufferedImage[] images = new BufferedImage[files.length];
	        int[] delays = new int[images.length];

	        for(int i = 0; i < files.length; i++) {
	            FileInputStream fin = new FileInputStream(files[i]);
	            BufferedImage image = javax.imageio.ImageIO.read(fin);
	            images[i] = image;
	            delays[i] = 0;
	            fin.close();
	        }

	        GIFWriter writer = new GIFWriter();
	        
	        writer.setLoopCount(1);
	        writer.writeAnimatedGIF(images, delays, fout);
//	        GIFTweaker.writeAnimatedGIF(images, delays, fout);
	        fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
	}

	class PngLoader extends Observable implements Runnable {
		Individual ind = null;
		String[] filenames = null;
		int currentIdx = -1;		
		boolean running = false;
		
		public PngLoader(Observer observer) {
			super();
			this.addObserver(observer);			
		}

		@Override
		public void run() {
			running = true;			
			while(running){
				loadIndividual();			
				setChanged();
				notifyObservers(ind);
				if(outputGif){
					ouputImg(ind, factor);
				}
			}
			if(outputGif){
				outputGif();
			}
		}

		private void loadIndividual() {
			if(filenames == null){
				filenames = FileGetter.getFileNames("", "", ".ind");
			}
			if(filenames == null || filenames.length == 0){
				return;
			}
			currentIdx+=everyNPic;
			if(currentIdx < 50){
				try {
					Thread.sleep(delayStart);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					Thread.sleep(delayTheRest);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(currentIdx >= filenames.length){
//				currentIdx =  0;
				currentIdx = filenames.length - 1;
				running = false;				
				return;
			}
			ind = Serialize.load(filenames[currentIdx]);
			
			System.out.println("" + filenames[currentIdx] + " loaded");				
						
		}	
	}
}
