package library.addons;

import library.EngineTools;

public class Animation {

    
    public float animLength = 1f;
    protected boolean looping;
    protected boolean reverce;

    private float time;
    public void setTime(float time){
        if(time > animLength)
            time = animLength;
        if(time < 0)
            time = 0;

        this.time = time;
        resetCurIndex();
    }
    private boolean playingForward = true;
    public boolean isAnimationRunning = true;
    private boolean isAnimJustFinished = false;
    private boolean isAnimationFinished = false;
    public boolean isAnimFinished() {
        return isAnimationFinished;
    }
    private float perc;
    private int index;

    public Position animPosOffset = new Position();
    public float animAlphaOffset;
    public float animRotOffset;
    public float animScale = 1;
    public float animScaleX = 1;
    public float animScaleY = 1;

    private int curIndexPosX = 1;
    protected boolean hasPosXAnim;
    protected float[] timesPosX;
    protected float[] valuePosX;

    private int curIndexPosY = 1;
    protected boolean hasPosYAnim;
    protected float[] timesPosY;
    protected float[] valuePosY;

    private int curIndexScale = 1;
    protected boolean hasScaleAnim;
    protected float[] timesScale;
    protected float[] valueScale;

    private int curIndexScaleX = 1;
    protected boolean hasScaleXAnim;
    protected float[] timesScaleX;
    protected float[] valueScaleX;

    private int curIndexScaleY = 1;
    protected boolean hasScaleYAnim;
    protected float[] timesScaleY;
    protected float[] valueScaleY;

    private int curIndexAlpha = 1;
    protected boolean hasAlphaAnim;
    protected float[] timesAlpha;
    protected float[] valueAlpha;

    private int curIndexRot = 1;
    protected boolean hasRotAnim;
    protected float[] timesRot;
    protected float[] valueRot;

    public void reset() {
        time = 0;
        playingForward = true;
        resetCurIndex();
        resetOffsets();
    }

    private void resetCurIndex(){
        if(playingForward){
            curIndexPosX = 1;
            curIndexPosY = 1;
            curIndexScale = 1;
            curIndexScaleX = 1;
            curIndexScaleY = 1;
            curIndexAlpha = 1;
            curIndexRot = 1;
        }else{
            if(hasPosXAnim)
                curIndexPosX = timesPosX.length - 1;
            if(hasPosYAnim)
                curIndexPosY = timesPosY.length - 2;
            if(hasScaleAnim)
                curIndexScale = timesScale.length - 2;
            if(hasScaleXAnim)
                curIndexScaleX = timesScaleX.length - 2;
            if(hasScaleYAnim)
                curIndexScaleY = timesScaleY.length - 2;
            if(hasAlphaAnim)
                curIndexAlpha = timesAlpha.length - 2;
            if(hasRotAnim)
                curIndexRot = timesRot.length - 2;
        }
    }

    private void resetOffsets(){
        animPosOffset.x = 0;
        animPosOffset.y = 0;
        animAlphaOffset = 0;
        animRotOffset = 0;
        animScale = 1;
        animScaleX = 1;
        animScaleY = 1;
    }

    public void update() {
        if (playingForward)
            updateAnimationForward();
        else
            updateAnimationBackward();


        if (isAnimJustFinished)
            isAnimationFinished = true;

        if (playingForward)
            time += EngineTools.deltaTime;
        else
            time -= EngineTools.deltaTime;

        if (time >= animLength) {
            if (looping) {
                if (reverce) {
                    time = animLength + animLength - time;
                    playingForward = false;
                } else {
                    time -= animLength;
                    curIndexPosX = 1;
                    curIndexPosY = 1;
                    curIndexAlpha = 1;
                    curIndexRot = 1;
                    curIndexScale = 1;
                    curIndexScaleX = 1;
                    curIndexScaleY = 1;
                }
            } else {
                if (reverce) {
                    time = animLength + animLength - time;
                    playingForward = false;
                } else {
                    time = animLength;
                    isAnimJustFinished = true;
                }
            }
        } else if (time <= 0) {
            if (looping) {
                time = -time;
                playingForward = true;
            } else {
                time = 0;
                isAnimJustFinished = true;
            }
        }
    }

