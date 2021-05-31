package library.drawable;

import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;
import library.addons.Animation;

public class SpriteAnimated extends Sprite {

    public Animation animation;
    public boolean alreadyUpdated = false;

    public SpriteAnimated(String assetsPictureName, int columns, int rows) {
        super(assetsPictureName, columns, rows);
    }

    @Override
    public void draw(float[] mvpMatrix) {
        if(!alreadyUpdated)
            animation.update();

        EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, rotation + animation.animRotOffset, 
                position.x + animation.animPosOffset.x, position.y + animation.animPosOffset.y, 
                scaleX*scale*animation.animScale*animation.animScaleX, scaleY*scale*animation.animScale*animation.animScaleY);


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
            GL20.glVertexAttrib4f(Shader.colorScale,1f,1f,1f,1f + animation.animAlphaOffset);
        else
            GL20.glVertexAttrib4f(Shader.colorScale,1f,1f,1f,color.a + animation.animAlphaOffset);


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
}