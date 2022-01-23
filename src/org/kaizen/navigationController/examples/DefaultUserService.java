/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController.examples;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shane.whitehead
 */
public class DefaultUserService implements UserService {

    private List<User> users;

    public DefaultUserService() {
        users = new ArrayList<>();
        users.add(new DefaultUser("fred"));
    }

    @Override
    public void authenticateUser(String name, char[] password, AuthenticationObserver observer) {
        for (User user : users) {
            if (name.equals(user.getName())) {
                observer.authenticiationWasSuccessful(this, user);
                return;
            }
        }

        observer.authenticiationDidFail(this);
    }

    @Override
    public void registerUser(String name, char[] password, RegistrationObserver observer) {
        User user = new DefaultUser(name);
        users.add(user);
        observer.didRegisterUser(this, user);
    }

}
