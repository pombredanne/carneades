/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fokus.carneades.simulation;

import java.util.List;

/**
 * 
 * Reference to another question
 *
 * @author stb
 */
public class QuestionRef {

    private String pred; // predicate of the referenced question
    private Integer arg; // which argument of the statement will be asked?
    private List<Integer> args; // order of other arguments

    public QuestionRef(String pred, Integer arg, List<Integer> args) {
        this.pred = pred;
        this.arg = arg;
        this.args = args;
    }

    public Integer getArg() {
        return arg;
    }

    public void setArg(Integer arg) {
        this.arg = arg;
    }
    
    public List<Integer> getArgs() {
        return args;
    }

    public void setArgs(List<Integer> args) {
        this.args = args;
    }

    public String getPred() {
        return pred;
    }

    public void setPred(String pred) {
        this.pred = pred;
    }
}