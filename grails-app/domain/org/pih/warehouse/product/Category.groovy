/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 
package org.pih.warehouse.product;

import java.util.Date;

class Category implements Comparable, Serializable {

	String id
	String name
	String description
	Integer sortOrder = 0;
	Category parentCategory
	Date dateCreated;
	Date lastUpdated;
	Boolean deleted = false
	Boolean isRoot = false

	static hasMany = [ categories : Category ];
	static mappedBy = [ categories : "parentCategory" ];
	static belongsTo = [ parentCategory : Category ];
	static transients = [ "parents", "children", "deleted", "products" ]
	static mapping = {
		id generator: 'uuid'
		sort name:"desc"
		categories sort:"name"
		cache true
	}
	
	static constraints = {
		name(nullable:false, maxSize: 255)
		description(nullable:true, maxSize: 255)
		sortOrder(nullable:true)
		isRoot(nullable:true)
		deleted(nullable:true)
		// parent category can't be the category itself or any of its children
		parentCategory(nullable:true, 
						validator: { value, obj ->  value != obj && !(obj.getChildren().find {it == value}) })
	}  
	
	String toString() { return "$name"; }	

	/**
	 * Sort by name
	 */
	int compareTo(obj) {
		this.getHierarchyAsString(">") <=> obj.getHierarchyAsString(">")
	}
	
	String getHierarchyAsString(String separator) {
		String s = ""
		getParents().each {
			s += it.name + separator
		}
		return s;
	}
	
	Boolean isRootCategory() { 
		return isRoot
	}
	
	static Category getRootCategory() { 
		return Category.findByIsRoot(true)
	}
	
	
	List getParents() { 
		def parents = []
		getAllParents(this, parents)	
		return (parents? parents.reverse() : []);
	}
	

	def List getAllParents(Category node, List parents) {	
		if (node) { 
			parents << node;
			if (node.parentCategory) {
				getAllParents(node.parentCategory, parents)
			}
			else {
				//return parents;
			}
		}
	}
	
	def getChildren() {
		return categories ? categories*.children.flatten() + categories : []
	}
	
	// FIXME We should move this method out of Category.  It's used primarily in the _selectOptions.gsp, 
	// but there's probably a better solution.
	def getProducts() { 
		try {  
			return Product.findAllByCategory(this);
		} catch (Exception e) { 
			//log.info("Error getting products for category " + this.id  + " - " + this.name)
			return null;	
		}
		
	}

    @Override
	int hashCode() {
		if (this.id != null) {
			return this.id.hashCode(); 
		}
		return super.hashCode();
	}

    @Override
	boolean equals(Object o) {
		if (o instanceof Category) {
			Category that = (Category)o;
			return this.id == that.id;
		}
		return false;
	}
}
