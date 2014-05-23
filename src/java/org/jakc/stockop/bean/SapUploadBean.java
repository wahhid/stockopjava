/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import com.csvreader.CsvWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import org.jakc.stockop.backing.BackingModel;
import org.jakc.stockop.datamodel.LazySapUploadDataModel;
import org.jakc.stockop.entity.Product;
import org.jakc.stockop.entity.Stock;
import org.jakc.stockop.entity.Transstock;
import org.jakc.stockop.entity.Transtockdetail;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class SapUploadBean {
    
    
    private BackingModel bm;
    private LazyDataModel<Stock> lazyModel;       
 
    private Stock object;
    private Stock selectedObject;
    private Stock[] selectedObjects;

    private StreamedContent file;
     
    private String periodeid;
    private String refreshperiodeid;
    
    //filter
    private HashMap filterList;
    private boolean filterArticleid;
    private String operatorArticleid;
    private String articleid;
    private boolean filterDifqty;
    private String operatorDifqty;
    private double difqty;
    private boolean filterDifunitcost;
    private String operatorDifunitcost;
    private double difunitcost;    
    private boolean filterPrinted;    
    
    
    //Sort
    
    private String sortField="articleid";
    private String sortOrder="asc";
    
            
    /** Creates a new instance of SapUploadBean */
    public SapUploadBean() {
        this.bm = new BackingModel();
    }

    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
    
    public void loadData(){
        this.filterList = new HashMap();
        try{
            System.out.println("Load Data...");            
            //Define Filter List                       
            this.filterList.put("site.siteid",this.getAuthBean().getSiteid());
            this.filterList.put("periode.periodeid",this.periodeid);
            this.lazyModel =  new LazySapUploadDataModel(this,this.bm);                 
        }catch(Exception ex){            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);                      
        }                
    }
    
    
