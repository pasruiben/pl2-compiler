// $ANTLR : "Anasem.g" -> "Anasem.java"$

	import java.util.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class Anasem extends antlr.TreeParser       implements AnasemTokenTypes
 {

	JPanel jContentPanel = new JPanel();
	
	//As
	//Asigna tipo, Lval y Rval segn el tipo de declaracin del acceso simple
	Atr_Expr AS_Acceso_Simple(AST dec) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST t = null;
				
		switch(dec.getType())
		{
			case VARIABLE_RESULTADO: case VARIABLE:
				t = dec.getFirstChild().getNextSibling();
				if (t.getType() == RANGO)
					t = ajustaTipoRango(t);
				res.setLval(true);
				res.setRval(true);
				break;
			case CONSTANTE:
				t = expresion(dec.getFirstChild().getNextSibling()).getTipo();
				res.setLval(false);
				res.setRval(true);
				break;
			case PROCEDIMIENTO: case FUNCION:
				t = dec.getFirstChild();
				res.setLval(false);
				res.setRval(false);
				break;
			case PARAMETRO:
				t = dec.getFirstChild().getNextSibling();
				if (t.getType() == RANGO)
					t = ajustaTipoRango(t);
				res.setLval(false);
				res.setRval(true);
				break;			
			case REGISTRO:
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, ACCESO SIMPLE AL NOMBRE DE UN REGISTRO",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;
			default:
				System.out.println("Acceso simple no contemplado");
		}
		
		res.setTipo(t);

		return res;
	}
	
	//Anndices sean de tipo entero
	Atr_Expr AS_Acceso_Array(Atr_Expr atr_raiz, AST indices) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST t = null, aux;
		
		if (atr_raiz.getTipo().getType() != ARRAY)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A ARRAY SOBRE ALGO QUE NO ES UN ARRAY",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		aux = indices.getFirstChild();
		
		if (indices.getNumberOfChildren() != atr_raiz.getTipo().getFirstChild().getNumberOfChildren())
		{
			JOptionPane.showMessageDialog(
								jContentPanel,
								" ERROR, EL NUMERO DE INDICES EN EL ACCESO A ARRAY NO ES EL ADECUADO",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
							System.exit(1); 	
		}
		
		while(aux != null)
		{		
			if(expresion(aux).getTipo().getType() != INTEGER)
			{
				JOptionPane.showMessageDialog(
									jContentPanel,
									" ERROR, ALGUNO DE LOS NDICES EN EL ACCESO A ARRAY NO ES ENTERO",
									"Error",
									JOptionPane.INFORMATION_MESSAGE);
 				System.exit(1); 	
			}
			
			aux = aux.getNextSibling();
		}

		//tipo base
		t = atr_raiz.getTipo().getFirstChild().getNextSibling();
		if (t.getType() == RANGO)
			t = ajustaTipoRango(t);
		
		res.setTipo(t);
		
		//lval y rval los hereda de la ra
		res.setLval(atr_raiz.getLval());
		res.setRval(atr_raiz.getRval());

		return res;
	}
	
	//Recibe un tipo rango (entero o caracter) y devuelve entero o caracter
	AST ajustaTipoRango(AST tipo_rango)
	{
		if (tipo_rango.getFirstChild().getType() == LIT_CAR)
			return (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(CHAR,"CHAR")));
		else
			return (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INTEGER,"INTEGER")));
	}
	
	//Anntico de los accesos a registros
	//Comprueba que la rasea de tipo registro
	//Comprueba que existe el campo al que se pretende acceder
	//Realiza las llamadas recursivas si el acceso contina
	Atr_Expr AS_Acceso_Registro(Atr_Expr atr_raiz, AST acceso) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		AST aux, t = null, campo = null;
		
		switch(acceso.getType())
		{
			case ACCESO_REGISTRO: case ACCESO_ARRAY_REGISTRO: case ACCESO_ARRAY:
				campo = acceso.getFirstChild();
				break;
			case IDENT:
				campo = acceso;
				break;
		}
		
		if (atr_raiz.getTipo().getType() != REGISTRO)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A REGISTRO SOBRE ALGO QUE NO ES UN REGISTRO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		aux = atr_raiz.getTipo().getFirstChild().getNextSibling();

		while (aux != null)
		{
			//si encuentra el campo
			if (aux.getFirstChild().equals(campo))
				break;
			
			aux = aux.getNextSibling();
		}
		
		if (aux == null)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, ACCESO A CAMPO " + campo.getText() + " INEXISTENTE EN EL REGISTRO " + atr_raiz.getTipo().getFirstChild().getText(),
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		t = aux.getFirstChild().getNextSibling(); //el tipo del campo
		
		if (t.getType() == RANGO)
			t = ajustaTipoRango(t);
		
		res.setTipo(t);
		res.setLval(true);
		res.setRval(true);
		
		//llamadas recursivas si no era un caso base
		switch(acceso.getType())
		{
			case ACCESO_ARRAY:
				res = AS_Acceso_Array(res, acceso.getFirstChild().getNextSibling());
				break;
			case ACCESO_REGISTRO:
				res = AS_Acceso_Registro(res, acceso.getFirstChild().getNextSibling());
				break;
			case ACCESO_ARRAY_REGISTRO: 
				res = AS_Acceso_Array_Registro(res, acceso.getFirstChild().getNextSibling(), acceso.getFirstChild().getNextSibling().getNextSibling());
				break;			
		}

		return res;
	}
	
	//Antico de los accesos a array seguidos de acceso a registro
	Atr_Expr AS_Acceso_Array_Registro(Atr_Expr atr_raiz, AST indices, AST acceso) throws RecognitionException
	{
		Atr_Expr res = new Atr_Expr();
		
		res = AS_Acceso_Array(atr_raiz, indices);
		
		res = AS_Acceso_Registro(res, acceso);
		
		return res;
	}
	
	//Funcin auxiliar que devuelve el tipo de un AST representando a un literal
	AST tipoLiteral(AST li)
	{
		AST tipo = null;				
		
		switch (li.getType())
		{
			case LIT_ENTERO: tipo = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INTEGER,"INTEGER"))); break;
			case LIT_REAL: tipo = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(REAL,"REAL"))); break;
			case LIT_CAR: tipo = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(CHAR,"CHAR"))); break;
			case CADENA: tipo = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(STRING,"STRING"))); break;
			case TRUE: case FALSE: tipo = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(BOOLEAN,"BOOLEAN"))); break;
			default: System.out.println("tipoLiteral sin tipo conocido");		
		}
		
		return tipo;
	}
	
	//Anntico de literales
	Atr_Expr AS_Literal(AST li)
	{
		Atr_Expr result = new Atr_Expr();
		
		result.setTipo(tipoLiteral(li));
		result.setLval(false);
		result.setRval(true);
		
		return result;
	}
	
	//A
	//Comprueba que e1 tiene Lval
	//Comprueba que e2 tiene Rval
	//Comprueba equivalencia de tipos
	void AS_Asignacion(Atr_Expr e1, Atr_Expr e2)
	{
		if (!e1.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESION A LA IZQUIERDA EN LA ASIGNACION NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		if (!e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESINo A LA DERECHA EN LA ASIGNACIiN NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			
			
		if (!tiposEquivalentes(e1.getTipo(), e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA ASIGNACIN NO TIENEN IGUAL TIPO, UNA ES DE TIPO " + e1.getTipo().getText() + " Y LA OTRA DE TIPO " + e2.getTipo().getText(),
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//Funci.n auxiliar para comprobar equivalencia de tipos
	boolean tiposEquivalentes(AST tipo1, AST tipo2)
	{
		//por nombre
		if (!tipo1.equals(tipo2))
			return false;
		
		//si son registros, por nombre del registro
		if (tipo1.getType() == REGISTRO)
			return tipo1.getFirstChild().equals(tipo2.getFirstChild());
			
		return true;
	}
	
	//An.lisis sem.ntico de la cabecera del for
	//Comprueba que a tenga Lval
	//Comprueba que e1 y e2 tengan Rval
	//Comprueba que a, e1 y e2 sean de tipo Integer
	void AS_Cabecera_For(Atr_Expr a, Atr_Expr e1, Atr_Expr e2)
	{
		if (!a.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, EL ACCESO EN LA CABECERA DEL FOR NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		if (!e1.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA PRIMERA EXPRESI.N EN LA CABECERA DEL FOR NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			
			
		if (!e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA SEGUNDA EXPRESI.N EN LA CABECERA DEL FOR NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
			
		if (a.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, EL ACCESO EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		if (e1.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA PRIMERA EXPRESI.N EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		if (e2.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA SEGUNDA EXPRESI.N EN LA CABECERA DEL FOR NO ES DE TIPO ENTERO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//An.lisis sem.ntico de las llamadas a rutinas
	//Comprueba que la ra.z sea de tipo cabecera
	//Comprueba que los par.metros reales se correspondan con los formales
	Atr_Expr AS_Llamada(Atr_Expr atr_raiz, LinkedList expresiones)
	{
		Atr_Expr res = new Atr_Expr();		
		Atr_Expr par_r; //par.metro real
		AST par_f, tipo; //par.metro formal
		Iterator it = expresiones.iterator();	
		
		if (atr_raiz.getTipo().getType() != CABECERA)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, SE HACE UNA LLAMADA SOBRE ALGO QUE NO ES UNA RUTINA",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
		
		String nombreRutina = atr_raiz.getTipo().getFirstChild().getText();		
		
		//nos situamos en el primer par.metro formal
		par_f = atr_raiz.getTipo().getFirstChild().getNextSibling().getFirstChild();
		
		while (par_f != null && it.hasNext())
		{
			par_r = (Atr_Expr)it.next();
			
			AST tipo_r = par_r.getTipo();
			if (tipo_r.getType() == RANGO)
				tipo_r = ajustaTipoRango(tipo_r);
			
			AST tipo_f = par_f.getFirstChild().getNextSibling();
			if (tipo_f.getType() == RANGO)
				tipo_f = ajustaTipoRango(tipo_f);
			
			if (!tipo_r.equals(tipo_f))
			{
	 			JOptionPane.showMessageDialog(
						jContentPanel,
						"ERROR, LOS PAR.METROS FORMALES NO COINCIDEN CON LOS REALES EN TIPO EN LA LLAMADA A " + nombreRutina,
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
	 		 	System.exit(1);
			}
			
			par_f = par_f.getNextSibling();
		}
		
		if (par_f != null && !it.hasNext())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA LLAMADA NO TIENE SUFICIENTES PAR.METROS REALES",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (par_f == null && it.hasNext())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA LLAMADA TIENE DEMASIADOS PAR.METROS REALES",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		tipo = atr_raiz.getTipo().getFirstChild().getNextSibling().getNextSibling();
		if (tipo.getType() == RANGO)
			tipo = ajustaTipoRango(tipo);
		
		res.setTipo(tipo);
		res.setLval(false);
		res.setRval(true);
		
		return res;
	}
	
	//An.lisis sem.ntico de la instrucci.n de entrada
	//Comprueba que la expresi.n tenga LVal
	void AS_Entrada(Atr_Expr e)
	{
		if (!e.getLval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN 'READ' NO TIENE LVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
	}
	
	//An.lisis sem.ntico de la instrucci.n de salida
	//Comprueba que las expresiones tengan Rval
	void AS_Salida(LinkedList l)
	{
		Iterator it = l.iterator();
		Atr_Expr aux;
		
		while(it.hasNext())
		{
			aux = (Atr_Expr)it.next();
			
			if (!aux.getRval())
			{
	 			JOptionPane.showMessageDialog(
						jContentPanel,
						"ERROR, ALGUNA EXPRESI.N EN 'WRITE' NO TIENE RVAL",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
	 		 	System.exit(1);
			}
		}
		
	}
		
	//An.lisis sem.ntico de las condiciones
	//Comprueba que e sea de tipo Boolean
	void AS_Condicion(Atr_Expr e)
	{
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA CONDICION NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (e.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA CONDICION NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
	}
	
	//An.lisis sem.ntico de las expresiones binarias aritm.ticas
	//Comprueba que las dos expresiones tengan Rval
	//Comprueba (por nombre) la igualdad de tipos de e1 y e2
	//Comprueba que el tipo sea integer, real o string
	//Si es string comprueba que la operaci.n sea la de concatenaci.n
	//Si son operadores que s.lo admiten un tipo comprueba que el tipo sea el correcto
	Atr_Expr AS_Exp_Bin_Arit(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_ARIT CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (!e1.getTipo().equals(e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_ARIT CON OPERADOR " + op.getText() + " NO TIENEN IGUAL TIPO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		if (e1.getTipo().getType() != INTEGER && e1.getTipo().getType() != REAL && e1.getTipo().getType() != STRING)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_ARIT NO SON NI DE TIPO ENTERO NI REAL NI CADENA",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (e1.getTipo().getType() == STRING && op.getType() != MAS)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, A LAS EXPRESIONES DE TIPO CADENA S.LO SE LES PUEDE APLICAR EL OPERADOR +",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		switch(op.getType())
		{
			case DIVISION_REAL:
				if (e1.getTipo().getType() != REAL)
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, EL OPERADOR " + op.getText() + " NO ES APLICABLE A EXPRESIONES DE TIPO DISTINTO A REAL",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;
			case DIVISION_ENTERA: case MOD:
				if (e1.getTipo().getType() != INTEGER)
				{
		 			JOptionPane.showMessageDialog(
							jContentPanel,
							"ERROR, EL OPERADOR " + op.getText() + " NO ES APLICABLE A EXPRESIONES DE TIPO DISTINTO A ENTERO",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
		 		 	System.exit(1);
				}
				break;			
		}
			
		result.setTipo(e1.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}
		
	//An.lisis sem.ntico de las expresiones binarias l.gicas
	//Comprueba que ambas sean de tipo boolean
	Atr_Expr AS_Exp_Bin_Log(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_LOG CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		if (e1.getTipo().getType() != BOOLEAN || e2.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_LOG CON OPERADOR " + op.getText() + " NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
			
		result.setTipo(e1.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}	
	
	//An.lisis sem.ntico de las expresiones binarias relacionales
	//Comprueba que e1 y e2 tengan rval
	//Comprueba que e1 y e2 sean de tipo integer, real, char o string
	//Comprueba que e1 y e2 sean del mismo tipo
	Atr_Expr AS_Exp_Bin_Rel(Atr_Expr e1, Atr_Expr e2, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e1.getRval() || !e2.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, UNA DE LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}

		if (!e1.getTipo().equals(e2.getTipo()))
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENEN IGUAL TIPO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}												
		
		if (e1.getTipo().getType() != INTEGER && e1.getTipo().getType() != REAL && e1.getTipo().getType() != CHAR && e1.getTipo().getType() != STRING)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LAS EXPRESIONES EN LA EXPR_BIN_REL CON OPERADOR " + op.getText() + " NO TIENEN TIPO ADECUADO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
		
		result.setTipo((AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(BOOLEAN,"BOOLEAN"))));
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}		
	
	//An.lisis sem.ntico de las expresiones unarias aritm.ticas
	//Comprueba que e sea de tipo integer o real
	Atr_Expr AS_Exp_Una_Arit(Atr_Expr e, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_ARIT CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}			

		if (e.getTipo().getType() != INTEGER && e.getTipo().getType() != REAL)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_ARIT CON OPERADOR " + op.getText() + " NO ES DE TIPO INTEGER NI REAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		result.setTipo(e.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}		
	
	//An.lisis sem.ntico de la expresi.n unaria l.gica
	//Comprueba que e sea de tipo boolean
	Atr_Expr AS_Exp_Una_Log(Atr_Expr e, AST op)
	{
		Atr_Expr result = new Atr_Expr();
		
		if (!e.getRval())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_LOG CON OPERADOR " + op.getText() + " NO TIENE RVAL",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}						

		if (e.getTipo().getType() != BOOLEAN)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN LA EXPR_UNA_LOG CON OPERADOR " + op.getText() + " NO ES DE TIPO BOOLEAN",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
			
		result.setTipo(e.getTipo());
		result.setLval(false);
		result.setRval(true);	
		
		return result;	
	}	
	
	//An.lisis sem.ntico de los registros
	//Comprueba que no haya varios campos con el mismo nombre
	void AS_Registro(LinkedList nombresCampos)
	{
		Set set = new HashSet();
		set.addAll(nombresCampos);
		
		if (nombresCampos.size() != set.size())
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, HAY CAMPOS CON IGUAL NOMBRE EN UN MISMO REGISTRO",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}	
	}
	
	//An.lisis sem.ntico de los extremos de los rangos enteros
	//Comprueba que ambas sean de tipo entero
	void AS_Rango_Entero(Atr_Expr limite_inferior, Atr_Expr limite_superior)
	{
		if (limite_inferior.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN EL EXTREMO INFERIOR DEL RANGO NO ES DE TIPO INTEGER",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}
		
		if (limite_superior.getTipo().getType() != INTEGER)
		{
 			JOptionPane.showMessageDialog(
					jContentPanel,
					"ERROR, LA EXPRESI.N EN EL EXTREMO SUPERIOR DEL RANGO NO ES DE TIPO INTEGER",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
 		 	System.exit(1);
		}		
	}
public Anasem() {
	tokenNames = _tokenNames;
}

	public final void programa(AST _t) throws RecognitionException {
		
		AST programa_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST programa_AST = null;
		
		try {      // for error handling
			AST __t4147 = _t;
			AST tmp1_AST = null;
			AST tmp1_AST_in = null;
			tmp1_AST = astFactory.create((AST)_t);
			tmp1_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp1_AST);
			ASTPair __currentAST4147 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PROGRAMA);
			_t = _t.getFirstChild();
			cabecera(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			declaraciones(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4147;
			_t = __t4147;
			_t = _t.getNextSibling();
			programa_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = programa_AST;
		_retTree = _t;
	}
	
	public final void cabecera(AST _t) throws RecognitionException {
		
		AST cabecera_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_AST = null;
		
		try {      // for error handling
			AST tmp2_AST = null;
			AST tmp2_AST_in = null;
			tmp2_AST = astFactory.create((AST)_t);
			tmp2_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp2_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			cabecera_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_AST;
		_retTree = _t;
	}
	
	public final void declaraciones(AST _t) throws RecognitionException {
		
		AST declaraciones_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaraciones_AST = null;
		
		try {      // for error handling
			AST __t4150 = _t;
			AST tmp3_AST = null;
			AST tmp3_AST_in = null;
			tmp3_AST = astFactory.create((AST)_t);
			tmp3_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp3_AST);
			ASTPair __currentAST4150 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DECLARACIONES);
			_t = _t.getFirstChild();
			dec_constantes(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_registros(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_variables(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_rutinas(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4150;
			_t = __t4150;
			_t = _t.getNextSibling();
			declaraciones_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = declaraciones_AST;
		_retTree = _t;
	}
	
	public final void cuerpo(AST _t) throws RecognitionException {
		
		AST cuerpo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_AST = null;
		
		try {      // for error handling
			AST __t4195 = _t;
			AST tmp4_AST = null;
			AST tmp4_AST_in = null;
			tmp4_AST = astFactory.create((AST)_t);
			tmp4_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp4_AST);
			ASTPair __currentAST4195 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CUERPO);
			_t = _t.getFirstChild();
			{
			_loop4197:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					instruccion(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4197;
				}
				
			} while (true);
			}
			currentAST = __currentAST4195;
			_t = __t4195;
			_t = _t.getNextSibling();
			cuerpo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_AST;
		_retTree = _t;
	}
	
	public final void dec_constantes(AST _t) throws RecognitionException {
		
		AST dec_constantes_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_constantes_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CONST:
			{
				AST __t4152 = _t;
				AST tmp5_AST = null;
				AST tmp5_AST_in = null;
				tmp5_AST = astFactory.create((AST)_t);
				tmp5_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp5_AST);
				ASTPair __currentAST4152 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,CONST);
				_t = _t.getFirstChild();
				{
				int _cnt4154=0;
				_loop4154:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CONSTANTE)) {
						dec_constante(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4154>=1 ) { break _loop4154; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4154++;
				} while (true);
				}
				currentAST = __currentAST4152;
				_t = __t4152;
				_t = _t.getNextSibling();
				dec_constantes_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			case TYPE:
			case VAR:
			case RUTINAS:
			{
				dec_constantes_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_constantes_AST;
		_retTree = _t;
	}
	
	public final void dec_registros(AST _t) throws RecognitionException {
		
		AST dec_registros_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_registros_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPE:
			{
				AST __t4158 = _t;
				AST tmp6_AST = null;
				AST tmp6_AST_in = null;
				tmp6_AST = astFactory.create((AST)_t);
				tmp6_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp6_AST);
				ASTPair __currentAST4158 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,TYPE);
				_t = _t.getFirstChild();
				{
				int _cnt4160=0;
				_loop4160:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==REGISTRO)) {
						dec_registro(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4160>=1 ) { break _loop4160; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4160++;
				} while (true);
				}
				currentAST = __currentAST4158;
				_t = __t4158;
				_t = _t.getNextSibling();
				dec_registros_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			case VAR:
			case RUTINAS:
			{
				dec_registros_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_registros_AST;
		_retTree = _t;
	}
	
	public final void dec_variables(AST _t) throws RecognitionException {
		
		AST dec_variables_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_variables_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR:
			{
				AST __t4168 = _t;
				AST tmp7_AST = null;
				AST tmp7_AST_in = null;
				tmp7_AST = astFactory.create((AST)_t);
				tmp7_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp7_AST);
				ASTPair __currentAST4168 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,VAR);
				_t = _t.getFirstChild();
				{
				int _cnt4170=0;
				_loop4170:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==VARIABLE)) {
						dec_variable(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4170>=1 ) { break _loop4170; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4170++;
				} while (true);
				}
				currentAST = __currentAST4168;
				_t = __t4168;
				_t = _t.getNextSibling();
				dec_variables_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			case RUTINAS:
			{
				dec_variables_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_variables_AST;
		_retTree = _t;
	}
	
	public final void dec_rutinas(AST _t) throws RecognitionException {
		
		AST dec_rutinas_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_rutinas_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case RUTINAS:
			{
				AST __t4174 = _t;
				AST tmp8_AST = null;
				AST tmp8_AST_in = null;
				tmp8_AST = astFactory.create((AST)_t);
				tmp8_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp8_AST);
				ASTPair __currentAST4174 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,RUTINAS);
				_t = _t.getFirstChild();
				{
				int _cnt4176=0;
				_loop4176:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==PROCEDIMIENTO||_t.getType()==FUNCION)) {
						dec_rutina(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4176>=1 ) { break _loop4176; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4176++;
				} while (true);
				}
				currentAST = __currentAST4174;
				_t = __t4174;
				_t = _t.getNextSibling();
				dec_rutinas_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			{
				dec_rutinas_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_rutinas_AST;
		_retTree = _t;
	}
	
	public final void dec_constante(AST _t) throws RecognitionException {
		
		AST dec_constante_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_constante_AST = null;
		Atr_Expr e1;
		
		try {      // for error handling
			AST __t4156 = _t;
			AST tmp9_AST = null;
			AST tmp9_AST_in = null;
			tmp9_AST = astFactory.create((AST)_t);
			tmp9_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp9_AST);
			ASTPair __currentAST4156 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CONSTANTE);
			_t = _t.getFirstChild();
			AST tmp10_AST = null;
			AST tmp10_AST_in = null;
			tmp10_AST = astFactory.create((AST)_t);
			tmp10_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp10_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			e1=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4156;
			_t = __t4156;
			_t = _t.getNextSibling();
			dec_constante_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_constante_AST;
		_retTree = _t;
	}
	
	public final Atr_Expr  expresion(AST _t) throws RecognitionException {
		Atr_Expr res = null;
		
		AST expresion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_AST = null;
		AST op1 = null;
		AST op1_AST = null;
		AST op2 = null;
		AST op2_AST = null;
		AST op3 = null;
		AST op3_AST = null;
		AST op4 = null;
		AST op4_AST = null;
		AST op5 = null;
		AST op5_AST = null;
		AST op6 = null;
		AST op6_AST = null;
		AST op7 = null;
		AST op7_AST = null;
		AST op8 = null;
		AST op8_AST = null;
		AST op9 = null;
		AST op9_AST = null;
		AST op10 = null;
		AST op10_AST = null;
		AST op11 = null;
		AST op11_AST = null;
		AST op12 = null;
		AST op12_AST = null;
		AST op13 = null;
		AST op13_AST = null;
		AST op14 = null;
		AST op14_AST = null;
		AST op15 = null;
		AST op15_AST = null;
		AST op16 = null;
		AST op16_AST = null;
		AST i = null;
		AST i_AST = null;
		AST j = null;
		AST j_AST = null;
		AST k = null;
		AST k_AST = null;
		AST m = null;
		AST m_AST = null;
		AST n = null;
		AST n_AST = null;
		AST o = null;
		AST o_AST = null;
		Atr_Expr e1, e2; LinkedList l;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OR:
			{
				AST __t4230 = _t;
				op1 = _t==ASTNULL ? null :(AST)_t;
				AST op1_AST_in = null;
				op1_AST = astFactory.create(op1);
				astFactory.addASTChild(currentAST, op1_AST);
				ASTPair __currentAST4230 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,OR);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4230;
				_t = __t4230;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Log(e1,e2,op1);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case AND:
			{
				AST __t4231 = _t;
				op2 = _t==ASTNULL ? null :(AST)_t;
				AST op2_AST_in = null;
				op2_AST = astFactory.create(op2);
				astFactory.addASTChild(currentAST, op2_AST);
				ASTPair __currentAST4231 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,AND);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4231;
				_t = __t4231;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Log(e1,e2,op2);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case NOT:
			{
				AST __t4232 = _t;
				op3 = _t==ASTNULL ? null :(AST)_t;
				AST op3_AST_in = null;
				op3_AST = astFactory.create(op3);
				astFactory.addASTChild(currentAST, op3_AST);
				ASTPair __currentAST4232 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4232;
				_t = __t4232;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Una_Log(e1,op3);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MAYOR:
			{
				AST __t4233 = _t;
				op4 = _t==ASTNULL ? null :(AST)_t;
				AST op4_AST_in = null;
				op4_AST = astFactory.create(op4);
				astFactory.addASTChild(currentAST, op4_AST);
				ASTPair __currentAST4233 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAYOR);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4233;
				_t = __t4233;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op4);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MAYOR_IGUAL:
			{
				AST __t4234 = _t;
				op5 = _t==ASTNULL ? null :(AST)_t;
				AST op5_AST_in = null;
				op5_AST = astFactory.create(op5);
				astFactory.addASTChild(currentAST, op5_AST);
				ASTPair __currentAST4234 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAYOR_IGUAL);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4234;
				_t = __t4234;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op5);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MENOR:
			{
				AST __t4235 = _t;
				op6 = _t==ASTNULL ? null :(AST)_t;
				AST op6_AST_in = null;
				op6_AST = astFactory.create(op6);
				astFactory.addASTChild(currentAST, op6_AST);
				ASTPair __currentAST4235 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MENOR);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4235;
				_t = __t4235;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op6);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MENOR_IGUAL:
			{
				AST __t4236 = _t;
				op7 = _t==ASTNULL ? null :(AST)_t;
				AST op7_AST_in = null;
				op7_AST = astFactory.create(op7);
				astFactory.addASTChild(currentAST, op7_AST);
				ASTPair __currentAST4236 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MENOR_IGUAL);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4236;
				_t = __t4236;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op7);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case IGUAL:
			{
				AST __t4237 = _t;
				op8 = _t==ASTNULL ? null :(AST)_t;
				AST op8_AST_in = null;
				op8_AST = astFactory.create(op8);
				astFactory.addASTChild(currentAST, op8_AST);
				ASTPair __currentAST4237 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IGUAL);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4237;
				_t = __t4237;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op8);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case DISTINTO:
			{
				AST __t4238 = _t;
				op9 = _t==ASTNULL ? null :(AST)_t;
				AST op9_AST_in = null;
				op9_AST = astFactory.create(op9);
				astFactory.addASTChild(currentAST, op9_AST);
				ASTPair __currentAST4238 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DISTINTO);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4238;
				_t = __t4238;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Rel(e1,e2,op9);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MAS:
			{
				AST __t4239 = _t;
				op10 = _t==ASTNULL ? null :(AST)_t;
				AST op10_AST_in = null;
				op10_AST = astFactory.create(op10);
				astFactory.addASTChild(currentAST, op10_AST);
				ASTPair __currentAST4239 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAS);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4239;
				_t = __t4239;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Arit(e1,e2,op10);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case POR:
			{
				AST __t4245 = _t;
				op13 = _t==ASTNULL ? null :(AST)_t;
				AST op13_AST_in = null;
				op13_AST = astFactory.create(op13);
				astFactory.addASTChild(currentAST, op13_AST);
				ASTPair __currentAST4245 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,POR);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4245;
				_t = __t4245;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Arit(e1,e2,op13);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case DIVISION_ENTERA:
			{
				AST __t4246 = _t;
				op14 = _t==ASTNULL ? null :(AST)_t;
				AST op14_AST_in = null;
				op14_AST = astFactory.create(op14);
				astFactory.addASTChild(currentAST, op14_AST);
				ASTPair __currentAST4246 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIVISION_ENTERA);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4246;
				_t = __t4246;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Arit(e1,e2,op14);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case DIVISION_REAL:
			{
				AST __t4247 = _t;
				op15 = _t==ASTNULL ? null :(AST)_t;
				AST op15_AST_in = null;
				op15_AST = astFactory.create(op15);
				astFactory.addASTChild(currentAST, op15_AST);
				ASTPair __currentAST4247 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIVISION_REAL);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4247;
				_t = __t4247;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Arit(e1,e2,op15);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case MOD:
			{
				AST __t4248 = _t;
				op16 = _t==ASTNULL ? null :(AST)_t;
				AST op16_AST_in = null;
				op16_AST = astFactory.create(op16);
				astFactory.addASTChild(currentAST, op16_AST);
				ASTPair __currentAST4248 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MOD);
				_t = _t.getFirstChild();
				e1=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				e2=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4248;
				_t = __t4248;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Exp_Bin_Arit(e1,e2,op16);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			{
				AST __t4249 = _t;
				AST tmp11_AST = null;
				AST tmp11_AST_in = null;
				tmp11_AST = astFactory.create((AST)_t);
				tmp11_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp11_AST);
				ASTPair __currentAST4249 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LLAMADA);
				_t = _t.getFirstChild();
				e1=acceso(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				l=lista_expresiones(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4249;
				_t = __t4249;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					
								res = AS_Llamada(e1,l); //hay que comprobar que sea un m.todo funcional
							
								if (res.getTipo().getType() == VACIO)
								{
									JOptionPane.showMessageDialog(
										jContentPanel,
										"ERROR, LA LLAMADA DENTRO DE UNA EXPRESI.N NO CORRESPONDE A UN M.TODO FUNCIONAL",
										"Error",
										JOptionPane.INFORMATION_MESSAGE);
							 		System.exit(1);
								}																					
							
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_SIMPLE:
			case ACCESO_REGISTRO:
			case ACCESO_ARRAY_REGISTRO:
			case ACCESO_ARRAY:
			{
				e1=acceso(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					res = e1;
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				i = (AST)_t;
				AST i_AST_in = null;
				i_AST = astFactory.create(i);
				astFactory.addASTChild(currentAST, i_AST);
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(i);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case LIT_REAL:
			{
				j = (AST)_t;
				AST j_AST_in = null;
				j_AST = astFactory.create(j);
				astFactory.addASTChild(currentAST, j_AST);
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(j);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CAR:
			{
				k = (AST)_t;
				AST k_AST_in = null;
				k_AST = astFactory.create(k);
				astFactory.addASTChild(currentAST, k_AST);
				match(_t,LIT_CAR);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(k);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case CADENA:
			{
				m = (AST)_t;
				AST m_AST_in = null;
				m_AST = astFactory.create(m);
				astFactory.addASTChild(currentAST, m_AST);
				match(_t,CADENA);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(m);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				n = (AST)_t;
				AST n_AST_in = null;
				n_AST = astFactory.create(n);
				astFactory.addASTChild(currentAST, n_AST);
				match(_t,TRUE);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(n);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				o = (AST)_t;
				AST o_AST_in = null;
				o_AST = astFactory.create(o);
				astFactory.addASTChild(currentAST, o_AST);
				match(_t,FALSE);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					res = AS_Literal(o);
				}
				expresion_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched4242 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==MENOS))) {
					AST __t4242 = _t;
					synPredMatched4242 = true;
					inputState.guessing++;
					try {
						{
						AST __t4241 = _t;
						ASTPair __currentAST4241 = currentAST.copy();
						currentAST.root = currentAST.child;
						currentAST.child = null;
						match(_t,MENOS);
						_t = _t.getFirstChild();
						expresion(_t);
						_t = _retTree;
						expresion(_t);
						_t = _retTree;
						currentAST = __currentAST4241;
						_t = __t4241;
						_t = _t.getNextSibling();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched4242 = false;
					}
					_t = __t4242;
inputState.guessing--;
				}
				if ( synPredMatched4242 ) {
					AST __t4243 = _t;
					op11 = _t==ASTNULL ? null :(AST)_t;
					AST op11_AST_in = null;
					op11_AST = astFactory.create(op11);
					astFactory.addASTChild(currentAST, op11_AST);
					ASTPair __currentAST4243 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,MENOS);
					_t = _t.getFirstChild();
					e1=expresion(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					e2=expresion(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST4243;
					_t = __t4243;
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						res = AS_Exp_Bin_Arit(e1,e2,op11);
					}
					expresion_AST = (AST)currentAST.root;
				}
				else if ((_t.getType()==MENOS)) {
					AST __t4244 = _t;
					op12 = _t==ASTNULL ? null :(AST)_t;
					AST op12_AST_in = null;
					op12_AST = astFactory.create(op12);
					astFactory.addASTChild(currentAST, op12_AST);
					ASTPair __currentAST4244 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,MENOS);
					_t = _t.getFirstChild();
					e1=expresion(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST4244;
					_t = __t4244;
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						res = AS_Exp_Una_Arit(e1,op12);
					}
					expresion_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_AST;
		_retTree = _t;
		return res;
	}
	
	public final void dec_registro(AST _t) throws RecognitionException {
		
		AST dec_registro_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_registro_AST = null;
		String s; LinkedList l = new LinkedList();
		
		try {      // for error handling
			AST __t4162 = _t;
			AST tmp12_AST = null;
			AST tmp12_AST_in = null;
			tmp12_AST = astFactory.create((AST)_t);
			tmp12_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp12_AST);
			ASTPair __currentAST4162 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,REGISTRO);
			_t = _t.getFirstChild();
			AST tmp13_AST = null;
			AST tmp13_AST_in = null;
			tmp13_AST = astFactory.create((AST)_t);
			tmp13_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp13_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			{
			int _cnt4164=0;
			_loop4164:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CAMPO)) {
					s=dec_campo(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						l.add(s);
					}
				}
				else {
					if ( _cnt4164>=1 ) { break _loop4164; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4164++;
			} while (true);
			}
			currentAST = __currentAST4162;
			_t = __t4162;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Registro(l);
			}
			dec_registro_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_registro_AST;
		_retTree = _t;
	}
	
	public final String  dec_campo(AST _t) throws RecognitionException {
		String s = null;
		
		AST dec_campo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_campo_AST = null;
		AST i = null;
		AST i_AST = null;
		
		try {      // for error handling
			AST __t4166 = _t;
			AST tmp14_AST = null;
			AST tmp14_AST_in = null;
			tmp14_AST = astFactory.create((AST)_t);
			tmp14_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp14_AST);
			ASTPair __currentAST4166 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CAMPO);
			_t = _t.getFirstChild();
			i = (AST)_t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				s = i.getText();
			}
			tipo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4166;
			_t = __t4166;
			_t = _t.getNextSibling();
			dec_campo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_campo_AST;
		_retTree = _t;
		return s;
	}
	
	public final void tipo(AST _t) throws RecognitionException {
		
		AST tipo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case REAL:
			case INTEGER:
			case BOOLEAN:
			case CHAR:
			case RANGO:
			{
				simple(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				definido(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			case STRING:
			{
				compuesto(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			case VARIABLE:
			case PROCEDIMIENTO:
			case FUNCION:
			{
				otro(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					
									JOptionPane.showMessageDialog(
										jContentPanel,
										"ERROR, TIPO INV.LIDO",
										"Error",
										JOptionPane.INFORMATION_MESSAGE);
							 		System.exit(1);
								
				}
				tipo_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_AST;
		_retTree = _t;
	}
	
	public final void dec_variable(AST _t) throws RecognitionException {
		
		AST dec_variable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_variable_AST = null;
		
		try {      // for error handling
			AST __t4172 = _t;
			AST tmp15_AST = null;
			AST tmp15_AST_in = null;
			tmp15_AST = astFactory.create((AST)_t);
			tmp15_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp15_AST);
			ASTPair __currentAST4172 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,VARIABLE);
			_t = _t.getFirstChild();
			AST tmp16_AST = null;
			AST tmp16_AST_in = null;
			tmp16_AST = astFactory.create((AST)_t);
			tmp16_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp16_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4172;
			_t = __t4172;
			_t = _t.getNextSibling();
			dec_variable_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_variable_AST;
		_retTree = _t;
	}
	
	public final void dec_rutina(AST _t) throws RecognitionException {
		
		AST dec_rutina_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_rutina_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PROCEDIMIENTO:
			{
				dec_procedimiento(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dec_rutina_AST = (AST)currentAST.root;
				break;
			}
			case FUNCION:
			{
				dec_funcion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dec_rutina_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_rutina_AST;
		_retTree = _t;
	}
	
	public final void dec_procedimiento(AST _t) throws RecognitionException {
		
		AST dec_procedimiento_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_procedimiento_AST = null;
		
		try {      // for error handling
			AST __t4185 = _t;
			AST tmp17_AST = null;
			AST tmp17_AST_in = null;
			tmp17_AST = astFactory.create((AST)_t);
			tmp17_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp17_AST);
			ASTPair __currentAST4185 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PROCEDIMIENTO);
			_t = _t.getFirstChild();
			cabecera_procedimiento(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_const_var(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4185;
			_t = __t4185;
			_t = _t.getNextSibling();
			dec_procedimiento_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_procedimiento_AST;
		_retTree = _t;
	}
	
	public final void dec_funcion(AST _t) throws RecognitionException {
		
		AST dec_funcion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_funcion_AST = null;
		
		try {      // for error handling
			AST __t4189 = _t;
			AST tmp18_AST = null;
			AST tmp18_AST_in = null;
			tmp18_AST = astFactory.create((AST)_t);
			tmp18_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp18_AST);
			ASTPair __currentAST4189 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FUNCION);
			_t = _t.getFirstChild();
			cabecera_funcion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_const_var(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4189;
			_t = __t4189;
			_t = _t.getNextSibling();
			dec_funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_funcion_AST;
		_retTree = _t;
	}
	
	public final void dec_parametros(AST _t) throws RecognitionException {
		
		AST dec_parametros_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_parametros_AST = null;
		
		try {      // for error handling
			AST __t4179 = _t;
			AST tmp19_AST = null;
			AST tmp19_AST_in = null;
			tmp19_AST = astFactory.create((AST)_t);
			tmp19_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp19_AST);
			ASTPair __currentAST4179 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PARAMETROS);
			_t = _t.getFirstChild();
			{
			_loop4181:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PARAMETRO)) {
					dec_parametro(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4181;
				}
				
			} while (true);
			}
			currentAST = __currentAST4179;
			_t = __t4179;
			_t = _t.getNextSibling();
			dec_parametros_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_parametros_AST;
		_retTree = _t;
	}
	
	public final void dec_parametro(AST _t) throws RecognitionException {
		
		AST dec_parametro_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_parametro_AST = null;
		
		try {      // for error handling
			AST __t4183 = _t;
			AST tmp20_AST = null;
			AST tmp20_AST_in = null;
			tmp20_AST = astFactory.create((AST)_t);
			tmp20_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp20_AST);
			ASTPair __currentAST4183 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PARAMETRO);
			_t = _t.getFirstChild();
			AST tmp21_AST = null;
			AST tmp21_AST_in = null;
			tmp21_AST = astFactory.create((AST)_t);
			tmp21_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp21_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4183;
			_t = __t4183;
			_t = _t.getNextSibling();
			dec_parametro_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_parametro_AST;
		_retTree = _t;
	}
	
	public final void cabecera_procedimiento(AST _t) throws RecognitionException {
		
		AST cabecera_procedimiento_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_procedimiento_AST = null;
		
		try {      // for error handling
			AST __t4187 = _t;
			AST tmp22_AST = null;
			AST tmp22_AST_in = null;
			tmp22_AST = astFactory.create((AST)_t);
			tmp22_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp22_AST);
			ASTPair __currentAST4187 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA);
			_t = _t.getFirstChild();
			AST tmp23_AST = null;
			AST tmp23_AST_in = null;
			tmp23_AST = astFactory.create((AST)_t);
			tmp23_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp23_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			dec_parametros(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp24_AST = null;
			AST tmp24_AST_in = null;
			tmp24_AST = astFactory.create((AST)_t);
			tmp24_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp24_AST);
			match(_t,VACIO);
			_t = _t.getNextSibling();
			currentAST = __currentAST4187;
			_t = __t4187;
			_t = _t.getNextSibling();
			cabecera_procedimiento_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_procedimiento_AST;
		_retTree = _t;
	}
	
	public final void dec_const_var(AST _t) throws RecognitionException {
		
		AST dec_const_var_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_const_var_AST = null;
		
		try {      // for error handling
			AST __t4193 = _t;
			AST tmp25_AST = null;
			AST tmp25_AST_in = null;
			tmp25_AST = astFactory.create((AST)_t);
			tmp25_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp25_AST);
			ASTPair __currentAST4193 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DECLARACIONES);
			_t = _t.getFirstChild();
			dec_constantes(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_variables(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4193;
			_t = __t4193;
			_t = _t.getNextSibling();
			dec_const_var_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_const_var_AST;
		_retTree = _t;
	}
	
	public final void cabecera_funcion(AST _t) throws RecognitionException {
		
		AST cabecera_funcion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_funcion_AST = null;
		
		try {      // for error handling
			AST __t4191 = _t;
			AST tmp26_AST = null;
			AST tmp26_AST_in = null;
			tmp26_AST = astFactory.create((AST)_t);
			tmp26_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp26_AST);
			ASTPair __currentAST4191 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA);
			_t = _t.getFirstChild();
			AST tmp27_AST = null;
			AST tmp27_AST_in = null;
			tmp27_AST = astFactory.create((AST)_t);
			tmp27_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			dec_parametros(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			tipo(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4191;
			_t = __t4191;
			_t = _t.getNextSibling();
			cabecera_funcion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_funcion_AST;
		_retTree = _t;
	}
	
	public final void instruccion(AST _t) throws RecognitionException {
		
		AST instruccion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LLAMADA:
			case ASIGNACION:
			case WRITE:
			case READ:
			{
				instruccion_simple(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_AST = (AST)currentAST.root;
				break;
			}
			case IF:
			case WHILE:
			case FOR:
			{
				instruccion_compuesta(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_AST;
		_retTree = _t;
	}
	
	public final void bloque(AST _t) throws RecognitionException {
		
		AST bloque_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bloque_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LLAMADA:
			case ASIGNACION:
			case WRITE:
			case READ:
			{
				instruccion_simple(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				bloque_AST = (AST)currentAST.root;
				break;
			}
			case CUERPO:
			{
				cuerpo(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				bloque_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = bloque_AST;
		_retTree = _t;
	}
	
	public final void instruccion_simple(AST _t) throws RecognitionException {
		
		AST instruccion_simple_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_simple_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASIGNACION:
			{
				asignacion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			{
				llamada_rutina(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
				break;
			}
			case WRITE:
			{
				salida(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
				break;
			}
			case READ:
			{
				entrada(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_simple_AST;
		_retTree = _t;
	}
	
	public final void instruccion_compuesta(AST _t) throws RecognitionException {
		
		AST instruccion_compuesta_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_compuesta_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IF:
			{
				condicional(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case WHILE:
			{
				iteracion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			case FOR:
			{
				iteracion_acotada(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_compuesta_AST;
		_retTree = _t;
	}
	
	public final void asignacion(AST _t) throws RecognitionException {
		
		AST asignacion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST asignacion_AST = null;
		Atr_Expr e1, e2;
		
		try {      // for error handling
			AST __t4203 = _t;
			AST tmp28_AST = null;
			AST tmp28_AST_in = null;
			tmp28_AST = astFactory.create((AST)_t);
			tmp28_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp28_AST);
			ASTPair __currentAST4203 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ASIGNACION);
			_t = _t.getFirstChild();
			e1=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			e2=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4203;
			_t = __t4203;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Asignacion(e1, e2);
			}
			asignacion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = asignacion_AST;
		_retTree = _t;
	}
	
	public final void llamada_rutina(AST _t) throws RecognitionException {
		
		AST llamada_rutina_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST llamada_rutina_AST = null;
		Atr_Expr e1, res; LinkedList l;
		
		try {      // for error handling
			AST __t4205 = _t;
			AST tmp29_AST = null;
			AST tmp29_AST_in = null;
			tmp29_AST = astFactory.create((AST)_t);
			tmp29_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp29_AST);
			ASTPair __currentAST4205 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,LLAMADA);
			_t = _t.getFirstChild();
			e1=acceso(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			l=lista_expresiones(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4205;
			_t = __t4205;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				
						res = AS_Llamada(e1,l);
						
						if (res.getTipo().getType() != VACIO)
						{
							JOptionPane.showMessageDialog(
								jContentPanel,
								"ERROR, LA LLAMADA FUERA DE UNA EXPRESI.N CORRESPONDE A UN M.TODO FUNCIONAL",
								"Error",
								JOptionPane.INFORMATION_MESSAGE);
					 		System.exit(1);
						}	
					
			}
			llamada_rutina_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = llamada_rutina_AST;
		_retTree = _t;
	}
	
	public final void salida(AST _t) throws RecognitionException {
		
		AST salida_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST salida_AST = null;
		LinkedList l;
		
		try {      // for error handling
			AST __t4207 = _t;
			AST tmp30_AST = null;
			AST tmp30_AST_in = null;
			tmp30_AST = astFactory.create((AST)_t);
			tmp30_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp30_AST);
			ASTPair __currentAST4207 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WRITE);
			_t = _t.getFirstChild();
			l=lista_expresiones(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4207;
			_t = __t4207;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Salida(l);
			}
			salida_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = salida_AST;
		_retTree = _t;
	}
	
	public final void entrada(AST _t) throws RecognitionException {
		
		AST entrada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entrada_AST = null;
		Atr_Expr e1;
		
		try {      // for error handling
			AST __t4209 = _t;
			AST tmp31_AST = null;
			AST tmp31_AST_in = null;
			tmp31_AST = astFactory.create((AST)_t);
			tmp31_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp31_AST);
			ASTPair __currentAST4209 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,READ);
			_t = _t.getFirstChild();
			e1=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4209;
			_t = __t4209;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Entrada(e1);
			}
			entrada_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = entrada_AST;
		_retTree = _t;
	}
	
	public final void condicional(AST _t) throws RecognitionException {
		
		AST condicional_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicional_AST = null;
		Atr_Expr e1;
		
		try {      // for error handling
			AST __t4211 = _t;
			AST tmp32_AST = null;
			AST tmp32_AST_in = null;
			tmp32_AST = astFactory.create((AST)_t);
			tmp32_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp32_AST);
			ASTPair __currentAST4211 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,IF);
			_t = _t.getFirstChild();
			e1=condicion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t4212 = _t;
			AST tmp33_AST = null;
			AST tmp33_AST_in = null;
			tmp33_AST = astFactory.create((AST)_t);
			tmp33_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp33_AST);
			ASTPair __currentAST4212 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,BLOQUE_IF);
			_t = _t.getFirstChild();
			bloque(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4212;
			_t = __t4212;
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case BLOQUE_ELSE:
			{
				AST __t4214 = _t;
				AST tmp34_AST = null;
				AST tmp34_AST_in = null;
				tmp34_AST = astFactory.create((AST)_t);
				tmp34_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp34_AST);
				ASTPair __currentAST4214 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,BLOQUE_ELSE);
				_t = _t.getFirstChild();
				bloque(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4214;
				_t = __t4214;
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST4211;
			_t = __t4211;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Condicion(e1);
			}
			condicional_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = condicional_AST;
		_retTree = _t;
	}
	
	public final void iteracion(AST _t) throws RecognitionException {
		
		AST iteracion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_AST = null;
		Atr_Expr e1;
		
		try {      // for error handling
			AST __t4216 = _t;
			AST tmp35_AST = null;
			AST tmp35_AST_in = null;
			tmp35_AST = astFactory.create((AST)_t);
			tmp35_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp35_AST);
			ASTPair __currentAST4216 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WHILE);
			_t = _t.getFirstChild();
			e1=condicion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			bloque(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4216;
			_t = __t4216;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Condicion(e1);
			}
			iteracion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_AST;
		_retTree = _t;
	}
	
	public final void iteracion_acotada(AST _t) throws RecognitionException {
		
		AST iteracion_acotada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_acotada_AST = null;
		
		try {      // for error handling
			AST __t4218 = _t;
			AST tmp36_AST = null;
			AST tmp36_AST_in = null;
			tmp36_AST = astFactory.create((AST)_t);
			tmp36_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp36_AST);
			ASTPair __currentAST4218 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FOR);
			_t = _t.getFirstChild();
			cabecera_for(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t4219 = _t;
			AST tmp37_AST = null;
			AST tmp37_AST_in = null;
			tmp37_AST = astFactory.create((AST)_t);
			tmp37_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp37_AST);
			ASTPair __currentAST4219 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,BLOQUE_FOR);
			_t = _t.getFirstChild();
			bloque(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4219;
			_t = __t4219;
			_t = _t.getNextSibling();
			currentAST = __currentAST4218;
			_t = __t4218;
			_t = _t.getNextSibling();
			iteracion_acotada_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_acotada_AST;
		_retTree = _t;
	}
	
	public final Atr_Expr  acceso(AST _t) throws RecognitionException {
		Atr_Expr res = new Atr_Expr();
		
		AST acceso_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ACCESO_REGISTRO:
			{
				res=acceso_registro(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY_REGISTRO:
			{
				res=acceso_array_registro(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY:
			{
				res=acceso_array(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_SIMPLE:
			{
				res=acceso_simple(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_AST;
		_retTree = _t;
		return res;
	}
	
	public final LinkedList  lista_expresiones(AST _t) throws RecognitionException {
		LinkedList l = new LinkedList();
		
		AST lista_expresiones_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_AST = null;
		Atr_Expr e1;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EXPRESIONES:
			{
				AST __t4224 = _t;
				AST tmp38_AST = null;
				AST tmp38_AST_in = null;
				tmp38_AST = astFactory.create((AST)_t);
				tmp38_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp38_AST);
				ASTPair __currentAST4224 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EXPRESIONES);
				_t = _t.getFirstChild();
				{
				int _cnt4226=0;
				_loop4226:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_1.member(_t.getType()))) {
						e1=expresion(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
						if ( inputState.guessing==0 ) {
							l.add(e1);
						}
					}
					else {
						if ( _cnt4226>=1 ) { break _loop4226; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4226++;
				} while (true);
				}
				currentAST = __currentAST4224;
				_t = __t4224;
				_t = _t.getNextSibling();
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			{
				lista_expresiones_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_AST;
		_retTree = _t;
		return l;
	}
	
	public final Atr_Expr  condicion(AST _t) throws RecognitionException {
		Atr_Expr res = null;
		
		AST condicion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicion_AST = null;
		
		try {      // for error handling
			AST __t4228 = _t;
			AST tmp39_AST = null;
			AST tmp39_AST_in = null;
			tmp39_AST = astFactory.create((AST)_t);
			tmp39_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp39_AST);
			ASTPair __currentAST4228 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CONDICION);
			_t = _t.getFirstChild();
			res=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4228;
			_t = __t4228;
			_t = _t.getNextSibling();
			condicion_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = condicion_AST;
		_retTree = _t;
		return res;
	}
	
	public final void cabecera_for(AST _t) throws RecognitionException {
		
		AST cabecera_for_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_for_AST = null;
		Atr_Expr a, e1, e2;
		
		try {      // for error handling
			AST __t4221 = _t;
			AST tmp40_AST = null;
			AST tmp40_AST_in = null;
			tmp40_AST = astFactory.create((AST)_t);
			tmp40_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp40_AST);
			ASTPair __currentAST4221 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA_FOR);
			_t = _t.getFirstChild();
			a=acceso(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			e1=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TO:
			{
				AST tmp41_AST = null;
				AST tmp41_AST_in = null;
				tmp41_AST = astFactory.create((AST)_t);
				tmp41_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp41_AST);
				match(_t,TO);
				_t = _t.getNextSibling();
				break;
			}
			case DOWNTO:
			{
				AST tmp42_AST = null;
				AST tmp42_AST_in = null;
				tmp42_AST = astFactory.create((AST)_t);
				tmp42_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp42_AST);
				match(_t,DOWNTO);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			e2=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4221;
			_t = __t4221;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Cabecera_For(a, e1, e2);
			}
			cabecera_for_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_for_AST;
		_retTree = _t;
	}
	
	public final Atr_Expr  acceso_registro(AST _t) throws RecognitionException {
		Atr_Expr res = new Atr_Expr();
		
		AST acceso_registro_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_registro_AST = null;
		AST a_AST = null;
		AST a = null;
		Atr_Expr e;
		
		try {      // for error handling
			AST __t4252 = _t;
			AST tmp43_AST = null;
			AST tmp43_AST_in = null;
			tmp43_AST = astFactory.create((AST)_t);
			tmp43_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp43_AST);
			ASTPair __currentAST4252 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_REGISTRO);
			_t = _t.getFirstChild();
			e=acceso_simple(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			a = _t==ASTNULL ? null : (AST)_t;
			acceso_ext(_t);
			_t = _retTree;
			a_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4252;
			_t = __t4252;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				res = AS_Acceso_Registro(e,a);
			}
			acceso_registro_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_registro_AST;
		_retTree = _t;
		return res;
	}
	
	public final Atr_Expr  acceso_array_registro(AST _t) throws RecognitionException {
		Atr_Expr res = new Atr_Expr();
		
		AST acceso_array_registro_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_registro_AST = null;
		AST i_AST = null;
		AST i = null;
		AST a_AST = null;
		AST a = null;
		Atr_Expr e;
		
		try {      // for error handling
			AST __t4254 = _t;
			AST tmp44_AST = null;
			AST tmp44_AST_in = null;
			tmp44_AST = astFactory.create((AST)_t);
			tmp44_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp44_AST);
			ASTPair __currentAST4254 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY_REGISTRO);
			_t = _t.getFirstChild();
			e=acceso_simple(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			i = _t==ASTNULL ? null : (AST)_t;
			indices(_t);
			_t = _retTree;
			i_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			a = _t==ASTNULL ? null : (AST)_t;
			acceso_ext(_t);
			_t = _retTree;
			a_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4254;
			_t = __t4254;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				res = AS_Acceso_Array_Registro(e, i, a);
			}
			acceso_array_registro_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_registro_AST;
		_retTree = _t;
		return res;
	}
	
	public final Atr_Expr  acceso_array(AST _t) throws RecognitionException {
		Atr_Expr res = new Atr_Expr();
		
		AST acceso_array_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_AST = null;
		AST i_AST = null;
		AST i = null;
		Atr_Expr e; LinkedList l;
		
		try {      // for error handling
			AST __t4256 = _t;
			AST tmp45_AST = null;
			AST tmp45_AST_in = null;
			tmp45_AST = astFactory.create((AST)_t);
			tmp45_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp45_AST);
			ASTPair __currentAST4256 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY);
			_t = _t.getFirstChild();
			e=acceso_simple(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			i = _t==ASTNULL ? null : (AST)_t;
			indices(_t);
			_t = _retTree;
			i_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4256;
			_t = __t4256;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				res = AS_Acceso_Array(e,i);
			}
			acceso_array_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_AST;
		_retTree = _t;
		return res;
	}
	
	public final Atr_Expr  acceso_simple(AST _t) throws RecognitionException {
		Atr_Expr res = new Atr_Expr();
		
		AST acceso_simple_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_simple_AST = null;
		AST d_AST = null;
		AST d = null;
		
		try {      // for error handling
			AST __t4258 = _t;
			AST tmp46_AST = null;
			AST tmp46_AST_in = null;
			tmp46_AST = astFactory.create((AST)_t);
			tmp46_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp46_AST);
			ASTPair __currentAST4258 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_SIMPLE);
			_t = _t.getFirstChild();
			AST tmp47_AST = null;
			AST tmp47_AST_in = null;
			tmp47_AST = astFactory.create((AST)_t);
			tmp47_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp47_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			d = _t==ASTNULL ? null : (AST)_t;
			declaracion_acceso(_t);
			_t = _retTree;
			d_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4258;
			_t = __t4258;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				res = AS_Acceso_Simple(d);
			}
			acceso_simple_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_simple_AST;
		_retTree = _t;
		return res;
	}
	
	public final void acceso_ext(AST _t) throws RecognitionException {
		
		AST acceso_ext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_ext_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ACCESO_REGISTRO:
			{
				acceso_registro_ext(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY_REGISTRO:
			{
				acceso_array_registro_ext(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY:
			{
				acceso_array_ext(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				acceso_simple_ext(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_ext_AST;
		_retTree = _t;
	}
	
	public final void indices(AST _t) throws RecognitionException {
		
		AST indices_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indices_AST = null;
		Atr_Expr e;
		
		try {      // for error handling
			AST __t4269 = _t;
			AST tmp48_AST = null;
			AST tmp48_AST_in = null;
			tmp48_AST = astFactory.create((AST)_t);
			tmp48_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp48_AST);
			ASTPair __currentAST4269 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,INDICES);
			_t = _t.getFirstChild();
			{
			int _cnt4271=0;
			_loop4271:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_1.member(_t.getType()))) {
					e=expresion(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt4271>=1 ) { break _loop4271; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4271++;
			} while (true);
			}
			currentAST = __currentAST4269;
			_t = __t4269;
			_t = _t.getNextSibling();
			indices_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = indices_AST;
		_retTree = _t;
	}
	
	public final void declaracion_acceso(AST _t) throws RecognitionException {
		
		AST declaracion_acceso_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_acceso_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VARIABLE_RESULTADO:
			{
				dec_variable_resultado_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case VARIABLE:
			{
				dec_variable_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				dec_registro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			{
				dec_constante_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case PROCEDIMIENTO:
			{
				dec_procedimiento_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case FUNCION:
			{
				dec_funcion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			case PARAMETRO:
			{
				dec_parametro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_acceso_AST;
		_retTree = _t;
	}
	
	public final void dec_variable_resultado_1(AST _t) throws RecognitionException {
		
		AST dec_variable_resultado_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_variable_resultado_1_AST = null;
		
		try {      // for error handling
			AST __t4314 = _t;
			AST tmp49_AST = null;
			AST tmp49_AST_in = null;
			tmp49_AST = astFactory.create((AST)_t);
			tmp49_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp49_AST);
			ASTPair __currentAST4314 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,VARIABLE_RESULTADO);
			_t = _t.getFirstChild();
			AST tmp50_AST = null;
			AST tmp50_AST_in = null;
			tmp50_AST = astFactory.create((AST)_t);
			tmp50_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp50_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4314;
			_t = __t4314;
			_t = _t.getNextSibling();
			dec_variable_resultado_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_variable_resultado_1_AST;
		_retTree = _t;
	}
	
	public final void dec_variable_1(AST _t) throws RecognitionException {
		
		AST dec_variable_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_variable_1_AST = null;
		
		try {      // for error handling
			AST __t4312 = _t;
			AST tmp51_AST = null;
			AST tmp51_AST_in = null;
			tmp51_AST = astFactory.create((AST)_t);
			tmp51_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp51_AST);
			ASTPair __currentAST4312 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,VARIABLE);
			_t = _t.getFirstChild();
			AST tmp52_AST = null;
			AST tmp52_AST_in = null;
			tmp52_AST = astFactory.create((AST)_t);
			tmp52_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp52_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4312;
			_t = __t4312;
			_t = _t.getNextSibling();
			dec_variable_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_variable_1_AST;
		_retTree = _t;
	}
	
	public final void dec_registro_1(AST _t) throws RecognitionException {
		
		AST dec_registro_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_registro_1_AST = null;
		
		try {      // for error handling
			AST __t4302 = _t;
			AST tmp53_AST = null;
			AST tmp53_AST_in = null;
			tmp53_AST = astFactory.create((AST)_t);
			tmp53_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp53_AST);
			ASTPair __currentAST4302 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,REGISTRO);
			_t = _t.getFirstChild();
			AST tmp54_AST = null;
			AST tmp54_AST_in = null;
			tmp54_AST = astFactory.create((AST)_t);
			tmp54_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp54_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			{
			int _cnt4304=0;
			_loop4304:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CAMPO)) {
					dec_campo_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt4304>=1 ) { break _loop4304; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4304++;
			} while (true);
			}
			currentAST = __currentAST4302;
			_t = __t4302;
			_t = _t.getNextSibling();
			dec_registro_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_registro_1_AST;
		_retTree = _t;
	}
	
	public final void dec_constante_1(AST _t) throws RecognitionException {
		
		AST dec_constante_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_constante_1_AST = null;
		
		try {      // for error handling
			AST __t4300 = _t;
			AST tmp55_AST = null;
			AST tmp55_AST_in = null;
			tmp55_AST = astFactory.create((AST)_t);
			tmp55_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp55_AST);
			ASTPair __currentAST4300 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CONSTANTE);
			_t = _t.getFirstChild();
			AST tmp56_AST = null;
			AST tmp56_AST_in = null;
			tmp56_AST = astFactory.create((AST)_t);
			tmp56_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp56_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4300;
			_t = __t4300;
			_t = _t.getNextSibling();
			dec_constante_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_constante_1_AST;
		_retTree = _t;
	}
	
	public final void dec_procedimiento_1(AST _t) throws RecognitionException {
		
		AST dec_procedimiento_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_procedimiento_1_AST = null;
		
		try {      // for error handling
			AST __t4323 = _t;
			AST tmp57_AST = null;
			AST tmp57_AST_in = null;
			tmp57_AST = astFactory.create((AST)_t);
			tmp57_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp57_AST);
			ASTPair __currentAST4323 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PROCEDIMIENTO);
			_t = _t.getFirstChild();
			cabecera_procedimiento_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_const_var_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4323;
			_t = __t4323;
			_t = _t.getNextSibling();
			dec_procedimiento_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_procedimiento_1_AST;
		_retTree = _t;
	}
	
	public final void dec_funcion_1(AST _t) throws RecognitionException {
		
		AST dec_funcion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_funcion_1_AST = null;
		
		try {      // for error handling
			AST __t4327 = _t;
			AST tmp58_AST = null;
			AST tmp58_AST_in = null;
			tmp58_AST = astFactory.create((AST)_t);
			tmp58_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp58_AST);
			ASTPair __currentAST4327 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FUNCION);
			_t = _t.getFirstChild();
			cabecera_funcion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_const_var_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			cuerpo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4327;
			_t = __t4327;
			_t = _t.getNextSibling();
			dec_funcion_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_funcion_1_AST;
		_retTree = _t;
	}
	
	public final void dec_parametro_1(AST _t) throws RecognitionException {
		
		AST dec_parametro_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_parametro_1_AST = null;
		
		try {      // for error handling
			AST __t4321 = _t;
			AST tmp59_AST = null;
			AST tmp59_AST_in = null;
			tmp59_AST = astFactory.create((AST)_t);
			tmp59_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp59_AST);
			ASTPair __currentAST4321 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PARAMETRO);
			_t = _t.getFirstChild();
			AST tmp60_AST = null;
			AST tmp60_AST_in = null;
			tmp60_AST = astFactory.create((AST)_t);
			tmp60_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp60_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4321;
			_t = __t4321;
			_t = _t.getNextSibling();
			dec_parametro_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_parametro_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_registro_ext(AST _t) throws RecognitionException {
		
		AST acceso_registro_ext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_registro_ext_AST = null;
		
		try {      // for error handling
			AST __t4262 = _t;
			AST tmp61_AST = null;
			AST tmp61_AST_in = null;
			tmp61_AST = astFactory.create((AST)_t);
			tmp61_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp61_AST);
			ASTPair __currentAST4262 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_REGISTRO);
			_t = _t.getFirstChild();
			AST tmp62_AST = null;
			AST tmp62_AST_in = null;
			tmp62_AST = astFactory.create((AST)_t);
			tmp62_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp62_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			acceso_ext(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4262;
			_t = __t4262;
			_t = _t.getNextSibling();
			acceso_registro_ext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_registro_ext_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_registro_ext(AST _t) throws RecognitionException {
		
		AST acceso_array_registro_ext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_registro_ext_AST = null;
		
		try {      // for error handling
			AST __t4264 = _t;
			AST tmp63_AST = null;
			AST tmp63_AST_in = null;
			tmp63_AST = astFactory.create((AST)_t);
			tmp63_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp63_AST);
			ASTPair __currentAST4264 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY_REGISTRO);
			_t = _t.getFirstChild();
			AST tmp64_AST = null;
			AST tmp64_AST_in = null;
			tmp64_AST = astFactory.create((AST)_t);
			tmp64_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp64_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			indices(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			acceso_ext(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4264;
			_t = __t4264;
			_t = _t.getNextSibling();
			acceso_array_registro_ext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_registro_ext_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_ext(AST _t) throws RecognitionException {
		
		AST acceso_array_ext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_ext_AST = null;
		
		try {      // for error handling
			AST __t4266 = _t;
			AST tmp65_AST = null;
			AST tmp65_AST_in = null;
			tmp65_AST = astFactory.create((AST)_t);
			tmp65_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp65_AST);
			ASTPair __currentAST4266 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY);
			_t = _t.getFirstChild();
			AST tmp66_AST = null;
			AST tmp66_AST_in = null;
			tmp66_AST = astFactory.create((AST)_t);
			tmp66_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp66_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			indices(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4266;
			_t = __t4266;
			_t = _t.getNextSibling();
			acceso_array_ext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_ext_AST;
		_retTree = _t;
	}
	
	public final void acceso_simple_ext(AST _t) throws RecognitionException {
		
		AST acceso_simple_ext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_simple_ext_AST = null;
		
		try {      // for error handling
			AST tmp67_AST = null;
			AST tmp67_AST_in = null;
			tmp67_AST = astFactory.create((AST)_t);
			tmp67_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp67_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			acceso_simple_ext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_simple_ext_AST;
		_retTree = _t;
	}
	
	public final void simple(AST _t) throws RecognitionException {
		
		AST simple_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simple_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INTEGER:
			{
				AST tmp68_AST = null;
				AST tmp68_AST_in = null;
				tmp68_AST = astFactory.create((AST)_t);
				tmp68_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp68_AST);
				match(_t,INTEGER);
				_t = _t.getNextSibling();
				simple_AST = (AST)currentAST.root;
				break;
			}
			case REAL:
			{
				AST tmp69_AST = null;
				AST tmp69_AST_in = null;
				tmp69_AST = astFactory.create((AST)_t);
				tmp69_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp69_AST);
				match(_t,REAL);
				_t = _t.getNextSibling();
				simple_AST = (AST)currentAST.root;
				break;
			}
			case BOOLEAN:
			{
				AST tmp70_AST = null;
				AST tmp70_AST_in = null;
				tmp70_AST = astFactory.create((AST)_t);
				tmp70_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp70_AST);
				match(_t,BOOLEAN);
				_t = _t.getNextSibling();
				simple_AST = (AST)currentAST.root;
				break;
			}
			case CHAR:
			{
				AST tmp71_AST = null;
				AST tmp71_AST_in = null;
				tmp71_AST = astFactory.create((AST)_t);
				tmp71_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp71_AST);
				match(_t,CHAR);
				_t = _t.getNextSibling();
				simple_AST = (AST)currentAST.root;
				break;
			}
			case RANGO:
			{
				tipo_rango(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				simple_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = simple_AST;
		_retTree = _t;
	}
	
	public final void definido(AST _t) throws RecognitionException {
		
		AST definido_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definido_AST = null;
		
		try {      // for error handling
			dec_registro_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			definido_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = definido_AST;
		_retTree = _t;
	}
	
	public final void compuesto(AST _t) throws RecognitionException {
		
		AST compuesto_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compuesto_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			{
				array(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				compuesto_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			{
				string(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				compuesto_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = compuesto_AST;
		_retTree = _t;
	}
	
	public final void otro(AST _t) throws RecognitionException {
		
		AST otro_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST otro_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FUNCION:
			{
				dec_funcion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_AST = (AST)currentAST.root;
				break;
			}
			case PROCEDIMIENTO:
			{
				dec_procedimiento_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_AST = (AST)currentAST.root;
				break;
			}
			case VARIABLE:
			{
				dec_variable_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			{
				dec_constante_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = otro_AST;
		_retTree = _t;
	}
	
	public final void tipo_array(AST _t) throws RecognitionException {
		
		AST tipo_array_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_array_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case REAL:
			case INTEGER:
			case BOOLEAN:
			case CHAR:
			case RANGO:
			{
				simple(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_array_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				definido(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_array_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_array_AST;
		_retTree = _t;
	}
	
	public final void tipo_rango(AST _t) throws RecognitionException {
		
		AST tipo_rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_rango_AST = null;
		
		try {      // for error handling
			boolean synPredMatched4290 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==RANGO))) {
				AST __t4290 = _t;
				synPredMatched4290 = true;
				inputState.guessing++;
				try {
					{
					AST __t4289 = _t;
					ASTPair __currentAST4289 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,RANGO);
					_t = _t.getFirstChild();
					match(_t,LIT_CAR);
					_t = _t.getNextSibling();
					currentAST = __currentAST4289;
					_t = __t4289;
					_t = _t.getNextSibling();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched4290 = false;
				}
				_t = __t4290;
inputState.guessing--;
			}
			if ( synPredMatched4290 ) {
				rango_caracter(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_rango_AST = (AST)currentAST.root;
			}
			else if ((_t.getType()==RANGO)) {
				rango_entero(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_rango_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(_t);
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_rango_AST;
		_retTree = _t;
	}
	
	public final void array(AST _t) throws RecognitionException {
		
		AST array_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_AST = null;
		
		try {      // for error handling
			AST __t4279 = _t;
			AST tmp72_AST = null;
			AST tmp72_AST_in = null;
			tmp72_AST = astFactory.create((AST)_t);
			tmp72_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp72_AST);
			ASTPair __currentAST4279 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ARRAY);
			_t = _t.getFirstChild();
			dimensiones_array(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			tipo_array(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4279;
			_t = __t4279;
			_t = _t.getNextSibling();
			array_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = array_AST;
		_retTree = _t;
	}
	
	public final void string(AST _t) throws RecognitionException {
		
		AST string_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST string_AST = null;
		
		try {      // for error handling
			AST __t4286 = _t;
			AST tmp73_AST = null;
			AST tmp73_AST_in = null;
			tmp73_AST = astFactory.create((AST)_t);
			tmp73_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp73_AST);
			ASTPair __currentAST4286 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,STRING);
			_t = _t.getFirstChild();
			AST tmp74_AST = null;
			AST tmp74_AST_in = null;
			tmp74_AST = astFactory.create((AST)_t);
			tmp74_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp74_AST);
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			currentAST = __currentAST4286;
			_t = __t4286;
			_t = _t.getNextSibling();
			string_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = string_AST;
		_retTree = _t;
	}
	
	public final void dimensiones_array(AST _t) throws RecognitionException {
		
		AST dimensiones_array_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimensiones_array_AST = null;
		
		try {      // for error handling
			AST __t4281 = _t;
			AST tmp75_AST = null;
			AST tmp75_AST_in = null;
			tmp75_AST = astFactory.create((AST)_t);
			tmp75_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp75_AST);
			ASTPair __currentAST4281 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DIMENSIONES);
			_t = _t.getFirstChild();
			{
			int _cnt4283=0;
			_loop4283:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_2.member(_t.getType()))) {
					dimension(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt4283>=1 ) { break _loop4283; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4283++;
			} while (true);
			}
			currentAST = __currentAST4281;
			_t = __t4281;
			_t = _t.getNextSibling();
			dimensiones_array_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dimensiones_array_AST;
		_retTree = _t;
	}
	
	public final void dimension(AST _t) throws RecognitionException {
		
		AST dimension_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimension_AST = null;
		Atr_Expr e;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case RANGO:
			{
				rango_entero(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dimension_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			case ACCESO_SIMPLE:
			case IGUAL:
			case MAYOR:
			case MAYOR_IGUAL:
			case MENOR:
			case MENOR_IGUAL:
			case DISTINTO:
			case MAS:
			case MENOS:
			case POR:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case OR:
			case AND:
			case NOT:
			case DIVISION_ENTERA:
			case DIVISION_REAL:
			case MOD:
			case CADENA:
			case TRUE:
			case FALSE:
			case ACCESO_REGISTRO:
			case ACCESO_ARRAY_REGISTRO:
			case ACCESO_ARRAY:
			{
				e=expresion(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dimension_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dimension_AST;
		_retTree = _t;
	}
	
	public final void rango_entero(AST _t) throws RecognitionException {
		
		AST rango_entero_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rango_entero_AST = null;
		Atr_Expr e1, e2;
		
		try {      // for error handling
			AST __t4294 = _t;
			AST tmp76_AST = null;
			AST tmp76_AST_in = null;
			tmp76_AST = astFactory.create((AST)_t);
			tmp76_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp76_AST);
			ASTPair __currentAST4294 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			e1=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			e2=expresion(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4294;
			_t = __t4294;
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				AS_Rango_Entero(e1, e2);
			}
			rango_entero_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = rango_entero_AST;
		_retTree = _t;
	}
	
	public final void rango_caracter(AST _t) throws RecognitionException {
		
		AST rango_caracter_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rango_caracter_AST = null;
		
		try {      // for error handling
			AST __t4292 = _t;
			AST tmp77_AST = null;
			AST tmp77_AST_in = null;
			tmp77_AST = astFactory.create((AST)_t);
			tmp77_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp77_AST);
			ASTPair __currentAST4292 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			AST tmp78_AST = null;
			AST tmp78_AST_in = null;
			tmp78_AST = astFactory.create((AST)_t);
			tmp78_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp78_AST);
			match(_t,LIT_CAR);
			_t = _t.getNextSibling();
			AST tmp79_AST = null;
			AST tmp79_AST_in = null;
			tmp79_AST = astFactory.create((AST)_t);
			tmp79_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp79_AST);
			match(_t,LIT_CAR);
			_t = _t.getNextSibling();
			currentAST = __currentAST4292;
			_t = __t4292;
			_t = _t.getNextSibling();
			rango_caracter_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = rango_caracter_AST;
		_retTree = _t;
	}
	
	public final void dec_constantes_1(AST _t) throws RecognitionException {
		
		AST dec_constantes_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_constantes_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CONST:
			{
				AST __t4296 = _t;
				AST tmp80_AST = null;
				AST tmp80_AST_in = null;
				tmp80_AST = astFactory.create((AST)_t);
				tmp80_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp80_AST);
				ASTPair __currentAST4296 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,CONST);
				_t = _t.getFirstChild();
				{
				int _cnt4298=0;
				_loop4298:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CONSTANTE)) {
						dec_constante_1(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4298>=1 ) { break _loop4298; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4298++;
				} while (true);
				}
				currentAST = __currentAST4296;
				_t = __t4296;
				_t = _t.getNextSibling();
				dec_constantes_1_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			case VAR:
			{
				dec_constantes_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_constantes_1_AST;
		_retTree = _t;
	}
	
	public final void expresion_1(AST _t) throws RecognitionException {
		
		AST expresion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expresion_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OR:
			{
				AST __t4367 = _t;
				AST tmp81_AST = null;
				AST tmp81_AST_in = null;
				tmp81_AST = astFactory.create((AST)_t);
				tmp81_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp81_AST);
				ASTPair __currentAST4367 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,OR);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4367;
				_t = __t4367;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case AND:
			{
				AST __t4368 = _t;
				AST tmp82_AST = null;
				AST tmp82_AST_in = null;
				tmp82_AST = astFactory.create((AST)_t);
				tmp82_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp82_AST);
				ASTPair __currentAST4368 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,AND);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4368;
				_t = __t4368;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case NOT:
			{
				AST __t4369 = _t;
				AST tmp83_AST = null;
				AST tmp83_AST_in = null;
				tmp83_AST = astFactory.create((AST)_t);
				tmp83_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp83_AST);
				ASTPair __currentAST4369 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4369;
				_t = __t4369;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MAYOR:
			{
				AST __t4370 = _t;
				AST tmp84_AST = null;
				AST tmp84_AST_in = null;
				tmp84_AST = astFactory.create((AST)_t);
				tmp84_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp84_AST);
				ASTPair __currentAST4370 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAYOR);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4370;
				_t = __t4370;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MAYOR_IGUAL:
			{
				AST __t4371 = _t;
				AST tmp85_AST = null;
				AST tmp85_AST_in = null;
				tmp85_AST = astFactory.create((AST)_t);
				tmp85_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp85_AST);
				ASTPair __currentAST4371 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAYOR_IGUAL);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4371;
				_t = __t4371;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MENOR:
			{
				AST __t4372 = _t;
				AST tmp86_AST = null;
				AST tmp86_AST_in = null;
				tmp86_AST = astFactory.create((AST)_t);
				tmp86_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp86_AST);
				ASTPair __currentAST4372 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MENOR);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4372;
				_t = __t4372;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MENOR_IGUAL:
			{
				AST __t4373 = _t;
				AST tmp87_AST = null;
				AST tmp87_AST_in = null;
				tmp87_AST = astFactory.create((AST)_t);
				tmp87_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp87_AST);
				ASTPair __currentAST4373 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MENOR_IGUAL);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4373;
				_t = __t4373;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MAS:
			{
				AST __t4374 = _t;
				AST tmp88_AST = null;
				AST tmp88_AST_in = null;
				tmp88_AST = astFactory.create((AST)_t);
				tmp88_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp88_AST);
				ASTPair __currentAST4374 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MAS);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4374;
				_t = __t4374;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case POR:
			{
				AST __t4380 = _t;
				AST tmp89_AST = null;
				AST tmp89_AST_in = null;
				tmp89_AST = astFactory.create((AST)_t);
				tmp89_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp89_AST);
				ASTPair __currentAST4380 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,POR);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4380;
				_t = __t4380;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case DIVISION_ENTERA:
			{
				AST __t4381 = _t;
				AST tmp90_AST = null;
				AST tmp90_AST_in = null;
				tmp90_AST = astFactory.create((AST)_t);
				tmp90_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp90_AST);
				ASTPair __currentAST4381 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIVISION_ENTERA);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4381;
				_t = __t4381;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case DIVISION_REAL:
			{
				AST __t4382 = _t;
				AST tmp91_AST = null;
				AST tmp91_AST_in = null;
				tmp91_AST = astFactory.create((AST)_t);
				tmp91_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp91_AST);
				ASTPair __currentAST4382 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIVISION_REAL);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4382;
				_t = __t4382;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case MOD:
			{
				AST __t4383 = _t;
				AST tmp92_AST = null;
				AST tmp92_AST_in = null;
				tmp92_AST = astFactory.create((AST)_t);
				tmp92_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp92_AST);
				ASTPair __currentAST4383 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MOD);
				_t = _t.getFirstChild();
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4383;
				_t = __t4383;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			{
				AST __t4384 = _t;
				AST tmp93_AST = null;
				AST tmp93_AST_in = null;
				tmp93_AST = astFactory.create((AST)_t);
				tmp93_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp93_AST);
				ASTPair __currentAST4384 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LLAMADA);
				_t = _t.getFirstChild();
				acceso_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				lista_expresiones_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST4384;
				_t = __t4384;
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_SIMPLE:
			case ACCESO_REGISTRO:
			case ACCESO_ARRAY_REGISTRO:
			case ACCESO_ARRAY:
			{
				acceso_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case LIT_ENTERO:
			{
				AST tmp94_AST = null;
				AST tmp94_AST_in = null;
				tmp94_AST = astFactory.create((AST)_t);
				tmp94_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case LIT_REAL:
			{
				AST tmp95_AST = null;
				AST tmp95_AST_in = null;
				tmp95_AST = astFactory.create((AST)_t);
				tmp95_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp95_AST);
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case LIT_CAR:
			{
				AST tmp96_AST = null;
				AST tmp96_AST_in = null;
				tmp96_AST = astFactory.create((AST)_t);
				tmp96_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp96_AST);
				match(_t,LIT_CAR);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case CADENA:
			{
				AST tmp97_AST = null;
				AST tmp97_AST_in = null;
				tmp97_AST = astFactory.create((AST)_t);
				tmp97_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp97_AST);
				match(_t,CADENA);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp98_AST = null;
				AST tmp98_AST_in = null;
				tmp98_AST = astFactory.create((AST)_t);
				tmp98_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp98_AST);
				match(_t,TRUE);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp99_AST = null;
				AST tmp99_AST_in = null;
				tmp99_AST = astFactory.create((AST)_t);
				tmp99_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp99_AST);
				match(_t,FALSE);
				_t = _t.getNextSibling();
				expresion_1_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched4377 = false;
				if (_t==null) _t=ASTNULL;
				if (((_t.getType()==MENOS))) {
					AST __t4377 = _t;
					synPredMatched4377 = true;
					inputState.guessing++;
					try {
						{
						AST __t4376 = _t;
						ASTPair __currentAST4376 = currentAST.copy();
						currentAST.root = currentAST.child;
						currentAST.child = null;
						match(_t,MENOS);
						_t = _t.getFirstChild();
						expresion_1(_t);
						_t = _retTree;
						expresion_1(_t);
						_t = _retTree;
						currentAST = __currentAST4376;
						_t = __t4376;
						_t = _t.getNextSibling();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched4377 = false;
					}
					_t = __t4377;
inputState.guessing--;
				}
				if ( synPredMatched4377 ) {
					AST __t4378 = _t;
					AST tmp100_AST = null;
					AST tmp100_AST_in = null;
					tmp100_AST = astFactory.create((AST)_t);
					tmp100_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp100_AST);
					ASTPair __currentAST4378 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,MENOS);
					_t = _t.getFirstChild();
					expresion_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					expresion_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST4378;
					_t = __t4378;
					_t = _t.getNextSibling();
					expresion_1_AST = (AST)currentAST.root;
				}
				else if ((_t.getType()==MENOS)) {
					AST __t4379 = _t;
					AST tmp101_AST = null;
					AST tmp101_AST_in = null;
					tmp101_AST = astFactory.create((AST)_t);
					tmp101_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp101_AST);
					ASTPair __currentAST4379 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,MENOS);
					_t = _t.getFirstChild();
					expresion_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST4379;
					_t = __t4379;
					_t = _t.getNextSibling();
					expresion_1_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = expresion_1_AST;
		_retTree = _t;
	}
	
	public final void dec_campo_1(AST _t) throws RecognitionException {
		
		AST dec_campo_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_campo_1_AST = null;
		
		try {      // for error handling
			AST __t4306 = _t;
			AST tmp102_AST = null;
			AST tmp102_AST_in = null;
			tmp102_AST = astFactory.create((AST)_t);
			tmp102_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp102_AST);
			ASTPair __currentAST4306 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CAMPO);
			_t = _t.getFirstChild();
			AST tmp103_AST = null;
			AST tmp103_AST_in = null;
			tmp103_AST = astFactory.create((AST)_t);
			tmp103_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp103_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			tipo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4306;
			_t = __t4306;
			_t = _t.getNextSibling();
			dec_campo_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_campo_1_AST;
		_retTree = _t;
	}
	
	public final void tipo_1(AST _t) throws RecognitionException {
		
		AST tipo_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case REAL:
			case INTEGER:
			case BOOLEAN:
			case CHAR:
			case RANGO:
			{
				simple_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_1_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				definido_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_1_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			case STRING:
			{
				compuesto_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_1_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			case VARIABLE:
			case PROCEDIMIENTO:
			case FUNCION:
			{
				otro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_1_AST;
		_retTree = _t;
	}
	
	public final void dec_variables_1(AST _t) throws RecognitionException {
		
		AST dec_variables_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_variables_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR:
			{
				AST __t4308 = _t;
				AST tmp104_AST = null;
				AST tmp104_AST_in = null;
				tmp104_AST = astFactory.create((AST)_t);
				tmp104_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp104_AST);
				ASTPair __currentAST4308 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,VAR);
				_t = _t.getFirstChild();
				{
				int _cnt4310=0;
				_loop4310:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==VARIABLE)) {
						dec_variable_1(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4310>=1 ) { break _loop4310; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4310++;
				} while (true);
				}
				currentAST = __currentAST4308;
				_t = __t4308;
				_t = _t.getNextSibling();
				dec_variables_1_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			{
				dec_variables_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_variables_1_AST;
		_retTree = _t;
	}
	
	public final void dec_rutina_1(AST _t) throws RecognitionException {
		
		AST dec_rutina_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_rutina_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PROCEDIMIENTO:
			{
				dec_procedimiento_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dec_rutina_1_AST = (AST)currentAST.root;
				break;
			}
			case FUNCION:
			{
				dec_funcion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dec_rutina_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_rutina_1_AST;
		_retTree = _t;
	}
	
	public final void dec_parametros_1(AST _t) throws RecognitionException {
		
		AST dec_parametros_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_parametros_1_AST = null;
		
		try {      // for error handling
			AST __t4317 = _t;
			AST tmp105_AST = null;
			AST tmp105_AST_in = null;
			tmp105_AST = astFactory.create((AST)_t);
			tmp105_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp105_AST);
			ASTPair __currentAST4317 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PARAMETROS);
			_t = _t.getFirstChild();
			{
			_loop4319:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PARAMETRO)) {
					dec_parametro_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4319;
				}
				
			} while (true);
			}
			currentAST = __currentAST4317;
			_t = __t4317;
			_t = _t.getNextSibling();
			dec_parametros_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_parametros_1_AST;
		_retTree = _t;
	}
	
	public final void cabecera_procedimiento_1(AST _t) throws RecognitionException {
		
		AST cabecera_procedimiento_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_procedimiento_1_AST = null;
		
		try {      // for error handling
			AST __t4325 = _t;
			AST tmp106_AST = null;
			AST tmp106_AST_in = null;
			tmp106_AST = astFactory.create((AST)_t);
			tmp106_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp106_AST);
			ASTPair __currentAST4325 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA);
			_t = _t.getFirstChild();
			AST tmp107_AST = null;
			AST tmp107_AST_in = null;
			tmp107_AST = astFactory.create((AST)_t);
			tmp107_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp107_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			dec_parametros_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4325;
			_t = __t4325;
			_t = _t.getNextSibling();
			cabecera_procedimiento_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_procedimiento_1_AST;
		_retTree = _t;
	}
	
	public final void dec_const_var_1(AST _t) throws RecognitionException {
		
		AST dec_const_var_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dec_const_var_1_AST = null;
		
		try {      // for error handling
			AST __t4331 = _t;
			AST tmp108_AST = null;
			AST tmp108_AST_in = null;
			tmp108_AST = astFactory.create((AST)_t);
			tmp108_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp108_AST);
			ASTPair __currentAST4331 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DECLARACIONES);
			_t = _t.getFirstChild();
			dec_constantes_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			dec_variables_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4331;
			_t = __t4331;
			_t = _t.getNextSibling();
			dec_const_var_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dec_const_var_1_AST;
		_retTree = _t;
	}
	
	public final void cuerpo_1(AST _t) throws RecognitionException {
		
		AST cuerpo_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cuerpo_1_AST = null;
		
		try {      // for error handling
			AST __t4333 = _t;
			AST tmp109_AST = null;
			AST tmp109_AST_in = null;
			tmp109_AST = astFactory.create((AST)_t);
			tmp109_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp109_AST);
			ASTPair __currentAST4333 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CUERPO);
			_t = _t.getFirstChild();
			{
			_loop4335:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					instruccion_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4335;
				}
				
			} while (true);
			}
			currentAST = __currentAST4333;
			_t = __t4333;
			_t = _t.getNextSibling();
			cuerpo_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cuerpo_1_AST;
		_retTree = _t;
	}
	
	public final void cabecera_funcion_1(AST _t) throws RecognitionException {
		
		AST cabecera_funcion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_funcion_1_AST = null;
		
		try {      // for error handling
			AST __t4329 = _t;
			AST tmp110_AST = null;
			AST tmp110_AST_in = null;
			tmp110_AST = astFactory.create((AST)_t);
			tmp110_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp110_AST);
			ASTPair __currentAST4329 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA);
			_t = _t.getFirstChild();
			AST tmp111_AST = null;
			AST tmp111_AST_in = null;
			tmp111_AST = astFactory.create((AST)_t);
			tmp111_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp111_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			dec_parametros_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			tipo_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4329;
			_t = __t4329;
			_t = _t.getNextSibling();
			cabecera_funcion_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_funcion_1_AST;
		_retTree = _t;
	}
	
	public final void instruccion_1(AST _t) throws RecognitionException {
		
		AST instruccion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LLAMADA:
			case ASIGNACION:
			case WRITE:
			case READ:
			{
				instruccion_simple_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_1_AST = (AST)currentAST.root;
				break;
			}
			case IF:
			case WHILE:
			case FOR:
			{
				instruccion_compuesta_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_1_AST;
		_retTree = _t;
	}
	
	public final void bloque_1(AST _t) throws RecognitionException {
		
		AST bloque_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bloque_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LLAMADA:
			case ASIGNACION:
			case WRITE:
			case READ:
			{
				instruccion_simple_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				bloque_1_AST = (AST)currentAST.root;
				break;
			}
			case CUERPO:
			{
				cuerpo_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				bloque_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = bloque_1_AST;
		_retTree = _t;
	}
	
	public final void instruccion_simple_1(AST _t) throws RecognitionException {
		
		AST instruccion_simple_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_simple_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASIGNACION:
			{
				asignacion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_1_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			{
				llamada_rutina_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_1_AST = (AST)currentAST.root;
				break;
			}
			case WRITE:
			{
				salida_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_1_AST = (AST)currentAST.root;
				break;
			}
			case READ:
			{
				entrada_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_simple_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_simple_1_AST;
		_retTree = _t;
	}
	
	public final void instruccion_compuesta_1(AST _t) throws RecognitionException {
		
		AST instruccion_compuesta_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instruccion_compuesta_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IF:
			{
				condicional_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_1_AST = (AST)currentAST.root;
				break;
			}
			case WHILE:
			{
				iteracion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_1_AST = (AST)currentAST.root;
				break;
			}
			case FOR:
			{
				iteracion_acotada_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				instruccion_compuesta_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = instruccion_compuesta_1_AST;
		_retTree = _t;
	}
	
	public final void asignacion_1(AST _t) throws RecognitionException {
		
		AST asignacion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST asignacion_1_AST = null;
		
		try {      // for error handling
			AST __t4341 = _t;
			AST tmp112_AST = null;
			AST tmp112_AST_in = null;
			tmp112_AST = astFactory.create((AST)_t);
			tmp112_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp112_AST);
			ASTPair __currentAST4341 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ASIGNACION);
			_t = _t.getFirstChild();
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4341;
			_t = __t4341;
			_t = _t.getNextSibling();
			asignacion_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = asignacion_1_AST;
		_retTree = _t;
	}
	
	public final void llamada_rutina_1(AST _t) throws RecognitionException {
		
		AST llamada_rutina_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST llamada_rutina_1_AST = null;
		
		try {      // for error handling
			AST __t4343 = _t;
			AST tmp113_AST = null;
			AST tmp113_AST_in = null;
			tmp113_AST = astFactory.create((AST)_t);
			tmp113_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp113_AST);
			ASTPair __currentAST4343 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,LLAMADA);
			_t = _t.getFirstChild();
			acceso_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			lista_expresiones_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4343;
			_t = __t4343;
			_t = _t.getNextSibling();
			llamada_rutina_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = llamada_rutina_1_AST;
		_retTree = _t;
	}
	
	public final void salida_1(AST _t) throws RecognitionException {
		
		AST salida_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST salida_1_AST = null;
		
		try {      // for error handling
			AST __t4345 = _t;
			AST tmp114_AST = null;
			AST tmp114_AST_in = null;
			tmp114_AST = astFactory.create((AST)_t);
			tmp114_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp114_AST);
			ASTPair __currentAST4345 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WRITE);
			_t = _t.getFirstChild();
			lista_expresiones_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4345;
			_t = __t4345;
			_t = _t.getNextSibling();
			salida_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = salida_1_AST;
		_retTree = _t;
	}
	
	public final void entrada_1(AST _t) throws RecognitionException {
		
		AST entrada_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entrada_1_AST = null;
		
		try {      // for error handling
			AST __t4347 = _t;
			AST tmp115_AST = null;
			AST tmp115_AST_in = null;
			tmp115_AST = astFactory.create((AST)_t);
			tmp115_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp115_AST);
			ASTPair __currentAST4347 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,READ);
			_t = _t.getFirstChild();
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4347;
			_t = __t4347;
			_t = _t.getNextSibling();
			entrada_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = entrada_1_AST;
		_retTree = _t;
	}
	
	public final void condicional_1(AST _t) throws RecognitionException {
		
		AST condicional_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicional_1_AST = null;
		
		try {      // for error handling
			AST __t4349 = _t;
			AST tmp116_AST = null;
			AST tmp116_AST_in = null;
			tmp116_AST = astFactory.create((AST)_t);
			tmp116_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp116_AST);
			ASTPair __currentAST4349 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,IF);
			_t = _t.getFirstChild();
			condicion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t4350 = _t;
			AST tmp117_AST = null;
			AST tmp117_AST_in = null;
			tmp117_AST = astFactory.create((AST)_t);
			tmp117_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp117_AST);
			ASTPair __currentAST4350 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,BLOQUE_IF);
			_t = _t.getFirstChild();
			bloque_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4350;
			_t = __t4350;
			_t = _t.getNextSibling();
			AST __t4351 = _t;
			AST tmp118_AST = null;
			AST tmp118_AST_in = null;
			tmp118_AST = astFactory.create((AST)_t);
			tmp118_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp118_AST);
			ASTPair __currentAST4351 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,BLOQUE_ELSE);
			_t = _t.getFirstChild();
			bloque_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4351;
			_t = __t4351;
			_t = _t.getNextSibling();
			currentAST = __currentAST4349;
			_t = __t4349;
			_t = _t.getNextSibling();
			condicional_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = condicional_1_AST;
		_retTree = _t;
	}
	
	public final void iteracion_1(AST _t) throws RecognitionException {
		
		AST iteracion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_1_AST = null;
		
		try {      // for error handling
			AST __t4353 = _t;
			AST tmp119_AST = null;
			AST tmp119_AST_in = null;
			tmp119_AST = astFactory.create((AST)_t);
			tmp119_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp119_AST);
			ASTPair __currentAST4353 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WHILE);
			_t = _t.getFirstChild();
			condicion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			bloque_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4353;
			_t = __t4353;
			_t = _t.getNextSibling();
			iteracion_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_1_AST;
		_retTree = _t;
	}
	
	public final void iteracion_acotada_1(AST _t) throws RecognitionException {
		
		AST iteracion_acotada_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iteracion_acotada_1_AST = null;
		
		try {      // for error handling
			AST __t4355 = _t;
			AST tmp120_AST = null;
			AST tmp120_AST_in = null;
			tmp120_AST = astFactory.create((AST)_t);
			tmp120_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp120_AST);
			ASTPair __currentAST4355 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FOR);
			_t = _t.getFirstChild();
			cabecera_for_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t4356 = _t;
			AST tmp121_AST = null;
			AST tmp121_AST_in = null;
			tmp121_AST = astFactory.create((AST)_t);
			tmp121_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp121_AST);
			ASTPair __currentAST4356 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,BLOQUE_FOR);
			_t = _t.getFirstChild();
			bloque_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4356;
			_t = __t4356;
			_t = _t.getNextSibling();
			currentAST = __currentAST4355;
			_t = __t4355;
			_t = _t.getNextSibling();
			iteracion_acotada_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = iteracion_acotada_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_1(AST _t) throws RecognitionException {
		
		AST acceso_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ACCESO_REGISTRO:
			{
				acceso_registro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY_REGISTRO:
			{
				acceso_array_registro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY:
			{
				acceso_array_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_SIMPLE:
			{
				acceso_simple_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_1_AST;
		_retTree = _t;
	}
	
	public final void lista_expresiones_1(AST _t) throws RecognitionException {
		
		AST lista_expresiones_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lista_expresiones_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EXPRESIONES:
			{
				AST __t4361 = _t;
				AST tmp122_AST = null;
				AST tmp122_AST_in = null;
				tmp122_AST = astFactory.create((AST)_t);
				tmp122_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp122_AST);
				ASTPair __currentAST4361 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EXPRESIONES);
				_t = _t.getFirstChild();
				{
				int _cnt4363=0;
				_loop4363:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_3.member(_t.getType()))) {
						expresion_1(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt4363>=1 ) { break _loop4363; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt4363++;
				} while (true);
				}
				currentAST = __currentAST4361;
				_t = __t4361;
				_t = _t.getNextSibling();
				lista_expresiones_1_AST = (AST)currentAST.root;
				break;
			}
			case 3:
			{
				lista_expresiones_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = lista_expresiones_1_AST;
		_retTree = _t;
	}
	
	public final void condicion_1(AST _t) throws RecognitionException {
		
		AST condicion_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST condicion_1_AST = null;
		
		try {      // for error handling
			AST __t4365 = _t;
			AST tmp123_AST = null;
			AST tmp123_AST_in = null;
			tmp123_AST = astFactory.create((AST)_t);
			tmp123_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp123_AST);
			ASTPair __currentAST4365 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CONDICION);
			_t = _t.getFirstChild();
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4365;
			_t = __t4365;
			_t = _t.getNextSibling();
			condicion_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = condicion_1_AST;
		_retTree = _t;
	}
	
	public final void cabecera_for_1(AST _t) throws RecognitionException {
		
		AST cabecera_for_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cabecera_for_1_AST = null;
		
		try {      // for error handling
			AST __t4358 = _t;
			AST tmp124_AST = null;
			AST tmp124_AST_in = null;
			tmp124_AST = astFactory.create((AST)_t);
			tmp124_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp124_AST);
			ASTPair __currentAST4358 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CABECERA_FOR);
			_t = _t.getFirstChild();
			acceso_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TO:
			{
				AST tmp125_AST = null;
				AST tmp125_AST_in = null;
				tmp125_AST = astFactory.create((AST)_t);
				tmp125_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp125_AST);
				match(_t,TO);
				_t = _t.getNextSibling();
				break;
			}
			case DOWNTO:
			{
				AST tmp126_AST = null;
				AST tmp126_AST_in = null;
				tmp126_AST = astFactory.create((AST)_t);
				tmp126_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp126_AST);
				match(_t,DOWNTO);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4358;
			_t = __t4358;
			_t = _t.getNextSibling();
			cabecera_for_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = cabecera_for_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_registro_1(AST _t) throws RecognitionException {
		
		AST acceso_registro_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_registro_1_AST = null;
		
		try {      // for error handling
			AST __t4387 = _t;
			AST tmp127_AST = null;
			AST tmp127_AST_in = null;
			tmp127_AST = astFactory.create((AST)_t);
			tmp127_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp127_AST);
			ASTPair __currentAST4387 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_REGISTRO);
			_t = _t.getFirstChild();
			acceso_simple_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			acceso_ext_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4387;
			_t = __t4387;
			_t = _t.getNextSibling();
			acceso_registro_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_registro_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_registro_1(AST _t) throws RecognitionException {
		
		AST acceso_array_registro_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_registro_1_AST = null;
		
		try {      // for error handling
			AST __t4389 = _t;
			AST tmp128_AST = null;
			AST tmp128_AST_in = null;
			tmp128_AST = astFactory.create((AST)_t);
			tmp128_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp128_AST);
			ASTPair __currentAST4389 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY_REGISTRO);
			_t = _t.getFirstChild();
			acceso_simple_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			indices_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			acceso_ext_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4389;
			_t = __t4389;
			_t = _t.getNextSibling();
			acceso_array_registro_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_registro_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_1(AST _t) throws RecognitionException {
		
		AST acceso_array_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_1_AST = null;
		
		try {      // for error handling
			AST __t4391 = _t;
			AST tmp129_AST = null;
			AST tmp129_AST_in = null;
			tmp129_AST = astFactory.create((AST)_t);
			tmp129_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp129_AST);
			ASTPair __currentAST4391 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY);
			_t = _t.getFirstChild();
			acceso_simple_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			indices_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4391;
			_t = __t4391;
			_t = _t.getNextSibling();
			acceso_array_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_simple_1(AST _t) throws RecognitionException {
		
		AST acceso_simple_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_simple_1_AST = null;
		
		try {      // for error handling
			AST __t4393 = _t;
			AST tmp130_AST = null;
			AST tmp130_AST_in = null;
			tmp130_AST = astFactory.create((AST)_t);
			tmp130_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp130_AST);
			ASTPair __currentAST4393 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_SIMPLE);
			_t = _t.getFirstChild();
			AST tmp131_AST = null;
			AST tmp131_AST_in = null;
			tmp131_AST = astFactory.create((AST)_t);
			tmp131_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp131_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			declaracion_acceso_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4393;
			_t = __t4393;
			_t = _t.getNextSibling();
			acceso_simple_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_simple_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_ext_1(AST _t) throws RecognitionException {
		
		AST acceso_ext_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_ext_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ACCESO_REGISTRO:
			{
				acceso_registro_ext_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY_REGISTRO:
			{
				acceso_array_registro_ext_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_1_AST = (AST)currentAST.root;
				break;
			}
			case ACCESO_ARRAY:
			{
				acceso_array_ext_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_1_AST = (AST)currentAST.root;
				break;
			}
			case IDENT:
			{
				acceso_simple_ext_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				acceso_ext_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_ext_1_AST;
		_retTree = _t;
	}
	
	public final void indices_1(AST _t) throws RecognitionException {
		
		AST indices_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indices_1_AST = null;
		
		try {      // for error handling
			AST __t4404 = _t;
			AST tmp132_AST = null;
			AST tmp132_AST_in = null;
			tmp132_AST = astFactory.create((AST)_t);
			tmp132_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp132_AST);
			ASTPair __currentAST4404 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,INDICES);
			_t = _t.getFirstChild();
			{
			int _cnt4406=0;
			_loop4406:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_3.member(_t.getType()))) {
					expresion_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt4406>=1 ) { break _loop4406; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4406++;
			} while (true);
			}
			currentAST = __currentAST4404;
			_t = __t4404;
			_t = _t.getNextSibling();
			indices_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = indices_1_AST;
		_retTree = _t;
	}
	
	public final void declaracion_acceso_1(AST _t) throws RecognitionException {
		
		AST declaracion_acceso_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaracion_acceso_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VARIABLE_RESULTADO:
			{
				dec_variable_resultado_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case VARIABLE:
			{
				dec_variable_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				dec_registro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			{
				dec_constante_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case PROCEDIMIENTO:
			{
				dec_procedimiento_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case FUNCION:
			{
				dec_funcion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			case PARAMETRO:
			{
				dec_parametro_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				declaracion_acceso_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = declaracion_acceso_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_registro_ext_1(AST _t) throws RecognitionException {
		
		AST acceso_registro_ext_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_registro_ext_1_AST = null;
		
		try {      // for error handling
			AST __t4397 = _t;
			AST tmp133_AST = null;
			AST tmp133_AST_in = null;
			tmp133_AST = astFactory.create((AST)_t);
			tmp133_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp133_AST);
			ASTPair __currentAST4397 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_REGISTRO);
			_t = _t.getFirstChild();
			AST tmp134_AST = null;
			AST tmp134_AST_in = null;
			tmp134_AST = astFactory.create((AST)_t);
			tmp134_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp134_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			acceso_ext_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4397;
			_t = __t4397;
			_t = _t.getNextSibling();
			acceso_registro_ext_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_registro_ext_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_registro_ext_1(AST _t) throws RecognitionException {
		
		AST acceso_array_registro_ext_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_registro_ext_1_AST = null;
		
		try {      // for error handling
			AST __t4399 = _t;
			AST tmp135_AST = null;
			AST tmp135_AST_in = null;
			tmp135_AST = astFactory.create((AST)_t);
			tmp135_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp135_AST);
			ASTPair __currentAST4399 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY_REGISTRO);
			_t = _t.getFirstChild();
			AST tmp136_AST = null;
			AST tmp136_AST_in = null;
			tmp136_AST = astFactory.create((AST)_t);
			tmp136_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp136_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			indices_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			acceso_ext_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4399;
			_t = __t4399;
			_t = _t.getNextSibling();
			acceso_array_registro_ext_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_registro_ext_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_array_ext_1(AST _t) throws RecognitionException {
		
		AST acceso_array_ext_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_array_ext_1_AST = null;
		
		try {      // for error handling
			AST __t4401 = _t;
			AST tmp137_AST = null;
			AST tmp137_AST_in = null;
			tmp137_AST = astFactory.create((AST)_t);
			tmp137_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp137_AST);
			ASTPair __currentAST4401 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ACCESO_ARRAY);
			_t = _t.getFirstChild();
			AST tmp138_AST = null;
			AST tmp138_AST_in = null;
			tmp138_AST = astFactory.create((AST)_t);
			tmp138_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp138_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			indices_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4401;
			_t = __t4401;
			_t = _t.getNextSibling();
			acceso_array_ext_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_array_ext_1_AST;
		_retTree = _t;
	}
	
	public final void acceso_simple_ext_1(AST _t) throws RecognitionException {
		
		AST acceso_simple_ext_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST acceso_simple_ext_1_AST = null;
		
		try {      // for error handling
			AST tmp139_AST = null;
			AST tmp139_AST_in = null;
			tmp139_AST = astFactory.create((AST)_t);
			tmp139_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp139_AST);
			match(_t,IDENT);
			_t = _t.getNextSibling();
			acceso_simple_ext_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = acceso_simple_ext_1_AST;
		_retTree = _t;
	}
	
	public final void simple_1(AST _t) throws RecognitionException {
		
		AST simple_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simple_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INTEGER:
			{
				AST tmp140_AST = null;
				AST tmp140_AST_in = null;
				tmp140_AST = astFactory.create((AST)_t);
				tmp140_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp140_AST);
				match(_t,INTEGER);
				_t = _t.getNextSibling();
				simple_1_AST = (AST)currentAST.root;
				break;
			}
			case REAL:
			{
				AST tmp141_AST = null;
				AST tmp141_AST_in = null;
				tmp141_AST = astFactory.create((AST)_t);
				tmp141_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp141_AST);
				match(_t,REAL);
				_t = _t.getNextSibling();
				simple_1_AST = (AST)currentAST.root;
				break;
			}
			case BOOLEAN:
			{
				AST tmp142_AST = null;
				AST tmp142_AST_in = null;
				tmp142_AST = astFactory.create((AST)_t);
				tmp142_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp142_AST);
				match(_t,BOOLEAN);
				_t = _t.getNextSibling();
				simple_1_AST = (AST)currentAST.root;
				break;
			}
			case CHAR:
			{
				AST tmp143_AST = null;
				AST tmp143_AST_in = null;
				tmp143_AST = astFactory.create((AST)_t);
				tmp143_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp143_AST);
				match(_t,CHAR);
				_t = _t.getNextSibling();
				simple_1_AST = (AST)currentAST.root;
				break;
			}
			case RANGO:
			{
				tipo_rango_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				simple_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = simple_1_AST;
		_retTree = _t;
	}
	
	public final void definido_1(AST _t) throws RecognitionException {
		
		AST definido_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definido_1_AST = null;
		
		try {      // for error handling
			dec_registro_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			definido_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = definido_1_AST;
		_retTree = _t;
	}
	
	public final void compuesto_1(AST _t) throws RecognitionException {
		
		AST compuesto_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compuesto_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			{
				array_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				compuesto_1_AST = (AST)currentAST.root;
				break;
			}
			case STRING:
			{
				string_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				compuesto_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = compuesto_1_AST;
		_retTree = _t;
	}
	
	public final void otro_1(AST _t) throws RecognitionException {
		
		AST otro_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST otro_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FUNCION:
			{
				dec_funcion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_1_AST = (AST)currentAST.root;
				break;
			}
			case PROCEDIMIENTO:
			{
				dec_procedimiento_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_1_AST = (AST)currentAST.root;
				break;
			}
			case CONSTANTE:
			{
				dec_constante_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_1_AST = (AST)currentAST.root;
				break;
			}
			case VARIABLE:
			{
				dec_variable_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				otro_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = otro_1_AST;
		_retTree = _t;
	}
	
	public final void tipo_array_1(AST _t) throws RecognitionException {
		
		AST tipo_array_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_array_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case REAL:
			case INTEGER:
			case BOOLEAN:
			case CHAR:
			case RANGO:
			{
				simple_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_array_1_AST = (AST)currentAST.root;
				break;
			}
			case REGISTRO:
			{
				definido_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_array_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_array_1_AST;
		_retTree = _t;
	}
	
	public final void tipo_rango_1(AST _t) throws RecognitionException {
		
		AST tipo_rango_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tipo_rango_1_AST = null;
		
		try {      // for error handling
			boolean synPredMatched4426 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==RANGO))) {
				AST __t4426 = _t;
				synPredMatched4426 = true;
				inputState.guessing++;
				try {
					{
					AST __t4425 = _t;
					ASTPair __currentAST4425 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,RANGO);
					_t = _t.getFirstChild();
					match(_t,LIT_CAR);
					_t = _t.getNextSibling();
					currentAST = __currentAST4425;
					_t = __t4425;
					_t = _t.getNextSibling();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched4426 = false;
				}
				_t = __t4426;
inputState.guessing--;
			}
			if ( synPredMatched4426 ) {
				rango_caracter_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_rango_1_AST = (AST)currentAST.root;
			}
			else if ((_t.getType()==RANGO)) {
				rango_entero_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				tipo_rango_1_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(_t);
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = tipo_rango_1_AST;
		_retTree = _t;
	}
	
	public final void array_1(AST _t) throws RecognitionException {
		
		AST array_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST array_1_AST = null;
		
		try {      // for error handling
			AST __t4414 = _t;
			AST tmp144_AST = null;
			AST tmp144_AST_in = null;
			tmp144_AST = astFactory.create((AST)_t);
			tmp144_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp144_AST);
			ASTPair __currentAST4414 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ARRAY);
			_t = _t.getFirstChild();
			dimensiones_array_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			tipo_array_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4414;
			_t = __t4414;
			_t = _t.getNextSibling();
			array_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = array_1_AST;
		_retTree = _t;
	}
	
	public final void string_1(AST _t) throws RecognitionException {
		
		AST string_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST string_1_AST = null;
		
		try {      // for error handling
			AST __t4421 = _t;
			AST tmp145_AST = null;
			AST tmp145_AST_in = null;
			tmp145_AST = astFactory.create((AST)_t);
			tmp145_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp145_AST);
			ASTPair __currentAST4421 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,STRING);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIT_ENTERO:
			{
				AST tmp146_AST = null;
				AST tmp146_AST_in = null;
				tmp146_AST = astFactory.create((AST)_t);
				tmp146_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp146_AST);
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST4421;
			_t = __t4421;
			_t = _t.getNextSibling();
			string_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = string_1_AST;
		_retTree = _t;
	}
	
	public final void dimensiones_array_1(AST _t) throws RecognitionException {
		
		AST dimensiones_array_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimensiones_array_1_AST = null;
		
		try {      // for error handling
			AST __t4416 = _t;
			AST tmp147_AST = null;
			AST tmp147_AST_in = null;
			tmp147_AST = astFactory.create((AST)_t);
			tmp147_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp147_AST);
			ASTPair __currentAST4416 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DIMENSIONES);
			_t = _t.getFirstChild();
			{
			int _cnt4418=0;
			_loop4418:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_4.member(_t.getType()))) {
					dimension_1(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt4418>=1 ) { break _loop4418; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt4418++;
			} while (true);
			}
			currentAST = __currentAST4416;
			_t = __t4416;
			_t = _t.getNextSibling();
			dimensiones_array_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dimensiones_array_1_AST;
		_retTree = _t;
	}
	
	public final void dimension_1(AST _t) throws RecognitionException {
		
		AST dimension_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimension_1_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case RANGO:
			{
				rango_entero_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dimension_1_AST = (AST)currentAST.root;
				break;
			}
			case LLAMADA:
			case ACCESO_SIMPLE:
			case MAYOR:
			case MAYOR_IGUAL:
			case MENOR:
			case MENOR_IGUAL:
			case MAS:
			case MENOS:
			case POR:
			case LIT_ENTERO:
			case LIT_REAL:
			case LIT_CAR:
			case OR:
			case AND:
			case NOT:
			case DIVISION_ENTERA:
			case DIVISION_REAL:
			case MOD:
			case CADENA:
			case TRUE:
			case FALSE:
			case ACCESO_REGISTRO:
			case ACCESO_ARRAY_REGISTRO:
			case ACCESO_ARRAY:
			{
				expresion_1(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				dimension_1_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = dimension_1_AST;
		_retTree = _t;
	}
	
	public final void rango_entero_1(AST _t) throws RecognitionException {
		
		AST rango_entero_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rango_entero_1_AST = null;
		
		try {      // for error handling
			AST __t4430 = _t;
			AST tmp148_AST = null;
			AST tmp148_AST_in = null;
			tmp148_AST = astFactory.create((AST)_t);
			tmp148_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp148_AST);
			ASTPair __currentAST4430 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			expresion_1(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST4430;
			_t = __t4430;
			_t = _t.getNextSibling();
			rango_entero_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = rango_entero_1_AST;
		_retTree = _t;
	}
	
	public final void rango_caracter_1(AST _t) throws RecognitionException {
		
		AST rango_caracter_1_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rango_caracter_1_AST = null;
		
		try {      // for error handling
			AST __t4428 = _t;
			AST tmp149_AST = null;
			AST tmp149_AST_in = null;
			tmp149_AST = astFactory.create((AST)_t);
			tmp149_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp149_AST);
			ASTPair __currentAST4428 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			AST tmp150_AST = null;
			AST tmp150_AST_in = null;
			tmp150_AST = astFactory.create((AST)_t);
			tmp150_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp150_AST);
			match(_t,LIT_CAR);
			_t = _t.getNextSibling();
			AST tmp151_AST = null;
			AST tmp151_AST_in = null;
			tmp151_AST = astFactory.create((AST)_t);
			tmp151_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp151_AST);
			match(_t,LIT_CAR);
			_t = _t.getNextSibling();
			currentAST = __currentAST4428;
			_t = __t4428;
			_t = _t.getNextSibling();
			rango_caracter_1_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		returnAST = rango_caracter_1_AST;
		_retTree = _t;
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
		"FORMACION",
		"PROGRAMA",
		"DECLARACIONES",
		"CONSTANTE",
		"REGISTRO",
		"CAMPO",
		"VARIABLE",
		"RUTINAS",
		"PARAMETROS",
		"PARAMETRO",
		"PROCEDIMIENTO",
		"CABECERA",
		"VACIO",
		"FUNCION",
		"CUERPO",
		"WRITE",
		"READ",
		"BLOQUE_IF",
		"BLOQUE_ELSE",
		"BLOQUE_FOR",
		"CABECERA_FOR",
		"CONDICION",
		"OR",
		"AND",
		"NOT",
		"DIVISION_ENTERA",
		"DIVISION_REAL",
		"MOD",
		"CADENA",
		"TRUE",
		"FALSE",
		"ACCESO_REGISTRO",
		"ACCESO_ARRAY_REGISTRO",
		"ACCESO_ARRAY",
		"INDICES",
		"INTEGER",
		"BOOLEAN",
		"CHAR",
		"ARRAY",
		"DIMENSIONES",
		"STRING",
		"RANGO",
		"VARIABLE_RESULTADO"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 88583700736L, 49152L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 32646699254024448L, 8587837440L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 32646699254024448L, 1108099465216L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 32576330507749632L, 8587837440L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 32576330507749632L, 1108099465216L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	}
	
