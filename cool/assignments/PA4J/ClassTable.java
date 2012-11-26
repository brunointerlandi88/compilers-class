import java.io.PrintStream;
import java.util.*;

class Type {
    public AbstractSymbol name;
    public AbstractSymbol context;
    
    public static Type resolve(AbstractSymbol context, AbstractSymbol type) {
        if (type.equals(TreeConstants.SELF_TYPE)) {
            return new SelfType(type, context);
        } else {
            return new Type(type);
        }
    }
    
    Type(AbstractSymbol name_) {
        name = name_;
        context = name_;
    }
}

class SelfType extends Type {
    SelfType(AbstractSymbol name, AbstractSymbol ctx) {
        super(name);
        context = ctx;
    }
}

class Parameter {
    public AbstractSymbol name;
    public Type type;
    
    Parameter(AbstractSymbol name_, Type type_) {
        name = name_;
        type = type_;
    }
}

class Signature {
    public AbstractSymbol className;
    public AbstractSymbol name;
    public Vector<Parameter> params;
    public Type returnType;
    private method source;
   
    Signature(AbstractSymbol klass, method function) {
        className = klass;
        source = function;
        name = function.name;
        returnType = Type.resolve(klass, function.return_type);
        params = new Vector<Parameter>();
        
        formalc formal;
        Type type;
        
        for (Enumeration e = function.formals.getElements(); e.hasMoreElements(); ) {
            formal = (formalc)e.nextElement();
            type = Type.resolve(klass, formal.type_decl);
            params.add(new Parameter(formal.name, type));
        }
    }
}

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private Map<AbstractSymbol,class_c> classes;
    private Map<AbstractSymbol,AbstractSymbol> parents;
    private Map<AbstractSymbol,Map<AbstractSymbol,Signature>> methods;
    private Map<AbstractSymbol,Map<AbstractSymbol,Type>> attributes;
    private int semantErrors;
    private PrintStream errorStream;

    public static class Scope {
        private ClassTable classTable;
        private Scope parent;
        private Map<AbstractSymbol,Type> bindings;
        
        public static class UndefinedIdentifierError extends Exception {}
        
        Scope(ClassTable classTable_, Scope parent_) {
            classTable = classTable_;
            parent = parent_;
            bindings = new HashMap<AbstractSymbol,Type>();
        }
        
        public void declare(AbstractSymbol context, AbstractSymbol identifier, AbstractSymbol type) {
            bindings.put(identifier, Type.resolve(context, type));
        }
        
        public AbstractSymbol getType(AbstractSymbol identifier, AbstractSymbol context) throws UndefinedIdentifierError {
            Type local = resolve(identifier, context);
            AbstractSymbol type;
            
            if (identifier.equals(TreeConstants.self)) {
                type = TreeConstants.SELF_TYPE;
            } else if (local != null) {
                type = local.name;
            } else {
                type = classTable.getType(context, identifier);
            }
            
            if (type == null) {
                throw new UndefinedIdentifierError();
            } else {
                return type;
            }
        }
        
        private Type resolve(AbstractSymbol identifier, AbstractSymbol context) {
            Type type = bindings.get(identifier);
            if (type != null) {
                return type;
            } else if (parent != null) {
                return parent.resolve(identifier, context);
            } else {
                return null;
            }
        }
    }

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
        AbstractSymbol filename 
            = AbstractTable.stringtable.addString("<basic class>");
        
        // The following demonstrates how to create dummy parse trees to
        // refer to basic Cool classes.  There's no need for method
        // bodies -- these are already built into the runtime system.

        // IMPORTANT: The results of the following expressions are
        // stored in local variables.  You will want to do something
        // with those variables at the end of this method to make this
        // code meaningful.

        // The Object class has no parent class. Its methods are
        //        cool_abort() : Object    aborts the program
        //        type_name() : Str        returns a string representation 
        //                                 of class name
        //        copy() : SELF_TYPE       returns a copy of the object

        class_c Object_class = 
            new class_c(0, 
                       TreeConstants.Object_, 
                       TreeConstants.No_class,
                       new Features(0)
                           .appendElement(new method(0, 
                                              TreeConstants.cool_abort, 
                                              new Formals(0), 
                                              TreeConstants.Object_, 
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.type_name,
                                              new Formals(0),
                                              TreeConstants.Str,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.copy,
                                              new Formals(0),
                                              TreeConstants.SELF_TYPE,
                                              new no_expr(0))),
                       filename);
        
        // The IO class inherits from Object. Its methods are
        //        out_string(Str) : SELF_TYPE  writes a string to the output
        //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
        //        in_string() : Str            reads a string from the input
        //        in_int() : Int                "   an int     "  "     "

        class_c IO_class = 
            new class_c(0,
                       TreeConstants.IO,
                       TreeConstants.Object_,
                       new Features(0)
                           .appendElement(new method(0,
                                              TreeConstants.out_string,
                                              new Formals(0)
                                                  .appendElement(new formalc(0,
                                                                     TreeConstants.arg,
                                                                     TreeConstants.Str)),
                                              TreeConstants.SELF_TYPE,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.out_int,
                                              new Formals(0)
                                                  .appendElement(new formalc(0,
                                                                     TreeConstants.arg,
                                                                     TreeConstants.Int)),
                                              TreeConstants.SELF_TYPE,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.in_string,
                                              new Formals(0),
                                              TreeConstants.Str,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.in_int,
                                              new Formals(0),
                                              TreeConstants.Int,
                                              new no_expr(0))),
                       filename);

        // The Int class has no methods and only a single attribute, the
        // "val" for the integer.

        class_c Int_class = 
            new class_c(0,
                       TreeConstants.Int,
                       TreeConstants.Object_,
                       new Features(0)
                           .appendElement(new attr(0,
                                            TreeConstants.val,
                                            TreeConstants.prim_slot,
                                            new no_expr(0))),
                       filename);

        // Bool also has only the "val" slot.
        class_c Bool_class = 
            new class_c(0,
                       TreeConstants.Bool,
                       TreeConstants.Object_,
                       new Features(0)
                           .appendElement(new attr(0,
                                            TreeConstants.val,
                                            TreeConstants.prim_slot,
                                            new no_expr(0))),
                       filename);

        // The class Str has a number of slots and operations:
        //       val                              the length of the string
        //       str_field                        the string itself
        //       length() : Int                   returns length of the string
        //       concat(arg: Str) : Str           performs string concatenation
        //       substr(arg: Int, arg2: Int): Str substring selection

        class_c Str_class =
            new class_c(0,
                       TreeConstants.Str,
                       TreeConstants.Object_,
                       new Features(0)
                           .appendElement(new attr(0,
                                            TreeConstants.val,
                                            TreeConstants.Int,
                                            new no_expr(0)))
                           .appendElement(new attr(0,
                                            TreeConstants.str_field,
                                            TreeConstants.prim_slot,
                                            new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.length,
                                              new Formals(0),
                                              TreeConstants.Int,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.concat,
                                              new Formals(0)
                                                  .appendElement(new formalc(0,
                                                                     TreeConstants.arg, 
                                                                     TreeConstants.Str)),
                                              TreeConstants.Str,
                                              new no_expr(0)))
                           .appendElement(new method(0,
                                              TreeConstants.substr,
                                              new Formals(0)
                                                  .appendElement(new formalc(0,
                                                                     TreeConstants.arg,
                                                                     TreeConstants.Int))
                                                  .appendElement(new formalc(0,
                                                                     TreeConstants.arg2,
                                                                     TreeConstants.Int)),
                                              TreeConstants.Str,
                                              new no_expr(0))),
                       filename);

        visit(Object_class);
        visit(IO_class);
        visit(Int_class);
        visit(Bool_class);
        visit(Str_class);
    }
    
    public ClassTable(Classes cls) {
        semantErrors = 0;
        errorStream = System.err;
        
        classes = new HashMap<AbstractSymbol,class_c>();
        parents = new HashMap<AbstractSymbol,AbstractSymbol>();
        methods = new HashMap<AbstractSymbol,Map<AbstractSymbol,Signature>>();
        attributes = new HashMap<AbstractSymbol,Map<AbstractSymbol,Type>>();
        
        installBasicClasses();
        
        for (Enumeration e = cls.getElements(); e.hasMoreElements(); ) {
            visit((class_c)e.nextElement());
        }
    }
    
    public void validate() {
        AbstractSymbol parent;
        class_c source;
        
        for (AbstractSymbol klass : classes.keySet()) {
            source = classes.get(klass);
            parent = parents.get(klass);
            
            if (parent.equals(TreeConstants.Int) ||
                parent.equals(TreeConstants.Bool) ||
                parent.equals(TreeConstants.Str) ||
                parent.equals(TreeConstants.SELF_TYPE)
               ) {
                semantError(source);
                errorStream.println("filename:line");
            }
            
            if (!klass.equals(TreeConstants.Object_) && !classes.containsKey(parent)) {
                semantError(source);
                errorStream.println("Class " + klass + " inherits from an undefined class " + parent + ".");
            }
        }
        
        if (!classes.containsKey(TreeConstants.Main)) {
            semantError();
            errorStream.println("Class Main is not defined.");
        }
    }
    
    public void visit(class_c klass) {
        Feature feature;
        if (classes.containsKey(klass.name)) {
            semantError(klass);
            errorStream.println("filename:line");
        } else {
            classes.put(klass.name, klass);
            parents.put(klass.name, klass.parent);
            methods.put(klass.name, new HashMap<AbstractSymbol,Signature>());
            attributes.put(klass.name, new HashMap<AbstractSymbol,Type>());
            
            for (Enumeration e = klass.features.getElements(); e.hasMoreElements(); ) {
                feature = (Feature)e.nextElement();
                if (feature instanceof method) {
                    visit(klass.name, (method)feature);
                } else if (feature instanceof attr) {
                    visit(klass.name, (attr)feature);
                }
            }
        }
    }
    
    public void visit(AbstractSymbol klass, method function) {
        methods.get(klass).put(function.name, new Signature(klass, function));
    }
    
    public void visit(AbstractSymbol klass, attr attribute) {
        attributes.get(klass).put(attribute.name, Type.resolve(klass, attribute.type_decl));
    }
    
    public AbstractSymbol getType(AbstractSymbol klass, AbstractSymbol recvType, AbstractSymbol methodName, Vector<AbstractSymbol> argTypes) {
        Signature signature = null;
        Map<AbstractSymbol,Signature> mTable;
        
        AbstractSymbol origRecvType = recvType;
        recvType = Type.resolve(klass, recvType).context;
        
        while (recvType != null) {
            mTable = methods.get(recvType);
            if (mTable != null) {
                signature = mTable.get(methodName);
            }
            if (signature != null) {
                break;
            }
            if (recvType.equals(TreeConstants.Object_)) {
                recvType = null;
            } else {
                recvType = parents.get(recvType);
            }
        }
        
        if (signature == null) {
            return null;
        } else {
            return Type.resolve(origRecvType, signature.returnType.name).context;
        }
    }
    
    public AbstractSymbol getType(AbstractSymbol klass, AbstractSymbol identifier) {
        Type attr = attributes.get(klass).get(identifier);
        if (attr == null) {
            return null;
        } else {
            return attr.name;
        }
    }
    
    public AbstractSymbol getLUB(AbstractSymbol t1, AbstractSymbol t2) {
        Vector<AbstractSymbol> ancestors = getAncestors(t1);
        
        while (ancestors.indexOf(t2) < 0 && t2 != null) {
            t2 = parents.get(t2);
        }
        return t2;
    }
    
    private Vector<AbstractSymbol> getAncestors(AbstractSymbol type) {
        Vector<AbstractSymbol> ancestors = new Vector<AbstractSymbol>();
        
        while (!type.equals(TreeConstants.Object_)) {
            ancestors.add(type);
            type = parents.get(type);
        }
        ancestors.add(TreeConstants.Object_);
        return ancestors;
    }
    
    public boolean isSubtype(AbstractSymbol context, AbstractSymbol t1, AbstractSymbol t2) {
        AbstractSymbol r1 = Type.resolve(context, t1).context,
                       r2 = Type.resolve(context, t2).context;
        
        Vector<AbstractSymbol> ancestors = getAncestors(r1);
        return ancestors.indexOf(r2) >= 0;
    }

    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_c c) {
        return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
        errorStream.print(filename + ":" + t.getLineNumber() + ": ");
        return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
        semantErrors++;
        return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
        return semantErrors != 0;
    }
}
                          
    
