/**
* Copyright (c) 2012 Partners In Health.  All rights reserved.
* The use and distribution terms for this software are covered by the
* Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
* which can be found in the file epl-v10.html at the root of this distribution.
* By using this software in any fashion, you are agreeing to be bound by
* the terms of this license.
* You must not remove this notice, or any other, from this software.
**/ 
package util

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.sitemesh.GrailsLayoutDecoratorMapper
import com.opensymphony.module.sitemesh.*
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.metaclass.ControllerDynamicMethods

class CustomLayoutDecoratorMapper extends GrailsLayoutDecoratorMapper {

	public Decorator getDecorator(HttpServletRequest request, Page page) {
		def layoutName = (request.session.layout) ?: request.getParameter("layout")		
		Decorator decorator = getNamedDecorator(request, layoutName)
		if (!decorator) { 
			decorator = super.getDecorator(request, page)
			if (decorator == null) {
				decorator = getNamedDecorator(request, "custom")
			}
		}
		return decorator
	}
}