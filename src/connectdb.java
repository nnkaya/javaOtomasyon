

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nurdan
 */
class connectdb {
    private Connection conn;
    Statement stmt ;
    public Connection baglan(){
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/sampledb");
            
        }catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println("hata baglan");
            Logger.getLogger(connectdb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(connectdb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public boolean kullaniciOnay(String type,String kullanici,String password){
        conn = this.baglan();
        try{
     
        stmt = conn.createStatement();
        String query = null;
        if(type == "ogrenci")
            query = "SELECT kullaniciAdi,passwd FROM ogrenci";
        else if(type == "ogretimUyesi")
            query = "SELECT kullaniciAdi,passwd FROM ogretimUyesi";
        if(type == "idariMemur")
            query = "SELECT kullaniciAdi,passwd FROM idariMemur";
        
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){    
            if(kullanici.equals(rs.getString("kullaniciAdi")) && password.equals(rs.getString("passwd")))
                return true;
        
        }   
    
        }catch(Exception e){
            System.out.println("hata kullaniciOnay");
        }
        return false;   
    }
    
    public void dersEkle(int id,String ders, int gun, String zaman ){
        conn = this.baglan();
        try{
            //stmt = conn.createStatement();
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("INSERT INTO dersler VALUES (?,?,?,?)");
            ps.setInt(1,id);
            ps.setString(2,ders);
            ps.setInt(3, gun);
            ps.setString(4,zaman);
            int executeUpdate = ps.executeUpdate();
            DersEklendi t = new DersEklendi();
                    t.setVisible(true);
        }catch(Exception e){
            System.out.println("hata dersEkle");
        }
     
    }
    
    
   
    public void dersKaldir(int dersID){
        conn = this.baglan();
        try{
            String sql = "DELETE FROM dersler WHERE ders_id = "+dersID;
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            DersKaldirildi t = new DersKaldirildi();
            t.setVisible(true);
        }catch(Exception e){
            System.out.println("hata dersKaldir");
        }

    }
    
    public void dersSec(String ogrAdi, int dersid){
        conn = this.baglan();
        try{
            String sql = "SELECT * FROM dersler";
            stmt = conn.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int ders_id = rs.getInt(1);
                String ders_adi = rs.getString(2);
                int gun_id = rs.getInt(3);
                String ders_zaman = rs.getString(4);
                if(dersid == ders_id){
                       String sql2 = "INSERT INTO "+ ogrAdi + "(aldigi_dersler)" + " VALUES(?)";
                       stmt = conn.createStatement();
                       PreparedStatement preparedStmt = conn.prepareStatement(sql2);
                       preparedStmt.setString (1, ders_adi);
                       preparedStmt.execute();
                       DersSecildi f = new DersSecildi();
                       f.setVisible(true);
                       break;
                }
             }
        }catch(Exception e){
            System.out.println("hata dersSec");
        }
    
    
  
    }
    
