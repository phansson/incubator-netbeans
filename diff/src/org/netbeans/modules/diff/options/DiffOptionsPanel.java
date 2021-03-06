/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.diff.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.*;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.diff.DiffModuleConfig;
import org.netbeans.spi.options.OptionsPanelController;

/**
 * Diff Options panel.
 *
 * @author  Maros Sandor
 */
@OptionsPanelController.Keywords(keywords={"diff"}, location=OptionsDisplayer.ADVANCED, tabTitle="Diff")
class DiffOptionsPanel extends javax.swing.JPanel implements ActionListener, DocumentListener {

    private boolean isChanged;
    
    /** Creates new form DiffOptionsPanel */
    public DiffOptionsPanel() {
        initComponents();
        ignoreWhitespace.addActionListener(this);
        ignoreAllWhitespace.addActionListener(this);
        ignoreCase.addActionListener(this);
    }

    public JCheckBox getIgnoreWhitespace() {
        return ignoreWhitespace;
    }

    public JCheckBox getIgnoreInnerWhitespace() {
        return ignoreAllWhitespace;
    }

    public JCheckBox getIgnoreCase() {
        return ignoreCase;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public boolean isChanged() {
        return isChanged;
    }    
    
    private void fireChanged() {
        if(ignoreWhitespace.isSelected() != DiffModuleConfig.getDefault().getOptions().ignoreLeadingAndtrailingWhitespace) {
            isChanged = true;
            return;
        }
        if(ignoreAllWhitespace.isSelected() != DiffModuleConfig.getDefault().getOptions().ignoreInnerWhitespace) {
            isChanged = true;
            return;
        }
        if(ignoreCase.isSelected() != DiffModuleConfig.getDefault().getOptions().ignoreCase) {
            isChanged = true;
            return;
        }
        isChanged = false;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        ignoreWhitespace = new javax.swing.JCheckBox();
        ignoreAllWhitespace = new javax.swing.JCheckBox();
        ignoreCase = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 0, 5));

        org.openide.awt.Mnemonics.setLocalizedText(ignoreWhitespace, org.openide.util.NbBundle.getMessage(DiffOptionsPanel.class, "jCheckBox1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ignoreAllWhitespace, org.openide.util.NbBundle.getMessage(DiffOptionsPanel.class, "DiffOptionsPanel.ignoreAllWhitespace.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(ignoreCase, org.openide.util.NbBundle.getMessage(DiffOptionsPanel.class, "DiffOptionsPanel.ignoreCase.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ignoreWhitespace)
                    .addComponent(ignoreAllWhitespace)
                    .addComponent(ignoreCase))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ignoreWhitespace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ignoreAllWhitespace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ignoreCase))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox ignoreAllWhitespace;
    private javax.swing.JCheckBox ignoreCase;
    private javax.swing.JCheckBox ignoreWhitespace;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed (ActionEvent e) {
        fireChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        fireChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        fireChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        fireChanged();
    }
    
}
