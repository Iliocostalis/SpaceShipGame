package library.drawable.list;

import library.*;
import library.addons.*;
import library.addons.fonts.*;
import library.screens.Stats;
import library.drawable.*;

public class ListElement implements IElement{
    public static Position boxOffset = new Position();
    public static Position buyOffset = new Position();
    public static Position iconOffset = new Position();
    public static Position titleOffset = new Position();
    public static Position priceOffset = new Position();
    public static Position statsOffset = new Position();
    public static Position statsInfoOffset = new Position();
    public static Position progressOffset = new Position();

    static Sprite buy;
    static Sprite box;
    static Sprite icon;
    static Sprite grayOverlay;
    static Font fontTitle;
    static Font fontPrice;
    static Font fontStats;
    static Font fontStatsInfo;
    static HealthBar progress;

    static boolean isReady;


    public boolean isClickedDown;
    public String title;
    public String price;
    public String stats;
    public String statsInfo;
    public int[] texturePointer = new int[1];
    public Upgrade upgrade;
    public int minZone;

    public ListElement(String title, Upgrade upgrade, int texturePointer, int minZone){
        if(!isReady){
            isReady = true;
            buy = new Sprite("buy.png",2,1);
            box = new Sprite("list.png",1,1);
            icon = new Sprite("dmgIcon.png",1,1);
            grayOverlay = new Sprite("white.png",1,1);
            grayOverlay.color = new Color(0, 0, 0, .5f);
            grayOverlay.setScaleX(box.texWidth);
            grayOverlay.setScaleY(box.texHeight);
            fontTitle = new Font(.7f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
            fontPrice = new Font(.6f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
            fontPrice.center = true;
            fontStats = new Font(.55f, new Position(), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
            fontStatsInfo = new Font(1f, new Position(), FontMaster.RESOLUTION_LOW, new Color(), FontNames.SQUAREFONT);
            progress = new HealthBar(400, 24, 6, new Color(.2f,.2f,.2f,1f), new Color(.2f,.8f,.2f,1f), new Color(.4f,.4f,.4f,1f));

            boxOffset.x = EngineTools.screenWidth/2f;
            buyOffset.x = EngineTools.screenWidth - buy.texWidth/2 - (box.texHeight-buy.texHeight-20)/2;
            iconOffset.x = icon.texWidth/2 + (box.texHeight-icon.texHeight-20)/2;
            titleOffset.x = 7 + icon.texWidth + 40;
            priceOffset.x = buyOffset.x;
            statsOffset.x = titleOffset.x;
            progressOffset.x = titleOffset.x + progress.width/2;
            statsInfoOffset.x = titleOffset.x + progress.width + 20;

            boxOffset.y = -box.texHeight/2;
            buyOffset.y = boxOffset.y;
            iconOffset.y = boxOffset.y;

            titleOffset.y = boxOffset.y + 60;
            priceOffset.y = boxOffset.y + 6;
            statsOffset.y = boxOffset.y - 20;

            progressOffset.y = boxOffset.y - 80;
            statsInfoOffset.y = boxOffset.y - 80;

            box.position.x = boxOffset.x;
            buy.position.x = buyOffset.x;
            icon.position.x = iconOffset.x;
            fontTitle.position.x = titleOffset.x;
            fontPrice.position.x = priceOffset.x;
            fontStats.position.x = statsOffset.x;
            fontStatsInfo.position.x = statsInfoOffset.x;
            progress.position.x = progressOffset.x;
            grayOverlay.position.x = boxOffset.x;
        }

        this.title = title;
        this.upgrade = upgrade;
        this.texturePointer[0] = texturePointer;
        this.minZone = minZone;

        setText();
    }

    @Override
    public float getHeight(){
        return box.texHeight;
    }

    @Override
    public void draw(float[] mvpMatrix, float currentOffsetY){
        box.position.y = boxOffset.y + currentOffsetY;
        buy.position.y = buyOffset.y + currentOffsetY;
        icon.position.y = iconOffset.y + currentOffsetY;
        fontTitle.position.y = titleOffset.y + currentOffsetY;
        fontPrice.position.y = priceOffset.y + currentOffsetY;
        fontStats.position.y = statsOffset.y + currentOffsetY;
        fontStatsInfo.position.y = statsInfoOffset.y + currentOffsetY;
        progress.position.y = progressOffset.y + currentOffsetY;


        box.draw(mvpMatrix);

        if(!upgrade.isMaxed){
            if(upgrade.price > Stats.energy || isClickedDown)
                buy.setAnimationImageIndex(1);
            else
                buy.setAnimationImageIndex(0);

            buy.draw(mvpMatrix);

            if(isClickedDown || upgrade.price > Stats.energy){
                fontPrice.position.y -= 8;
                FontMaster.draw(mvpMatrix, fontPrice, price);
                fontPrice.position.y += 8;
            }else
                FontMaster.draw(mvpMatrix, fontPrice, price);
        }else{
            fontPrice.position.y = box.position.y;
            FontMaster.draw(mvpMatrix, fontPrice, "maxed");
        }
        

        icon.texturePointer = texturePointer;
        icon.draw(mvpMatrix);

        FontMaster.draw(mvpMatrix, fontTitle, title);
        FontMaster.draw(mvpMatrix, fontStats, stats);
        FontMaster.draw(mvpMatrix, fontStatsInfo, statsInfo);

        progress.setPercentage((float)upgrade.upgrades / upgrade.steps);
        progress.draw(mvpMatrix);

        if(minZone > Stats.zone){
            grayOverlay.position.y = box.position.y;
            grayOverlay.draw(mvpMatrix);
        }
    }

    @Override
    public void checkClick(float currentOffsetY, float x, float y, boolean clickDown){
        if(minZone > Stats.zone)
            return;
            
        buy.position.y = buyOffset.y + currentOffsetY;

        if (buy.position.x + buy.texWidth / 2 > x && buy.position.x - buy.texWidth / 2 < x &&
                    buy.position.y + buy.texHeight / 2 > y && buy.position.y - buy.texHeight / 2 < y) {

            if (clickDown) {
                if (!isClickedDown)

                    isClickedDown = true;
            }
            if (!clickDown && isClickedDown) {
                onClick();
                isClickedDown = false;
            }
        } else if (!clickDown)
            isClickedDown = false;
    }

    public void onClick(){
        if(upgrade.price > Stats.energy || upgrade.upgrades == upgrade.steps)
            return;

        Stats.energy-=upgrade.price;
        upgrade.bought();
        setText();
    }

    private void setText(){
        if(upgrade.isMaxed)
            price = "";
        else
            price = Stats.valueToString(upgrade.price);
        
        stats = String.valueOf(upgrade.description());
        statsInfo = "+" + String.valueOf(upgrade.factor) + "%";
    }
}
