/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.htmlui;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.JFrame;
import net.java.html.BrwsrCtx;
import net.java.html.geo.OnLocation;
import net.java.html.geo.Position;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FreeGeoProviderTest {
    private static final CountDownLatch down = new CountDownLatch(1);
    private static BrwsrCtx ctx;

    private CountDownLatch done = new CountDownLatch(1);
    private Position position;
    private Exception error;

    @BeforeClass
    public static void initializeContext() throws Exception {
        final JFXPanel p = new JFXPanel();
        final URL u = DialogsTest.class.getResource("/org/netbeans/api/htmlui/empty.html");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView v = new WebView();
                Scene s = new Scene(v);
                p.setScene(s);
                NbBrowsers.load(v, u, new Runnable() {
                    @Override
                    public void run() {
                        ctx = BrwsrCtx.findDefault(DialogsTest.class);
                        down.countDown();
                    }
                }, null);
            }
        });
        down.await();
        JFrame f = new JFrame();
        f.getContentPane().add(p);
        f.pack();
        f.setVisible(true);
    }


    @Test
    public void checkGeoLocation() throws InterruptedException {
        ctx.execute(new Runnable() {
            @Override
            public void run() {
                Position.Handle query = Loc.createQuery(FreeGeoProviderTest.this);
                query.setTimeout(10000);
                query.start();
            }
        });
        done.await(15, TimeUnit.SECONDS);
        if (error != null) {
            return;
        }
        assertNotNull(position);
    }

    @OnLocation(className = "Loc", onError = "noLocation")
    public void location(Position p) {
        this.position = p;
        done.countDown();
    }

    public void noLocation(Exception ex) {
        error = ex;
        done.countDown();
    }
}
