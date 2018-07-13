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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.09.2016
 */
public class Worker extends Thread
{

    private String name;
    private final Socket socket;
    private final PrintWriter raus;
    private final BufferedReader rein;
    private final WorkerListener server;

    public Worker(Socket s, WorkerListener server) throws IOException
    {
        setName("Worker-" + getName());
        socket = s;
        this.server = server;
        rein = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        raus = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run()
    {
        String text;
        while (socket.isConnected())
        {
            try
            {
                text = rein.readLine();
            }
            catch (IOException ex)
            {
                System.err.println("Fehler: " + ex);
                text = null;
            }
            if (text == null)
            {
                break;
            }
            else
            {
                server.receive(this, text);
            }
        }
        server.fire(this);
    }

    public void send(String text)
    {
        raus.println(text);
    }

    public void setArbeiterName(String s)
    {
        name = s;
        send("Your name was changed to " + s);
    }

    @Override
    public String toString()
    {
        if (name == null || name.isEmpty())
        {
            return "unknown User";
        }
        return name;
    }

    public void close()
    {
        try
        {
            socket.close();
            rein.close();
            raus.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
