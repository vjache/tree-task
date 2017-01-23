package com.vjache.treetask;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task tree execution strategy using executor service. This strategy
 * executes success or failure branches in parallel. The choice between
 * success and failure branches happens based on result of current
 * node -- if run result is false or exception thrown then run failure
 * branches, else success branches are executed.
 */
public class TaskNodeExecutor<R> implements Runnable {

    private final TaskNode<R>        rootNode;
    private final ExecutorService executorService;
    private final AtomicInteger   count;
    private final R result;

    public TaskNodeExecutor(R result, TaskNode<R> rootNode, AtomicInteger count, ExecutorService executorService) {
        this.result          = result;
        this.rootNode        = rootNode;
        this.executorService = executorService;
        this.count           = count;
        count.incrementAndGet();
    }

    public void run() {
        try {
            final Branch branch = rootNode.run(result);
            if (branch == null) throw new NullPointerException("Branch must not be null.");
            switch (branch)
            {
                case Success: {
                    for (TaskNode<R> n : rootNode.getSuccessBranches())
                        scheduleChildExecution(n);
                    break;
                }
                case Failure: {
                    for (TaskNode<R> n : rootNode.getFailureBranches())
                        scheduleChildExecution(n);
                    break;
                }
            }
        }
        catch (Throwable e) {
            for (TaskNode n : rootNode.getFailureBranches())
                scheduleChildExecution(n);
        }
        finally {
            count.decrementAndGet();
        }
    }

    private void scheduleChildExecution(TaskNode<R> n) {
        executorService.execute(new TaskNodeExecutor<R>(result, n, count, executorService));
    }

    private void start() {
        executorService.execute(this);
    }

    public static <R> void execute(R result, TaskNode<R> taskTree, ExecutorService executor) throws InterruptedException {

        final AtomicInteger tasksToRunCnt = new AtomicInteger(0);

        new TaskNodeExecutor<R>(result, taskTree, tasksToRunCnt, executor).start();

        while (tasksToRunCnt.get() > 0) {
            Thread.sleep(50);
        }
    }

    public static <R> void execute(R result, TaskNode<R> taskTree) throws InterruptedException {

        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        execute(result, taskTree, executor);

        executor.shutdown();
    }
}
