/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.util;

import java.util.List;

/**
 *
 * @author wahhid
 */
public class ProcessCallBack {
    private boolean error;
    private String errmsg;
    private Object object;    

    public ProcessCallBack(boolean error, String errmsg, Object object){
        this.error = error;
        this.errmsg = errmsg;
        this.object = object;
    }
    
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }        
    
}
