package library.addons.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

public class AudioMaster {

    static ArrayList<Integer> buffers = new ArrayList<>();
    static ArrayList<Integer> sourceIds = new ArrayList<>();
    public static Source defaultSource;
    public static int soundBuffer;

    
    static long context;
    static long device;

    public static void initialize(){

        device = ALC10.alcOpenDevice((ByteBuffer) null);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        setListenerData();

        defaultSource = new Source();
    }

    public static int loadSound(String file){
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);
        WaveData wf = WaveData.create(file);
        AL10.alBufferData(buffer, AL10.AL_FORMAT_STEREO16, wf.data, wf.samplerate);
        wf.dispose();
        return buffer;
    }

    public static Source getNewSource(){
        Source source = new Source();
        sourceIds.add(source.sourceId);
        return source;
    }

    public static void setListenerData(){
        AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public static void cleanUp(){
        for(int buffer : buffers){
            AL10.alDeleteBuffers(buffer);
        }
        for(int sources : sourceIds){
            AL10.alDeleteSources(sources);
        }
        ALC10.alcDestroyContext(context);
        ALC.destroy();
    }
}