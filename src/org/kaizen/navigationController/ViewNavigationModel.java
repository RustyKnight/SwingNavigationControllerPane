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
public interface ViewNavigationModel<View> {

    public interface Listener<View> extends EventListener {
        public void didPush(ViewNavigationModel<View> source, View view);
        public void didPop(ViewNavigationModel<View> source, View view);
        public void didReplaceView(ViewNavigationModel<View> source, View view);
        public void didPopToRoot(ViewNavigationModel<View> source, View oldView, View newView);
    }
    
//    public NavigationControllerContext getContext();
    
    public void push(View view);
    public void pop();
    public void replaceWith(View view);
    public void popToRoot();
    
    // Get the first/last view in the stack without modifying
    // the stack
    public View first();
    public View last();
    
    public void addListener(Listener<View> listener);
    public void removeListener(Listener listener);

}