    private void updateAnimationForward() {
        if (hasPosXAnim) {
            if (timesPosX[timesPosX.length - 1] < time) {
                index = 3 * (timesPosX.length - 2);
                animPosOffset.x = EngineTools.curve(
                        valuePosX[index], valuePosX[index + 1], valuePosX[index + 2], 
                        valuePosX[index + 3], 1);
            } else
                for (int i = curIndexPosX; i < timesPosX.length; i++) {
                    if (timesPosX[i] >= time) {
                        curIndexPosX = i;
                        index = 3 * (i - 1);
                        perc = (time - timesPosX[i - 1]) / (timesPosX[i] - timesPosX[i - 1]);

                        animPosOffset.x = EngineTools.curve(valuePosX[index], valuePosX[index + 1],
                                valuePosX[index + 2], valuePosX[index + 3], perc);
                        break;
                    }
                }
        }
        if (hasPosYAnim) {
            if (timesPosY[timesPosY.length - 1] < time) {
                index = 3 * (timesPosY.length - 2);
                animPosOffset.y = EngineTools.curve(valuePosY[index++], valuePosY[index++], valuePosY[index++],
                        valuePosY[index++], 1);
            } else
                for (int i = curIndexPosY; i < timesPosY.length; i++) {
                    if (timesPosY[i] >= time) {
                        curIndexPosY = i;
                        index = 3 * (i - 1);
                        perc = (time - timesPosY[i - 1]) / (timesPosY[i] - timesPosY[i - 1]);

                        animPosOffset.y = EngineTools.curve(valuePosY[index++], valuePosY[index++], valuePosY[index++],
                                valuePosY[index++], perc);
                        break;
                    }
                }
        }
        if (hasAlphaAnim) {
            if (timesAlpha[timesAlpha.length - 1] < time) {
                index = 3 * (timesAlpha.length - 2);
                animAlphaOffset = EngineTools.curve(valueAlpha[index++], valueAlpha[index++], valueAlpha[index++],
                        valueAlpha[index++], 1);
            } else
                for (int i = curIndexAlpha; i < timesAlpha.length; i++) {
                    if (timesAlpha[i] >= time) {
                        curIndexAlpha = i;
                        index = 3 * (i - 1);
                        perc = (time - timesAlpha[i - 1]) / (timesAlpha[i] - timesAlpha[i - 1]);

                        animAlphaOffset = EngineTools.curve(valueAlpha[index++], valueAlpha[index++],
                                valueAlpha[index++], valueAlpha[index++], perc);
                        break;
                    }
                }
        }
        if (hasScaleAnim) {
            if (timesScale[timesScale.length - 1] < time) {
                index = 3 * (timesScale.length - 2);
                animScale = EngineTools.curve(valueScale[index++], valueScale[index++], valueScale[index++],
                        valueScale[index++], 1);
            } else
                for (int i = curIndexScale; i < timesScale.length; i++) {
                    if (timesScale[i] >= time) {
                        curIndexScale = i;
                        index = 3 * (i - 1);
                        perc = (time - timesScale[i - 1]) / (timesScale[i] - timesScale[i - 1]);

                        animScale = EngineTools.curve(valueScale[index++], valueScale[index++],
                                valueScale[index++], valueScale[index++], perc);
                        break;
                    }
                }
        }
        if (hasScaleXAnim) {
            if (timesScaleX[timesScaleX.length - 1] < time) {
                index = 3 * (timesScaleX.length - 2);
                animScaleX = EngineTools.curve(valueScaleX[index++], valueScaleX[index++], valueScaleX[index++],
                        valueScaleX[index++], 1);
            } else
                for (int i = curIndexScaleX; i < timesScaleX.length; i++) {
                    if (timesScaleX[i] >= time) {
                        curIndexScaleX = i;
                        index = 3 * (i - 1);
                        perc = (time - timesScaleX[i - 1]) / (timesScaleX[i] - timesScaleX[i - 1]);

                        animScaleX = EngineTools.curve(valueScaleX[index++], valueScaleX[index++],
                                valueScaleX[index++], valueScaleX[index++], perc);
                        break;
                    }
                }
        }
        if (hasScaleYAnim) {
            if (timesScaleY[timesScaleY.length - 1] < time) {
                index = 3 * (timesScaleY.length - 2);
                animScaleY = EngineTools.curve(valueScaleY[index++], valueScaleY[index++], valueScaleY[index++],
                        valueScaleY[index++], 1);
            } else
                for (int i = curIndexScaleY; i < timesScaleY.length; i++) {
                    if (timesScaleY[i] >= time) {
                        curIndexScaleY = i;
                        index = 3 * (i - 1);
                        perc = (time - timesScaleY[i - 1]) / (timesScaleY[i] - timesScaleY[i - 1]);

                        animScaleY = EngineTools.curve(valueScaleY[index++], valueScaleY[index++],
                                valueScaleY[index++], valueScaleY[index++], perc);
                        break;
                    }
                }
        }
        if (hasRotAnim) {
            if (timesRot[timesRot.length - 1] < time) {
                index = 3 * (timesRot.length - 2);
                animRotOffset = EngineTools.curve(valueRot[index++], valueRot[index++], valueRot[index++],
                        valueRot[index++], 1);
            } else
                for (int i = curIndexRot; i < timesRot.length; i++) {
                    if (timesRot[i] >= time) {
                        curIndexRot = i;
                        index = 3 * (i - 1);
                        perc = (time - timesRot[i - 1]) / (timesRot[i] - timesRot[i - 1]);

                        animRotOffset = EngineTools.curve(valueRot[index++], valueRot[index++], valueRot[index++],
                                valueRot[index++], perc);
                        break;
                    }
                }
        }
    }

