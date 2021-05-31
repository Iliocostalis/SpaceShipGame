package library.drawable.list;

public interface IElement {
    void draw(float[] mvpMatrix, float currentOffsetY);
    float getHeight();
    void checkClick(float currentOffsetY, float x, float y, boolean clickDown);
}