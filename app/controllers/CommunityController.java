package controllers;

import java.net.URLEncoder;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.*;
 
import play.*;
import play.mvc.*;
import play.libs.Crypto;
import play.libs.Mail;
import play.data.validation.*;
import java.io.UnsupportedEncodingException;

import notifiers.*;
import models.*;
import play.*;
import play.i18n.Messages;

import javax.naming.NamingEnumeration;
import javax.naming.directory.*;

@With(Secure.class)
public class CommunityController extends Controller {

	public static void index() {
	
		List<Community> communities_list = Community.findAll();
		
		render("Community/index.html", communities_list);
	}
	
	public static void createCommunityIndex() {
		render("Community/createCommunityIndex.html");
	}
	
	public static void createNewCommunity(
	  @Required String nom_communaute,
	  @Required String prefixe_communaute,
	  @Required String application_link,
	  @Required String message_description,
	  @Required String message_bienvenue
	){
	 
		try {
		
			if (validation.hasErrors()){
                render("Community/createCommunityIndex.html");
            } 
			else {
				
				Community communaute = new Community (
				nom_communaute,
				prefixe_communaute,
				application_link,
				message_description,
				message_bienvenue).save();				
				
				flash.success(Messages.get("create_community_success"));
				
				render("Community/createCommunityIndex.html");
				//index();
			}
	   
        } catch (Exception e) {
		System.out.println("An exception occurred in CommunityController.createNewCommunity");
		e.printStackTrace();
		render("Community/createCommunityIndex.html"); }
		
	}
	
	public static void displayCommunityUpdate(Long id) {
	
		Community community_retrieved = Community.findById(id);
		
		render("Community/updateCommunityIndex.html", community_retrieved);
	
	}
	
	public static void updateCommunity(
	  Long id,
	  @Required String nom_communaute,
	  @Required String prefixe_communaute,
	  @Required String application_link,
	  @Required String message_description,
	  @Required String message_bienvenue){
	
		Community community_retrieved = Community.findById(id);
		
		if (validation.hasErrors()){
                render("Community/updateCommunityIndex.html",community_retrieved);
        } 
		else {
			
			// Updating the community attributes with the new ones
			if (nom_communaute != null) {
				community_retrieved.name = nom_communaute;
			}
			
			if (prefixe_communaute != null) {
				community_retrieved.communityPrefix = prefixe_communaute.toUpperCase();
				
				// Updating the dolomiteURL of the community
				community_retrieved.dolomiteURL = "";
				community_retrieved.dolomiteURL = "http://"+prefixe_communaute.toLowerCase()+Play.configuration.getProperty("domain");
			}
			
			if (message_description != null) {
				community_retrieved.descriptionText = message_description;
			}
			
			if (message_bienvenue != null) {
				community_retrieved.welcomingMessage = message_bienvenue;
			}
			
			if (application_link != null) {
				community_retrieved.applicationURL = application_link;
			}
			
			community_retrieved.save();
			
			flash.success(Messages.get("update_community_success"));
			
			render("Community/updateCommunityIndex.html",community_retrieved);
		}
		//index();		
	}
	
	public static void deleteCommunity(Long id) {
	
		Community community_deleted = Community.findById(id);		
		community_deleted.delete();
		
		index();
	}	
}
