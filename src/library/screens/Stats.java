package library.screens;

import library.EngineTools;
import library.addons.Upgrade;

public class Stats {
    
    public static int maxDistance = 8000;

    public static double energy = 12000d;
    public static double material = 2;
    public static int zone = 0;
    private static double[] bossHp = {1000,2000,3000,4000,5000};
    private static double[] enemyHp = {100,200,300,400,500};

    //// Normal Upgrades ////
    public static double dropBase = 1;
    public static double drop;
    public static double dmgBase = 7;
    public static double dmg;
    public static float firerateBase = 1;
    public static float firerate;

    public static double generatorAmount = 0;
    public static double generatorEfficencyBase = 1;
    public static double generatorEfficency;
    public static double generatorTimeBase = 1;
    public static double generatorTime;

    public static float distanceBase = 2000;
    public static float distance;

    //// Premium Upgrades ////
    public static float clicksPerShotBase = 20;
    public static float clicksPerShot;
    public static float specialShotDmgFactorBase = 5;
    public static float specialShotDmgFactor;

    public static float flySpeedBase = 1;
    public static float flySpeed;




    public static Upgrade[] upgradesDrop = new Upgrade[3];
    public static Upgrade[] upgradesDmg = new Upgrade[3];
    public static Upgrade[] upgradesFirerate = new Upgrade[3];
    public static Upgrade[] upgradesGEfficency = new Upgrade[3];
    public static Upgrade[] upgradesGTime = new Upgrade[3];
    public static Upgrade[] upgradesDistance = new Upgrade[3];

    public static Upgrade[] upgradesClicksPerShot = new Upgrade[1];
    public static Upgrade[] upgradesSpecialShotDmgFactor = new Upgrade[1];
    public static Upgrade[] upgradesFlySpeed = new Upgrade[1];


    public static float acceleration;
    public static float accelerationRot;
    public static float maxSpeed;
    public static float maxSpeedSquared;
    public static float maxSpeedRot;
    public static float speedReductionPerc;
    public static float rotReductionFlat;
    public static float rotReductionPerc;

    public static void initalize(){
        upgradesDrop[0] = new Upgrade(Upgrade.DROP, 200, 10, 0, 100, 1, 1.2f);
        upgradesDrop[1] = new Upgrade(Upgrade.DROP, 100, 4, 0, 100, 1, 1.2f);
        upgradesDrop[2] = new Upgrade(Upgrade.DROP, 100, 4, 0, 100, 1, 1.2f);

        upgradesDmg[0] = new Upgrade(Upgrade.DMG, 100, 5, 0, 100, 1, 1.2f);
        upgradesDmg[1] = new Upgrade(Upgrade.DMG, 200, 10, 0, 100, 1, 1.2f);
        upgradesDmg[2] = new Upgrade(Upgrade.DMG, 200, 10, 0, 100, 1, 1.2f);

        upgradesFirerate[0] = new Upgrade(Upgrade.FIRERATE, 100, 5, 0, 100, 1, 1.2f);
        upgradesFirerate[1] = new Upgrade(Upgrade.FIRERATE, 200, 10, 0, 100, 1, 1.2f);
        upgradesFirerate[2] = new Upgrade(Upgrade.FIRERATE, 200, 10, 0, 100, 1, 1.2f);

        upgradesGEfficency[0] = new Upgrade(Upgrade.G_EFFICENCY, 100, 5, 0, 100, 1, 1.2f);
        upgradesGEfficency[1] = new Upgrade(Upgrade.G_EFFICENCY, 200, 10, 0, 100, 1, 1.2f);
        upgradesGEfficency[2] = new Upgrade(Upgrade.G_EFFICENCY, 200, 10, 0, 100, 1, 1.2f);

        upgradesGTime[0] = new Upgrade(Upgrade.G_TIME, 100, 5, 0, 100, 1, 1.2f);
        upgradesGTime[1] = new Upgrade(Upgrade.G_TIME, 200, 10, 0, 100, 1, 1.2f);
        upgradesGTime[2] = new Upgrade(Upgrade.G_TIME, 200, 10, 0, 100, 1, 1.2f);

        upgradesDistance[0] = new Upgrade(Upgrade.DISTANCE, 20, 5, 0, 100, 1, 1.2f);
        upgradesDistance[1] = new Upgrade(Upgrade.DISTANCE, 40, 8, 0, 100, 1, 1.2f);
        upgradesDistance[2] = new Upgrade(Upgrade.DISTANCE, 50, 10, 0, 100, 1, 1.2f);

        upgradesClicksPerShot[0] = new Upgrade(Upgrade.CLICKS_P_S, 50, 5, 0, 100, 1, 1.2f);
        upgradesSpecialShotDmgFactor[0] = new Upgrade(Upgrade.SPECIAL_S_DMG_F, 1000, 20, 0, 100, 1, 1.2f);
        upgradesFlySpeed[0] = new Upgrade(Upgrade.FLY_SPEED, 100, 10, 0, 100, 1, 1.2f);

        calcStats(Upgrade.DROP);
        calcStats(Upgrade.DMG);
        calcStats(Upgrade.FIRERATE);
        calcStats(Upgrade.G_EFFICENCY);
        calcStats(Upgrade.G_TIME);
        calcStats(Upgrade.DISTANCE);
        calcStats(Upgrade.CLICKS_P_S);
        calcStats(Upgrade.SPECIAL_S_DMG_F);
        calcStats(Upgrade.FLY_SPEED);
    }

