package picross.util;

public class Tuple2<T1,T2> {
  public T1 _1;
  public T2 _2;
  public int length = 2;

  public Tuple2(T1 arg1, T2 arg2){
    _1 = arg1;
    _2 = arg2;
  }
}