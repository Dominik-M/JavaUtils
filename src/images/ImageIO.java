/**
 * Copyright (C) 2016 Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package images;

import eds.Dictionary;
import graphic.ProgressPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import javax.swing.Icon;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.03.2016
 */
public class ImageIO
{

    /**
     * FileFilter instance to allow only supported image file formats. Per
     * default these are png, gif and jpg files.
     */
    public static final FileFilter IMAGE_FILE_FILTER = (File file) ->
    {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index > 0)
        {
            return name.substring(index).matches(".png|.gif|.jpg");
        }
        return false;
    };

    private static final Dictionary<String, Sprite> SPRITES = new Dictionary<>();

    private static final Color COLOR_TRANSPARENT = new Color(250, 250, 250);

    private ImageIO()
    {
    }

    public static boolean containsSprite(String name)
    {
        return SPRITES.containsKey(name);
    }

    public static Sprite getSprite(String name)
    {
        Sprite sprite = SPRITES.get(name);
        if (sprite == null)
        {
            System.err.println("Image " + name + " is missing");
        }
        return sprite;
    }

    public static boolean addSprite(String name, Sprite sprite)
    {
        return SPRITES.add(name, sprite);
    }

    public static java.util.LinkedList<String> getSpriteNames()
    {
        return SPRITES.getKeys();
    }

    public static void initAllSprites(File dir)
    {
        if (dir.exists() && dir.isDirectory())
        {
            try
            {
                ProgressPanel.setProgress(0);
                ProgressPanel.setText("Loading images...");
                File[] files = dir.listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    File file = files[i];
                    if (file.isFile())
                    {
                        if (file.getName().startsWith("map_"))
                        {
                            if (ImageIO.initSprite(file.getName(), javax.imageio.ImageIO.read(file),
                                    false))
                            {
                                System.out.println("initialized map: " + file.getName());
                            }
                        }
                        else
                        {
                            if (ImageIO.initSprite(file.getName(), javax.imageio.ImageIO.read(file),
                                    true))
                            {
                                System.out
                                        .println("initialized image: " + file.getName());
                            }
                        }
                    }
                    ProgressPanel.setProgress(100 * i / files.length);
                }
            }
            catch (IOException ex)
            {
                System.err.println(ex);
            }
            finally
            {
                ProgressPanel.setProgress(100);
                ProgressPanel.setText("Ready");
            }
        }
    }

    public static BufferedImage getRandomBackground(int width, int height)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        int starcount = (int) (Math.random() * width);
        g.setColor(Color.WHITE);
        for (int i = 0; i < starcount; i++)
        {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 4) + 1;
            g.fillOval(x, y, size, size);
        }
        return image;
    }

    public static boolean initSprite(String key, BufferedImage bufferedSourceImage,
            boolean rotatable)
    {
        try
        {
            if (!SPRITES.containsKey(key))
            {
                Image sourceImage = ImageIO.makeColorTransparent(bufferedSourceImage,
                        COLOR_TRANSPARENT);
                int w = bufferedSourceImage.getWidth();
                int h = bufferedSourceImage.getHeight();
                BufferedImage[] images = new BufferedImage[64];
                // create an accelerated image of the right size to store our sprite in
                GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
                if (rotatable)
                {
                    int s = (int) Math.ceil(Math.sqrt(w * w + h * h));
                    BufferedImage biTmp = gc.createCompatibleImage(s, s, Transparency.BITMASK);
                    Graphics2D gTmp = biTmp.createGraphics();
                    gTmp.drawImage(sourceImage, (s - w) / 2, (s - h) / 2, null);

                    for (int i = 0; i < 64; i++)
                    {
                        images[i] = gc.createCompatibleImage(s, s, Transparency.BITMASK);
                        Graphics2D g2D = images[i].createGraphics();

                        g2D.translate(s / 2, s / 2); // Translate the coordinate system (zero a image's center)
                        g2D.rotate(Math.toRadians(360.0 / 64 * i)); // Rotate the image
                        g2D.translate(-s / 2, -s / 2); // Translate the coordinate system (zero a image's center)
                        g2D.drawImage(biTmp, 0, 0, null);
                    }
                    gTmp.dispose();
                }
                else
                {
                    images[0] = gc.createCompatibleImage(w, h, Transparency.BITMASK);

                    Graphics2D g2D = images[0].createGraphics();
                    g2D.drawImage(sourceImage, 0, 0, null);
                }
                Sprite sprite = new Sprite(images);
                return SPRITES.add(key, sprite);
            }
        }
        catch (HeadlessException ex)
        {
            System.err.println(ex);
        }
        return false;
    }

    public static Icon initIcon(File file)
    {
        Icon icon = null;
        try
        {
            icon = new javax.swing.ImageIcon(file.getAbsolutePath());
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
        return icon;
    }

    public static Image makeColorTransparent(Image im, final Color color)
    {
        ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb)
            {
                if ((rgb | 0xFF000000) == markerRGB)
                {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }
                else
                {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}
