/** FCFSSchedulingAlgorithm.java
 *
 * A first-come first-served scheduling algorithm.
 * The current implementation will work without memory management features
 *
 */
package com.jimweller.cpuscheduler;

import java.lang.reflect.Member;
import java.util.*;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {

    private ArrayList<Process> jobs;
    private String alloc_algorithm;
    // Add data structures to support memory management
    /*------------------------------------------------------------*/
    private long[][] memory;
    private HashMap<Long, Integer>pidMap; // <PID, INDEX> pair
    private int MEMORY_SIZE = 380;
    /*------------------------------------------------------------*/

    class FCFSComparator implements Comparator<Process> {
        public int compare(Process p1, Process p2) {
            if (p1.getArrivalTime() != p2.getArrivalTime()) {
                return Long.signum(p1.getArrivalTime() - p2.getArrivalTime());
            }
            return Long.signum(p1.getPID() - p2.getPID());
        }
    }

    FCFSComparator comparator = new FCFSComparator();

    FCFSSchedulingAlgorithm() {
        activeJob = null;
        jobs = new ArrayList<Process>();
        // Initialize memory
        /*------------------------------------------------------------*/
        alloc_algorithm = "FIRST"; // By default, use first fit algorithm
        memory = new long[MEMORY_SIZE + 1][3];
        memory[0][0] = 0; memory[0][1] = MEMORY_SIZE; memory[0][2] = 1; // Bin is empty if empty bit = 0, Size, Previous bin size
        pidMap = new HashMap<>();
        /*------------------------------------------------------------*/
    }


    /** Add the new job to the correct queue. */
    public void addJob(Process p) {
        // Check if any memory is available
        /*------------------------------------------------------------*/
        int i = 0;
        long m = p.getMemSize();
        long pid = p.getPID();

        if (alloc_algorithm.equals("FIRST")) {
            while (i < MEMORY_SIZE) {
                if (Long.signum(memory[i][0]) == 0 && Long.signum(memory[i][1] - m) >= 0) { // Bin available
                    int next = (int)(i + m);
                    memory[next][0] = 0;
                    memory[next][1] = memory[i][1] - m;
                    memory[next][2] = m;
                    memory[i][0] = 1;
                    memory[i][1] = m;
                    pidMap.put(pid, i);
                    System.out.println("memory[i][0]: " + memory[i][0] + ", memory[i][1]: " + memory[i][1] + ", next: " + next + ", memory[next][1]: " + memory[next][1] + ", memory[next][2]: " + memory[next][2]);
                    System.out.println("pidMap: " + "(" + pid + ", " + i + ")");
                    break;
                } else {
                    i = (int)(i + memory[i][1]);
                }
            }
        } else if (alloc_algorithm.equals("BEST")) {
            // TODO: Extra Credit (Best-fit algorithm)
        }

        if (i >= MEMORY_SIZE) { // Not enough memory available to add the current process to the queue
            p.setIgnore(true);
            return;
        }

        jobs.add(p);
        Collections.sort(jobs, comparator);
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
        if (p == activeJob)
            activeJob = null;

        // In case memory was allocated, free it
        /*------------------------------------------------------------*/
        long pid = p.getPID();
        if (pidMap.containsKey(pid)) {
            int i = pidMap.get(pid);
            int r = (int)(i + memory[i][1]);
            int l = (int)(i - memory[i][2]);

            memory[i][0] = 0; // Free the bin by setting empty bit to 0
            pidMap.remove(pid);

            // Implement free block coalescing
            if (l >= 0 && r < MEMORY_SIZE && Long.signum(memory[l][0]) == 0 && Long.signum(memory[r][0]) == 0) { // Two neighbors are both empty
                memory[l][1] += (memory[i][1] + memory[r][1]);
                memory[i][1] = 0;
                memory[i][2] = 0;
                memory[r][1] = 0;
                memory[r][2] = 0;
            } else if (l >= 0 && Long.signum(memory[l][0]) == 0) { // Left bin is empty
                memory[l][1] += memory[i][1];
                memory[i][1] = 0;
                memory[i][2] = 0;
            } else if (r < MEMORY_SIZE && Long.signum(memory[r][0]) == 0){ // Right bin is empty
                memory[i][1] += memory[r][1];
                memory[r][0] = 0;
                memory[r][1] = 0;
                memory[r][2] = 0;
            }
        }
        /*------------------------------------------------------------*/

        return jobs.remove(p);
    }

    /**
     * Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such
     * as when switching to another algorithm in the GUI
     */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
        Process earliest = null;

        if (!isJobFinished())
            return activeJob;
        if (jobs.size() > 0)
            earliest = jobs.get(0);
        activeJob = earliest;
        return activeJob;
    }

    public String getName() {
        return "First-Come First-Served";
    }

    public void setMemoryManagment(String v) {
        // Modify class to support memory management
        alloc_algorithm = v;
        System.out.println("Allocation Algorithm: " + alloc_algorithm);
    }
}