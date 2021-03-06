/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx2.project.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.javafx2.project.JFXProjectProperties;

/**
 *
 * @author psomol
 */
public class JFXApplicationPanel extends javax.swing.JPanel {

    private static JFXProjectProperties jfxProps = null;

    /**
     * Creates new form JFXApplicationPanel
     */
    public JFXApplicationPanel(JFXProjectProperties properties) {
        initComponents();
        jfxProps = properties;
        textFieldVer.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
            void changed() {
                String v = textFieldVer.getText();
                jfxProps.setImplementationVersion(v);
            }
        });
        String ver = jfxProps.getImplementationVersion();
        textFieldVer.setText(ver);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        labelVer = new javax.swing.JLabel();
        textFieldVer = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        labelVer.setLabelFor(textFieldVer);
        org.openide.awt.Mnemonics.setLocalizedText(labelVer, org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "JFXApplicationPanel.labelVer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(14, 1, 10, 0);
        add(labelVer, gridBagConstraints);
        labelVer.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "AN_JFXApplicationPanel.labelVer.text")); // NOI18N
        labelVer.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "AD_JFXApplicationPanel.labelVer.text")); // NOI18N

        textFieldVer.setText(org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "JFXApplicationPanel.textFieldVer.text")); // NOI18N
        textFieldVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldVerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 160;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 5);
        add(textFieldVer, gridBagConstraints);
        textFieldVer.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "AN_JFXApplicationPanel.textFieldVer.text")); // NOI18N
        textFieldVer.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(JFXApplicationPanel.class, "AD_JFXApplicationPanel.textFieldVer.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldVerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldVerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelVer;
    private javax.swing.JTextField textFieldVer;
    // End of variables declaration//GEN-END:variables
}
