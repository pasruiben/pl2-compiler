/////////////////////////////////////
// Procesador.java (clase principal)
/////////////////////////////////////

import java.io.*;
import antlr.*;

public class Procesador 
{	
	static boolean Analex = false; //do only lexical analysis?
	
	public static void main(String args[]) 
	{
		if (Analex)
		{
			try 
			{
				FileInputStream fis = new FileInputStream(args[0]);
	
				Analex analex = new Analex(fis);
	
				Token token = analex.nextToken();
	
				while(token.getType() != Token.EOF_TYPE) 
				{
					System.out.println(token);
					
					token = analex.nextToken();
				}
			}
			catch(ANTLRException ae) 
			{
				System.err.println(ae.getMessage());
			}
			catch(FileNotFoundException fnfe) 
			{
				System.err.println("No se encontró el fichero");
			}
		}
		else
		{
			try
			{
				InputStream fis = new FileInputStream(args[0]);
				Analex analex = new Analex(fis);
				Anasint anasint = new Anasint(analex);
				anasint.entrada();
			}
			catch(ANTLRException ae)
			{
				System.err.println(ae.getMessage());
			}
			catch(FileNotFoundException fnfe)
			{
				System.err.println("No se encontró el fichero");
			}
		}
	}
}
