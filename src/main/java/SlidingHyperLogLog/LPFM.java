package SlidingHyperLogLog;

import org.apache.commons.lang3.tuple.Pair;

public interface LPFM {

    void offer(long timestamp, int R);

    int getMaxSince(long tMin);

}
