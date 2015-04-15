import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.sql.Timestamp;

public class MasterState implements Serializable {
    public HashMap<String, Commit> branches;
    public int currentUniqueID;
    public String currentBranch;
    public StagingArea stage;

    public MasterState() {
        branches = new HashMap<String, Commit>();
        Commit c = new Commit();
        c.previous = null;
        c.files = new HashSet();
        c.message = "initial commit";
        c.created = new Timestamp((new GregorianCalendar()).getTime().getTime());
        c.id = 0;
        branches.put("master", c);
        currentUniqueID = 1;
        currentBranch = "master";
        stage = new StagingArea();
    }

    public void save(){
        try {
            FileOutputStream fileOut = new FileOutputStream("./.gitlet/state");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        }
        catch (IOException io) {
            System.out.println("something went wrong");
            System.out.println(io);
        }
    }

    public void commit(String message){
        Commit c = new Commit();
        Commit old = branches.get(currentBranch);
        c.previous = old;
        c.message = message;
        c.created = new Timestamp((new GregorianCalendar()).getTime().getTime());
        c.id = currentUniqueID;
        HashSet fileSet = new HashSet();
        if (old != null) {
            fileSet.addAll(old.files);
        }
        fileSet.addAll(stage.stagedFiles);
        fileSet.removeAll(stage.removedFiles);
        c.files = fileSet;
        branches.put(currentBranch, c);

        File newDir = new File(".gitlet/"+currentUniqueID);
        newDir.mkdir();
        for (String name : stage.stagedFiles) {
            try {
                Path f = (new File(name)).toPath();
                Files.copy(f, newDir.toPath().resolve(f.getFileName()));
            }
            catch (IOException io) {
                System.out.println(io);
            }
        }
        currentUniqueID++;
        stage = new StagingArea();
    }


    public void log() {
        Commit c = branches.get(currentBranch);
        while (c != null) {
            System.out.println("====");
            System.out.println("Commit " + c.id + ".");
            System.out.println(c.created);
            System.out.println(c.message);
            System.out.println();
            c = c.previous;
        }
    }
    public void globalLog() {
        for (Commit c : branches.values()) {
            while (c != null) {
                System.out.println("====");
                System.out.println("Commit " + c.id + ".");
                System.out.println(c.created);
                System.out.println(c.message);
                System.out.println();
                c = c.previous;
            }
        }
    }

    public void find(String message) {
        Commit c = branches.get(currentBranch);
        while (c != null) {
            if (c.message.equals(message)) {
                System.out.println(c.id);
            }
            c = c.previous;
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        System.out.println("*" + currentBranch);
        Set<String> rest = branches.keySet();
        rest.remove(currentBranch);
        for (String s : rest) {
            System.out.println(s);
        }

        System.out.println("");
        System.out.println("=== Staged Files ===");
        for (String name : stage.stagedFiles) {
            System.out.println(name);
        }

        System.out.println("");
        System.out.println("=== Files Marked for Removal ===");
        for (String name : stage.removedFiles) {
            System.out.println(name);
        }
    }

    public void checkoutFile(String name){
        Commit c = branches.get(currentBranch);
        //might not exist in that dir
        if (!branches.get(currentBranch).files.contains(name)){
            return;
        }
        
        File f = new File("./.gitlet/" + c.id + "/" + name);
        while (!f.exists()) {
            c = c.previous;
            f = new File("./.gitlet/" + c.id + "/" + name);
        }
        Path p = f.toPath();
        Path d = (new File(".")).toPath();
        try {
            Files.copy(p,d.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException io) {
            System.out.println(io);
        }
    }

    public void checkoutBranch(String name){
        branches.get(name).restore();
    }

    public void checkoutSpecific(int commitID, String name){
        Commit c = branches.get(currentBranch);
        while (c != null){
            if (c.id == commitID){
                if (!c.files.contains(name)){
                    return;
                }
                File f = new File("./.gitlet/" + c.id + "/" + name);
                while (!f.exists()) {
                    c = c.previous;
                    f = new File("./.gitlet/" + c.id + "/" + name);
                }
                Path p = f.toPath();
                Path d = (new File(".")).toPath();
                try {
                    Files.copy(p,d.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException io) {
                    System.out.println(io);
                }
                return;
            }
            c = c.previous;
        }
    }

    public void branch(String name){
        branches.put(name, branches.get(currentBranch));
    }

    public void removeBranch(String name){
        branches.remove(name);
    }

    public void reset(int commitID){
        Commit c = branches.get(currentBranch);
        while (c != null){
            if (c.id == commitID){
                c.restore();
                return;
            }
            c = c.previous;
        }
    }

    public void merge(String branchName){
        //read spec carefully for this one!
        //don't forget to add to staging area
    }
    public void rebase(String branchName){}
    public void advancedRebase(String branchName){}
}
