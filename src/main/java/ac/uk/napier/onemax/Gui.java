package ac.uk.napier.onemax;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.JLabel;
import java.awt.SystemColor;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends JFrame implements Observer {

	private JPanel contentPane;
	private JPanel displayPanel;
	private JButton runButton;
	private Individual individual;
	private Algorithm alg;
	private Thread thread;
	protected BufferedImage image = null;
	protected boolean showImage = false;
	protected int imageTransparancy = 0;
	private JSlider sliderSize;
	private JCheckBox chckbxThreshold;
	private JSlider sliderThreshold;
	private JLabel lblFitness;
	private JLabel lblOptimal;
	private JLabel lblEvaluations;
	public static int optimal = 0;
	MyChartPanel myChart = null;

	/**
	 * Launch the applications.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(Color.GRAY);
		controlPanel.setPreferredSize(new Dimension(150, 1000));
		contentPane.add(controlPanel, BorderLayout.WEST);
		controlPanel.setLayout(null);

		runButton = new JButton("Start");
		runButton.setBackground(Color.GREEN);
		runButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				if (alg == null || !alg.isRunning()) {
//					int response = JOptionPane.showConfirmDialog(null, "seed with last ind?");
//					if(response == JOptionPane.YES_OPTION) {
//						Algorithm.seed = true;
//					}else {
//						Algorithm.seed = false;
//						Individual.setEvaluations(0);
//					}
					Individual.setEvaluations(0);
					try {
						alg = (Algorithm) Parameters.algorithmClass.newInstance();
						System.out.println("Starting with " + Parameters.algorithmClass );
					} catch (InstantiationException e) {						
						e.printStackTrace();
					} catch (IllegalAccessException e) {						
						e.printStackTrace();
					}					
					alg.addObserver(Gui.this);
					thread = new Thread(alg);
					thread.start();
					runButton.setBackground(Color.red);
					runButton.setText("Stop");
					sliderSize.setEnabled(false);
					chckbxThreshold.setEnabled(false);
					sliderThreshold.setEnabled(false);
					if(myChart != null) {
						myChart.dispose();
					}
					myChart = new MyChartPanel();
					myChart.setVisible(true);
					
					
				} else {
					alg.stop();
					runButton.setBackground(Color.green);
					runButton.setText("Run");
					sliderSize.setEnabled(true);
					chckbxThreshold.setEnabled(true);
					sliderThreshold.setEnabled(true);					
				}
				
			}
		});
		runButton.setBounds(10, 21, 130, 23);
		controlPanel.add(runButton);

		JButton loadBtn = new JButton("Load");
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (alg != null && alg.isRunning()) {
					JOptionPane.showMessageDialog(null, "Stop The current Algorithm First");
					return;
				}
				JFileChooser fc = new JFileChooser("./pics/");
				int choice = fc.showOpenDialog(null);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					Parameters.getInstance().setImageFilename(f.getAbsolutePath());
					individual = Individual.getOptimal();
					optimal = individual.getFitness();
					displayPanel.setPreferredSize(
							new Dimension(Parameters.getInstance().getWidth(), Parameters.getInstance().getHeight()));
					repaint();
				}
			}
		});
		loadBtn.setBounds(10, 50, 130, 23);
		controlPanel.add(loadBtn);

		JButton btnSaveSvg = new JButton("Save SVG");
		btnSaveSvg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int answer = JOptionPane.showConfirmDialog(null,
							"Warning A 1000 X 563 Image will create a 40MB SVG and Take a While. Are You Sure?");
					if (answer == JOptionPane.YES_OPTION) {
						SVG.save(displayPanel);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSaveSvg.setBounds(26, 326, 89, 23);
		controlPanel.add(btnSaveSvg);

		sliderThreshold = new JSlider();
		sliderThreshold.setEnabled(false);
		sliderThreshold.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Parameters.thresholdValue = sliderThreshold.getValue();
				Parameters.getInstance().setImageFilename(Parameters.getInstance().getImageFilename());
				individual = Individual.getOptimal();
				optimal = individual.getFitness();
				repaint();
			}
		});
		sliderThreshold.setMaximum(255);
		sliderThreshold.setBounds(10, 188, 130, 26);
		controlPanel.add(sliderThreshold);

		chckbxThreshold = new JCheckBox("Threshold / Halftone");
		chckbxThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (alg != null && alg.isRunning()) {
					JOptionPane.showMessageDialog(null, "Stop The current Algorithm First");
					return;
				}
				if (chckbxThreshold.isSelected()) {
					Parameters.threshold = true;
					Parameters.thresholdValue = sliderThreshold.getValue();
					sliderThreshold.setEnabled(true);
				} else {
					Parameters.threshold = false;
					sliderThreshold.setEnabled(false);
				}
				Parameters.getInstance().setImageFilename(Parameters.getInstance().getImageFilename());
				individual = Individual.getOptimal();
				optimal = individual.getFitness();
				repaint();
			}
		});
		chckbxThreshold.setBounds(10, 158, 130, 23);
		controlPanel.add(chckbxThreshold);

		
		JSlider slider_1 = new JSlider();
		slider_1.setEnabled(false);
		slider_1.setMaximum(255);
		
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(slider_1.isEnabled()) {
					
				}
				if (!slider_1.getValueIsAdjusting()) {
					Gui.this.imageTransparancy = slider_1.getValue();
					Gui.this.image = null;
					Gui.this.loadImage();
					System.out.println("state changed " + Gui.this.imageTransparancy);
					Gui.this.repaint();
//					displayPanel.repaint();
				}
			}

		});
		slider_1.setBounds(10, 273, 130, 26);
		controlPanel.add(slider_1);

		JCheckBox chckbxOriginal = new JCheckBox("Original");
		chckbxOriginal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Gui.this.showImage = chckbxOriginal.isSelected();
				slider_1.setEnabled(chckbxOriginal.isSelected());
				Gui.this.repaint();
			}
		});
		chckbxOriginal.setBounds(26, 235, 97, 23);
		controlPanel.add(chckbxOriginal);
		
		
		JLabel lblSize = new JLabel("Size", SwingConstants.CENTER);
		lblSize.setBackground(new Color(240, 240, 240));
		lblSize.setOpaque(true);
		lblSize.setBounds(10, 84, 130, 14);
		controlPanel.add(lblSize);
		
		
		sliderSize = new JSlider();
		sliderSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblSize.setText("Size: " + sliderSize.getValue());
			}
		});
		sliderSize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Parameters.getInstance().setImage(null);
				Parameters.imageSize = sliderSize.getValue();
				Parameters.getInstance().getImage();
				individual = Individual.getOptimal();
				optimal = individual.getFitness();
				Gui.this.repaint();
			}
		});
		
		sliderSize.setMaximum(100);
		sliderSize.setMinimum(1);
		sliderSize.setValue(100);
		sliderSize.setEnabled(true);
		sliderSize.setBounds(10, 109, 130, 26);
		controlPanel.add(sliderSize);
		
		
		
		lblFitness = new JLabel("Fitness: ");
		lblFitness.setBounds(10, 392, 130, 23);
		lblFitness.setBackground(new Color(240, 240, 240));
		lblFitness.setOpaque(true);
		controlPanel.add(lblFitness);
		
		lblOptimal = new JLabel("Optimal: ");
		lblOptimal.setOpaque(true);
		lblOptimal.setBackground(SystemColor.menu);
		lblOptimal.setBounds(10, 425, 130, 23);
		controlPanel.add(lblOptimal);
		
		lblEvaluations = new JLabel("Evaluations: ");
		lblEvaluations.setOpaque(true);
		lblEvaluations.setBackground(SystemColor.menu);
		lblEvaluations.setBounds(10, 459, 130, 23);
		controlPanel.add(lblEvaluations);
		
		Object[] items = new Object[2];
		items[0] = "EA";
		items[1] = "HillClimber";
		JComboBox comboBox = new JComboBox(items);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(comboBox.getSelectedItem());
				try {
					String name = "ac.uk.napier.onemax." + (String) comboBox.getSelectedItem();
					Parameters.getInstance().algorithmClass = Class.forName(name);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		comboBox.setBounds(10, 522, 130, 20);
		controlPanel.add(comboBox);
		
		JLabel lblAlgorithm = new JLabel("Algorithm", SwingConstants.CENTER);
		lblAlgorithm.setOpaque(true);
		lblAlgorithm.setBackground(SystemColor.menu);
		lblAlgorithm.setBounds(10, 509, 130, 14);
		controlPanel.add(lblAlgorithm);

		
		displayPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
//				super.paint(g);
				if (individual != null) {
					individual.draw(g);
				}
//				Individual ind = Individual.getOptimal();
//				optimal = individual.getFitness();
//				ind.draw(g);
//				g.drawImage(ind.getImageFromChromosome(),0,0, null);
//				g.drawImage(Parameters.getInstance().getImage(), 0, 0, null);

				if (Gui.this.image != null && Gui.this.showImage) {
					g.drawImage(Gui.this.image, 0, 0, Gui.this.image.getWidth(), Gui.this.image.getHeight(), null);
				}
			}

		};
	
		displayPanel.setPreferredSize(
				new Dimension(Parameters.getInstance().getWidth(), Parameters.getInstance().getHeight()));
		contentPane.add(displayPanel, BorderLayout.CENTER);
		individual = Individual.getOptimal();
		optimal = individual.getFitness();
		repaint();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Individual) {
			individual = (Individual) arg;
			lblFitness.setText("Fitness: " + individual.getFitness());
			lblOptimal.setText("Optimal: " + optimal);
			lblEvaluations.setText("Evaluations: " + Individual.getEvaluations());
			if(myChart != null) {
				myChart.addDataPoint((Individual) individual, alg);
			}						
			
			repaint();
		}
		if (o instanceof Algorithm) {
			if (!((Algorithm) o).isRunning()) {
				runButton.setBackground(Color.green);
				runButton.setText("Run");
			}
		}
	}
	
	protected void loadImage() {
		Parameters parameters = Parameters.getInstance();
		Gui.this.image = new BufferedImage(parameters.getWidth(), parameters.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = Gui.this.image.createGraphics();
		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g.setRenderingHints(renderHints);
		BufferedImage image = null;
		image = parameters.getImage();
		for (int x = 0; x < parameters.getWidth(); x++) {
			for (int y = 0; y < parameters.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				int al = (rgb >> 24) & 0xFF;
				int bl = (rgb) & 0xFF;// * a / 255;
				int gr = (rgb >> 8) & 0xFF;// * a / 255;
				int re = (rgb >> 16) & 0xFF;// * a / 255;

				g.setColor(new Color(re, gr, bl, imageTransparancy));
//				g.fillOval(x, y, 1, 1);
				g.fillRect(x, y, 1, 1);
			}
		}
		// g.drawImage(image, 0, 0, parameters.size, parameters.size, null);
		g.dispose();
	}
	public JSlider getSliderSize() {
		return sliderSize;
	}
	public JCheckBox getChckbxThreshold() {
		return chckbxThreshold;
	}
	public JSlider getSliderThreshold() {
		return sliderThreshold;
	}
	public JLabel getLblFitness() {
		return lblFitness;
	}
	public JLabel getLblOptimal() {
		return lblOptimal;
	}
	public JLabel getLblEvaluations() {
		return lblEvaluations;
	}
}
