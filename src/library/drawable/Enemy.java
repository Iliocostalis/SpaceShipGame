package library.drawable;

import library.EngineTools;
import library.addons.Color;
import library.addons.Position;

public class Enemy extends Sprite {

    public Position velocity = new Position();
    public boolean isBoss;
    public double healthMax;
    public double health;
    public float rotationVelocity;
    private float time;
    private float frameTime;
    public boolean isDestroyed = false;
    public ParticleMaster particleController;
    public double value;
    public HealthBar healthBar = new HealthBar(130,30,5,new Color(.7f,.7f,.7f,1), new Color(1,0,0,1), new Color(0,0,0,0));

    public Enemy(String assetsPictureName, int columns, int rows) {
        super(assetsPictureName, columns, rows);
    }

    protected Enemy(){

    }

    @Override
    protected void preDraw() {
        position.x += velocity.x * EngineTools.deltaTime;
        position.y += velocity.y * EngineTools.deltaTime;

        rotation += rotationVelocity * EngineTools.deltaTime;
        if(rotation > 360)
            rotation -= 360;
        else if(rotation < -360)
            rotation += 360;

        time += EngineTools.deltaTime;
        if(time >= frameTime){
            time -= frameTime;
            aniIndexImage = (aniIndexImage + 1) % texturePointer.length;
            
        }

        if(health < healthMax && !isBoss){
            healthBar.setPercentage((float)(health/healthMax));
            healthBar.position.x = position.x;
            healthBar.position.y = position.y + 100;
            healthBar.draw(mvpMatrix);
        }
    }

    public Enemy clone(boolean exactly){
        Enemy enemy = new Enemy();
        if(exactly){
            enemy.spriteCoords[0] = spriteCoords[0];
            enemy.spriteCoords[1] = spriteCoords[1];
            enemy.spriteCoords[2] = spriteCoords[2];
            enemy.spriteCoords[3] = spriteCoords[3];
            enemy.spriteCoords[4] = spriteCoords[4];
            enemy.spriteCoords[5] = spriteCoords[5];
            enemy.spriteCoords[6] = spriteCoords[6];
            enemy.spriteCoords[7] = spriteCoords[7];

            enemy.loadVertex();

            enemy.texHeight = texHeight;
            enemy.texWidth = texWidth;

            enemy.texturePointer = texturePointer.clone();
            enemy.aniIndexImage = aniIndexImage;
        
            enemy.position.x = position.x;
            enemy.position.y = position.y;
            enemy.rotation = rotation;
            enemy.color = color;

            enemy.scale = scale;
            enemy.scaleX = scaleX;
            enemy.scaleY = scaleY;

            enemy.health = healthMax;
            enemy.healthMax = healthMax;
        }else {
            enemy.spriteCoords[0] = spriteCoords[0];
            enemy.spriteCoords[1] = spriteCoords[1];
            enemy.spriteCoords[2] = spriteCoords[2];
            enemy.spriteCoords[3] = spriteCoords[3];
            enemy.spriteCoords[4] = spriteCoords[4];
            enemy.spriteCoords[5] = spriteCoords[5];
            enemy.spriteCoords[6] = spriteCoords[6];
            enemy.spriteCoords[7] = spriteCoords[7];

            enemy.loadVertex();

            enemy.texHeight = texHeight;
            enemy.texWidth = texWidth;

            enemy.texturePointer = texturePointer.clone();

            enemy.scale = scale;
            enemy.scaleX = scaleX;
            enemy.scaleY = scaleY;
        }

        return enemy;
    }   
}