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
    List<Integer> nums = Arrays.asList(1,2,3);
    String s = ArrayUtil.join(nums,"/");
    assertEquals("1/2/3", s);
  }

  @Test
  public void unitsTest(){
    int index = 6;
    int insertValue = 0;
    List<Integer> l = ArrayUtil.units(insertValue, index+1);

    assertEquals(7, l.size());
    assertEquals(0, (int)l.get(0));
    assertEquals(0, (int)l.get(1));
    assertEquals(0, (int)l.get(2));
    assertEquals(0, (int)l.get(3));
    assertEquals(0, (int)l.get(4));
    assertEquals(0, (int)l.get(5));
    assertEquals(0, (int)l.get(6));
  }

  @Test
  public void fillUntilTest(){
    List<Integer> list = Arrays.asList(1,2,3);
    List<Integer> nums = ArrayUtil.fillUntil(6, 0, list);

    assertEquals(7, nums.size());
    assertEquals(1, (int)nums.get(0));
    assertEquals(2, (int)nums.get(1));
    assertEquals(3, (int)nums.get(2));
    assertEquals(0, (int)nums.get(3));
    assertEquals(0, (int)nums.get(4));
    assertEquals(0, (int)nums.get(5));
    assertEquals(0, (int)nums.get(6));
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