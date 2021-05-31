package library.addons;

import java.io.IOException;
import java.io.InputStream;

import library.EngineTools;

public class AnimationLoader {
    
    static InputStream is;
    static float[] timesBuffer;
    static float[] valueBuffer;
    static float buffer;
    static byte[] wordBuffer = new byte[4];

    public static Animation loadAnimation(String animationName){
        Animation animation = new Animation();
        
        try {
            is = EngineTools.mClass.getResourceAsStream("/assets/animation/" + animationName);
            
            is.read(wordBuffer);
            animation.animLength = Float.intBitsToFloat(EngineTools.byteArrayToInt(wordBuffer));
            animation.looping = is.read() == 1;
            animation.reverce = is.read() == 1;

            animation.hasPosYAnim = is.read() == 1;
            if(animation.hasPosYAnim){
                loadBlock(is);
                animation.timesPosY = timesBuffer;
                animation.valuePosY = valueBuffer;
            }
                

            animation.hasPosXAnim = is.read() == 1;
            if (animation.hasPosXAnim){
            	loadBlock(is);
                animation.timesPosX = timesBuffer;
                animation.valuePosX = valueBuffer;
            }
                

            animation.hasScaleAnim = is.read() == 1;
            if (animation.hasScaleAnim){
            	loadBlock(is);
                animation.timesScale = timesBuffer;
                animation.valueScale = valueBuffer;
            }
                

            animation.hasScaleXAnim = is.read() == 1;
            if (animation.hasScaleXAnim){
            	loadBlock(is);
                animation.timesScaleX = timesBuffer;
                animation.valueScaleX = valueBuffer;
            }


            animation.hasScaleYAnim = is.read() == 1;
            if (animation.hasScaleYAnim){
            	loadBlock(is);
                animation.timesScaleY = timesBuffer;
                animation.valueScaleY = valueBuffer;
            }


            animation.hasAlphaAnim = is.read() == 1;
            if (animation.hasAlphaAnim){
            	loadBlock(is);
                animation.timesAlpha = timesBuffer;
                animation.valueAlpha = valueBuffer;
            }
                

            animation.hasRotAnim = is.read() == 1;
            if (animation.hasRotAnim){
            	loadBlock(is);
                animation.timesRot = timesBuffer;
                animation.valueRot = valueBuffer;
            }
                
            is.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animation;
    }

    private static void loadBlock(InputStream is) throws IOException {
    	is.read(wordBuffer);
        int timesLength = EngineTools.byteArrayToInt(wordBuffer);
        is.read(wordBuffer);
        int valueLength = EngineTools.byteArrayToInt(wordBuffer);

        timesBuffer = new float[timesLength];
        valueBuffer = new float[valueLength];

        for (int i = 0; i < timesLength; i++) {
        	is.read(wordBuffer);
            timesBuffer[i] = Float.intBitsToFloat(EngineTools.byteArrayToInt(wordBuffer));
        }
        for (int i = 0; i < valueLength; i++) {
        	is.read(wordBuffer);
            valueBuffer[i] = Float.intBitsToFloat(EngineTools.byteArrayToInt(wordBuffer));
        }
    }
}