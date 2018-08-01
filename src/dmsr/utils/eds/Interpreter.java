/*
 * Copyright (C) 2018 Dominik Messerschmidt
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
package dmsr.utils.eds;

/**
 *
 * @author Dominik Messerschmidt
 */
public class Interpreter
{

    public final Dictionary<String, Executable> COMMANDS = new Dictionary<>();

    public boolean process(String[] args, int startIndex)
    {
        boolean ok = args != null && startIndex >= 0 && args.length >= startIndex;
        if (ok)
        {
            String command;
            String[] params;
            command = args[startIndex];
            params = new String[args.length - startIndex - 1];
            for (int i = 0; i < params.length; i++)
            {
                params[i] = args[startIndex + i + 1];
            }
            if (COMMANDS.containsKey(command))
            {
                COMMANDS.get(command).execute(params);
                ok = true;
            }
            else
            {
                System.err.println("Unknown command: " + command);
                ok = false;
            }
        }
        else
        {
            System.err.println("Missing command argument");
        }
        return ok;
    }

    public boolean process(String[] args)
    {
        return process(args, 0);
    }

    /**
     *
     * @param inputline Command line input like "COMMAND [parameter]"
     * @return true if command found and executed
     */
    public boolean process(String inputline)
    {
        return process(inputline.split(" "));
    }
}
