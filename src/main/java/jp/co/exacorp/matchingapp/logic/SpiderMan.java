package jp.co.exacorp.matchingapp.logic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;

@Stateless
public class SpiderMan {

	public String createThumbnail (Map<String, Double> userPi, Map<String, Double> mlpaPi,
			String userId, String mlpaName, String resultType, String ffName, String imgDir) throws Exception {
		String swFilePath = imgDir + userId + "_" + resultType + "tmp.jpg";
		String tfFileName = userId + resultType + ".jpg";
		String tfFilePath = imgDir + tfFileName;

		try {
			deleteFile(swFilePath);
			deleteFile(tfFilePath);
			createSpiderWeb(userPi, mlpaPi, mlpaName, swFilePath);
			trapFaceBySpiderWeb(imgDir + ffName, swFilePath, tfFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tfFileName;
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	private void createSpiderWeb (Map<String, Double> userPi, Map<String, Double> mlpaPi,
			String mlpaName, String filePath) throws Exception {
		String userName = "You";

		String cat1 = "Openness";
		String cat2 = "Conscientiousness";
		String cat3 = "Extraversion";
		String cat4 = "Agreeableness";
		String cat5 = "Neuroticism";

		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(userPi.get("Openness"), userName, cat1);
		dataset.addValue(userPi.get("Conscientiousness"), userName, cat2);
		dataset.addValue(userPi.get("Extraversion"), userName, cat3);
		dataset.addValue(userPi.get("Agreeableness"), userName, cat4);
		dataset.addValue(userPi.get("Neuroticism"), userName, cat5);

		dataset.addValue(mlpaPi.get("Openness"), mlpaName, cat1);
		dataset.addValue(mlpaPi.get("Conscientiousness"), mlpaName, cat2);
		dataset.addValue(mlpaPi.get("Extraversion"), mlpaName, cat3);
		dataset.addValue(mlpaPi.get("Agreeableness"), mlpaName, cat4);
		dataset.addValue(mlpaPi.get("Neuroticism"), mlpaName, cat5);

		SpiderWebPlot plot = new SpiderWebPlot(dataset);
		plot.setMaxValue(1.00);

		JFreeChart richard = new JFreeChart("Result of match making", plot);
		File outputFile = new File(filePath);

		try {
			ChartUtilities.saveChartAsJPEG(outputFile, richard, 375, 500);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("Done." +  outputFile.getAbsolutePath());
	}

	private void trapFaceBySpiderWeb (String faceFilePath, String swFilePath,
			String tfFilePath) throws Exception {
		File file = new File (tfFilePath);
		try {
			BufferedImage choppedHead = ImageIO.read(new FileInputStream(faceFilePath));
			BufferedImage spiderWeb = ImageIO.read(new FileInputStream(swFilePath));
			BufferedImage trapedFace = new BufferedImage(755, 500, BufferedImage.TYPE_INT_RGB);
			Graphics g = trapedFace.getGraphics();
			g.drawImage(choppedHead, 0, 60, null);
			g.drawImage(spiderWeb, 380, 0, null);

			ImageIO.write(trapedFace, "jpg", file);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("Done." +  file.getAbsolutePath());

	}

}
