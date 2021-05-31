package library.screens;

import java.util.Arrays;

import library.EngineTools;
import library.FrameBuffer;
import library.addons.Animation;
import library.addons.AnimationC;
import library.addons.AnimationLoader;
import library.addons.Color;
import library.addons.fonts.*;
import library.addons.Position;
import library.addons.OnClickListener;
import library.drawable.list.*;
import library.drawable.Sprite;
import library.drawable.SpriteAnimated;
import library.drawable.UIClickable;

public class Shop extends Screen {

    Sprite shop;
    Color light = new Color(.7f,.7f,.7f,1);
    Color dark = new Color(.3f,.3f,.3f,1);
    Color middleDark = new Color(.45f,.45f,.45f,1);
    float shopTopPosition = 500;
    int heightTop = 8;
    int heightMiddle = 100;

    
    UIClickable maxminimize;
    List lists[];
    int selectedList = 0;
    UIClickable[] tabs;
    Font tabNameFont;
    String tabNames[] = {"Basic Upgrades", "Premium", "Eq", "Boosts", "P2W"};
    boolean shopVisible = true;
    AnimationC shopAnimOpen, shopAnimClose;
    SpriteAnimated maxminimizeS;
    Animation maximize, minimize;

    public Shop(){ onCreate();}

    @Override
    public void onCreate() {
        shop = new Sprite("white.png",1,1);
        shop.position.x = EngineTools.screenWidth/2;
        shop.setScaleX(EngineTools.screenWidth);


        maxminimize = new UIClickable("maxminimize.png",true);
        maxminimize.position.x = EngineTools.screenWidth - maxminimize.texWidth/2;
        maxminimize.onClickListener = new OnClickListener(){
            @Override
            public void onClick(){
                toggleShopVisibility();
            }
        };

        maxminimizeS = new SpriteAnimated("maxminimizeS.png",1,1);
        maxminimizeS.position.x = EngineTools.screenWidth - maxminimize.texWidth/2 + 12;
        maximize = AnimationLoader.loadAnimation("maximize.anim");
        minimize = AnimationLoader.loadAnimation("minimize.anim");
        maxminimizeS.animation = maximize;

        loadTabs();
        loadLists();

        tabs[0].isSelected = true;

        shopAnimOpen = new AnimationC();
        shopAnimOpen.hasPosYAnim = true;
        shopAnimOpen.timesPosY = new float[]{0, .4f};
        shopAnimOpen.valuePosY = new float[]{tabs[0].texHeight, EngineTools.screenHeight/2, EngineTools.screenHeight-200, EngineTools.screenHeight-200};

        shopAnimClose = new AnimationC();
        shopAnimClose.hasPosYAnim = true;
        shopAnimClose.timesPosY = new float[]{0, .4f};
        shopAnimClose.valuePosY = new float[]{EngineTools.screenHeight-200, EngineTools.screenHeight/2, tabs[0].texHeight, tabs[0].texHeight};
    }

    private void loadTabs(){
        tabs = new UIClickable[5];
        for(int i = 0; i < tabs.length; i++){
            tabs[i] = new UIClickable("tab"+ (i+1) + ".png",true);
            tabs[i].position.x = tabs[i].texWidth/2 + tabs[i].texWidth * (i);
            tabs[i].position.y = tabs[i].texHeight/2;
            final int id = i;
            tabs[i].onClickListener = new OnClickListener(){
                @Override
                public void onClick(){
                    selectTab();
                    tabs[id].isSelected = true;
                    selectedList = id;
                }
            };
        }
        tabNameFont = new Font(.7f, new Position(20,0), FontMaster.RESOLUTION_MEDIUM, new Color(), FontNames.SQUAREFONT);
    }

