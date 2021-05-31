package library.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL30;

import library.EngineTools;
import library.Shader;
import library.addons.Color;
import library.addons.Position;

public class ParticleMasterArray {
    public boolean finished = false;
    public boolean emitNew = true;
    public float maxLifeTime;
    private float spawnDelay;
    public float spawnDelayInit;
    public float spawnDelayRange;
    public float currentDelay;
    public int amountMax;
    public int amountCurrently;

    public float minVelocityX;
    public float maxVelocityX;
    public float minVelocityY;
    public float maxVelocityY;

    public boolean useGravity;
    public float gravityX;
    public float gravityY;

    public boolean useDirection;
    public float direction;
    public float directionRange;
    public float directionSpeed;

    public float rotation;
    public float rotationRange;
    public float rotationSpeed;
    public float rotationSpeedRange;

    private int[] texturePointer;

    public float scale;
    public float scaleRange;
    public float scaleEnd;

    public Color colorStart;
    public Color colorEnd;

    private int currentIndex = 0;
    // 0-pX 1-pY 2-lifeTime 3-r 4-g 5-b 6-a 7-rDif 8-gDif 9-bDif 10-aDif 11-scaleDif 12-scale 13-rotation 14-rotationSpeed 15-veloX 16-veloY
    private float[][] particleInfos;
    private Random random = new Random();

    public Position parentPosition;
    private Position parentAnimationPosition;
    private Position lastPosition = new Position();
    public Position offsetPosPF = new Position();

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureBuffer;

    private float[] vertex;
    private float[] color;

    private float width;
    private float height;

    protected ParticleMasterArray(){

    }

