/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kaizen.navigationController;

/**
 * Some optional life cycle management call backs
 */
public interface NavigatableView {
    public void willPresent();
    public void didPresent();
    public void willDismiss();
    public void didDismiss();
}
