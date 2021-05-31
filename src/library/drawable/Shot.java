package library.drawable;

import library.EngineTools;
import library.addons.Position;

public class Shot extends Sprite{

    public Position velocity = new Position();
    public double dmg;
    private float time;
    private float frameTime;
    public boolean destroyed = false;
    public ParticleMaster particleTrail;
    public ParticleMaster particleExplosion;

    public Shot(String assetsPictureName, int columns, int rows) {
        super(assetsPictureName, columns, rows);
    }

    protected Shot(){

    }

    @Override
    protected void preDraw() {
        position.x += velocity.x * EngineTools.deltaTime;
        position.y += velocity.y * EngineTools.deltaTime;
        time += EngineTools.deltaTime;
        if(time >= frameTime){
            time -= frameTime;
            aniIndexImage = (aniIndexImage + 1) % texturePointer.length;
            
        }
    }

    public Shot clone(boolean exactly){
        Shot shot = new Shot();
        if(exactly){
            shot.spriteCoords[0] = spriteCoords[0];
            shot.spriteCoords[1] = spriteCoords[1];
            shot.spriteCoords[2] = spriteCoords[2];
            shot.spriteCoords[3] = spriteCoords[3];
            shot.spriteCoords[4] = spriteCoords[4];
            shot.spriteCoords[5] = spriteCoords[5];
            shot.spriteCoords[6] = spriteCoords[6];
            shot.spriteCoords[7] = spriteCoords[7];

            shot.loadVertex();

            shot.texHeight = texHeight;
            shot.texWidth = texWidth;

            shot.texturePointer = texturePointer.clone();
            shot.aniIndexImage = aniIndexImage;
        
            shot.position.x = position.x;
            shot.position.y = position.y;
            shot.rotation = rotation;
            shot.color = color;

            shot.scale = scale;
            shot.scaleX = scaleX;
            shot.scaleY = scaleY;

            shot.dmg = dmg;
        }else {
            shot.spriteCoords[0] = spriteCoords[0];
            shot.spriteCoords[1] = spriteCoords[1];
            shot.spriteCoords[2] = spriteCoords[2];
            shot.spriteCoords[3] = spriteCoords[3];
            shot.spriteCoords[4] = spriteCoords[4];
            shot.spriteCoords[5] = spriteCoords[5];
            shot.spriteCoords[6] = spriteCoords[6];
            shot.spriteCoords[7] = spriteCoords[7];

            shot.loadVertex();

            shot.texHeight = texHeight;
            shot.texWidth = texWidth;

            shot.texturePointer = texturePointer.clone();

            shot.scale = scale;
            shot.scaleX = scaleX;
            shot.scaleY = scaleY;

            shot.dmg = dmg;
        }

        return shot;
    }   
}