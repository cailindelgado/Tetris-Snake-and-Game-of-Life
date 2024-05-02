package sheep.games.random;

import java.util.Random;

public class RandomTetrosTile implements RandomTile {
    private Random random;

    public RandomTetrosTile(Random random) {
        this.random = random;
    }

    /**
     *  this method 'picks' a random number
     * @return A random number between 0 and 5 inclusive
     */
    @Override
    public int pick() {
        return random.nextInt(6);
    }
}
