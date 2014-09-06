import java.util.ArrayList;
import java.util.regex.*;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class PicrossEditor {
    private String[] files;
    private String[] opts;
    private Picross picross;

    class Picross {
        public int width;
        public int height;
        public int[][] left;
        public int[][] up;

        public Picross(int _width, int _height, int[][] _left, int[][] _up){
            width = _width;
            height = _height;
            left = _left;
            up = _up;
        }
    }

    private void printerr(String s){
        System.err.println(s);
    }

    private void println(String s){
        System.out.println(s);
    }

    private void printParseError(String s, int n){
        printerr(String.format("Syntax Error (line: %d): %s", n, s));
    }

    private void help(){
        println("usage: picross-editor [options] [file]");
        println("options:");
        println("  -h | --help     show help");
    }

    private String removeComment(String s){
        String ret = s;
        int p1 = s.indexOf("#");
        if(p1 > -1){
            int p = p1 == 0 ? 0 : p1 -1;
            ret = s.substring(0,p);
        }
        int p2 = s.indexOf("-");
        if(p2 > -1){
            int p = p1 == 0 ? 0 : p1 -1;
            ret = s.substring(0,p);
        }

        return ret;
    }

    private Picross parse(String filename){
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

        int width = null;
        int height = null;
        int[][] left = null;
        int[][] up = null;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String assign = "^([WH])\\s+=\\s+(0|[1-9][0-9]+)$";
            String arrayStart = "^(LEFT|UP)\\s*:";
            String nums = "^(?:0|[1-9][0-9]+)(?:[ ](?:0|[1-9][0-9]+))*$"
            Pattern assignRegex = Pattern.compile("^([WH])\\s+=\\s+(0|[1-9][0-9]+)$");
            Pattern arrayStartRegex = Pattern.compile("^(LEFT|UP)\\s*:");
            Pattern numSplitRegex = Pattern.compile(" ");

            String current = null;
            ArrayList<ArrayList<Integer>> list = null

            String line;
            int linenum = -1;
            while ((line = br.readLine()) != null) {
                ++linenum;
                line = line.trim();
                line = removeComment(line);
                if(Pattern.matches(assign, line)){
                    Matcher m = assignRegex.matcher(line);
                    if(m.group(0).equals("W")){
                        width = Integer.parseInt(m.group(1));
                    }else if(m.group(0).equals("H")){
                        height = Integer.parseInt(m.group(1));
                    }else{
                        printParseError(String.format("Parameter %s does not exist", m.group(1)), linenum)
                    }
                }else if(Pattern.matches(arrayStart, line)){
                    Matcher m = arrayStart.matcher(line);
                    if(m.group(0).equals("LEFT") || m.group(0).equals("UP")){
                        current = m.group(0);
                        list = new ArrayList<ArrayList<Integer>>();
                    }
                }else if(Pattern.matches(nums, line)){
                    String[] strs = numSplitRegex.split(line);
                    ArrayList<Integer> arr = new ArrayList<Integer>();
                    for (String s : strs) {
                        arr.add(Integer.parseInt(s));
                    }
                    list.add(arr);
                }else if(line.equals("")){
                    if(current != null){
                        int[][] nums = new int[][](list.size());
                        for (int i = 0; i < list.size(); i++) {
                            ArrayList<Integer> l = list.get(i)
                            int[] arr = new int[](l.size());
                            for (int n = 0; n < l.size(); n++) {
                                arr[n] = l.get(n); 
                            }
                            nums[i] = arr;
                        }

                        if(current.equals("LEFT")){
                            left = nums;
                        }else if(current.equals("UP")){
                            up = nums;
                        }

                        list = null;
                        current = null;
                    }
                }
            }

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

        return new Picross(width, height, left, up);
    }

    public PicrossEditor(String[] files, String[] opts){
        if(files.length == 0){
            help();
            System.exit(0);
        }else if(opts.length > 1){
            if(opts[0] == "-h" || opts[0] == "--help"){
                help();
                System.exit(0);
            }else{
                printerr("option " + opts[0] + " is invalid.");
                printerr("");
                help();
                System.exit(1);
            }
        }

        picross = parse(files[0]);
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