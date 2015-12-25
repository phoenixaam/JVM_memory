package vm;

public class Memory {
    private final int fromIndex;
    private final int toIndex;

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public Memory(int fromIndex, int toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;

    }

    public int getSize() {
        return toIndex - fromIndex + 1;
    }
}
