package ac.uk.napier.onemax;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Random;

import javax.imageio.ImageIO;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import static marvinplugins.MarvinPluginCollection.*;

public class Parameters {

	private static Random rnd = new Random();

	/**
	 * Image parameters
	 */
	private static int width;
	private static int height;
	private static BufferedImage image = null;
	private static String imageFilename = "./pics/me.jpg";
	private static Parameters parameters = null;
	public static boolean threshold = false;
	public static int thresholdValue = 100;
	public static int imageSize = 100;

	public static Class algorithmClass = EA.class;

	/*
	 * EA parameters
	 */
	int popSize = 1000;
	int tournamentSize = 4;
	double crossoverProbability = 0.9;// 0.99
	double mutationProbability = 0.01;// 0.1

	// mutationRate is set on image load and image resize to mutationFactor / (width
	// * height)
	// A mutationFactor of 1 gives a probability of flipping 1 pixel per mutation
	public int mutationFactor = 1;
	public double mutationRate = 1;

	public static void main(String[] args) {
		parameters = new Parameters();
	}

	private Parameters() {
		image = getImage();
	}

	public void setImage(BufferedImage image) {
		Parameters.image = image;
	}

	public BufferedImage getImage() {
		if (image == null) {
			try {
				BufferedImage img = ImageIO.read(new File(imageFilename));
				width = img.getWidth();
				height = img.getHeight();
				width = (int) (width * new Double(imageSize) / 100);
				height = (int) (height * new Double(imageSize) / 100);

				BufferedImage resized = new BufferedImage(width, height, img.getType());
				Graphics2D g = resized.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth(), img.getHeight(), null);
				g.dispose();
				ImageIO.write(resized, "png", new File("temp.png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			MarvinImage original = MarvinImageIO.loadImage("temp.png");
			MarvinImage output = original.clone();
			if (!threshold) {
				halftoneErrorDiffusion(original, output);
				MarvinImageIO.saveImage(output, "halftone.png");
			} else {
				thresholding(original, output, thresholdValue);
				MarvinImageIO.saveImage(output, "threshold.png");
			}

			image = output.getBufferedImage();

//			
//			image = resized;		

			mutationRate = new Double(mutationFactor) / new Double(width * height);
			System.out.println(mutationRate);

			Individual ind = Individual.getOptimal();
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("ascii.txt")));
				writer.write(ind.getAsciiFromChromosome());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return image;
	}

	public static Parameters getInstance() {
		if (parameters == null) {
			parameters = new Parameters();
		}
		return parameters;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getImageFilename() {
		return imageFilename;
	}

	public Random getRandom() {
		return rnd;
	}

	public void setImageFilename(String imageFilename) {
		Parameters.imageFilename = imageFilename;
		image = null;
		image = getImage();
	}
}
