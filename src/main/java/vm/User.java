package vm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {
    private static int counter;
    private static final Map<Integer, User> users = new HashMap<>();
    private Memory userMemory;
    private final int userId;

    public User() {
        this.userId = ++counter;
        users.put(userId, this);
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

    public void setUserMemory(Memory memory) {
        userMemory = Objects.requireNonNull(memory);
    }

    public static User getUserById(int userId) {
        if (userId != 0) {
            return users.get(userId);
        } else {
            return null;
        }
    }
}
