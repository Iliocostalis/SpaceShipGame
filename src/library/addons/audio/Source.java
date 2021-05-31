package library.addons.audio;

import org.lwjgl.openal.AL10;

public class Source {
    protected int sourceId;

    public Source(){
        sourceId = AL10.alGenSources();
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 1f);
        AL10.alSourcef(sourceId, AL10.AL_PITCH, 1f);
        AL10.alSource3f(sourceId, AL10.AL_POSITION, 0,0,0);
    }

    public void play(int buffer){
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
        AL10.alSourcePlay(sourceId);
    }
}