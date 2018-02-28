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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.Timer;

/**
 *
 * @author Dominik Messerschmidt
 */
public class MainFrame extends javax.swing.JFrame
{

    private final Timer refreshTimer = new Timer(200, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            if (filewalker != null)
            {
                switch (filewalker.getProcessState())
                {
                    case IDLE:
                        finishedSearch();
                        break;
                    case SEARCHING:
                        filecountLabel.setText("Indexing...    -    Files processed: " + filewalker.getProcessedFiles());
                        break;
                    case SORTING:
                        if (selectedFiles != null)
                        {
                            int part = filewalker.getProcessedFiles();
                            int total = selectedFiles.length;
                            filecountLabel.setText("Sorting...    -    Files processed: " + part + " / " + total);
                            copyProgressBar.setValue(100 * part / total);
                        }
                        else
                        {
                            filecountLabel.setText("Sorting...    -    Files processed: " + filewalker.getProcessedFiles());
                        }
                        break;
                    case COPYING:
                        if (selectedFiles != null)
                        {
                            int part = filewalker.getProcessedFiles();
                            int total = selectedFiles.length;
                            filecountLabel.setText("Copying...    -    Files processed: " + part + " / " + total);
                            copyProgressBar.setValue(100 * part / total);
                        }
                        else
                        {
                            filecountLabel.setText("Copying...    -    Files processed: " + filewalker.getProcessedFiles());
                        }
                        break;
                }
            }
        }
    });

    private final FilewalkerListener callback = new FilewalkerListener()
    {
        @Override
        public void stateChanged(Filewalker source)
        {
            switch (source.getProcessState())
            {
                case IDLE:
                    finishedSearch();
                    selectedFiles = source.getSelectedFiles();
                    refreshList();
                    break;
                case SEARCHING:
                    selectedFiles = null;
                    refreshList();
                    System.out.println("Starting file search");
                    break;
                case SORTING:
                    System.out.println("Starting sort process");
                    selectedFiles = source.getSelectedFiles();
                    refreshList();
                    break;
                case COPYING:
                    System.out.println("Starting copy process");
                    break;
                case DONE:
                    System.out.println("Finished copy process");
                    selectedFiles = source.getSelectedFiles();
                    filecountLabel.setText("Ready    -    Files copied: " + selectedFiles.length);
                    refreshList();
                    refreshTimer.stop();
                    copyProgressBar.setValue(100);
                    startButton.setText("Start search");
                    break;
            }
        }
    };

    Filewalker filewalker;
    private File[] selectedFiles;

    /**
     * Creates new form MainFrame
     */
    public MainFrame()
    {
        initComponents();
        postInit();
    }

    private void finishedSearch()
    {
        filecountLabel.setText("Ready    -    Files found: " + filewalker.getProcessedFiles());
        refreshTimer.stop();
        startButton.setText("Start search");
        startCopy.setEnabled(true);
        copyProgressBar.setValue(0);
    }

    private void postInit()
    {
        sourceTextField.setText(new File("").getAbsolutePath());
        destTextField.setText(new File("").getAbsolutePath() + "\\output");
        output.setVisible(false);
        startCopy.setEnabled(false);
        pack();
    }

    public void refreshList()
    {
        output.setText("");
        if (selectedFiles != null)
        {
            for (File f : selectedFiles)
            {
                output.append(f.getAbsolutePath() + "\r\n");
            }
            output.setVisible(true);
        }
        else
        {
            output.setVisible(false);
        }
    }

    public File getSourceFile()
    {
        return new File(sourceTextField.getText());
    }

    public File getDestFile()
    {
        return new File(destTextField.getText());
    }

    public File requestDirectory(File current)
    {
        JFileChooser jfc = new JFileChooser(current);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = jfc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            return jfc.getSelectedFile();
        }
        else
        {
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        sourceTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        sourceBrowseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        destTextField = new javax.swing.JTextField();
        destBrowseButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        recusiveEnableCheckBox = new javax.swing.JCheckBox();
        startButton = new javax.swing.JButton();
        copyProgressBar = new javax.swing.JProgressBar();
        filecountLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();
        startCopy = new javax.swing.JButton();
        filterFilesCheckBox = new javax.swing.JCheckBox();
        skipSysCheckBox = new javax.swing.JCheckBox();
        fileendingsTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FileCopyR");

        sourceTextField.setText("C:\\Users\\domin\\Documents\\FileCopyR");

        jLabel1.setText("Source Folder:");

        sourceBrowseButton.setText("Browse");
        sourceBrowseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                sourceBrowseButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Target Folder:");

        destTextField.setText("C:");

        destBrowseButton.setText("Browse");
        destBrowseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                destBrowseButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Options:");

        recusiveEnableCheckBox.setSelected(true);
        recusiveEnableCheckBox.setText("Recursive");

        startButton.setText("Start search");
        startButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startButtonActionPerformed(evt);
            }
        });

        copyProgressBar.setStringPainted(true);

        filecountLabel.setText(" ");

        output.setEditable(false);
        output.setColumns(20);
        output.setRows(5);
        jScrollPane1.setViewportView(output);

        startCopy.setText("Copy to target");
        startCopy.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startCopyActionPerformed(evt);
            }
        });

        filterFilesCheckBox.setSelected(true);
        filterFilesCheckBox.setText("Filter for filename endings");
        filterFilesCheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                filterFilesCheckBoxActionPerformed(evt);
            }
        });

        skipSysCheckBox.setSelected(true);
        skipSysCheckBox.setText("Skip system directories");

        fileendingsTextField.setText("png,jpg,bmp,gif");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(copyProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sourceTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(destTextField))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(recusiveEnableCheckBox)
                                        .addGap(105, 105, 105)
                                        .addComponent(skipSysCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(filterFilesCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fileendingsTextField)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceBrowseButton)
                            .addComponent(destBrowseButton)))
                    .addComponent(filecountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(startCopy)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sourceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(sourceBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(destTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(destBrowseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(recusiveEnableCheckBox)
                        .addComponent(skipSysCheckBox)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterFilesCheckBox)
                    .addComponent(fileendingsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(startCopy))
                .addGap(18, 18, 18)
                .addComponent(filecountLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(copyProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sourceBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sourceBrowseButtonActionPerformed
    {//GEN-HEADEREND:event_sourceBrowseButtonActionPerformed
        File dir = requestDirectory(getSourceFile());
        if (dir != null && dir.exists() && dir.isDirectory())
        {
            sourceTextField.setText(dir.getAbsolutePath());
        }
    }//GEN-LAST:event_sourceBrowseButtonActionPerformed

    private void destBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_destBrowseButtonActionPerformed
    {//GEN-HEADEREND:event_destBrowseButtonActionPerformed
        File dir = requestDirectory(getDestFile());
        if (dir != null && dir.exists() && dir.isDirectory())
        {
            destTextField.setText(dir.getAbsolutePath());
        }
    }//GEN-LAST:event_destBrowseButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startButtonActionPerformed
    {//GEN-HEADEREND:event_startButtonActionPerformed
        if (filewalker != null && (filewalker.isAlive() || filewalker.getProcessState() == Filewalker.State.COPYING))
        {
            // stop
            System.out.println("Sending stop request");
            filewalker.stopRequest();
        }
        else
        {
            // start
            System.out.println("Creating new Filewalker instance");
            boolean recursive = recusiveEnableCheckBox.isSelected();
            boolean skipSys = skipSysCheckBox.isSelected();
            filewalker = new Filewalker(getSourceFile(), recursive, skipSys);
            if (filterFilesCheckBox.isSelected())
            {
                String[] fileendings = fileendingsTextField.getText().split(",");
                filewalker.setSupportedFormats(fileendings);
            }
            filewalker.LISTENER.add(callback);
            filewalker.start();
            refreshTimer.start();
            startButton.setText("Stop");
            startCopy.setEnabled(false);
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void startCopyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startCopyActionPerformed
    {//GEN-HEADEREND:event_startCopyActionPerformed
        if (filewalker != null && filewalker.getProcessState() == Filewalker.State.IDLE)
        {
            filewalker.startCopyProcess(getDestFile(), selectedFiles);
            refreshTimer.start();
            startCopy.setEnabled(false);
            startButton.setText("Stop");
        }
    }//GEN-LAST:event_startCopyActionPerformed

    private void filterFilesCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_filterFilesCheckBoxActionPerformed
    {//GEN-HEADEREND:event_filterFilesCheckBoxActionPerformed
        fileendingsTextField.setEnabled(filterFilesCheckBox.isSelected());
    }//GEN-LAST:event_filterFilesCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar copyProgressBar;
    private javax.swing.JButton destBrowseButton;
    private javax.swing.JTextField destTextField;
    private javax.swing.JLabel filecountLabel;
    private javax.swing.JTextField fileendingsTextField;
    private javax.swing.JCheckBox filterFilesCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea output;
    private javax.swing.JCheckBox recusiveEnableCheckBox;
    private javax.swing.JCheckBox skipSysCheckBox;
    private javax.swing.JButton sourceBrowseButton;
    private javax.swing.JTextField sourceTextField;
    private javax.swing.JButton startButton;
    private javax.swing.JButton startCopy;
    // End of variables declaration//GEN-END:variables
}
