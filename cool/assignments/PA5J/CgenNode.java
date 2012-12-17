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
    
    private List<AbstractSymbol> attrIndex, methIndex;
    private Set<AbstractSymbol>  attrs, methods;
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
    
    void buildLayoutIndex(List<AbstractSymbol> attributes, List<AbstractSymbol> methods) {
        if (parent != null) {
            parent.buildLayoutIndex(attributes, methods);
        }
        
        Feature feature;
        List<AbstractSymbol> list;
        AbstractSymbol name;
        
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            feature = (Feature)e.nextElement();
            list = null;
            name = null;
            
            if (feature instanceof method) {
                list = methods;
                name = ((method)feature).name;
            } else if (feature instanceof attr) {
                list = attributes;
                name = ((attr)feature).name;
            }
            if (list != null && list.indexOf(name) < 0) {
                list.add(name);
            }
        }
    }
    
    void buildLayoutIndex() {
        attrs   = new HashSet<AbstractSymbol>();
        methods = new HashSet<AbstractSymbol>();
        
        Feature feature;
        Set<AbstractSymbol> set;
        AbstractSymbol name;
        
        for (Enumeration e = features.getElements(); e.hasMoreElements(); ) {
            feature = (Feature)e.nextElement();
            set  = null;
            name = null;
            
            if (feature instanceof method) {
                set  = methods;
                name = ((method)feature).name;
            } else if (feature instanceof attr) {
                set  = attrs;
                name = ((attr)feature).name;
            }
            if (set != null) {
                set.add(name);
            }
        }
        
        attrIndex = new Vector<AbstractSymbol>();
        methIndex = new Vector<AbstractSymbol>();
        
        buildLayoutIndex(attrIndex, methIndex);
    }
    
    void codeDispatchTable(PrintStream s) {
        s.println(name + "_dispTab:");
        
        for (AbstractSymbol method : methIndex) {
            s.println(CgenSupport.WORD + classFor(method) + "." + method);
        }
    }
    
    AbstractSymbol classFor(AbstractSymbol method) {
        if (methods.contains(method)) {
            return name;
        } else if (parent != null) {
            return parent.classFor(method);
        } else {
            return null;
        }
    }
    
    void codeProtoObject(PrintStream s, Map<AbstractSymbol,Integer> classIds, IntTable intTable) {
        s.println(name + "_protObj:");
        s.println(CgenSupport.WORD + classIds.get(name));
        s.println(CgenSupport.WORD + (attrIndex.size() + 3));
        s.println(CgenSupport.WORD + name + "_dispTab");
        
        IntSymbol isym;
        
        if (name.equals(TreeConstants.Str)) {
            isym = (IntSymbol)intTable.addInt(0);
            s.print(CgenSupport.WORD) ; isym.codeRef(s) ; s.println("");
            s.println(CgenSupport.WORD + "0");
        }
        if (!name.equals(TreeConstants.Str)) {
            for (int i = 0; i < attrIndex.size(); i++) {
                s.println(CgenSupport.WORD + "0");
            }
        }
        
        s.println(CgenSupport.WORD + "-1");
    }
    
    void codeInit(PrintStream s) {
        s.println(name + "_init:");
        CgenSupport.emitMove("$fp", "$sp", s);
        CgenSupport.emitPush("$ra", s);
        if (!name.equals(TreeConstants.Object_)) {
            CgenSupport.emitPush("$fp", s);
            CgenSupport.emitJal(parent.name + "_init", s);
        }
        // TODO initialize attributes
        CgenSupport.emitLoad("$ra", 1, "$sp", s);
        CgenSupport.emitAddiu("$sp", "$sp", 8, s);
        CgenSupport.emitLoad("$fp", 0, "$sp", s);
        CgenSupport.emitReturn(s);
    }
    
    void codeMethods(PrintStream s) {
        if (basic_status == 0) return;
        
        for (AbstractSymbol method : methods) {
            codeMethod(s, method);
        }
    }
    
    void codeMethod(PrintStream s, AbstractSymbol method) {
        s.println(name + "." + method + ":");
        CgenSupport.emitMove("$fp", "$sp", s);
        CgenSupport.emitPush("$ra", s);
        
        CgenSupport.emitLoad("$ra", 1, "$sp", s);
        CgenSupport.emitAddiu("$sp", "$sp", 8, s);
        CgenSupport.emitLoad("$fp", 0, "$sp", s);
        CgenSupport.emitReturn(s);
    }
}
    

    
