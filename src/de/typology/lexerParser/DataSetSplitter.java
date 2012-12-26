package de.typology.lexerParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import de.typology.utils.Config;
import de.typology.utils.IOHelper;

/**
 * This class splits and samples a given input file into trainings and test data
 * 
 * The threasholds can be configured in config.txt the relevant fields are
 * 
 * splitDataRatio
 * 
 * smpleRate
 * 
 * nGramLength
 * 
 * @author rpickhardt
 * 
 */

public class DataSetSplitter {

	private BufferedWriter testDataWriter;
	private BufferedWriter trainingDataWriter;

	public DataSetSplitter() {
		this.testDataWriter = IOHelper.openWriteFile(Config.get().testingPath,
				Config.get().memoryLimitForWritingFiles);
		this.trainingDataWriter = IOHelper.openWriteFile(
				Config.get().trainingPath,
				Config.get().memoryLimitForWritingFiles);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSetSplitter dss = new DataSetSplitter();
		BufferedReader br = IOHelper
				.openReadFile(Config.get().parsedWikiOutputPath);
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				for (int i = 0; i < tokens.length; i = i
						+ Config.get().nGramLength) {
					int rand = (int) (Math.random() * 100);
					if (rand > Config.get().sampleRate) {// keep data
						rand = (int) (Math.random() * 100);
						if (rand > Config.get().splitDataRatio) {
							dss.appendTestData(tokens, i);
						} else {
							dss.appendTrainingData(tokens, i);
						}
					} else {// write a mark that data was taken away?
						dss.addlineBreak();
					}
				}
				dss.addlineBreak();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dss.close();
		}

	}

	private void close() {
		try {
			if (this.trainingDataWriter != null) {
				this.trainingDataWriter.close();
			}
			if (this.testDataWriter != null) {
				this.testDataWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addlineBreak() throws IOException {
		this.trainingDataWriter.write("\n");
	}

	private void appendTrainingData(String[] tokens, int i) throws IOException {
		for (int j = i; j < Math.min(i + Config.get().nGramLength,
				tokens.length); j++) {
			this.trainingDataWriter.write(tokens[j] + " ");
		}

	}

	private void appendTestData(String[] tokens, int i) throws IOException {
		for (int j = i; j < Math.min(i + Config.get().nGramLength,
				tokens.length); j++) {
			this.testDataWriter.write(tokens[j] + " ");
		}
		this.testDataWriter.write("\n");
	}

}
