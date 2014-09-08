package picross;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import java.util.*;
import java.util.regex.*;

import java.io.*;

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