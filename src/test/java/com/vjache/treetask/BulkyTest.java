package com.vjache.treetask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 *
 */
public class BulkyTest {

    private static class DataTotals {
        private AtomicInteger successes = new AtomicInteger(0);
        private AtomicInteger failures  = new AtomicInteger(0);
        private AtomicInteger failuresExc  = new AtomicInteger(0);
    }

    class TestNode extends TaskNode<DataTotals> {

        @Override
        Branch run(DataTotals result) throws Throwable {

            switch (decideRandomOutcome()) {
                case Success:
                    result.successes.incrementAndGet();
                    return Branch.Success;
                case Failure:
                    result.failures.incrementAndGet();
                    return Branch.Failure;
                case FailureWithException:
                    result.failuresExc.incrementAndGet();
                    throw new RuntimeException("Test");
            }

            return Branch.Failure; // Actually unreachable.
        }
    }

    Random rnd;

    @Before
    public void setUp() {
        rnd = new Random();
    }

    @Test
    public void bulkTree() throws InterruptedException {
        int treeDeepness = 10;
        TestNode   root = generateTree(treeDeepness);
        DataTotals data = new DataTotals();
        TaskNodeExecutor.execute(data, root);

        int theExpectedTotal = (1 << treeDeepness) - 1; //The sum of geometric progression with factor 2

        int actualTotal = data.successes.get() + data.failures.get() + data.failuresExc.get();

        Assert.assertEquals(theExpectedTotal, actualTotal);

        System.out.println(
                format("Totals: success = %s, failure = %s, failureEx = %s, sum = %s.",
                        data.successes, data.failures, data.failuresExc, actualTotal));
    }

    enum Outcome {
        Success,
        Failure,
        FailureWithException
    }

    private Outcome decideRandomOutcome() {
        return Outcome.values()[rnd.nextInt(Outcome.values().length)];
    }

    private TestNode generateTree(int deep) {

        TestNode root = new TestNode();

        if (deep > 1) {

            for (int i = 0; i < 2; i++) {
                root.onSuccess(generateTree(deep - 1));
            }

            for (int i = 0; i < 2; i++) {
                root.onFailure(generateTree(deep - 1));
            }

        }

        return root;
    }

}
