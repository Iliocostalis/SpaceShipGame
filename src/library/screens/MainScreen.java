package library.screens;

import java.util.ArrayList;
import java.util.Random;

import library.EngineTools;
import library.addons.Color;
import library.addons.Position;
import library.addons.TrailParticleHolder;
import library.addons.fonts.*;
import library.drawable.Enemy;
import library.drawable.HealthBar;
import library.drawable.Ore;
import library.drawable.ParticleMaster;
import library.drawable.Shot;
import library.drawable.Sprite;
import library.drawable.Trail;

public class MainScreen extends Screen {

    Sprite background[], ship;
    Enemy enemy;
    public static Position movedDistance = new Position();

    Shot shot;
    Shot specialShot;
    ParticleMaster shipParticle;
    ParticleMaster shotParticle;
    ParticleMaster shotExplosion;
    ParticleMaster specialShotExplosion;
    ParticleMaster specialShotParticle;
    ParticleMaster enemyExplosion;
    HealthBar bossHealthBar;
    Font bossTimeFont;

    Random random = new Random();

    ArrayList<Shot> shots = new ArrayList<>();
    ArrayList<Shot> removeShots = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Enemy> removeEnemies = new ArrayList<>();
    ArrayList<ParticleMaster> particleMasters = new ArrayList<>();
    ArrayList<ParticleMaster> removeParticleMasters = new ArrayList<>();
    ArrayList<Ore> ores = new ArrayList<>();
    ArrayList<Ore> removeOres = new ArrayList<>();
    ArrayList<Font> dmgText = new ArrayList<>();
    ArrayList<Font> removeDmgText = new ArrayList<>();

    ArrayList<TrailParticleHolder> holdAlive = new ArrayList<>();
    ArrayList<TrailParticleHolder> removeHoldAlive = new ArrayList<>();


    


    //** Player */
    Position velocity = new Position();
    float rotationVelocity = 0;
    // Stats
    float shotTimer;
    float shotVelocityMax = 800f;

    int currentClicks = 0;
    float specialShotVelocityMax = 1200f;

    HealthBar shotLoading;

    //Movement
    Enemy closestEnemy;
    final float targetEnemyDistance = 350;
    final float maxEnemyShotDistance = 450;
    final float targetBossDistance = 450;
    final float maxBossShotDistance = 550;
    float targetDistanceSq;
    float maxShotDistanceSq;

    //Particle
    float shipParticleMaxSpeed = 350;
    float shipParticleMinSpeed = 100;
    float shipParticleSpawnDelayMin = 0.015f;
    float shipParticleSpawnDelayMax = 0.01f;
    //** Player */



    //** Enemy */
    float enemyHealth = 80;
    int maxEnemies = 1;
    boolean isBossfight = false;
    float bossMaxTime = 30;
    float bossTimer;
    //** Enemy */

    //** Ore */
    Ore oreMaster;
    int oreIronTexturePointer;
    int oreGoldTexturePointer;
    int oreRubyTexturePointer;
    int oreEmeraldTexturePointer;
    int oreSapphireTexturePointer;
    Color oreIronTrail = new Color(.53f, .71f, .69f, .5f);
    Color oreGoldTrail = new Color(.89f, .74f, .16f, .5f);
    Color oreRubyTrail = new Color(.74f, .07f, .07f, .5f);
    Color oreEmeraldTrail = new Color(.07f, .85f, .09f, .5f);
    Color oreSapphireTrail = new Color(.07f, .16f, .67f, .5f);

    float oreCollectRange = 1000;
    float oreStartVelocity = 100;
    float oreVelocityRange = 100;
    float oreRotationVelocity = 100;
    float oreRotationVelocityRange = 100;
    float oreAcceleration = 2200;

    float spawnRateIron = .8f;
    float spawnRateGold = .2f;
    float spawnRateSapphire = 0.0f;
    float spawnRateEmerald = 0.0f;
    float spawnRateRuby = 0.0f;

    //** Ore */

    float[] mvpMatrix;

