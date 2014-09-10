package picross.util;

import java.util.*;
import picross.util.ArrayUtil;

public class ArrayUtil {
  public static <T> String join(Iterable<T> list, String sp){
    StringBuffer sb = new StringBuffer();
    String splitter;

    splitter = "";
    for (T obj : list) {
        sb.append(splitter);
        sb.append(String.valueOf(obj));
        splitter = sp;
    }

    return sb.toString();
  }

  public static <T> String join(T[] arr, String sp){
    StringBuffer sb = new StringBuffer();
    String splitter;

    splitter = "";
    for (T obj : arr) {
        sb.append(splitter);
        sb.append(String.valueOf(obj));
        splitter = sp;
    }

    return sb.toString();
  }

  public static <T> String join(Enumeration<T> e ,String sp){
    StringBuffer sb = new StringBuffer();
    String splitter;

    splitter = "";
    while(e.hasMoreElements()){
      sb.append(splitter);
      sb.append(e.nextElement().toString());
      splitter = sp;
    }

    return sb.toString();
  }

  public static short[] toPrimitives(Short[] v){
      short[] r=new short[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].shortValue();return r;
  }
  public static char[] toPrimitives(Character[] v){
      char[] r=new char[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].charValue();return r;
  }
  public static byte[] toPrimitives(Byte[] v){
      byte[] r=new byte[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].byteValue();return r;
  }
  public static boolean[] toPrimitives(Boolean[] v){
      boolean[] r=new boolean[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].booleanValue();return r;
  }
  public static long[] toPrimitives(Long[] v){
      long[] r=new long[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].longValue();return r;
  }
  public static float[] toPrimitives(Float[] v){
      float[] r=new float[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].floatValue();return r;
  }
  public static double[] toPrimitives(Double[] v){
      double[] r=new double[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].doubleValue();return r;
  }
  public static int[] toPrimitives(Integer[] v){
      int[] r=new int[v.length];for(int i=0;i<r.length;i++)r[i]=v[i].intValue();return r;
  }
}