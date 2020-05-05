package org.KPFU;

/**
 * Hello world!
 *
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        final int PROXIMITY_MEASURE = 3;
        BufferedImage image;
        try
        {
            //считывание изображения
            image = ImageIO.read(new File("d:/jarFiles/katana.jpg"));

            //перевод в бинарное изображение:
            int[][] binImage = new int[image.getWidth()][image.getHeight()];

            for (int i = 0; i < image.getWidth(); i++)
            {
                for (int j = 0; j < image.getHeight(); j++)
                {
                    // Получаем цвет текущего пикселя
                    Color color = new Color(image.getRGB(i,j));
                    int blue = color.getBlue();
                    int red = color.getRed();
                    int green = color.getGreen();

                    // Применяем стандартный алгоритм для получения черно-белого изображения
                    int grey = (int) (red * 0.299 + green * 0.587 + blue * 0.114);

                    //пороговая фильтрация
                    if (grey <= 127)
                        binImage[i][j] = 0;
                    else
                        binImage[i][j] = 1;

                }
            }
            System.out.println(binImage[0][0]);
            TextBoxes boxes = new TextBoxes(binImage, PROXIMITY_MEASURE);
            BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        }
        catch (IOException e)
        {
            System.out.println(e);
        }

        System.out.println( "Hello World!" );
    }
}
