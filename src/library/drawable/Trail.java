package library.drawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;
import library.addons.Color;
import library.addons.Position;

public class Trail {
    
    private Position parentPosition;
    public void setParentPosition(Position position){
        parentPosition = position;
        if(parentAnimationOffset == null){
            for(Position p : pos){
                p.x = position.x;
                p.y = position.y;
            }
        }else{
            for(Position p : pos){
                p.x = position.x + parentAnimationOffset.x;
                p.y = position.y + parentAnimationOffset.y;
            }
        }
        
    }
    private Position parentAnimationOffset;
    public void setParentAnimationPosition(Position position){
        parentAnimationOffset = position;
        for(Position p : pos){
            p.x = parentPosition.x + position.x;
            p.y = parentPosition.y + position.y;
        }
    }
    public float width;
    public float timeLength;
    public int steps;
    public Color color;

    // Are the sprite Coordinates
    FloatBuffer vertexBuffer;
    // Buffer holding the texture coordinates
    FloatBuffer textureBuffer;
    // Is the draw Order
    ShortBuffer drawOrderBuffer;

    private int[] texturePointer;
    private float[] vertexPos;
    private float[] texturePos;
    private short[] drawOrder;
    protected Position[] pos;

    Position vector = new Position();
    float lastDirection;

    protected Trail(){

    }

    public Trail(String assetsPictureName, float width, int steps){
        //Load Texture(s) and set sprite size
        loadGLTexture(assetsPictureName);
        this.width = width;

        pos = new Position[steps+1];
        for(int i = 0; i < pos.length; i++){
            pos[i] = new Position();
        }
        vertexPos = new float[4 + steps*4];
        texturePos = new float[4 + steps*4];
        drawOrder = new short[steps*6];
        
        for(int i = 0, t = 0; i < texturePos.length; i++){
            texturePos[i++] = t*1f/steps;
            texturePos[i++] = 0f;
            
            texturePos[i++] = t*1f/steps;
            texturePos[i] = 1f;
            t++;
        }

        drawOrder[0] = 0;
        drawOrder[1] = 1;
        drawOrder[2] = 3;

        drawOrder[3] = 0;
        drawOrder[4] = 2;
        drawOrder[5] = 3;

        for(int i = 6, t = 3; i < drawOrder.length; i++){
            drawOrder[i++] = (short) (t);
            drawOrder[i++] = (short) (t - 1);
            drawOrder[i++] = (short) (t + 1);


            drawOrder[i++] = (short) (t);
            drawOrder[i++] = (short) (t + 1);
            drawOrder[i] = (short) (t + 2);

            t+=2;
        }

        //Initialize Byte Buffer for Shape Coordinates
        loadVertex();
    }

