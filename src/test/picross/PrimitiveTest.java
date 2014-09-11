import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import java.util.*;
import java.util.regex.*;

public class PrimitiveTest {
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

  enum Person{TOM,EMILY};

  @Test
  public void enumCompareTest(){
    assertTrue(Person.TOM == Person.TOM);
    assertTrue(Person.TOM != Person.EMILY);
  }

  @Test
  public void listCastTest(){
    List l;
    {
        l = Arrays.asList(1,2,3);
    }
    {
        assertEquals(2, l.get(1));
    }
  }

  @Test
  public void listCreationTest(){
    List<Integer> l = new ArrayList<Integer>();
    l.add(1);
    l.add(2);
    l.add(3);

    List<Integer> list = Arrays.asList(0,0,0,0,0);
    Collections.copy(list,l);
    assertEquals(1, (int)list.get(0));
    assertEquals(2, (int)list.get(1));
    assertEquals(3, (int)list.get(2));
    assertEquals(0, (int)list.get(3));
    assertEquals(0, (int)list.get(4));
  }
}