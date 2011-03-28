// $ANTLR : "AnasintTrabajo.g" -> "Anasint.java"$

	import java.util.*;
	import antlr.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class Anasint extends antlr.LLkParser       implements AnasintTokenTypes
 {
		

protected Anasint(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected Anasint(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public Anasint(TokenStream lexer) {
  this(lexer,1);
}

public Anasint(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST program_AST = null;
		AST c_AST = null;
		AST d_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			cabecera();
			c_AST = (AST)returnAST;
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			match(PUNTO_Y_COMA);
			declaraciones_program();
			d_AST = (AST)returnAST;
			cuerpo_program();
			e_AST = (AST)returnAST;
			AST tmp2_AST = null;
			tmp2_AST = astFactory.create(LT(1));
			match(Token.EOF_TYPE);
			if ( inputState.guessing==0 ) {
				program_AST = (AST)currentAST.root;
				program_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PROGRAM,"program")).add(c_AST).add(d_AST).add(e_AST));
				currentAST.root = program_AST;
				currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
					program_AST.getFirstChild() : program_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = program_AST;
	}
	
	public final void cabecera() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_AST = null;
		
		try {      // for error handling
			match(PROGRAM);
			AST tmp4_AST = null;
			tmp4_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp4_AST);
			match(IDENT);
			cabecera_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_AST;
	}
	
	public final void declaraciones_program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_program_AST = null;
		
		try {      // for error handling
			lista_declaraciones_const();
			astFactory.addASTChild(currentAST, returnAST);
			lista_declaraciones_type();
			astFactory.addASTChild(currentAST, returnAST);
			lista_declaraciones_var();
			astFactory.addASTChild(currentAST, returnAST);
			lista_declaraciones_funcyproc();
			astFactory.addASTChild(currentAST, returnAST);
			declaraciones_program_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_program_AST;
	}
	
	public final void cuerpo_program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_program_AST = null;
		
		try {      // for error handling
			AST tmp5_AST = null;
			tmp5_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp5_AST);
			match(BEGIN);
			instrucciones();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp6_AST = null;
			tmp6_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp6_AST);
			match(END);
			cuerpo_program_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_program_AST;
	}
	
	public final void lista_declaraciones_const() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_declaraciones_const_AST = null;
		
		try {      // for error handling
			AST tmp7_AST = null;
			tmp7_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp7_AST);
			match(CONST);
			{
			int _cnt3897=0;
			_loop3897:
			do {
				if ((LA(1)==IDENT)) {
					declaracion_const();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3897>=1 ) { break _loop3897; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3897++;
			} while (true);
			}
			lista_declaraciones_const_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_declaraciones_const_AST;
	}
	
	public final void lista_declaraciones_type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_declaraciones_type_AST = null;
		
		try {      // for error handling
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp8_AST);
			match(TYPE);
			{
			int _cnt3900=0;
			_loop3900:
			do {
				if ((LA(1)==IDENT)) {
					declaracion_type();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3900>=1 ) { break _loop3900; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3900++;
			} while (true);
			}
			lista_declaraciones_type_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_declaraciones_type_AST;
	}
	
	public final void lista_declaraciones_var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_declaraciones_var_AST = null;
		
		try {      // for error handling
			AST tmp9_AST = null;
			tmp9_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp9_AST);
			match(VAR);
			{
			int _cnt3903=0;
			_loop3903:
			do {
				if ((LA(1)==IDENT)) {
					declaracion_var();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3903>=1 ) { break _loop3903; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3903++;
			} while (true);
			}
			lista_declaraciones_var_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_declaraciones_var_AST;
	}
	
	public final void lista_declaraciones_funcyproc() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_declaraciones_funcyproc_AST = null;
		
		try {      // for error handling
			{
			int _cnt3906=0;
			_loop3906:
			do {
				if ((LA(1)==FUNCTION)) {
					declaracion_funcyproc();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3906>=1 ) { break _loop3906; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3906++;
			} while (true);
			}
			lista_declaraciones_funcyproc_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_declaraciones_funcyproc_AST;
	}
	
	public final void declaracion_const() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_const_AST = null;
		
		try {      // for error handling
			AST tmp10_AST = null;
			tmp10_AST = astFactory.create(LT(1));
			match(IDENT);
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			match(IGUAL);
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			match(IDENT);
			AST tmp13_AST = null;
			tmp13_AST = astFactory.create(LT(1));
			match(PUNTO_Y_COMA);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_const_AST;
	}
	
	public final void declaracion_type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_type_AST = null;
		
		try {      // for error handling
			AST tmp14_AST = null;
			tmp14_AST = astFactory.create(LT(1));
			match(IDENT);
			AST tmp15_AST = null;
			tmp15_AST = astFactory.create(LT(1));
			match(IGUAL);
			AST tmp16_AST = null;
			tmp16_AST = astFactory.create(LT(1));
			match(RECORD);
			campos();
			AST tmp17_AST = null;
			tmp17_AST = astFactory.create(LT(1));
			match(END);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_type_AST;
	}
	
	public final void declaracion_var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_var_AST = null;
		
		try {      // for error handling
			variables();
			AST tmp18_AST = null;
			tmp18_AST = astFactory.create(LT(1));
			match(DOS_PUNTOS);
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			match(IDENT);
			AST tmp20_AST = null;
			tmp20_AST = astFactory.create(LT(1));
			match(PUNTO_Y_COMA);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_8);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_var_AST;
	}
	
	public final void declaracion_funcyproc() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_funcyproc_AST = null;
		
		try {      // for error handling
			if ((LA(1)==FUNCTION)) {
				funcion();
			}
			else if ((LA(1)==FUNCTION)) {
				procedimiento();
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_funcyproc_AST;
	}
	
	public final void instrucciones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instrucciones_AST = null;
		
		try {      // for error handling
			{
			_loop3931:
			do {
				if ((_tokenSet_10.member(LA(1)))) {
					instruccion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3931;
				}
				
			} while (true);
			}
			instrucciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = instrucciones_AST;
	}
	
	public final void campos() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST campos_AST = null;
		
		try {      // for error handling
			{
			int _cnt3912=0;
			_loop3912:
			do {
				if ((LA(1)==IDENT)) {
					campo();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3912>=1 ) { break _loop3912; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3912++;
			} while (true);
			}
			campos_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = campos_AST;
	}
	
	public final void campo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST campo_AST = null;
		
		try {      // for error handling
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp21_AST);
			match(IDENT);
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp22_AST);
			match(DOS_PUNTOS);
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp23_AST);
			match(IDENT);
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp24_AST);
			match(PUNTO_Y_COMA);
			campo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = campo_AST;
	}
	
	public final void variables() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variables_AST = null;
		
		try {      // for error handling
			{
			int _cnt3917=0;
			_loop3917:
			do {
				if ((LA(1)==IDENT)) {
					AST tmp25_AST = null;
					tmp25_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp25_AST);
					match(IDENT);
				}
				else {
					if ( _cnt3917>=1 ) { break _loop3917; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3917++;
			} while (true);
			}
			variables_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = variables_AST;
	}
	
	public final void funcion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST funcion_AST = null;
		
		try {      // for error handling
			cabecera_func();
			astFactory.addASTChild(currentAST, returnAST);
			declaraciones_func();
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo_func();
			astFactory.addASTChild(currentAST, returnAST);
			funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = funcion_AST;
	}
	
	public final void procedimiento() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST procedimiento_AST = null;
		
		try {      // for error handling
			cabecera_proc();
			astFactory.addASTChild(currentAST, returnAST);
			declaraciones_func();
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo_func();
			astFactory.addASTChild(currentAST, returnAST);
			procedimiento_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = procedimiento_AST;
	}
	
	public final void cabecera_func() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_func_AST = null;
		
		try {      // for error handling
			AST tmp26_AST = null;
			tmp26_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp26_AST);
			match(FUNCTION);
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(IDENT);
			parametros();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp28_AST = null;
			tmp28_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp28_AST);
			match(DOS_PUNTOS);
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp29_AST);
			match(IDENT);
			cabecera_func_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_func_AST;
	}
	
	public final void declaraciones_func() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_func_AST = null;
		
		try {      // for error handling
			declaracion_const();
			astFactory.addASTChild(currentAST, returnAST);
			declaracion_var();
			astFactory.addASTChild(currentAST, returnAST);
			declaraciones_func_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_func_AST;
	}
	
	public final void cuerpo_func() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_func_AST = null;
		
		try {      // for error handling
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp30_AST);
			match(BEGIN);
			instrucciones();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp31_AST);
			match(END);
			cuerpo_func_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_9);
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_func_AST;
	}
	
	public final void parametros() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parametros_AST = null;
		
		try {      // for error handling
			AST tmp32_AST = null;
			tmp32_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp32_AST);
			match(PARENTESIS_ABIERTO);
			parametro();
			astFactory.addASTChild(currentAST, returnAST);
			{
			int _cnt3923=0;
			_loop3923:
			do {
				if ((LA(1)==COMA)) {
					AST tmp33_AST = null;
					tmp33_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp33_AST);
					match(COMA);
					parametro();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt3923>=1 ) { break _loop3923; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3923++;
			} while (true);
			}
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp34_AST);
			match(PARENTESIS);
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp35_AST);
			match(CERRADO);
			parametros_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = parametros_AST;
	}
	
	public final void parametro() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parametro_AST = null;
		
		try {      // for error handling
			AST tmp36_AST = null;
			tmp36_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp36_AST);
			match(IDENT);
			AST tmp37_AST = null;
			tmp37_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp37_AST);
			match(DOS_PUNTOS);
			AST tmp38_AST = null;
			tmp38_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp38_AST);
			match(IDENT);
			parametro_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_16);
			} else {
			  throw ex;
			}
		}
		returnAST = parametro_AST;
	}
	
	public final void cabecera_proc() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_proc_AST = null;
		
		try {      // for error handling
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp39_AST);
			match(FUNCTION);
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(IDENT);
			parametros();
			astFactory.addASTChild(currentAST, returnAST);
			cabecera_proc_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_proc_AST;
	}
	
	public final void instruccion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_AST = null;
		AST i_AST = null;
		AST j_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				instruccion_simple();
				i_AST = (AST)returnAST;
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				match(PUNTO_Y_COMA);
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(i_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case IF:
			case WHILE:
			case FOR:
			{
				instruccion_compuesta();
				j_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					instruccion_AST = (AST)currentAST.root;
					instruccion_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCION,"instruccion")).add(j_AST));
					currentAST.root = instruccion_AST;
					currentAST.child = instruccion_AST!=null &&instruccion_AST.getFirstChild()!=null ?
						instruccion_AST.getFirstChild() : instruccion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_AST;
	}
	
	public final void instruccion_simple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_simple_AST = null;
		
		try {      // for error handling
			boolean synPredMatched3935 = false;
			if (((_tokenSet_18.member(LA(1))))) {
				int _m3935 = mark();
				synPredMatched3935 = true;
				inputState.guessing++;
				try {
					{
					expresion();
					match(ASIGNACION);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched3935 = false;
				}
				rewind(_m3935);
inputState.guessing--;
			}
			if ( synPredMatched3935 ) {
				asignacion();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==IDENT)) {
				llamada_metodo();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==IDENT)) {
				retorno();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_simple_AST;
	}
	
	public final void instruccion_compuesta() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_compuesta_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IF:
			{
				condicion();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case WHILE:
			{
				iteracion();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case FOR:
			{
				iteracion_acotada();
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_compuesta_AST;
	}
	
	public final void expresion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_AST = null;
		
		try {      // for error handling
			expresion_nivel_1();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop3952:
			do {
				if ((LA(1)==O)) {
					AST tmp42_AST = null;
					tmp42_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp42_AST);
					match(O);
					expresion_nivel_1();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3952;
				}
				
			} while (true);
			}
			expresion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_20);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_AST;
	}
	
	public final void asignacion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST asignacion_AST = null;
		AST e1_AST = null;
		AST e2_AST = null;
		
		try {      // for error handling
			expresion();
			e1_AST = (AST)returnAST;
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			match(ASIGNACION);
			expresion();
			e2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				asignacion_AST = (AST)currentAST.root;
				asignacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(tmp43_AST)).add(e1_AST).add(e2_AST));
				currentAST.root = asignacion_AST;
				currentAST.child = asignacion_AST!=null &&asignacion_AST.getFirstChild()!=null ?
					asignacion_AST.getFirstChild() : asignacion_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_21);
			} else {
			  throw ex;
			}
		}
		returnAST = asignacion_AST;
	}
	
	public final void llamada_metodo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST llamada_metodo_AST = null;
		AST i_AST = null;
		AST s_AST = null;
		
		try {      // for error handling
			acceso();
			i_AST = (AST)returnAST;
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			match(PARENTESIS_ABIERTO);
			lista_expresiones();
			s_AST = (AST)returnAST;
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			match(PARENTESIS_CERRADO);
			if ( inputState.guessing==0 ) {
				llamada_metodo_AST = (AST)currentAST.root;
				llamada_metodo_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LLAMADA,"llamada")).add(i_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPRESIONES,"expresiones")).add(s_AST))));
				currentAST.root = llamada_metodo_AST;
				currentAST.child = llamada_metodo_AST!=null &&llamada_metodo_AST.getFirstChild()!=null ?
					llamada_metodo_AST.getFirstChild() : llamada_metodo_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = llamada_metodo_AST;
	}
	
	public final void retorno() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST retorno_AST = null;
		
		try {      // for error handling
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			match(IDENT);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_19);
			} else {
			  throw ex;
			}
		}
		returnAST = retorno_AST;
	}
	
	public final void condicion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicion_AST = null;
		
		try {      // for error handling
			AST tmp47_AST = null;
			tmp47_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp47_AST);
			match(IF);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(THEN);
			contenido();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ELSE:
			{
				match(ELSE);
				contenido();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case IDENT:
			case END:
			case PARENTESIS_ABIERTO:
			case IF:
			case WHILE:
			case FOR:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			condicion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = condicion_AST;
	}
	
	public final void iteracion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_AST = null;
		
		try {      // for error handling
			AST tmp50_AST = null;
			tmp50_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp50_AST);
			match(WHILE);
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			match(DO);
			contenido();
			astFactory.addASTChild(currentAST, returnAST);
			iteracion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_AST;
	}
	
	public final void iteracion_acotada() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_acotada_AST = null;
		
		try {      // for error handling
			AST tmp52_AST = null;
			tmp52_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp52_AST);
			match(FOR);
			asignacion();
			astFactory.addASTChild(currentAST, returnAST);
			formato();
			astFactory.addASTChild(currentAST, returnAST);
			iteracion_acotada_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_acotada_AST;
	}
	
	public final void acceso() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_AST = null;
		Token  i1 = null;
		AST i1_AST = null;
		Token  i2 = null;
		AST i2_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			boolean synPredMatched3977 = false;
			if (((LA(1)==IDENT))) {
				int _m3977 = mark();
				synPredMatched3977 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(PUNTO);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched3977 = false;
				}
				rewind(_m3977);
