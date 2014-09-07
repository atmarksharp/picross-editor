package picross;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runners.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.security.Permission;

import picross.PicrossEditor;

public class PicrossEditorTest {

  // === Fields ====================================

  PrintStream systemOut = System.out;
  PrintStream systemErr = System.err;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

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
  public void showHelpTest1(){
    try{
      PicrossEditor p = new PicrossEditor(new String[]{"example/e.txt"}, new String[]{"-h"});
    }catch(ExitException e){
      assertEquals("\nusage", outContent.toString().substring(0,6));
    }
  }

  @Test
  public void showHelpTest2(){
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
}