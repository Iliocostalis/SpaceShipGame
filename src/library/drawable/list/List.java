package library.drawable.list;

import java.util.ArrayList;

public class List {

    private float space;
    public float posYOffset;
    public float topPosY;
    public float bottomPosY;
    private float listHeight;
    public float getListHeight(){
        return listHeight;
    }
    private float currentOffsetY;

    public ArrayList<IElement> elements = new ArrayList<>();

    public void addElement(IElement element){
        elements.add(element);
    }

    public List(float topPosY, float bottomPosY, float space){
        this.topPosY = topPosY;
        this.bottomPosY = bottomPosY;
        this.space = space;
    }

    public void draw(float[] mvpMatrix){
        currentOffsetY = topPosY + posYOffset;

        for (IElement e : elements) {
            e.draw(mvpMatrix, currentOffsetY);
            currentOffsetY -= e.getHeight() + space;
        }

        listHeight = topPosY + posYOffset - currentOffsetY;
    }

    public void checkClick(float x, float y, boolean clickDown){
        currentOffsetY = topPosY + posYOffset;

        for (IElement e : elements) {
            e.checkClick(currentOffsetY,x,y,clickDown);
            currentOffsetY -= e.getHeight() + space;
        }
    }
}
