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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.sendopts;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.api.sendopts.CommandLine;
import org.openide.util.NbBundle;

/**
 * Bridge between the CLIHandler that can be unit tested
 * @author Jaroslav Tulach
 */
final class HandlerImpl extends Object {
    static int execute(String[] arr, InputStream is, OutputStream os, OutputStream err, File pwd) {
        try {
            CommandLine.getDefault().process(
                arr, is, os, err, pwd
                );
            for (int i = 0; i < arr.length; i++) {
                arr[i] = null;
            }
            return 0;
        } catch (CommandException ex) {
            PrintStream ps = new PrintStream(err);
            ps.println(ex.getLocalizedMessage());
            // XXX pst is not useful, only in verbose mode
            // the question is how to turn that mode on
            // ex.printStackTrace(ps);
            int ret = ex.getExitCode();
            if (ret == 0) {
                ret = Integer.MIN_VALUE;
            }
            return ret;
        }
    }
    
    static void usage(PrintWriter w) {
        w.print(NbBundle.getMessage(HandlerImpl.class, "MSG_OptionsHeader")); // NOI18N
        CommandLine.getDefault().usage(w);
        w.println();
    }
}
