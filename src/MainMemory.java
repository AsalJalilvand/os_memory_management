
public class MainMemory {
    protected int[] frames;
    private int frameCount;
    private int fullFramesCount;

    public MainMemory(int frameCount) {
        Project.outputMessageBuffer.add("Frame Count = " + frameCount + "\r\n");
        this.frameCount = frameCount;
        frames = new int[frameCount];
        fullFramesCount = 0;
        for (int i = 0; i < frameCount; i++) {
            frames[i] = -1;//
        }
    }

    public boolean isFree() {
        return fullFramesCount != frameCount;
    }

    public int addToMemory(int pageNum) {
        Project.outputMessageBuffer.add("Adding new page to memory...\r\n");
        for (int i = 0; i < frameCount; i++) {
            if (frames[i] == -1) {
                frames[i] = pageNum;
                fullFramesCount++;
                printMemory();
                return i;
            }
        }
        return -1;
    }

    public int replace(int oldPage, int newPage) {
        for (int i = 0; i < frameCount; i++) {
            if (frames[i] == oldPage) {
                Project.outputMessageBuffer.add("Page " + oldPage + " in frame " + i + " swapped out.\r\n");
                frames[i] = newPage;
                Project.outputMessageBuffer.add("Page " + newPage + " swapped in frame " + i + ".\r\n");
                printMemory();
                return i;
            }
        }
        return -1;
    }

    public int getPage(int frameNum) {
        return frames[frameNum];
    }

    private void printMemory() {
        //   Project.outputMessageBuffer.add("***************\r\n") ;
        Project.outputMessageBuffer.add("Main memory : ");
        for (int i = 0; i < frameCount; i++) {
            Project.outputMessageBuffer.add("[" + frames[i] + "] ");
        }
        Project.outputMessageBuffer.add("\r\n");
        //  Project.outputMessageBuffer.add("***************\r\n") ;

    }
}
