package library.addons;

import library.screens.Stats;

public class Upgrade {
    public static final int DROP = 0;
    public static final int DMG = 1;
    public static final int FIRERATE = 2;
    public static final int FLY_SPEED = 3;
    public static final int G_EFFICENCY = 4;
    public static final int G_TIME = 5;
    public static final int DISTANCE = 6;
    public static final int CLICKS_P_S = 7;
    public static final int SPECIAL_S_DMG_F = 8;
    private static final String[] names = {
                        "Increase dropvalue by ", 
                        "Increase damage by ", 
                        "Increase firerate by ", 
                        "Increase fly speed by ", 
                        "Increase g efficency by ", 
                        "Reduce g time by ", 
                        "Reduce meteor distance by ", 
                        "Reduce clicks per shot by ", 
                        "Increase special shot dmg by "};

    public final int type;

    public int factor;
    public final int max;
    public final int steps;
    public int upgrades;

    public final double startPrice;
    public double price;
    public final double slope;
    public final double percIncrease;

    public boolean isMaxed = false;

    /**
    * @param type This is the upgrade type
    * @param max  This is the end value of the stats
    * @param steps  This is how often it can be upgraded
    * @param upgrades  This is the current amount of upgrades
    * @param startPrice  This is the start value
    * @param slope  This is the start slope
    * @param percIncrease  This is the % increase
    */
    public Upgrade(int type, int max, int steps, int upgrades, double startPrice, double slope, double percIncrease){
        this.type = type;
        this.max = max;
        this.steps = steps;
        this.upgrades = upgrades;
        this.startPrice = startPrice;
        this.slope = slope;
        this.percIncrease = percIncrease;

        if(upgrades >= steps){
            upgrades = steps;
            isMaxed = true;
        }

        price = Stats.price(upgrades, startPrice, slope, percIncrease);
    }

    public void bought(){
        upgrades++;
        if(upgrades >= steps){
            upgrades = steps;
            isMaxed = true;
        }
            
        
        factor = max/steps * upgrades;
        price = Stats.price(upgrades, startPrice, slope, percIncrease);
        Stats.calcStats(type);
    }

    /**
     *  @return The upgrade description
     */
    public String description(){
        return names[type] + (max/steps) + "%";
    }
}