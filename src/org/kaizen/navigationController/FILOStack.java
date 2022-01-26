/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

import java.util.ArrayList;
import java.util.List;

/**
 * I just got feed up with Stack - lets be honest, the concept of a LIFO is
 * not hard - if I pop and element and non exists, just return null for
 * pitty sake, I don't need an exception thrown 
 */
public class FILOStack<V> {
    
    private List<V> contents;

    public FILOStack() {
        contents = new ArrayList<>(128);
    }
    
    public void push(V value) {
        contents.add(value);
    }
    
    public V pop() {
        if (contents.isEmpty()) {
            return null;
        }
        return contents.remove(contents.size() -1);
    }
    
    public V peekLast() {
        if (contents.isEmpty()) {
            return null;
        }
        return contents.get(contents.size() -1);
    }
    
    public V peekFirst() {
        if (contents.isEmpty()) {
            return null;
        }
        return contents.get(0);
    }
    
    public int size() {
        return contents.size();
    }
    
    public void remove(V value) {
        contents.remove(value);
    }
    
    public void clear() {
        contents.clear();
    }
}
