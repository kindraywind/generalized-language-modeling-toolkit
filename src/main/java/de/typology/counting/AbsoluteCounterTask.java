package de.typology.counting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class AbsoluteCounterTask implements Runnable {

    private InputStream input;

    private OutputStream output;

    private String delimiter;

    public AbsoluteCounterTask(
            InputStream input,
            OutputStream output,
            String delimiter) {
        this.input = input;
        this.output = output;
        this.delimiter = delimiter;
    }

    @Override
    public void run() {
        try {
            Map<String, Integer> sequenceCounts =
                    new HashMap<String, Integer>();

            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input),
                            100 * 1024 * 1024)) {
                String sequence;
                while ((sequence = reader.readLine()) != null) {
                    Integer count = sequenceCounts.get(sequence);
                    sequenceCounts.put(sequence, count == null ? 1 : count + 1);
                }
            }

            try (BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(output),
                            100 * 1024 * 1024)) {
                for (Map.Entry<String, Integer> sequenceCount : sequenceCounts
                        .entrySet()) {
                    String sequence = sequenceCount.getKey();
                    Integer counter = sequenceCount.getValue();
                    writer.write(sequence + delimiter + counter + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
