package vm;

import java.util.Objects;

public class VirtualMachine {
    private final Object[] memory;
    private final int minFreeMemorySize;
    private final int[] memoryUsage;
    private static Memory lastGivenMemory;
    private static final int defaultAllMemorySize = 256;
    private static final int defaultMinFreeMemorySize = 64;

    private static VirtualMachine virtualMachine;

    private VirtualMachine(int allMemorySize, int minFreeMemorySize) {
        this.memory = new Object[allMemorySize];
        this.minFreeMemorySize = minFreeMemorySize;
        this.memoryUsage = new int[allMemorySize];
    }


    public static void create(int allMemorySize, int minFreeMemorySize) {
        virtualMachine = new VirtualMachine(allMemorySize, minFreeMemorySize);
    }

    public static VirtualMachine getInstance() {
        if (virtualMachine == null) {
            virtualMachine = new VirtualMachine(defaultAllMemorySize, defaultMinFreeMemorySize);
        }
        return virtualMachine;
    }

    public static void main(String[] args) {
        VirtualMachine.create(100, 15);
        VirtualMachine virtualMachine = VirtualMachine.getInstance();
        for (int i = 0; i < 10; i++) {
            boolean flag;
            System.out.println("итерация " + i);
            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            flag = user1.askForMemory(25);
            if (flag) {
                user1.usingMemory(virtualMachine.memory);
                System.out.println("user " + user1.getUserId() + " take 25");
            }
            flag = user2.askForMemory(15);
            if (flag) {
                user2.usingMemory(virtualMachine.memory);
                System.out.println("user " + user2.getUserId() + " take 15");
            }
            flag = user3.askForMemory(5);
            if (flag) {
                user3.usingMemory(virtualMachine.memory);
                System.out.println("user " + user3.getUserId() + " take 5");
            }
            flag = user1.release();
            if (flag) {
                System.out.println("user " + user1.getUserId() + " release");
            }
            printMemory();
            while (virtualMachine.calculateFreeSpace() < virtualMachine.minFreeMemorySize) {
                System.out.println("мало памяти: " + virtualMachine.calculateFreeSpace());
                System.out.println("запуск GC");
                virtualMachine.new GC().clean();
                printMemory();
            }
            flag = user2.release();
            if (flag) {
                System.out.println("user " + user2.getUserId() + " release");
            }
        }
    }

    private static void printMemory() {
        System.out.println("memoryUsage:");
        for (int i : virtualMachine.memoryUsage) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("memory:");
        for (Object o : virtualMachine.memory) {
            System.out.print(o + " ");
        }
        System.out.println();
    }

    private int calculateFreeSpace() {
        int result;
        if (lastGivenMemory != null) {
            result = memory.length - (lastGivenMemory.getToIndex() + 1);
        } else {
            result = memory.length;
        }
        return result;
    }


    public Memory getMemory(int userId, int size) {
        int from = 0;
        int to = 0;
        if (size > 0 && lastGivenMemory == null && memory.length > size) {
            to = size - 1;
        } else if (size > 0 && (memory.length - (lastGivenMemory.getToIndex() + 1)) > size) {
            from = lastGivenMemory.getToIndex() + 1;
            to = from + size - 1;
        } else {
            return new Memory(0, 0);
        }
        Memory newUserMemory = new Memory(from, to);
        lastGivenMemory = newUserMemory;
        for (int i = from; i <= to; i++) {
            memoryUsage[i] = userId;
        }
        return newUserMemory;
    }

    public boolean setFreeMemory(int userId, Memory userMemory) {
        int from = userMemory.getFromIndex();
        int to = userMemory.getToIndex();
        for (int i = from; i <= to; i++) {
            if (memoryUsage[i] == userId) {
                memoryUsage[i] = 0;
            } else {
//                new Exception()
                return false;
            }
        }
        return true;
    }


    private class GC {
        public void clean() {
            Memory memoryToMove = findMemoryToMove();

            if (memoryToMove.getFromIndex() > 0) {
                Memory memoryToClean = findMemoryToClean(memoryToMove);
                if (memoryToClean.getSize() > 0) {
                    // determine user for memory
                    // not now
//                        int userId = memoryUsage[memoryToMove.getFromIndex()];
                    // move data
                    System.arraycopy(memory, memoryToMove.getFromIndex(), memory, memoryToClean.getFromIndex(), memoryToMove.getSize());
                    // move usage
                    System.arraycopy(memoryUsage, memoryToMove.getFromIndex(), memoryUsage, memoryToClean.getFromIndex(), memoryToMove.getSize());
                    // clean free space
                    for (int i = memoryToClean.getFromIndex() + memoryToMove.getSize(); i <= memoryToMove.getToIndex(); i++) {
                        memory[i] = 0;
                        memoryUsage[i] = 0;
                    }
                    // move last given
                    lastGivenMemory = new Memory(memoryToClean.getFromIndex(), memoryToClean.getFromIndex() + memoryToMove.getSize() - 1);
                    // set new memory to user
                    // // TODO: 25.12.2015 find user by id and set him new memory
                    // scan memoryUsage
                }
            }
        }

        private Memory findMemoryToMove() {
            lastGivenMemory = Objects.requireNonNull(lastGivenMemory);
            int from = 0;
            int to = 0;
            for (int i = lastGivenMemory.getToIndex(); i >= 0; i--) {
                if (to > 0 && memoryUsage[i] != 0 ) {
                    from = i;
                } else if (memoryUsage[i] != 0) {
                    from = i;
                    to = i;
                } else if (from != 0) {
                    break;
                }
            }
            return new Memory(from, to);
        }

        private Memory findMemoryToClean(Memory memoryToMove) {
            int from = 0;
            int to = 0;
            for (int i = memoryToMove.getFromIndex() - 1; i >= 0; i--) {
                if (to > 0 && memoryUsage[i] == 0) {
                    from = i;
                } else if (memoryUsage[i] == 0) {
                    from = i;
                    to = i;
                } else if (from != 0) {
                    break;
                }
            }
            return new Memory(from, to);
        }
    }


}
