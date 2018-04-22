package SlidingHyperLogLog;

import java.util.Arrays;

/**
 * List of possible future maxima based on an array
 *
 * Not thread safe
 *
 */
public class RingBufferLPFM implements LPFM {

    private final long _windowSize;

    // Ring-buffer of timestamps and Rs. Note that there will be expired ts and rs in these arrays
    // _start points beyond these
    private long[] _ts = null;
    private int[]  _rs = null;

    // Start and end of ring buffer inclusive, and exclusive respectively
    // Thus _end is where we're going to write
    private int _start;
    private int _end;

    RingBufferLPFM(long windowSize){
        _windowSize = windowSize;
        _start = 0;
        _end = 0;
    }

    private void setArraysToSize(int newLen) {

        int oldLen = _ts.length;
        long[] newTs = new long[newLen];
        int[]  newRs = new  int[newLen];

        int sourcePos = _start;

        for(int destPos = 0; destPos < oldLen;)
        {
            newTs[destPos] = _ts[sourcePos];
            newRs[destPos] = _rs[sourcePos];

            sourcePos++; destPos++;

            if (sourcePos == oldLen)
                sourcePos = 0;
        }

        _ts = newTs;
        _rs = newRs;

        _start = 0;
        _end = oldLen;

    }

    public void offer(long timestamp, int R) {

        // If buffer is full, resize to bigger one
        if (_start == _end) {
            // Special-case lazy initialization on first offer
            if (null == _ts) {
                _ts = new long[1];
                _rs = new  int[1];
            }
            else
                setArraysToSize(_ts.length << 1); // Use powers of 2
        }

        long tMin = timestamp - _windowSize;
        int len = _ts.length;


        // Move past items outside the window
        int sourceJ = _start;
        while (sourceJ != _end && _ts[sourceJ] < tMin ) {
            sourceJ++;
            if (sourceJ == len)
                sourceJ = 0;
         }
        _start = sourceJ;

        // Compact items which aren't a PFM (possible future maximum)
        int destinJ = sourceJ;
        while (sourceJ != _end)
        {
            if(sourceJ != destinJ) {
                _rs[destinJ] = _rs[sourceJ];
            }

            if (_rs[sourceJ] > R) {
                destinJ++;
            }

            sourceJ++;

            if(destinJ == len)
                destinJ = 0;
            if(sourceJ == len)
                sourceJ = 0;
        }
        // Put new t and R at end
        _ts[destinJ] = timestamp;
        _rs[destinJ] = R;

        destinJ++;
        if(destinJ == len)
            _end = 0;
        else
            _end = destinJ;

    }

    @Override
    public int getMaxSince(long tMin) {

        if (_ts == null)
            return 0;

        // The array will also have at least 1 item in it, since offer will always
        // put something at the end. If and only if nothing has ever been offered
        // will len be zero.
        int len = _ts.length;

        int maxSoFar = 0;
        int searchPos = _start;

        // If the buffer is full _searchPos == _end. For speed, special case this by searching this one item
        // using the standard method, and then progressing
        if (searchPos == _end)
        {
            if (_ts[searchPos] >= tMin && _rs[searchPos] > maxSoFar) {
                maxSoFar = _rs[searchPos];
            }

            searchPos++;

            if(searchPos == len)
                searchPos = 0;
        }

        while (searchPos != _end)
        {
            if (_ts[searchPos] >= tMin && _rs[searchPos] > maxSoFar) {
                maxSoFar = _rs[searchPos];
            }

            searchPos++;

            if(searchPos == len)
                searchPos = 0;
        }

        return maxSoFar;
    }

    @Override
    public String toString(){
        return String.format("_ts=%s, _rs=%s, _start=%d, _end=%d", Arrays.toString(_ts), Arrays.toString(_rs), _start, _end);
    }

}
