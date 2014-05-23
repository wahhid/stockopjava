/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class UploadFileBean {
           
    private static final int BUFFER_SIZE = 6124;
    /** Creates a new instance of UploadFileBean */
    public UploadFileBean() {
    }
    
    public void processFileUpload(FileUploadEvent event){
       ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();         
        String fullDir = extContext.getRealPath("//upload");
        File createDir = new File(fullDir);
        File result;
        if(createDir.exists() == false){
            boolean success = new File(fullDir).mkdir();
            if(success){
                System.out.println("New file created! 1");
                result = new File(extContext.getRealPath("//upload") + "//" + event.getFile().getFileName());
            }else{
                System.out.println("New file created! 2");
                result = new File(extContext.getRealPath("//upload") + "//"  + event.getFile().getFileName());
            }            
        }else{
            System.out.println("New file created! 3");
            result = new File(extContext.getRealPath("//upload") + "//" + event.getFile().getFileName());
        }
        
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(result);
            byte[] buffer = new byte[this.BUFFER_SIZE];
            int bulk;
            
            InputStream inputStream = event.getFile().getInputstream();
            while(true){
                bulk = inputStream.read(buffer);
                if(bulk<0){
                    break;
                }
                fileOutputStream.write(buffer,0,bulk);
                fileOutputStream.flush();                
            }
            
            fileOutputStream.close();
            inputStream.close();
            
            FacesMessage msg = new FacesMessage("Successfull", event.getFile().getFileName() + " is uploaded");
            FacesContext.getCurrentInstance().addMessage(null, msg);           
            
        }catch(IOException ex){
            System.out.println("Created File Error!");
            FacesMessage error = new FacesMessage("The files were not uploaded");
            FacesContext.getCurrentInstance().addMessage(null, error);                                
        }        
    }
}