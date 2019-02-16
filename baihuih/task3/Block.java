/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baihuih.task3;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Baihui
 */
public class Block {

    private int index;
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    private BigInteger nonce = new BigInteger("0");
    private int difficulty;

    Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;

    }

    //This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce and difficulty
    public String calculateHash() {
        String concat = Integer.toString(index) + timestamp.toString() + data + previousHash + nonce.toString() + Integer.toString(difficulty);

        MessageDigest md = null;

        try {
            // hash the input using SHA-265
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.getStackTrace();
        }
        // get the hash value from bytes
        byte[] hashValue = md.digest(concat.getBytes());

        // convert the hash result into different types of binary formats
        String hexdecimalText = DatatypeConverter.printHexBinary(hashValue);

        return hexdecimalText;
    }

    // This method finds a good hash. it increments the nonce until it produces a good hash
    public String proofOfWork() {

        int count;
        String hexaText;
        do {
            hexaText = this.calculateHash();

            //String binaryText = new BigInteger(hexaText, 16).toString(2);

            char[] binaryChar = hexaText.toCharArray();

            count = 0;
            while (count != difficulty) {
                if (binaryChar[count] == '0') {
                    count++;
                } else {
                    break;
                }
            }
            if (count != difficulty) {
                nonce = nonce.add(new BigInteger("1"));
            }
        } while (count != difficulty);

        return hexaText;
    }

    // Simple getter method
    public int getDifficulty() {
        return difficulty;
    }

    //Simple setter method
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    //Override Java's toSting method
    //A JSON representation of all of this block's data is returned 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"");

        // index
        sb.append("index\":");
        sb.append(index);
        sb.append(", ");

        // timestamp
        sb.append("\"timestamp\":\"");
        sb.append(timestamp.toString());
        sb.append("\", ");

        // data
        sb.append("\"Tx\":\"");
        sb.append(data);
        sb.append("\", ");

        // previousHash
        sb.append("\"previousHash\":\n\"");
        sb.append(previousHash);
        sb.append("\", ");

        // nonce
        sb.append("\"nonce\":");
        sb.append(nonce.toString());
        sb.append(", ");

        // difficulty
        sb.append("\"difficulty\":");
        sb.append(difficulty);
     

        sb.append("}\n");

        return sb.toString();
    }

    // Simple setter method
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    // simple getter method
    public String getPreviousHash() {
        return previousHash;
    }

    // Simple getter method
    public int getIndex() {
        return this.index;
    }

    // simple setter method 
    public void setIndex(int index) {
        this.index = index;
    }

    //Simple setter method
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // simple getter method
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    // simple getter method
    public String getData() {
        return this.data;
    }

    // simple setter method
    public void setData(String data) {
        this.data = data;
    }
    
    // simple getter method
    public BigInteger getNonce(){
        return nonce;
    }
}
