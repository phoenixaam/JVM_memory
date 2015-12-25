package vm;

import java.util.Objects;

public class User {
    private static int counter;
    private Memory userMemory;
    private final int userId;

    public User() {
        this.userId = ++counter;
    }

    public int getUserId() {
        return userId;
    }

    public boolean askForMemory(int size) {
        Memory newMemory = VirtualMachine.getInstance().getMemory(userId, size);
        if (newMemory.getSize() == size) {
            userMemory = newMemory;
            return true;
        } else {
            return false;
        }
    }

    public boolean release() {
        if (userMemory != null) {
            return VirtualMachine.getInstance().setFreeMemory(userId, userMemory);
        } else {
            return false;
        }

    }

    public void usingMemory(Object[] memory) {
        memory = Objects.requireNonNull(memory);
        Character toSave = 'A';
        for (int i = userMemory.getFromIndex(); i <= userMemory.getToIndex(); i++) {
            memory[i] = toSave++;
        }
    }
}
