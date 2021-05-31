package library.addons.fonts;

import library.addons.Color;
import library.addons.Position;

public class Font {
    public Color color;
    public float scale;
    public Position position;
    public int resolution;
    public FontNames name;
    public boolean center;
    public Position endPos = new Position();
    public float rotation;
    public String text;
    

    public Font(){
        color = new Color();
        scale = 1f;
        position = new Position();
        resolution = FontMaster.RESOLUTION_MEDIUM;
        name = FontNames.values()[0];
    }

    public Font(Color color){
        this.color = color;
        scale = 1f;
        position = new Position();
        resolution = FontMaster.RESOLUTION_MEDIUM;
        name = FontNames.values()[0];
    }

    public Font(float scale, Position position, int resolution, Color color, FontNames name){
        this.color = color;
        this.scale = scale;
        this.position = position;
        this.resolution = resolution;
        this.name = name;
    }

    public Font(float scale, Position position, int resolution, Color color, FontNames name, String text){
        this.color = color;
        this.scale = scale;
        this.position = position;
        this.resolution = resolution;
        this.name = name;
        this.text = text;
    }
}