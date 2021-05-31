package library.drawable;

import library.EngineTools;
import library.addons.Position;
import library.screens.MainScreen;

public class Ore extends Sprite {

    public Position velocity = new Position();
    public double value;
    public float rotationVelocity;
    public Trail trail;

    protected Ore(){

    }

    public Ore(String assetsPictureName, int columns, int rows){
        super(assetsPictureName, columns, rows);
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

        for(int i = 0; i < trail.pos.length; i++){
            trail.pos[i].x += MainScreen.movedDistance.x;
            trail.pos[i].y += MainScreen.movedDistance.y;
        }
        trail.draw(mvpMatrix);
    }

    @Override
    public Ore clone(){
        Ore ore = new Ore();

        ore.spriteCoords[0] = spriteCoords[0];
        ore.spriteCoords[1] = spriteCoords[1];
        ore.spriteCoords[2] = spriteCoords[2];
        ore.spriteCoords[3] = spriteCoords[3];
        ore.spriteCoords[4] = spriteCoords[4];
        ore.spriteCoords[5] = spriteCoords[5];
        ore.spriteCoords[6] = spriteCoords[6];
        ore.spriteCoords[7] = spriteCoords[7];

        ore.loadVertex();

        ore.texHeight = texHeight;
        ore.texWidth = texWidth;

        ore.texturePointer = texturePointer.clone();

        ore.scale = scale;
        ore.scaleX = scaleX;
        ore.scaleY = scaleY;

        ore.trail = trail.clone();

        return ore;
    }

}