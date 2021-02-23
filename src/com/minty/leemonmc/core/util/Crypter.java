package com.minty.leemonmc.core.util;

public class Crypter {

	public Crypter() {
		
	}
	
	public String encrypt(String password, int key){
        String crypte = "";
        for (int i=0; i<password.length();i++)  {
            int c=password.charAt(i)^key; 
            crypte=crypte+(char)c;
        }
        return crypte;
    }

  public String decrypt(String password, int key){
        String aCrypter= "";
        for (int i=0; i<password.length();i++)  {
            int c=password.charAt(i)^key; 
            aCrypter=aCrypter+(char)c;
        }
        return aCrypter;
   }

}
