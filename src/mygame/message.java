/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Lorenzo
 */
@Serializable
public class message extends AbstractMessage {
    private String hello;       // custom message data
    public message() {}    // empty constructor
    public message(String s) { hello = s; } // custom constructor
    public String getMessage(){
        return hello;
    } 
};