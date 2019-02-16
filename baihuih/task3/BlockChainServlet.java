/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baihuih.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Baihui
 */
@WebServlet(name = "BlockChainServlet", urlPatterns = {"/BlockChainServlet/*"})
public class BlockChainServlet extends HttpServlet {

    private BigInteger e = new BigInteger("65537");
    private BigInteger n = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");
    private BlockChain bc = new BlockChain();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BlockChainServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BlockChainServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        System.out.println("Console: deGet visited");

        String result = "";

        // parse the name from URL to see if user wants verify or view 
        String name = (request.getPathInfo()).substring(1);

        if (name.equals("view")) {
            // calling toString method to display block chain 
            result = bc.toString();

            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
        } else if (name.equals("verify")) {
            // calling isChainValid method to check if the block chain is valid 
            boolean is_valid = bc.isChainValid();

            result += "Chain verification: " + is_valid + "\n";
            if (!is_valid) {
                result += verificationMessage();
            }

            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");

        } else {// if name is not valid, set status to 401
            response.setStatus(401);
            return;
        }

        // output result to client for feedback
        PrintWriter out = response.getWriter();
        out.println(result);
    }

    public String verificationMessage() {
        // set prevHash to empty string for comparison
        String prevHash = "";

        if (bc.chain.size() == 1) {// if only one block in the chain
            Block b = bc.chain.get(0);

            
            // calculate the hash of the block
            String hash = b.calculateHash();
            // check if previousHash is equal to empty string
            boolean is_previousHash_valid = b.getPreviousHash().equals(prevHash);
            // check if the hash is equal to the result of proofOfWork
            boolean is_proofOfWork_valid = hash.equals(b.proofOfWork());
            // check if the chainHash is equal to hash
            boolean is_chainHash_valid = bc.chainHash.equals(hash);

            // if all conditions are true, the chain is valid
            if (is_proofOfWork_valid && is_chainHash_valid && is_previousHash_valid) {
                //return true;
            } else {// print error message and return false
                // displaying error message to console according to which condition is wrong
                if (!is_proofOfWork_valid) {
                    String zeros = "";
                    for (int i = 0; i < b.getDifficulty(); i++) {
                        zeros += "0";
                    }
                   
                    return "Improper hash on node 0 Does not begin with " + zeros;
                } else if (!is_chainHash_valid) {
                    
                    return "Inproper chain hash in chain";
                } else if (!is_previousHash_valid) {
                    
                    return "Inproper previous hash on node 0 hash in chain";
                }
                //return false;
            }

        } else {// if more than one block in the chain 
            boolean is_previousHash_valid = false;
            boolean is_proofOfWork_valid = false;

            for (int i = 0; i < bc.chain.size(); i++) { // for each block
                Block b = bc.chain.get(i);
                // calculate the hash of block i
                String hash = b.calculateHash();

                // check if previousHash is valid
                is_previousHash_valid = b.getPreviousHash().equals(prevHash);
                // check if the hash is equal to the result of proofOfWork
                is_proofOfWork_valid = hash.equals(b.proofOfWork());

                if (!is_proofOfWork_valid) {// if not valid, display error message and return false
                    String zeros = "";
                    for (int j = 0; j < b.getDifficulty(); j++) {
                        zeros += "0";
                    }
                    
                    return "Improper hash on node " + i + " Does not begin with " + zeros;
                } else if (!is_previousHash_valid) {
                    System.out.println("Inproper previous hash on node " + i + " hash in chain");
                   
                    return "Inproper previous hash on node " + i + " hash in chain";
                }

                // reset prevHash to the hash of this block for next iteration
                prevHash = b.calculateHash();
            }

            // after checking all blocks, check the chainHash is pointing to the latest block
            boolean is_chainHash_valid = bc.chainHash.equals(prevHash);
            if (is_chainHash_valid) {
                //return true;
            } else {
                //System.out.println("Inproper chain hash in chain");
                return "Inproper chain hash in chain";
            
            }

        }

        return "";
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);

        System.out.println("Console: doPost visited");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String input = br.readLine();
        //String difficulty = br.readLine();

        String data = input.split(",")[0];
        String difficulty = input.split(",")[1];
        
        System.out.println("data " + data);
        
        System.out.println("difficulty " + difficulty);
        
        String result = "";
        
        String transaction = data.split("#")[0];
        BigInteger signature = new BigInteger(data.split("#")[1]);

        //System.out.println("Transaction " + transaction);
        //System.out.println("Signature " + signature.toString());

        // decrypted the signature 
        BigInteger decrypted = signature.modPow(e, n);
        //System.out.println("decrypted " + decrypted.toString());

        MessageDigest md = null;

        try {
            // hash the input using SHA-265
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.getStackTrace();
        }
        // get the hash value from bytes
        //byte[] signatureHash =  md.digest(decrypted.toByteArray());
        byte[] transactionHash = md.digest(transaction.getBytes());

        byte[] significantByte = new byte[transactionHash.length + 1];
        significantByte[0] = 0;
        for (int i = 0; i < transactionHash.length; i++) {
            significantByte[i + 1] = transactionHash[i];
        }

        // convert the hash result into different types of binary formats
        //BigInteger signatureInt = new BigInteger(DatatypeConverter.printHexBinary(significantByte),16);
        BigInteger transactionHashInt = new BigInteger(DatatypeConverter.printHexBinary(significantByte), 16);

        System.out.println("signature" + decrypted.toString());
        System.out.println("transactionInt " + transactionHashInt.toString());

        if (decrypted.compareTo(transactionHashInt) == 0) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Block b = new Block(bc.chain.size(), ts, transaction, Integer.parseInt(difficulty));

            

            result = addBlock(data, Integer.parseInt(difficulty));
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            
            //long end = System.currentTimeMillis();
            //result += "Total execution time to add this block " + (end - start) + " milliseconds";
        } else {
            result =  "Signature is wrong";
        }
        


        PrintWriter out = response.getWriter();
        out.println(result);

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "addBlock")
    public String addBlock(@WebParam(name = "data") String data, @WebParam(name = "difficulty") int difficulty) {

        long start = System.currentTimeMillis();
        String transaction = data.split("#")[0];
        BigInteger signature = new BigInteger(data.split("#")[1]);

        System.out.println("Transaction " + transaction);
        System.out.println("Signature " + signature.toString());

        // decrypted the signature 
        BigInteger decrypted = signature.modPow(e, n);
        System.out.println("decrypted " + decrypted.toString());

        MessageDigest md = null;

        try {
            // hash the input using SHA-265
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.getStackTrace();
        }
        // get the hash value from bytes
        //byte[] signatureHash =  md.digest(decrypted.toByteArray());
        byte[] transactionHash = md.digest(transaction.getBytes());

        byte[] significantByte = new byte[transactionHash.length + 1];
        significantByte[0] = 0;
        for (int i = 0; i < transactionHash.length; i++) {
            significantByte[i + 1] = transactionHash[i];
        }

        // convert the hash result into different types of binary formats
        //BigInteger signatureInt = new BigInteger(DatatypeConverter.printHexBinary(significantByte),16);
        BigInteger transactionHashInt = new BigInteger(DatatypeConverter.printHexBinary(significantByte), 16);

        System.out.println("signature" + decrypted.toString());
        System.out.println("transactionInt " + transactionHashInt.toString());

        if (decrypted.compareTo(transactionHashInt) == 0) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Block b = new Block(bc.chain.size(), ts, transaction, difficulty);

            bc.addBlock(b);

            long end = System.currentTimeMillis();
            return "Total execution time to add this block " + (end - start) + " milliseconds";
        } else {
            return "Signature is wrong";
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
