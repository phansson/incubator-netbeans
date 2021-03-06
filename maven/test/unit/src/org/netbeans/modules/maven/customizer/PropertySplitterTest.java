/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.customizer;

import junit.framework.TestCase;

/**
 * @author mkleint
 */
public class PropertySplitterTest extends TestCase {
    
    public PropertySplitterTest(String testName) {
        super(testName);
    }
    
    public void testNextPair() {
        PropertySplitter instance = new PropertySplitter("exec=\"tes t1\"");
        String result = instance.nextPair();
        assertEquals("exec=\"tes t1\"", result);
        instance = new PropertySplitter("exec=tes t1\nexec2=te st2");
        result = instance.nextPair();
        assertEquals("exec=tes t1", result);
        result = instance.nextPair();
        assertEquals("exec2=te st2", result);
        
        instance = new PropertySplitter("exec=\"test1 exec2=test2\"");
        result = instance.nextPair();
        assertEquals("exec=\"test1 exec2=test2\"", result);
        //Issue MEVENIDE-600
        instance = new PropertySplitter("netbeans.jar.run.workdir=\"C:\\Documents and Settings\\Anuradha\\My Documents\\NetBeansProjects\\mavenproject4\"");
        result = instance.nextPair();
        assertEquals("netbeans.jar.run.workdir=\"C:\\Documents and Settings\\Anuradha\\My Documents\\NetBeansProjects\\mavenproject4\"", result);
        
        instance = new PropertySplitter("exec=\"test1 exec2=test2\"\nexec2=\"test3==test3\"");
        result = instance.nextPair();
        assertEquals("exec=\"test1 exec2=test2\"", result);
        result = instance.nextPair();
        assertEquals("exec2=\"test3==test3\"", result);

        instance = new PropertySplitter("\"-Dfoo bar=baz quux\" whatever");
        instance.setSeparator(' ');
        assertEquals("\"-Dfoo bar=baz quux\"", instance.nextPair());
        assertEquals("whatever", instance.nextPair());
        assertEquals(null, instance.nextPair());
        
        instance = new PropertySplitter("foo=1\\\n2\\\n3\nbar=123");
        assertEquals("foo=123", instance.nextPair());
        assertEquals("bar=123", instance.nextPair());
        assertEquals(null, instance.nextPair());
        
    } 
    
    public void testIssue211686() {
        PropertySplitter instance = new PropertySplitter("-Dmaven.home=\"C:\\Program Files\\NetBeans Dev 201204240400\\java\\maven\" -Xms10m -classpath %classpath test.mavenproject17.App");
        instance.setSeparator(' ');
        assertEquals("-Dmaven.home=\"C:\\Program Files\\NetBeans Dev 201204240400\\java\\maven\"", instance.nextPair());

        instance = new PropertySplitter("-Dmaven.home='C:\\Program Files\\NetBeans Dev 201204240400\\java\\maven' -Xms10m -classpath %classpath test.mavenproject17.App");
        instance.setSeparator(' ');
        assertEquals("-Dmaven.home='C:\\Program Files\\NetBeans Dev 201204240400\\java\\maven'", instance.nextPair());
        
        // and embed the double quotes in quotes..
        instance = new PropertySplitter("-Dmaven.home='C:\\Program Files\\NetBeans Dev 2012\" \"04240400\\java\\maven' -Xms10m -classpath %classpath test.mavenproject17.App");
        instance.setSeparator(' ');
        assertEquals("-Dmaven.home='C:\\Program Files\\NetBeans Dev 2012\" \"04240400\\java\\maven'", instance.nextPair());
        
        // and embed the quotes in double quotes..
        instance = new PropertySplitter("-Dmaven.home='C:\\Program Files\\NetBeans Dev 2012\" \"04240400\\java\\maven' -Xms10m -classpath %classpath test.mavenproject17.App");
        instance.setSeparator(' ');
        assertEquals("-Dmaven.home='C:\\Program Files\\NetBeans Dev 2012\" \"04240400\\java\\maven'", instance.nextPair());

    }

}