    Font font = new Font(.7f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
    Sprite materialIcon = new Sprite("material.png",1,1);
    Sprite moneyIcon = new Sprite("money.png",1,1);

    MainScreen() {
        onCreate();
    }

    @Override
    public void onCreate() {

        ship = new Sprite("ship.png", 1, 1);
        ship.setScale(.3f);
        ship.position = new Position((float)EngineTools.screenWidth/2, (float)EngineTools.screenHeight/2);

        shotLoading = new HealthBar(160,40,7,new Color(.7f,.7f,.7f,1), new Color(0,0,1,1), new Color(0,0,0,0));
        shotLoading.position.x = ship.position.x;
        shotLoading.position.y = ship.position.y + 200;


        background = new Sprite[3];
        background[0] = new Sprite("planet1.png",1,1);
        background[1] = new Sprite("planet2.png",1,1);
        background[2] = new Sprite("planet3.png",1,1);

        background[0].position = new Position(300, 2800);
        background[1].position = new Position(1300, 1700);
        background[2].position = new Position(600, 1000);

        background[0].setScale(.5f);
        background[1].setScale(.5f);
        background[2].setScale(.5f);

        background[0].rotation = 30;
        background[1].rotation = 0;
        background[2].rotation = 50;

        shot = new Shot("projectile.png",1,1);
        shot.setScale(.2f);
        specialShot = shot.clone(false);
        specialShot.setScale(.4f);


        loadShipParticle();
        loadShotParticle();
        loadShotExplosion();
        loadEnemyExplosion();
        loadOre();


        enemy = new Enemy("asteroid.png",1,1);
        enemy.setScale(.2f);
        enemy.healthMax = enemyHealth;
        enemy.health = enemyHealth;

        bossHealthBar = new HealthBar(1000, 60, 10, new Color(.7f,.7f,.7f,1), new Color(1,0,0,1), new Color(.4f,0,0,1));
        bossHealthBar.position = new Position(EngineTools.screenWidth/2f, EngineTools.screenHeight - 200);
        bossTimeFont = new Font(.5f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
        bossTimeFont.position.x = bossHealthBar.position.x - bossHealthBar.width/2;
        bossTimeFont.position.y = bossHealthBar.position.y - bossHealthBar.height/2 - 40;
    }

    private void loadShipParticle(){
        shipParticle = new ParticleMaster("white.png", .3f, 0.005f, new Position());
        shipParticle.spawnDelayRange = 0.01f;

        shipParticle.rotationRange = 180;
        shipParticle.rotationSpeed = 100;

        shipParticle.useDirection = true;
        shipParticle.directionRange = 10;

        shipParticle.colorStart = new Color(241/255f, 204/255f, 30/255f, 400/255f);
        shipParticle.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        shipParticle.scale = 5f;
        shipParticle.scaleRange = 2f;
        shipParticle.scaleEnd = 12f;

        //shipParticle.offsetPosPF = movedDistance;
    }

    private void loadEnemyExplosion(){
        enemyExplosion = new ParticleMaster("white.png", .3f, 0.002f, null);
        enemyExplosion.spawnDelayRange = 0.001f;
        enemyExplosion.amountMax = 30;

        enemyExplosion.useDirection = true;
        enemyExplosion.directionRange = 180;
        enemyExplosion.directionSpeed = 500;

        enemyExplosion.rotationSpeed = 300;
        enemyExplosion.rotationRange = 180;


        //enemyExplosion.colorStart = new Color(228/255f, 23/255f,23/255f, 255f/255f);
        //enemyExplosion.colorEnd = new Color(255/255f, 228/255f, 52/255f, 0/255f);

        enemyExplosion.colorStart = new Color(241/255f, 204/255f, 30/255f, 400/255f);
        enemyExplosion.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        enemyExplosion.scale = 20f;
        enemyExplosion.scaleRange = 4f;
        enemyExplosion.scaleEnd = 4f;
    }

    private void loadShotExplosion(){
        shotExplosion = new ParticleMaster("white.png", .3f, 0.002f, null);
        shotExplosion.spawnDelayRange = 0.001f;
        shotExplosion.amountMax = 20;

        shotExplosion.useDirection = true;
        shotExplosion.directionRange = 180;
        shotExplosion.directionSpeed = 300;

        shotExplosion.rotationSpeed = 300;
        shotExplosion.rotationRange = 180;

        shotExplosion.colorStart = new Color(241/255f, 204/255f, 30/255f, 170/255f);
        shotExplosion.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        shotExplosion.scale = 14f;
        shotExplosion.scaleRange = 4f;
        shotExplosion.scaleEnd = 4f;




        specialShotExplosion = new ParticleMaster("white.png", .3f, 0.001f, null);
        specialShotExplosion.spawnDelayRange = 0.0005f;
        specialShotExplosion.amountMax = 100;

        specialShotExplosion.useDirection = true;
        specialShotExplosion.directionRange = 180;
        specialShotExplosion.directionSpeed = 500;

        specialShotExplosion.rotationSpeed = 300;
        specialShotExplosion.rotationRange = 180;

        specialShotExplosion.colorStart = new Color(241/255f, 204/255f, 30/255f, 170/255f);
        specialShotExplosion.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        specialShotExplosion.scale = 20f;
        specialShotExplosion.scaleRange = 6f;
        specialShotExplosion.scaleEnd = 4f;
    }

    private void loadShotParticle(){
        shotParticle = new ParticleMaster("white.png", 0.3f, 0.05f, null);
        shotParticle.spawnDelayRange = 0.04f;
        shotParticle.rotationRange = 180;
        shotParticle.rotationSpeed = 100;
        shotParticle.colorStart = new Color(241/255f, 204/255f, 30/255f, 400/255f);
        shotParticle.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        shotParticle.scale = 5f;
        shotParticle.scaleRange = 2f;
        shotParticle.scaleEnd = 12f;


        specialShotParticle = shotParticle.clone();
        specialShotParticle.spawnDelayRange = 0.02f;
        specialShotParticle.rotationRange = 180;
        specialShotParticle.rotationSpeed = 250;
        //specialShotParticle.colorStart = new Color(241/255f, 204/255f, 30/255f, 400/255f);
        //specialShotParticle.colorEnd = new Color(190/255f, 12/255f, 12/255f, 0);

        specialShotParticle.scale = 7f;
        specialShotParticle.scaleRange = 4f;
        specialShotParticle.scaleEnd = 20f;
    }

    private void loadOre(){
        oreMaster = new Ore("oreIron.png",1,1);
        oreMaster.setScale(.2f);
        oreMaster.trail = new Trail("oreTrail.png", 18, 20);
        oreIronTexturePointer = oreMaster.texturePointer[0];
        oreGoldTexturePointer = EngineTools.loadGLTexture("oreGold.png");
        oreRubyTexturePointer = EngineTools.loadGLTexture("oreRuby.png");
        oreEmeraldTexturePointer = EngineTools.loadGLTexture("oreEmerald.png");
        oreSapphireTexturePointer = EngineTools.loadGLTexture("oreSaphire.png");
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        this.mvpMatrix = mvpMatrix;
        shotTimer += EngineTools.deltaTime;

        moveToClosestEnemy();
        spawnEnemy();
        drawBackground();
        moveRemoveEnemies();
        shotEnemyCollision();
        moveOres();

        for(ParticleMaster particle : particleMasters){
            if(particle.finished)
                removeParticleMasters.add(particle);
            else
                particle.draw(mvpMatrix);
        }
        for(ParticleMaster particle : removeParticleMasters){
            particleMasters.remove(particle);
        }
        removeParticleMasters.clear();

        for (Shot shot : removeShots) {
            shots.remove(shot);
            shot.particleTrail.emitNew = false;
            holdAlive.add(new TrailParticleHolder(shot.particleTrail, null, 2));
        }
        removeShots.clear();
        for (Enemy enemy : removeEnemies) {
            enemies.remove(enemy);
        }
        removeEnemies.clear();
        for (Ore ore : removeOres) {
            ores.remove(ore);
            holdAlive.add(new TrailParticleHolder(null, ore.trail, 2));
        }
        removeOres.clear();



        for (TrailParticleHolder holder : holdAlive) {
            if(holder.isAlive)
                holder.draw(mvpMatrix);
            else
                removeHoldAlive.add(holder);
        }
        removeOres.clear();

        for (TrailParticleHolder holder : removeHoldAlive) {
            holdAlive.remove(holder);
        }
        removeHoldAlive.clear();


        shipParticle.draw(mvpMatrix);
        ship.draw(mvpMatrix);
        shotLoading.draw(mvpMatrix);



        for(Font font : dmgText){
            font.color.a -= EngineTools.deltaTime;
            if(font.color.a <= 0)
                removeDmgText.add(font);
            else{
                font.position.y += 200 * EngineTools.deltaTime;
                font.position.x -= Math.sin(Math.toRadians(font.rotation)) * 200 * EngineTools.deltaTime;
                FontMaster.draw(mvpMatrix, font);
            }    
        }
        for(Font font : removeDmgText){
            dmgText.remove(font);
        }
        removeDmgText.clear();

        drawBossUi();

        font.position.x = 40;
        font.position.y = EngineTools.screenHeight - 60;
        FontMaster.draw(mvpMatrix, font, Stats.valueToString(Stats.energy));

        moneyIcon.setScale(.5f);
        moneyIcon.position.y = font.position.y;
        moneyIcon.position.x = font.endPos.x + 50;
        moneyIcon.draw(mvpMatrix);

        font.position.y = EngineTools.screenHeight - 120;
        FontMaster.draw(mvpMatrix, font, Stats.valueToString(Stats.material));

        materialIcon.setScale(.6f);
        materialIcon.position.y = font.position.y;
        materialIcon.position.x = font.endPos.x + 50;
        materialIcon.draw(mvpMatrix);
    }

    private void moveToClosestEnemy(){
        if(closestEnemy == null)
            return;

        float closestRange = (closestEnemy.position.x - ship.position.x) * (closestEnemy.position.x - ship.position.x) + (closestEnemy.position.y - ship.position.y) * (closestEnemy.position.y - ship.position.y);

        float directionEnemy = 360 + (float)Math.toDegrees(Math.atan2(closestEnemy.position.y - ship.position.y,  closestEnemy.position.x - ship.position.x));
        float directionDif = (directionEnemy - ship.rotation - 90 + 720) % 360;

        if(directionDif < 180)
            ship.rotation += Math.min(directionDif, 0.2f);
        else
            ship.rotation -= 360f - Math.max(directionDif, 359.8f);

        if(directionDif < 180){
            if(rotationVelocity < directionDif*2)
                rotationVelocity += Stats.accelerationRot * EngineTools.deltaTime;
            else{
                rotationVelocity -= Stats.accelerationRot * EngineTools.deltaTime;
            }
        } else {
            directionDif = 360f-directionDif;

            if(-rotationVelocity < directionDif*2)
                rotationVelocity -= Stats.accelerationRot * EngineTools.deltaTime;
            else{
                rotationVelocity += Stats.accelerationRot * EngineTools.deltaTime;
            }
        }
        //float velDifPow = (float)Math.pow(Math.pow((velocity.x - closestEnemy.velocity.x),2) + Math.pow((velocity.y - closestEnemy.velocity.y),2), 1f);
        float velocitySquared = velocity.x*velocity.x + velocity.y*velocity.y;
        if(velocitySquared - targetDistanceSq >= 0){
            if(velocitySquared < closestRange-targetDistanceSq) {
                velocity.y += Math.cos(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
                velocity.x -= Math.sin(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
            }else{
                velocity.y -= Math.cos(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
                velocity.x += Math.sin(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
            }
        }else{
            float nextFramePos = (closestEnemy.position.x - ship.position.x - velocity.x*0.02f) * (closestEnemy.position.x - ship.position.x - velocity.x*0.02f) + (closestEnemy.position.y - ship.position.y - velocity.y*0.02f) * (closestEnemy.position.y - ship.position.y - velocity.y*0.02f);
            if(nextFramePos < closestRange)
                velocitySquared = -velocitySquared;

            if(velocitySquared > targetDistanceSq-closestRange) {
                velocity.y += Math.cos(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
                velocity.x -= Math.sin(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
            }else{
                velocity.y -= Math.cos(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
                velocity.x += Math.sin(Math.toRadians(ship.rotation)) * Stats.acceleration * EngineTools.deltaTime;
            }
        }


        velocity.x -= velocity.x * Stats.speedReductionPerc * EngineTools.deltaTime;
        velocity.y -= velocity.y * Stats.speedReductionPerc * EngineTools.deltaTime;

        velocitySquared = velocity.x*velocity.x + velocity.y*velocity.y;
        if(velocitySquared > Stats.maxSpeedSquared){
            velocity.x = velocity.x * (Stats.maxSpeedSquared/velocitySquared);
            velocity.y = velocity.y * (Stats.maxSpeedSquared/velocitySquared);
        }

        rotationVelocity -= rotationVelocity * Stats.rotReductionPerc * EngineTools.deltaTime;
        rotationVelocity = rotationVelocity > 0 ? rotationVelocity - Stats.rotReductionFlat * EngineTools.deltaTime : rotationVelocity;
        rotationVelocity = rotationVelocity < 0 ? rotationVelocity + Stats.rotReductionFlat * EngineTools.deltaTime : rotationVelocity;

        rotationVelocity = Math.min(rotationVelocity, Stats.maxSpeedRot);
        rotationVelocity = Math.max(rotationVelocity, -Stats.maxSpeedRot);

        ship.rotation += rotationVelocity * EngineTools.deltaTime;

        if(ship.rotation > 360)
            ship.rotation = ship.rotation % 360;
        if(ship.rotation < 0)
            ship.rotation = (ship.rotation + 3600) % 360;

        movedDistance.x = -velocity.x * EngineTools.deltaTime;
        movedDistance.y = -velocity.y * EngineTools.deltaTime;




        float velo = (velocity.x*velocity.x+velocity.y*velocity.y);
        shipParticle.directionSpeed = shipParticleMinSpeed + (shipParticleMaxSpeed-shipParticleMinSpeed)/(Stats.maxSpeedSquared/velo);
        shipParticle.spawnDelayInit = shipParticleSpawnDelayMin - (shipParticleSpawnDelayMin-shipParticleSpawnDelayMax)/(Stats.maxSpeedSquared/velo);

        shipParticle.direction = ship.rotation-90;
        shipParticle.parentPosition.x = ship.position.x;
        shipParticle.parentPosition.y = ship.position.y;

        //Up/Down
        shipParticle.parentPosition.x += -Math.sin(Math.toRadians(ship.rotation)) * -200 * ship.getScale();
        shipParticle.parentPosition.y += Math.cos(Math.toRadians(ship.rotation)) * -200 * ship.getScale();

        if(closestRange < maxShotDistanceSq && shotTimer >= 1f/Stats.firerate){
            shotTimer = 0;
            newShot(closestEnemy);
        }

        if(currentClicks >= Stats.clicksPerShot){
            currentClicks = 0;
            newSpecialShot(closestEnemy);
        }
    }

    private void drawBackground(){
        for (int i = 0; i < background.length; i++) {
            background[i].position.x += movedDistance.x/5;
            background[i].position.y += movedDistance.y/5;
            background[i].draw(mvpMatrix);
        }
    }

    private void drawBossUi(){
        if(!closestEnemy.isBoss)
            return;
        
        if(maxShotDistanceSq >= (closestEnemy.position.x - ship.position.x) * (closestEnemy.position.x - ship.position.x) + (closestEnemy.position.y - ship.position.y) * (closestEnemy.position.y - ship.position.y))
            bossTimer -= EngineTools.deltaTime;
        
        if(bossTimer < 0){
            bossFailed();
            return;
        }

        bossHealthBar.setPercentage((float)(closestEnemy.health/closestEnemy.healthMax));
        bossHealthBar.draw(mvpMatrix);
        FontMaster.draw(mvpMatrix, bossTimeFont, Stats.valueToString(bossTimer));
    }

    private void bossFailed(){
        findClosestEnemy();
        removeEnemies.add(closestEnemy);
    }

    private void bossDefeated(Position position){
        ParticleMaster newParticle = enemyExplosion.clone();
        newParticle.setPositionParent(new Position(position.x, position.y));
        particleMasters.add(newParticle);
        spawnOre(closestEnemy);
        findClosestEnemy();
        bossTimer = 0;
        Stats.zone++;

        // Recalculate enemy Hp
        double newHp = Stats.getEnemyHp();
        for(Enemy enemy : enemies){
            if(enemy.isBoss)
                continue;

            double factor = newHp / enemy.healthMax;
            enemy.health *= factor;
            enemy.healthMax *= factor;
        }
    }

    private void enemyDefeated(Position position){
        ParticleMaster newParticle = enemyExplosion.clone();
        newParticle.setPositionParent(new Position(position.x, position.y));
        particleMasters.add(newParticle);
        spawnOre(closestEnemy);
        findClosestEnemy();
    }

    private void moveRemoveEnemies(){
        for(Enemy enemy : enemies){
            enemy.position.x += movedDistance.x;
            enemy.position.y += movedDistance.y;

            if(enemy.health <= 0 || Math.abs(enemy.position.x - ship.position.x) + Math.abs(enemy.position.y - ship.position.y) > Stats.maxDistance)
                removeEnemies.add(enemy);
                if(enemy.health <= 0){
                    enemy.isDestroyed = true;

                    if(enemy.isBoss)
                        bossDefeated(enemy.position);
                    else
                        enemyDefeated(enemy.position);
            }
            else{
                enemy.draw(mvpMatrix);
            }
        }
    }

    private void shotEnemyCollision(){
        for (Shot shot : shots) {
            shot.position.x += movedDistance.x;
            shot.position.y += movedDistance.y;
            boolean hit = false;

            for(Enemy enemy : enemies){
                if((shot.position.x - enemy.position.x) * (shot.position.x - enemy.position.x) + (shot.position.y - enemy.position.y) * (shot.position.y - enemy.position.y) < shot.texWidth*enemy.texWidth){
                    removeShots.add(shot);
                    hit = true;
                    enemy.health -= shot.dmg;
                    
                    Font font = new Font(.7f, new Position(enemy.position.x, enemy.position.y + enemy.texWidth/3), FontMaster.RESOLUTION_MEDIUM, new Color(.95f,.1f,.1f,1.2f), FontNames.SQUAREFONT);
                    font.text = Stats.valueToString(shot.dmg);
                    font.center = true;
                    font.rotation = random.nextInt(30) - 15;
                    dmgText.add(font);

                    shot.particleExplosion.resetLastPosition();
                    particleMasters.add(shot.particleExplosion);
                }
            }
            if(!hit){
                if(Math.abs(shot.position.x - ship.position.x) + Math.abs(shot.position.y - ship.position.y) > Stats.maxDistance)
                    removeShots.add(shot);
                else{
                    shot.particleTrail.draw(mvpMatrix);
                    shot.draw(mvpMatrix);
                }
            }
        }
    }

    private void moveOres(){
        Position vector = new Position();

        for (Ore ore : ores) {
            ore.position.x += movedDistance.x;
            ore.position.y += movedDistance.y;

            vector.x = ship.position.x - ore.position.x;
            vector.y = ship.position.y - ore.position.y;

            float sqrt = (float) Math.sqrt(vector.x*vector.x + vector.y*vector.y);
            if(sqrt == 0){
                vector.x = 0;
                vector.y = 0;
            }else{
                vector.x /= sqrt;
                vector.y /= sqrt;
            }

            ore.velocity.x *= 1 - 3 * EngineTools.deltaTime;
            ore.velocity.y *= 1 - 3 * EngineTools.deltaTime;

            ore.velocity.x += oreAcceleration * vector.x * EngineTools.deltaTime;
            ore.velocity.y += oreAcceleration * vector.y * EngineTools.deltaTime;


            if((ore.position.x - ship.position.x)*(ore.position.x - ship.position.x) + (ore.position.y - ship.position.y)*(ore.position.y - ship.position.y) <= oreCollectRange){
                removeOres.add(ore);
                Stats.material += ore.value;
            }else{
                ore.draw(mvpMatrix);
            }
        }
    }

    private void spawnEnemy(){
        if(enemies.size() >= maxEnemies)
            return;

        float degree = ((float)random.nextInt(3600) / 10f);
        float posX = ((float)(Math.cos(Math.toRadians(degree)))) * Stats.distance + ship.position.x;
        float posY = ((float)(Math.sin(Math.toRadians(degree)))) * Stats.distance + ship.position.y;

        Enemy newEnemy = enemy.clone(false);
        newEnemy.position.x = posX;
        newEnemy.position.y = posY;
        newEnemy.rotationVelocity = random.nextFloat() * 30 - 15;
        newEnemy.velocity.x = random.nextFloat() * 100 - 50;
        newEnemy.velocity.y = random.nextFloat() * 100 - 50;
        newEnemy.value = Stats.getOreValue();
        newEnemy.healthMax = Stats.getEnemyHp();
        newEnemy.health = newEnemy.healthMax;

        enemies.add(newEnemy);

        findClosestEnemy();
    }

    private void findClosestEnemy(){
        float closestRange = Float.MAX_VALUE;
        float range;
        for(Enemy enemy : enemies){
            if(enemy.isDestroyed)
                continue;

            range = (enemy.position.x - ship.position.x) * (enemy.position.x - ship.position.x) + (enemy.position.y - ship.position.y) * (enemy.position.y - ship.position.y);
            if(range < closestRange){
                closestRange = range;
                closestEnemy = enemy;
            }
        }

        targetDistanceSq = targetEnemyDistance*targetEnemyDistance;
        maxShotDistanceSq = maxEnemyShotDistance*maxEnemyShotDistance;
    }

    public void spawnBoss(){
        if(bossTimer > 0)
            return;

        float degree = ((float)random.nextInt(3600) / 10f);
        float posX = ((float)(Math.cos(Math.toRadians(degree)))) * Stats.distance*1 + ship.position.x;
        float posY = ((float)(Math.sin(Math.toRadians(degree)))) * Stats.distance*1 + ship.position.y;

        Enemy newEnemy = enemy.clone(false);
        newEnemy.position.x = posX;
        newEnemy.position.y = posY;
        newEnemy.rotationVelocity = random.nextFloat() * 30 - 15;
        newEnemy.velocity.x = random.nextFloat() * 100 - 50;
        newEnemy.velocity.y = random.nextFloat() * 100 - 50;
        newEnemy.isBoss = true;
        newEnemy.setScale(1f);
        newEnemy.value = Stats.getOreValue()*10;
        newEnemy.healthMax = Stats.getBossHp();
        newEnemy.health = newEnemy.healthMax;

        enemies.add(newEnemy);

        closestEnemy = newEnemy;

        targetDistanceSq = targetBossDistance*targetBossDistance;
        maxShotDistanceSq = maxBossShotDistance*maxBossShotDistance;

        bossTimer = bossMaxTime;
    }

    boolean left = false;
    private void newShot(Enemy enemy){
        Shot shot = this.shot.clone(false);
        shot.dmg = Stats.dmg;
        shot.position.x = ship.position.x;
        shot.position.y = ship.position.y;

        //Up/Down
        shot.position.x += -Math.sin(Math.toRadians(ship.rotation)) * -10 * ship.getScale();
        shot.position.y += Math.cos(Math.toRadians(ship.rotation)) * -10 * ship.getScale();

        //Left/Right
        if(left){
            shot.position.x -= Math.cos(Math.toRadians(ship.rotation)) * 150 * ship.getScale();
            shot.position.y -= Math.sin(Math.toRadians(ship.rotation)) * 150 * ship.getScale();
        }else{
            shot.position.x += Math.cos(Math.toRadians(ship.rotation)) * 150 * ship.getScale();
            shot.position.y += Math.sin(Math.toRadians(ship.rotation)) * 150 * ship.getScale();
        }
        left = !left;

        float autoDirection = (270 + (float)Math.toDegrees(Math.atan2(enemy.position.y + enemy.velocity.y * 0.1f - shot.position.y, enemy.position.x + enemy.velocity.x * 0.1f - shot.position.x))) % 360;

        shot.velocity.y = (float)Math.cos(Math.toRadians(autoDirection)) * shotVelocityMax;
        shot.velocity.x = -(float)Math.sin(Math.toRadians(autoDirection)) * shotVelocityMax;

        shot.rotation = -90 + (float)Math.toDegrees(Math.atan2(shot.velocity.y,shot.velocity.x));

        shot.particleTrail = shotParticle.clone();
        shot.particleTrail.setPositionParent(shot.position);
        shot.particleTrail.offsetPosPF = movedDistance;

        shot.particleExplosion = shotExplosion.clone();
        shot.particleExplosion.setPositionParent(shot.position);
        shot.particleExplosion.offsetPosPF = movedDistance;

        shots.add(shot);
    }

    private void newSpecialShot(Enemy enemy){
        Shot shot = specialShot.clone(false);
        shot.dmg = Stats.dmg * Stats.specialShotDmgFactor;
        shot.position.x = ship.position.x;
        shot.position.y = ship.position.y;

        //Up/Down
        shot.position.x += -Math.sin(Math.toRadians(ship.rotation)) * -10 * ship.getScale();
        shot.position.y += Math.cos(Math.toRadians(ship.rotation)) * -10 * ship.getScale();

        float autoDirection = (270 + (float)Math.toDegrees(Math.atan2(enemy.position.y + enemy.velocity.y * 0.1f - shot.position.y, enemy.position.x + enemy.velocity.x * 0.1f - shot.position.x))) % 360;

        shot.velocity.y = (float)Math.cos(Math.toRadians(autoDirection)) * specialShotVelocityMax;
        shot.velocity.x = -(float)Math.sin(Math.toRadians(autoDirection)) * specialShotVelocityMax;

        shot.rotation = -90 + (float)Math.toDegrees(Math.atan2(shot.velocity.y,shot.velocity.x));

        shot.particleTrail = specialShotParticle.clone();
        shot.particleTrail.setPositionParent(shot.position);
        shot.particleTrail.offsetPosPF = movedDistance;

        shot.particleExplosion = specialShotExplosion.clone();
        shot.particleExplosion.setPositionParent(shot.position);
        shot.particleExplosion.offsetPosPF = movedDistance;

        shots.add(shot);
    }

    private void spawnOre(Enemy enemy){
        int a;
        if(enemy.isBoss){
            a = random.nextInt(7) + 5;
        }else{
            a = random.nextInt(3) + 1;
        }
        
        double value = enemy.value/a;

        for(int i = 0; i < a; i++){
            Ore ore = oreMaster.clone();
            ore.value = value;
            ore.position.x = enemy.position.x;
            ore.position.y = enemy.position.y;
            ore.trail.setParentPosition(ore.position);
            ore.rotation = -ship.rotation + random.nextInt(180) - 90;
            ore.rotationVelocity = oreRotationVelocity + random.nextFloat() * oreRotationVelocityRange * 2 - oreRotationVelocityRange;

            ore.velocity.x = 1000 * (float)Math.sin(Math.toRadians(ore.rotation));
            ore.velocity.y = 1000 * (float)Math.cos(Math.toRadians(ore.rotation));

            switch(random.nextInt(5)){
                case 0:
                    ore.texturePointer[0] = oreIronTexturePointer;
                    ore.trail.color = oreIronTrail;
                    break;

                case 1:
                    ore.texturePointer[0] = oreGoldTexturePointer;
                    ore.trail.color = oreGoldTrail;
                    break;

                case 2:
                    ore.texturePointer[0] = oreSapphireTexturePointer;
                    ore.trail.color = oreSapphireTrail;
                    break;

                case 3:
                    ore.texturePointer[0] = oreEmeraldTexturePointer;
                    ore.trail.color = oreEmeraldTrail;
                    break;

                case 4:
                    ore.texturePointer[0] = oreRubyTexturePointer;
                    ore.trail.color = oreRubyTrail;
                    break;
            }



            ores.add(ore);
        }
    }

    @Override
    public void touched(float x, float y, boolean clickDown) {
        if(clickDown && !ScreenManager.shop.shopVisible)
            currentClicks++;

        shotLoading.setPercentage((float)currentClicks/Stats.clicksPerShot);
    }

    @Override
    public void touchMove(float startX, float startY, float currentX, float currentY) {

    }
}