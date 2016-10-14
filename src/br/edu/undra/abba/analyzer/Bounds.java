/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.analyzer;

/**
 *
 * @author alexandre
 */
public enum Bounds {
    
    UPPER, BOTTOM;
    
    public static void main(String[] args){
        for(Bounds t : Bounds.values()) System.out.println(t);
    }
    
}

