package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class Scene
{
   private Spatial sceneModel;
   private DirectionalLight sun;  
   private Vector3f sunpos;
   private AssetManager asset;
   private Node rNode;
   private ViewPort port;
 //--------------------variabili multithreading
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
    private Future f[]=new Future[3];
    
   Scene(AssetManager a,Node n,ViewPort p)
   {
      asset=a;
      rNode=n;
      port=p;
      sunpos = new Vector3f(0,0,1000);
      f[0]=null; f[1]=null; f[2]=null;
      f[0]=executor.submit(initFog); //thread nebbia
      f[1]=executor.submit(initLight); //thread luce
      f[2]=executor.submit(initScene); //thread terreno
      while(!f[0].isDone() || !f[1].isDone() || !f[2].isDone()) { } //attende la fine di tutti i thread
      f[0]=null; f[1]=null; f[2]=null;
   }
    
   private Callable initFog=new Callable()
   {
      public Object call()
      {
          FilterPostProcessor fpp=new FilterPostProcessor(asset);
          FogFilter fog=new FogFilter();
          fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
          fog.setFogDistance(1000);
          fog.setFogDensity(2.0f);
          fpp.addFilter(fog);
          port.addProcessor(fpp);
          return true;   
      }
   };
   
   private Callable initLight=new Callable()
   {
      public Object call()
      {
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.Orange);
        rNode.addLight(sun);  
        return true;   
      }
   };
   
   private Callable initScene=new Callable()
   {
      public Object call()
      {
        sceneModel = asset.loadModel("Scenes/Island.j3o");
        rNode.attachChild(sceneModel);
        return true;   
      }
   };
   
   public void LightMovement()
   {
      try
      {
        if(f[0]==null) 
        {
          f[0]=executor.submit(LightMovement_thread); //fa partire il thread
        }
        else {
                if(f[0].isDone() || f[0].isCancelled()) //se il thread è finito o è stato cancellato
                  f[0]=null;   //il future viene rimesso a null per far ripartire il thread
             }
      } catch(Exception e) {}
   }
   
   private Callable LightMovement_thread=new Callable()
   {
      public Object call()
      {
        if(sunpos.z == -1000)sunpos.z=1000;
        sunpos.z -= 0.5f;
        sunpos.y = -FastMath.sqrt((FastMath.pow(sunpos.z,2))/2)+1000;
        System.out.println(sunpos);
        sun.setDirection(new Vector3f(0,sunpos.y,sunpos.z).negate());
        return true;   
      }
   };
   
};