    public ParticleMasterArray(String texure, float lifeTime, float spawnDelay, Position parentPosition){
        this.maxLifeTime = lifeTime;
        this.spawnDelayInit = spawnDelay;
        this.parentPosition = parentPosition;

        this.spawnDelayRange = 0f;
        this.amountMax = 0;
        this.minVelocityX = 0f;
        this.maxVelocityX = 0f;
        this.minVelocityY = 0f;
        this.maxVelocityY = 0f;
    
        this.useGravity = false;
        this.gravityX = 0f;
        this.gravityY = 0f;
    
        this.direction = 0f;
        this.directionRange = 0f;
    
        this.rotation = 0f;
        this.rotationRange = 0f;
        this.rotationSpeed = 0f;
        this.rotationSpeedRange = 0f;
    
        this.scale = 1f;
        this.scaleRange = 0f;
        this.scaleEnd = 1f;
    
        colorStart = new Color(1,1,1,1);
        colorEnd = new Color(1,1,1,1);




        int amount = 0;
        if(spawnDelayInit == 0)
            amount = amountMax;
        else
            amount = (int) (maxLifeTime/spawnDelayInit + 1);
        

        particleInfos = new float[amount][17];

        int vertexBufferLength = amount * 8;
        int colorBufferLength = amount * 16;
        int textureBufferLength = amount * 8;

        ByteBuffer bb = ByteBuffer.allocateDirect(vertexBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        bb = ByteBuffer.allocateDirect(colorBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asFloatBuffer();
        
        
        bb = ByteBuffer.allocateDirect(textureBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        textureBuffer = bb.asFloatBuffer();

        for(int i = 0; i < textureBufferLength; i+=8){
            textureBuffer.put(0f);
            textureBuffer.put(0f);
            textureBuffer.put(1f);
            textureBuffer.put(0f);
            textureBuffer.put(1f);
            textureBuffer.put(1f);
            textureBuffer.put(0f);
            textureBuffer.put(1f);
        }

        textureBuffer.position(0);

        color = new float[colorBufferLength];
        vertex = new float[vertexBufferLength];

        loadTexture(texure);
    }

    public void draw(float[] mvpMatrix) {
        if(finished)
            return;

        currentDelay += EngineTools.deltaTime;

        while(emitNew && (amountMax == 0 || amountCurrently < amountMax) && currentDelay >= spawnDelay){
            currentDelay -= spawnDelay;
            amountCurrently++;
            float[] singleParticle = particleInfos[currentIndex];

            float age = currentDelay - EngineTools.deltaTime;
            
            spawnDelay = spawnDelayInit + random.nextFloat() * 2f * spawnDelayRange - spawnDelayRange;

            singleParticle[2] = age;

            if(useDirection){
                float directionUsed = direction + random.nextFloat() * 2f * directionRange - directionRange;
                singleParticle[15] = directionSpeed * (float)Math.cos(Math.toRadians(directionUsed));
                singleParticle[16] = directionSpeed * (float)Math.sin(Math.toRadians(directionUsed));
            }else{
                singleParticle[15] = random.nextFloat() * (maxVelocityX - minVelocityX) + minVelocityX;
                singleParticle[16] = random.nextFloat() * (maxVelocityY - minVelocityY) + minVelocityY;
            }

            float offsetX;
            float offsetY;
            if(parentAnimationPosition == null){
                offsetX = parentPosition.x - (parentPosition.x - lastPosition.x) * currentDelay/EngineTools.deltaTime;
                offsetY = parentPosition.y - (parentPosition.y - lastPosition.y) * currentDelay/EngineTools.deltaTime;
            }else{
                offsetX = parentPosition.x - (parentPosition.x + parentAnimationPosition.x - lastPosition.x) * currentDelay/EngineTools.deltaTime;
                offsetY = parentPosition.y - (parentPosition.y + parentAnimationPosition.x - lastPosition.y) * currentDelay/EngineTools.deltaTime;
            }

            singleParticle[0] = offsetX + singleParticle[15] * age;
            singleParticle[1] = offsetY + singleParticle[16] * age;


            singleParticle[14] = rotationSpeed + random.nextFloat() * 2f * rotationSpeedRange - rotationSpeedRange;
            singleParticle[13] = rotation + random.nextFloat() * 2f * rotationRange - rotationRange + 
                                                singleParticle[14] * age;
            
            singleParticle[7] = (colorEnd.r - colorStart.r) / maxLifeTime;
            singleParticle[8] = (colorEnd.g - colorStart.g) / maxLifeTime;
            singleParticle[9] = (colorEnd.b - colorStart.b) / maxLifeTime;
            singleParticle[10] = (colorEnd.a - colorStart.a) / maxLifeTime;

            singleParticle[3] = colorStart.r + singleParticle[7] * age;
            singleParticle[4] = colorStart.g + singleParticle[8] * age;
            singleParticle[5] = colorStart.b + singleParticle[9] * age;
            singleParticle[6] = colorStart.a + singleParticle[10] * age;

            singleParticle[12] = scale + random.nextFloat() * 2f * scaleRange - scaleRange;
            singleParticle[11] = (scaleEnd - singleParticle[12]) / maxLifeTime;
            singleParticle[12] += singleParticle[11] * age;

            currentIndex = (currentIndex + 1) % particleInfos.length;
        }

        if(parentAnimationPosition == null){
            lastPosition.x = parentPosition.x;
            lastPosition.y = parentPosition.y;
        }else{
            lastPosition.x = parentPosition.x + parentAnimationPosition.x;
            lastPosition.y = parentPosition.y + parentAnimationPosition.y;
        }


        finished = true;
        int vertices = 0;
        int colorIndex = 0;
        int vertexIndex = 0;
        float mvpMatrixScaleX = mvpMatrix[0]/3f;
        float mvpMatrixScaleY = mvpMatrix[5]/3f;
        for(int i = 0; i < particleInfos.length; i++){
            
            float[] singleParticle = particleInfos[currentIndex];

            if(singleParticle[2] >= maxLifeTime){
                currentIndex = (currentIndex + 1) % particleInfos.length;
                continue;
            }
                

            finished = false;
            singleParticle[2] += EngineTools.deltaTime;

            if(useGravity){
                singleParticle[15] += gravityX * EngineTools.deltaTime;
                singleParticle[16] += gravityY * EngineTools.deltaTime;
            }
    
            singleParticle[0] += singleParticle[15] * EngineTools.deltaTime + offsetPosPF.x;
            singleParticle[1] += singleParticle[16] * EngineTools.deltaTime + offsetPosPF.y;
    
            singleParticle[13] += singleParticle[14] * EngineTools.deltaTime;
            
            singleParticle[3] += singleParticle[7] * EngineTools.deltaTime;
            singleParticle[4] += singleParticle[8] * EngineTools.deltaTime;
            singleParticle[5] += singleParticle[9] * EngineTools.deltaTime;
            singleParticle[6] += singleParticle[10] * EngineTools.deltaTime;
    
            singleParticle[12] += singleParticle[11] * EngineTools.deltaTime;
            
            float cos = (float)Math.cos(Math.toRadians(singleParticle[13]));
            float sin = (float)Math.sin(Math.toRadians(singleParticle[13]));

            float sizeXCos = singleParticle[12] * width * cos;
            float sizeYCos = singleParticle[12] * height * cos;
            float sizeXSin = singleParticle[12] * width * sin;
            float sizeYSin = singleParticle[12] * height * sin;
            

            vertex[vertexIndex++] = ((singleParticle[0] + sizeXCos - sizeYSin) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((singleParticle[1] + sizeXSin + sizeYCos) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((singleParticle[0] - sizeXSin - sizeYCos) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((singleParticle[1] + sizeXCos - sizeYSin) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((singleParticle[0] - sizeXCos + sizeYSin) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((singleParticle[1] - sizeXSin - sizeYCos) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((singleParticle[0] + sizeXSin + sizeYCos) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((singleParticle[1] - sizeXCos + sizeYSin) * mvpMatrixScaleY - 1f);


            color[colorIndex++] = singleParticle[3];
            color[colorIndex++] = singleParticle[4];
            color[colorIndex++] = singleParticle[5];
            color[colorIndex++] = singleParticle[6];

            color[colorIndex++] = singleParticle[3];
            color[colorIndex++] = singleParticle[4];
            color[colorIndex++] = singleParticle[5];
            color[colorIndex++] = singleParticle[6];
            
            color[colorIndex++] = singleParticle[3];
            color[colorIndex++] = singleParticle[4];
            color[colorIndex++] = singleParticle[5];
            color[colorIndex++] = singleParticle[6];
            
            color[colorIndex++] = singleParticle[3];
            color[colorIndex++] = singleParticle[4];
            color[colorIndex++] = singleParticle[5];
            color[colorIndex++] = singleParticle[6];

            vertices += 4;
            currentIndex = (currentIndex + 1) % particleInfos.length;
        }

        vertexBuffer.put(vertex);
        colorBuffer.put(color);
        vertexBuffer.position(0);
        colorBuffer.position(0);
        


        //Add program to OpenGL Environment
        GL30.glUseProgram(Shader.particleProgram);

        GL30.glEnableVertexAttribArray(Shader.positionHandleParticle);
        GL30.glEnableVertexAttribArray(Shader.colorScaleParticle);
        GL30.glEnableVertexAttribArray(Shader.textureCoordinateHandleParticle);

        GL30.glVertexAttribPointer(Shader.positionHandleParticle, 2, GL30.GL_FLOAT, false, 8, vertexBuffer);
        GL30.glVertexAttribPointer(Shader.colorScaleParticle, 4, GL30.GL_FLOAT, false, 16, colorBuffer);
        GL30.glVertexAttribPointer(Shader.textureCoordinateHandleParticle, 2, GL30.GL_FLOAT, false, 8, textureBuffer);

        //Set the active texture unit to texture unit 0.
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texturePointer[0]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL30.glUniform1i(Shader.textureUniformHandleParticle, 0);

        GL30.glDrawArrays(GL30.GL_QUADS, 0, vertices);

        GL30.glDisableVertexAttribArray(Shader.positionHandleParticle);
        GL30.glDisableVertexAttribArray(Shader.colorScaleParticle);
        GL30.glDisableVertexAttribArray(Shader.textureCoordinateHandleParticle);
    }

    public void resetLastPosition(){
        lastPosition.x = parentPosition.x;
        lastPosition.y = parentPosition.y;
    }
    public void setPositionParent(Position position){
        parentPosition = position;
        resetLastPosition();
    }

    public void setAnimationPositionParent(Position position){
        parentAnimationPosition = position;
        lastPosition.x = position.x + parentPosition.x;
        lastPosition.y = position.y + parentPosition.x;
    }

    private void loadTexture(String texure){
        int[] texturePointerCombined = EngineTools.loadGLTexture(texure, 1, 1);
        texturePointer = new int[texturePointerCombined.length - 2];

        for (int i = 0; i < texturePointer.length; i++) {
            texturePointer[i] = texturePointerCombined[i + 2];
        }

        width = texturePointerCombined[0];
        height = texturePointerCombined[1];
    }

    public ParticleMasterArray clone(){
        ParticleMasterArray particleController = new ParticleMasterArray();

        particleController.maxLifeTime = maxLifeTime;
        particleController.spawnDelayInit = spawnDelayInit;
        particleController.parentPosition = parentPosition;

        particleController.spawnDelayRange = spawnDelayRange;
        particleController.amountCurrently = amountCurrently;
        particleController.amountMax = amountMax;
    
        particleController.minVelocityX = minVelocityX;
        particleController.maxVelocityX = maxVelocityX;
        particleController.minVelocityY = minVelocityY;
        particleController.maxVelocityY = maxVelocityY;
    
        particleController.useGravity = useGravity;
        particleController.gravityX = gravityX;
        particleController.gravityY = gravityY;
    
        particleController.useDirection = useDirection;
        particleController.direction = direction;
        particleController.directionSpeed = directionSpeed;
        particleController.directionRange = directionRange;
    
        particleController.rotation = rotation;
        particleController.rotationRange = rotationRange;
        particleController.rotationSpeed = rotationSpeed;
        particleController.rotationSpeedRange = rotationSpeedRange;
    
        particleController.scale = scale;
        particleController.scaleRange = scaleRange;
        particleController.scaleEnd = scaleEnd;
    
        particleController.colorStart = colorStart;
        particleController.colorEnd = colorEnd;

        particleController.width = width;
        particleController.height = height;

        int amount = 0;
        if(particleController.spawnDelayInit == 0)
            amount = particleController.amountMax;
        else
            amount = (int) (particleController.maxLifeTime/particleController.spawnDelayInit + 1);
        

        particleController.particleInfos = new float[amount][17];


        particleController.texturePointer = texturePointer.clone();

        int vertexBufferLength = amount * 8;
        int colorBufferLength = amount * 16;
        int textureBufferLength = amount * 8;

        ByteBuffer bb = ByteBuffer.allocateDirect(vertexBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        particleController.vertexBuffer = bb.asFloatBuffer();

        bb = ByteBuffer.allocateDirect(colorBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        particleController.colorBuffer = bb.asFloatBuffer();
        
        
        bb = ByteBuffer.allocateDirect(textureBufferLength * 4);
        bb.order(ByteOrder.nativeOrder());
        particleController.textureBuffer = bb.asFloatBuffer();

        for(int i = 0; i < textureBufferLength; i+=8){
            particleController.textureBuffer.put(0f);
            particleController.textureBuffer.put(0f);
            particleController.textureBuffer.put(1f);
            particleController.textureBuffer.put(0f);
            particleController.textureBuffer.put(1f);
            particleController.textureBuffer.put(1f);
            particleController.textureBuffer.put(0f);
            particleController.textureBuffer.put(1f);
        }

        particleController.textureBuffer.position(0);

        particleController.vertex = new float[vertexBufferLength];
        particleController.color = new float[colorBufferLength];


        return particleController;
    }
}