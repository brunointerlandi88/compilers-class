/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import java.io.PrintStream;
import java.util.*;

class CgenNode extends class_ {
    /** The parent of this node in the inheritance tree */
    private CgenNode parent;

    /** The children of this node in the inheritance tree */
    private Vector children;

    /** Indicates a basic class */
    final static int Basic = 0;

    /** Indicates a class that came from a Cool program */
    final static int NotBasic = 1;
    
    /** Does this node correspond to a basic class? */
    private int basic_status;
    
    protected List<AbstractSymbol> attributes, methods;
    private Set<AbstractSymbol> _attributes, _methods;
    private int id;

    /** Constructs a new CgenNode to represent class "c".
     * @param c the class
     * @param basic_status is this class basic or not
     * @param table the class table
     * */
    CgenNode(Class_ c, int basic_status, CgenClassTable table) {
        super(0, c.getName(), c.getParent(), c.getFeatures(), c.getFilename());
        this.parent = null;
        this.children = new Vector();
        this.basic_status = basic_status;
        AbstractTable.stringtable.addString(name.getString());
    }
    
    void setId(int id) {
        this.id = id;
    }
    
    int getId() {
        return id;
    }

    void addChild(CgenNode child) {
        children.addElement(child);
    }

    /** Gets the children of this class
     * @return the children
     * */
    Enumeration getChildren() {
        return children.elements(); 
    }

    /** Sets the parent of this class.
     * @param parent the parent
     * */
    void setParentNd(CgenNode parent) {
        if (this.parent != null) {
            Utilities.fatalError("parent already set in CgenNode.setParent()");
        }
        if (parent == null) {
            Utilities.fatalError("null parent in CgenNode.setParent()");
        }
        this.parent = parent;
    }    
        

    /** Gets the parent of this class
     * @return the parent
     * */
    CgenNode getParentNd() {
        return parent; 
    }

    /** Returns true is this is a basic class.
     * @return true or false
     * */
    boolean basic() { 
        return basic_status == Basic; 
    }
    
    void buildLayoutIndex() {
        if (parent != null) parent.buildLayoutIndex();
        if (methods != null) return;
        
        attributes = new Vector<AbstractSymbol>();
        methods    = new Vector<AbstractSymbol>();
        
        _attributes = new HashSet<AbstractSymbol>();
        _methods    = new HashSet<AbstractSymbol>();
        
        if (parent != null) {
            for (AbstractSymbol a : parent.attributes) {
                attributes.add(a);
            }
            for (AbstractSymbol m : parent.methods) {
                methods.add(m);
            }
        }
        
        Feature feature;
        List<AbstractSymbol> list;
        Set<AbstractSymbol> _set;
        AbstractSymbol name;
        
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            feature = (Feature)e.nextElement();
            list    = null;
            _set    = null;
            name    = null;
            
            if (feature instanceof method) {
                list = methods;
                _set = _methods;
                name = ((method)feature).name;
                ((method)feature).calculateTemps();
            } else if (feature instanceof attr) {
                list = attributes;
                _set = _attributes;
                name = ((attr)feature).name;
            }
            if (list != null && list.indexOf(name) < 0) {
                list.add(name);
            }
            if (_set != null) _set.add(name);
        }
    }
    
    void codeDispatchTable(PrintStream s) {
        s.println(name + "_dispTab:");
        
        for (AbstractSymbol method : methods) {
            s.println(CgenSupport.WORD + classFor(method) + "." + method);
        }
    }
    
    AbstractSymbol classFor(AbstractSymbol method) {
        if (_methods.contains(method)) {
            return name;
        } else if (parent != null) {
            return parent.classFor(method);
        } else {
            return null;
        }
    }
    
    int methodOffset(AbstractSymbol methodName) {
        return methods.indexOf(methodName);
    }
    
    int attrOffset(AbstractSymbol attrName) {
        return 3 + attributes.indexOf(attrName);
    }
    
    void codeProtoObject(PrintStream s, IntTable intTable) {
        s.println(name + "_protObj:");
        s.println(CgenSupport.WORD + id);
        s.println(CgenSupport.WORD + (attributes.size() + 3));
        s.println(CgenSupport.WORD + name + "_dispTab");
        
        IntSymbol isym;
        
        if (name.equals(TreeConstants.Str)) {
            isym = (IntSymbol)intTable.addInt(0);
            s.print(CgenSupport.WORD) ; isym.codeRef(s) ; s.println("");
            s.println(CgenSupport.WORD + "0");
        }
        if (!name.equals(TreeConstants.Str)) {
            for (int i = 0; i < attributes.size(); i++) {
                s.println(CgenSupport.WORD + "0");
            }
        }
        
        s.println(CgenSupport.WORD + "-1");
    }
    
    void codeInit(PrintStream s, CgenClassTable classTable) {
        Feature feature;
        Enumeration e;
        CgenClassTable.Environment env;
        
        List<Integer> t = new Vector<Integer>();
        t.add(0);
        
        if (!basic()) {
            for (e = features.getElements(); e.hasMoreElements(); ) {
                feature = (Feature)e.nextElement();
                if (feature instanceof attr) {
                    t.add(1 + ((attr)feature).calculateTemps());
                }
            }
        }
        
        int temps     = Collections.max(t),
            frameSize = 12 + 4 * temps;
        
        s.println(name + "_init:");
        
        CgenSupport.emitAddiu("$sp", "$sp", -frameSize, s);
        
        CgenSupport.emitStore("$fp", temps + 3, "$sp", s);
        CgenSupport.emitStore("$ra", temps + 2, "$sp", s);
        CgenSupport.emitStore("$s0", temps + 1, "$sp", s);
        
        CgenSupport.emitAddiu("$fp", "$sp", 4, s);
        CgenSupport.emitMove("$s0", "$a0", s);
        
        if (!name.equals(TreeConstants.Object_)) {
            CgenSupport.emitJal(parent.name + "_init", s);
        }
        
        if (!basic()) {
            env = classTable.createEnv(name, TreeConstants.self, new Vector<AbstractSymbol>(), 3, temps, 0);
            
            for (e = features.getElements(); e.hasMoreElements(); ) {
                feature = (Feature)e.nextElement();
                if (feature instanceof attr) {
                    ((attr)feature).codeDefault(s, env);
                }
            }
            for (e = features.getElements(); e.hasMoreElements(); ) {
                feature = (Feature)e.nextElement();
                if (feature instanceof attr) {
                    ((attr)feature).codeInit(s, env);
                }
            }
        }
        
        CgenSupport.emitLoad("$fp", temps + 3, "$sp", s);
        CgenSupport.emitLoad("$ra", temps + 2, "$sp", s);
        CgenSupport.emitLoad("$s0", temps + 1, "$sp", s);
        
        CgenSupport.emitAddiu("$sp", "$sp", frameSize, s);
        CgenSupport.emitReturn(s);
    }
    
    void codeMethods(PrintStream s, CgenClassTable classTable) {
        codeInit(s, classTable);
        
        if (basic()) return;
        
        Feature feature;
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            feature = (Feature)e.nextElement();
            if (feature instanceof method) {
                ((method)feature).code(s, classTable, name);
            }
        }
    }
}
    

    
