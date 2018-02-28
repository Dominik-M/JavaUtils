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
package main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominik Messerschmidt
 */
public class Filewalker extends Thread
{

    public static final String[] SUPPORTED_IMAGE_FORMATS = new String[]
    {
        "png", "jpg", "gif", "bmp"
    };

    public static final String[] SUPPORTED_AUDIO_FORMATS = new String[]
    {
        "wav", "mp3", "mid"
    };

    public static final String[] SYSTEM_DIRS = new String[]
    {
        "C:\\ProgramData", "C:\\Program Files", "C:\\Program Files (x86)", "C:\\Windows", "C:\\$"
    };

    public static final FileFilter SYS_DIR_FILE_FILTER = new FileFilter()
    {
        @Override
        public boolean accept(File file)
        {
            for (String sysdir : SYSTEM_DIRS)
            {
                if (file.getAbsolutePath().startsWith(sysdir))
                {
                    return false;
                }
            }
            return true;
        }
    };

    public enum State
    {
        IDLE, SEARCHING, SORTING, COPYING, DONE;
    }

    private final File root;
    private final boolean recursive, skipSys;
    private final LinkedList<File> selection;
    private FileFilter filter;
    private int processedFiles;
    private boolean stopRequest;
    private State state;
    public final LinkedList<FilewalkerListener> LISTENER = new LinkedList<>();

    public Filewalker(File rootDir, boolean recursiveSearchEnabled, boolean skipSystemDirs)
    {
        root = rootDir;
        recursive = recursiveSearchEnabled;
        selection = new LinkedList<>();
        state = State.IDLE;
        skipSys = skipSystemDirs;
        filter = null;
    }

    public Filewalker(File rootDir)
    {
        this(rootDir, true, true);
    }

