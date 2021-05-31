package library.drawable;

import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;
import library.addons.OnClickListener;

public class UIClickable extends Sprite {

    public boolean isSelected = false;
    private boolean isClickedDown = false;
    public boolean isClickedDown(){
        return isClickedDown;
    }
    private boolean hasClickAnimation;

    public OnClickListener onClickListener = () -> {};
    public OnClickListener onClickDownListener = () -> {};

    public UIClickable(String assetsPictureName, int columns, int rows) {
        super(assetsPictureName, columns, rows);
        this.hasClickAnimation = false;
    }

    public UIClickable(String assetsPictureName, boolean hasClickAnimation) {
        super(assetsPictureName, hasClickAnimation ? 2 : 1,1);

        this.hasClickAnimation = hasClickAnimation;
    }

    @Override
    public void draw(float[] mvpMatrix){
        // Manage animation
        if(hasClickAnimation && (isClickedDown || isSelected))
            aniIndexImage = 1;
        else
            aniIndexImage = 0;



        EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, 0, position.x, position.y, scaleX*scale, scaleY*scale);

        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandle);

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
        GL20.glEnableVertexAttribArray(Shader.textureCoordinateHandle);



        // Pass the projection and view transformation to the shader
        GL20.glUniformMatrix4fv(Shader.vPMatrixHandle, false, finalMatrix);

        //Draw the triangle
        GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);
        
        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandle);
        GL20.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }

    public void checkClick(float x, float y, boolean clickDown){
        if(position.x + spriteCoords[0]*scale*scaleX < x && position.x - spriteCoords[0]*scale*scaleX > x &&
                position.y + spriteCoords[3]*scale*scaleY < y && position.y - spriteCoords[3]*scale*scaleY > y){

            if(clickDown){
                if(!isClickedDown)
                onClickDownListener.onClick();
                
                isClickedDown = true;
            }
            if(!clickDown && isClickedDown) {
                onClickListener.onClick();
                isClickedDown = false;
            }
        }else if(!clickDown){
            isClickedDown = false;
        }
    }
}