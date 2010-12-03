package org.pih.warehouse.product;

import java.util.Date;

class ProductGroup {

    String name
	String code
	String description
	Integer sortOrder = 0;
	Date dateCreated;
	Date lastUpdated;
	
	static constraints = {
		name(nullable:false)
		code(nullable:false)
		description(nullable:true)
		sortOrder(nullable:true)
		dateCreated(display:false)
		lastUpdated(display:false)
	}

	static mapping = {
		sort "sortOrder"
	}
}