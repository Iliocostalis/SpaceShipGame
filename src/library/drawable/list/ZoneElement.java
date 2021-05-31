package library.drawable.list;

import library.addons.Color;
import library.addons.Position;
import library.addons.fonts.*;

public class ZoneElement implements IElement {

    static boolean isReady = false;
    static Font font;
    static final float fontScale = .7f;
    static final float height = 80;
    static final float positionX = 20;

    String description;

    public ZoneElement(int zone){
        if(!isReady){
            isReady = true;
            font = new Font(fontScale, new Position(positionX,0), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
        }
        description = "Zone " + (zone+1);
    }
    
    @Override
    public void draw(float[] mvpMatrix, float currentOffsetY) {
        font.position.y = currentOffsetY - height/2;
        FontMaster.draw(mvpMatrix, font, description);
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void checkClick(float currentOffsetY, float x, float y, boolean clickDown) {
        
    }
}