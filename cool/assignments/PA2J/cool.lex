/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
    boolean string_valid = true;

    int comment_depth = 0;
    
    private int curr_lineno = 1;
    int get_curr_lineno() {
        return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
        filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
        return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    Symbol symbol;
    switch(yy_lexical_state) {
        case MCOMMENT:
            symbol = new Symbol(TokenConstants.ERROR, "EOF in comment");
            break;
        case STRING:
            symbol = new Symbol(TokenConstants.ERROR, "EOF in string constant");
            break;
        default:
            symbol = new Symbol(TokenConstants.EOF);
    }
    yybegin(YYINITIAL);
    return symbol;
%eofval}

%class CoolLexer
%cup

%states MCOMMENT, SCOMMENT, STRING
%ignorecase

%%

<YYINITIAL,MCOMMENT>[ \f\r\t\v] {}

<YYINITIAL>"--"                 {   yybegin(SCOMMENT); }
<SCOMMENT>.                     {}
<SCOMMENT>\n                    {   curr_lineno++;
                                    yybegin(YYINITIAL);
                                }

<YYINITIAL,MCOMMENT>"(*"        {   comment_depth++;
                                    yybegin(MCOMMENT);
                                }
<YYINITIAL,MCOMMENT>\n          {   curr_lineno++;  }
<MCOMMENT>\\.|.                 {}
<MCOMMENT>"*)"                  {   comment_depth--;
                                    if (comment_depth == 0) yybegin(YYINITIAL);
                                }
<YYINITIAL>"*)"                 {   return new Symbol(TokenConstants.ERROR, "Unmatched *)"); }

<YYINITIAL>"case"               {   return new Symbol(TokenConstants.CASE);     }
<YYINITIAL>"class"              {   return new Symbol(TokenConstants.CLASS);    }
<YYINITIAL>"else"               {   return new Symbol(TokenConstants.ELSE);     }
<YYINITIAL>"esac"               {   return new Symbol(TokenConstants.ESAC);     }
<YYINITIAL>"fi"                 {   return new Symbol(TokenConstants.FI);       }
<YYINITIAL>"if"                 {   return new Symbol(TokenConstants.IF);       }
<YYINITIAL>"in"                 {   return new Symbol(TokenConstants.IN);       }
<YYINITIAL>"inherits"           {   return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL>"isvoid"             {   return new Symbol(TokenConstants.ISVOID);   }
<YYINITIAL>"let"                {   return new Symbol(TokenConstants.LET);      }
<YYINITIAL>"loop"               {   return new Symbol(TokenConstants.LOOP);     }
<YYINITIAL>"new"                {   return new Symbol(TokenConstants.NEW);      }
<YYINITIAL>"not"                {   return new Symbol(TokenConstants.NOT);      }
<YYINITIAL>"of"                 {   return new Symbol(TokenConstants.OF);       }
<YYINITIAL>"pool"               {   return new Symbol(TokenConstants.POOL);     }
<YYINITIAL>"then"               {   return new Symbol(TokenConstants.THEN);     }
<YYINITIAL>"while"              {   return new Symbol(TokenConstants.WHILE);    }

<YYINITIAL>"false"|"true"       {   String text = yytext();
                                    String first = text.substring(0,1);
                                    if (first.toUpperCase().equals(first)) {
                                        AbstractTable.idtable.addString(text);
                                        return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.lookup(text));
                                    } else if (first.equals("t")) {
                                        return new Symbol(TokenConstants.BOOL_CONST, "true");
                                    } else {
                                        return new Symbol(TokenConstants.BOOL_CONST, "false");
                                    }
                                }

<YYINITIAL>"{"                  {   return new Symbol(TokenConstants.LBRACE);   }
<YYINITIAL>"}"                  {   return new Symbol(TokenConstants.RBRACE);   }
<YYINITIAL>"("                  {   return new Symbol(TokenConstants.LPAREN);   }
<YYINITIAL>")"                  {   return new Symbol(TokenConstants.RPAREN);   }
<YYINITIAL>":"                  {   return new Symbol(TokenConstants.COLON);    }
<YYINITIAL>";"                  {   return new Symbol(TokenConstants.SEMI);     }
<YYINITIAL>","                  {   return new Symbol(TokenConstants.COMMA);    }

<YYINITIAL>"."                  {   return new Symbol(TokenConstants.DOT);      }
<YYINITIAL>"~"                  {   return new Symbol(TokenConstants.NEG);      }
<YYINITIAL>"@"                  {   return new Symbol(TokenConstants.AT);       }
<YYINITIAL>"*"                  {   return new Symbol(TokenConstants.MULT);     }
<YYINITIAL>"/"                  {   return new Symbol(TokenConstants.DIV);      }
<YYINITIAL>"+"                  {   return new Symbol(TokenConstants.PLUS);     }
<YYINITIAL>"-"                  {   return new Symbol(TokenConstants.MINUS);    }
<YYINITIAL>"<="                 {   return new Symbol(TokenConstants.LE);       }
<YYINITIAL>"<"                  {   return new Symbol(TokenConstants.LT);       }
<YYINITIAL>"="                  {   return new Symbol(TokenConstants.EQ);       }
<YYINITIAL>"<-"                 {   return new Symbol(TokenConstants.ASSIGN);   }
<YYINITIAL>"=>"                 {   return new Symbol(TokenConstants.DARROW);   }

<YYINITIAL>[a-z][a-z0-9_]*      {   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }

<YYINITIAL>[0-9]+               {   AbstractTable.inttable.addString(yytext());
                                    return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.lookup(yytext()));
                                }

<YYINITIAL>\"                   {
                                    yybegin(STRING);
                                    string_buf = new StringBuffer();
                                    string_valid = true;
                                }
<STRING>\n                      {
                                    curr_lineno++;
                                    yybegin(YYINITIAL);
                                    return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
                                }
<STRING>\\(\n|.)                {
                                    String text = yytext();
                                    if (text.substring(1,2).equals("\n")) curr_lineno++;
                                    
                                    if (text.charAt(1) == 0)        string_valid = false;
                                    else if (text.equals("\\b"))    string_buf.append("\b");
                                    else if (text.equals("\\t"))    string_buf.append("\t");
                                    else if (text.equals("\\n"))    string_buf.append("\n");
                                    else if (text.equals("\\f"))    string_buf.append("\f");
                                    else
                                        string_buf.append(text.substring(1));
                                }
<STRING>\"                      {
                                    yybegin(YYINITIAL);
                                    String text = string_buf.toString();
                                    Symbol symbol;
                                    if (text.length() >= MAX_STR_CONST) {
                                        symbol = new Symbol(TokenConstants.ERROR, "String constant too long");
                                    } else if (string_valid) {
                                        AbstractTable.stringtable.addString(text);
                                        symbol = new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.lookup(text));
                                    } else {
                                        symbol = new Symbol(TokenConstants.ERROR, "String contains null character");
                                    }
                                    return symbol;
                                }
<STRING>\r|.                    {
                                    String text = yytext();
                                    if (text.charAt(0) == 0) string_valid = false;
                                    string_buf.append(text);
                                }

<YYINITIAL>.                    {   if (yytext().charAt(0) != 11)
                                        return new Symbol(TokenConstants.ERROR, yytext());
                                }