    public void draw(float[] mvpMatrix){

        for(int i = pos.length-2; i >= 0; i--){
            pos[i+1].x = pos[i].x;
            pos[i+1].y = pos[i].y;
        }
        if(parentAnimationOffset == null){
            pos[0].x = parentPosition.x;
            pos[0].y = parentPosition.y;
        }else{
            pos[0].x = parentAnimationOffset.x + parentPosition.x;
            pos[0].y = parentAnimationOffset.y + parentPosition.y;
        }
        

        int i = 0;
        boolean change = true;
        for(int t = 0; t < pos.length-1; t++){
            vector.x = pos[t+1].y - pos[t].y;
            vector.y = pos[t].x - pos[t+1].x;
            float sqrt = (float) Math.sqrt(vector.x*vector.x + vector.y*vector.y);
            if(sqrt == 0){
                vector.x = 1;
                vector.y = 1;
            }else{
                vector.x /= sqrt;
                vector.y /= sqrt;
            }

            float direction = (float)Math.atan2(vector.y, vector.x);
            float c = direction - lastDirection;
            c = c < 0 ? -c : c;

            if(c > 1.570796f && c < 4.712388f)
                change = !change;

            if(change){
                vertexPos[i++] = pos[t].x + vector.x*width;
                vertexPos[i++] = pos[t].y + vector.y*width;

                vertexPos[i++] = pos[t].x - vector.x*width;
                vertexPos[i++] = pos[t].y - vector.y*width;
            }else{
                vertexPos[i++] = pos[t].x - vector.x*width;
                vertexPos[i++] = pos[t].y - vector.y*width;

                vertexPos[i++] = pos[t].x + vector.x*width;
                vertexPos[i++] = pos[t].y + vector.y*width;
            }
            lastDirection = direction;
        }
        if(change){
            vertexPos[i++] = pos[pos.length-1].x + vector.x*width;
            vertexPos[i++] = pos[pos.length-1].y + vector.y*width;

            vertexPos[i++] = pos[pos.length-1].x - vector.x*width;
            vertexPos[i++] = pos[pos.length-1].y - vector.y*width;
        }else{
            vertexPos[i++] = pos[pos.length-1].x - vector.x*width;
            vertexPos[i++] = pos[pos.length-1].y - vector.y*width;

            vertexPos[i++] = pos[pos.length-1].x + vector.x*width;
            vertexPos[i++] = pos[pos.length-1].y + vector.y*width;
        }

        
        vertexBuffer.put(vertexPos); //Add the coordinates to the FloatBuffer
        vertexBuffer.position(0);//Set the Buffer to Read the first coordinate

        
        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandle);
        GL20.glEnableVertexAttribArray(Shader.textureCoordinateHandle);
        
        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandle, 2,
        		GL20.GL_FLOAT, false,
                0, vertexBuffer);



        // Pass color scale
        GL20.glVertexAttrib4f(Shader.colorScale,color.r,color.g,color.b,color.a);


        //Set the active texture unit to texture unit 0.
        GL20.glActiveTexture(GL20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texturePointer[0]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL20.glUniform1i(Shader.textureUniformHandle, 0);

        //Pass in the texture coordinate information
        GL20.glVertexAttribPointer(Shader.textureCoordinateHandle, 2, GL20.GL_FLOAT, false, 0, textureBuffer);
        



        // Pass the projection and view transformation to the shader
        GL20.glUniformMatrix4fv(Shader.vPMatrixHandle, false, mvpMatrix);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, drawOrderBuffer);
        
        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandle);
        GL20.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }

    private void loadGLTexture(String assetsPictureName) {
    	int[] texturePointerCombined = EngineTools.loadGLTexture(assetsPictureName, 1, 1);
        texturePointer = new int[texturePointerCombined.length - 2];

        for (int i = 0; i < texturePointer.length; i++) {
            texturePointer[i] = texturePointerCombined[i + 2];
        }
    }

    private void loadVertex(){
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexPos.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        vertexBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer

        bb = ByteBuffer.allocateDirect(texturePos.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        textureBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer
        textureBuffer.put(texturePos); //Add the coordinates to the FloatBuffer
        textureBuffer.position(0);//Set the Buffer to Read the first coordinate

        // initialize byte buffer for the draw list
        bb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        bb.order(ByteOrder.nativeOrder());
        drawOrderBuffer = bb.asShortBuffer();
        drawOrderBuffer.put(drawOrder);
        drawOrderBuffer.position(0);
    }

    public Trail clone(){
        Trail trail = new Trail();
        trail.width = width;
        trail.timeLength = timeLength;
        trail.steps = steps;
        trail.color = color;

        trail.texturePointer = texturePointer.clone();

        trail.drawOrder = new short[drawOrder.length];
        trail.vertexPos = new float[vertexPos.length];
        trail.texturePos = texturePos.clone();

        trail.pos = new Position[pos.length];
        for(int i = 0; i < trail.pos.length; i++){
            trail.pos[i] = new Position();
        }

        trail.drawOrder = new short[drawOrder.length];
        trail.drawOrder[0] = 0;
        trail.drawOrder[1] = 1;
        trail.drawOrder[2] = 3;

        trail.drawOrder[3] = 0;
        trail.drawOrder[4] = 2;
        trail.drawOrder[5] = 3;

        for(int i = 6, t = 3; i < trail.drawOrder.length; i++){
            trail.drawOrder[i++] = (short) (t);
            trail.drawOrder[i++] = (short) (t - 1);
            trail.drawOrder[i++] = (short) (t + 1);


            trail.drawOrder[i++] = (short) (t);
            trail.drawOrder[i++] = (short) (t + 1);
            trail.drawOrder[i] = (short) (t + 2);

            t+=2;
        }

        trail.loadVertex();
        return trail;
    }

}