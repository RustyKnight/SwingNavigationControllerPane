/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

import java.util.EventListener;

/**
 *
 * @author shane.whitehead
 */
public interface NavigationControllerModel<View> {

    public interface Listener<View> extends EventListener {
        public void didPush(NavigationControllerModel<View> source, View view);
        public void didPop(NavigationControllerModel<View> source, View view);
        public void didReplaceView(NavigationControllerModel<View> source, View view);
    }
    
    public void push(View view);
    public void pop();
    public void replaceWith(View view);
    public void popToRoot();

}
