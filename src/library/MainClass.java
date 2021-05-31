package library;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import library.addons.Matrix;
import library.screens.Stats;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MainClass {

	// The window handle
	private long window;
	private int windowWidth = 1080/2;
	private int windowHeight = 1920/2;
	private int virutalWidth = 1440;
	private int virutalHeight = 2560;
	
	private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    
    DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);;
    DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);;
    IntBuffer windowWidthBuffer = BufferUtils.createIntBuffer(1);
	IntBuffer windowHeightBuffer = BufferUtils.createIntBuffer(1);

	boolean enableVSync = true;
	boolean fullscreen = false;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_REFRESH_RATE, 120);

		
		if(fullscreen){
			// Create the fullscreen window
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			window = glfwCreateWindow(vidmode.width(), vidmode.height(), "Hello World!", glfwGetPrimaryMonitor(), NULL);
		}else{
			// Create the window
			window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL);
			if ( window == NULL )
				throw new RuntimeException("Failed to create the GLFW window");

			// Get the thread stack and push a new frame
			try ( MemoryStack stack = stackPush() ) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				// Get the window size passed to glfwCreateWindow
				glfwGetWindowSize(window, pWidth, pHeight);

				// Get the resolution of the primary monitor
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

				// Center the window
				glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
				);
			} // the stack frame is popped automatically
		}
		

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		

		// Enable v-sync
		if(enableVSync)
			glfwSwapInterval(1);
		else
			glfwSwapInterval(0);
		
		

		// Make the window visible
		glfwShowWindow(window);
		
		GL.createCapabilities();


		glfwGetWindowSize(window, windowWidthBuffer, windowHeightBuffer);
		glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL30.GL_DEPTH_TEST);
		glEnable(GL_BLEND);

		EngineTools.screenWidth = virutalWidth;
		EngineTools.screenHeight = virutalHeight;
		//EngineTools.screenWidth = (int)windowWidthBuffer.get(0);
		//EngineTools.screenHeight = (int)windowHeightBuffer.get(0);
		EngineTools.windowWidth = (int)windowWidthBuffer.get(0);
		EngineTools.windowHeight = (int)windowHeightBuffer.get(0);
		EngineTools.mClass = getClass();
		
		EngineTools.initialise();
	}
	
	private void loop() {
        
        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
		    @Override
		    public void invoke(final long window, final int buttonId, final int action, final int mods) {
			  glfwGetCursorPos(window, posX, posY);
		      GameClass.onTouch((float)posX.get(0) * virutalWidth/windowWidth, virutalHeight - (float)posY.get(0) * virutalHeight/windowHeight, buttonId, action);
		    }
		});
		
		
		glfwSetScrollCallback(window, new GLFWScrollCallback() {
			@Override
		    public void invoke(final long window, final double xoffset, final double yoffset) {
				GameClass.scrollYoffset = (float) yoffset;
		    }
		});

		glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
				if (action == GLFW_PRESS || action == GLFW_REPEAT){
					GameClass.keyPress(key, true);
				}
				else if (action == GLFW_RELEASE)
					GameClass.keyPress(key, false);

				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, true);
			}
		});


		GL20.glViewport(0, 0, windowWidth, windowHeight);
		Matrix.frustumM(projectionMatrix, 0, 0, EngineTools.screenWidth, 0, EngineTools.screenHeight, 3, 7);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		Matrix.multiplyMM(vPMatrix, projectionMatrix, viewMatrix);

		double lastTime = 0;
        // Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {

			FrameBuffer.clear(1);
			FrameBuffer.clear(0);
			
			EngineTools.screenChanged = false;

			glfwGetWindowSize(window, windowWidthBuffer, windowHeightBuffer);
			if(EngineTools.windowWidth != windowWidthBuffer.get(0) || EngineTools.windowHeight != windowHeightBuffer.get(0)) {
				EngineTools.screenWidthLast = EngineTools.screenWidth;
				EngineTools.screenHeightLast = EngineTools.screenHeight;

				EngineTools.screenWidth = virutalWidth;
				EngineTools.screenHeight = virutalHeight;
				EngineTools.screenChanged = true;

				EngineTools.windowHeight = windowWidthBuffer.get(0);
				EngineTools.windowHeight = windowHeightBuffer.get(0);
				
				glBindFramebuffer(GL_FRAMEBUFFER, 0);  
				glViewport(0, 0, windowWidth, windowHeight);

				Matrix.frustumM(projectionMatrix, 0, 0, EngineTools.screenWidth, 0, EngineTools.screenHeight, 3, 7);
				Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
				Matrix.multiplyMM(vPMatrix, projectionMatrix, viewMatrix);
			}
			
			double time = glfwGetTime();
			EngineTools.deltaTime = (float) (time - lastTime);
			EngineTools.deltaTime = .02f;
			lastTime = time;
			
			//System.out.println(1/EngineTools.deltaTime);


			glfwGetCursorPos(window, posX, posY);
			Stats.updateEveryFrame();
			GameClass.onDraw(vPMatrix, (float)posX.get(0) * virutalWidth/windowWidth, virutalHeight - (float)posY.get(0) * virutalHeight/windowHeight);
			


			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new MainClass().run();
	}

}