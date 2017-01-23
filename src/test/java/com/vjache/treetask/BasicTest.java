package com.vjache.treetask;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.vjache.treetask.Branch.Failure;
import static com.vjache.treetask.Branch.Success;
import static org.junit.Assert.*;

/**
 *
 */
public class BasicTest {

    private static class TotalData {
        public Boolean isPasportValid;
        public Boolean isWanted;
        public Boolean isTerrorist;
    }

    @Test
    public void simpleTaskTree() throws InterruptedException {

        TaskNode<TotalData> validatePassport = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("validatePassport");
                result.isPasportValid = true;
                return Success;
            }
        };

        TaskNode<TotalData> checkWanted = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkWanted");
                return Success;
            }
        };

        TaskNode<TotalData> checkWantedLocal = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkWantedLocal");
                return Success;
            }
        };

        TaskNode<TotalData> checkWantedInternet = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkWantedInternet");
                result.isWanted = true;
                return Success;
            }
        };

        TaskNode<TotalData> checkTerrorist = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkTerrorist");
                result.isTerrorist = false;
                return Success;
            }
        };

        TaskNode<TotalData> finish = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("finish");
                return Success;
            }
        };

        validatePassport.onSuccess(checkWanted);
        validatePassport.onSuccess(checkTerrorist);
        validatePassport.onFailure(finish);

        checkWanted.onSuccess(checkWantedLocal);
        checkWanted.onSuccess(checkWantedInternet);

        /////////////////////////////////////////////////////////


        final TotalData result = new TotalData();

        assertEquals(null, result.isPasportValid);
        assertEquals(null, result.isWanted);
        assertEquals(null, result.isTerrorist);

        TaskNodeExecutor.execute(result, validatePassport);

        assertTrue(result.isPasportValid);
        assertTrue(result.isWanted);
        assertFalse(result.isTerrorist);
    }

    @Test
    public void failureBranch() throws InterruptedException {

        TaskNode<TotalData> validatePassport = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("validatePassport");
                result.isPasportValid = true;
                return Failure; // Ensure failure branch will be choosen
            }
        };

        TaskNode<TotalData> checkWanted = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkWanted");
                return Success;
            }
        };

        TaskNode<TotalData> checkTerrorist = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkTerrorist");
                result.isTerrorist = false;
                return Success;
            }
        };

        final AtomicBoolean isFinishExecuted = new AtomicBoolean(false);

        TaskNode<TotalData> finish = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("finish");
                isFinishExecuted.set(true);
                return Success;
            }
        };

        validatePassport.onSuccess(checkWanted);
        validatePassport.onSuccess(checkTerrorist);
        validatePassport.onFailure(finish);

        /////////////////////////////////////////////////////////


        final TotalData result = new TotalData();

        assertEquals(null, result.isPasportValid);
        assertEquals(null, result.isWanted);
        assertEquals(null, result.isTerrorist);

        TaskNodeExecutor.execute(result, validatePassport);

        assertTrue(result.isPasportValid);
        assertEquals(null, result.isWanted);
        assertEquals(null, result.isTerrorist);
        assertTrue(isFinishExecuted.get());
    }

    @Test
    public void failureBranchOnException() throws InterruptedException {

        TaskNode<TotalData> validatePassport = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("validatePassport");
                result.isPasportValid = true;
                throw new RuntimeException("TEST"); // Ensure failure branch will be choosen
            }
        };

        TaskNode<TotalData> checkWanted = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkWanted");
                return Success;
            }
        };

        TaskNode<TotalData> checkTerrorist = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("checkTerrorist");
                result.isTerrorist = false;
                return Success;
            }
        };

        final AtomicBoolean isFinishExecuted = new AtomicBoolean(false);

        TaskNode<TotalData> finish = new TaskNode<TotalData>() {
            Branch run(TotalData result) throws Throwable {
                System.out.println("finish");
                isFinishExecuted.set(true);
                return Success;
            }
        };

        validatePassport.onSuccess(checkWanted);
        validatePassport.onSuccess(checkTerrorist);
        validatePassport.onFailure(finish);

        /////////////////////////////////////////////////////////


        final TotalData result = new TotalData();

        assertEquals(null, result.isPasportValid);
        assertEquals(null, result.isWanted);
        assertEquals(null, result.isTerrorist);

        TaskNodeExecutor.execute(result, validatePassport);

        assertTrue(result.isPasportValid);
        assertEquals(null, result.isWanted);
        assertEquals(null, result.isTerrorist);
        assertTrue(isFinishExecuted.get());
    }

}
