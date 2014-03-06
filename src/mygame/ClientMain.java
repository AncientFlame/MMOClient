package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.JmeContext;
import com.jme3.water.SimpleWaterProcessor;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class ClientMain extends SimpleApplication {
    NiftyJmeDisplay niftyDisplay;
    private Client myClient;
    private static ClientMain app;
    private Spatial sceneModel;
    DirectionalLight sun;
    StartGUIController startController;
    mygame.sun sole;
    private float time;
    private Vector3f sunpos;
    
    
    public static void main(String[] args) {
        
        app = new ClientMain();
        Settings sys = new Settings();
        app.setSettings(sys.get_settings());
        app.setPauseOnLostFocus(true);
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        app.start(JmeContext.Type.Display); // standard display type
    }
    


    @Override
    public void simpleInitApp() {
        startController = new StartGUIController(stateManager, app, guiViewPort);
        sunpos = new Vector3f(0,0,1000);
        initStartGUI();
        startController.setNifty(niftyDisplay);
        
        sole = new sun(assetManager, new Vector3f(0,0,1000), 70);
        rootNode.attachChild(sole.getSun());
        
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(500.0f);
        
         /** Add fog to a scene */
            FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
            FogFilter fog=new FogFilter();
            fog.setFogColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
            fog.setFogDistance(1000);
            fog.setFogDensity(2.0f);
            fpp.addFilter(fog);
            viewPort.addProcessor(fpp);

        
        
        
        Serializer.registerClass(message.class);
        try {
            myClient = Network.connectToServer("localhost", 56122);
            myClient.start();
            myClient.addMessageListener(new ClientListener(), message.class);
        } catch (IOException ex) {
            
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        Message message = new message("Lorenzo");
        myClient.send(message);
        
        
        initScene();
        
    }
    @Override
    public void simpleUpdate(float tpf) {
            time+=tpf;
            if(sunpos.z == -1000)sunpos.z=1000;
            sunpos.z -= 0.5f;
            sunpos.y = -FastMath.sqrt((FastMath.pow(sunpos.z,2))/2)+1000;
            /*sunpos.y = FastMath.sqrt(-((FastMath.pow(sunpos.z,2))/7)+20);*/
            sole.updateSunPosition(sunpos);
            System.out.println(sunpos);
            sun.setDirection(new Vector3f(0,sunpos.y,sunpos.z).negate());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }
    public class ClientListener implements MessageListener<Client> {
        public void messageReceived(Client source, Message m) {
            message helloMessage = (message) m;
            if (m instanceof message) {
            // do something with the message
            
            System.out.println("Client #"+ source.getId() + " received: '"+helloMessage.getMessage()+"'");
           
          } 
        }
    }
    
    
    @Override
    public void destroy() {
      myClient.close();
      super.destroy();
    }
    
    public void initScene(){
         sceneModel = assetManager.loadModel("Scenes/Island.j3o");
         cam.setLocation(new Vector3f(0.0f,120.0f, 10.0f));
         rootNode.attachChild(sceneModel);
         initWater();
         //initShadow();
         initLight();
    }
    
    private void initShadow(){
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.93f, 0.33f, 0.60f);
        fpp.addFilter(ssaoFilter); 
        /* this shadow needs a directional light */
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
    }
    private void initLight(){
        /** A white, directional light source */ 
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.Orange);
        rootNode.addLight(sun);
    }
    
    private void initWater(){
    // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneModel);

        // we set the water plane
        Vector3f waterLocation=new Vector3f(0,0,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        viewPort.addProcessor(waterProcessor);

        // we set wave properties
        waterProcessor.setWaterDepth(0);         // transparency of water
        waterProcessor.setDistortionScale(0.05f); // strength of waves
        waterProcessor.setWaveSpeed(0.05f);       // speed of waves

        // we define the wave size by setting the size of the texture coordinates
        Quad quad = new Quad(512,512);
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));

        // we create the water geometry from the quad
        Geometry water=new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-250, 20, 250);
        water.setShadowMode(ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
        rootNode.attachChild(water);
    }
    
    
    
    private void initStartGUI(){
        niftyDisplay = new NiftyJmeDisplay(
        assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GameStart.xml", "start", startController);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    
}
