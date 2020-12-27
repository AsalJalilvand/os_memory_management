import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Algorithm {

    //LRU
    private LinkedList<Integer> lruStack;

    //Second Chance
    private ArrayList<ArrayList<Integer>> scList;

    //FIFO
    private LinkedList<Integer> fifoQueue;

    //OPT
    private List<Integer> futureRequests;

    public Algorithm(int algorithmType, List futureRequests) {

        switch (algorithmType) {
            case 1:
                lruStack = new LinkedList<Integer>();
                break;
            case 2:
                fifoQueue = new LinkedList<Integer>();
                break;
            case 3:
                this.futureRequests = futureRequests;
                break;
            case 4:
                scList = new ArrayList<ArrayList<Integer>>();
                break;
        }


    }

    ;

    //returns
    public int handlePageFault(int page, int algoType, String action, int requestIndex) {
        int newFrame = -1;
        int oldPage = -1;
        //check if memory available for allocation
        if (Project.mainMemory.isFree()) {
            newFrame = Project.mainMemory.addToMemory(page);
        }
        //find a frame for replacement
        else {
            Project.outputMessageBuffer.add("Memory Full!!!!!!!!!!!!!\r\n");
            switch (algoType) {
                case 1:
                    oldPage = lruReplace();
                    break;
                case 2:
                    oldPage = fifoReplace();
                    break;
                case 3: {
                    oldPage = optReplace(requestIndex);
                    if (oldPage == -1)   //opt didn't find a page
                    {
                        //select last frame for replacement
                        oldPage = Project.mainMemory.getPage(Project.frameCount - 1);
                    }
                }
                break;
                case 4:
                    oldPage = secondChanceReplace();
                    break;
            }
            newFrame = Project.mainMemory.replace(oldPage, page);
            Project.outputMessageBuffer.add("Update page table for removed page from main memory.\r\n");
            Project.pageTable.update(oldPage, -1);
        }
        switch (algoType) {
            case 1:
                lruUpdate(page);
                break;
            case 2:
                fifoUpdate(page);
                break;
            case 4:
                secondChanceUpdate(page, action);
        }
        return newFrame;
    }

    //-------Second Chance-----
    public void secondChanceUpdate(int page, String action) {
        for (int i = 0; i < scList.size(); i++) {
            if (scList.get(i).get(0) == page) {
                if (action.equals("r")) {
                    //set reference bit by bit-wise OR with 2
                    scList.get(i).set(1, scList.get(i).get(1) | 2);
                    Project.outputMessageBuffer.add("Set reference bit of page " + scList.get(i).get(0) + " to 1.\r\n");
                } else {
                    //set modify bit by bit-wise OR with 1
                    scList.get(i).set(1, scList.get(i).get(1) | 1);
                    Project.outputMessageBuffer.add("Set modify bit of page " + scList.get(i).get(0) + " to 1.\r\n");
                }
                printSC();
                return;
            }
        }
        //add new element to second chance list with class defined as (2,0)-> referenced
        scList.add(new ArrayList<Integer>(Arrays.asList(page, 2)));
        Project.outputMessageBuffer.add("Set reference bit of page " + page + " to 1.\r\n");
        printSC();
    }

    public int secondChanceReplace() {
        int i = 0;
        int page = -1;
        while (true) {
            if (scList.get(i).get(1) == 0) {
                page = scList.get(i).get(0);
                scList.remove(i);
                break;
            }
            //reduce element class by 1
            scList.get(i).set(1, scList.get(i).get(1) - 1);
            i = (i + 1) % scList.size();
        }
        return page;
    }

    private void printSC() {
        // Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("Second Chance list [page,page class]: ");
        for (int i = 0; i < scList.size(); i++) {
            Project.outputMessageBuffer.add("[" + scList.get(i).get(0) + "," + scList.get(i).get(1) + "] ");
        }
        Project.outputMessageBuffer.add("\r\n");
        // Project.outputMessageBuffer.add("|\r\n***************\r\n") ;
    }

    //-------LRU--------------
    public void lruUpdate(int pageNum) {
        Project.outputMessageBuffer.add("LRU stack updating...\r\n");
        if (lruStack.contains(pageNum)) {
            lruStack.addFirst(lruStack.remove(lruStack.indexOf(pageNum)));
        } else
            lruStack.addFirst(pageNum);

        // Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("LRU Stack: " + lruStack.toString() + "\r\n");
        // Project.outputMessageBuffer.add("\r\n") ;
    }

    public int lruReplace() {
        return lruStack.removeLast();
    }

    //----FIFO------
    public void fifoUpdate(int pageNum) {
        Project.outputMessageBuffer.add("FIFO queue updatin...\r\n");
        fifoQueue.addLast(pageNum);
        // Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("FIFO queue: " + fifoQueue.toString() + "\r\n");
        //  Project.outputMessageBuffer.add("***************\r\n") ;
    }

    public int fifoReplace() {
        return fifoQueue.removeFirst();
    }

    //-----OPT-------
    public int optReplace(int requestIndex) {
        int farthestIndex = 0;
        int pageToBeReplaced = -1;
        boolean pageWillBeRequested;

        for (int i = 0; i < Project.mainMemory.frames.length; i++) {
            pageWillBeRequested = false;
            for (int j = requestIndex + 1; j < futureRequests.size(); j++) {
                if (Project.mainMemory.frames[i] == futureRequests.get(j)) {
                    if (j > farthestIndex) {
                        farthestIndex = j;
                        pageToBeReplaced = futureRequests.get(j);
                    }
                    pageWillBeRequested = true;
                    break;//no need to check other occurrences
                }
            }
            if (!pageWillBeRequested) {
                pageToBeReplaced = Project.mainMemory.frames[i];
                break;
            }

        }
        return pageToBeReplaced;
    }


}
