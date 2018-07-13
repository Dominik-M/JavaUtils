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

import dmsr.utils.client.Client;
import dmsr.utils.server.Constants;
import dmsr.utils.server.Server;

/**
 *
 * @author Dominik Messerschmidt
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // sort.Sortingtest.runTest(10, 10000);
        Server server = Server.getInstance();
        server.start();
        Client client = new Client();
        /*
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
         */
        if (client.connect("Peter", "0.0.0.0", 52056))
        {
            client.send(client.assembleRequest(Constants.REQUEST_QUIT, ""));
        }
    }
}
