package ac.uk.napier.onemax;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class Individual implements Serializable {

	/**
	 * Remove all the transient and leave in parameters Serialize load should set
	 * parameters if needed i.e. when seeding But isnt needed for ind animator
	 * Probably need threshold and threshhold value in here Get rid of singleton
	 * just complicated things All parameters just public static
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient static Parameters parameters;
	private transient static int width;
	private transient static int height;
	private transient static BufferedImage image;
	private transient static Random rnd;

	private String imageFilename;
	private boolean[][] chromosome;
	private int fitness;
	private static int evaluations = 0;

	public Individual() {
		parameters = Parameters.getInstance();
		image = parameters.getImage();
		width = parameters.getWidth();
		height = parameters.getHeight();
		rnd = parameters.getRandom();
		imageFilename = parameters.getImageFilename();
		chromosome = new boolean[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
//        		chromosome[y][x] = true;
				if (rnd.nextBoolean()) {
					chromosome[y][x] = true;
				} else {

				}
			}
		}
		evaluateFitness();
	}

	public int evaluateFitness() {
		evaluations++;
		fitness = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (chromosome[y][x] && image.getRGB(x, y) == Color.WHITE.getRGB()) {
					fitness++;
				} else if (!chromosome[y][x] && image.getRGB(x, y) == Color.BLACK.getRGB()) {
					fitness++;
				}
			}
		}
		return fitness;
	}

	public void draw(Graphics g) {
		// doesnt use width and height so that ind animator doesnt need to load images
		// in serialize
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, chromosome[0].length, chromosome.length);
		g.setColor(Color.WHITE);
		for (int y = 0; y < chromosome.length; y++) {
			for (int x = 0; x < chromosome[y].length; x++) {
				if (chromosome[y][x]) {
					g.fillRect(x, y, 1, 1);
				}
			}
		}
	}

	public Individual copy() {
		Individual individual = new Individual();
		individual.fitness = this.fitness;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				individual.chromosome[y][x] = chromosome[y][x];
			}
		}
		return individual;
	}

	public static Individual getOptimal() {
		Individual ind = new Individual();
		for (int y = 0; y < ind.height; y++) {
			for (int x = 0; x < ind.width; x++) {
				if (ind.image.getRGB(x, y) == Color.WHITE.getRGB()) {
					ind.chromosome[y][x] = true;
				} else {
					ind.chromosome[y][x] = false;
				}
			}
		}
		ind.evaluateFitness();
		return ind;
	}

	public BufferedImage getImageFromChromosome() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (chromosome[y][x]) {
					image.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					image.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		return image;
	}

	public String getAsciiFromChromosome() {
		StringBuilder sb = new StringBuilder();
		String c = "#";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (image.getRGB(x, y) == Color.WHITE.getRGB()) {
					sb.append(" ");
				} else if (image.getRGB(x, y) == Color.BLACK.getRGB()) {
					sb.append(c);
				} else {
					System.err.println("Color not allowed " + image.getRGB(x, y) + " Only black " + Color.BLACK.getRGB()
							+ " and white " + Color.WHITE.getRGB() + " allowed");
				}

			}
			sb.append("\r\n");
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("ascii.txt")));
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public int getFitness() {
		return fitness;
	}

	public boolean[][] getChromosome() {
		return chromosome;
	}

	public static int getHeight() {
		return height;
	}

	public static int getWidth() {
		return width;
	}

	public static int getEvaluations() {
		return evaluations;
	}

	public void resetTransientFields() {
		parameters = Parameters.getInstance();
		parameters.setImageFilename(imageFilename);
		image = parameters.getImage();
		width = parameters.getWidth();
		height = parameters.getHeight();
		rnd = parameters.getRandom();
	}

	public static void setEvaluations(int i) {
		evaluations = i;
		
	}

}
