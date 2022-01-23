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
public class DefaultUser implements User {
    
    String name;

    public DefaultUser(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