    public static void calcStats(int type){
        switch(type){
            case Upgrade.DROP:
                drop = dropBase;
                for (Upgrade upgrade : upgradesDrop) {
                    drop *= (double)(100 + upgrade.factor)/100d;
                }
                break;

            case Upgrade.DMG:
                dmg = dmgBase;
                for (Upgrade upgrade : upgradesDmg) {
                    dmg *= (double)(100 + upgrade.factor)/100d;
                }
                break;
            
            case Upgrade.FIRERATE:
                firerate = firerateBase;
                for (Upgrade upgrade : upgradesFirerate) {
                    firerate *= (float)(100 + upgrade.factor)/100d;
                }
                break;
            
            case Upgrade.G_EFFICENCY:
                generatorEfficency = generatorEfficencyBase;
                for (Upgrade upgrade : upgradesGEfficency) {
                    generatorEfficency *= (double)(100 + upgrade.factor)/100d;
                }
                break;
                
            case Upgrade.G_TIME:
                generatorTime = generatorTimeBase;
                for (Upgrade upgrade : upgradesGTime) {
                    generatorTime *= (double)(100 + upgrade.factor)/100d;
                }
                break;

            case Upgrade.DISTANCE:
                distance = distanceBase;
                for (Upgrade upgrade : upgradesDistance) {
                    distance *= (float)(100 - upgrade.factor)/100d;
                }
                break;

            case Upgrade.CLICKS_P_S:
                clicksPerShot = clicksPerShotBase;
                for (Upgrade upgrade : upgradesClicksPerShot) {
                    clicksPerShot *= (float)(100 - upgrade.factor)/100d;
                }
                break;

            case Upgrade.SPECIAL_S_DMG_F:
                specialShotDmgFactor = specialShotDmgFactorBase;
                for (Upgrade upgrade : upgradesSpecialShotDmgFactor) {
                    specialShotDmgFactor *= (float)(100 + upgrade.factor)/100d;
                }
                break;

            case Upgrade.FLY_SPEED:
                flySpeed = flySpeedBase;
                for (Upgrade upgrade : upgradesFlySpeed) {
                    flySpeed *= (float)(100 + upgrade.factor)/100d;
                }
                acceleration = 300*flySpeed;
                accelerationRot = 200*flySpeed;
                maxSpeed = 300*flySpeed;
                maxSpeedSquared = maxSpeed*maxSpeed;
                maxSpeedRot = 100*flySpeed;
                speedReductionPerc = 0.2f*flySpeed;
                rotReductionFlat = 40*flySpeed;
                rotReductionPerc = 0.2f*flySpeed;
                break;
        }
    }

    static double timer = 0;
    static float delay = 0.2f;
    public static void updateEveryFrame(){
        timer += EngineTools.deltaTime;
        if(timer < delay)
            return;


        double generatedEnergy = generatorEfficency*generatorTime*timer;
        double resourceCost = generatorTime*timer;
        if(material >= resourceCost){
            material -= resourceCost;
            energy += generatedEnergy;
        }else if(material != 0){
            energy += generatedEnergy*(material/resourceCost);
            material = 0;
        }



        timer = 0;
    }

    public static double getOreValue(){
        return drop;
    }

    public static double getBossHp(){
        return bossHp[zone];
    }

    public static double getEnemyHp(){
        return enemyHp[zone];
    }

    /**
    * Calculates the price for upgrades.
    * @param n This is the amount of upgrades
    * @param startPrice  This is the start value
    * @param slope  This is the start slope
    * @param percIncrease  This is the % increase
    * @return int This returns sum of numA and numB.
    */
    public static double price(int n, double startPrice, double slope, double percIncrease){
        return startPrice+startPrice*slope*(Math.pow(percIncrease,n) - 1);
    }

    static char[] nn = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'};
    public static String valueToString(double value){
        if(value < 0){
            System.out.println("Value negative");
            return "-";
        }
        if(value < .1)
            return "0";

        String valueString = String.format("%4.3e",value);
        char[] valueChars = valueString.toCharArray();
        StringBuilder sBText = new StringBuilder();

        if(valueString.contains("e")){
            int e = Integer.valueOf(valueString.split("e+")[1]);
            int n = e / 3;
            int rest = e % 3;

            if(e == -1){
                sBText.append('0');
                sBText.append('.');
            }
            
            sBText.append(valueChars[0]);

            if(rest == 0)
                sBText.append('.');

            sBText.append(valueChars[2]);

            if(rest == 1 && valueChars[3] != '0')
                sBText.append('.');

            if(e != -1 && (valueChars[3] != '0' || rest == 2))
                sBText.append(valueChars[3]);

            if(n > 0){
                sBText.append(' ');
                sBText.append(nn[n]);
            }
            
        }else{
            sBText.append(valueString.split("\\.")[0]);
        }
        
        return sBText.toString();
    }
}