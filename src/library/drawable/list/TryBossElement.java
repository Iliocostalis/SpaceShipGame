package library.drawable.list;

import library.EngineTools;
import library.addons.Color;
import library.addons.Position;
import library.addons.fonts.*;
import library.drawable.Sprite;
import library.screens.ScreenManager;
import library.screens.Stats;

public class TryBossElement implements IElement{

    static boolean isReady;
    static Sprite button;
    static float space;
    static Font font;
    final int zone;
    boolean isClickedDown;

    public TryBossElement(int zone){
        if(!isReady){
            isReady = true;
            button = new Sprite("tryBoss.png", 2,1);
            button.position.x = EngineTools.screenWidth/2f;

            font = new Font(.9f, new Position(button.position.x,0), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT, "Fight Boss");
            font.center = true;
        }
        this.zone = zone;
    }

	@Override
	public void draw(float[] mvpMatrix, float currentOffsetY) {
        if(zone != Stats.zone)
            return;

        button.position.y = currentOffsetY - button.texHeight/2;
        font.position.y = button.position.y + 7;

        if(isClickedDown){
            font.position.y -= 12;
            button.setAnimationImageIndex(1);
        }else
            button.setAnimationImageIndex(0);
            

        button.draw(mvpMatrix);
        FontMaster.draw(mvpMatrix, font);
	}

	@Override
	public float getHeight() {
        if(zone != Stats.zone)
            return 0;
        else
		    return button.texHeight + space;
	}

    private void onClick(){
        ScreenManager.mainScreen.spawnBoss();
    }

	@Override
	public void checkClick(float currentOffsetY, float x, float y, boolean clickDown) {
        if(zone != Stats.zone)
            return;

        button.position.y = currentOffsetY - button.texHeight/2;
        if(button.position.x + button.texWidth/2 > x && button.position.x - button.texWidth/2 < x &&
            button.position.y + button.texHeight/2 > y && button.position.y - button.texHeight/2 < y){

            if(clickDown){
                isClickedDown = true;
            }
            if(!clickDown && isClickedDown) {
                onClick();
                isClickedDown = false;
            }
        }else if(!clickDown){
            isClickedDown = false;
        }
	}
}