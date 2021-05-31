package library.drawable;

public class Border{

    public static Sprite border;
    public static float scale;
    
    public static void loadBorder(String assetsPictureName, int columns, int rows, float scale){
        border = new Sprite(assetsPictureName, columns, rows);
        border.setScale(scale);
        Border.scale = scale;
    }

    public static void drawBorder(float[] mvpMatrix, float centerX, float centerY, float halfWidth, float halfHeight) {
        float halfBorderSize = border.texWidth/2;

        border.setAnimationImageIndex(0);
        border.position.x = centerX - halfWidth - halfBorderSize;
        border.position.y = centerY + halfHeight + halfBorderSize;
        border.draw(mvpMatrix);
        border.setAnimationImageIndex(2);
        border.position.x = centerX + halfWidth + halfBorderSize;
        border.draw(mvpMatrix);
        border.setAnimationImageIndex(8);
        border.position.y = centerY - halfHeight - halfBorderSize;
        border.draw(mvpMatrix);
        border.setAnimationImageIndex(6);
        border.position.x = centerX - halfWidth - halfBorderSize;
        border.draw(mvpMatrix);
    
        border.setAnimationImageIndex(1);
        border.position.x = centerX;
        border.position.y = centerY + halfHeight + halfBorderSize;
        border.setScaleX((halfWidth) / (halfBorderSize));
        border.draw(mvpMatrix);
        border.setAnimationImageIndex(7);
        border.position.y = centerY - halfHeight - halfBorderSize;
        border.draw(mvpMatrix);
    
        border.setAnimationImageIndex(5);
        border.position.x = centerX + halfWidth + halfBorderSize;
        border.position.y = centerY;
        border.setScaleX(1);
        border.setScaleY(halfHeight / halfBorderSize);
        border.draw(mvpMatrix);
        border.setAnimationImageIndex(3);
        border.position.x = centerX - halfWidth - halfBorderSize;
        border.draw(mvpMatrix);
        border.setScaleY(1);
    }
}