package library.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;
import library.addons.Color;
import library.addons.Position;

public class Sprite {

    // Are the sprite Coordinates
    public FloatBuffer vertexBuffer;

    float spriteCoords[] = { -0.5f,  0.5f,   // top left
                                     -0.5f, -0.5f,   // bottom left
                                      0.5f, -0.5f,   // bottom right
                                      0.5f,  0.5f }; //top right

    public float texHeight = 0, texWidth = 0;

    // The texture pointer
    public int[] texturePointer;
    int aniIndexImage = 0;
    public void setAnimationImageIndex(int index){
        if(index < 0)
            index = 0;
        if(index > texturePointer.length)
            index = texturePointer.length;

        aniIndexImage = index;
    }
    public int getAniImageIndex(){
        return aniIndexImage;
    }

    public Position position = new Position();
    public float rotation = 0;
    public Color color;


    float scale = 1;
    float scaleX = 1;
    float scaleY = 1;

    public float getScale() {
        return scale;
    }
    public float getScaleX() {
        return scaleX;
    }
    public float getScaleY() {
        return scaleY;
    }
    public void setScale(float scale) {
        texHeight /= this.scale;
        texWidth /= this.scale;

        texHeight *= scale;
        texWidth *= scale;

        this.scale = scale;
    }
    public void setScaleX(float scaleX) {
        texWidth /= this.scaleX;
        texWidth *= scaleX;

        this.scaleX = scaleX;
    }
    public void setScaleY(float scaleY) {
        texHeight /= this.scaleY;
        texHeight *= scaleY;

        this.scaleY = scaleY;
    }

    float[] rotationMatrix = new float[16];
    float[] finalMatrix = new float[16];
    float[] mvpMatrix = new float[16];


    public Sprite(int textureId, float SizeX, float SizeY) {
        texturePointer = new int[1];
        texturePointer[0] = textureId;

        // Scale Sprite to bitmap size
        scalePlane(SizeX, SizeY);
        //Initialize Byte Buffer for Shape Coordinates
        loadVertex();
    }

    public Sprite(String assetsPictureName, int columns, int rows) {
        //Load Texture(s) and set sprite size
        loadGLTexture(assetsPictureName, columns, rows);

        //Initialize Byte Buffer for Shape Coordinates
        loadVertex();
    }

    protected Sprite(){

    }

    protected void preDraw(){

    }

    public void draw(float[] mvpMatrix) {
        /*  // Rotate around other Point
        Matrix.setRotateM(rotationMatrix, 0, rotation, 0, 0, 1.0f);
        Matrix.translateM(rotationMatrix, 0, 0, 100f, 0);
        Matrix.multiplyMM(finalMatrix, mvpMatrix, rotationMatrix);
        Matrix.translateM(finalMatrix, 0, -1, 1, 0);
        Matrix.scaleM(finalMatrix,0,scale,scale,1); */
        this.mvpMatrix = mvpMatrix;
        preDraw();
        EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, rotation, position.x, position.y, scaleX*scale, scaleY*scale);

        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandle);
        GL20.glEnableVertexAttribArray(Shader.textureCoordinateHandle);
        
        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandle, 2,
        		GL20.GL_FLOAT, false,
                8, vertexBuffer);



        // Pass color scale
        if(color == null)
            GL20.glVertexAttrib4f(Shader.colorScale,1, 1, 1, 1);
        else
            GL20.glVertexAttrib4f(Shader.colorScale, color.r, color.g, color.b, color.a);


        //Set the active texture unit to texture unit 0.
        GL20.glActiveTexture(GL20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texturePointer[aniIndexImage]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL20.glUniform1i(Shader.textureUniformHandle, 0);

        //Pass in the texture coordinate information
        GL20.glVertexAttribPointer(Shader.textureCoordinateHandle, 2, GL20.GL_FLOAT, false, 0, Shader.textureBuffer);
        



        // Pass the projection and view transformation to the shader
        GL20.glUniformMatrix4fv(Shader.vPMatrixHandle, false, finalMatrix);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);
        
        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandle);
        GL20.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }
    
    private void loadGLTexture(String assetsPictureName, int columns, int rows) {
    	int[] texturePointerCombined = EngineTools.loadGLTexture(assetsPictureName, columns, rows);
        texturePointer = new int[texturePointerCombined.length - 2];

        for (int i = 0; i < texturePointer.length; i++) {
            texturePointer[i] = texturePointerCombined[i + 2];
        }
        
        // Scale Sprite to bitmap size
        scalePlane(texturePointerCombined[0],texturePointerCombined[1]);
    }

    private void scalePlane(float width, float height){
        // Scale Sprite to bitmap size
        for (int i = 0; i < 4; i++) {
            spriteCoords[i * 2] *= width;
            spriteCoords[i * 2 + 1] *= height;
        }
        texWidth = width;
        texHeight = height;
    }

    protected void loadVertex(){
        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        vertexBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer
        vertexBuffer.put(spriteCoords); //Add the coordinates to the FloatBuffer
        vertexBuffer.position(0);//Set the Buffer to Read the first coordinate
    }

    public Sprite clone(){

        Sprite sprite = new Sprite();

        sprite.spriteCoords[0] = spriteCoords[0];
        sprite.spriteCoords[1] = spriteCoords[1];
        sprite.spriteCoords[2] = spriteCoords[2];
        sprite.spriteCoords[3] = spriteCoords[3];
        sprite.spriteCoords[4] = spriteCoords[4];
        sprite.spriteCoords[5] = spriteCoords[5];
        sprite.spriteCoords[6] = spriteCoords[6];
        sprite.spriteCoords[7] = spriteCoords[7];

        sprite.loadVertex();

        sprite.texHeight = texHeight;
        sprite.texWidth = texWidth;

        sprite.texturePointer = texturePointer.clone();

        sprite.scale = scale;
        sprite.scaleX = scaleX;
        sprite.scaleY = scaleY;

        return sprite;
    }   
}