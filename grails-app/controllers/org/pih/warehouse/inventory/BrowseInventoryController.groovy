/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 

package org.pih.warehouse.inventory;

import grails.validation.ValidationException;
import groovy.sql.Sql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pih.warehouse.shipping.ShipmentStatusCode;
import org.pih.warehouse.util.DateUtil;
import org.pih.warehouse.core.Constants;
import org.pih.warehouse.core.Location 
import org.pih.warehouse.core.User;
import org.pih.warehouse.product.Category;
import org.pih.warehouse.product.Product;
import org.pih.warehouse.inventory.Transaction;
import org.pih.warehouse.core.Location;

import org.pih.warehouse.reporting.Consumption;

class BrowseInventoryController {
	
	def dataSource
    def productService;	
	def inventoryService;

    def index = {
		redirect(action: "list");
	}
	
	
	def list = {
		def q = params.q 
		def category = Category.get(params?.category?.id)?:null
		def location = Location.get(params?.location?.id)?:session.warehouse
		if (!location) { 
			throw new Exception("Location is required")
		}				
		//def inventorySnapshots = InventorySnapshot.findAllByLocation(location)
		def inventorySnapshots = InventorySnapshot.withCriteria {
			and {
								
				eq("location", location)
				if (category) { 
					product { 
						eq("category", category)
					}
				}
				if (q) { 
					product {
						ilike("name", "%" + q + "%")
					}
				}
			}
		}

		
		def locations = Location.list();
		def categories = Category.list();
		
		[inventorySnapshots: inventorySnapshots, locations: locations, categories: categories, selectedLocation: location]
		
	}
	
	
}