/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 
package org.pih.warehouse.product

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.pih.warehouse.core.Constants;
import org.pih.warehouse.core.MailService;
import org.pih.warehouse.core.Person;
import org.pih.warehouse.core.User;
import org.pih.warehouse.core.Location;
import org.pih.warehouse.inventory.InventoryItem;
import org.pih.warehouse.inventory.InventoryService;
import org.pih.warehouse.inventory.TransactionException;
import org.pih.warehouse.product.Product;
import org.pih.warehouse.report.ReportService;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.validation.Errors;

import sun.util.logging.resources.logging;

class CreateProductFromTemplateController {
	
	MailService mailService
	ProductService productService
	InventoryService inventoryService
	
	def index =  { 
		redirect(action: "create")
	}
	
	
    def createFlow = {    	
    	start {
    		action {
    			flow.product = new ProductGloveCommand()
				//[product:flow.product]
    		}
    		on("success").to("chooseTemplate")
			on(Exception).to("error")
    	}
    		
    	chooseTemplate {
			on("back").to("start")
			on("next") { 		
				log.info "Choose Template (on next) : " + params

						
				flow.category = Category.findByNameLike(params.templateName)
				flow.product.category = flow.category
				flow.templateName = params.templateName
				
			}.to("enterDetails")
			on("error").to("confirmDetails")
			on("cancel").to("finish")	
			on(Exception).to("error")
			on("chooseTemplate").to("chooseTemplate")
			on("enterDetails").to("enterDetails")
			on("confirmDetails").to("confirmDetails")
			//on("showProduct").to("showProduct")
    	}
		enterDetails { 
			on("back").to("chooseTemplate")
			on("next") { ProductGloveCommand product ->
				log.info "Enter Details (on next) : " + params
				
				flow.product = product
				if (product.hasErrors()) { 
					flash.message = "errors"
					error()
				}
				else { 
					//flash.message = "Product details have been entered"					
				}
				
				
			}.to("confirmDetails")
			
			on("error").to("confirmDetails")
			on("cancel").to("finish")
			on(Exception).to("error")
			on("chooseTemplate").to("chooseTemplate")
			on("enterDetails").to("enterDetails")
			on("confirmDetails").to("confirmDetails")
			//on("showProduct").to("showProduct")
			
		}
		confirmDetails { 
			on("back").to("enterDetails")
			on("next") { 
				// Save product 
				if (flow.product.validate() && !flow.product.hasErrors()) { 
					def product = flow.product
					def productInstance = new Product();
					productInstance.name = product.title
					productInstance.description = product.description
					productInstance.category = product.category
					productInstance.save(flush:true);
					flow.productInstance = productInstance
					flash.message = "Product details have been saved to the database with ID " + productInstance.id
					
				}
				else { 
					flash.message = "fix errors"
					error()
				}
				
			}.to("showProduct")
			on("error").to("enterDetails")
			on("cancel").to("finish")
			on(Exception).to("error")
			on("chooseTemplate").to("chooseTemplate")
			on("enterDetails").to("enterDetails")
			on("confirmDetails").to("confirmDetails")
			on("showProduct").to("showProduct")
		}
		showProduct { 
			on("back").to("confirmDetails")
			on("next").to("finish")
			on(Exception).to("start")
			on("chooseTemplate").to("chooseTemplate")
			on("enterDetails").to("enterDetails")
			on("confirmDetails").to("confirmDetails")
			on("showProduct").to("showProduct")
			
		}
		error { 
			on("startOver").to("start")
			on(Exception).to("start")
			on("chooseTemplate").to("chooseTemplate")
			on("enterDetails").to("enterDetails")
			on("confirmDetails").to("confirmDetails")
			//on("showProduct").to("showProduct")
		}
		finish { 			
			redirect(controller: "createProductFromTemplate", action: "index")
		}
    }
}


