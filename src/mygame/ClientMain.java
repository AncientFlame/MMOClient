package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientMain extends SimpleApplication {
    NiftyJmeDisplay niftyDisplay;
    private Client myClient;
    private static ClientMain app;
    StartGUIController startController;
    private mygame.Scene scena;
    
    public static void main(String[] args) 
    {    
        app = new ClientMain();
        Settings sys = new Settings();
        app.setSettings(sys.get_settings());
        app.setPauseOnLostFocus(true);
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        app.start(JmeContext.Type.Display); // standard display type
    }
    
    @Override
    public void simpleInitApp() 
    {
        startController = new StartGUIController(stateManager, app, guiViewPort);
       // sunpos = new Vector3f(0,0,1000);
        initStartGUI();
        startController.setNifty(niftyDisplay);
        
       // sole = new sun(assetManager, new Vector3f(0,0,1000), 70);
       // rootNode.attachChild(sole.getSun());
        
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(500.0f);
        
        scena=new Scene(assetManager,rootNode,viewPort);
               
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
               
    }
    @Override
    public void simpleUpdate(float tpf) 
    {
       scena.LightMovement();
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
     
    /*private void initShadow(){
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.93f, 0.33f, 0.60f);
        fpp.addFilter(ssaoFilter); 
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
    }*/    
    
   /* private void initWater(){
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
    }*/
    
      
    private void initStartGUI(){
        niftyDisplay = new NiftyJmeDisplay(
        assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GameStart.xml", "start", startController);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    @Override
    public void destroy(){
      myClient.close();
      super.destroy();
      scena.executor.shutdown();
    }
    
}
