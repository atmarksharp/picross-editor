package picross;

import java.util.*;
import java.util.regex.*;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.*;
import java.awt.event.*;

public class PicrossEditor {
    protected int pixelSize = 30;
    protected int imageUnitSize = 40;
    protected int boldLineThickness = 3;
    protected int normalLineThickness = 1;

    protected Border boldLine = new LineBorder(Color.black, boldLineThickness);
    protected Border haflLine = new LineBorder(Color.black, boldLineThickness/2);
    protected Border normalLine = new LineBorder(Color.black, normalLineThickness);

    protected String[] files;
    protected String[] opts;
    protected Picross picross;
    protected boolean showParseResult = false;
    protected boolean runningInJar = false;

    protected JFrame window;
    protected JMenuBar menubar;
    protected JPanel picrossPanel;
    protected JPanel leftColumn;
    protected JPanel upColumn;
    protected JPanel body;
    protected enum PixelType {FILL, CROSS, GUESS_NOFILL ,GUESS_FILL, GUESS_CROSS, CHECK}

    protected BufferedImage pixelImage;
    protected Map<Integer,BufferedImage> imageCache = new HashMap<Integer, BufferedImage>();

    protected PicrossListener picrossListener;

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

    class PicrossListener implements MouseListener, MouseMotionListener {
        public void mouseClicked(MouseEvent e){
            println("mouseClicked");
        }

        public void mouseEntered(MouseEvent e){
            println("mouseEntered");
        }

        public void mouseExited(MouseEvent e){
            println("mouseExited");
        }

        public void mousePressed(MouseEvent e){
            println("mousePressed");
        }

        public void mouseReleased(MouseEvent e){
            println("mouseReleased");
        }

        public void mouseDragged(MouseEvent e){
            println("mouseDragged");
        }

        public void mouseMoved(MouseEvent e){
            
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

    protected Color hsb(int h, int s, int b){
        return Color.getHSBColor(h/360.0F, s/100.0F, b/100.0F);
    }

    protected int maxSize(List<List<Integer>> list){
        List<Integer> sizes = new ArrayList<Integer>();

        for (int i=0; i<list.size(); i++) {
            List<Integer> nums = list.get(i);
            sizes.add(nums.size());
        }

        return Collections.max(sizes);
    }

    protected Point calcImagePosition(int n){
        return new Point(imageUnitSize*((n-1)%20),imageUnitSize*((n-1)/20));
    }

    protected BufferedImage convertToBuffered(Image img){
        BufferedImage retImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)retImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return retImage;
    }

    protected BufferedImage pixelImage(PixelType type){
        if(type == PixelType.FILL){
            return numberImage(101);
        }else if(type == PixelType.CROSS){
            return numberImage(102);
        }else if(type == PixelType.GUESS_NOFILL){
            return numberImage(103);
        }else if(type == PixelType.GUESS_FILL){
            return numberImage(104);
        }else if(type == PixelType.GUESS_CROSS){
            return numberImage(105);
        }else if(type == PixelType.CHECK){
            return numberImage(106);
        }
    }