//    private List<Stock> generateStocks(){
//        AuthBean authbean = this.getAuthBean();
//        EntityManager em = getEntityManager();   
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Stock> query = builder.createQuery(Stock.class);
//        Root<Stock> oRoot = query.from(Stock.class);        
//        Predicate criteria = builder.conjunction();        
//        criteria = builder.and(criteria,builder.equal(oRoot.get("site").get("siteid"), authbean.getSiteid()));                                        
//        criteria = builder.and(criteria,builder.equal(oRoot.get("periode").get("periodeid"), this.periodeid)); 
//        if(this.filterArticleid){
//            if(this.operatorArticleid.equals("eq")){
//                criteria = builder.and(criteria,builder.equal(oRoot.get("articleid"),this.articleid));
//                        
//            }
//        }
//        if(this.filterDifqty){
//            if(this.operatorDifqty.equals("eq")){
//                criteria = builder.and(criteria,builder.equal(oRoot.<Double>get("difqty"),this.difqty));
//            }
//            if(this.operatorDifqty.equals("lt")){
//                criteria = builder.and(criteria,builder.lt(oRoot.<Double>get("difqty"),this.difqty));
//            }
//            if(this.operatorDifqty.equals("gt")){
//                criteria = builder.and(criteria,builder.gt(oRoot.<Double>get("difqty"),this.difqty));
//            }                                    
//        }
//        
//        if(this.filterDifunitcost){
//            if(this.operatorDifunitcost.equals("eq")){
//                criteria = builder.and(criteria,builder.equal(oRoot.<Double>get("difunitcost"),this.difunitcost));
//            }
//            if(this.operatorDifunitcost.equals("lt")){
//                criteria = builder.and(criteria,builder.lt(oRoot.<Double>get("difunitcost"),this.difunitcost));
//            }
//            if(this.operatorDifunitcost.equals("gt")){
//                criteria = builder.and(criteria,builder.gt(oRoot.<Double>get("difunitcost"),this.difunitcost));
//            }                 
//        }
//        
//        
//        if(this.filterPrinted){
//             criteria = builder.and(criteria,builder.equal(oRoot.<Boolean>get("printed"), true));
//        }
//        
//        if(this.sortOrder.equals("asc")){            
//            query.orderBy(builder.asc(oRoot.get(this.sortField)));
//        }
//
//        if(this.sortOrder.equals("desc")){
//            query.orderBy(builder.desc(oRoot.get(this.sortField)));
//        }
//        
//        query.where(criteria);
//        List<Stock> stocks = em.createQuery(query).getResultList();
//        return stocks;                                
//    }
    
    
    public void updateOpnamqty(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        System.out.println("Site id : " + authbean.getSiteid());
        System.out.println("Periode id : " + this.periodeid);
        String strSQL = "SELECT s FROM Stock s WHERE s.site.siteid=:siteid  AND s.periode.periodeid=:periodeid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", authbean.getSiteid())
                    .setParameter("periodeid", this.refreshperiodeid);
            query.setHint("eclipselink.refresh", "true");
            List<Stock> os = query.getResultList();            
            for(Stock o : os){
                Double qty = this.calculateOpnnamqty(o.getArticleid());
                o.setOpnamqty(qty);
                o.setDifqty(o.getQty() - (o.getPendingqty() + o.getOpnamqty()));
                o.setDifunitcost(o.getDifqty() * o.getUnitcost());
                o.setRefreshdate(new Date(System.currentTimeMillis()));
                this.saveOpnamqty(o);
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Refresh Stock Data : " + os.size() + " records","");
            FacesContext.getCurrentInstance().addMessage(null, msg);                                             
        }catch(NoResultException ex){
            ex.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(),"");            
            FacesContext.getCurrentInstance().addMessage(null, msg); 
        }finally{
            em.close();
        }             
    }
    
    public void generateFile(){
        System.out.println("Generate File");        
        AuthBean authbean = this.getAuthBean();
        EntityManager em = getEntityManager();   
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Stock> query = builder.createQuery(Stock.class);
        Root<Stock> oRoot = query.from(Stock.class);        
        Predicate criteria = builder.conjunction();        
        criteria = builder.and(criteria,builder.equal(oRoot.get("site").get("siteid"), authbean.getSiteid()));                                        
        criteria = builder.and(criteria,builder.equal(oRoot.get("periode").get("periodeid"), this.periodeid));       
        query.where(criteria);
        List<Stock> stocks = em.createQuery(query).getResultList();
        System.out.println("Row Count : " + stocks.size());        
        String outputFile = this.periodeid + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date(System.currentTimeMillis())) + ".csv";
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();                 
        String fullPath = extContext.getRealPath("//temp") + "//" + outputFile;        
        //boolean alreadyExists = new File(fullPath).exists();
        try {
                System.out.println("Create File CSV");
                // use FileWriter constructor that specifies open for appending
                CsvWriter csvOutput = new CsvWriter(new FileWriter(fullPath, true), ',');
                csvOutput.setDelimiter(';');

                // write out a few records
                for(Stock o : stocks){
                    csvOutput.write(o.getSite().getSiteid());
                    csvOutput.write(o.getPid());
                    csvOutput.write(Integer.toString(o.getItem()));
                    csvOutput.write(o.getArticleid());
                    csvOutput.write(Double.toString(o.getQty()));
                    csvOutput.write(Double.toString(o.getOpnamqty()));
                    csvOutput.write(Double.toString(o.getUnitcost()));
                    csvOutput.endRecord();
                }
                csvOutput.close();
              
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/temp/" + outputFile);
        this.file = new DefaultStreamedContent(stream, "text/csv", outputFile);          
    }
    
    public void printedRequest(){
        for(Stock o : this.selectedObjects){
            o.setPrinted(true);
            this.updateStock(o);
        }
        this.selectedObjects = null;        
        this.loadData();
    }
    
    public void clearPrintTag(){
        EntityManager em = this.getEntityManager();
        String strSQL = "UPDATE Stock s SET s.printed=:printed";        
        em.getTransaction().begin();
        try{
            Query query = em.createQuery(strSQL).setParameter("printed", false);
            query.executeUpdate();
            em.getTransaction().commit();
            this.loadData();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
        
    }
    
    private void closeSession(){
        List<Transstock> os = this.findTransstocks();
        for(Transstock o : os){            
            this.updateTransstock(o);
        }
    }
    private double calculateOpnnamqty(String articleid){
        double qty = 0;
        List<Transtockdetail> os = this.findByArticleID(articleid);
        if(os != null){
            for(Transtockdetail o : os){
                qty = qty + o.getQty2();
            }            
        }
        return qty;
    }
    
    private void saveOpnamqty(Stock o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }
    
    private List<Transtockdetail> findByArticleID(String articleid){
        AuthBean authbean = this.getAuthBean();
        List<Transtockdetail> os = new ArrayList();
        List<Transtockdetail> osTemp = new ArrayList();        
        EntityManager em = this.getEntityManager();        
        String strSQL = "SELECT t FROM Transtockdetail t WHERE t.transstock.site.siteid=:siteid AND t.transstock.periode.periodeid=:periodeid AND t.product.articleid=:articleid";
        try{            
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", authbean.getSiteid())
                    .setParameter("periodeid", this.refreshperiodeid)
                    .setParameter("articleid",articleid );
            query.setHint("eclipselink.refresh", true);
            return query.getResultList();
        }catch(NoResultException ex){            
            return null;
        }finally{
            em.close();
        }        
    }
     
    private Product findProduct(String ean){
        EntityManager em = this.getEntityManager();
        try{
            return (Product) em.createNamedQuery("Product.findByEan").setParameter("ean", ean).getSingleResult();
        }catch(Exception ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private List<Transstock> findTransstocks(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT t FROM Transstock t WHERE t.site.siteid=:siteid AND t.periode.periodeid=:periodeid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", authbean.getSiteid())
                    .setParameter("periodeid", this.refreshperiodeid);
            query.setHint("eclipselink.refresh", true);
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private void updateStock(Stock o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }        
    }
    private void updateTransstock(Transstock o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }
    
    public void rptRequestPrinted() throws IOException{   
        AuthBean authbean = this.getAuthBean();        
        String url;
        url = "/StockOp/ReportArticle?siteid=" + authbean.getSiteid() + "&periodeid=" + periodeid  + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();                
        
        FacesContext context = FacesContext.getCurrentInstance();
        try {           
           context.getExternalContext().redirect(url);
        }catch (Exception ex) {
           System.out.println(ex.getMessage());
        }
        finally{
           context.responseComplete();
        }             
    }    
            
    private AuthBean getAuthBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ValueBinding binding = application.createValueBinding("#{authBean}");
        AuthBean authbean =  (AuthBean) binding.getValue(facesContext);
        return authbean;
    }  
    
    public LazyDataModel<Stock> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Stock> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }

    public Stock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Stock selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Stock getObject() {
        return object;
    }

    public void setObject(Stock object) {
        this.object = object;
    }

    public String getRefreshperiodeid() {
        return refreshperiodeid;
    }

    public void setRefreshperiodeid(String refreshperiodeid) {
        this.refreshperiodeid = refreshperiodeid;
    }

    public Stock[] getSelectedObjects() {
        return selectedObjects;
    }

    public void setSelectedObjects(Stock[] selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public double getDifqty() {
        return difqty;
    }

    public void setDifqty(double difqty) {
        this.difqty = difqty;
    }

    public double getDifunitcost() {
        return difunitcost;
    }

    public void setDifunitcost(double difunitcost) {
        this.difunitcost = difunitcost;
    }

    public boolean isFilterArticleid() {
        return filterArticleid;
    }

    public void setFilterArticleid(boolean filterArticleid) {
        this.filterArticleid = filterArticleid;
    }

    public boolean isFilterDifqty() {
        return filterDifqty;
    }

    public void setFilterDifqty(boolean filterDifqty) {
        this.filterDifqty = filterDifqty;
    }

    public boolean isFilterDifunitcost() {
        return filterDifunitcost;
    }

    public void setFilterDifunitcost(boolean filterDifunitcost) {
        this.filterDifunitcost = filterDifunitcost;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getOperatorArticleid() {
        return operatorArticleid;
    }

    public void setOperatorArticleid(String operatorArticleid) {
        this.operatorArticleid = operatorArticleid;
    }

    public String getOperatorDifqty() {
        return operatorDifqty;
    }

    public void setOperatorDifqty(String operatorDifqty) {
        this.operatorDifqty = operatorDifqty;
    }

    public String getOperatorDifunitcost() {
        return operatorDifunitcost;
    }

    public void setOperatorDifunitcost(String operatorDifunitcost) {
        this.operatorDifunitcost = operatorDifunitcost;
    }

    public boolean isFilterPrinted() {
        return filterPrinted;
    }

    public void setFilterPrinted(boolean filterPrinted) {
        this.filterPrinted = filterPrinted;
    }

    
    public StreamedContent getFile() {
        this.generateFile();
        return file;
    }   

    public HashMap getFilterList() {
        return filterList;
    }

    public void setFilterList(HashMap filterList) {
        this.filterList = filterList;
    }
}
