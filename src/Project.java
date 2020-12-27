import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Project {
    protected static int frameCount;
    private static int processSize;
    private static int pageSize;
    private static int pageCount;
    private static String algoName;
    private static int tlbSize;
    protected static List<Integer> pageRequests;
    private static List<Integer> pageOffsets;
    private static List<String> pageAction;
    protected static List<String> outputMessageBuffer;

    private static TLB tlb;
    protected static MainMemory mainMemory;
    protected static PageTable pageTable;
    private static Algorithm algorithm;

    public static void main(String args[]) throws IOException {
        int frame;
        int algoType = 0;
        pageRequests = new ArrayList<Integer>();
        pageOffsets = new ArrayList<Integer>();
        pageAction = new ArrayList<String>();
        outputMessageBuffer = new ArrayList<String>();
        //--------------read from file----------
        readInput();
        //-------------initialize instances--------
        pageCount = (int) Math.ceil(processSize / pageSize);
        tlb = new TLB(tlbSize);
        mainMemory = new MainMemory(frameCount);
        pageTable = new PageTable(pageCount);
        //----------resolve algorithm type----------
        if (algoName.equals("LRU"))
            algoType = 1;
        else if (algoName.equals("FIFO"))
            algoType = 2;
        else if (algoName.equals("OPT"))
            algoType = 3;
        else if (algoName.equals("Second Chance"))
            algoType = 4;

        algorithm = new Algorithm(algoType, pageRequests);
        //------------request pages-------------
        for (int i = 0; i < pageRequests.size(); i++) {
            outputMessageBuffer.add("----------------------------------------------------------------------\r\n");
            outputMessageBuffer.add("Logical address [" + pageRequests.get(i) + "," + pageOffsets.get(i) + "] requested.\r\n");

            if (error(pageRequests.get(i), pageOffsets.get(i)))
                continue;

            frame = request(pageRequests.get(i), algoType, pageAction.get(i), i);

            if (frame == -1) {
                outputMessageBuffer.add("----------------------------------------------------------------------\r\n");
                outputMessageBuffer.add("Restarting last request.\r\n");
                frame = request(pageRequests.get(i), algoType, pageAction.get(i), i);//restart request
            }

            outputMessageBuffer.add("Physical address [" + frame + "," + pageOffsets.get(i) + "].\r\n");
        }
        writeOutputFile();

    }

    private static boolean error(int pageRequest, int offset) {
        if (pageRequest < 0 || pageRequest > pageCount) {
            outputMessageBuffer.add("Error : Page request out of bound!\r\n");
            return true;
        }
        if (offset < 0 || offset > pageSize) {
            outputMessageBuffer.add("Error : Offset out of bound!\r\n");
            return true;
        }
        return false;
    }

    private static int request(int page, int algoType, String action, int requsetIndex) {
        int tlbRV = tlb.getFrame(page);   //TLB return value
        int ptRV;   //page table return value
        int newFrame;

        //is page available in TLB?
        if (tlbRV != -1) {
            if (algoType == 1)
                algorithm.lruUpdate(page);
            if (algoType == 4)
                algorithm.secondChanceUpdate(page, action);
            return tlbRV;
        } else {
            ptRV = pageTable.getFrame(page);
            //is page available in memory?
            if (ptRV != -1) {
                outputMessageBuffer.add("Page found in page table.\r\n");
                //update TLB
                tlb.update(page, ptRV);
                if (algoType == 1)
                    algorithm.lruUpdate(page);
                if (algoType == 4)
                    algorithm.secondChanceUpdate(page, action);
                return ptRV;
            } else {
                outputMessageBuffer.add("Page not found in page table.\r\n");
                //allocate new frame to page
                newFrame = algorithm.handlePageFault(page, algoType, action, requsetIndex);
                //update page table and TLB
                outputMessageBuffer.add("Update page table for new allocated frame.\r\n");
                pageTable.update(page, newFrame);
                tlb.update(page, newFrame);
            }
        }
        //return -1 for restarting request

        return -1;
    }

    private static void readInput() throws FileNotFoundException {
        File inFile = new File("input.txt");
        Scanner sc = new Scanner(inFile);
        String line; //request line
        String[] addressParts;   //split request to get page,offset,action(read or modify)
        frameCount = Integer.parseInt(sc.nextLine());
        processSize = Integer.parseInt(sc.nextLine());
        pageSize = Integer.parseInt(sc.nextLine());
        algoName = sc.nextLine();
        tlbSize = Integer.parseInt(sc.nextLine());

        while (sc.hasNext()) {
            line = sc.nextLine();
            addressParts = line.split(",");
            pageRequests.add((Integer.valueOf(addressParts[0])));
            pageOffsets.add(Integer.valueOf(addressParts[1]));
            pageAction.add(addressParts[2]);
        }


        sc.close();
    }

    protected static void writeOutputFile() throws IOException {
        FileWriter writer = new FileWriter("output.txt");
        for (String str : outputMessageBuffer) {
            writer.write(str);

        }
        writer.close();
    }
}
