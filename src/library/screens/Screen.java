package library.screens;

public abstract class Screen{

    public boolean isVisible = false;

    public abstract void onCreate();

    public abstract void onDraw(float[] mvpMatrix);

    public abstract void touched(float x, float y, boolean clickDown);

    public abstract void touchMove(float startX, float startY, float currentX, float currentY);
}