import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import java.util.*;

import picross.util.*;

public class ArrayUtilTest {
  @Test
  public void Integer_joinTest(){
    Integer[] nums = new Integer[]{1,2,3,4};
    String s = ArrayUtil.join(nums,",");
    assertEquals("1,2,3,4", s);
  }

  @Test
  public void String_joinTest(){
    String[] list = new String[]{"a","b","c"};
    String s = ArrayUtil.join(list," ");
    assertEquals("a b c", s);
  }

  @Test
  public void List_Integer_joinTest(){
    List<Integer> nums = new ArrayList<Integer>();
    nums.add(1);
    nums.add(2);
    nums.add(3);

    String s = ArrayUtil.join(nums,"/");
    assertEquals("1/2/3", s);
  }

  @Test
  public void Integer_toPrimitivesTest(){
    Integer[] m = new Integer[]{1,2};
    int[] p = ArrayUtil.toPrimitives(m);
    assertEquals(1, p[0]);
    assertEquals(2, p[1]);
  }

  @Test
  public void Double_toPrimitivesTest(){
    Double[] m = new Double[]{1.1,2.2};
    double[] p = ArrayUtil.toPrimitives(m);
    assertEquals(1.1, p[0], 0.001);
    assertEquals(2.2, p[1], 0.001);
  }

  @Test
  public void Float_toPrimitivesTest(){
    Float[] m = new Float[]{1.1F,2.2F};
    float[] p = ArrayUtil.toPrimitives(m);
    assertEquals(1.1F, p[0], 0.001);
    assertEquals(2.2F, p[1], 0.001);
  }

  @Test
  public void Short_toPrimitivesTest(){
    Short[] m = new Short[]{1,2};
    short[] p = ArrayUtil.toPrimitives(m);
    assertEquals(1, p[0]);
    assertEquals(2, p[1]);
  }

  @Test
  public void Character_toPrimitivesTest(){
    Character[] m = new Character[]{'a','b'};
    char[] p = ArrayUtil.toPrimitives(m);
    assertEquals('a', p[0]);
    assertEquals('b', p[1]);
  }

  @Test
  public void Byte_toPrimitivesTest(){
    Byte[] m = new Byte[]{1,2};
    byte[] p = ArrayUtil.toPrimitives(m);
    assertEquals(1, p[0]);
    assertEquals(2, p[1]);
  }

  @Test
  public void Boolean_toPrimitivesTest(){
    Boolean[] m = new Boolean[]{true,false};
    boolean[] p = ArrayUtil.toPrimitives(m);
    assertEquals(true, p[0]);
    assertEquals(false, p[1]);
  }
}