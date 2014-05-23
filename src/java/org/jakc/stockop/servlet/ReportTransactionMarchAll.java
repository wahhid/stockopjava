/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.jakc.stockop.entity.Employee;
import org.jakc.stockop.entity.Periode;
import org.jakc.stockop.entity.Product;
import org.jakc.stockop.entity.Site;
import org.jakc.stockop.entity.Stock;
import org.jakc.stockop.entity.Transstock;
import org.jakc.stockop.entity.Transtockdetail;
import org.jakc.stockop.entity.report.RptStockTakeList;

/**
 *
 * @author root
 */
@WebServlet(name = "ReportTransactionMarchAll", urlPatterns = {"/ReportTransactionMarchAll"})
public class ReportTransactionMarchAll extends HttpServlet {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    List<RptStockTakeList> os = new ArrayList();
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String siteid;
        String sitename = null;
        String periodeid;
        String periodename = null;
        String periodedesc = null;
        String gondolaid;
        String gondolaname = null;
        String nik;
        String fullname = null;
        String userid;
        String userfullname = null;
        String variant = null;
        String marchcode = null;
        String productinfo;
                
        siteid = request.getParameter("siteid");
        Site site = this.findSite(siteid);
        if(site != null){
            sitename = site.getBranchname();
        }
        
        periodeid = request.getParameter("periodeid");
        Periode periode = this.findPeriode(periodeid);
        if(periode != null){
            periodename = periode.getPeriodename();
            periodedesc = periode.getDescription();
        }
        
        productinfo = request.getParameter("productinfo");
        variant = request.getParameter("variant");
        marchcode = request.getParameter("marchcode");
        userid = request.getParameter("printedbynik");
        userfullname = request.getParameter("printedbyname");                        
        
        HashMap parameters = new HashMap();
        parameters.put("SITEID", siteid);
        parameters.put("SITENAME", sitename);
        parameters.put("PERIODEID", periodename);
        parameters.put("PERIODENAME", periodedesc);        
        parameters.put("MARCHCODE",marchcode);
        parameters.put("FULLNAME",fullname);
        parameters.put("PRINTED_BY",userfullname);
        
        
        