    private void updateAnimationBackward() {
        if (hasPosXAnim) {
            for (int i = curIndexPosX; i > 0; i--) {
                if (timesPosX[i-1] < time) {
                    curIndexPosX = i;
                    index = 3 * (i-1);
                    if (timesPosX[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesPosX[i-1]) / (timesPosX[i] - timesPosX[i-1]);

                    animPosOffset.x = EngineTools.curve(valuePosX[index], valuePosX[index+1],
                            valuePosX[index+2], valuePosX[index+3], perc);
                    break;
                }
            }
        }
        if (hasPosYAnim) {
            for (int i = curIndexPosY; i > 0; i--) {
                if (timesPosY[i-1] < time) {
                    curIndexPosY = i;
                    index = 3 * (i-1);
                    if (timesPosY[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesPosY[i-1]) / (timesPosY[i] - timesPosY[i-1]);

                    animPosOffset.y = EngineTools.curve(valuePosY[index], valuePosY[index + 1], valuePosY[index + 2],
                            valuePosY[index + 3], perc);
                    break;
                }
            }
        }
        if (hasAlphaAnim) {
            for (int i = curIndexAlpha; i > 0; i--) {
                if (timesAlpha[i-1] < time) {
                    curIndexAlpha = i;
                    index = 3 * (i-1);
                    if (timesAlpha[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesAlpha[i-1]) / (timesAlpha[i] - timesAlpha[i-1]);

                    animAlphaOffset = EngineTools.curve(valueAlpha[index], valueAlpha[index + 1], valueAlpha[index + 2],
                            valueAlpha[index + 3], perc);
                    break;
                }
            }
        }
        if (hasScaleAnim) {
            for (int i = curIndexScale; i > 0; i--) {
                if (timesScale[i-1] < time) {
                    curIndexScale = i;
                    index = 3 * (i-1);
                    if (timesScale[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesScale[i-1]) / (timesScale[i] - timesScale[i-1]);

                    animScale = EngineTools.curve(valueScale[index], valueScale[index + 1], valueScale[index + 2],
                            valueScale[index + 3], perc);
                    break;
                }
            }
        }
        if (hasScaleXAnim) {
            for (int i = curIndexScaleX; i > 0; i--) {
                if (timesScaleX[i-1] < time) {
                    curIndexScaleX = i;
                    index = 3 * (i-1);
                    if (timesScaleX[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesScaleX[i-1]) / (timesScaleX[i] - timesScaleX[i-1]);

                    animScaleX = EngineTools.curve(valueScaleX[index], valueScaleX[index + 1], valueScaleX[index + 2],
                            valueScaleX[index + 3], perc);
                    break;
                }
            }
        }
        if (hasScaleYAnim) {
            for (int i = curIndexScaleY; i > 0; i--) {
                if (timesScaleY[i-1] < time) {
                    curIndexScaleY = i;
                    index = 3 * (i-1);
                    if (timesScaleY[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesScaleY[i-1]) / (timesScaleY[i] - timesScaleY[i-1]);

                    animScaleY = EngineTools.curve(valueScaleY[index], valueScaleY[index + 1], valueScaleY[index + 2],
                            valueScaleY[index + 3], perc);
                    break;
                }
            }
        }
        if (hasRotAnim) {
            for (int i = curIndexRot; i > 0; i--) {
                if (timesRot[i-1] < time) {
                    curIndexRot = i;
                    index = 3 * (i-1);
                    if (timesRot[i] <= time)
                        perc = 1;
                    else
                        perc = (time - timesRot[i-1]) / (timesRot[i] - timesRot[i-1]);

                    animRotOffset = EngineTools.curve(valueRot[index], valueRot[index + 1], valueRot[index + 2],
                            valueRot[index + 3], perc);
                    break;
                }
            }
        }
    }
}