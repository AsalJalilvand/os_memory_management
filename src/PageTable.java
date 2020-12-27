/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 5/29/16
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PageTable {
    private int[] pageTable;

    public PageTable(int pageCount) {
        Project.outputMessageBuffer.add("Page Count = " + pageCount + "\r\n");
        pageTable = new int[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pageTable[i] = -1; //no pages in memory at the beginning
            // -1 = invalid
        }
    }

    public int getFrame(int pageNum) {
        return pageTable[pageNum];
    }

    public void update(int pageNum, int frame) {
        // Project.outputMessageBuffer.add("Page table updating...\r\n");
        pageTable[pageNum] = frame;
        printPT();
    }

    public boolean valid(int pageNum) {
        return pageTable[pageNum] == -1;
    }

    private void printPT() {
        // Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("Page table : ");
        for (int i = 0; i < pageTable.length; i++) {
            Project.outputMessageBuffer.add("[" + pageTable[i] + "] ");
        }
        Project.outputMessageBuffer.add("\r\n");
        // Project.outputMessageBuffer.add("***************\r\n") ;
    }

}
