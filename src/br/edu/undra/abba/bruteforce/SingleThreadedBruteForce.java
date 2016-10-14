/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.bruteforce;

import br.edu.undra.abba.analyzer.Analyzer;
import br.edu.undra.abba.analyzer.Bounds;

/**
 *
 * @author alexandre
 */
public class SingleThreadedBruteForce extends BruteForce {

    /**
     *
     * @param initial the initial string
     * @param target the target string
     */
    public SingleThreadedBruteForce(String initial, String target) {
        super(initial, target);
    }

    @Override
    public String tryObtainingTargetFromInitial() {

        Analyzer.analyseInSingleThread(initial, target, Bounds.UPPER);

        if (!Analyzer.canObtainTargetFromInitialString()) {

            Analyzer.analyseInSingleThread(initial, target, Bounds.BOTTOM);

        }

        return canObtain();
    }
}
