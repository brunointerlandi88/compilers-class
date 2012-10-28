/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 3;
	private final int YYINITIAL = 0;
	private final int SCOMMENT = 2;
	private final int MCOMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		56,
		61,
		64
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"3:9,4,5,3,4,1,3:18,4,3,43,3:5,6,9,7,36,31,2,32,35,42:10,29,30,37,38,39,3,34" +
",11,40,10,23,13,15,40,18,16,40:2,14,40,17,22,24,40,19,12,20,26,21,25,40:3,3" +
",8,3:2,41,3,11,40,10,23,13,15,40,18,16,40:2,14,40,17,22,24,40,19,12,20,26,2" +
"1,25,40:3,27,3,28,33,3,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,108,
"0,1:2,2,1:2,3,4,1,5,1:10,6,7,8,1:4,9:2,10,9,1:3,9:14,1:8,11,9,12,3,13,14,15" +
",16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40" +
",41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,9,56,57,58,59")[0];

	private int yy_nxt[][] = unpackFromString(60,44,
"1,2,3,4,2,5,6,7,4,8,9,103:2,104,81,58,62,82,103:2,105,57,65,103,106,107,103" +
",10,11,12,13,14,15,16,17,18,19,20,21,4,103,4,22,23,-1:46,24,-1:48,25,-1:45," +
"26,-1:44,103,83,103:2,84,103:12,-1:13,103:3,-1:3,31,-1:35,32,-1:44,33,-1:46" +
",22,-1:11,103:17,-1:13,103:3,-1:11,103:8,97,103:8,-1:13,103:3,-1,1,2,48:2,2" +
",5,59,63,66,48:12,2,48:22,-1:10,103,88,103:4,27,103:10,-1:13,103:3,-1:3,55:" +
"42,1,-1,50:3,51,50:38,-1:10,103:2,89,103:2,28,103,29,103:9,-1:13,103:3,-1:1" +
"0,49,-1:34,1,52:4,53,52:2,60,52:34,54,-1:10,103:5,30,103:11,-1:13,103:3,-1:" +
"3,48:3,-1,48:38,-1:10,103:10,34,103:6,-1:13,103:3,-1:11,103:15,35,103,-1:13" +
",103:3,-1:11,103:10,36,103:6,-1:13,103:3,-1:11,103:3,37,103:13,-1:13,103:3," +
"-1:11,38,103:16,-1:13,103:3,-1:11,103:3,39,103:13,-1:13,103:3,-1:11,103:14," +
"40,103:2,-1:13,103:3,-1:11,103:7,41,103:9,-1:13,103:3,-1:11,103:3,42,103:13" +
",-1:13,103:3,-1:11,103:4,43,103:12,-1:13,103:3,-1:11,103:2,44,103:14,-1:13," +
"103:3,-1:11,103:3,45,103:13,-1:13,103:3,-1:11,103:13,46,103:3,-1:13,103:3,-" +
"1:11,103:2,47,103:14,-1:13,103:3,-1:11,103:3,67,103:8,87,103:4,-1:13,103:3," +
"-1:11,103:3,68,103:8,69,103:4,-1:13,103:3,-1:11,103:2,70,103:14,-1:13,103:3" +
",-1:11,103,94,103:15,-1:13,103:3,-1:11,103,71,103:15,-1:13,103:3,-1:11,103:" +
"2,72,103:14,-1:13,103:3,-1:11,103:12,73,103:4,-1:13,103:3,-1:11,103:4,95,10" +
"3:12,-1:13,103:3,-1:11,103:11,96,103:5,-1:13,103:3,-1:11,103:3,74,103:13,-1" +
":13,103:3,-1:11,103:16,75,-1:13,103:3,-1:11,103:12,76,103:4,-1:13,103:3,-1:" +
"11,103:6,98,103:10,-1:13,103:3,-1:11,103:2,77,103:14,-1:13,103:3,-1:11,103:" +
"2,75,103:14,-1:13,103:3,-1:11,103:12,99,103:4,-1:13,103:3,-1:11,103:3,100,1" +
"03:13,-1:13,103:3,-1:11,103:4,78,103:12,-1:13,103:3,-1:11,103:6,79,103:10,-" +
"1:13,103:3,-1:11,103:9,101,103:7,-1:13,103:3,-1:11,103:6,102,103:10,-1:13,1" +
"03:3,-1:11,103:10,80,103:6,-1:13,103:3,-1:11,103:2,85,103,86,103:12,-1:13,1" +
"03:3,-1:11,103:8,90,91,103:7,-1:13,103:3,-1:11,103:12,92,103:4,-1:13,103:3," +
"-1:11,103:8,93,103:8,-1:13,103:3,-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{}
					case -3:
						break;
					case 3:
						{   return new Symbol(TokenConstants.MINUS);    }
					case -4:
						break;
					case 4:
						{   if (yytext().charAt(0) != 11)
                                        return new Symbol(TokenConstants.ERROR, yytext());
                                }
					case -5:
						break;
					case 5:
						{   curr_lineno++;  }
					case -6:
						break;
					case 6:
						{   return new Symbol(TokenConstants.LPAREN);   }
					case -7:
						break;
					case 7:
						{   return new Symbol(TokenConstants.MULT);     }
					case -8:
						break;
					case 8:
						{   return new Symbol(TokenConstants.RPAREN);   }
					case -9:
						break;
					case 9:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -10:
						break;
					case 10:
						{   return new Symbol(TokenConstants.LBRACE);   }
					case -11:
						break;
					case 11:
						{   return new Symbol(TokenConstants.RBRACE);   }
					case -12:
						break;
					case 12:
						{   return new Symbol(TokenConstants.COLON);    }
					case -13:
						break;
					case 13:
						{   return new Symbol(TokenConstants.SEMI);     }
					case -14:
						break;
					case 14:
						{   return new Symbol(TokenConstants.COMMA);    }
					case -15:
						break;
					case 15:
						{   return new Symbol(TokenConstants.DOT);      }
					case -16:
						break;
					case 16:
						{   return new Symbol(TokenConstants.NEG);      }
					case -17:
						break;
					case 17:
						{   return new Symbol(TokenConstants.AT);       }
					case -18:
						break;
					case 18:
						{   return new Symbol(TokenConstants.DIV);      }
					case -19:
						break;
					case 19:
						{   return new Symbol(TokenConstants.PLUS);     }
					case -20:
						break;
					case 20:
						{   return new Symbol(TokenConstants.LT);       }
					case -21:
						break;
					case 21:
						{   return new Symbol(TokenConstants.EQ);       }
					case -22:
						break;
					case 22:
						{   AbstractTable.inttable.addString(yytext());
                                    return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.lookup(yytext()));
                                }
					case -23:
						break;
					case 23:
						{
                                    yybegin(STRING);
                                    string_buf = new StringBuffer();
                                    string_valid = true;
                                }
					case -24:
						break;
					case 24:
						{   yybegin(SCOMMENT); }
					case -25:
						break;
					case 25:
						{   comment_depth++;
                                    yybegin(MCOMMENT);
                                }
					case -26:
						break;
					case 26:
						{   return new Symbol(TokenConstants.ERROR, "Unmatched *)"); }
					case -27:
						break;
					case 27:
						{   return new Symbol(TokenConstants.FI);       }
					case -28:
						break;
					case 28:
						{   return new Symbol(TokenConstants.IF);       }
					case -29:
						break;
					case 29:
						{   return new Symbol(TokenConstants.IN);       }
					case -30:
						break;
					case 30:
						{   return new Symbol(TokenConstants.OF);       }
					case -31:
						break;
					case 31:
						{   return new Symbol(TokenConstants.ASSIGN);   }
					case -32:
						break;
					case 32:
						{   return new Symbol(TokenConstants.LE);       }
					case -33:
						break;
					case 33:
						{   return new Symbol(TokenConstants.DARROW);   }
					case -34:
						break;
					case 34:
						{   return new Symbol(TokenConstants.LET);      }
					case -35:
						break;
					case 35:
						{   return new Symbol(TokenConstants.NEW);      }
					case -36:
						break;
					case 36:
						{   return new Symbol(TokenConstants.NOT);      }
					case -37:
						break;
					case 37:
						{   return new Symbol(TokenConstants.CASE);     }
					case -38:
						break;
					case 38:
						{   return new Symbol(TokenConstants.ESAC);     }
					case -39:
						break;
					case 39:
						{   return new Symbol(TokenConstants.ELSE);     }
					case -40:
						break;
					case 40:
						{   return new Symbol(TokenConstants.LOOP);     }
					case -41:
						break;
					case 41:
						{   return new Symbol(TokenConstants.THEN);     }
					case -42:
						break;
					case 42:
						{   String text = yytext();
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
					case -43:
						break;
					case 43:
						{   return new Symbol(TokenConstants.POOL);     }
					case -44:
						break;
					case 44:
						{   return new Symbol(TokenConstants.CLASS);    }
					case -45:
						break;
					case 45:
						{   return new Symbol(TokenConstants.WHILE);    }
					case -46:
						break;
					case 46:
						{   return new Symbol(TokenConstants.ISVOID);   }
					case -47:
						break;
					case 47:
						{   return new Symbol(TokenConstants.INHERITS); }
					case -48:
						break;
					case 48:
						{}
					case -49:
						break;
					case 49:
						{   comment_depth--;
                                    if (comment_depth == 0) yybegin(YYINITIAL);
                                }
					case -50:
						break;
					case 50:
						{}
					case -51:
						break;
					case 51:
						{   curr_lineno++;
                                    yybegin(YYINITIAL);
                                }
					case -52:
						break;
					case 52:
						{
                                    String text = yytext();
                                    if (text.charAt(0) == 0) string_valid = false;
                                    string_buf.append(text);
                                }
					case -53:
						break;
					case 53:
						{
                                    curr_lineno++;
                                    yybegin(YYINITIAL);
                                    return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
                                }
					case -54:
						break;
					case 54:
						{
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
					case -55:
						break;
					case 55:
						{
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
					case -56:
						break;
					case 57:
						{}
					case -57:
						break;
					case 58:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -58:
						break;
					case 59:
						{}
					case -59:
						break;
					case 60:
						{
                                    String text = yytext();
                                    if (text.charAt(0) == 0) string_valid = false;
                                    string_buf.append(text);
                                }
					case -60:
						break;
					case 62:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -61:
						break;
					case 63:
						{}
					case -62:
						break;
					case 65:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -63:
						break;
					case 66:
						{}
					case -64:
						break;
					case 67:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -65:
						break;
					case 68:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -66:
						break;
					case 69:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -67:
						break;
					case 70:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -68:
						break;
					case 71:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -69:
						break;
					case 72:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -70:
						break;
					case 73:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -71:
						break;
					case 74:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -72:
						break;
					case 75:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -73:
						break;
					case 76:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -74:
						break;
					case 77:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -75:
						break;
					case 78:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -76:
						break;
					case 79:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -77:
						break;
					case 80:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -78:
						break;
					case 81:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -79:
						break;
					case 82:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -80:
						break;
					case 83:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -81:
						break;
					case 84:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -82:
						break;
					case 85:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -83:
						break;
					case 86:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -84:
						break;
					case 87:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -85:
						break;
					case 88:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -86:
						break;
					case 89:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -87:
						break;
					case 90:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -88:
						break;
					case 91:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -89:
						break;
					case 92:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -90:
						break;
					case 93:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -91:
						break;
					case 94:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -92:
						break;
					case 95:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -93:
						break;
					case 96:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -94:
						break;
					case 97:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -95:
						break;
					case 98:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -96:
						break;
					case 99:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -97:
						break;
					case 100:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -98:
						break;
					case 101:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -99:
						break;
					case 102:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -100:
						break;
					case 103:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -101:
						break;
					case 104:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -102:
						break;
					case 105:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -103:
						break;
					case 106:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -104:
						break;
					case 107:
						{   String text = yytext();
                                    String first = text.substring(0,1);
                                    int type;
                                    if (first.toLowerCase().equals(first))
                                        type = TokenConstants.OBJECTID;
                                    else
                                        type = TokenConstants.TYPEID;
                                    AbstractTable.idtable.addString(text);
                                    return new Symbol(type, AbstractTable.idtable.lookup(text));
                                }
					case -105:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
