/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package baihuih.task3;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 *
 * @author Baihui
 */
public class BlockChain {

    List<Block> chain = null;
    String chainHash;

    // Constructor to initialize the chain array and chainHash
    BlockChain() {
        this.chain = new ArrayList<Block>();
        chainHash = "";
        
        Block b = new Block(0, new Timestamp(System.currentTimeMillis()), "Genesis", 2);
        this.addBlock(b);
    }

    // Getter method to extract current time stamp
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    //retrun a reference to the most recently added Block
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    //Parameter: newBlock - block that is added to the blockChain as the most recent block
    public void addBlock(Block newBlock) {
        // set the prviousHash to the hash of last block
        newBlock.setPreviousHash(chainHash);

        // run proofOfWork to increment the nonce of the new block
        newBlock.proofOfWork();

        // add new block to the chain
        chain.add(newBlock);

        // set chainHash to the hash of new block
        chainHash = newBlock.calculateHash();

    }

    // Override toString() method to return a string representation of the entire chain
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\"chain\" : [");

        // index
        for (int i = 0; i < chain.size(); i++) {
            sb.append(chain.get(i).toString());
            if (i != chain.size() - 1) {
                sb.append(",\n");
            }
        }

        sb.append("\n], \"chainHash\":\"");

        sb.append(chainHash);

        sb.append("\"}");

        return sb.toString();
    }
    
    // check if the chain of block is valid
    // by checking if the previous hash of each block and the chainHash are valid
    // returns true if and only if the chain is valid 
    public boolean isChainValid() {
        // set prevHash to empty string for comparison
        String prevHash = "";

        
        if (chain.size() == 1) {// if only one block in the chain
            Block b = chain.get(0);
           
            // calculate the hash of the block
            String hash = b.calculateHash();
            // check if previousHash is equal to empty string
            boolean is_previousHash_valid = b.getPreviousHash().equals(prevHash);
            // check if the hash is equal to the result of proofOfWork
            boolean is_proofOfWork_valid = hash.equals(b.proofOfWork());
            // check if the chainHash is equal to hash
            boolean is_chainHash_valid = chainHash.equals(hash);

            // if all conditions are true, the chain is valid
            if (is_proofOfWork_valid && is_chainHash_valid && is_previousHash_valid) {
                return true;
            } else {// print error message and return false
                // displaying error message to console according to which condition is wrong
                if (!is_proofOfWork_valid) {
                    String zeros = "";
                    for (int i = 0; i < b.getDifficulty(); i++) {
                        zeros += "0";
                    }
                    System.out.println("Improper hash on node 0 Does not begin with " + zeros);
                } else if (!is_chainHash_valid) {
                    System.out.println("Inproper chain hash in chain");
                } else if (!is_previousHash_valid) {
                    System.out.println("Inproper previous hash on node 0 hash in chain");
                }
                return false;
            }

        } else {// if more than one block in the chain 
            boolean is_previousHash_valid = false;
            boolean is_proofOfWork_valid = false;

            for (int i = 0; i < chain.size(); i++) { // for each block
                Block b = chain.get(i);
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
                    System.out.println("Improper hash on node " + i + " Does not begin with " + zeros);
                    return false;
                } else if (!is_previousHash_valid) {
                    System.out.println("Inproper previous hash on node " + i + " hash in chain");
                    return false;
                }
                
                // reset prevHash to the hash of this block for next iteration
                prevHash = b.calculateHash();
            }
            
            // after checking all blocks, check the chainHash is pointing to the latest block
            boolean is_chainHash_valid = chainHash.equals(prevHash);
            if (is_chainHash_valid) {
                return true;
            } else {
                System.out.println("Inproper chain hash in chain");
                return false;
            }

        }
    }

    // this rountine repairs the chain
    // recompute the hashes and make sure they are valid
    public void repairChain() {
        if (chain.size() == 1) {// if only on block in the chain
            Block b = chain.get(0);
            // reset previousHash to empty string.
            b.setPreviousHash("");
            
            // recompute proofOfWork and assign result to chainHash
            chainHash = b.proofOfWork();
            
            // put repaired block back to the chain
            chain.set(0, b);
        } else {// if more than one block in the chain
            // placeholder for prevHash
            String prevHash = "";
            for (int i = 0; i < chain.size(); i++) { // for each block
             
                Block b = chain.get(i);
                // reset previousHash to correct hash from prevHash
                b.setPreviousHash(prevHash);
                
                //recompute proofOfWork and assign it to prevHash
                prevHash = b.proofOfWork();
               
                // put the block back to the chain
                chain.set(i,b);

            }
            // at the end, make sure chainHash is pointing to the last block
            chainHash = prevHash;
        }

    }

    
    // main method is the UI for running block chain
    // My program takes about 8000 milliseconds to add block of difficulty 4
    // Whil it takes about 15000 millisecond to add block of difficulty 5
    // To verify, it always take about 1-3 milliseconds to run    
    public static void main(String[] args) {
        BlockChain bc = new BlockChain();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Block b = new Block(0, ts, "Genesis", 2);

        bc.addBlock(b);

        //System.out.println(bc.toString());

        Scanner input = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.print("Block Chain Menu\n");
            System.out.print("1. Add a transaction to the blockchain\n");
            System.out.print("2. Verify the blockchain\n");
            System.out.print("3. View the blockchain\n");
            System.out.print("4. Corrupt the chain\n");
            System.out.print("5. Hide the corruption by repairing the chain\n");
            System.out.print("6. Exit\n");

            choice = Integer.parseInt(input.nextLine());

            switch (choice) {
                case 1:
                    long start = System.currentTimeMillis();
                    System.out.println("Enter difficulty");
                    int difficulty = Integer.parseInt(input.nextLine());
                    System.out.println("Enter Transaction");
                    String data = input.nextLine();

                    ts = new Timestamp(System.currentTimeMillis());
                    b = new Block(bc.chain.size(), ts, data, difficulty);
                    bc.addBlock(b);

                    long end = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block " + (end - start) + " milliseconds");
                    break;
                case 2:
                    start = System.currentTimeMillis();
                    System.out.println("Verifying");
                    boolean is_valid = bc.isChainValid();

                    System.out.println("Chain verification: " + is_valid);
                    end = System.currentTimeMillis();
                    System.out.println("Total execution time to verify the chain is " + (end - start) + " milliseconds");

                    break;
                case 3:
                    System.out.println("View the BlockChain");
                    System.out.println(bc.toString());
                    break;
                case 4:
                    System.out.println("Corrupt the BlockCahin\nEnter block to corrupt");
                    int index = Integer.parseInt(input.nextLine());
                    System.out.println("Enter new data for block " + index);
                    data = input.nextLine();
                    bc.chain.get(index).setData(data);
                    System.out.println("Block " + index + " now holds " + data);
                    break;
                case 5:
                    System.out.println("Repair the BlockChain");
                    bc.repairChain();
                    System.out.println("Repair complete");
                    break;
                case 6:
                    break;
            }

        } while (choice != 6);
        input.close();

    }

}
