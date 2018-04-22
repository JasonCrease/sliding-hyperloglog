package SlidingHyperLogLog;


import org.testng.annotations.Test;

public class SlidingHyperLogLogOptionsTests {

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "windowSize is -1. It must be > 0.")
    public void windowNegativeTest()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(-1)
                .setRsd(0.1)
                .build();
    }

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "rsd must between 0 and 1")
    public void rsdTooHighTest1()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(10)
                .setRsd(2)
                .build();
    }

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Decrease rsd. Not so close to 1.")
    public void rsdTooHighTest2()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(10)
                .setRsd(1 - 1E-9)
                .build();
    }

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Increase rsd. Not so close to 0.")
    public void rsdTooLowTest1()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(10)
                .setRsd(1E-24)
                .build();
    }

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "rsd must between 0 and 1")
    public void rsdTooLowTest2()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(10)
                .setRsd(-1)
                .build();
    }

    @Test(expectedExceptions =  IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Please set rsd")
    public void rsdNotSet()
    {
        new SlidingHyperLogLog.Builder()
                .setWindowSize(10)
                .build();
    }
}
