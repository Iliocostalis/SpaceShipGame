package library.addons;

public class Color {

    public float r;
    public float g;
    public float b;
    public float a;

    public Color(){
        r = 1f;
        g = 1f;
        b = 1f;
        a = 1f;
    }

    public Color(int hex){
        r = ((float)(hex >>> 24 & 0xff))/255f;
        g = ((float)(hex >>> 16 & 0xff))/255f;
        b = ((float)(hex >>> 8 & 0xff))/255f;
        a = ((float)(hex & 0xff))/255f;
    }

    public Color(float r,float g,float b,float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}