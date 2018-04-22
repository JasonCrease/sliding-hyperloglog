package SlidingHyperLogLog;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Implementation of Sliding HyperLogLog, as described at:
 * https://hal.archives-ouvertes.fr/hal-00465313/document
 *
 * Significantly based on stream-lib implementation of HyperLogLog at:
 * https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/stream/cardinality/HyperLogLog.java
 *
 */
public class SlidingHyperLogLog {

    private final int _b;
    private final double _alphaMM;
    private final HashFunction hashFunction = Hashing.murmur3_32(1729);
    private final LPFM[] _lpfms;
    private final short _hashedValueShiftSize;

    private SlidingHyperLogLog(int b, long windowSize, LPFMFactory lpfmFactory) {
        _b = b;
        int m = 1 << _b;
        _hashedValueShiftSize = (short)(Integer.SIZE - _b);

        _lpfms = new LPFM[m];
        _alphaMM = getAlphaMM(_b, m);

        for (int i = 0; i < m; i++) {
            _lpfms[i] = lpfmFactory.buildLPFM(windowSize);
        }
    }

    private static double getAlphaMM(final int p, final int m) {
        // See the paper.
        // http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf . Page 140
        switch (p) {
            case 4:
                return 0.673 * m * m;
            case 5:
                return 0.697 * m * m;
            case 6:
                return 0.709 * m * m;
            default:
                return (0.7213 / (1 + 1.079 / m)) * m * m;
        }
    }

    public void offerHashed(long t, int hashedValue) {

        final int j = hashedValue >>> _hashedValueShiftSize;
        final int r = Integer.numberOfLeadingZeros((hashedValue << _b) | (1 << (_b - 1)) + 1) + 1;
        _lpfms[j].offer(t, r);
    }

    public void offer(long t, int i) {
        final int x = hashFunction.hashInt(i).asInt();
        offerHashed(t, x);
    }


    public void offer(long t, double d) {
        final int x = hashFunction.hashLong(Double.doubleToRawLongBits(d)).asInt();
        offerHashed(t, x);
    }


    public void offer(long t, long l) {
        final int x = hashFunction.hashLong(l).asInt();
        offerHashed(t, x);
    }

    public void offer(long t, String s) {
        final int x = hashFunction.hashString(s, Charset.defaultCharset()).asInt();
        offerHashed(t, x);
    }


    public long cardinalitySince(long tMin) {

        double lpfmSum = 0;

        double zeros = 0.0;

        int count = _lpfms.length;

        for (int i = 0; i < count; i++) {
            LPFM lpfm = _lpfms[i];
            int val = lpfm.getMaxSince(tMin);

            lpfmSum += 1.0 / (1 << val);
            if (val == 0) {
                zeros++;
            }
        }

        double estimate = _alphaMM * (1 / lpfmSum);

        if (estimate <= (5.0 / 2.0) * count) {
            // Small Range Estimate
            // http://algo.inria.fr/flajolet/Publications/FlFuGaMe07.pdf . Page 140
            return Math.round(linearCounting(count, zeros));
        } else {
            return Math.round(estimate);
        }
        // Possibly also add large range correction from paper?

    }

    private static double linearCounting(int m, double V) {
        return m * Math.log(m / V);
    }


    public static class Builder
    {
        private int _b;
        private LPFMFactory _lpfmFactory = new RingBufferLPFMFactory();
        private long _windowSize;

        public SlidingHyperLogLog.Builder setWindowSize(long windowSize) {
            _windowSize = windowSize;
            return this;
        }

        public SlidingHyperLogLog.Builder setLpfmFactory(LPFMFactory lpfmFactory) {
            _lpfmFactory = lpfmFactory;
            return this;
        }

        public SlidingHyperLogLog.Builder setRsd(double rsd) {
            if (rsd <= 0 || rsd >= 1)
                throw new IllegalArgumentException("rsd must between 0 and 1");

            _b = b(rsd);
            if (_b < 1)
                throw new IllegalArgumentException("Decrease rsd. Not so close to 1.");
            if (_b > 30)
                throw new IllegalArgumentException("Increase rsd. Not so close to 0.");
            return this;
        }

        private static int b(double rsd) {
            return (int) (Math.log((1.106 / rsd) * (1.106 / rsd)) / Math.log(2));
        }

        public SlidingHyperLogLog build() {
            if(_b == 0)
                throw new IllegalArgumentException("Please set rsd");
            if(_windowSize <= 0)
                throw new IllegalArgumentException("windowSize is " + _windowSize + ". It must be > 0.");

            return new SlidingHyperLogLog(this._b, this._windowSize, this._lpfmFactory);
        }
    }
}
