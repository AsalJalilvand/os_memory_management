import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TLB {
    private Map<Integer, Integer> tlb;
    private Map<Integer, Integer> tlbReferenceCount;
    private int tlbSize;
    private Iterator mapIt;
    private Map.Entry tlbEntry;


    public TLB(int tlbSize) {
        this.tlbSize = tlbSize;
        tlb = new HashMap();
        tlbReferenceCount = new HashMap();
        Project.outputMessageBuffer.add("TLB Size : " + tlbSize + "\r\n");
    }

    public int getFrame(int pageNum) {
        if (tlb.containsKey(pageNum)) {
            //add reference count by 1
            tlbReferenceCount.put(pageNum, tlbReferenceCount.get(pageNum) + 1);
            Project.outputMessageBuffer.add("Page found in TLB.\r\n");
            return tlb.get(pageNum);
        } else
            Project.outputMessageBuffer.add("Page not found in TLB.\r\n");
        return -1;//page not in tlb
    }

    public void update(int pageNum, int frame) {
        int maxReference;
        int replacedTLBKey = 0;
        Project.outputMessageBuffer.add("TLB Updating...\r\n");
        if (tlb.size() < tlbSize) //empty space found in TLB
        {
            tlb.put(pageNum, frame);
            tlbReferenceCount.put(pageNum, 1);
        } else {
            maxReference = 0;
            mapIt = tlbReferenceCount.entrySet().iterator();
            //find most referenced entry in TLB
            while (mapIt.hasNext()) {
                tlbEntry = (Map.Entry) mapIt.next();
                if ((Integer) tlbEntry.getValue() > maxReference) {
                    maxReference = (Integer) tlbEntry.getValue();
                    replacedTLBKey = (Integer) tlbEntry.getKey();
                }
            }
            //remove entry found by MFU
            tlb.remove(replacedTLBKey);
            tlbReferenceCount.remove(replacedTLBKey);
            Project.outputMessageBuffer.add("Entry for page " + replacedTLBKey + " removed from TLB.\r\n");
            //add new entry
            tlb.put(pageNum, frame);
            tlbReferenceCount.put(pageNum, 1);
            Project.outputMessageBuffer.add("Entry for page " + pageNum + " added to TLB.\r\n");
        }
        printTLB();
    }

    private void printTLB() {
        //   Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("TLB [page,frame] : ");
        mapIt = tlb.entrySet().iterator();
        //find most referenced entry in TLB
        while (mapIt.hasNext()) {
            tlbEntry = (Map.Entry) mapIt.next();
            Project.outputMessageBuffer.add("[" + tlbEntry.getKey() + "|" + tlbEntry.getValue() + "] ");
        }
        Project.outputMessageBuffer.add("\r\n");
        // Project.outputMessageBuffer.add("***************\r\n") ;
    }
}
