package de.glmtk.smoothing.helper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.glmtk.legacy.counting.AbsoluteCounter;
import de.glmtk.legacy.counting.ContinuationCounter;
import de.glmtk.legacy.indexing.Index;
import de.glmtk.legacy.indexing.IndexBuilder;
import de.glmtk.legacy.sequencing.Sequencer;
import de.glmtk.pattern.Pattern;
import de.glmtk.pattern.PatternElem;
import de.glmtk.smoothing.Corpus;
import de.glmtk.utils.StringUtils;

public abstract class TestCorpus {

    // TODO: incorporate Corpus#getWords()

    protected static Path resourcesDir = Paths.get("src/test/resources");

    protected Path workingDir;

    protected Index index;

    protected Path absoluteDir;

    protected Path continuationDir;

    public TestCorpus(
            Path trainingFile,
            Path workingDir) throws IOException, InterruptedException {
        this.workingDir = workingDir;

        if (!Files.exists(workingDir)) {
            Files.createDirectory(workingDir);
        }

        Path indexFile = workingDir.resolve("index.txt");
        Path sequencesDir = workingDir.resolve("sequences");
        absoluteDir = workingDir.resolve("absolute");
        continuationDir = workingDir.resolve("continuation");

        // index
        if (!Files.exists(indexFile)) {
            try (InputStream input = Files.newInputStream(trainingFile);
                    OutputStream output = Files.newOutputStream(indexFile)) {
                IndexBuilder indexBuilder = new IndexBuilder(false, false, 5);
                indexBuilder.buildIndex(input, output, 1, 1);
            }
        }
        try (InputStream input = Files.newInputStream(indexFile)) {
            index = new Index(input);
        }

        // sequences
        if (!Files.exists(sequencesDir)) {
            Sequencer sequencer =
                    new Sequencer(trainingFile, sequencesDir, index, 1, false,
                            false);
            sequencer.sequence(Pattern.getCombinations(5,
                    Arrays.asList(PatternElem.CNT, PatternElem.SKP)));
        }

        // absolute
        if (!Files.exists(absoluteDir)) {
            AbsoluteCounter absoluteCounter =
                    new AbsoluteCounter(sequencesDir, absoluteDir, "\t", 1,
                            false, true);
            absoluteCounter.count();
        }

        // continuation
        if (!Files.exists(continuationDir)) {
            ContinuationCounter continuationCounter =
                    new ContinuationCounter(absoluteDir, continuationDir,
                            index, "\t", 1, false, true);
            continuationCounter.count();
        }
    }

    public abstract String[] getWords();

    public List<String> getSequenceList(int n, int length) {
        List<String> result = new LinkedList<String>();
        for (int k = 0; k != length; ++k) {
            result.add(getWords()[n % getWords().length]);
            n /= getWords().length;
        }
        Collections.reverse(result);
        return result;
    }

    public String getSequenceString(int n, int length) {
        return StringUtils.join(getSequenceList(n, length), " ");
    }

    public Path getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(Path workingDir) {
        this.workingDir = workingDir;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public Path getAbsoluteDir() {
        return absoluteDir;
    }

    public void setAbsoluteDir(Path absoluteDir) {
        this.absoluteDir = absoluteDir;
    }

    public Path getContinuationDir() {
        return continuationDir;
    }

    public void setContinuationDir(Path continuationDir) {
        this.continuationDir = continuationDir;
    }

    public Corpus getCorpus() throws IOException {
        return new Corpus(getAbsoluteDir(), getContinuationDir());
    }

    public Path getSequencesTestingSample(int length) throws IOException {
        Path sequencesTestSample =
                workingDir.resolve("sequences-testing-samples-" + length);
        if (!Files.exists(sequencesTestSample)) {
            try (BufferedWriter writer =
                    Files.newBufferedWriter(sequencesTestSample,
                            Charset.defaultCharset())) {
                for (int i = 0; i != ((int) Math.pow(getWords().length, length)); ++i) {
                    writer.write(getSequenceString(i, length));
                    writer.write("\n");
                }
            }
        }
        return sequencesTestSample;
    }

}
