/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.runner;

import br.edu.undra.abba.analyzer.Analyzer;
import br.edu.undra.abba.analyzer.Bounds;

/**
 *
 * @author alexandre
 */
public class AnalyzerRunnable implements Runnable {

    private final Analyzer analyzer;
    private final long from;
    private final long to;
    private final Bounds bound;

    public AnalyzerRunnable(Analyzer analyzer, long from, long to, Bounds bound) {
        this.analyzer = analyzer;
        this.from = from;
        this.to = to;
        this.bound = bound;
    }

    @Override
    public void run() {
       analyzer.analyse(from, to, bound);
    }
    
}
