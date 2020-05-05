package org.KPFU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TextBoxes
{
    private int[][] binI;
    private int[][] convBinI;
    private int[][] boundingBoxes;
    private int[][] txtBoxes;
    private int PROXIMITY_MEASURE;

    public TextBoxes(int[][] binI, int PROXIMITY_MEASURE)
    {
        this.binI = binI;
        this.PROXIMITY_MEASURE = PROXIMITY_MEASURE;
        initConvBinI();
        initBoundingBoxes();
        initTxtBoxes();
    }

    private void initConvBinI()
    {
        int nRows = binI.length;
        int nColumns = binI[0].length;

        int nConvRows;// = nRows/PROXIMITY_MEASURE;
        if (nRows % PROXIMITY_MEASURE == 0)
            nConvRows = nRows/PROXIMITY_MEASURE;
        else
            nConvRows = nRows/PROXIMITY_MEASURE + 1;

        int nConvColumns;
        if (nColumns % PROXIMITY_MEASURE == 0)
            nConvColumns = nColumns/PROXIMITY_MEASURE;
        else
            nConvColumns = nColumns/PROXIMITY_MEASURE + 1;

        convBinI = new int[nConvRows][nConvColumns];
        int len1 = convBinI.length;
        int len2 = convBinI[0].length;
        int i = 0;
        int j = 0;
        while (i < nRows)
        {
            while (j < nColumns)
            {
                boolean breakFlag = false;
                for (int i1 = i; (i1 < i + PROXIMITY_MEASURE) && (i1 < nRows); i1++)
                {
                    for (int j1 = j; (j1 < j + PROXIMITY_MEASURE) && (j1 < nColumns); j1++)
                        if (binI[i1][j1] == 1)
                        {
                            convBinI[i/PROXIMITY_MEASURE][j/PROXIMITY_MEASURE] = 1;
                            breakFlag = true;
                            break;
                        }
                    if (breakFlag == true)
                        break;
                }
                if (breakFlag == false)
                    convBinI[i/PROXIMITY_MEASURE][j/PROXIMITY_MEASURE] = 0;
                j = j + PROXIMITY_MEASURE;
            }
            i = i + PROXIMITY_MEASURE;
        }
    }

    private void initBoundingBoxes()
    {
        //Красим объекты на изображение в разные цвета
        int color = 2;
        for (int i = 0; i < convBinI.length; i++)
            for (int j = 0; j < convBinI[0].length; j++)
            {
                if (convBinI[i][j] == 1)
                {
                    convBinI[i][j] = color;
                    color = color + 1;
                }

                if (convBinI[i][j] != 0)
                {
                    for (int i1 = i; (i1 <= i + 1) && (i1 < convBinI.length); i1++)
                    {
                        for (int j1 = j; (j1 <= j + 1) && (j1 < convBinI[0].length); j1++)
                            if (convBinI[i1][j1] > 1)
                                convBinI[i1][j1] = convBinI[i][j];
                        for (int j1 = j - 1; (j1 >= j - 1) && (j1 >= 0); j1--)
                            if (convBinI[i1][j1] > 1)
                                convBinI[i1][j1] = convBinI[i][j];
                    }

                }
            }

        //Получаем ограничивающие коробки для каждого цвета
        int nColor = color;
        boundingBoxes = new int[nColor - 1][4];
        for (color = 2; color <= nColor; color++)
        {
            int left = convBinI.length-1;
            int right = 0;
            int down = convBinI[0].length - 1;
            int up = 0;
            for (int i = 0; i < convBinI.length; i++)
                for (int j = 0; j < convBinI[0].length; j++)
                    if (convBinI[i][j] == color)
                    {
                        if (i < left) { left = i;}
                        if (i > right) { right = i;}
                        if (j < down) {down = j;}
                        if (j > up) {up = j;}
                    }
            boundingBoxes[color-2][0] = left;
            boundingBoxes[color-2][1] = right;
            boundingBoxes[color-2][2] = up;
            boundingBoxes[color-2][3] = down;
        }
    }

    private void initTxtBoxes()
    {
        //объединяем полученные пересекающиеся ограничивающие коробки
        int[][] textBoxes = new int[boundingBoxes.length][boundingBoxes[0].length];
        int k = 0;
        for (int i = 0; i < boundingBoxes.length; i++)
            for (int j = i+1; j < boundingBoxes.length; j++)
            {
                boolean leftCondition = (boundingBoxes[i][0] < boundingBoxes[j][0]) && (boundingBoxes[j][0] < boundingBoxes[i][1]);
                boolean rightCondition = (boundingBoxes[i][0] < boundingBoxes[j][1]) && (boundingBoxes[j][1] < boundingBoxes[i][1]);
                boolean upCondition = (boundingBoxes[i][2] < boundingBoxes[j][2]) && (boundingBoxes[j][2] < boundingBoxes[i][3]);
                boolean downCondition = (boundingBoxes[i][2] < boundingBoxes[j][3]) && (boundingBoxes[j][3] < boundingBoxes[i][3]);

                int left = -1;
                int right = -1;
                int up = -1;
                int down = -1;

                //Предполагается, что должно выполняться одно из условий
                if (leftCondition && upCondition)
                {
                    left =  boundingBoxes[i][0];
                    right = boundingBoxes[j][1];
                    up = boundingBoxes[i][2];
                    down = boundingBoxes[j][3];
                }

                if (rightCondition && upCondition)
                {
                    left =  boundingBoxes[j][0];
                    right = boundingBoxes[i][1];
                    up = boundingBoxes[i][2];
                    down = boundingBoxes[j][3];
                }

                if (leftCondition && downCondition)
                {
                    left =  boundingBoxes[i][0];
                    right = boundingBoxes[j][1];
                    up = boundingBoxes[j][2];
                    down = boundingBoxes[i][3];
                }

                if (rightCondition && downCondition)
                {
                    left =  boundingBoxes[j][0];
                    right = boundingBoxes[i][1];
                    up = boundingBoxes[j][2];
                    down = boundingBoxes[i][3];
                }

                textBoxes[k][0] = left;
                textBoxes[k][1] = right;
                textBoxes[k][2] = up;
                textBoxes[k][3] = down;
                k = k + 1;
            }

        // возврат натуральной величины ограницивающим коробкам
        txtBoxes = new int[k][4];
        for (int i = 0; i <= k; i++)
        {
            txtBoxes[i][0] = textBoxes[i][0]*PROXIMITY_MEASURE;
            txtBoxes[i][1] = textBoxes[i][1]*PROXIMITY_MEASURE;
            if (txtBoxes[i][1] < binI.length) {txtBoxes[i][1] = binI.length - 1;}
            txtBoxes[i][2] = textBoxes[i][2]*PROXIMITY_MEASURE;
            txtBoxes[i][3] = textBoxes[i][3]*PROXIMITY_MEASURE;
            if (txtBoxes[i][3] < binI[0].length) {txtBoxes[i][3] = binI[0].length - 1;}
        }
    }

    public int[][] getTxtBoxes()
    {
        return txtBoxes;
    }

    public BufferedImage getImageWithTextBoxes(BufferedImage image)
    {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.red);
        for (int i = 0; i < txtBoxes.length; i++)
        {
            g2d.drawRect(txtBoxes[i][0], txtBoxes[i][2], txtBoxes[i][1] - txtBoxes[i][0], txtBoxes[i][3] - txtBoxes[i][2]);
            g2d.dispose();
        }
        return image;
    }
}
