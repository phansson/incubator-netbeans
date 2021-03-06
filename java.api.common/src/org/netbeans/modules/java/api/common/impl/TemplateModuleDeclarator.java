/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.java.api.common.impl;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.templates.CreateDescriptor;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.api.templates.CreateFromTemplateDecorator;

/**
 * Automatically declares dependencies on modules used in template.
 * Scans the created file; if some unresolved identifier appears, which corresponds to a 
 * class in platform or libraries, the decorator will add {@code requires} directive to the
 * appropriate {@code module-info.java}. Activates only on projects which have source level >= 9
 * and contain module declaration.
 * 
 * @author sdedic
 */
@ServiceProvider(service = CreateFromTemplateDecorator.class)
public final class TemplateModuleDeclarator implements CreateFromTemplateDecorator {
    private static final String NAME_MODULE_INFO = "module-info.java"; // NOI18N
    private static final String ATTRIBUTE_REQUIRED_MODULES = "java.template.requiredModules";                 // NOI18N
    private static final SpecificationVersion SOURCE_LEVEL_9 = new SpecificationVersion("9"); // NOI18N
    private static final String MIME_JAVA = "text/x-java"; // NOI18N
    private static final String CLASS_EXTENSION = ".class"; // NOI18N
    
    @Override
    public boolean isBeforeCreation() {
        return false;
    }

    @Override
    public boolean isAfterCreation() {
        return true;
    }

    @Override
    public boolean accept(CreateDescriptor desc) {
        if (desc.getValue(ATTRIBUTE_REQUIRED_MODULES) != null) {
            return true;
        }
        FileObject t = desc.getTarget();
        if (!MIME_JAVA.equals(desc.getTemplate().getMIMEType())) {
            return false;
        }
        String s = SourceLevelQuery.getSourceLevel(t);
        if (s == null || SOURCE_LEVEL_9.compareTo(new SpecificationVersion(s)) > 0) {
            return false;
        }
        // check module-info
        ClassPath srcPath = ClassPath.getClassPath(t, ClassPath.SOURCE);
        if (srcPath == null) {
            return false;
        }
        if (srcPath.findResource(NAME_MODULE_INFO) == null) { // NOI18N
            return false;
        }
        return true;
    }

    @Override
    public List<FileObject> decorate(CreateDescriptor desc, List<FileObject> createdFiles) throws IOException {
        List<FileObject>  jsources = new ArrayList<>(createdFiles.size());
        for (FileObject f : createdFiles) {
            if (MIME_JAVA.equals(f.getMIMEType())) {
                jsources.add(f);
            }
        }
        if (jsources.isEmpty()) {
            return null;
        }
        FileObject t = desc.getTarget();
        // check module-info
        ClassPath srcPath = ClassPath.getClassPath(t, ClassPath.SOURCE);
        if (srcPath == null) {
            return null;
        }
        FileObject modinfo = srcPath.findResource(NAME_MODULE_INFO); // NOI18N
        if (modinfo == null) {
            return null;
        }
        
        Object a = desc.getTemplate().getAttribute(ATTRIBUTE_REQUIRED_MODULES);
        Set<String> modules;
        
        if (a instanceof String) {
            String s = a.toString();
            if (!s.isEmpty()) {
                modules = new HashSet<>(Arrays.asList(s.split(","))); // NOI18N
            } else {
                modules = Collections.emptySet();
            }
        } else {
            modules = inferModuleNames(t, jsources);
        }
        
        if (modules.isEmpty()) {
            return null;
        }
        DefaultProjectModulesModifier.addRequiredModules(modinfo, modules);
        return null;
    }
        
