package picross;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runners.*;

import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.security.Permission;

import picross.PicrossEditor;


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

    File file = new File("examples/e.txt");
    assertTrue(file != null && file.exists());

    BufferedReader br = null;
    try{
      br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null)
         assertFalse(line.contains("\n"));
      System.out.println(line);
    }catch(IOException e){
    }finally{
      try{
        br.close();
      }catch(IOException e){}
    }
  }

  @Test
  public void regexTest(){
    String assign = "^([WH])\\s*=\\s*(0|[1-9][0-9]*)$";
    String arrayStart = "^(LEFT|UP)\\s*:";
    String nums = "^(?:0|[1-9][0-9]*)(?:[ ](?:0|[1-9][0-9]*))*$";
    Pattern assignRegex = Pattern.compile(assign);
    Pattern arrayStartRegex = Pattern.compile(arrayStart);
    Pattern numSplitRegex = Pattern.compile(" ");

    // ==========================================

    // == OK ========
    for(int i=0; i<110; i+=4){
      assertTrue(Pattern.matches(assign, String.format("W = %d", i)));
      assertTrue(Pattern.matches(assign, String.format("H  =\t%d", i)));
    }

    // == NG ========
    assertFalse(Pattern.matches(assign, "W = 01"));
    assertFalse(Pattern.matches(assign, "H = -1"));
    assertFalse(Pattern.matches(assign, "R = 12"));
    assertFalse(Pattern.matches(assign, "LEFT:"));
    assertFalse(Pattern.matches(assign, "1 2 4"));

    // == OK ========
    assertTrue(Pattern.matches(arrayStart, "LEFT:"));
    assertTrue(Pattern.matches(arrayStart, "UP :"));
    assertTrue(Pattern.matches(arrayStart, "UP\t \t:"));

    // == NG ========
    assertFalse(Pattern.matches(arrayStart, "UPON\t \t:"));
    assertFalse(Pattern.matches(arrayStart, "RIGHT\t \t:"));

    // == OK ========
    assertTrue(Pattern.matches(nums, "1 4 6"));
    assertTrue(Pattern.matches(nums, "10 4 63"));
    assertTrue(Pattern.matches(nums, "1 7 4 28 3 9 1 1 1 2 2"));
    assertTrue(Pattern.matches(nums, "12"));

    // == NG ========
    assertFalse(Pattern.matches(nums, "12  4 5"));
    assertFalse(Pattern.matches(nums, "2,4,51"));

    // == OK ========
    Matcher m = assignRegex.matcher("W = 3");
    assertTrue(m.find());
    assertEquals("W", m.group(1));
    assertEquals("3", m.group(2));

    // == OK ========
    m = assignRegex.matcher("H=20");
    assertTrue(m.find());
    assertEquals("H", m.group(1));
    assertEquals("20", m.group(2));

    // == OK ========
    m = arrayStartRegex.matcher("LEFT:");
    assertTrue(m.find());
    assertEquals("LEFT", m.group(1));

    // == OK ========
    m = arrayStartRegex.matcher("UP\t\t:");
    assertTrue(m.find());
    assertEquals("UP", m.group(1));

    // == OK ========
    String[] strs = numSplitRegex.split("1 2 3 4");
    assertEquals("1", strs[0]);
    assertEquals("3", strs[2]);

    // == OK ========
    strs = numSplitRegex.split("11 2 32 4");
    List<Integer> numbers = new ArrayList<Integer>();
    for (String s : strs) {
      numbers.add(Integer.parseInt(s));
    }
    assertEquals(2, (int)numbers.get(1));
    assertEquals(32, (int)numbers.get(2));
  }

  @Test
  public void parseTest(){
    try{
      PicrossEditor p = new PicrossEditor(new String[]{"examples/e.txt"}, new String[]{"-r"});
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
}