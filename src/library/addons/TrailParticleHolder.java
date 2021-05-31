package library.addons;

import library.EngineTools;
import library.drawable.ParticleMaster;
import library.drawable.Trail;

public class TrailParticleHolder {
    public ParticleMaster particleMaster;
    public Trail trail;
    private float timeRemaining;
    public boolean isAlive = true;

    public TrailParticleHolder(ParticleMaster particleMaster, Trail trail, float time){
        this.trail = trail;
        this.particleMaster = particleMaster;
        timeRemaining = time;
    }

    public void draw(float[] mvpMatrix){
        timeRemaining -= EngineTools.deltaTime;

        if(timeRemaining <= 0){
            isAlive = false;
            return;
        }

        if(particleMaster != null){
            particleMaster.draw(mvpMatrix);
        }
        if(trail != null){
            trail.draw(mvpMatrix);
        }
    }
}
