package com.team6.arcadesim.scenes;

public abstract class Scene {
    // This is an abstract class for different scenes in the arcade simulator
    
    /** Called when the scene is created. Initialize your scene here. */
    public abstract void create();
    
    /** Called every frame. Update your scene logic here. 
     * @param delta Time since last frame in seconds */
    public abstract void update(float delta);
    
    /** Called when the scene is disposed. Clean up resources here. */
    public abstract void dispose();
}
