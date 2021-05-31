package library.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;
import library.addons.Position;

public class ParticleController {
    
    public boolean finished = false;
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

    public float rStart;
    public float gStart;
    public float bStart;
    public float aStart;

    public float rEnd;
    public float gEnd;
    public float bEnd;
    public float aEnd;

    private int currentIndex = 0;
    private Particle[] particles;
    private Random random = new Random();

    private Position parentPosition;
    private Position parentAnimationPosition;
    private Position lastPosition = new Position();
    public Position offsetPosPF = new Position();

    // Are the sprite Coordinates
    private FloatBuffer vertexBuffer;

    private float[] finalMatrix = new float[16];

    private float spriteCoords[] = { -0.5f,  0.5f,   // top left
                                    -0.5f, -0.5f,   // bottom left
                                    0.5f, -0.5f,   // bottom right
                                    0.5f,  0.5f }; //top right

    protected ParticleController(){

    }

    public ParticleController(String texure, float lifeTime, float spawnDelay, Position parentPosition){
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
    
        this.rStart = 1f;
        this.gStart = 1f;
        this.bStart = 1f;
        this.aStart = 1f;
    
        this.rEnd = 1f;
        this.gEnd = 1f;
        this.bEnd = 1f;
        this.aEnd = 1f;




        int amount = 0;
        if(spawnDelayInit == 0)
            amount = amountMax;
        else
            amount = (int) (maxLifeTime/spawnDelayInit + 1);
        

        particles = new Particle[amount];
        for(int i = 0; i < particles.length; i++){
            particles[i] = new Particle();
        }


        

        loadTexture(texure);

        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        vertexBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer
        vertexBuffer.put(spriteCoords); //Add the coordinates to the FloatBuffer
        vertexBuffer.position(0);//Set the Buffer to Read the first coordinate
    }

    public void draw(float[] mvpMatrix) {
        if(finished)
            return;

        currentDelay += EngineTools.deltaTime;

        while((amountMax == 0 || amountCurrently < amountMax) && currentDelay >= spawnDelay){
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
            
            particles[currentIndex].rDif = (rEnd - rStart) / maxLifeTime;
            particles[currentIndex].gDif = (gEnd - gStart) / maxLifeTime;
            particles[currentIndex].bDif = (bEnd - bStart) / maxLifeTime;
            particles[currentIndex].aDif = (aEnd - aStart) / maxLifeTime;

            particles[currentIndex].r = rStart + particles[currentIndex].rDif * age;
            particles[currentIndex].g = gStart + particles[currentIndex].gDif * age;
            particles[currentIndex].b = bStart + particles[currentIndex].bDif * age;
            particles[currentIndex].a = aStart + particles[currentIndex].aDif * age;

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


        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandle);
        GL20.glEnableVertexAttribArray(Shader.textureCoordinateHandle);

        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandle, 2,
                GL20.GL_FLOAT, false,
                8, vertexBuffer);

        //Set the active texture unit to texture unit 0.
        GL20.glActiveTexture(GL20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texturePointer[0]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL20.glUniform1i(Shader.textureUniformHandle, 0);

        //Pass in the texture coordinate information
        GL20.glVertexAttribPointer(Shader.textureCoordinateHandle, 2, GL20.GL_FLOAT, false, 0, Shader.textureBuffer);

        finished = true;
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
    
    
            
    
            EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, particles[currentIndex].rotation, particles[currentIndex].position.x, particles[currentIndex].position.y, particles[currentIndex].scale, particles[currentIndex].scale);
    
            // Pass color scale
            GL20.glVertexAttrib4f(Shader.colorScale, particles[currentIndex].r, particles[currentIndex].g, particles[currentIndex].b, particles[currentIndex].a);
    
            // Pass the projection and view transformation to the shader
            GL20.glUniformMatrix4fv(Shader.vPMatrixHandle, false, finalMatrix);
    
            //Draw the triangle
            GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);

            currentIndex = (currentIndex + 1) % particles.length;


        }


        
        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandle);
        GL20.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }

    public void setPositionParent(Position position){
        parentPosition = position;
        lastPosition.x = position.x;
        lastPosition.y = position.y;
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
        
        // Scale SpriteCoords to size
        for (int i = 0; i < 4; i++) {
            spriteCoords[i * 2] *= texturePointerCombined[0];
            spriteCoords[i * 2 + 1] *= texturePointerCombined[1];
        }
    }

    public ParticleController clone(){
        ParticleController particleController = new ParticleController();

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
    
        particleController.rStart = rStart;
        particleController.gStart = gStart;
        particleController.bStart = bStart;
        particleController.aStart = aStart;
    
        particleController.rEnd = rEnd;
        particleController.gEnd = gEnd;
        particleController.bEnd = bEnd;
        particleController.aEnd = aEnd;

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
        particleController.spriteCoords[0] = spriteCoords[0];
        particleController.spriteCoords[1] = spriteCoords[1];
        particleController.spriteCoords[2] = spriteCoords[2];
        particleController.spriteCoords[3] = spriteCoords[3];
        particleController.spriteCoords[4] = spriteCoords[4];
        particleController.spriteCoords[5] = spriteCoords[5];
        particleController.spriteCoords[6] = spriteCoords[6];
        particleController.spriteCoords[7] = spriteCoords[7];

        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        particleController.vertexBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer
        particleController.vertexBuffer.put(spriteCoords); //Add the coordinates to the FloatBuffer
        particleController.vertexBuffer.position(0);//Set the Buffer to Read the first coordinate

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