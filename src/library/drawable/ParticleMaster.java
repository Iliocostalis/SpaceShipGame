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

public class ParticleMaster {
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
    private Particle[] particles;
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

    protected ParticleMaster(){

    }

    public ParticleMaster(String texure, float lifeTime, float spawnDelay, Position parentPosition){
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
        

        particles = new Particle[amount];
        for(int i = 0; i < particles.length; i++){
            particles[i] = new Particle();
        }

        int vertexBufferLength = particles.length * 8;
        int colorBufferLength = particles.length * 16;
        int textureBufferLength = particles.length * 8;

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

            float age = currentDelay - EngineTools.deltaTime;
            
            spawnDelay = spawnDelayInit + random.nextFloat() * 2f * spawnDelayRange - spawnDelayRange;

            particles[currentIndex].lifeTime = age;

            if(useDirection){
                float directionUsed = direction + random.nextFloat() * 2f * directionRange - directionRange;
                particles[currentIndex].velocityX = directionSpeed * (float)Math.cos(Math.toRadians(directionUsed));
                particles[currentIndex].velocityY = directionSpeed * (float)Math.sin(Math.toRadians(directionUsed));
            }else{
                particles[currentIndex].velocityX = random.nextFloat() * (maxVelocityX - minVelocityX) + minVelocityX;
                particles[currentIndex].velocityY = random.nextFloat() * (maxVelocityY - minVelocityY) + minVelocityY;
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

            particles[currentIndex].position.x = offsetX + particles[currentIndex].velocityX * age;
            particles[currentIndex].position.y = offsetY + particles[currentIndex].velocityY * age;


            particles[currentIndex].rotationSpeed = rotationSpeed + random.nextFloat() * 2f * rotationSpeedRange - rotationSpeedRange;
            particles[currentIndex].rotation = rotation + random.nextFloat() * 2f * rotationRange - rotationRange + 
                                                particles[currentIndex].rotationSpeed * age;
            
            particles[currentIndex].rDif = (colorEnd.r - colorStart.r) / maxLifeTime;
            particles[currentIndex].gDif = (colorEnd.g - colorStart.g) / maxLifeTime;
            particles[currentIndex].bDif = (colorEnd.b - colorStart.b) / maxLifeTime;
            particles[currentIndex].aDif = (colorEnd.a - colorStart.a) / maxLifeTime;

            particles[currentIndex].r = colorStart.r + particles[currentIndex].rDif * age;
            particles[currentIndex].g = colorStart.g + particles[currentIndex].gDif * age;
            particles[currentIndex].b = colorStart.b + particles[currentIndex].bDif * age;
            particles[currentIndex].a = colorStart.a + particles[currentIndex].aDif * age;

            particles[currentIndex].scale = scale + random.nextFloat() * 2f * scaleRange - scaleRange;
            particles[currentIndex].scaleDif = (scaleEnd - particles[currentIndex].scale) / maxLifeTime;
            particles[currentIndex].scale += particles[currentIndex].scaleDif * age;

            currentIndex = (currentIndex + 1) % particles.length;
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
        for(int i = 0; i < particles.length; i++){
            
            if(particles[currentIndex].lifeTime >= maxLifeTime){
                currentIndex = (currentIndex + 1) % particles.length;
                continue;
            }
                
            finished = false;
            particles[currentIndex].lifeTime += EngineTools.deltaTime;

            if(useGravity){
                particles[currentIndex].velocityX += gravityX * EngineTools.deltaTime;
                particles[currentIndex].velocityY += gravityY * EngineTools.deltaTime;
            }
    
            particles[currentIndex].position.x += particles[currentIndex].velocityX * EngineTools.deltaTime + offsetPosPF.x;
            particles[currentIndex].position.y += particles[currentIndex].velocityY * EngineTools.deltaTime + offsetPosPF.y;
    
            particles[currentIndex].rotation += particles[currentIndex].rotationSpeed * EngineTools.deltaTime;
            
            particles[currentIndex].r += particles[currentIndex].rDif * EngineTools.deltaTime;
            particles[currentIndex].g += particles[currentIndex].gDif * EngineTools.deltaTime;
            particles[currentIndex].b += particles[currentIndex].bDif * EngineTools.deltaTime;
            particles[currentIndex].a += particles[currentIndex].aDif * EngineTools.deltaTime;
    
            particles[currentIndex].scale += particles[currentIndex].scaleDif * EngineTools.deltaTime;
            
            float cos = (float)Math.cos(Math.toRadians(particles[currentIndex].rotation));
            float sin = (float)Math.sin(Math.toRadians(particles[currentIndex].rotation));

            float sizeXCos = particles[currentIndex].scale * width * cos;
            float sizeYCos = particles[currentIndex].scale * height * cos;
            float sizeXSin = particles[currentIndex].scale * width * sin;
            float sizeYSin = particles[currentIndex].scale * height * sin;
            
    
            /*vertexBuffer.put((particles[currentIndex].position.x + sizeXCos - sizeYSin) * mvpMatrixScaleX - 1f);
            vertexBuffer.put((particles[currentIndex].position.y + sizeXSin + sizeYCos) * mvpMatrixScaleY - 1f);

            vertexBuffer.put((particles[currentIndex].position.x - sizeXSin - sizeYCos) * mvpMatrixScaleX - 1f);
            vertexBuffer.put((particles[currentIndex].position.y + sizeXCos - sizeYSin) * mvpMatrixScaleY - 1f);

            vertexBuffer.put((particles[currentIndex].position.x - sizeXCos + sizeYSin) * mvpMatrixScaleX - 1f);
            vertexBuffer.put((particles[currentIndex].position.y - sizeXSin - sizeYCos) * mvpMatrixScaleY - 1f);

            vertexBuffer.put((particles[currentIndex].position.x + sizeXSin + sizeYCos) * mvpMatrixScaleX - 1f);
            vertexBuffer.put((particles[currentIndex].position.y - sizeXCos + sizeYSin) * mvpMatrixScaleY - 1f);*/

            vertex[vertexIndex++] = ((particles[currentIndex].position.x + sizeXCos - sizeYSin) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((particles[currentIndex].position.y + sizeXSin + sizeYCos) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((particles[currentIndex].position.x - sizeXSin - sizeYCos) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((particles[currentIndex].position.y + sizeXCos - sizeYSin) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((particles[currentIndex].position.x - sizeXCos + sizeYSin) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((particles[currentIndex].position.y - sizeXSin - sizeYCos) * mvpMatrixScaleY - 1f);

            vertex[vertexIndex++] = ((particles[currentIndex].position.x + sizeXSin + sizeYCos) * mvpMatrixScaleX - 1f);
            vertex[vertexIndex++] = ((particles[currentIndex].position.y - sizeXCos + sizeYSin) * mvpMatrixScaleY - 1f);

            /*colorBuffer.put(particles[currentIndex].r);
            colorBuffer.put(particles[currentIndex].g);
            colorBuffer.put(particles[currentIndex].b);
            colorBuffer.put(particles[currentIndex].a);
            
            colorBuffer.put(particles[currentIndex].r);
            colorBuffer.put(particles[currentIndex].g);
            colorBuffer.put(particles[currentIndex].b);
            colorBuffer.put(particles[currentIndex].a);
            
            colorBuffer.put(particles[currentIndex].r);
            colorBuffer.put(particles[currentIndex].g);
            colorBuffer.put(particles[currentIndex].b);
            colorBuffer.put(particles[currentIndex].a);
            
            colorBuffer.put(particles[currentIndex].r);
            colorBuffer.put(particles[currentIndex].g);
            colorBuffer.put(particles[currentIndex].b);
            colorBuffer.put(particles[currentIndex].a);*/

            color[colorIndex++] = particles[currentIndex].r;
            color[colorIndex++] = particles[currentIndex].g;
            color[colorIndex++] = particles[currentIndex].b;
            color[colorIndex++] = particles[currentIndex].a;

            color[colorIndex++] = particles[currentIndex].r;
            color[colorIndex++] = particles[currentIndex].g;
            color[colorIndex++] = particles[currentIndex].b;
            color[colorIndex++] = particles[currentIndex].a;

            color[colorIndex++] = particles[currentIndex].r;
            color[colorIndex++] = particles[currentIndex].g;
            color[colorIndex++] = particles[currentIndex].b;
            color[colorIndex++] = particles[currentIndex].a;

            
            color[colorIndex++] = particles[currentIndex].r;
            color[colorIndex++] = particles[currentIndex].g;
            color[colorIndex++] = particles[currentIndex].b;
            color[colorIndex++] = particles[currentIndex].a;

            vertices += 4;
            currentIndex = (currentIndex + 1) % particles.length;
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

    public ParticleMaster clone(){
        ParticleMaster particleController = new ParticleMaster();

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
        

        particleController.particles = new Particle[amount];
        for(int i = 0; i < particleController.particles.length; i++){
            particleController.particles[i] = new Particle();
        }


        particleController.texturePointer = texturePointer.clone();

        int vertexBufferLength = particleController.particles.length * 8;
        int colorBufferLength = particleController.particles.length * 16;
        int textureBufferLength = particleController.particles.length * 8;

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

    class Particle {
        Position position = new Position();
        float lifeTime;
        float r,b,g,a;
        float rDif,bDif,gDif,aDif;
        float scaleDif;
        float scale;
        float rotation;
        float rotationSpeed;
        float velocityX;
        float velocityY;
    }
}