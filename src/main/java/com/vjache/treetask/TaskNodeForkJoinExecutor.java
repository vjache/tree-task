package com.vjache.treetask;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * This is an alternative task executor which is uses the fork/join facility of Java Concurrency.
 */
public class TaskNodeForkJoinExecutor<R> extends RecursiveAction {
    private final TaskNode<R> task;
    private final R result;

    TaskNodeForkJoinExecutor(R result, TaskNode<R> task) {
        this.result = result;
        this.task = task;
    }
    protected void compute() {
       final  ArrayList<TaskNodeForkJoinExecutor> forked = new ArrayList<>();
        try {
            final Branch branch = task.run(result);
            if (branch == null) throw new NullPointerException("Branch must not be null.");
            switch (branch)
            {
                case Success: {
                    for (TaskNode<R> n : task.getSuccessBranches())
                        new TaskNodeForkJoinExecutor<R>(result, n).fork();
                    break;
                }
                case Failure: {
                    for (TaskNode<R> n : task.getFailureBranches())
                        new TaskNodeForkJoinExecutor<R>(result, n).fork();
                    break;
                }
            }
        }
        catch (Throwable e) {
            for (TaskNode n : task.getFailureBranches())
                new TaskNodeForkJoinExecutor<R>(result, n).fork();
        }

        for (TaskNodeForkJoinExecutor t : forked)
            t.join();
    }

    public static <R> void execute(R result, TaskNode<R> taskTree) throws InterruptedException {

        final ForkJoinPool executor = new ForkJoinPool();

        executor.invoke(new TaskNodeForkJoinExecutor<R>(result, taskTree));
    }
}
