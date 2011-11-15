package controllers;

import java.io.*;
import java.util.logging.*;
import models.LdapUser;
import play.mvc.*;
import play.libs.Crypto;
import play.data.validation.*;
import play.*;
import play.i18n.Messages;

public class Inscription extends BaseController {

	public static void adduser(
		@Required(message="The first password is required") String password1,
		@Required(message="The second password is required") String password2,
		@Required(message="The firstname is required") String firstname,
		@Required(message="The lastname is required") String lastname,
		@Required(message="The login is required") String login,
		@Required(message="The email is required") String email,
		String signature) {
		System.out.println('1');
		int result = -1;
        String checkResult;
		try {
			if (signature.equals(Crypto.sign(firstname + lastname + email))) {
				if (validation.hasErrors()) {
					render("Application/inscription.html");
				} else {
                    checkResult = checkPass(password1,password2);
                    if ( checkResult == null){ //valid passwords
                        result = new LdapUser(email, password1, firstname, lastname, login).addUser();
                        System.out.println(result);
                        if (result==0) {
                            //user doesn't exist yet
                            flash.now("success","You have been successfully registered " + firstname + " " + lastname + "." );
                        } else if(result==1) {
                            //user already exists
                            flash.now("success","You have already been successfully registered " + firstname + " " + lastname + "." );
                        }
                        render("Application/inscription.html");
					}
					else{
						flash.now("error",checkResult);
                        render("Application/inscription.html", firstname, lastname, email, signature);
					}
                }
			} else {
				flash.now("error",Messages.get("msg_signature_no_match"));
				render("Application/inscription.html");
			}
		} catch (Exception e) {
			System.out.println("An exception occurred in Inscription.adduser()");
			e.printStackTrace();
			render("Application/index.html");
		}
	}

	public static void resetPass (
		@Required(message="The first password is required") String password1,
		@Required(message="The second password is required") String password2,
		@Required String firstname,
		@Required String lastname,
		@Required(message="The login is required") String login,
		@Required(message="The email is required") String email,
		@Required(message="Invalid signature") String signature) {

		String checkResult;
		try {
			if (signature.equals(Crypto.sign(firstname + lastname + email))) {
				if (validation.hasErrors()) {
					render("Application/reset.html");
				} else {
					checkResult = checkPass(password1,password2);
					System.out.println("v: "+validation);
					if ( checkResult == null){ //valid passwords
						// retrieve the user and change the password
						new LdapUser(email, password1, firstname, lastname, login).updateUser(email,password1,firstname,lastname);
						flash.now("success","Your password has been successfully changed");
						System.out.println("params: "+params.toString());
						params.remove(signature);
					}
					else{
						flash.now("error",checkResult);
					}
				}
			}
			render("Application/reset.html");
		} catch (Exception e)
		{
			render("Application/index.html");
		}

	}

	public static String checkPass(String password1, String password2) {
		if (password1.length() < 6) 
			return Messages.get("error_short_pass_msg");
		if (!password2.equals(password1)) 
			return Messages.get("error_pass_no_match_msg");
		if (!password1.matches("[a-zA-Z]") || !password1.matches("[0-9]")) 
			return Messages.get("error_alfanum_pass_msg");
		return null;
	}

}



