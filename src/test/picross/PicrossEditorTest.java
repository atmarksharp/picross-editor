package picross;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import java.util.*;
import java.util.regex.*;

import java.awt.Point;

import java.io.*;

import java.security.Permission;

import picross.PicrossEditor;
import picross.util.*;


// =========================

public class PicrossEditorTest {

    // === Fields ====================================

    PrintStream systemOut = System.out;
    PrintStream systemErr = System.err;
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    protected static class ExitException extends SecurityException {
        public final int status;
        public ExitException(int status) {
            super("");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager{
        @Override
        public void checkPermission(Permission perm) {
        // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) {
        // allow anything.
        }
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    private class PicrossEditorMock extends PicrossEditor {
        public PicrossEditorMock(){
            super();
        }
    }

    private void println(Object s){
        systemOut.println(s);
    }

    // === Prepare ====================================

    @Before
    public void setUpStreams() {
        System.setSecurityManager(new NoExitSecurityManager());
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setSecurityManager(null);
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(systemOut);
        System.setErr(systemErr);
    }

    // === Tests ====================================

    @Test
    public void noParameterTest(){
        try{
            PicrossEditor p = new PicrossEditor(new String[]{}, new String[]{});
        }catch(ExitException e){
            assertEquals("\nusage", outContent.toString().substring(0,6));
        }
    }

    @Test
    public void helpTest1(){
        try{
            PicrossEditor p = new PicrossEditor(new String[]{"example/e.txt"}, new String[]{"-h"});
        }catch(ExitException e){
            assertEquals("\nusage", outContent.toString().substring(0,6));
        }
    }

    @Test
    public void helpTest2(){
        try{
            PicrossEditor p = new PicrossEditor(new String[]{"example/e.txt"}, new String[]{"--help"});
        }catch(ExitException e){
            assertEquals("\nusage", outContent.toString().substring(0,6));
        }
    }

    @Test
    public void invalidOptionTest(){
        try{
            PicrossEditor p = new PicrossEditor(new String[]{"example/e.txt"}, new String[]{"-foo"});
        }catch(ExitException e){
            assertEquals("option -foo", errContent.toString().substring(0,11));
        }
    }

    @Test
    public void commentTest(){
        PicrossEditorMock p = new PicrossEditorMock();

        assertEquals("", p.removeComment(" # this is comment ").trim());
        assertEquals("", p.removeComment(" - this is comment\t").trim());
        assertEquals("", p.removeComment("----").trim());
        assertEquals("", p.removeComment(" # --- foo\t").trim());
        assertEquals("", p.removeComment(" - # --- foo\t").trim());
        assertEquals("", p.removeComment(" ## -#_ - --- # -- -# $ % hf\t").trim());
        assertEquals("", p.removeComment("# sizes (W: width, H: height)").trim());
    }

    @Test
    public void noNewLineTest() throws IOException {
        PicrossEditorMock p = new PicrossEditorMock();

        File file = new File("examples/e-8x8.txt");
        assertTrue(file != null && file.exists());

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
                 assertFalse(line.contains("\n"));
        }catch(IOException e){
        }finally{
            try{
                br.close();
            }catch(IOException e){}
        }
    }

    @Test
    public void parseTest(){
        try{
            PicrossEditor p = new PicrossEditor(new String[]{"examples/e-8x8.txt"}, new String[]{"-r"});
        }catch(ExitException e){
            String s = 
                "W = 8\n" +
                "H = 8\n" +
                "\n" +
                "LEFT:\n" +
                "4\n" +
                "2 2\n" +
                "2 2\n" +
                "8\n" +
                "2\n" +
                "2 2\n" +
                "2 2\n" +
                "4\n" +
                "\n" +
                "UP:\n" +
                "4\n" +
                "6\n" +
                "2 1 2\n" +
                "1 1 1\n" +
                "1 1 1\n" +
                "2 1 2\n" +
                "3 2\n" + 
                "2 1\n" +
                "\n";

            String[] expected = Pattern.compile("\n").split(s);
            String[] result = Pattern.compile("\n").split(outContent.toString());

            assertEquals(expected.length, result.length);

            for(int i=0; i<result.length; i++){
                assertEquals(expected[i], result[i]);
            }
        }
    }

    @Test
    public void calcImagePositionTest(){
        PicrossEditorMock p = new PicrossEditorMock();
        Point pt;

        final int unit = p.imageUnitSize;

        pt = p.calcImagePosition(1);
        assertEquals(0, pt.x);
        assertEquals(0, pt.y);

        pt = p.calcImagePosition(2);
        assertEquals(unit*1, pt.x);
        assertEquals(0, pt.y);

        pt = p.calcImagePosition(20);
        assertEquals(unit*19, pt.x);
        assertEquals(0, pt.y);

        pt = p.calcImagePosition(21);
        assertEquals(0, pt.x);
        assertEquals(unit, pt.y);

        pt = p.calcImagePosition(42);
        assertEquals(unit, pt.x);
        assertEquals(unit*2, pt.y);
    }

    @Test
    public void detectPositionTest(){
        PicrossEditorMock p = new PicrossEditorMock();
        Point pt;

        int unit = p.pixelSize;

        pt = p.detectPosition(unit/2, unit/2);
        assertEquals(0, pt.x);
        assertEquals(0, pt.y);

        pt = p.detectPosition(unit/2 + unit*3, unit/2 + unit*2);
        assertEquals(3, pt.x);
        assertEquals(2, pt.y);
    }

    @Test
    public void calcIndexTest(){
        PicrossEditorMock p = new PicrossEditorMock();
        p.picross = p.createPicross("test",4,4,null,null);

        assertEquals(0, (Object)p.calcIndex(0,0));
        assertEquals(1, (Object)p.calcIndex(1,0));
        assertEquals(null, (Object)p.calcIndex(4,0));
        assertEquals(4, (Object)p.calcIndex(0,1));
    }

    @Test
    public void parseLineTest(){
        PicrossEditorMock p = new PicrossEditorMock();
        List<List<Integer>> list = new ArrayList<List<Integer>>();
        List<Integer> l = new ArrayList<Integer>();
        l.add(1);
        list.add(l);

        p.picross = p.createPicross("test",1,1,list,list);
        // 1x1:
        // ■

        p.progress = p.createProgress(p.picross);
        p.progress.pixels = new PixelType[]{PixelType.FILL};

        assertEquals(PixelType.FILL, p.progress.getPixel(0,0));
        assertEquals(PixelType.FILL, p.progress.getVLine(0)[0]);
        assertEquals(1, p.progress.parseLine(p.progress.getVLine(0))[0]);
    }

    @Test
    public void parseLineTest2(){
        PicrossEditorMock p = new PicrossEditorMock();

        List<Integer> l1 = Arrays.asList(1);
        List<Integer> l2 = Arrays.asList(0);
        List<List<Integer>> list = Arrays.asList(l1,l2);

        PixelType fill = PixelType.FILL;
        PixelType nofill = PixelType.NOFILL;

        p.picross = p.createPicross("test2",2,2,list,list);
        // 2x2:
        // ■╳
        // ╳╳

        p.progress = p.createProgress(p.picross);
        p.progress.pixels = new PixelType[]{fill, nofill, nofill, nofill};

        assertEquals(2, p.picross.width);
        assertEquals(2, p.picross.height);

        assertEquals(fill, p.progress.getPixel(0,0));
        assertEquals(nofill, p.progress.getPixel(1,0));
        assertEquals(nofill, p.progress.getPixel(0,1));
        assertEquals(nofill, p.progress.getPixel(1,1));

        assertEquals(2, p.progress.getVLine(0).length);
        assertEquals(2, p.progress.getVLine(1).length);

        assertEquals(fill, p.progress.getVLine(0)[0]);
        assertEquals(nofill, p.progress.getVLine(0)[1]);

        assertEquals(nofill, p.progress.getVLine(1)[0]);
        assertEquals(nofill, p.progress.getVLine(1)[1]);

        assertEquals(1, p.progress.parseLine(p.progress.getVLine(0)).length);
        assertEquals(1, p.progress.parseLine(p.progress.getVLine(1)).length);

        assertEquals(1, p.progress.parseLine(p.progress.getVLine(0))[0]);
        assertEquals(0, p.progress.parseLine(p.progress.getVLine(1))[0]);

        assertEquals(true, p.progress.checkFinished());
    }

    @Test
    public void shrinkLineTest(){
        PicrossEditorMock p = new PicrossEditorMock();
        p.progress = p.createProgress(null, true);

        PixelType fill = PixelType.FILL;
        PixelType nofill = PixelType.NOFILL;

        PixelType[] list =
            new PixelType[]{nofill,nofill,fill,fill,fill,nofill};

        List<Tuple2<PixelType,Integer>> shrinked = p.progress.shrink(list);

        assertEquals(PixelType.NOFILL, shrinked.get(0)._1);
        assertEquals(2, (int)shrinked.get(0)._2);
        assertEquals(PixelType.FILL, shrinked.get(1)._1);
        assertEquals(3, (int)shrinked.get(1)._2);
        assertEquals(PixelType.NOFILL, shrinked.get(2)._1);
        assertEquals(1, (int)shrinked.get(2)._2);
    }

    @Test
    public void checkNumbersTest(){
        PicrossEditorMock p = new PicrossEditorMock();

        List<Integer> l1 = Arrays.asList(3,2,3);
        List<List<Integer>> list1 = Arrays.asList(l1);

        List<Integer> f = Arrays.asList(1);
        List<Integer> n = Arrays.asList(0);
        List<List<Integer>> list2 = Arrays.asList(f,f,f,n,n,f,f,n,f,f,f);

        PixelType fill = PixelType.FILL;
        PixelType nofill = PixelType.NOFILL;

        p.picross = p.createPicross("checkNumbers",1,11,list1,list2);
        // 1x11:
        // ■■■■╳╳╳■■╳■■■

        p.progress = p.createProgress(p.picross);
        p.progress.pixels = new PixelType[]{fill, nofill, nofill, nofill};

        PixelType[] list =
            new PixelType[]{nofill,nofill,fill,fill,fill,nofill};

        List<Tuple2<PixelType,Integer>> shrinked = p.progress.shrink(list);

        assertEquals(PixelType.NOFILL, shrinked.get(0)._1);
        assertEquals(2, (int)shrinked.get(0)._2);
        assertEquals(PixelType.FILL, shrinked.get(1)._1);
        assertEquals(3, (int)shrinked.get(1)._2);
        assertEquals(PixelType.NOFILL, shrinked.get(2)._1);
        assertEquals(1, (int)shrinked.get(2)._2);
    }
}