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
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Constants;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 16.09.2016
 */
public class Client extends Thread
{

    /**
     * Default Port number
     */
    public static final int PORT = 52056;

    private boolean noAck = false;
    private String[] names;
    private String name;
    private Socket socket;
    private BufferedReader vomserver;
    private PrintWriter zumserver;
    private final LinkedList<ClientListener> LISTENER = new LinkedList<>();

    public boolean isConnected()
    {
        return socket != null && socket.isConnected();
    }

    public void addClientListener(ClientListener l)
    {
        LISTENER.add(l);
    }

    public void removeClientListener(ClientListener l)
    {
        LISTENER.remove(l);
    }

    public boolean connect(String name, String adress, int port)
    {
        try
        {
            println("Verbindung wird hergestellt...");
            socket = new Socket(adress, port);
            zumserver = new PrintWriter(socket.getOutputStream(), true);
            vomserver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            start();
            if (setUserName(name))
            {
                println("Connection established successfully");
            }
            else
            {
                println("ERROR: Timed out while connecting");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        for (ClientListener l : LISTENER)
        {
            l.onConnect();
            l.println("Connected with " + socket.getInetAddress());
        }
        return true;
    }

    public void disconnect()
    {
        try
        {
            if (isConnected())
            {
                socket.close();
                zumserver.close();
                vomserver.close();
                socket = null;
                for (ClientListener l : LISTENER)
                {
                    l.onDisconnect();
                }
                println("Verbindung unterbrochen.");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void run()
    {
        if (isConnected())
        {
            println("verbunden mit " + socket.toString());
            while (socket != null && !socket.isClosed())
            {
                String text;
                try
                {
                    text = vomserver.readLine();
                }
                catch (java.net.SocketException ex)
                {
                    disconnect();
                    break;
                }
                catch (Exception ex)
                {
                    System.err.println("Fehler: " + ex);
                    continue;
                }
                println(text);
                if (text == null)
                {
                    println("Server offline");
                    disconnect();
                    break;
                }
                else if (text.startsWith(Constants.USERLIST_COMMAND))
                {
                    text = text.substring(Constants.USERLIST_COMMAND.length());
                    names = text.split(" ");
                    for (ClientListener l : LISTENER)
                    {
                        l.onUserlistChanged();
                    }
                }
                else if (text.startsWith(Constants.ACK))
                {
                    noAck = false;
                    notify();
                }
                else
                /* if (!sound.Sound.isPlaying())*/ {
                    //sound.Sound.playRandomSound();
                } // println(text);
            }
        }
        else
        {
            println("Es konnte keine Verbindung hergestellt werden.");
        }
    }

    public boolean send(String text)
    {
        if (isConnected())
        {
            zumserver.println(text);
            return true;
        }
        return false;
    }

    public synchronized boolean sendAndWaitOnAck(String text, int timeout)
    {
        if (send(text))
        {
            try
            {
                noAck = true;
                for (int i = 0; i < timeout / 10 && noAck; i++)
                {
                    wait(10);
                }
            }
            catch (Exception ex)
            {
            }
            return !noAck;
        }
        return false;
    }

    public boolean setUserName(String name)
    {
        this.name = name;

        return sendAndWaitOnAck(assembleRequest(Constants.REQUEST_NAME, name), 10000);
    }

    /**
     * Builds a String of marker for requests and the given opcode and params.
     *
     * @param code
     * @param params
     * @return
     */
    public String assembleRequest(int code, String params)
    {
        return Constants.REQUEST + code + " " + params;
    }

    public String[] getUsers()
    {
        return names;
    }

    public void println(String txt)
    {
        for (ClientListener l : LISTENER)
        {
            l.println(txt);
        }
        System.out.println(Thread.currentThread().getName() + ": " + txt);
    }
}
