import java.util.*;

public class Paginacao {
    private static final int NUM_FRAMES = 3;

    public static void main(String[] args) {
        int[] pageReferences = {0, 1, 1, 2, 3, 0, 4, 1, 2, 5, 3, 4, 6};

        System.out.println("Metodo FIFO -> " + fifo(pageReferences) + " falta de paginas");
        System.out.println("Metodo LRU -> " + lru(pageReferences) + " falta de paginas");
        System.out.println("Metodo Clock -> " + clock(pageReferences) + " falta de paginas");
        System.out.println("Metodo NFU -> " + nfu(pageReferences) + " falta de paginas");
    }

    // Algoritmo FIFO
    public static int fifo(int[] pageReferences) {
        Queue<Integer> memoryQueue = new LinkedList<>();
        int pageFaults = 0;

        for (int page : pageReferences) {
            if (!memoryQueue.contains(page)) {
                pageFaults++;
                if (memoryQueue.size() == NUM_FRAMES) {
                    memoryQueue.poll();
                }
                memoryQueue.offer(page);
            }
        }

        return pageFaults;
    }

    // Algoritmo LRU
    public static int lru(int[] pageReferences) {
        LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>(NUM_FRAMES, 0.75f, true);
        int pageFaults = 0;

        for (int page : pageReferences) {
            if (!cache.containsKey(page)) {
                pageFaults++;
                if (cache.size() >= NUM_FRAMES) {
                    int lruPage = cache.keySet().iterator().next();
                    cache.remove(lruPage);
                }
            }
            cache.put(page, 1);
        }

        return pageFaults;
    }

    // Algoritmo Clock
    public static int clock(int[] pageReferences) {
        class PageFrame {
            int pageNumber;
            int referenceBit;

            PageFrame() {
                this.pageNumber = -1;
                this.referenceBit = 0;
            }
        }

        PageFrame[] memory = new PageFrame[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            memory[i] = new PageFrame();
        }

        int pageFaults = 0;
        int pointer = 0;

        for (int page : pageReferences) {
            boolean pageFound = false;
            for (PageFrame frame : memory) {
                if (frame.pageNumber == page) {
                    frame.referenceBit = 1;
                    pageFound = true;
                    break;
                }
            }

            if (!pageFound) {
                pageFaults++;
                while (true) {
                    if (memory[pointer].referenceBit == 0) {
                        memory[pointer].pageNumber = page;
                        memory[pointer].referenceBit = 1;
                        pointer = (pointer + 1) % NUM_FRAMES;
                        break;
                    } else {
                        memory[pointer].referenceBit = 0;
                        pointer = (pointer + 1) % NUM_FRAMES;
                    }
                }
            }
        }

        return pageFaults;
    }

    // Algoritmo NFU
    public static int nfu(int[] pageReferences) {
        int[] counters = new int[NUM_FRAMES];
        int[] memory = new int[NUM_FRAMES];
        Arrays.fill(memory, -1);
        int pageFaults = 0;

        for (int page : pageReferences) {
            boolean pageFound = false;
            for (int i = 0; i < memory.length; i++) {
                if (memory[i] == page) {
                    counters[i]++;
                    pageFound = true;
                    break;
                }
            }

            if (!pageFound) {
                pageFaults++;
                int minIndex = 0;
                for (int i = 1; i < counters.length; i++) {
                    if (counters[i] < counters[minIndex]) {
                        minIndex = i;
                    }
                }
                memory[minIndex] = page;
                counters[minIndex] = 1;
            }
        }

        return pageFaults;
    }
}