package library.drawable.list;

public class SpaceElement implements IElement{

    float space;
    
    public SpaceElement(float space){
        this.space = space;
    }

    
    @Override
    public void draw(float[] mvpMatrix, float currentOffsetY) {
    }

    @Override
    public float getHeight() {
        return space;
    }

    @Override
    public void checkClick(float currentOffsetY, float x, float y, boolean clickDown) {
    }
    
}