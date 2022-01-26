/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

import javax.swing.JComponent;

/**
 *
 * @author shane.whitehead
 */
public abstract class AbstractViewNavigationController<View> implements ViewNavigationController<View> {

    private ViewNavigationModel<View> model;
    private ViewNavigationPane navigationView;

    private ViewNavigationModel.Listener<View> modelListener = new ViewNavigationModel.Listener<View>() {
        @Override
        public void didPush(ViewNavigationModel<View> source, View view) {
            push(view);
        }

        @Override
        public void didPop(ViewNavigationModel<View> source, View view) {
            pop(view);
        }

        @Override
        public void didReplaceView(ViewNavigationModel<View> source, View view) {
            replaceView(view);
        }

        @Override
        public void didPopToRoot(ViewNavigationModel<View> source, View oldView, View newView) {
            popToRoot(oldView, newView);
        }
    };

    public AbstractViewNavigationController(ViewNavigationModel<View> model, ViewNavigationPane navigationView) {
        this.model = model;
        this.navigationView = navigationView;
        
        this.model.addListener(getModelListener());
    }

    @Override
    public ViewNavigationModel<View> getModel() {
        return model;
    }

    @Override
    public ViewNavigationPane getNavigationView() {
        return navigationView;
    }
    
    protected ViewNavigationModel.Listener<View> getModelListener() {
        return modelListener;
    }

    protected void popToRoot(View oldView, View newView) {
        getNavigationView().popToRoot();
    }

    protected void push(View view) {
        JComponent component = componentForView(view);
        getNavigationView().push(component);
    }

    public void pop(View view) {
        getNavigationView().pop();
    }

    public void replaceView(View view) {
        JComponent component = componentForView(view);
        getNavigationView().replaceWith(component);
    }
    
    protected abstract JComponent componentForView(View view);

}
