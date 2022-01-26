/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

/**
 * This acts as a controller/coordinator between the model and the navigation
 * view itself.  
 * 
 * The primary issue that this interface tries to solve is, how do you decouple
 * the model, from the basic logic and the actual generation of the view
 * components themselves?
 * 
 * One could argue that the model should be controlling the business logic, but
 * the problem I have is, in order to do so, we need to expose the component views
 * to the model, but the model shouldn't be dealing with the component views.
 * 
 * There's also the issue of context.  How do you manage context between the
 * individual views?  One view might be generating data which is needed
 * further down the track?  Who's responsible for that!?
 * 
 * So instead, the controller steps to fill the gap.  I can generate the view
 * components and act as their observer, managing the context and direct the 
 * flow as needed
 */
public interface ViewNavigationController<View> {
    public ViewNavigationModel<View> getModel();
    public ViewNavigationPane getNavigationView();
}
