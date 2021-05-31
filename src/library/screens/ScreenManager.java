package library.screens;

import java.util.ArrayList;

public class ScreenManager{

    public static final int MAINSCREEN = 1;



    public static ArrayList<Screen> screens = new ArrayList<>();

    public static MainScreen mainScreen;
    public static Shop shop;
    
    public static void loadScreens(){
    	mainScreen = new MainScreen();
        screens.add(mainScreen);
        
        shop = new Shop();
        screens.add(shop);
    }

    public static void loadScreen(int id){
        switch (id){
            case MAINSCREEN:
            	mainScreen = new MainScreen();
                screens.add(mainScreen);
                break;

        }
    }
}
