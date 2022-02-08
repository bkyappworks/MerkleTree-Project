/**
 * 95-771 Data Structures and Algorithms
 *  @author Yueh Liu
 *  Assignment #1
 *  1. read a UTF-8 file of text lines – delimited by line breaks. Each line will be stored in a node on a SinglyLinkedList original.
 *  2. create a second SinglyLinkedList hashed contains the cryptographic hashes of these nodes in original.
 *  3. create a new SinglyLinkedList for each level in the Merkle tree.
 *  4. create a SinglyLinkedList headList that contains heads of SinglyLinkedLists at each level.
 */

import edu.cmu.andrew.yuehl.SinglyLinkedList;
import edu.colorado.nodes.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class merkleTree {
    private SinglyLinkedList original;
    private SinglyLinkedList hashed;
    private SinglyLinkedList headList;
    private String rootVal;
    public merkleTree(){
        original = new SinglyLinkedList();
        hashed = new SinglyLinkedList();
        headList = new SinglyLinkedList();
    }
    /**
     * read a UTF-8 file of text lines – delimited by line breaks. Each line will be stored in a node on a SinglyLinkedList original
     * reference: https://www.w3schools.com/java/java_files_read.asp
     * @param filename
     */
    public void readFile(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                original.addAtEndNode(data);
            }
            int size = original.countNodes();
            if (size % 2 != 0) {
                Object last = original.getLast();
                original.addAtEndNode(last);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * a second list will be created containing the cryptographic hashes of these nodes
     * populate SinglyLinkedList hashed with h()
     * @param original
     */
    public void createHashed(SinglyLinkedList original) {
        ObjectNode head = (ObjectNode) original.getHeadNode();
        while (head != null) {
            try {
                hashed.addAtEndNode(h(head.getData().toString()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            head = head.getLink();
        }
    }
    public static String h(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= 31; i++) {
            byte b = hash[i];
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    /**
     * create a new list for each level in the Merkle tree
     * @param list
     * @return
     */
    public SinglyLinkedList buildTree(SinglyLinkedList list) {
        ObjectNode head = (ObjectNode) list.getHeadNode();
        // head is null or head.next == null, means the list is at root level
        if (head == null || head.getLink() == null) {
            rootVal = head.getData().toString();
            return list;
        }
        // If the list has an odd number of nodes, copy the last node and then add it to the end of the list
        int size = list.countNodes();
        if (size % 2 != 0) {
            Object last = list.getLast();
            list.addAtEndNode(last);
        }
        SinglyLinkedList newList = new SinglyLinkedList(); // the list of next level

        while (head != null && head.getLink() != null) {
            // every two node as a group
            try {
                newList.addAtEndNode(h(head.getData().toString()+ head.getLink().getData().toString()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            head = head.getLink().getLink();
        }
        headList.addAtEndNode(newList.getHeadNode());
        return buildTree(newList);
    }
    /**
     * "smallFile.txt" -> Merkle root = A4E10610B30C40CA608058C521AD3D9EEC42C1892688903984580C56D3CF8A7D
     * "CrimeLatLonXY.csv" -> Merkle root = A5A74A770E0C3922362202DAD62A97655F8652064CCCBE7D3EA2B588C7E07B58
     * "CrimeLatLonXY1990_Size2.csv" -> Merkle root = DDD49991D04273A7300EF24CFAD21E2706C145001483D161D53937D90F76C001
     * "CrimeLatLonXY1990_Size3.csv" -> Merkle root = 313A2AD830ED85B5203C8C2A9895ADFA521CD4ABB74B83C25DA2C6A47AE08818
     * The file "CrimeLatLonXY.csv" will generate A5A74A770E0C3922362202DAD62A97655F8652064CCCBE7D3EA2B588C7E07B58
     * @param args
     */
    public static void main(String[] args) {
        merkleTree merkleTree = new merkleTree();
        // show all four Merkle roots and be sure to say which one of these four files has the Merkle root that we see
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter A Filename: ");
        String filename = scanner.nextLine();
        merkleTree.readFile(filename);// "smallFile.txt" // "CrimeLatLonXY.csv" // "CrimeLatLonXY1990_Size2.csv" // "CrimeLatLonXY1990_Size3.csv"
        merkleTree.createHashed(merkleTree.original);
        merkleTree.buildTree(merkleTree.hashed);
        System.out.println("Merkle root = "+merkleTree.rootVal);
//        System.out.println("headList = "+merkleTree.headList);
    }
}
