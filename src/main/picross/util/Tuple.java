package picross.util;

import java.util.*;

@SuppressWarnings("unchecked")
public class Tuple {
  private List<Object> list;

  private Tuple(Object[] args){
    list = new ArrayList<Object>();
    for (Object arg : args) {
      list.add(arg);
    }
  }

  public int size(){
    return list.size();
  }

  public Object _(int n){
    return list.get(n-1);
  }

  public <T> T _(int n, Class<T> clazz){
    return (T)list.get(n-1);
  }

  public static Tuple1 _(Object arg1){
    return new Tuple1(arg1);
  }

  public static Tuple2 _(Object arg1, Object arg2){
    return new Tuple2(arg1, arg2);
  }

  public static Tuple3 _(Object arg1, Object arg2, Object arg3){
    return new Tuple3(arg1, arg2, arg3);
  }

  public static Tuple createTuple(Object... args){
    return new Tuple(args);
  }
}