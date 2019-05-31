/** SJFSchedulingAlgorithm.java
 * 
 * A shortest job first scheduling algorithm.
 *
 * @author: Charles Zhu
 * Spring 2016
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

import com.jimweller.cpuscheduler.Process;

public class SJFSchedulingAlgorithm extends BaseSchedulingAlgorithm implements OptionallyPreemptiveSchedulingAlgorithm {

    private ArrayList<Process> jobs;
    private boolean preemptive;

    class SJFComparator implements Comparator<Process> {
        public int compare(Process p1, Process p2) {
            if (p1.getBurstTime() != p2.getBurstTime()) {
                return Long.signum(p1.getBurstTime() - p2.getBurstTime());
            }
            return Long.signum(p1.getPID() - p2.getPID());
        }
    }

    SJFComparator comparator = new SJFComparator();

    SJFSchedulingAlgorithm(){
        // Fill in this method
        /*------------------------------------------------------------*/
        activeJob = null;
        jobs = new ArrayList<Process>();
        /*------------------------------------------------------------*/
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p){
        // Fill in this method
        /*------------------------------------------------------------*/
//        System.out.println("Job queue add job pid: " + p.getPID() + ", cpu burst time: " + p.getBurstTime());
        jobs.add(p);
        Collections.sort(jobs, comparator);
        /*------------------------------------------------------------*/
    }
    
    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
        // Fill in this method
        /*------------------------------------------------------------*/
//        System.out.println("Job query remove jod pid: " + p.getPID() + ", cpu burst time: " + p.getBurstTime());
        if (p == activeJob)
            activeJob = null;
        return jobs.remove(p);
        /*------------------------------------------------------------*/
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }

    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime){
        // Fill in this method
        /*------------------------------------------------------------*/
        Process shortest = null;

        if (!preemptive && !isJobFinished())
            return activeJob;
        if (jobs.size() > 0)
            shortest = jobs.get(0);

        activeJob = shortest;
//        System.out.println("Next job to run: " + activeJob.getPID() + ", cpu burst time: " + activeJob.getBurstTime());
        return activeJob;
        /*------------------------------------------------------------*/
    }

    public String getName(){
        return "Shortest Job First";
    }

    /**
     * @return Value of preemptive.
     */
    public boolean isPreemptive(){
        // Fill in this method
        /*------------------------------------------------------------*/
        return preemptive;
        /*------------------------------------------------------------*/
    }
    
    /**
     * @param v  Value to assign to preemptive.
     */
    public void setPreemptive(boolean  v){
        // Fill in this method
        /*------------------------------------------------------------*/
        preemptive = v;
        /*------------------------------------------------------------*/
    }
    
}