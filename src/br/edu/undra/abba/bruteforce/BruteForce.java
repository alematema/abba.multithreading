/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.bruteforce;

import br.edu.undra.abba.analyzer.Analyzer;

/**
 *
 * @author alexandre
 */
public abstract class BruteForce {
    
    protected final String initial;
    protected final String target;

    public BruteForce(String initial, String target) {
        this.initial = initial;
        this.target = target;
    }
    
    public void describeKeyThatOpensSecret(char[] key, String initial, String target) {
        Analyzer.describeHowKeyOpensSecret(key, initial, target);
    }
    
    abstract public String tryObtainingTargetFromInitial() ;
    
    protected String canObtain(){
        
        String canObtain;
       
        if(!Analyzer.canObtainTargetFromInitialString()){
            canObtain = "Impossible";
        }else{
            canObtain = "Possible";
            describeKeyThatOpensSecret(Analyzer.binaryKey, initial, target);
        }
        
        return canObtain;
    }
    
    
}
