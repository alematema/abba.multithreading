/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.bruteforce;

import br.edu.undra.abba.analyzer.Analyzer;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexandre
 */
public class MultiThreadedBruteForce extends BruteForce {

    /**
     *
     * @param initial the initial string
     * @param target the target string
     */
    public MultiThreadedBruteForce(String initial, String target) {
        super (initial,target);
    }

    @Override
    public String tryObtainingTargetFromInitial() {
       
        Forker.setPacketFullSize((long)Math.pow(2, target.length() - initial.length()));
        Forker.setPacketSize(1000000l);
        Forker.verboseOn();
        Forker.runThreadsAfterCreation();
        
        Analyzer.verboseOn();
        
        try {//forks
            
            Forker.fork(Analyzer.class, initial, target);
            
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(MultiThreadedBruteForce.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //joins
        while (Analyzer.secretIsClosed()) {
            if (Analyzer.hasFinishedAnalysing()) {
                break;
            }
        }
        
        return canObtain();
    }
    
}
