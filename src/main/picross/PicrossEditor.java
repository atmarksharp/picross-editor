package picross;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.*;

public class PicrossEditor {
    protected String[] files;
    protected String[] opts;
    protected Picross picross;
    protected boolean showParseResult = false;
    protected JFrame window;
    protected JMenu menu;

    class Picross {
        public String title;
        public int width;
        public int height;
        public List<List<Integer>> left;
        public List<List<Integer>> up;

        public Picross(String _title, int _width, int _height, List<List<Integer>> _left, List<List<Integer>> _up){
            title = _title;
            width = _width;
            height = _height;
            left = _left;
            up = _up;
        }
    }

    protected void printerr(Object s){
        System.err.println(s);
    }

    protected void println(Object s){
        System.out.println(s);
    }

    protected void printParseError(String s, int n){
        printerr(String.format("Syntax Error (line: %d): %s", n, s));
    }

    protected void help(){
        println("");
        println("usage: picross-editor [options] [file]");
        println("options:");
        println("  -h | --help     show help");
        println("");
    }

    protected String listToString(List<List<Integer>> list){
        StringBuffer sb = new StringBuffer();
        String splitter;

        for (List<Integer> row : list) {
            splitter = "";
            for (Integer i : row) {
                sb.append(splitter);
                sb.append(String.valueOf(i));
                splitter = " ";
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    protected void printParseResult(Picross p){
        println(String.format("W = %d", p.width));
        println(String.format("H = %d", p.height));
        println("");
        println(String.format("LEFT:\n%s", listToString(p.left)));
        println(String.format("UP:\n%s", listToString(p.up)));
    }

    protected String removeComment(String _s){
        String s = _s;
        int p1 = s.indexOf("#");
        if(p1 > -1){
            s = s.substring(0, p1);
        }
        int p2 = s.indexOf("-");
        if(p2 > -1){
            s = s.substring(0, p2);
        }

        return s.trim();
    }

    protected void applyArray(String current, List<List<Integer>> list, Picross p){
        if(current != null && current.equals("LEFT")){
            p.left = list;
        }else if(current != null && current.equals("UP")){
            p.up = list;
        }
    }

    protected void printFileFormatError(String message, String filename){
        printerr(String.format("[%s] %s", filename, message));
    }

    protected boolean isValid(Picross p, String filename){
        boolean flag = true;

        if(p.width <= 0){
            printFileFormatError("W (width) should be \"> 0\"", filename);
            flag = false;
        }

        if(p.height <= 0){
            printFileFormatError("H (height) should be \"> 0\"", filename);
            flag = false;
        }

        if(p.left == null){
            printFileFormatError("Parameter \"LEFT\" is missing", filename);
            flag = false;
        }

        if(p.up == null){
            printFileFormatError("Parameter \"UP\" is missing", filename);
            flag = false;
        }

        if(p.width != p.up.size()){
            printFileFormatError("Size of W (width) and UP should be the same", filename);
            flag = false;
        }

        if(p.height != p.left.size()){
            printFileFormatError("Size of H (height) and LEFT should be the same", filename);
            flag = false;
        }

        return flag;
    }

    protected Picross parse(String filename){
        File file = new File(filename);

        if(!file.exists()){
            printerr(filename + " doesn't exist.");
            System.exit(1);
        }else if(file.isDirectory()){
            printerr(filename + " is directory.");
            System.exit(1);
        }

        FileReader fr = null;
        BufferedReader br = null;

        Picross p = new Picross(filename,-1,-1,null,null);

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String assign = "^([WH])\\s*=\\s*(0|[1-9][0-9]*)$";
            String arrayStart = "^(LEFT|UP)\\s*:";
            String nums = "^(?:0|[1-9][0-9]*)(?:[ ](?:0|[1-9][0-9]*))*$";
            Pattern assignRegex = Pattern.compile(assign);
            Pattern arrayStartRegex = Pattern.compile(arrayStart);
            Pattern numSplitRegex = Pattern.compile(" ");

            String current = null;
            List<List<Integer>> list = null;

            String line;
            int linenum = 0;

            while ((line = br.readLine()) != null) {
                ++linenum;
                line = line.trim();
                line = removeComment(line);

                // ex. "W = 10"
                if(Pattern.matches(assign, line)){
                    applyArray(current, list, p);
                    list = null;
                    current = null;

                    Matcher m = assignRegex.matcher(line);
                    m.find();

                    if(m.group(1).equals("W")){
                        p.width = Integer.parseInt(m.group(2));
                    }else if(m.group(1).equals("H")){
                        p.height = Integer.parseInt(m.group(2));
                    }else{
                        printParseError(String.format("Parameter %s does not exist", m.group(1)), linenum);
                    }

                // ex. "LEFT :"
                }else if(Pattern.matches(arrayStart, line)){
                    applyArray(current, list, p);
                    list = null;
                    current = null;

                    Matcher m = arrayStartRegex.matcher(line);
                    m.find();

                    if(m.group(1).equals("LEFT") || m.group(1).equals("UP")){
                        current = m.group(1);
                        list = new ArrayList<List<Integer>>();
                    }

                // ex. "1 2 4"
                }else if(Pattern.matches(nums, line)){
                    if(current == null){
                        printParseError("Syntax error", linenum);
                        System.exit(1);
                    }

                    String[] strs = numSplitRegex.split(line);
                    List<Integer> row = new ArrayList<Integer>();
                    for (String s : strs) {
                        row.add(Integer.parseInt(s));
                    }
                    list.add(row);

                // ex. "--- # this line is skipped "
                }else if(line.trim().equals("")){
                    /* skip */

                // invalid
                }else{
                    printParseError("Syntax error", linenum);
                    System.exit(1);
                }
            }

            applyArray(current, list, p);
            list = null;
            current = null;

        }catch(IOException e){
            System.err.println(e);
            System.exit(1);
        }finally{
            try {
                if(fr != null) fr.close();
                if(br != null) br.close();
            }catch(IOException e){
                System.err.println(e);
                System.exit(1);
            }
        }

        if(!isValid(p, filename)){
            System.exit(1);
        }

        return p;
    }

    protected void createWindow(){
        window = new JFrame("Picross Editor");
    }

    protected void initWindow(){
        if(window == null){
            createWindow();
        }
    }

    protected PicrossEditor(){} // for debug

    public PicrossEditor(String[] files, String[] opts){
        if(files.length == 0){
            help();
            System.exit(0);
        }

        if(opts.length > 0){
            if(opts[0].equals("-h") || opts[0].equals("--help")){
                help();
                System.exit(1);
            }else if(opts[0].equals("-r") || opts[0].equals("--parse-result")){
                showParseResult = true;
            }else{
                printerr("option " + opts[0] + " is invalid.");
                printerr("");
                help();
                System.exit(1);
            }
        }
        
        picross = parse(files[0]);

        if(showParseResult){
            printParseResult(picross);
            System.exit(0);
        }

        initWindow();
    }

    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<String>();
        ArrayList<String> opts = new ArrayList<String>();

        for (String arg : args) {
            if(arg.startsWith("-")){
                opts.add(arg);
            }else{
                files.add(arg);
            }
        }

        new PicrossEditor((String[])files.toArray(new String[]{}),(String[])opts.toArray(new String[]{}));
    }
}