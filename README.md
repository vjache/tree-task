# Tree Task

This is a simple execution environment for tree like cascaded tasks. The main API to specify a tree tasks is a 'TaskNode' abstract java class. To execute such a trees of tasks there are two executors TaskNodeExecutor and TaskNodeForkJoinExecutor (just for fun). See class Example for example usage, and also JUnit tests BasicTest.