    private void loadLists(){
        lists = new List[tabs.length];

        // List 0
        lists[0] = new List(EngineTools.screenHeight - EngineTools.screenWidth - heightTop - heightMiddle, tabs[0].texHeight, 12);
        int zone = 0;

        lists[0].addElement(new SpaceElement(3));
        lists[0].addElement(new ZoneElement(zone));
        
        lists[0].addElement(new ListElement("Droprate 1", Stats.upgradesDrop[0], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("Generator Efficency 1", Stats.upgradesGEfficency[0], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("DMG 1", Stats.upgradesDmg[0], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("Generator Speed 1", Stats.upgradesGTime[0], EngineTools.loadGLTexture("dmgIcon.png"), zone));

        lists[0].addElement(new SpaceElement(10));
        lists[0].addElement(new TryBossElement(zone++));
        lists[0].addElement(new SpaceElement(30));
        lists[0].addElement(new ZoneElement(zone));


        lists[0].addElement(new ListElement("Droprate 2", Stats.upgradesDrop[1], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("DMG 2", Stats.upgradesDmg[1], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("Generator Efficency 2", Stats.upgradesGEfficency[1], EngineTools.loadGLTexture("dmgIcon.png"), zone));
        lists[0].addElement(new ListElement("Generator Speed 2", Stats.upgradesGTime[1], EngineTools.loadGLTexture("dmgIcon.png"), zone));

        lists[0].addElement(new SpaceElement(10));
        lists[0].addElement(new TryBossElement(zone++));
        lists[0].addElement(new SpaceElement(30));
        lists[0].addElement(new ZoneElement(zone));



        // List 1
        lists[1] = new List(EngineTools.screenHeight - EngineTools.screenWidth - heightTop - heightMiddle, tabs[0].texHeight, 12);

        lists[1].addElement(new ListElement("Droprate", Stats.upgradesDrop[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("Dmg", Stats.upgradesDmg[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("Firerate", Stats.upgradesFirerate[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("Fly speed", Stats.upgradesFlySpeed[0], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("G Efficency", Stats.upgradesGEfficency[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("G Time", Stats.upgradesGTime[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("Distance", Stats.upgradesDistance[2], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("CPS", Stats.upgradesClicksPerShot[0], EngineTools.loadGLTexture("dmgIcon.png"), 0));
        lists[1].addElement(new ListElement("S Dmg", Stats.upgradesSpecialShotDmgFactor[0], EngineTools.loadGLTexture("dmgIcon.png"), 0));

        
        lists[2] = new List(EngineTools.screenHeight - EngineTools.screenWidth - heightTop - heightMiddle, tabs[0].texHeight, 12);

        
        lists[3] = new List(EngineTools.screenHeight - EngineTools.screenWidth - heightTop - heightMiddle, tabs[0].texHeight, 12);

        
        lists[4] = new List(EngineTools.screenHeight - EngineTools.screenWidth - heightTop - heightMiddle, tabs[0].texHeight, 12);
    }

    private void toggleShopVisibility(){
        if(shopVisible)
            maxminimizeS.animation = minimize;
        else
            maxminimizeS.animation = maximize;

        maxminimizeS.animation.reset();
    
        shopAnimOpen.reset();
        shopAnimClose.reset();
    
        shopVisible = !shopVisible;
    }

    private void selectTab(){
        for (UIClickable tab : tabs) {
            tab.isSelected = false;
        }
        if(!shopVisible)
            toggleShopVisibility();
    }

    private void updateShopPositions(){
        
        if(shopVisible){
            shopAnimOpen.update();
            shopTopPosition = shopAnimOpen.animPosOffset.y;
        }else{
            shopAnimClose.update();
            shopTopPosition = shopAnimClose.animPosOffset.y;
        }

        // 10 is the size of the maxminimize border
        maxminimize.position.y = shopTopPosition - 10 + maxminimize.texHeight/2;
        maxminimizeS.position.y = maxminimize.position.y;
        lists[selectedList].topPosY = shopTopPosition - heightTop - heightMiddle;

        tabNameFont.position.y = shopTopPosition - heightTop - heightMiddle/2f;
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        updateShopPositions();

        maxminimize.draw(mvpMatrix);
        maxminimizeS.draw(mvpMatrix);
        
        shop.color = light;
        shop.setScaleY(heightTop);
        shop.position.y = shopTopPosition - heightTop/2f;
        shop.draw(mvpMatrix);

        shop.color = dark;
        shop.setScaleY(heightMiddle);
        shop.position.y = shopTopPosition - heightTop - heightMiddle/2f;
        shop.draw(mvpMatrix);

        shop.color = middleDark;
        shop.setScaleY(shopTopPosition - heightTop - heightMiddle);
        shop.position.y = shopTopPosition - heightTop - heightMiddle - (shopTopPosition - 10 - 100)/2f;
        shop.draw(mvpMatrix);

        FontMaster.draw(mvpMatrix, tabNameFont, tabNames[selectedList]);


        if(!moving){
            float decreaseFactor = 1;
            if(lists[selectedList].posYOffset < 0) {
                lists[selectedList].posYOffset += (averageMoveVelocity * EngineTools.deltaTime) * (200 / (-lists[selectedList].posYOffset + 200));
                decreaseFactor = 3;
            }else if(lists[selectedList].posYOffset > -lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY){
                float minDif = Math.min((lists[selectedList].posYOffset - (-lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY)), lists[selectedList].posYOffset);
                lists[selectedList].posYOffset += (averageMoveVelocity * EngineTools.deltaTime) * (200 / (minDif + 200));
                decreaseFactor = 3;
            }else {
                lists[selectedList].posYOffset += averageMoveVelocity * EngineTools.deltaTime;
            }

            if(averageMoveVelocity > 0){
                if(averageMoveVelocity < decreaseFactor * 4000 * EngineTools.deltaTime)
                    averageMoveVelocity = 0;
                else
                    averageMoveVelocity -= decreaseFactor * 4000 * EngineTools.deltaTime;
            }else {
                if(averageMoveVelocity > -decreaseFactor * 4000 * EngineTools.deltaTime)
                    averageMoveVelocity = 0;
                else
                    averageMoveVelocity += decreaseFactor * 4000 * EngineTools.deltaTime;
            }

            averageMoveVelocity *= Math.pow(0.90f, decreaseFactor);

            if(Math.abs(averageMoveVelocity) < 100)
                averageMoveVelocity = 0;




            if(lists[selectedList].posYOffset < 0) {
                if(lists[selectedList].posYOffset > -10)
                    lists[selectedList].posYOffset = 0;
                else
                    lists[selectedList].posYOffset -= (lists[selectedList].posYOffset*12f - 200)*EngineTools.deltaTime;
            }else if(lists[selectedList].posYOffset > -lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY){
                float minDif = Math.min((lists[selectedList].posYOffset - (-lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY)), lists[selectedList].posYOffset);
                if(minDif < 10)
                    lists[selectedList].posYOffset -= minDif;
                else
                    lists[selectedList].posYOffset -= (minDif * 12f + 200)*EngineTools.deltaTime;
            }
        }




        FrameBuffer.select(1);

        lists[selectedList].draw(mvpMatrix);

        FrameBuffer.select(0);

        FrameBuffer.drawArea(1,0,1,0,(float)(lists[selectedList].topPosY + 10) / EngineTools.screenHeight);

        for (UIClickable tab : tabs) {
            tab.draw(mvpMatrix);
        }
    }

    @Override
    public void touched(float x, float y, boolean clickDown) {
        maxminimize.checkClick(x,y,clickDown);
        if(!clickDown && moving || y-10 > lists[selectedList].topPosY || y < lists[selectedList].bottomPosY){
            lists[selectedList].checkClick(-1000, -1000, clickDown);
        }else
            lists[selectedList].checkClick(x, y, clickDown);

        for (UIClickable tab : tabs) {
            tab.checkClick(x, y, clickDown);
        }

        // Scroll
        lastY = y;
        if(!clickDown)
            moving = false;

        if(clickDown){
            Arrays.fill(lastMovesDif, 0);
            averageMoveVelocity = 0;
        }else{
            for (int i = 0; i < lastMovesDif.length; i++) {
                averageMoveVelocity += lastMovesDif[i];
            }
            averageMoveVelocity /= lastMovesDif.length;
            averageMoveVelocity *= 60f;
        }
    }

    float lastY;
    boolean moving = false;
    float[] lastMovesDif = new float[5];
    float averageMoveVelocity;

    @Override
    public void touchMove(float startX, float startY, float currentX, float currentY) {
        if(startY < lists[selectedList].topPosY + 10 && startY > lists[selectedList].bottomPosY){

            if(!moving && Math.abs(startY - currentY) < 20)
                return;

            if(lists[selectedList].posYOffset < 0) {
                lists[selectedList].posYOffset += (currentY - lastY) * (200 / (-lists[selectedList].posYOffset + 200));
            }else if(lists[selectedList].posYOffset > -lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY){
                float minDif = Math.min((lists[selectedList].posYOffset - (-lists[selectedList].topPosY + lists[selectedList].getListHeight() + lists[selectedList].bottomPosY)), lists[selectedList].posYOffset);
                lists[selectedList].posYOffset += (currentY - lastY) * (200 / (minDif + 200));
            }else {
                lists[selectedList].posYOffset += currentY - lastY;
            }


            for (int i = lastMovesDif.length-1; i > 0; i--) {
                lastMovesDif[i] = lastMovesDif[i-1];
            }
            lastMovesDif[0] = currentY - lastY;

            lastY = currentY;
            moving = true;
        }
    }
}
