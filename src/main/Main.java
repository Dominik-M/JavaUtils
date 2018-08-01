/*
 * Copyright (C) 2018 domin
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
package main;

import dmsr.utils.eds.Executable;
import dmsr.utils.eds.Interpreter;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Dominik Messerschmidt
 */
public final class Main
{

    public static final Interpreter INTERPRETER = new Interpreter();

    private static void init()
    {
        INTERPRETER.COMMANDS.add("help", (Executable) (String... params) ->
        {
            System.out.println("Commands:");
            for (String cmd : INTERPRETER.COMMANDS.getKeys())
            {
                System.out.println(cmd);
            }
        });

        INTERPRETER.COMMANDS.add("createSpritesheet", (Executable) (String... params) ->
        {
            try
            {
                BufferedImage img = dmsr.utils.images.ImageIO.mergeToSpriteSheet(new File(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
                ImageIO.write(img, "png", new File("spritesheet.png"));
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(null, "Invalid parameters\n" + ex, "createSpritesheet - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        init();

        if (!INTERPRETER.process(args, 0))
        {
            INTERPRETER.process("help");
            String input = args[0];
            for (int i = 1; i < args.length; i++)
            {
                input += " " + args[i];
            }
            JOptionPane.showMessageDialog(null, "Failed to process input \"" + input + "\"", "Error", JOptionPane.ERROR_MESSAGE);
        }

        /*
        // sort.Sortingtest.runTest(10, 10000);
        Server server = Server.getInstance();
        server.start();
        Client client = new Client();
        client.addClientListener(new ClientListener()
        {
            @Override
            public void onConnect()
            {
                System.out.println("onConnect()");
            }

            @Override
            public void onDisconnect()
            {
                System.out.println("onDisconnect()");
            }

            @Override
            public void onUserlistChanged()
            {
                System.out.println("onUserlistChanged()");
            }

            @Override
            public void println(String txt)
            {
                System.out.println(txt);
            }
        });

        if (client.connect("Peter", "0.0.0.0", 52056))
        {
            client.send(client.assembleRequest(Constants.REQUEST_QUIT, ""));
        }
         */
    }
}
