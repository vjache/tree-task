package com.vjache.treetask;

import static com.vjache.treetask.Branch.Success;

/**
 *
 */
public class Example {

    public static void main(String args[]) throws InterruptedException {
        TaskNode root = new TaskNode() {
            Branch run(Object result) throws Throwable {
                System.out.println("root");
                return Success;
            }
        };

        TaskNode n1 = new TaskNode() {
            Branch run(Object result) throws Throwable {
                System.out.println("n1");
                return Success;
            }
        };

        TaskNode n2 = new TaskNode() {
            Branch run(Object result) throws Throwable {
                System.out.println("n2");
                return Success;
            }
        };

        TaskNode n21 = new TaskNode() {
            Branch run(Object result) throws Throwable {
                Thread.sleep(1000);
                System.out.println("n21");
                return Success;
            }
        };

        TaskNode n22 = new TaskNode() {
            Branch run(Object result) throws Throwable {
                System.out.println("n22");
                return Success;
            }
        };

        root.onSuccess(n1);
        root.onSuccess(n2);

        n2.onSuccess(n21);
        n2.onFailure(n22);

        //TaskNodeExecutor.execute(new Object(), root);
        TaskNodeForkJoinExecutor.execute(new Object(), root);
    }

}
