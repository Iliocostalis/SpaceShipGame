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

public class HealthBar {

    FloatBuffer vertexBufferBorder;
    FloatBuffer vertexBufferHealth;
    ShortBuffer drawOrderBufferBorder;
    short[] drawOrderBorder = {1,0,2, 1,2,3, 3,2,4, 3,4,5, 5,4,6, 5,6,7, 7,6,0, 7,0,1};
    Color colorBorder;
    Color colorHealth;
    Color colorBackground;

    public Position position = new Position();
    float scaleX = 1;
    float scaleY = 1;
    float scale = 1;
    float rotation;
    public float width;
    public float height;
    public float borderThickness;

    float[] finalMatrix = new float[16];
    float[] vertexHealth = new float[8];

    float percentage = 1;
    public void setPercentage(float value){
        if(value > 1)
            value = 1;
        if(value < 0)
            value = 0;

        percentage = value;
    }
    public float getPercentage(){
        return percentage;
    }

    public HealthBar(float width, float height, float borderThickness, Color colorBorder, Color colorHealth, Color colorBackground){
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.colorBorder = colorBorder;
        this.colorHealth = colorHealth;
        this.colorBackground = colorBackground;

        ByteBuffer bb = ByteBuffer.allocateDirect(32);
        bb.order(ByteOrder.nativeOrder());
        vertexBufferHealth = bb.asFloatBuffer();

        //Bottom Left
        vertexHealth[0] = (-width/2 + borderThickness);
        vertexHealth[1] = (-height/2 + borderThickness);

        // Top Left
        vertexHealth[2] = (-width/2 + borderThickness);
        vertexHealth[3] = (height/2 - borderThickness);

        // Top Right
        vertexHealth[4] = (width/2 - borderThickness);
        vertexHealth[5] = (height/2 - borderThickness);

        // Bottom Right
        vertexHealth[6] = (width/2 - borderThickness);
        vertexHealth[7] = (-height/2 + borderThickness);

        

        vertexBufferHealth.put(vertexHealth);
        vertexBufferHealth.position(0);


        bb = ByteBuffer.allocateDirect(64);
        bb.order(ByteOrder.nativeOrder());
        vertexBufferBorder = bb.asFloatBuffer();

        //Bottom Left
        vertexBufferBorder.put(-width/2);
        vertexBufferBorder.put(-height/2);
        vertexBufferBorder.put(-width/2 + borderThickness);
        vertexBufferBorder.put(-height/2 + borderThickness);

        // Bottom Right
        vertexBufferBorder.put(width/2);
        vertexBufferBorder.put(-height/2);
        vertexBufferBorder.put(width/2 - borderThickness);
        vertexBufferBorder.put(-height/2 + borderThickness);

        // Top Right
        vertexBufferBorder.put(width/2);
        vertexBufferBorder.put(height/2);
        vertexBufferBorder.put(width/2 - borderThickness);
        vertexBufferBorder.put(height/2 - borderThickness);

        // Top Left
        vertexBufferBorder.put(-width/2);
        vertexBufferBorder.put(height/2);
        vertexBufferBorder.put(-width/2 + borderThickness);
        vertexBufferBorder.put(height/2 - borderThickness);

        vertexBufferBorder.position(0);

        bb = ByteBuffer.allocateDirect(48);
        bb.order(ByteOrder.nativeOrder());
        drawOrderBufferBorder = bb.asShortBuffer();
        drawOrderBufferBorder.put(drawOrderBorder);

        drawOrderBufferBorder.position(0);
    }

    public void draw(float[] mvpMatrix){
        EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, rotation, position.x, position.y, scaleX*scale, scaleY*scale);

        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.simpleProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandleSimple);

        // Pass the projection and view transformation to the shader
        GL20.glUniformMatrix4fv(Shader.vPMatrixHandleSimple, false, finalMatrix);




        //// Draw Border ////
        // Pass color
        GL20.glVertexAttrib4f(Shader.colorSimple, colorBorder.r, colorBorder.g, colorBorder.b, colorBorder.a);

        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandleSimple, 2,
                GL20.GL_FLOAT, false,
                0, vertexBufferBorder);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, drawOrderBufferBorder);

        

        ////// Draw Health Background //////
        vertexHealth[4] = -width / 2 + borderThickness + (width / 2 - borderThickness) - vertexHealth[0];
        vertexHealth[6] = vertexHealth[4];
        vertexBufferHealth.put(vertexHealth);
        vertexBufferHealth.position(0);

        // Pass color
        GL20.glVertexAttrib4f(Shader.colorSimple, colorBackground.r, colorBackground.g, colorBackground.b, colorBackground.a);

        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandleSimple, 2,
                GL20.GL_FLOAT, false,
                0, vertexBufferHealth);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);



        ////// Draw Health //////
        vertexHealth[4] = -width / 2 + borderThickness + ((width / 2 - borderThickness) - vertexHealth[0])*percentage;
        vertexHealth[6] = vertexHealth[4];
        vertexBufferHealth.put(vertexHealth);
        vertexBufferHealth.position(0);

        // Pass color
        GL20.glVertexAttrib4f(Shader.colorSimple, colorHealth.r, colorHealth.g, colorHealth.b, colorHealth.a);

        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandleSimple, 2,
                GL20.GL_FLOAT, false,
                0, vertexBufferHealth);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);

        

        
        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandleSimple);
    }
}
