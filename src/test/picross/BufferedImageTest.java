import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;

import javax.imageio.ImageIO;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;

import java.io.*;

import java.awt.image.BufferedImage;

public class BufferedImageTest {
  public String sh(String s){
    System.out.println("\t[shell] "+s);
    try{
      Process p = Runtime.getRuntime().exec(s);
      InputStream is = p.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) sb.append(line);
      br.close();
      return sb.toString();
    }catch(IOException e){
      System.out.println(e);
      return null;
    }
  }

  public String tess(BufferedImage img, String filename, int mode){
    try{
      String path = "/tmp/"+filename+".png";
      File file = new File(path);
      ImageIO.write(img, "png", file);

      sh(String.format("tesseract %s /tmp/%s.report -psm %d", path, filename, mode));
      String result =  sh("cat /tmp/"+filename+".report.txt");
      // sh("rm -rf "+path+"; rm -rf "+filename+".report.txt");
      return result;
    }catch(IOException e){
      System.out.println(e);
      return null;
    }
  }

  @Test
  public void imageReadTest() throws IOException {
    BufferedImage img = ImageIO.read(new File("resource/pixel_images.png"));
    String result = tess(img,"read",1).replaceAll(" ","");
    assertEquals("12345678910", result.substring(0,11));
  }

  @Test
  public void imageCropTest() throws IOException {
    BufferedImage img = ImageIO.read(new File("resource/pixel_images.png")).getSubimage(0,0,40,40);
    String result = tess(img,"crop",10);
    assertEquals("1", result);
  }

  @Test
  public void imageDrawTest() throws IOException {
    BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)img.createGraphics();
    g.setFont(new Font("Sans Serif", Font.BOLD, 24));
    g.setColor(Color.red);
    g.drawString("Hello World",20,70);
    g.dispose();

    String result = tess(img,"draw",7);
    assertEquals("Hello World", result);
  }
}