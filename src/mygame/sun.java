package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author Lorenzo
 */
public class sun {
    private Vector3f pos;
    private Sphere sphere;
    private Geometry sphereGeo;
    private Material mat;
    
    /*Sphere sphereMesh = new Sphere(64,64, 100f);
    Geometry sphereGeo = new Geometry("Sun", sphereMesh);
    sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Orange);
    sphereGeo.setMaterial(mat);
    sphereGeo.setLocalTranslation(new Vector3f(0,0,1000));
    rootNode.attachChild(sphereGeo);*/
    sun(AssetManager assetmanager,Vector3f pos, float size){
        sphere = new Sphere(32, 32, size);
        sphereGeo = new Geometry("Sun", sphere);
        mat = new Material(assetmanager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        mat.setColor("Color", ColorRGBA.Orange);
        mat.setReceivesShadows(false);
        sphereGeo.setMaterial(mat);
        
        sphereGeo.setLocalTranslation(pos);

    }
    public Geometry getSun(){
        return sphereGeo;
    } 
    public void updateSunPosition(Vector3f pos){
        sphereGeo.setLocalTranslation(pos);
    }
    public Vector3f getSunPosition(){
        return sphereGeo.getLocalTranslation();
    }
    
}
