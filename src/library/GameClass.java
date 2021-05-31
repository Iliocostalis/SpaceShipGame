package library;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import library.screens.Screen;
import library.screens.ScreenManager;

public class GameClass {

    private static boolean touched = false;
    private static float touchedX, touchedY;

    private static boolean touchJustEnded = false;
    private static float touchEndedX, touchEndedY;

    private static boolean touchMoved = false;

    public static float scrollYoffset;

    public static void onCreate(){
        ScreenManager.loadScreens();

        ScreenManager.mainScreen.isVisible = true;
        ScreenManager.shop.isVisible = true;
        //ScreenManager.testScreen.isVisible = true;
    }

    public static void onDraw(float[] vpMatrix, float cursorPosX, float cursorPosY){
        checkTouch(cursorPosX, cursorPosY);
        EngineTools.cursorPosX = cursorPosX;
        EngineTools.cursorPosY = cursorPosY;

        for (Screen screen : ScreenManager.screens) {
            if(screen.isVisible)
                screen.onDraw(vpMatrix);
        }
    }

    private static void checkTouch(float cursorPosX, float cursorPosY){
        if(touched){
            for (Screen screen : ScreenManager.screens) {
                if(!screen.isVisible)
                    continue;
                screen.touched(touchedX,touchedY,true);
            }
            touched = false;
        }else if(touchJustEnded){
            for (Screen screen : ScreenManager.screens) {
                if(!screen.isVisible)
                    continue;
                screen.touched(touchEndedX,touchEndedY,false);
            }
            touchJustEnded = false;
        }else if(touchMoved){
            for (Screen screen : ScreenManager.screens) {
                if(!screen.isVisible)
                    continue;
                screen.touchMove(touchedX, touchedY, cursorPosX, cursorPosY);
            }
        }
    }

    public static void onTouch(float x, float y, int buttonId, int action){
    	if (buttonId == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
			
		}
		if (buttonId == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE) {
			
		}
		if (buttonId == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
			
			touchedX = x;
            touchedY = y;
            touched = true;
            touchMoved = true;
		}
		if (buttonId == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
			touchEndedX = x;
            touchEndedY = y;
            
            touchJustEnded = true;
            touchMoved = false;
		}
    }

    public static void keyPress(int keyId, boolean down) {
        
    }
}