package library;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.lwjgl.opengl.GL20;

public class Shader {

    // Is the draw Order
    public static ShortBuffer drawOrderBuffer;
    // Buffer holding the texture coordinates
    public static FloatBuffer textureBuffer;

    // OpenGL ES Program
    public static int mProgram;
    public static int particleProgram;
    public static int simpleProgram;

    // Use to access and set the values in shader
    // Main
    public static int vPMatrixHandle;
    public static int positionHandle;
    public static int colorScale;
    public static int textureUniformHandle;
    public static int textureCoordinateHandle;

    // Particle
    public static int positionHandleParticle;
    public static int colorScaleParticle;
    public static int textureUniformHandleParticle;
    public static int textureCoordinateHandleParticle;

    // Simple
    public static int vPMatrixHandleSimple;
    public static int positionHandleSimple;
    public static int colorSimple;

    public static void initialize() {
        /** Load Shader and create Program */

        // Load Shader
        loadMainShader();
        loadParticleShader();
        loadSimpleShader();
        
        /** Load Buffer used by all Sprites */
        float[] texture = { 0f, 0f,   0f, 1f,   1f, 1f,   1f, 0f };
        short[] drawOrder = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    
        ByteBuffer bb = ByteBuffer.allocateDirect(texture.length * 4);
        bb.order(ByteOrder.nativeOrder()); //Use the Device's Native Byte Order
        textureBuffer = bb.asFloatBuffer(); //Create a floating point buffer from the ByteBuffer
        textureBuffer.put(texture); //Add the coordinates to the FloatBuffer
        textureBuffer.position(0);//Set the Buffer to Read the first coordinate

        // initialize byte buffer for the draw list
        bb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        bb.order(ByteOrder.nativeOrder());
        drawOrderBuffer = bb.asShortBuffer();
        drawOrderBuffer.put(drawOrder);
        drawOrderBuffer.position(0);
    }

    private static void loadMainShader(){
        int vertexShader = loadShader(GL20.GL_VERTEX_SHADER, "vsMain.txt");
        int fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, "fsMain.txt");

        mProgram = loadProgramm(vertexShader, fragmentShader);

        // Load all Handles
        colorScale = GL20.glGetAttribLocation(mProgram, "colorScale");
        positionHandle = GL20.glGetAttribLocation(mProgram, "vPosition");
        textureUniformHandle = GL20.glGetUniformLocation(mProgram, "s_Texture");
        textureCoordinateHandle = GL20.glGetAttribLocation(mProgram, "a_texCoord");
        vPMatrixHandle = GL20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private static void loadParticleShader(){
        int vertexShader = loadShader(GL20.GL_VERTEX_SHADER, "particleVS.txt");
        int fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, "particleFS.txt");

        particleProgram = loadProgramm(vertexShader, fragmentShader);

        // Load all Handles
        colorScaleParticle = GL20.glGetAttribLocation(particleProgram, "colorScale");
        positionHandleParticle = GL20.glGetAttribLocation(particleProgram, "vPosition");
        textureUniformHandleParticle = GL20.glGetUniformLocation(particleProgram, "s_Texture");
        textureCoordinateHandleParticle = GL20.glGetAttribLocation(particleProgram, "a_texCoord");
    }

    private static void loadSimpleShader(){
        int vertexShader = loadShader(GL20.GL_VERTEX_SHADER, "simpleVS.txt");
        int fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, "simpleFS.txt");

        simpleProgram = loadProgramm(vertexShader, fragmentShader);

        // Load all Handles
        colorSimple = GL20.glGetAttribLocation(simpleProgram, "v_Color");
        positionHandleSimple = GL20.glGetAttribLocation(simpleProgram, "vPosition");
        vPMatrixHandleSimple = GL20.glGetUniformLocation(simpleProgram, "uMVPMatrix");
    }

    public static int loadProgramm(int vertexShader, int fragmentShader){
        // create empty OpenGL ES Program
        int program = GL20.glCreateProgram();

        // add the vertex shader to program
        GL20.glAttachShader(program, vertexShader);

        // add the fragment shader to program
        GL20.glAttachShader(program, fragmentShader);

        // creates OpenGL ES program executables
        GL20.glLinkProgram(program);

        return program;
    }

    public static int loadShader(int type, String shaderName) {
        int shader = GL20.glCreateShader(type);

        String path = "src/Library/ShaderCodes/" + shaderName;
        String shaderCode = "";

		try {
			shaderCode = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
        }
        
        // add the source code to the shader and compile it
        GL20.glShaderSource(shader, shaderCode);
        GL20.glCompileShader(shader);

        return shader;
    }
}