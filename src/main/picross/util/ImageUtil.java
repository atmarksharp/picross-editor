package picross.util;

import picross.PicrossEditor;
import picross.util.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.Image;
import java.awt.Point;

public class ImageUtil {
  protected static PicrossEditor picrossEditor;
  protected static BufferedImage pixelImage;
  protected static Map<Integer,BufferedImage> imageCache = new HashMap<Integer, BufferedImage>();
  public static int imageUnitSize;
  public static int pixelSize;
  public static boolean runningInJar;

  public static void init(PicrossEditor p){
    picrossEditor = p;

    imageUnitSize = p.imageUnitSize;
    pixelSize = p.pixelSize;

    checkIfRunningInJar();

    try{
      if(runningInJar){
          pixelImage = ImageIO.read(PicrossEditor.class.getClassLoader().getResource("pixel_images.png"));
      }else{
          pixelImage = ImageIO.read(new File("resource/pixel_images.png"));
      }
    }catch(Exception e){
        e.printStackTrace();
        System.exit(1);
    }
  }

  protected static void checkIfRunningInJar(){
    String s = PicrossEditor.class.getResource("PicrossEditor.class").toString();
    runningInJar = s.startsWith("jar");
  }

  public static BufferedImage checkedNumberImage(int n){
    BufferedImage numberImg = numberImage(n);
    BufferedImage checkImg = cellImage(PixelType.CHECK);
    BufferedImage retImage =
      new BufferedImage(numberImg.getWidth(null),
                        numberImg.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)retImage.getGraphics();
    g.drawImage(numberImg, 0, 0, null);
    g.drawImage(checkImg, 0, 0, null);
    g.dispose();

    return retImage;
  }

  public static BufferedImage convertToBuffered(Image img){
    BufferedImage retImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D)retImage.getGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose();

    return retImage;
  }

  public static BufferedImage cellImage(PixelType type){
    if(type == PixelType.NOFILL){
        return getImage(101, false);
    }else if(type == PixelType.FILL){
        return getImage(102, false);
    }else if(type == PixelType.CROSS){
        return getImage(103, false);
    }else if(type == PixelType.GUESS_NOFILL){
        return getImage(104, false);
    }else if(type == PixelType.GUESS_FILL){
        return getImage(105, false);
    }else if(type == PixelType.GUESS_CROSS){
        return getImage(106, false);
    }else if(type == PixelType.CHECK){
        return getImage(107, false);
    }else{
        return null;
    }
  }

  public static BufferedImage numberImage(int n){
    return getImage(n, true);
  }

  public static BufferedImage getImage(int n, boolean numberOnly){
    if(numberOnly && n > 100){
      return null;
    }

    if(imageCache.get(n) == null){
      Point pt = calcImagePosition(n);
      BufferedImage img = pixelImage.getSubimage(pt.x, pt.y, imageUnitSize, imageUnitSize);
      BufferedImage scaled = convertToBuffered(img.getScaledInstance(pixelSize, pixelSize, Image.SCALE_SMOOTH));

      // Cashe an Image
      imageCache.put(n,scaled);

      return scaled;
    }else{
      // Use the Cashed Image
      return imageCache.get(n);
    }
  }

  public static Point calcImagePosition(int n){
    return new Point(imageUnitSize*((n-1)%20),imageUnitSize*((n-1)/20));
  }
}