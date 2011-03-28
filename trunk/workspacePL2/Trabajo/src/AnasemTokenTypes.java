// $ANTLR : "Anasem.g" -> "Anasem.java"$

	import java.util.*;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;

public interface AnasemTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int EXPRESIONES = 4;
	int INSTRUCCION = 5;
	int INSTRUCCIONES = 6;
	int MENOSUNARIO = 7;
	int LLAMADA = 8;
	int ACCESO_TABLA = 9;
	int ACCESO_OBJETO = 10;
	int ACCESO_SIMPLE = 11;
	int LISTA_ENTEROS = 12;
	int PUNTO_Y_COMA = 13;
	int PROGRAM = 14;
	int IDENT = 15;
	int CONST = 16;
	int TYPE = 17;
	int VAR = 18;
	int BEGIN = 19;
	int END = 20;
	int IGUAL = 21;
	int RECORD = 22;
	int DOS_PUNTOS = 23;
	int FUNCTION = 24;
	int PARENTESIS_ABIERTO = 25;
	int COMA = 26;
	int PARENTESIS = 27;
	int CERRADO = 28;
	int ASIGNACION = 29;
	int PARENTESIS_CERRADO = 30;
	int IF = 31;
	int THEN = 32;
	int ELSE = 33;
	int WHILE = 34;
	int DO = 35;
	int FOR = 36;
	int TO = 37;
	int DOWNTO = 38;
	int O = 39;
	int Y = 40;
	int NO = 41;
	int MAYOR = 42;
	int MAYOR_IGUAL = 43;
	int MENOR = 44;
	int MENOR_IGUAL = 45;
	int DISTINTO = 46;
	int MAS = 47;
	int MENOS = 48;
	int POR = 49;
	int DIVISION = 50;
	int CORCHETE_ABIERTO = 51;
	int LIT_ENTERO = 52;
	int LIT_REAL = 53;
	int LIT_CAR = 54;
	int CIERTO = 55;
	int FALSO = 56;
	int CORCHETE_CERRADO = 57;
	int PUNTO = 58;
	int ENTERO = 59;
	int REAL = 60;
	int LOGICO = 61;
	int CARACTER = 62;
	int FORMACION = 63;
	int PROGRAMA = 64;
	int DECLARACIONES = 65;
	int CONSTANTE = 66;
	int REGISTRO = 67;
	int CAMPO = 68;
	int VARIABLE = 69;
	int RUTINAS = 70;
	int PARAMETROS = 71;
	int PARAMETRO = 72;
	int PROCEDIMIENTO = 73;
	int CABECERA = 74;
	int VACIO = 75;
	int FUNCION = 76;
	int CUERPO = 77;
	int WRITE = 78;
	int READ = 79;
	int BLOQUE_IF = 80;
	int BLOQUE_ELSE = 81;
	int BLOQUE_FOR = 82;
	int CABECERA_FOR = 83;
	int CONDICION = 84;
	int OR = 85;
	int AND = 86;
	int NOT = 87;
	int DIVISION_ENTERA = 88;
	int DIVISION_REAL = 89;
	int MOD = 90;
	int CADENA = 91;
	int TRUE = 92;
	int FALSE = 93;
	int ACCESO_REGISTRO = 94;
	int ACCESO_ARRAY_REGISTRO = 95;
	int ACCESO_ARRAY = 96;
	int INDICES = 97;
	int INTEGER = 98;
	int BOOLEAN = 99;
	int CHAR = 100;
	int ARRAY = 101;
	int DIMENSIONES = 102;
	int STRING = 103;
	int RANGO = 104;
	int VARIABLE_RESULTADO = 105;
}