   private Set<String> inferModuleNames(FileObject t, List<FileObject> jsources) throws IOException {
        ClasspathInfo cpi = ClasspathInfo.create(t);
        JavaSource src = JavaSource.create(cpi, jsources);
        Set<String> unresolved = new HashSet<>();
        src.runUserActionTask(new Task<CompilationController>() {
            @Override
            public void run(CompilationController parameter) throws Exception {
                parameter.toPhase(JavaSource.Phase.RESOLVED);
                Scanner s = new Scanner(parameter, unresolved);
                s.scan(parameter.getCompilationUnit(), null);
            }
        }, true);
        ClassPath searchPath = ClassPathSupport.createProxyClassPath(
                cpi.getClassPath(PathKind.MODULE_BOOT),
                cpi.getClassPath(PathKind.MODULE_COMPILE));
        Set<String> moduleNames = new HashSet<>();
        for (String fqn : unresolved) {
            findModuleNames(fqn, searchPath, moduleNames);
        }
        return moduleNames;
    }
   
    
    private void findModuleNames(String fqn, ClassPath searchPath, Set<String> moduleNames) {
        fqn = fqn.replace(".", "/"); // NOI18N
        String resourceName = fqn + CLASS_EXTENSION;
        FileObject classResource = searchPath.findResource(resourceName);
        if (classResource == null || classResource.isFolder()) {
            int last = fqn.length();
            for (int i = fqn.lastIndexOf('.'); i >= 0; i = fqn.lastIndexOf('/', last)) { // NOI18N
                resourceName = fqn.substring(0, i);
                classResource = searchPath.findResource(resourceName + CLASS_EXTENSION);
                if (classResource != null && classResource.isData()) {
                    resourceName += fqn.substring(i).replace("/", "$") + CLASS_EXTENSION; // NOI18N
                    classResource = searchPath.findResource(resourceName);
                    break;
                }
                classResource = searchPath.findResource(resourceName);
                if (classResource != null && classResource.isFolder()) {
                    return;
                }
            }
        }
        if (classResource != null) {
            FileObject r = searchPath.findOwnerRoot(classResource);
            for (ClassPath.Entry e : searchPath.entries()) {
                if (e.getRoot().equals(r)) {
                    String name = SourceUtils.getModuleName(e.getURL(), true);
                    moduleNames.add(name);
                }
            }
        }
    }
   
    private static class Scanner extends TreePathScanner<Boolean, Boolean> {
        private final CompilationInfo info;
        private final Set<String> unresolved;
        
        public Scanner(CompilationInfo info, Set<String> s) {
            this.info = info;
            this.unresolved = s;
        }

        @Override
        public Boolean visitMemberSelect(MemberSelectTree node, Boolean p) {
            Boolean r = super.scan(node.getExpression(), p);
            if (r == Boolean.TRUE) {
                return r;
            }
            TreePath parentPath = getCurrentPath().getParentPath();
            if (parentPath == null) {
                return null;
            }
            Tree t = node;
            Tree par = parentPath.getLeaf();
            if (node.getIdentifier().contentEquals("*")) { // NOI18N
                // assume import, discard one level
                t = node.getExpression();
                par = node;
                parentPath = new TreePath(parentPath, t);
            }
            if (par.getKind() == Kind.METHOD_INVOCATION) {
                return null;
            }
            final Element el = info.getTrees().getElement(getCurrentPath());
            if (el == null || !(el.getKind().isClass() || el.getKind().isInterface() || el.getKind() == ElementKind.PACKAGE)) {
                // ignore non-type and non-package stuff up to the root
                return true;
            }
            TypeMirror type = el.asType();
            if (type == null) {
                return null;
            }
            String fqn = null;
            
            if (type.getKind() == TypeKind.ERROR) {
                if (par.getKind() == Tree.Kind.ASSIGNMENT) {
                    AssignmentTree at = (AssignmentTree) getCurrentPath().getParentPath().getLeaf();

                    if (at.getVariable() == node) {
                        return null;
                    }
                }
            } else if (type.getKind() == TypeKind.PACKAGE) {
                String s = ((PackageElement) el).getQualifiedName().toString();
                if (info.getElements().getPackageElement(s) != null) {
                    return null;
                }
            }
            fqn = node.toString();
            if (fqn.endsWith(".<error>")) { // NOI18N
                fqn = fqn.substring(0, fqn.lastIndexOf(".")); // NOI18N
            }
            unresolved.add(fqn);
            return null;
        }
    }
}
