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
package dmsr.utils.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.09.2016
 */
public class Server implements Runnable, WorkerListener
{

    private static Server SERVER = null;
    private static Thread SERVER_THREAD;

    private final CopyOnWriteArrayList<Worker> USER;
    private final CopyOnWriteArrayList<ServerListener> LISTENER;
    private ServerSocket serverSocket;
    private boolean remoteQuitAllowed = true;

    private Server()
    {
        USER = new CopyOnWriteArrayList<>();
        LISTENER = new CopyOnWriteArrayList<>();
    }

    public static final Server getInstance()
    {
        if (SERVER == null)
        {
            synchronized (Server.class)
            {
                if (SERVER == null)
                {
                    SERVER = new Server();
                }
            }
        }
        return SERVER;
    }

    public void start()
    {
        try
        {
            serverSocket = new ServerSocket(Constants.PORT);
            SERVER_THREAD = new Thread(SERVER);
            SERVER_THREAD.setName("Server-Thread");
            SERVER_THREAD.start();
            for (ServerListener l : LISTENER)
            {
                l.onStart();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isRunning()
    {
        return serverSocket != null && !serverSocket.isClosed() && SERVER_THREAD.isAlive();
    }

    @Override
    public void run()
    {
        try
        {
            String host = serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
            sendAll("Server is running on " + host);
            while (!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
                Worker neu = new Worker(socket, this);
                neu.start();
                addUser(neu);
            }
            close();
        }
        catch (IOException ex)
        {
            println("Fehler: " + ex);
        }
    }

    @Override
    public synchronized void receive(Worker source, String text)
    {
        println(text);
        if (text.startsWith(Constants.REQUEST))
        {
            text = text.substring(Constants.REQUEST.length());
            int code = 0;
            if (text.contains(" "))
            {
                try
                {
                    code = Integer.parseInt(text.substring(0, text.indexOf(" ")));
                }
                catch (Exception ex)
                {
                }
                text = text.substring(text.indexOf(" ") + 1);
            }
            source.send(Constants.ACK);
            switch (code)
            {
                case Constants.REQUEST_NAME:
                {
                    source.setArbeiterName(text);
                    break;
                }
                case Constants.REQUEST_QUIT:
                {
                    if (remoteQuitAllowed)
                    {
                        try
                        {
                            serverSocket.close();
                        }
                        catch (IOException ex)
                        {
                            System.err.println(ex);
                        }
                    }
                    break;
                }
                default:
                {
                    for (ServerListener l : LISTENER)
                    {
                        l.performRequest(source, code, text);
                    }
                }
            }
        }
        else if (text.startsWith(Constants.ERROR))
        {

        }
        else if (text.startsWith(Constants.ACK))
        {

        }
        else
        {
            sendAll(source + ": " + text);
        }
    }

    @Override
    public void fire(Worker a)
    {
        USER.remove(a);
        sendAll(a + " disconnected");
        for (ServerListener l : LISTENER)
        {
            l.onUserlistChange();
        }
        sendUserList();
    }

    public synchronized void addUser(Worker neu)
    {
        USER.add(neu);
        try
        {
            wait(1000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendAll(neu + " joined");
        neu.send("Server: Welcome " + neu + "!");
        for (ServerListener l : LISTENER)
        {
            l.onUserlistChange();
        }
        sendUserList();
    }

    public String[] getUserNames()
    {
        String[] names = new String[USER.size()];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = USER.get(i).toString();
        }
        return names;
    }

    public void sendUserList()
    {
        String cmd = Constants.USERLIST_COMMAND + " ";
        for (String user : getUserNames())
        {
            cmd += user + " ";
        }
        sendAll(cmd);
    }

    public void sendAll(String text)
    {
        println(text);
        for (Worker user : USER)
        {
            user.send(text);
        }
    }

    public void sendAt(String text, String name)
    {
        for (Worker dest : USER)
        {
            if (name.equals(dest.toString()))
            {
                dest.send(text);
            }
        }
    }

    public void println(String txt)
    {
        for (ServerListener l : LISTENER)
        {
            l.println(txt);
        }
        System.out.println(Thread.currentThread().getName() + ": " + txt);
    }

    public void close()
    {
        try
        {
            for (Worker user : USER)
            {
                user.close();
            }
            serverSocket.close();
            for (ServerListener l : LISTENER)
            {
                l.onClose();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addServerListener(ServerListener l)
    {
        LISTENER.add(l);
    }

    public void removeServerListener(ServerListener l)
    {
        LISTENER.remove(l);
    }
}
