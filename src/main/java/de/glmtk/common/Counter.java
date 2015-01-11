package de.glmtk.common;

import java.util.List;

import de.glmtk.util.StringUtils;

/**
 * This class is used for counting continuation counts It is also a wrapper
 * class to handle the continuation counts during Kneser Ney Smoothing. It is
 * thus called during training and also during testing.
 */
public class Counter {
    public static String getSequenceAndCounter(String line,
                                               Counter counter) throws IllegalArgumentException {
        List<String> split = StringUtils.splitAtChar(line, '\t');
        if (split.size() == 2) {
            // absolute
            counter.setOnePlusCount(parseLong(split.get(1)));
            counter.setOneCount(0L);
            counter.setTwoCount(0L);
            counter.setThreePlusCount(0L);
        } else if (split.size() == 5) {
            // continuation
            counter.setOnePlusCount(parseLong(split.get(1)));
            counter.setOneCount(parseLong(split.get(2)));
            counter.setTwoCount(parseLong(split.get(3)));
            counter.setThreePlusCount(parseLong(split.get(4)));
        } else
            throw new IllegalArgumentException(
                    "Expected line to have format '<sequence>(\\t<count>){1,4}'.");
        return split.get(0);
    }

    private static long parseLong(String value) throws NumberFormatException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format(
                    "Unable to parse '%s' as a floating point.", value));
        }
    }

    private long onePlusCount;
    private long oneCount;
    private long twoCount;
    private long threePlusCount;

    public Counter() {
        onePlusCount = 0;
        oneCount = 0;
        twoCount = 0;
        threePlusCount = 0;
    }

    public Counter(long onePlusCount,
                   long oneCount,
                   long twoCount,
                   long threePlusCount) {
        this.onePlusCount = onePlusCount;
        this.oneCount = oneCount;
        this.twoCount = twoCount;
        this.threePlusCount = threePlusCount;
    }

    public void add(Counter counter) {
        onePlusCount += counter.onePlusCount;
        oneCount += counter.oneCount;
        twoCount += counter.twoCount;
        threePlusCount += counter.threePlusCount;
    }

    public void add(long count) {
        onePlusCount += count;
        if (count == 1)
            ++oneCount;
        //            oneCount += count;
        if (count == 2)
            ++twoCount;
        //            twoCount += count;
        if (count >= 3)
            ++threePlusCount;
        //            threePlusCount += count;
    }

    public void addOne(long count) {
        ++onePlusCount;
        if (count == 1)
            ++oneCount;
        else if (count == 2)
            ++twoCount;
        else if (count >= 3)
            ++threePlusCount;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(Long.toString(onePlusCount)).append('\t');
        result.append(Long.toString(oneCount)).append('\t');
        result.append(Long.toString(twoCount)).append('\t');
        result.append(Long.toString(threePlusCount));
        return result.toString();
    }

    public long getOnePlusCount() {
        return onePlusCount;
    }

    public void setOnePlusCount(long onePlusCount) {
        this.onePlusCount = onePlusCount;
    }

    public long getOneCount() {
        return oneCount;
    }

    public void setOneCount(long oneCount) {
        this.oneCount = oneCount;
    }

    public long getTwoCount() {
        return twoCount;
    }

    public void setTwoCount(long twoCount) {
        this.twoCount = twoCount;
    }

    public long getThreePlusCount() {
        return threePlusCount;
    }

    public void setThreePlusCount(long threePlusCount) {
        this.threePlusCount = threePlusCount;
    }

    /**
     * Currently only used in testing.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        else if (other == null || getClass() != other.getClass())
            return false;

        Counter o = (Counter) other;
        if (onePlusCount != o.onePlusCount)
            return false;
        else if (oneCount != o.oneCount)
            return false;
        else if (twoCount != o.twoCount)
            return false;
        else if (threePlusCount != o.threePlusCount)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation guided by: <a href=
     * "http://www.angelikalanger.com/Articles/EffectiveJava/03.HashCode/03.HashCode.html"
     * >Angelika Langer: Implementing the hashCode() Method</a>
     */
    @Override
    public int hashCode() {
        int hash = 23984;
        int mult = 457;

        hash += mult * onePlusCount;
        hash += mult * oneCount;
        hash += mult * twoCount;
        hash += mult * threePlusCount;

        return hash;
    }
}
