import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import java.util.*;

import picross.util.*;

@SuppressWarnings("unchecked")
public class TupleTest {
  @Test
  public void Tuple1(){
    Tuple1<String> t = Tuple._("hello");
    assertEquals(1, t.length);
    assertEquals("hello", t._1);
  }
  
  @Test
  public void Tuple2(){
    Tuple2<Integer, String> t = Tuple._(1, "hello");
    assertEquals(2, t.length);
    assertEquals(1, (int)t._1);
    assertEquals("hello", t._2);
  }

  @Test
  public void Tuple3(){
    Tuple3<Integer, Double, String> t = Tuple._(1, 2.0, "hello");
    assertEquals(3, t.length);
    assertEquals(1, (int)t._1);
    assertEquals(2.0, (double)t._2, 0.01);
    assertEquals("hello", t._3);
  }

  @Test
  public void createTupleTest(){
    Tuple t = Tuple.createTuple(1, 2.0, "hello", 4.5F, new int[]{3});
    assertEquals(5, t.size());
    assertEquals(1, (int)t._(1, int.class));
    assertEquals(2.0, (double)t._(2, double.class), 0.01);
    assertEquals("hello", t._(3, String.class));
    assertEquals(4.5F, (float)t._(4, float.class), 0.01F);
    assertEquals(3, t._(5, int[].class)[0]);
  }

  @Test
  public void updateTest(){
    Tuple2<Integer,String> t = Tuple._(1,"hello");
    assertEquals(2, t.length);
    assertEquals(1, (int)t._1);
    assertEquals("hello", t._2);

    t._1 = 7;
    t._2 = "foo";
    assertEquals(2, t.length);
    assertEquals(7, (int)t._1);
    assertEquals("foo", t._2);
  }
}