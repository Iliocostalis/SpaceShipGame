package library;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL30;

import library.addons.Matrix;

public class FrameBuffer {
    public static int[] framebuffer = new int[2];
    public static int[] colorTextureBuffer = new int[2];

    static FloatBuffer textureBuffer;
    static FloatBuffer vertexBuffer;
    static float textureCoords[] = new float[8];
    static float spriteCoords[] = new float[8];
    static float[] projectionMatrix = new float[16];
    static float[] viewMatrix = new float[16];
    static float[] vPMatrix = new float[16];

    public static void initialize(){
        // init our fbo
        framebuffer[1] = GL30.glGenFramebuffers();                          // create a new framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer[1]);						// switch to the new framebuffer
        // initialize color texture
        colorTextureBuffer[1] = GL30.glGenTextures();                                               // and a new texture used as a color buffer
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, colorTextureBuffer[1]);                                   // Bind the colorbuffer texture
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, EngineTools.windowWidth, EngineTools.windowHeight, 0,GL30. GL_RGBA, GL30.GL_INT, (java.nio.ByteBuffer) null);  // Create the texture data

        GL30.glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);               // make it linear filterd
        GL30. glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);               // make it linear filterd

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, colorTextureBuffer[1], 0); // attach it to the framebuffer

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE)
            System.out.println("ok");
            
        ByteBuffer bb = ByteBuffer.allocateDirect(32);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        bb = ByteBuffer.allocateDirect(32);
        bb.order(ByteOrder.nativeOrder());
        textureBuffer = bb.asFloatBuffer();
    }

    public static void drawArea(int index, float xMin, float xMax, float yMin, float yMax){
        textureCoords[0] = xMin;
        textureCoords[1] = yMin;
        textureCoords[2] = xMin;
        textureCoords[3] = yMax;
        textureCoords[4] = xMax;
        textureCoords[5] = yMax;
        textureCoords[6] = xMax;
        textureCoords[7] = xMin;

        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        spriteCoords[0] = xMin;
        spriteCoords[1] = yMin;

        spriteCoords[2] = xMin;
        spriteCoords[3] = yMax;

        spriteCoords[4] = xMax;
        spriteCoords[5] = yMax;

        spriteCoords[6] = xMax;
        spriteCoords[7] = yMin;

        vertexBuffer.put(spriteCoords);
        vertexBuffer.position(0);


        Matrix.frustumM(projectionMatrix, 0, 0, 1, 0, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, projectionMatrix, viewMatrix);


        //Add program to OpenGL ES Environment
        GL30.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL30.glEnableVertexAttribArray(Shader.positionHandle);
        GL30.glEnableVertexAttribArray(Shader.textureCoordinateHandle);

        //Prepare the triangle coordinate data
        GL30.glVertexAttribPointer(Shader.positionHandle, 2,
                GL30.GL_FLOAT, false,
                8, vertexBuffer);

        GL30.glVertexAttrib4f(Shader.colorScale,1, 1, 1, 1);

        //Set the active texture unit to texture unit 0.
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, colorTextureBuffer[index]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL30.glUniform1i(Shader.textureUniformHandle, 0);

        //Pass in the texture coordinate information
        GL30.glVertexAttribPointer(Shader.textureCoordinateHandle, 2, GL30.GL_FLOAT, false, 0, textureBuffer);

        // Pass the projection and view transformation to the shader
        GL30.glUniformMatrix4fv(Shader.vPMatrixHandle, false, vPMatrix);

        //Draw the triangle
        GL30.glDrawElements(GL30.GL_TRIANGLES, Shader.drawOrderBuffer);

        //Disable Vertex Array
        GL30.glDisableVertexAttribArray(Shader.positionHandle);
        GL30.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }

    public static void clear(int index){

        switch (index){
            case 0:
                GL30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
                break;
            case 1:
                GL30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer[index]);
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
    }

    public static void select(int index){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer[index]);   // Switch back to selected framebuffer
    }
}
