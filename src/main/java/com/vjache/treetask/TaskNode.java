package com.vjache.treetask;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a specialized runnable entity -- task, which supports desired tree structure.
 * In other words this is a tree cascade of tasks. Each task have its downstream tasks of
 * two kinds -- success and failure. If this task run result is successful then it is supposed
 * that executor will continue execute all success downstream task, otherwise with all failure ones.
 */
public abstract class TaskNode<R> {

    private ArrayList<TaskNode<R>> successBranches = new ArrayList<>();
    private ArrayList<TaskNode<R>> failureBranches = new ArrayList<>();

    public Collection<TaskNode<R>> getSuccessBranches() {
        return successBranches;
    }

    public Collection<TaskNode<R>> getFailureBranches() {
        return failureBranches;
    }

    public void onSuccess(TaskNode<R> node) {
        successBranches.add(node);
    }

    public void onFailure(TaskNode<R> node) {
        failureBranches.add(node);
    }

    /**
     * This method is actually a task to be executed.
     * @param result -- this is an object representing a result data shared by all cascade tasks.
     * @return -- either Branch.Success or Branch.Failure
     * @throws Throwable -- if this function throws throwable then it is the same as if ir returned Branch.Failure.
     */
    abstract Branch run(R result) throws Throwable;
}
