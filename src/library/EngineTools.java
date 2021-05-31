package library;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL20;

import library.addons.Position;
import library.addons.audio.AudioMaster;
import library.addons.fonts.FontMaster;
import library.screens.Stats;

public class EngineTools {

    public static float deltaTime = 0;
    public static Class<? extends MainClass> mClass;

    public static int windowWidth;
    public static int windowHeight;
    
    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static int screenWidthLast = 0;
    public static int screenHeightLast = 0;
    public static int widthCalc = 1080;
    public static int heightCalc = 1920;
    public static boolean screenChanged = false;

    public static float cursorPosX, cursorPosY;

    private static List<String> textureNames = new ArrayList<>();
    private static List<int[]> texturePointers = new ArrayList<>();
    private static List<Integer> textureColumns = new ArrayList<>();
    private static List<Integer> textureRows = new ArrayList<>();

    public static void initialise(){
        FrameBuffer.initialize();

        FontMaster.initialize();
        
        /** Load Shader */
        Shader.initialize();

        AudioMaster.initialize();


        Stats.initalize();

        
        GameClass.onCreate();
    }

    public static int[] loadGLTexture(String assetsPictureName, int columns, int rows){
        // check if Bitmap was already loaded
    	if(textureNames != null && textureNames.size() > 0)
            for(int i = 0; i < textureNames.size(); i++){
                if(textureNames.get(i).equals(assetsPictureName)){
                	if(textureColumns.get(i) == columns && textureRows.get(i) == rows)
                		return texturePointers.get(i);
                }
            }


    	int[] data = null;
        File imgPath = new File("src/assets/" + assetsPictureName);
        BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(imgPath);

			data = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[]texturePointer = new int[columns*rows];
		
        // Calculate finished Bitmap size
        int sBMWidth = bufferedImage.getWidth() / columns;
        int sBMHeight = bufferedImage.getHeight() / rows;

        
        // generate one/more texture pointer
        //GLES20.glGenTextures(columns*rows, texturePointer, 0);
        GL20.glGenTextures(texturePointer);

        int[] buffer = new int[sBMWidth*sBMHeight];

        // Break Bitmap in smaller Peaces used for animation
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){
                if(rows == 1 && columns == 1)
                	buffer = data;
                else {
                	for(int y = 0; y < sBMHeight; y++) {
                		for(int x = 0; x < sBMWidth; x++) {
                			buffer[x + sBMWidth * y] = data[x + bufferedImage.getWidth() * y + r*sBMHeight*bufferedImage.getWidth() + c*sBMWidth];
                        }
                	}
                }
                    
                // bind it to our array
                GL20.glBindTexture(GL20.GL_TEXTURE_2D, texturePointer[r*columns + c]);

                // apply texture filter
                GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
                GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
                GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP);
                GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP);

                // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
                GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, sBMWidth, sBMHeight, 0, GL20.GL_BGRA, GL20.GL_UNSIGNED_BYTE, buffer);
    	        
            }
        }

        int[] texturePointerCombined = new int[columns*rows + 2];
        texturePointerCombined[0] = sBMWidth;
        texturePointerCombined[1] = sBMHeight;

        for (int i = 0; i < texturePointer.length; i++) {
            texturePointerCombined[i + 2] = texturePointer[i];
        }
        
        
        // Add to list
        texturePointers.add(texturePointerCombined);
        textureNames.add(assetsPictureName);
        textureColumns.add(columns);
        textureRows.add(rows);
        
        return texturePointerCombined;
    }

    public static int loadGLTexture(String assetsPictureName){
        
    	int[] data = null;
        File imgPath = new File("src/assets/" + assetsPictureName);
        BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(imgPath);

			data = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] texturePointer = new int[1];
        
        // generate one/more texture pointer
        //GLES20.glGenTextures(columns*rows, texturePointer, 0);
        GL20.glGenTextures(texturePointer);

        // bind it to our array
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texturePointer[0]);

        // apply texture filter
        GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
        GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP);
        GL20.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP);

        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL20.GL_BGRA, GL20.GL_UNSIGNED_BYTE, data);

        return texturePointer[0];
    }
    
    public static byte[] intToByteArray(int value){
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value};
    }

    public static int byteArrayToInt(byte[] bytes){
        return (((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF)));
    }

    public static int byteArrayToInt(byte[] bytes, int startOffset){
        return (((bytes[startOffset] & 0xFF) << 24) |
                ((bytes[startOffset + 1] & 0xFF) << 16) |
                ((bytes[startOffset + 2] & 0xFF) << 8) |
                ((bytes[startOffset + 3] & 0xFF)));
    }

    public static void clearList(){
        texturePointers.clear();
        textureNames.clear();
        textureColumns.clear();
        textureRows.clear();
    }

    public static void matrixRotPosScale(float[] result, float[] viewMatrix, float rotation, float posX, float posY, float scaleX, float scaleY) {
        
        rotation *= (float) (Math.PI / 180.0f);
        float s = (float) Math.sin(rotation);
        float c = (float) Math.cos(rotation);

        result[0] = c * viewMatrix[0] * scaleX;
        result[1] = s * viewMatrix[5] * scaleX;

        result[4] = -s * viewMatrix[0] * scaleY;
        result[5] = c * viewMatrix[5] * scaleY;

        result[8] = viewMatrix[8];
        result[9] = viewMatrix[9];
        result[10] = viewMatrix[10];
        result[11] = viewMatrix[11];

        result[12] = viewMatrix[0] * posX + viewMatrix[12];
        result[13] = viewMatrix[5] * posY + viewMatrix[13];
        result[14] = viewMatrix[14];
        result[15] = viewMatrix[15];
    }

    public static boolean isClickInside(Position position, float widhtHalf, float heightHalf, float x, float y){
        return position.x + widhtHalf > x && position.x - widhtHalf < x &&
                position.y + heightHalf > y && position.y - heightHalf < y;
    }

    private static float oneMinusP;
    public static float curve(float a, float b, float c, float d, float x){
        oneMinusP = 1-x;
        return (a * oneMinusP * oneMinusP * oneMinusP) + 
                (b * 3.0f * oneMinusP * oneMinusP * x) +
                (c * 3.0f * oneMinusP * x * x) +
                (d * x * x * x);
    }
    private static Position e,f,g,h,j;
    public static Position bezier(float percentage, Position a, Position b, Position c, Position d){
        // Next 3 Points
        e.x = a.x + ( b.x - a.x) * percentage;
        e.y = a.y + ( b.y - a.y) * percentage;

        f.x = b.x + ( c.x - b.x) * percentage;
        f.y = b.y + ( c.y - b.y) * percentage;

        g.x = c.x + ( d.x - c.x) * percentage;
        g.y = c.y + ( d.y - c.y) * percentage;

        // Next 2 Points
        h.x = e.x + ( f.x - e.x) * percentage;
        h.y = e.y + ( f.y - e.y) * percentage;

        j.x = f.x + ( g.x - f.x) * percentage;
        j.y = f.y + ( g.y - f.y) * percentage;

        // Last Point
        return new Position(h.x + ( j.x - h.x) * percentage, h.x + ( j.x - h.x) * percentage);
    }
}