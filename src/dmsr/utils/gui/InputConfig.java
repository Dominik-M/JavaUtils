/*
 * Copyright (C) 2015 Dominik Messerschmidt <dominik_messerschmidt@yahoo.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dmsr.utils.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Dominik Messerschmidt <dominik_messerschmidt@yahoo.de>
 */
@SuppressWarnings("serial")
public class InputConfig extends javax.swing.JPanel
{

    public enum Key
    {
        // TODO define keys here
        KEY_UP(KeyEvent.VK_UP),
        KEY_RIGHT(KeyEvent.VK_RIGHT),
        KEY_DOWN(KeyEvent.VK_DOWN),
        KEY_LEFT(KeyEvent.VK_LEFT),
        KEY_A(KeyEvent.VK_SPACE),
        KEY_B(KeyEvent.VK_SHIFT),
        KEY_SELECT(KeyEvent.VK_CONTROL),
        KEY_ENTER(KeyEvent.VK_ENTER);

        int keycode;

        Key(int keycode)
        {
            this.keycode = keycode;
        }
    }

    public static final File CONFIG_FILE = new File("config.dat");

    private final MouseListener mouseListener = new MouseListener()
    {

        @Override
        public void mouseClicked(MouseEvent arg0)
        {
            hint.setText("Press a Key");
            inputEn = true;
        }

        @Override
        public void mouseEntered(MouseEvent arg0)
        {
        }

        @Override
        public void mouseExited(MouseEvent arg0)
        {
        }

        @Override
        public void mousePressed(MouseEvent arg0)
        {
        }

        @Override
        public void mouseReleased(MouseEvent arg0)
        {
        }
    };
    private final KeyListener keyListener = new KeyListener()
    {

        @Override
        public void keyPressed(KeyEvent arg0)
        {
            if (inputEn)
            {
                for (int i = 0; i < buttons.length; i++)
                {
                    if (arg0.getSource().equals(buttons[i]))
                    {
                        Key.values()[i].keycode = arg0.getKeyCode();
                        break;
                    }
                }
                hint.setText("");
                inputEn = false;
                paintLabels();
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0)
        {
        }

        @Override
        public void keyTyped(KeyEvent arg0)
        {
        }

    };

    private javax.swing.JLabel hint;
    private javax.swing.JButton[] buttons;
    private boolean inputEn;

    /**
     * Creates new form InputConfig
     */
    public InputConfig()
    {
        initComponents();
        paintLabels();
    }

    private void initComponents()
    {
        buttons = new javax.swing.JButton[Key.values().length];
        hint = new javax.swing.JLabel("");
        inputEn = false;
        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i] = new javax.swing.JButton(KeyEvent.getKeyText(Key.values()[i].keycode));
            buttons[i].addMouseListener(mouseListener);
            buttons[i].addKeyListener(keyListener);
        }
        javax.swing.JPanel gridview = new javax.swing.JPanel();
        int cols = 2;
        gridview.setLayout(new java.awt.GridLayout(1 + Key.values().length / cols, cols));
        for (int i = 0; i < buttons.length; i++)
        {
            javax.swing.JPanel btnPanel = new javax.swing.JPanel();
            btnPanel.setLayout(new java.awt.GridLayout(1, 2));
            btnPanel.add(new javax.swing.JLabel(Key.values()[i].name() + ": ",
                    javax.swing.JLabel.CENTER));
            btnPanel.add(buttons[i]);
            gridview.add(btnPanel);
        }
        this.setLayout(new java.awt.BorderLayout());
        this.add(gridview, java.awt.BorderLayout.CENTER);
        this.add(hint, java.awt.BorderLayout.SOUTH);
    }

    private void paintLabels()
    {
        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i].setText(KeyEvent.getKeyText(Key.values()[i].keycode));
        }
    }

    public static Key translateKeyCode(int keycode)
    {
        for (Key key : Key.values())
        {
            if (key.keycode == keycode)
            {
                return key;
            }
        }
        return null;
    }

    public static void showInputConfigDialog(Frame parent)
    {
        javax.swing.JDialog dialog = new javax.swing.JDialog(parent, "Configure Input", true);
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().add(new InputConfig(), BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocation(parent.getLocation());
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowListener()
        {

            @Override
            public void windowOpened(WindowEvent e)
            {
            }

            @Override
            public void windowClosing(WindowEvent e)
            {
            }

            @Override
            public void windowClosed(WindowEvent e)
            {
                saveConfig();
            }

            @Override
            public void windowIconified(WindowEvent e)
            {
            }

            @Override
            public void windowDeiconified(WindowEvent e)
            {
            }

            @Override
            public void windowActivated(WindowEvent e)
            {
            }

            @Override
            public void windowDeactivated(WindowEvent e)
            {
            }
        });
    }

    public static void saveConfig()
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            int[] keycodes = new int[Key.values().length];
            for (int i = 0; i < keycodes.length; i++)
            {
                keycodes[i] = Key.values()[i].keycode;
            }
            oos.writeObject(keycodes);
            fos.close();
            oos.close();
            System.out.println("saved config file");
        }
        catch (IOException ex)
        {
            System.err.println("failed to save Config File: " + ex);
        }
    }

    public static void loadConfig()
    {
        try
        {
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            int[] keycodes = (int[]) ois.readObject();
            for (int i = 0; i < keycodes.length && i < Key.values().length; i++)
            {
                Key.values()[i].keycode = keycodes[i];
            }
            fis.close();
            ois.close();
            System.out.println("loaded config file");
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.err.println("failed to load Config File: " + ex);
        }
    }
}