inputState.guessing--;
			}
			if ( synPredMatched3977 ) {
				i1 = LT(1);
				i1_AST = astFactory.create(i1);
				match(IDENT);
				AST tmp53_AST = null;
				tmp53_AST = astFactory.create(LT(1));
				match(PUNTO);
				i2 = LT(1);
				i2_AST = astFactory.create(i2);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					acceso_AST = (AST)currentAST.root;
					acceso_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_OBJETO,"acceso_objeto")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ACCESO_SIMPLE,"acceso_simple")).add(i1_AST))).add(i2_AST));
					currentAST.root = acceso_AST;
					currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
						acceso_AST.getFirstChild() : acceso_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((LA(1)==IDENT)) {
				i = LT(1);
				i_AST = astFactory.create(i);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					acceso_AST = (AST)currentAST.root;
					acceso_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ACCESO_SIMPLE,"acceso_simple")).add(i_AST));
					currentAST.root = acceso_AST;
					currentAST.child = acceso_AST!=null &&acceso_AST.getFirstChild()!=null ?
						acceso_AST.getFirstChild() : acceso_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_23);
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_AST;
	}
	
	public final void lista_expresiones() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop3942:
				do {
					if ((LA(1)==COMA)) {
						match(COMA);
						expresion();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop3942;
					}
					
				} while (true);
				}
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			case PARENTESIS_CERRADO:
			{
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_24);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_AST;
	}
	
	public final void contenido() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST contenido_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case IDENT:
			case PARENTESIS_ABIERTO:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				instruccion_simple();
				astFactory.addASTChild(currentAST, returnAST);
				contenido_AST = (AST)currentAST.root;
				break;
			}
			case BEGIN:
			{
				bloque();
				astFactory.addASTChild(currentAST, returnAST);
				contenido_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = contenido_AST;
	}
	
	public final void formato() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formato_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TO:
			{
				AST tmp55_AST = null;
				tmp55_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp55_AST);
				match(TO);
				AST tmp56_AST = null;
				tmp56_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp56_AST);
				match(IDENT);
				AST tmp57_AST = null;
				tmp57_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp57_AST);
				match(DO);
				contenido();
				astFactory.addASTChild(currentAST, returnAST);
				formato_AST = (AST)currentAST.root;
				break;
			}
			case DOWNTO:
			{
				AST tmp58_AST = null;
				tmp58_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp58_AST);
				match(DOWNTO);
				AST tmp59_AST = null;
				tmp59_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp59_AST);
				match(IDENT);
				AST tmp60_AST = null;
				tmp60_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp60_AST);
				match(DO);
				contenido();
				astFactory.addASTChild(currentAST, returnAST);
				formato_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = formato_AST;
	}
	
	public final void bloque() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bloque_AST = null;
		AST i_AST = null;
		
		try {      // for error handling
			AST tmp61_AST = null;
			tmp61_AST = astFactory.create(LT(1));
			match(BEGIN);
			instrucciones();
			i_AST = (AST)returnAST;
			AST tmp62_AST = null;
			tmp62_AST = astFactory.create(LT(1));
			match(END);
			if ( inputState.guessing==0 ) {
				bloque_AST = (AST)currentAST.root;
				bloque_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTRUCCIONES,"instrucciones")).add(i_AST));
				currentAST.root = bloque_AST;
				currentAST.child = bloque_AST!=null &&bloque_AST.getFirstChild()!=null ?
					bloque_AST.getFirstChild() : bloque_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = bloque_AST;
	}
	
	public final void expresion_nivel_1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_1_AST = null;
		
		try {      // for error handling
			expresion_nivel_2();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop3955:
			do {
				if ((LA(1)==Y)) {
					AST tmp63_AST = null;
					tmp63_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp63_AST);
					match(Y);
					expresion_nivel_2();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3955;
				}
				
			} while (true);
			}
			expresion_nivel_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_26);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_1_AST;
	}
	
	public final void expresion_nivel_2() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_2_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NO:
			{
				AST tmp64_AST = null;
				tmp64_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp64_AST);
				match(NO);
				expresion_nivel_2();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_2_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				expresion_nivel_3();
				astFactory.addASTChild(currentAST, returnAST);
				expresion_nivel_2_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_2_AST;
	}
	
	public final void expresion_nivel_3() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_3_AST = null;
		
		try {      // for error handling
			expresion_nivel_4();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case IGUAL:
			case MAYOR:
			case MAYOR_IGUAL:
			case MENOR:
			case MENOR_IGUAL:
			case DISTINTO:
			{
				{
				switch ( LA(1)) {
				case MAYOR:
				{
					AST tmp65_AST = null;
					tmp65_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp65_AST);
					match(MAYOR);
					break;
				}
				case MAYOR_IGUAL:
				{
					AST tmp66_AST = null;
					tmp66_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp66_AST);
					match(MAYOR_IGUAL);
					break;
				}
				case MENOR:
				{
					AST tmp67_AST = null;
					tmp67_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp67_AST);
					match(MENOR);
					break;
				}
				case MENOR_IGUAL:
				{
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp68_AST);
					match(MENOR_IGUAL);
					break;
				}
				case IGUAL:
				{
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp69_AST);
					match(IGUAL);
					break;
				}
				case DISTINTO:
				{
					AST tmp70_AST = null;
					tmp70_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp70_AST);
					match(DISTINTO);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				expresion_nivel_4();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case PUNTO_Y_COMA:
			case IDENT:
			case END:
			case PARENTESIS_ABIERTO:
			case COMA:
			case ASIGNACION:
			case PARENTESIS_CERRADO:
			case IF:
			case THEN:
			case ELSE:
			case WHILE:
			case DO:
			case FOR:
			case TO:
			case DOWNTO:
			case O:
			case Y:
			case NO:
			case MENOS:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			case CORCHETE_CERRADO:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expresion_nivel_3_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_3_AST;
	}
	
	public final void expresion_nivel_4() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_4_AST = null;
		
		try {      // for error handling
			expresion_nivel_5();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop3963:
			do {
				if ((LA(1)==MAS||LA(1)==MENOS)) {
					{
					switch ( LA(1)) {
					case MAS:
					{
						AST tmp71_AST = null;
						tmp71_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp71_AST);
						match(MAS);
						break;
					}
					case MENOS:
					{
						AST tmp72_AST = null;
						tmp72_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp72_AST);
						match(MENOS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					expresion_nivel_5();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3963;
				}
				
			} while (true);
			}
			expresion_nivel_4_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_28);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_4_AST;
	}
	
	public final void expresion_nivel_5() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_5_AST = null;
		
		try {      // for error handling
			expresion_nivel_6();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop3967:
			do {
				if ((LA(1)==POR||LA(1)==DIVISION)) {
					{
					switch ( LA(1)) {
					case POR:
					{
						AST tmp73_AST = null;
						tmp73_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp73_AST);
						match(POR);
						break;
					}
					case DIVISION:
					{
						AST tmp74_AST = null;
						tmp74_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp74_AST);
						match(DIVISION);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					expresion_nivel_6();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3967;
				}
				
			} while (true);
			}
			expresion_nivel_5_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_5_AST;
	}
	
	public final void expresion_nivel_6() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_6_AST = null;
		AST i_AST = null;
		AST j_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MENOS:
			{
				AST tmp75_AST = null;
				tmp75_AST = astFactory.create(LT(1));
				match(MENOS);
				expresion_nivel_6();
				i_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					expresion_nivel_6_AST = (AST)currentAST.root;
					expresion_nivel_6_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MENOSUNARIO,"menosunario")).add(i_AST));
					currentAST.root = expresion_nivel_6_AST;
					currentAST.child = expresion_nivel_6_AST!=null &&expresion_nivel_6_AST.getFirstChild()!=null ?
						expresion_nivel_6_AST.getFirstChild() : expresion_nivel_6_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case IDENT:
			case PARENTESIS_ABIERTO:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case CIERTO:
			case FALSO:
			{
				expresion_nivel_7();
				j_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					expresion_nivel_6_AST = (AST)currentAST.root;
					expresion_nivel_6_AST=j_AST;
					currentAST.root = expresion_nivel_6_AST;
					currentAST.child = expresion_nivel_6_AST!=null &&expresion_nivel_6_AST.getFirstChild()!=null ?
						expresion_nivel_6_AST.getFirstChild() : expresion_nivel_6_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_nivel_6_AST;
	}
	
	public final void expresion_nivel_7() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_nivel_7_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PARENTESIS_ABIERTO:
			{
				match(PARENTESIS_ABIERTO);
				expresion();
				astFactory.addASTChild(currentAST, returnAST);
				match(PARENTESIS_CERRADO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp78_AST = null;
				tmp78_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp78_AST);
				match(LIT_ENTERO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_REAL:
			{
				AST tmp79_AST = null;
				tmp79_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp79_AST);
				match(LIT_REAL);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CAR:
			{
				AST tmp80_AST = null;
				tmp80_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp80_AST);
				match(LIT_CAR);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case CIERTO:
			{
				AST tmp81_AST = null;
				tmp81_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(CIERTO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			case FALSO:
			{
				AST tmp82_AST = null;
				tmp82_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp82_AST);
				match(FALSO);
				expresion_nivel_7_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched3971 = false;
				if (((LA(1)==IDENT))) {
					int _m3971 = mark();
					synPredMatched3971 = true;
					inputState.guessing++;
					try {
						{
						acceso();
						match(PARENTESIS_ABIERTO);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched3971 = false;
					}
					rewind(_m3971);
inputState.guessing--;
				}
				if ( synPredMatched3971 ) {
					llamada_metodo();
					astFactory.addASTChild(currentAST, returnAST);
					expresion_nivel_7_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched3973 = false;
					if (((LA(1)==IDENT))) {
						int _m3973 = mark();
						synPredMatched3973 = true;
						inputState.guessing++;
						try {
							{
							acceso();
							match(CORCHETE_ABIERTO);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched3973 = false;
						}
						rewind(_m3973);
inputState.guessing--;
					}
					if ( synPredMatched3973 ) {
						acceso_tabla();
						astFactory.addASTChild(currentAST, returnAST);
						expresion_nivel_7_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==IDENT)) {
						acceso();
						astFactory.addASTChild(currentAST, returnAST);
						expresion_nivel_7_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_22);
				} else {
				  throw ex;
				}
			}
			returnAST = expresion_nivel_7_AST;
		}
		
	public final void acceso_tabla() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_tabla_AST = null;
		AST c_AST = null;
		AST d_AST = null;
		
		try {      // for error handling
			acceso();
			c_AST = (AST)returnAST;
			AST tmp83_AST = null;
			tmp83_AST = astFactory.create(LT(1));
			match(CORCHETE_ABIERTO);
			lista_expresiones_nv();
			d_AST = (AST)returnAST;
			AST tmp84_AST = null;
			tmp84_AST = astFactory.create(LT(1));
			match(CORCHETE_CERRADO);
			if ( inputState.guessing==0 ) {
				acceso_tabla_AST = (AST)currentAST.root;
				acceso_tabla_AST=(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ACCESO_TABLA,"acceso_tabla")).add(c_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPRESIONES,"expresiones")).add(d_AST))));
				currentAST.root = acceso_tabla_AST;
				currentAST.child = acceso_tabla_AST!=null &&acceso_tabla_AST.getFirstChild()!=null ?
					acceso_tabla_AST.getFirstChild() : acceso_tabla_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_tabla_AST;
	}
	
	public final void lista_expresiones_nv() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_nv_AST = null;
		
		try {      // for error handling
			expresion();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop3980:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					expresion();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop3980;
				}
				
			} while (true);
			}
			lista_expresiones_nv_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_nv_AST;
	}
	
	public final void tipo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ENTERO:
			case REAL:
			case LOGICO:
			case CARACTER:
			{
				tipo_predefinido_simple();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case FORMACION:
			{
				tipo_predefinido_compuesto();
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				AST tmp86_AST = null;
				tmp86_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp86_AST);
				match(IDENT);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_AST;
	}
	
	public final void tipo_predefinido_simple() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_predefinido_simple_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ENTERO:
			{
				AST tmp87_AST = null;
				tmp87_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp87_AST);
				match(ENTERO);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case REAL:
			{
				AST tmp88_AST = null;
				tmp88_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp88_AST);
				match(REAL);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case LOGICO:
			{
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp89_AST);
				match(LOGICO);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			case CARACTER:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp90_AST);
				match(CARACTER);
				tipo_predefinido_simple_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_predefinido_simple_AST;
	}
	
	public final void tipo_predefinido_compuesto() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_predefinido_compuesto_AST = null;
		
		try {      // for error handling
			formacion();
			astFactory.addASTChild(currentAST, returnAST);
			tipo_predefinido_compuesto_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_predefinido_compuesto_AST;
	}
	
	public final void formacion() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formacion_AST = null;
		AST l_AST = null;
		AST t_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			AST tmp91_AST = null;
			tmp91_AST = astFactory.create(LT(1));
			match(FORMACION);
			lista_enteros();
			l_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case ENTERO:
			case REAL:
			case LOGICO:
			case CARACTER:
			{
				tipo_predefinido_simple();
				t_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					formacion_AST = (AST)currentAST.root;
					formacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(FORMACION,"formacion")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LISTA_ENTEROS,"lista_enteros")).add(l_AST))).add(t_AST));
					currentAST.root = formacion_AST;
					currentAST.child = formacion_AST!=null &&formacion_AST.getFirstChild()!=null ?
						formacion_AST.getFirstChild() : formacion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case IDENT:
			{
				i = LT(1);
				i_AST = astFactory.create(i);
				match(IDENT);
				if ( inputState.guessing==0 ) {
					formacion_AST = (AST)currentAST.root;
					formacion_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(FORMACION,"formacion")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LISTA_ENTEROS,"lista_enteros")).add(l_AST))).add(i_AST));
					currentAST.root = formacion_AST;
					currentAST.child = formacion_AST!=null &&formacion_AST.getFirstChild()!=null ?
						formacion_AST.getFirstChild() : formacion_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = formacion_AST;
	}
	
	public final void lista_enteros() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_enteros_AST = null;
		
		try {      // for error handling
			AST tmp92_AST = null;
			tmp92_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp92_AST);
			match(LIT_ENTERO);
			{
			_loop3988:
			do {
				if ((LA(1)==COMA)) {
					match(COMA);
					AST tmp94_AST = null;
					tmp94_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp94_AST);
					match(LIT_ENTERO);
				}
				else {
					break _loop3988;
				}
				
			} while (true);
			}
			lista_enteros_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = lista_enteros_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"EXPRESIONES",
		"INSTRUCCION",
		"INSTRUCCIONES",
		"MENOSUNARIO",
		"LLAMADA",
		"ACCESO_TABLA",
		"ACCESO_OBJETO",
		"ACCESO_SIMPLE",
		"LISTA_ENTEROS",
		"PUNTO_Y_COMA",
		"PROGRAM",
		"IDENT",
		"CONST",
		"TYPE",
		"VAR",
		"BEGIN",
		"END",
		"IGUAL",
		"RECORD",
		"DOS_PUNTOS",
		"FUNCTION",
		"PARENTESIS_ABIERTO",
		"COMA",
		"PARENTESIS",
		"CERRADO",
		"ASIGNACION",
		"PARENTESIS_CERRADO",
		"IF",
		"THEN",
		"ELSE",
		"WHILE",
		"DO",
		"FOR",
		"TO",
		"DOWNTO",
		"O",
		"Y",
		"NO",
		"MAYOR",
		"MAYOR_IGUAL",
		"MENOR",
		"MENOR_IGUAL",
		"DISTINTO",
		"MAS",
		"MENOS",
		"POR",
		"DIVISION",
		"CORCHETE_ABIERTO",
		"LIT_ENTERO",
		"LIT_REAL",
		"LIT_CAR",
		"CIERTO",
		"FALSO",
		"CORCHETE_CERRADO",
		"PUNTO",
		"ENTERO",
		"REAL",
		"LOGICO",
		"CARACTER",
		"FORMACION"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 8192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 524288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 131072L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 262144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 16777216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 163840L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 294912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 17334272L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 17301504L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 139895350528868352L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 1048576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1081344L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 8388608L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 32768L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 8421376L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 201326592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 139895350529916928L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 139895262482038784L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 139895359119859712L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 284010999845003264L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 139895771436720128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 285978575905005568L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 288230375718690816L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 1073741824L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 139895359119851520L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 284011549600817152L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 284012649112444928L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 284148988556386304L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 284289726044741632L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 144115188075855872L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 8646911284551385088L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	
	}
