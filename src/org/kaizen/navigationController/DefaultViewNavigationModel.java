/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 *
 * @author shane.whitehead
 */
public class DefaultViewNavigationModel<View> implements ViewNavigationModel<View> {

//    private NavigationControllerContext context = new DefaultNavigationControllerContext();
    private List<Listener<View>> listeners;
    
    private FILOStack<View> viewStack = new FILOStack<>();

    protected FILOStack<View> getViewStack() {
        return viewStack;
    }
    
//    @Override
//    public NavigationControllerContext getContext() {
//        return context;
//    }

    protected List<Listener<View>> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        return listeners;
    }

    @Override
    public void addListener(Listener<View> listener) {
        getListeners().add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        getListeners().remove(listener);
    }
    
    protected void fireDidPush(View view) {
        for (Listener listener : getListeners()) {
            listener.didPush(this, view);
        }
    }
    
    protected void fireDidPop(View view) {
        for (Listener listener : getListeners()) {
            listener.didPop(this, view);
        }
    }
    
    protected void fireDidReplaceView(View view) {
        for (Listener listener : getListeners()) {
            listener.didReplaceView(this, view);
        }
    }
    
    protected void fireDidPopToRoot(View oldView, View newView) {
        for (Listener listener : getListeners()) {
            listener.didPopToRoot(this, oldView, newView);
        }
    }

    @Override
    public void push(View view) {
        getViewStack().push(view);
        fireDidPush(view);
    }

    @Override
    public void pop() {
        fireDidPop(getViewStack().pop());
    }

    @Override
    public void replaceWith(View view) {
        FILOStack<View> viewStack = getViewStack();
        viewStack.pop();
        viewStack.push(view);
        fireDidReplaceView(view);
    }

    @Override
    public void popToRoot() {
        FILOStack<View> viewStack = getViewStack();
        View first = viewStack.peekFirst();
        View last = viewStack.peekLast();
        viewStack.clear();
        viewStack.push(first);
        
        fireDidPopToRoot(last, first);
    }

    @Override
    public View first() {
        return getViewStack().peekFirst();
    }

    @Override
    public View last() {
        return getViewStack().peekLast();
    }
    
}
