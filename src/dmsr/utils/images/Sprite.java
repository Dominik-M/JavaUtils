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

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 26.03.2016
 */
public class Sprite
{
    // The images to be drawn for this sprite

    private final BufferedImage[] images = new BufferedImage[64];

    // The standard collision areas (when direction is 0)
    protected Sprite(BufferedImage[] images)
    {
        for (int i = 0; i < 64; i++)
        {
            this.images[i] = images[i];
        }
    }

    /**
     * Returns the width of the sprite with given id.
     *
     * @param spriteId the id of the sprite
     * @return The width in pixels of this sprite
     */
    public int getWidth(int spriteId)
    {
        return images[spriteId].getWidth(null);
    }

    /**
     * Returns the width of the sprite with id = 0.
     *
     * @return The width in pixels of this sprite
     */
    public int getWidth()
    {
        return getWidth(0);
    }

    /**
     * Returns the height of the sprite with given id.
     *
     * @param spriteId the id of the sprite
     * @return The height in pixels of this sprite
     */
    public int getHeight(int spriteId)
    {
        return images[spriteId].getHeight(null);
    }

    /**
     * Returns the height of the sprite with id = 0.
     *
     * @return The height in pixels of this sprite
     */
    public int getHeight()
    {
        return getHeight(0);
    }

    public BufferedImage[] getImages()
    {
        return images;
    }

    /**
     * Draws the sprite onto the graphics context provided.
     *
     * @param g The graphics context on which to draw the sprite
     * @param x The x location in pixel coordinates at which to draw the sprite
     * @param y The y location in pixel coordinates at which to draw the sprite
     * @param rotDegree rotation of the image in degree
     */
    public void draw(Graphics g, int x, int y, double rotDegree)
    {
        g.drawImage(images[(int) (64 * rotDegree / 360)], x, y, null);
    }
}