    public void setSupportedFormats(String... fileendings)
    {
        if (fileendings == null)
        {
            System.out.println("Filewalker.setSupportedFormats(): Disabled file filtering");
            filter = null;
        }
        else
        {
            System.out.print("Filewalker.setSupportedFormats(): Enabled filter for file endings:");
            for (String format : fileendings)
            {
                System.out.print(" " + format);
            }
            System.out.println();
            filter = new FileFilter()
            {
                final String[] SUPPORTED_FORMATS = fileendings;

                @Override
                public boolean accept(File file)
                {
                    for (String format : SUPPORTED_FORMATS)
                    {
                        if (file.getName().endsWith("." + format.toLowerCase()) || file.getName().endsWith("." + format.toUpperCase()))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
    }

    public State getProcessState()
    {
        return state;
    }

    public void stopRequest()
    {
        stopRequest = true;
    }

    public int getProcessedFiles()
    {
        return processedFiles;
    }

    public File[] getSelectedFiles()
    {
        File[] files = new File[selection.size()];
        for (int i = 0; i < files.length; i++)
        {
            files[i] = selection.get(i);
        }
        return files;
    }

    public void startCopyProcess(File targetDir, File[] selectedFiles)
    {
        if (state == State.IDLE)
        {
            if (selectedFiles == null || selectedFiles.length <= 0)
            {
                System.out.println("Nothing to do");
                return;
            }
            if (targetDir != null)
            {
                if (!targetDir.exists())
                {
                    if (targetDir.mkdir())
                    {
                        System.out.println("Created directory " + targetDir);
                    }
                }
                if (targetDir.exists() && targetDir.isDirectory())
                {
                    setState(State.COPYING);
                    selection.clear();
                    processedFiles = 0;
                    new CopyProcess(this, targetDir, selectedFiles).start();
                }
            }
            else
            {
                System.err.println("Target Directory must not be null!");
            }
        }
        else
        {
            System.err.println("Cannot start Copying in state " + state.name());
        }
    }

    public int sort(LinkedList<File> files)
    {
        int count = 0;
        FileData[] data = new FileData[files.size()];
        LinkedList<File> rem = new LinkedList<>();
        for (int i = 0; i < files.size() && !stopRequest; i++)
        {
            data[i] = new FileData(files.get(i));
        }
        for (int i1 = 0; i1 < data.length && !stopRequest; i1++)
        {
            FileData f1 = data[i1];
            for (int i2 = i1 + 1; i2 < files.size() && !stopRequest; i2++)
            {
                FileData f2 = data[i2];
                if (f1 != null && f2 != null && f1.equals(f2))
                {
                    rem.add(files.get(i2));
                    System.out.println("found ambiguous file " + rem.getLast());
                    data[i2] = null;
                    count++;
                }
            }
            processedFiles++;
        }
        files.removeAll(rem);
        return count;
    }

    private void setState(State next)
    {
        this.state = next;
        for (FilewalkerListener l : LISTENER)
        {
            l.stateChanged(this);
        }
    }

    private void search(File dir)
    {
        if (stopRequest)
        {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null && (!skipSys || SYS_DIR_FILE_FILTER.accept(dir)))
        {
            for (File file : files)
            {
                System.out.println("Processing " + file.getAbsolutePath());
                if (file.isFile())
                {
                    if (filter == null || filter.accept(file))
                    {
                        selection.add(file);
                    }
                }
                else if (file.isDirectory())
                {
                    if (recursive)
                    {
                        search(file);
                    }
                }
                processedFiles++;
                if (stopRequest)
                {
                    return;
                }
            }
        }
    }

    @Override
    public void run()
    {
        selection.clear();
        processedFiles = 0;
        setState(State.SEARCHING);
        search(root);
        System.out.println("Finished searching.");
        System.out.println("Processed files: " + processedFiles + " - files found: " + selection.size());
        setState(State.SORTING);
        processedFiles = 0;
        int result = sort(selection);
        System.out.println("Finished sorting.");
        if (result > 0)
        {
            System.out.println("sorted out " + result + " files");
        }
        else
        {
            System.out.println("No doubled files found");
        }
        setState(State.IDLE);
    }

    public static final boolean isNumber(char c)
    {
        return c >= '0' && c <= '9';
    }

    private static class FileData
    {

        public final File FILE;
        public final long SIZE;

        public FileData(File file)
        {
            FILE = file;
            SIZE = file.getTotalSpace();
        }

        public boolean equals(FileData file)
        {
            return file.SIZE == SIZE && file.FILE.getName().equals(FILE.getName());
        }
    }

    private static class CopyProcess extends Thread
    {

        private final Filewalker parent;
        private final File targetDir;
        private final File[] filesToCopy;

        CopyProcess(Filewalker parent, File target, File[] filesToCopy)
        {
            this.parent = parent;
            this.targetDir = target;
            this.filesToCopy = filesToCopy;
        }

        boolean copy(File file)
        {
            boolean result = true;
            File target = new File(targetDir.getAbsolutePath() + "/" + file.getName());
            try
            {
                Path source = file.toPath();
                while (target.exists())
                {
                    String name = target.getName();
                    String format = "";
                    int index = name.indexOf(".");
                    if (index > 0)
                    {
                        format = name.substring(index);
                        name = name.substring(0, index);
                    }

                    int digits = 0;
                    while (digits < name.length() && Filewalker.isNumber(name.charAt(name.length() - digits - 1)))
                    {
                        digits++;
                    }
                    int ending = 0;
                    if (digits > 0)
                    {
                        index = name.length() - digits;
                        ending = 1 + Integer.parseInt(name.substring(index));
                        name = name.substring(0, index);
                    }
                    name = name + ending + format;

                    System.out.println(target.getName() + " has ambiguous name - resolved to " + name);

                    target = new File(targetDir.getAbsolutePath() + "/" + name);
                }
                Path dest = target.toPath();

                Files.copy(source, dest);
            }
            catch (FileAlreadyExistsException ex)
            {
                System.err.println(ex);
            }
            catch (IOException ex)
            {
                Logger.getLogger(Filewalker.class.getName()).log(Level.SEVERE, null, ex);
            }
            return result;
        }

        @Override
        public void run()
        {
            parent.stopRequest = false;
            if (parent.state == Filewalker.State.COPYING)
            {
                System.out.println("Copying " + filesToCopy.length + " files to " + targetDir);
                //TODO copy files
                for (int i = 0; i < filesToCopy.length && !parent.stopRequest; i++)
                {
                    if (copy(filesToCopy[i]))
                    {
                        System.out.println("Copied " + filesToCopy[i]);
                        parent.selection.add(filesToCopy[i]);
                    }
                    parent.processedFiles++;
                }
                parent.setState(Filewalker.State.DONE);
            }
        }
    }
}
