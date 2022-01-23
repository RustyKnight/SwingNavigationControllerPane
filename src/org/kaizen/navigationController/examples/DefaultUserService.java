/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController.examples;

/**
 *
 * @author shane.whitehead
 */
public class DefaultUserService implements UserService {

    @Override
    public void authenticateUser(String name, char[] password, Observer observer) {
        if ("fred".equals(name)) {
            observer.authenticiationWasSuccessful(this, new DefaultUser("fred"));
        } else {
            observer.authenticiationDidFail(this);
        }
    }
    
}
