package library.addons.fonts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import library.EngineTools;
import library.Shader;

public class FontMaster {

    static int amountFonts;

    public static final int RESOLUTION_LOW = 0;
    public static final int RESOLUTION_MEDIUM = 1;
    static final int differentRes = 2;
    static final float[] IMAGESIZE = {256, 512};
    static final int amountChars = 95;
    static final int fontValues = 7;

    static int[][][][] fontData;
    static int[][] pointer;
    static int[][][][] kernings;
    static int[][] maxHeightHalf;

    static FloatBuffer vertexBuffer;
    static FloatBuffer textureCoordinates;
    static float[] textureCoords = new float[8];
    static float[] finalMatrix = new float[16];

    public static void initialize(){
        amountFonts = FontNames.values().length;
        fontData = new int[amountFonts][differentRes][amountChars][fontValues];
        pointer = new int[amountFonts][differentRes];
        kernings = new int[amountFonts][differentRes][amountChars][amountChars];
        maxHeightHalf = new int[amountFonts][differentRes];

        for(FontNames fn : FontNames.values()){
            int id = fn.ordinal();
            String name = fn.name().toLowerCase();
            loadFont(name, id);
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(8 * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        vertexBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer

        bb = ByteBuffer.allocateDirect(8 * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        textureCoordinates = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer


        
        float[] spriteCoords = {0,1, 0,0, 1,0, 1,1};
        vertexBuffer.put(spriteCoords); //Add the coordinates to the FloatBuffer
        vertexBuffer.position(0);//Set the Buffer to Read the first coordinate
    }

    private static void loadFont(String name, int fontId){
        pointer[fontId][0] = EngineTools.loadGLTexture("font/" + name + "-s.png");
        pointer[fontId][1] = EngineTools.loadGLTexture("font/" + name + "-m.png");

        loadData(new File("src/assets/font/" + name + "-s.fnt"), fontId, 0);
		loadData(new File("src/assets/font/" + name + "-m.fnt"), fontId, 1);
    }

    private static void loadData(File file, int fontId, int resolution){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            
            char[] lineAsChar;

            while ((line = br.readLine()) != null){
                lineAsChar = line.toCharArray();

                
                if(lineAsChar[0] == 'c' && lineAsChar[1] == 'h' && lineAsChar[2] == 'a' && lineAsChar[3] == 'r' && lineAsChar[4] != 's'){
                    loadCharInfos(lineAsChar, fontId, resolution);
                }
                    
                
                if(lineAsChar[0] == 'k' && lineAsChar[1] == 'e' && lineAsChar[2] == 'r' && lineAsChar[3] == 'n' && lineAsChar[7] == ' '){
                    loadKernelFromChars(lineAsChar, fontId, resolution);
                }
            }

            br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
        }
    }

    private static void loadKernelFromChars(char[] cArray, int fontId, int resolution){
        int value = 0;
        int first = 0;
        int second = 0;

        int i = 0;
        boolean positive = true;
        boolean readValue = false;
        for(char c : cArray){

            if(c == '='){
                readValue = true;
                continue;
            }
            if(readValue){
                if(c == '-'){
                    positive = false;
                    continue;
                }

                if(c >= '0' && c <= '9'){
                    
                    value *= 10;
                    if(positive)
                        value += c-48;
                    else
                        value -= c-48;

                }else{
                    readValue = false;
                    positive = true;
                    if(i == 0){
                        first = value;
                        i++;
                    }else if(i == 1){
                        second = value;
                        i++;
                    }else{
                        kernings[fontId][resolution][first-32][second-32] = value;
                        i = 0;
                    }
                    value = 0;
                }
            }
        }
    }

    private static void loadCharInfos(char[] cArray, int fontId, int resolution){
        int value = 0;
        int i = -1;
        int charId = 0;
        boolean positive = true;
        boolean readValue = false;
        for(char c : cArray){

            if(c == '='){
                readValue = true;
                continue;
            }
            if(readValue){
                if(c == '-'){
                    positive = false;
                    continue;
                }

                if(c >= '0' && c <= '9'){
                    
                    value *= 10;
                    if(positive)
                        value += c-48;
                    else
                        value -= c-48;

                }else{
                    readValue = false;
                    positive = true;
                    if(i == -1){
                        charId = value;
                        i++;
                    }else{
                        fontData[fontId][resolution][charId-32][i++] = value;
                    }
                    if(i == 7)
                        break;
                    value = 0;
                }
            }
        }

        if(fontData[fontId][resolution][charId-32][3] + fontData[fontId][resolution][charId-32][5] > maxHeightHalf[fontId][resolution]*2)
            maxHeightHalf[fontId][resolution] = (fontData[fontId][resolution][charId-32][3] + fontData[fontId][resolution][charId-32][5]) / 2;
    }

    public static void draw(float[] mvpMatrix, Font font){
        draw(mvpMatrix, font, font.text);
    }
    
    public static void draw(float[] mvpMatrix, Font font, String text){
        char[] charArray = text.toCharArray();
        int fontId = font.name.ordinal();
        
        //Add program to OpenGL ES Environment
        GL20.glUseProgram(Shader.mProgram);

        //Enable a handle to the triangle vertices
        GL20.glEnableVertexAttribArray(Shader.positionHandle);
        GL20.glEnableVertexAttribArray(Shader.textureCoordinateHandle);

        // Pass color scale
        GL20.glVertexAttrib4f(Shader.colorScale, font.color.r, font.color.g, font.color.b, font.color.a);

        //Set the active texture unit to texture unit 0.
        GL20.glActiveTexture(GL20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, pointer[fontId][font.resolution]);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GL20.glUniform1i(Shader.textureUniformHandle, 0);

        //Prepare the triangle coordinate data
        GL20.glVertexAttribPointer(Shader.positionHandle, 2, GL20.GL_FLOAT, false, 0, vertexBuffer);




        float curserPosX = 0;
        float curserPosY = 0;

        if(font.center){
            for(int i = 0; i < charArray.length; i++){
                int currentChar = charArray[i] - 32;
                int kerningOffset = 0;

                if(i < charArray.length - 1){
                    kerningOffset = kernings[fontId][font.resolution][currentChar][charArray[i+1]-32];
                }
    
                curserPosX += (fontData[fontId][font.resolution][currentChar][6] + kerningOffset) * font.scale;
            }

            curserPosX = -curserPosX/2;
        }





        for(int i = 0; i < charArray.length; i++){
            int currentChar = charArray[i] - 32;
            int kerningOffset = 0;
            if(i < charArray.length - 1){
                kerningOffset = kernings[fontId][font.resolution][currentChar][charArray[i+1]-32];
            }

            float xMin = (float)fontData[fontId][font.resolution][currentChar][0] / IMAGESIZE[font.resolution];
            float yMin = (float)fontData[fontId][font.resolution][currentChar][1] / IMAGESIZE[font.resolution];
            float xMax = xMin + (float)fontData[fontId][font.resolution][currentChar][2] / IMAGESIZE[font.resolution];
            float yMax = yMin + (float)fontData[fontId][font.resolution][currentChar][3] / IMAGESIZE[font.resolution];
            
            textureCoords[0] = xMin;
            textureCoords[1] = yMin;
            textureCoords[2] = xMin;
            textureCoords[3] = yMax;
            textureCoords[4] = xMax;
            textureCoords[5] = yMax;
            textureCoords[6] = xMax;
            textureCoords[7] = yMin;

            textureCoordinates.put(textureCoords); //Add the coordinates to the FloatBuffer
            textureCoordinates.position(0);//Set the Buffer to Read the first coordinate

            //Pass in the texture coordinate information
            GL20.glVertexAttribPointer(Shader.textureCoordinateHandle, 2, GL20.GL_FLOAT, false, 0, textureCoordinates);


            EngineTools.matrixRotPosScale(finalMatrix, mvpMatrix, font.rotation, 
                font.position.x + fontData[fontId][font.resolution][currentChar][4] * font.scale + curserPosX, font.position.y + (maxHeightHalf[fontId][font.resolution] - fontData[fontId][font.resolution][currentChar][3] - fontData[fontId][font.resolution][currentChar][5]) * font.scale + curserPosY, 
                font.scale * fontData[fontId][font.resolution][currentChar][2], font.scale * fontData[fontId][font.resolution][currentChar][3]);

            // Pass the projection and view transformation to the shader
            GL20.glUniformMatrix4fv(Shader.vPMatrixHandle, false, finalMatrix);

            //Draw the triangle
            GL20.glDrawElements(GL20.GL_TRIANGLES, Shader.drawOrderBuffer);

            

            if(font.rotation != 0){
                curserPosY += ((fontData[fontId][font.resolution][currentChar][6] + kerningOffset) * font.scale) * Math.sin(Math.toRadians(font.rotation));
                curserPosX += ((fontData[fontId][font.resolution][currentChar][6] + kerningOffset) * font.scale) * Math.cos(Math.toRadians(font.rotation));
            }else{
                curserPosX += (fontData[fontId][font.resolution][currentChar][6] + kerningOffset) * font.scale;
            }
                
        }

        font.endPos.x = font.position.x + curserPosX;
        font.endPos.y = font.position.y + curserPosY;


        //Disable Vertex Array
        GL20.glDisableVertexAttribArray(Shader.positionHandle);
        GL20.glDisableVertexAttribArray(Shader.textureCoordinateHandle);
    }
}