package SlidingHyperLogLog;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * A basic, safe implementation.
 *
 * Slow. Deliberately kept simple and unoptimized.
 *
 * Not thread-safe
 */
public class BasicListLPFM implements LPFM {

    private final long windowSize;
    private final List<Pair<Long, Integer>> LFPMs = Lists.newLinkedList();

    BasicListLPFM(long windowSize) {

        this.windowSize = windowSize;
    }

    @Override
    public void offer(long timestamp, int R) {

        LFPMs.removeIf(p -> p.getLeft() < timestamp - windowSize);
        LFPMs.removeIf(p -> p.getRight() <= R );
        LFPMs.add(Pair.of(timestamp, R));
    }

    @Override
    public int getMaxSince(long tMin) {

        return LFPMs.stream()
                .filter(p -> p.getLeft() >= tMin)
                .map(Pair::getRight)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

}