    protected BufferedImage numberImage(int n){
        if(n > 100) return null;

        if(pixelImage == null){
            try{
                if(runningInJar){
                    pixelImage = ImageIO.read(getClass().getResource("./pixel_images.png"));
                }else{
                    pixelImage = ImageIO.read(new File("resource/pixel_images.png"));
                }
            }catch(Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }

        if(imageCache.get(n) == null){
            Point pt = calcImagePosition(n);
            BufferedImage img = pixelImage.getSubimage(pt.x, pt.y, imageUnitSize, imageUnitSize);
            BufferedImage scaled = convertToBuffered(img.getScaledInstance(pixelSize, pixelSize, Image.SCALE_SMOOTH));

            // Cashe an Image
            imageCache.put(n,scaled);

            return scaled;
        }else{
            // Use the Cashed Image
            return imageCache.get(n);
        }
    }

    protected JLabel leftNumberLabel(int index, int rowIndex, int max){
        List<Integer> row = picross.left.get(rowIndex);
        int len = row.size();
        int spaces = max - len;
        int id = index - spaces;

        if(id < 0){
            return new JLabel();
        }else{
            BufferedImage img = numberImage(row.get(id));
            if(img == null){
                return new JLabel(String.valueOf(row.get(id)));
            }else{
                return new JLabel(new ImageIcon(img));
            }
        }
    }

    protected JLabel upNumberLabel(int index, int columnIndex, int max){
        List<Integer> column = picross.up.get(columnIndex);
        int len = column.size();
        int spaces = max - len;
        int id = index - spaces;

        if(id < 0){
            return new JLabel();
        }else{
            BufferedImage img = numberImage(column.get(id));
            if(img == null){
                return new JLabel(String.valueOf(column.get(id)));
            }else{
                return new JLabel(new ImageIcon(img));
            }
        }
    }

    protected JPanel createPicrossPanel(boolean fileOpened){
        JPanel panel = new JPanel();

        // Calculate Parameters
        int leftColumnWidth = maxSize(picross.left) * pixelSize;
        int leftColumnHeight = picross.left.size() * pixelSize;
        int upColumnWidth = picross.up.size() * pixelSize;
        int upColumnHeight = maxSize(picross.up) * pixelSize;
        int bodyWidth = upColumnWidth;
        int bodyHeight = leftColumnHeight;
        int width = (leftColumnWidth + bodyWidth);
        int height = (upColumnHeight + bodyHeight);

        // Set Absolute Layout
        panel.setLayout(null);
        panel.setSize(width, height);

        if(!fileOpened){
            return panel;
        }

        // Initialize
        JPanel emptyColumn = new JPanel();
        leftColumn = new JPanel();
        upColumn = new JPanel();
        body = new JPanel();

        // Set Listeners
        if(picrossListener == null){
            picrossListener = new PicrossListener();
        }

        body.addMouseListener(picrossListener);
        body.addMouseMotionListener(picrossListener);


        // Set Bounds
        emptyColumn.setBounds(0, 0, (width - bodyWidth), (height - bodyHeight));
        leftColumn.setBounds(0, (height - bodyHeight), leftColumnWidth, leftColumnHeight);
        upColumn.setBounds((width - bodyWidth), 0, upColumnWidth, upColumnHeight);
        body.setBounds((width - bodyWidth), (height - bodyHeight), bodyWidth, bodyHeight);

        // Set Border
        emptyColumn.setBorder(null);
        leftColumn.setBorder(BorderFactory.createMatteBorder(boldLineThickness, boldLineThickness, boldLineThickness, 0, Color.black));
        upColumn.setBorder(BorderFactory.createMatteBorder(boldLineThickness, boldLineThickness, 0, boldLineThickness, Color.black));
        body.setBorder(boldLine);

        // Set Background
        emptyColumn.setBackground( null );
        leftColumn.setBackground( hsb(28, 14, 100) );
        upColumn.setBackground( hsb(28, 14, 100) );
        body.setBackground( hsb(28, 4, 100) );

        // Prepare for Next
        JPanel numRow;
        JPanel numBox;

        // Set Number Rows on the Left Column
        leftColumn.setLayout(new GridLayout(leftColumnHeight/pixelSize, 1));
        for(int i=0; i<leftColumnHeight/pixelSize; i++) {
            numRow = new JPanel();
            numRow.setBackground(null);
            numRow.setBorder(normalLine);
            numRow.setLayout(new GridLayout(1, leftColumnWidth/pixelSize));

            for (int j=0; j<leftColumnWidth/pixelSize; j++) {
                numBox = new JPanel();
                FlowLayout layout = new FlowLayout();
                layout.setVgap(0);
                layout.setHgap(0);
                numBox.setLayout(layout);
                numBox.setBackground(null);
                numBox.setBorder(BorderFactory.createMatteBorder(0,normalLineThickness,0,0,Color.black));
                numBox.add(leftNumberLabel(j,i,leftColumnWidth/pixelSize));
                numRow.add(numBox);
            }

            leftColumn.add(numRow); 
        }

        // Set Number Rows on the Up Column
        upColumn.setLayout(new GridLayout(1, upColumnWidth/pixelSize));
        for(int i=0; i<upColumnWidth/pixelSize; i++) {
            numRow = new JPanel();
            numRow.setBackground(null);
            numRow.setBorder(new LineBorder(Color.black, normalLineThickness));
            numRow.setLayout(new GridLayout(upColumnHeight/pixelSize, 1));

            for (int j=0; j<upColumnHeight/pixelSize; j++) {
                numBox = new JPanel();
                FlowLayout layout = new FlowLayout();
                layout.setVgap(0);
                layout.setHgap(0);
                numBox.setLayout(layout);
                numBox.setBackground(null);
                numBox.setBorder(BorderFactory.createMatteBorder(normalLineThickness,0,0,0,Color.black));
                numBox.add(upNumberLabel(j,i,upColumnHeight/pixelSize));
                numRow.add(numBox);
            }

            upColumn.add(numRow); 
        }

        // Set Cells on the Body
        body.setLayout(new GridLayout(bodyHeight/pixelSize, bodyWidth/pixelSize));
        for(int i=0; i<(bodyWidth*bodyHeight)/(pixelSize*pixelSize); i++) {
            numBox = new JPanel();
            numBox.setBackground(null);
            numBox.setBorder(normalLine);
            body.add(numBox);
        }

        // Add Children
        panel.add(emptyColumn);
        panel.add(leftColumn);
        panel.add(upColumn);
        panel.add(body);

        return panel;
    }

    protected JMenuBar createMenuBar(boolean fileOpened){
        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem fileNew = new JMenuItem("New");
        JMenuItem fileOpen = new JMenuItem("Open");
        JMenuItem quit = new JMenuItem("Quit");

        JMenu edit = new JMenu("Edit");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");

        JMenu tool = new JMenu("Tool");
        JMenuItem guessStart = new JMenuItem("GuessStart");
        JMenuItem guessEnd = new JMenuItem("GuessEnd");

        {
            file.add(fileNew);
            file.add(fileOpen);
            if(fileOpened){
                JMenuItem fileSave = new JMenuItem("Save");
                file.addSeparator();
                file.add(fileSave);
                file.addSeparator();
            }
            file.add(quit);
        }
        {
            if(fileOpened){
                edit.add(undo);
                edit.add(redo);

                tool.add(guessStart);
                tool.add(guessEnd);
            }
        }

        if(fileOpened){
            menubar.add(file);
            menubar.add(edit);
            menubar.add(tool);
        }else{
            menubar.add(file);
        }

        return menubar;
    }

    protected JFrame createWindow(String filename){
        JFrame window = new JFrame("Picross Editor");

        menubar = createMenuBar(filename != null);
        window.setJMenuBar(menubar);
        picrossPanel = createPicrossPanel(filename != null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add("Center",picrossPanel);

        window.setSize(new Dimension(600,600));
        window.setLayout(new GridLayout(1, 1));
        window.add(mainPanel);

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception e){
            printerr("Could not get the system look-and-feel");
            // go next
        }

        return window;
    }

    protected void initWindow(String filename){
        window = createWindow(filename);
        window.setVisible(true);
    }

    protected void checkIfRunningInJar(){
        String s = PicrossEditor.class.getResource("PicrossEditor.class").toString();
        runningInJar = s.startsWith("jar");
    }

    protected PicrossEditor(){} // for debug

    public PicrossEditor(String[] files, String[] opts){
        checkIfRunningInJar();

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

        initWindow(picross != null? files[0] : null);
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