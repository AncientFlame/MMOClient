/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Lorenzo
 */
public class StartGUIController extends AbstractAppState implements ScreenController{
    private NiftyJmeDisplay nifty;
    private ViewPort viewPort;
    //private Screen screen;
    private SimpleApplication app;
    boolean menu=true;
    

    StartGUIController(AppStateManager stateManager, SimpleApplication app, ViewPort port) {
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
        viewPort = port;
    }    
 
    
    public void onStartScreen() {
        
    }

    public void onEndScreen() {
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
    }
 
    @Override
    public void update(float tpf) { 
      /** jME update loop! */ 
    }
    public void quitGame(int x, int y){
        app.stop();
    }
    public void startGame(int x, int y){
        this.menu=false;
        viewPort.removeProcessor(nifty);      
    }

    public void bind(Nifty nifty, Screen screen) {
        
    }

    public void setNifty(NiftyJmeDisplay niftyDisplay) {
         nifty = niftyDisplay;
        
    }
}
