package library.drawable.list;

import library.addons.Color;
import library.addons.Position;
import library.addons.fonts.Font;
import library.addons.fonts.FontMaster;
import library.addons.fonts.FontNames;

public class DescriptionElement implements IElement {

    static boolean isReady = false;
    static Font font;

    float fontScale;
    float height;
    String description;
    float positionX;
    boolean center;

    public DescriptionElement(String description, float height, float fontScale, float positionX, boolean center){
        if(!isReady){
            isReady = true;
            font = new Font(1f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
        }
        this.description = description;
        this.height = height;
        this.fontScale = fontScale;
        this.positionX = positionX;
        this.center = center;
    }

    @Override
    public void draw(float[] mvpMatrix, float currentOffsetY) {
        font.position.x = positionX;
        font.position.y = currentOffsetY - height/2;
        font.scale = fontScale;
        font.center = center;
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