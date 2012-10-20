package KBaluh.github.com.Levels;

/**
 * Author: KBaluh
 * Date: 19.10.12:21:43
 */
public class VictoryCondition {

    private int maxFishSkipped = 10;
    private int fishSkipped = 0;

    private int maxKillCount = 20;
    private int killCount = 0;

    /**
     * Set params for victory condition
     * @param maxFishSkipped - maximum fish skipped count on level
     */
    public void initCondition(int maxFishSkipped, int maxKillCount) {
        this.maxFishSkipped = maxFishSkipped;
        this.maxKillCount = maxKillCount;

        fishSkipped = 0;
        killCount = 0;
    }

    /**
     * Increment fishSkipped at 1
     */
    public void addSkippedFish() {
        ++fishSkipped;
    }

    /**
     * Increment kill count
     */
    public void addKill() {
        ++killCount;
    }

    /**
     * @return Return max fish skipped count
     */
    public int getMaxFishSkipped() {
        return maxFishSkipped;
    }

    /**
     * @return Result current fish skipped count
     */
    public int getFishSkipped() {
        return fishSkipped;
    }

    /**
     * @return return count player kill mobs
     */
    public int getKillCount() {
        return killCount;
    }

    /**
     * @return return max count mob need kill
     */
    public int getMaxKillCount() {
        return maxKillCount;
    }

    /**
     * Check if player skipped maximum fish count, then return "false".
     * @return isFail
     */
    public boolean playerFail() {
        return (fishSkipped >= maxFishSkipped);
    }

    /**
     * Check if player kill max kill count
     * @return IsWin
     */
    public boolean isVictory() {
        return (killCount >= maxKillCount);
    }
}
