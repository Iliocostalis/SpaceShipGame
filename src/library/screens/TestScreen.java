package library.screens;

import library.addons.Position;
import library.addons.audio.*;
import library.drawable.UIClickable;

public class TestScreen extends Screen {

    UIClickable uc;

    public TestScreen() { onCreate(); }

    @Override
    public void onCreate() {
        final int sound = AudioMaster.loadSound("test.wav");

        uc = new UIClickable("oreIron.png", false);
        uc.onClickListener = () -> {AudioMaster.getNewSource().play(sound);};
        uc.position = new Position(500, 1000);
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        uc.draw(mvpMatrix);
    }

    @Override
    public void touched(float x, float y, boolean clickDown) {
        uc.checkClick(x, y, clickDown);
    }

    @Override
    public void touchMove(float startX, float startY, float currentX, float currentY) {

    }
    
}