   public void displayDersler(){
        conn = this.baglan();
        try{
            String sql = "SELECT * FROM dersler";
            
            stmt = conn.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery(sql);
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            tableModel.addColumn("DERS İD");
            tableModel.addColumn("DERS ADI");
            tableModel.addColumn("GÜN İD");
            tableModel.addColumn("ZAMAN");
            while (rs.next()) {
                int ders_id = rs.getInt(1);
                String ders_adi = rs.getString(2);
                int gun_id = rs.getInt(3);
                String ders_zaman = rs.getString(4);
                tableModel.insertRow(0, new Object[] { ders_id,ders_adi,gun_id,ders_zaman });
            }
        JFrame f = new JFrame();
        f.setTitle("DERSLER"); 
        f.setSize(550, 350);
        f.add(new JScrollPane(table));
        f.setVisible(true); 
        
        }catch(Exception e){
            System.out.println("hata displayDersler");
        }
    }
    
   
   public void uyeOl(String kullanici,String password, String type){
        conn = this.baglan();
        try{
     
        stmt = conn.createStatement();
        String query = null;
        if(type == "Öğrenci"){
            String CREATE_TABLE_SQL="CREATE TABLE "+kullanici + " (" + "aldigi_dersler VARCHAR(45) NOT NULL,"+ "harf_notu CHAR(2) NOT NULL)";
            System.out.println(CREATE_TABLE_SQL);
            stmt = conn.createStatement();
            stmt.executeUpdate(CREATE_TABLE_SQL);
            System.out.println("Table created");
            query =  "INSERT INTO ogrenci(kullaniciAdi,passwd)" + " VALUES(?,?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, kullanici);
            preparedStmt.setString (2, password);
            preparedStmt.execute();
        }    
        else if(type == "Öğretim Üyesi"){
            query =  "INSERT INTO ogretimUyesi(kullaniciAdi,passwd)" + " VALUES(?,?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, kullanici);
            preparedStmt.setString (2, password);
            preparedStmt.execute();
        }
        if(type == "İdari Memur"){
            query =  "INSERT INTO idariMemur(kullaniciAdi,passwd)" + " VALUES(?,?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, kullanici);
            preparedStmt.setString (2, password);
            preparedStmt.execute();
        }
        UyeOlundu f = new UyeOlundu();
        f.setVisible(true);
    
        }catch(Exception e){
            System.out.println("hata uyeOl");
        } 
    }
   
   public void notGir(String ogrAdi, String dersAdi, String harfNotu){
       conn = this.baglan();
        try{
     
            stmt = conn.createStatement();
            String query = null;
            query =  "UPDATE "+ogrAdi+" SET harf_notu='"+harfNotu+"' WHERE aldigi_dersler='"+dersAdi+"'";
            System.out.println(query);
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
        }catch(Exception e){
            System.out.println("hata notGir");
        } 
   
   }
   
   public void displayOgrTable(String ogrName){
        conn = this.baglan();
        try{
            String sql = "SELECT * FROM "+ogrName;
            
            stmt = conn.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery(sql);
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            tableModel.addColumn("DERS ADI");
            tableModel.addColumn("HARF NOTU");

            while (rs.next()) {
                String ders_adi = rs.getString(1);
                String harf_notu = rs.getString(2);
                tableModel.insertRow(0, new Object[] { ders_adi,harf_notu });
            }
            JFrame f = new JFrame();
            f.setTitle("Ders Notları"); 
            f.setSize(550, 350);
            f.add(new JScrollPane(table));
            f.setVisible(true); 
        
        }catch(Exception e){
            System.out.println("hata displayOgrTable");
        }
   }
   
   public void displayDersProgrami(String kullaniciAdi){
        conn = this.baglan();
        try{
            String sql = "SELECT ders_adi,ders_zaman" +
                            "  FROM "+kullaniciAdi +
                            "  INNER JOIN dersler ON aldigi_dersler = ders_adi";
            stmt = conn.createStatement();
            
            ResultSet rs;
             System.out.println("1");

            rs = stmt.executeQuery(sql);
             System.out.println("2");
             
            DefaultTableModel tableModel = new DefaultTableModel();
            JTable table = new JTable(tableModel);
            System.out.println("1");
            tableModel.addColumn("DERS ADI");
            tableModel.addColumn("ZAMAN");
            while (rs.next()) {
                String adi = rs.getString("ders_adi");
                String dersZaman = rs.getString("ders_zaman");
                System.out.println(adi);
                System.out.println(dersZaman);
                tableModel.insertRow(0, new Object[] {adi,dersZaman});
                
            }
        JFrame f = new JFrame();
        f.setTitle("DERS PROGRAMI");
        f.add(new JScrollPane(table));
        f.setSize(550, 350);
        f.setVisible(true); 
        
        }catch(Exception e){
            System.out.println("hata displayDersProgrami");
        }
    }
   
}
