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
package server;

import java.io.File;

/**
 *
 * @author Dominik Messerschmidt
 * <dominik.messerschmidt@continental-corporation.com> Created 14.09.2016
 */
public class Constants {

    /**
     * Default Port number
     */
    public static final int PORT = 52056;

    /**
     * Command prefixes
     */
    public static final String REQUEST = "#R", ACK = "#A", ERROR = "#E", COMMAND = "#C";

    /**
     * Request codes
     */
    public static final int REQUEST_NAME = 1, REQUEST_QUIT = 2, REQUEST_PRINT = 3, REQUEST_VALIDATE = 4, REQUEST_ADD = 5;

    /**
     * Error codes
     */
    public static final int ERROR_UNKNOWN_COMMAND = 1;

    /**
     * Command codes
     */
    public static final int C_USERLIST = 1, C_OK = 2, C_NOK = 3;

    public static final File DB_FILE = new File("values.dat");

    public static String USERLIST_COMMAND = COMMAND + C_USERLIST;
    public static String REPLY_VALID = COMMAND + C_OK;
    public static String REPLY_INVALID = COMMAND + C_NOK;

    private Constants() {
    }
}