        // set input and output stream
        ServletOutputStream servletOutputStream = response.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        BufferedInputStream bufferedInputStream;
        try {
            // get report location
            ServletContext context = getServletContext();
            String reportLocation = context.getRealPath("WEB-INF");

            // get report
            fis = new FileInputStream(reportLocation + "/stockopmarchv1.jasper");
            bufferedInputStream = new BufferedInputStream(fis);
            response.setContentType("application/pdf");
            // fetch data from database            
            os = this.findTransstockdetails(siteid,periodeid,variant,productinfo,marchcode);
     
                                                                            
            // fill it
            JRDataSource jrbcds = new JRBeanCollectionDataSource(os);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);                                
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrbcds);
            // export to pdf                
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            response.setContentLength(baos.size());
            baos.writeTo(servletOutputStream);                        

            // close it
            fis.close();
            bufferedInputStream.close();                    

        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            servletOutputStream.flush();
            servletOutputStream.close();
            baos.close();
        }

    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }

    
    private double findTotalStockDetails(String siteid,String periodeid,String ean){
        EntityManager em = this.getEntityManager();        
        double totalstock = 0;
        List<Transtockdetail> transtockdetails = this.findDetailByEan(siteid, periodeid,ean);        
        for(Transtockdetail o : transtockdetails){
            totalstock = totalstock + o.getQty2();
        }
        return totalstock;
    }
              
    private List<RptStockTakeList> findTransstockdetails(String siteid,String periodeid,String variant,String productinfo ,String marchcode){
        EntityManager em = this.getEntityManager();
        List<RptStockTakeList> os = new ArrayList();                        
        try{
            List<Transtockdetail> transtockdetails = this.findDetail(siteid, periodeid);
            for(Transtockdetail o : transtockdetails){    
                RptStockTakeList stl = new RptStockTakeList();
                if(o.getProduct() != null){
                    stl.setFlagexist(" ");
                    double totaldetail = 0;                
                    totaldetail = this.findTotalStockDetails(siteid, periodeid,o.getProduct().getEan());                                      
                    stl.setDetailid(o.getDetailid());
                    stl.setSequence(o.getSequence());
                    stl.setProductid(o.getProduct().getEan());           
                    stl.setArticleid(o.getProduct().getArticleid());
                    stl.setProductname(o.getProduct().getProductname());           
                    stl.setGondolaid(o.getTransstock().getGondola().getGondolaname());
                    stl.setGondolaname(o.getTransstock().getGondola().getDescription());                
                    stl.setQty2(o.getQty2());   
                    
                    Stock stock = this.findStock(siteid, periodeid, o.getProduct().getArticleid());
                    if(stock != null){
                        System.out.println("Stock Not Null");
                        double lastQty = stock.getQty() - stock.getPendingqty();
                        System.out.println("QTY : " + o.getQty2());
                        System.out.println("Last QTY : " + lastQty);                        
                        if(lastQty != totaldetail){                                       
                            stl.setFlagdifference("*");
                        }else{
                            stl.setFlagdifference(" ");
                        }                      
                        stl.setFlagstock(" ");
                    }else{
                        System.out.println("Stock Null");
                        stl.setFlagstock("*");
                        stl.setFlagdifference("*");
                    }                          
                }else{
                    stl.setFlagexist("*");
                    stl.setFlagstock("*");  
                    stl.setFlagdifference("*");
                    stl.setDetailid(o.getDetailid());
                    stl.setSequence(o.getSequence());                   
                    stl.setGondolaid(o.getTransstock().getGondola().getGondolaname());
                    stl.setGondolaname(o.getTransstock().getGondola().getDescription());          
                    stl.setProductid(this.findTranstokdetailtemp(o.getDetailid()));
                    stl.setArticleid("");
                    stl.setProductname("");
                    stl.setQty2(o.getQty2());   
                    
                }
                
                if(variant.equals("00")){
                    if(marchcode.equals("*")){
                        if(productinfo.equals("00")){
                            os.add(stl);
                        }else{
                            if(stl.getFlagexist().equals("*")){
                                os.add(stl);
                            }
                        }                        
                    }else{
                        if(o.getProduct() != null && o.getProduct().getCategoryid().startsWith(marchcode)){
                            if(productinfo.equals("00")){
                                os.add(stl);    
                            }else{
                                if(stl.getFlagexist().equals("*")){
                                    os.add(stl);    
                                }
                            }
                            
                        }
                    }
                    
                }else{
                    if(stl.getFlagdifference().equals("*")){
                        if(marchcode.equals("*")){
                            if(productinfo.equals("00")){
                                os.add(stl);
                            }else{
                                if(stl.getFlagexist().equals("*")){
                                    os.add(stl);
                                }
                            }
                            
                        }else{
                            if(o.getProduct() != null && o.getProduct().getCategoryid().startsWith(marchcode)){
                                if(productinfo.equals("00")){
                                    os.add(stl);    
                                }else{
                                    if(stl.getFlagexist().equals("*")){
                                        os.add(stl);    
                                    }
                                }
                                
                            }
                        }                        
                    }
                }
                
            }
        }catch(NoResultException ex){
            ex.printStackTrace();
        }finally{
            em.close();
        }
        return os;
    }    
    
    private List<Transtockdetail> findDetail(String siteid,String periodeid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT t FROM Transtockdetail t WHERE t.transstock.site.siteid=:siteid AND t.transstock.periode.periodeid=:periodeid ORDER BY t.transstock.gondola.gondolaid,t.sequence";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", siteid)
                    .setParameter("periodeid", periodeid);
            query.setHint("eclipselink.refresh", true);
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private List<Transtockdetail> findDetailByEan(String siteid,String periodeid,String ean){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT t FROM Transtockdetail t "
                + "WHERE "
                + "t.transstock.site.siteid=:siteid AND "
                + "t.transstock.periode.periodeid=:periodeid AND "                
                + "t.product.ean=:ean "
                + "ORDER BY t.sequence";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", siteid)
                    .setParameter("periodeid", periodeid)                    
                    .setParameter("ean",ean);
            query.setHint("eclipselink.refresh", true);
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
        

    
     private Site findSite(String siteid){
        EntityManager em = this.getEntityManager();        
        try{        
            Query query = em.createNamedQuery("Site.findBySiteid").setParameter("siteid", siteid);
            query.setHint("eclipselink.refresh", true);
            return  (Site) query.getSingleResult();
        }catch(NoResultException ex){
            //ex.printStackTrace();
            return null;
        }finally{
            em.close();
        }        
    }
    
    
    private Periode findPeriode(String periodeid){
        EntityManager em = this.getEntityManager();        
        try{        
            Query query = em.createNamedQuery("Periode.findByPeriodeid").setParameter("periodeid", periodeid);
            query.setHint("eclipselink.refresh", true);
            return  (Periode) query.getSingleResult();
        }catch(NoResultException ex){
            //ex.printStackTrace();
            return null;
        }finally{
            em.close();
        }                
    }
                      
    private Stock findStock(String siteid, String periodeid , String articleid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT s FROM Stock s WHERE s.site.siteid=:siteid AND s.periode.periodeid=:periodeid AND s.articleid=:articleid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", siteid)
                    .setParameter("periodeid", periodeid)
                    .setParameter("articleid", articleid);
            query.setHint("eclipselink.refresh", true);
            return (Stock) query.getSingleResult();
        }catch(NoResultException ex){
            ex.printStackTrace();
            return null;
        }finally{
            em.close();
        }        
    }
    
    private String findTranstokdetailtemp(int detailid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT t.detailid, t.productid FROM Transtockdetailtemp t WHERE t.detailid=:detailid";
        try{
            Query query = em.createQuery(strSQL).setParameter("detailid", detailid);
            query.setHint("eclipselink.refresh", true);
            Object[] o =  (Object[]) query.getSingleResult();
            return (String) o[1];
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
            
    }    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
