/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 
package org.pih.warehouse

import org.pih.warehouse.product.Category;
import org.pih.warehouse.product.Product;

class CategorySelectTagLib {
	
	def productService
	def inventoryService
	
	def selectCategoryMcDropdown = { attrs->
		out << render(template:"../taglib/selectCategoryMcDropDown", model:[attrs:attrs])
	}
	
	
	def selectCategory_v2 = { attrs ->		
		if (!attrs.from) { 
			attrs.from = productService.getRootCategory()
			attrs.depth = 0;
		}
		
		attrs.optionKey = 'id'
		attrs.noSelection = ['null':'-Choose a category-']

		attrs.value = attrs.value
		attrs.optionValue = { it.name }
		//out << g.select(attrs)
		
		out << render(template:"../taglib/selectCategories", model:[attrs:attrs])
		
	}
	
	def selectProducts = { attrs ->
		//def selectedCategory = Category.get(attrs.value as int)
		//def rootCategory = productService.getRootCategory();
		//out << "<select class='" + attrs.cssClass + "' id='" + attrs.id + "' name='" + attrs.name + "'>";
		//out << render(template:"../category/selectOptions", model:[category:rootCategory, selected:selectedCategory, level: 0])
		//out << "</select>"
		//out << "<select class'" + attrs.cssClass + "' id='" + attrs.id + "' name='" + attrs.name + "'>";
		if (attrs?.category) { 		
			attrs.category = Category.get(attrs.category.id)
			attrs.products = inventoryService.getProductsByCategory(attrs.category).sort { it.name } 
			//attrs.from = inventoryService.getProductsByCategory(category)
			//attrs.from = Product.list().sort { it?.name?.toLowerCase() };
			//attrs.optionKey = 'id'
			//attrs.optionValue = 'name'
			//attrs.value = attrs.value
			//attrs.optionValue = { it.name }
			
			//out << g.select(attrs) 
			out << render(template:"../taglib/selectProducts", model:[attrs:attrs])
	
			
		}
		else { 
			out << "select a category"
		}
	}
	
	def categorySelect = { attrs ->		
		def selectedCategory = Category.get(attrs.value)
		def rootCategory = productService.getRootCategory();
		def excludeSpaces = attrs?.excludeSpaces 
		
		out << "<select class='" + attrs.cssClass + "' id='" + attrs.id + "' name='" + attrs.name + "'>";
		out << render(template:"../category/selectOptions", model:[category:rootCategory, selected:selectedCategory, level: 0, excludeSpaces:excludeSpaces])		
		out << "</select>"
	}
	
	
	//  Checkbox list that can be used as a more user-friendly alternative to
	// a multiselect list box
	def checkBoxList = { attrs, body ->
		def from = attrs.from
		def value = attrs.value
		//def cname = attrs.name
		def cname = "product.id"
		def isChecked, ht, wd, style, html

		//  sets the style to override height and/or width if either of them
		//  is specified, else the default from the CSS is taken
		style = "style='"
		if(attrs.height)
			style += "height:${attrs.height};"
		if(attrs.width)
			style += "width:${attrs.width};"

		if (style.length() == "style='".length())
			style = ""
		else
			style += "'" // closing single quote

		html = "<ul class='CheckBoxList' " + style + ">"

		out << html

		from.each { obj ->

			// if we wanted to select the checkbox using a click anywhere on the label (also hover effect)
			// but grails does not recognize index suffix in the name as an array:
			//      cname = "${attrs.name}[${idx++}]"
			//      and put this inside the li: <label for='$cname'>...</label>

			isChecked = (value?.contains(obj."${attrs.optionKey}"))? true: false

			out << "<li>" <<
				checkBox(name:cname, value:obj."${attrs.optionKey}", checked: isChecked) <<
					"${obj}" << "</li>"
		}

		out << "</ul>"

	}
	
}
