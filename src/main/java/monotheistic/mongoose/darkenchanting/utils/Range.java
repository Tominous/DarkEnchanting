package monotheistic.mongoose.darkenchanting.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Range {
    private int first;
    private int last;
    private int chance;
    public Range(int chance, int first, int last) {
        this.chance=chance;
        this.first = first;
        this.last = last;
    }

    public int getNumber() {
        if(new Random().nextInt(101)<=chance)
        return ThreadLocalRandom.current().nextInt(first, last+1);
        else return -1;
    }
}
