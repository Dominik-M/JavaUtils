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
package dmsr.utils.images;

import dmsr.utils.eds.Dictionary;
import dmsr.utils.gui.ProgressPanel;
import java.awt.Color;
import java.awt.Graphics;
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
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.swing.Icon;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.03.2016
 */
public class ImageIO
{

    /**
     * Filename endings of supported image file formats. Per default these are
     * PNG, GIF, BMP and JPG.
     */
    public static final String[] SUPPORTED_FORMATS = new String[]
    {
        "png", "gif", "bmp", "jpg"
    };

    /**
     * FilenameFilter instance to allow only supported image file formats by
     * checking the filename ending (not case sensitive). Per default these are
     * png, gif, bmp and jpg files.
     */
    public static final FilenameFilter IMAGE_FILE_NAME_FILTER = new FilenameFilter()
    {

        @Override
        public boolean accept(File file, String name)
        {
            for (String format : SUPPORTED_FORMATS)
            {
                if (name.endsWith("." + format.toLowerCase()) || name.endsWith("." + format.toUpperCase()))
                {
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * FileFilter instance to allow only supported image file formats. Per
     * default these are png, gif, bmp and jpg files.
     */
    public static final FileFilter IMAGE_FILE_FILTER = (File file) ->
    {
        return IMAGE_FILE_NAME_FILTER.accept(file, file.getName());
    };

    /**
     * Color value used as marker to auto-create transparency when importing
     * sprites without alpha channel.
     */
    public static final Color COLOR_TRANSPARENT = new Color(250, 250, 250);

    /**
     * Dictionary mapping each imported Sprite to the name of the file where it
     * was loaded from.
     */
    private static final Dictionary<String, Sprite> SPRITES = new Dictionary<>();

    private ImageIO()
    {
    }

    /**
     * Checks whether the given key is already contained in the SPRITES
     * dictionary.
     *
     * @param name key to look for in SPRITES dictionary, usually a filename.
     * @return true if key is contained in the dictionary, false if not.
     */
    public static boolean containsSprite(String name)
    {
        return SPRITES.containsKey(name);
    }

    /**
     * Retrieve a Sprite associated with given key string from SPRITES
     * dictionary.
     *
     * @param name key to look for in SPRITES dictionary, usually a filename.
     * @return The Sprite mapped to given key or null if the key is missing.
     */
    public static Sprite getSprite(String name)
    {
        Sprite sprite = SPRITES.get(name);
        if (sprite == null)
        {
            System.err.println("Image " + name + " is missing");
        }
        return sprite;
    }

    /**
     * Map a Sprite to a key string in the SPRITES dictionary. Adding fails if
     * the key is already contained in the dictionary.
     *
     * @param name key string to associate the Sprite to, usually the origin
     * filename.
     * @param sprite A Sprite instance to be stored in the SPRITES dictionary.
     * @return true if the Sprite was added to the dictionary, false if the
     * given key already exists and thus the key-value pair could not be added.
     */
    public static boolean addSprite(String name, Sprite sprite)
    {
        return SPRITES.add(name, sprite);
    }

    /**
     * Retrieve a LinkedList of all key strings in the SPRITES dictionary. Keys
     * are usually filenames where the Sprites were loaded from.
     *
     * @return LinkedList of key strings
     */
    public static java.util.LinkedList<String> getSpriteNames()
    {
        return SPRITES.getKeys();
    }

    /**
     * Loads Sprites from all supported image files in the given directory and
     * adds them to the SPRITES dictionary. All Sprites are initialized as
     * rotatable per default. Files which cannot be imported because of an error
     * are skipped. Does nothing if given file does not exist or does not point
     * to a directory. Further updates default ProgressPanel about loading
     * process.
     *
     * @param dir File pointing to the directory to load files from.
     */
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

    /**
     * Creates and returns a BufferedImage of given dimensions with black
     * background and randomly spreaded white circles. Supposed to look like
     * stars in space.
     *
     * @param width Width of the Image in pixels
     * @param height Height of the Image in pixels
     * @return BufferedImage with given dimensions.
     */
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

    /**
     * Creates a Sprite from given Image and maps it to the given key in SPRITES
     * dictionary if the key is not already contained. If rotatable flag is set
     * rotated versions in 64 different angles from 0 to 354 degrees are stored
     * in the Sprite.
     *
     * @param key key string in SPRITE dictionary associated with the Sprite
     * @param bufferedSourceImage source image
     * @param rotatable If rotated images should be created and buffered.
     * @return
     */
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

    /**
     * Creates and returns an ImageIcon from given File.
     *
     * @param file image file to create an icon from
     * @return ImageIcon created from given file
     */
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

    public static File[] getImageFiles(String dirPath)
    {
        File[] imgFiles;
        File inputDirectory = new File(dirPath);

        if (inputDirectory.exists() && inputDirectory.isDirectory())
        {
            imgFiles = inputDirectory.listFiles(IMAGE_FILE_FILTER);
        }
        else
        {
            imgFiles = new File[0];
        }
        return imgFiles;
    }

    public static boolean writeImageData(BufferedImage img, String outputPath)
    {
        final String TAG = "Utils.writeImageData(): ";
        File outputFile;
        PrintWriter out;
        boolean retVal = true;

        try
        {
            outputFile = new File(outputPath);
            out = new PrintWriter(outputFile);
        }
        catch (FileNotFoundException e1)
        {
            System.err.println(TAG + "Output file not found");
            e1.printStackTrace();
            return false;
        }
        if (img != null)
        {
            int width = img.getWidth(), height = img.getHeight();
            System.out.println(TAG + "Image Dimensions: " + width + " x " + height);
            out.print(outputFile.getName().toUpperCase() + "[" + width + "x" + height + "] = ");
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    out.print(img.getRGB(x, y) + ",");
                }
                out.println();
            }
            out.println();
        }
        out.close();
        return retVal;
    }

    public static Image blend(BufferedImage img, final int value)
    {
        ImageFilter filter = new RGBImageFilter()
        {

            @Override
            public final int filterRGB(int x, int y, int rgb)
            {
                if ((rgb & 0xFF000000) != 0)
                {
                    // blend
                    return value | rgb;
                }
                else
                {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public static void replaceColor(BufferedImage img, int target, int value)
    {
        // the color we are looking for... Alpha bits are set to opaque
        int markerRGB = target | 0xFF000000;

        if (img != null)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                for (int y = 0; y < img.getHeight(); y++)
                {
                    if ((img.getRGB(x, y) | 0xFF000000) == markerRGB)
                    {
                        img.setRGB(x, y, value);
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("Image must not be null!");
        }
    }

    public static Image makeColorTransparent(Image im, final int color)
    {
        ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color | 0xFF000000;

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

    public static Image makeColorTransparent(Image im, final Color color)
    {
        int rgb = color.getRGB();
        return makeColorTransparent(im, rgb);
    }

    public static Image makeColorTransparentInverse(Image im, final int color)
    {
        ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb)
            {
                if ((rgb | 0xFF000000) != markerRGB)
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

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static boolean resizeImageFiles(double scale, File... files)
    {
        for (File f : files)
        {
            try
            {
                BufferedImage img = javax.imageio.ImageIO.read(f);
                Image rawimg = img.getScaledInstance((int) (img.getWidth() * scale), (int) (img.getHeight() * scale), 0);

                File output = new File(f.getParentFile().getAbsolutePath() + "/resized/"
                        + f.getName().substring(0, f.getName().lastIndexOf(".")) + ".png");

                javax.imageio.ImageIO.write(toBufferedImage(rawimg), "png", output);
            }
            catch (IOException e)
            {
                System.err.println("Failed to process " + f);
            }
        }
        return true;
    }

    public static BufferedImage mergeToSpriteSheet(File directory, int rows, int cols)
    {
        if (directory != null && directory.exists() && directory.isDirectory())
        {
            LinkedList<File> imagefiles = new LinkedList<>();
            for (File f : directory.listFiles())
            {
                // BufferedImage img = ImageIO.read(f);
                imagefiles.add(f);
            }
            if (imagefiles.size() > 1)
            {
                try
                {
                    BufferedImage img = javax.imageio.ImageIO.read(imagefiles.get(0));
                    int width = img.getWidth();
                    int height = img.getHeight();
                    int totalWidth = width * cols;
                    int totalHeight = height * rows;

                    System.out.println("Merging " + imagefiles.size() + " images. Resolution: "
                            + width + " x " + height);

                    BufferedImage spritesheet = new BufferedImage(totalWidth, totalHeight,
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics g = spritesheet.createGraphics();
                    for (int i = 0; i < imagefiles.size(); i++)
                    {
                        int x = i % cols * width;
                        int y = i / cols * height;
                        img = javax.imageio.ImageIO.read(imagefiles.get(i));
                        g.drawImage(img, x, y, width, height, null);
                    }
                    return spritesheet;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
