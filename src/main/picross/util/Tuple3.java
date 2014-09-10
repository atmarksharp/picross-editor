package picross.util;

public class Tuple3<T1,T2,T3> {
  public T1 _1;
  public T2 _2;
  public T3 _3;
  public int length = 3;

  public Tuple3(T1 arg1, T2 arg2, T3 arg3){
    _1 = arg1;
    _2 = arg2;
    _3 = arg3;
  }
}