/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController.examples;

public interface UserService {
    
    public interface AuthenticationObserver {
        public void authenticiationWasSuccessful(UserService source, User user);
        public void authenticiationDidFail(UserService source);
    }
    
    public interface RegistrationObserver {
        public void didRegisterUser(UserService source, User user);
        public void reegistrationDidFail(UserService source);
    }
    
    public void authenticateUser(String name, char[] password, AuthenticationObserver observer);
    public void registerUser(String name, char[] password, RegistrationObserver observer);
}
