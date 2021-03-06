package org.pih.warehouse.product

import org.apache.commons.lang.StringUtils
import org.junit.Test;
import org.pih.warehouse.core.Constants
import org.pih.warehouse.core.Location
import org.pih.warehouse.core.LocationType
import org.pih.warehouse.core.Tag;
import org.pih.warehouse.core.User;

import testutils.DbHelper



class ProductServiceIntegrationTests extends GroovyTestCase {
	
	
	def productService
	def product1;
	def product2;
	def product3;
	def product4;
	def product5;
	def product6;
	def group1;
	def group2;

    /**
     *
     */
	protected void setUp(){
		product1 = DbHelper.createProductWithGroups("boo floweree 250mg",["Hoo moodiccina", "Boo floweree"])
		product2 = DbHelper.createProductWithGroups("boo pill",["Boo floweree"])
		product3 = DbHelper.createProductWithGroups("foo",["Hoo moodiccina"])
		product4 = DbHelper.createProductWithGroups("abc tellon",["Hoo moodiccina"])
		product5 = DbHelper.createProductWithGroups("goomoon",["Boo floweree"])
		product6 = DbHelper.createProductWithGroups("buhoo floweree root",[])
		group1 = ProductGroup.findByDescription("Hoo moodiccina")
		group2 = ProductGroup.findByDescription("Boo floweree")

        // Create new root category if it doesn't exist
        def rootCategory = Category.findByName("ROOT")
		def category = DbHelper.createCategoryIfNotExists("ROOT")
        println rootCategory
        println category
        assertEquals rootCategory, category
		category.isRoot = true;
		category.save(flush:true)
        assertTrue !category.hasErrors()

        // Create products with tags
		DbHelper.createProductWithTags("Ibuprofen 200mg tablet", ["nsaid","pain","favorite"])
		DbHelper.createProductWithTags("Acetaminophen 325mg tablet", ["pain","pain reliever"])
		DbHelper.createProductWithTags("Naproxen 220mg tablet", ["pain reliever","pain","nsaid","fever reducer"])

        // Create a tag without products
		DbHelper.createTag("tagwithnoproducts")

        // Create a product with a unique product code
        def product7 = DbHelper.createProductIfNotExists("Test Product")
        product7.productCode = "AB13"
        product7.save(flush: true)
        assertNotNull product7
        assertTrue !product7.hasErrors()

	}

	/**
	 * Adds quotes around each element and a newline after the end of the row.
	 */
	String csvize(row) { 
		return csvize(row, ",")
	}
	
	String csvize(row, delimiter) { 
		return "\"" + row.join("\"" + delimiter + "\"") + "\"\n"
	}

    @Test
	void csvize() {
		def row = ["1", "test", "another", "last one"]
		assertEquals csvize(row), "\"1\",\"test\",\"another\",\"last one\"\n"		
	}

    @Test
	void searchProductAndProductGroup_shouldGetAllProductsUnderMachtedGroups(){
		def result = productService.searchProductAndProductGroup("floweree")
		println result
		
		// Only searches products, not product groups any longer
		assert result.size() == 2
		//[[ff8081813d20ed97013d20ee12e1025c, boo floweree 250mg, null], [ff8081813d20ed97013d20ee13d40263, buhoo floweree root, null]]
		assert result.any{ it[1] == "boo floweree 250mg" && it[2] == null && it[0] == product1.id}
		//assert result.any{ it[1] == "boo floweree 250mg" && it[2] == "Hoo moodiccina" && it[0] == product1.id && it[3] == group1.id}
		//assert result.any{ it[1] == "boo pill" && it[2] == "Boo floweree" && it[0] == product2.id && it[3] == group2.id}
		//assert result.any{ it[1] == "goomoon" &&  it[2] == "Boo floweree" && it[0] == product5.id && it[3] == group2.id}
		assert result.any{ it[1] == "buhoo floweree root" &&  it[2] == null && it[0] == product6.id}
	
	}

	/*
	void test_import_shouldFailWhenFormatIsInvalid() {		
		def csv = """\"ID\"\t\"Name\"\t\"Category\"\t\"Description\"\t\"Product Code\"\t\"Unit of Measure\"\t\"Manufacturer\"\t\"Manufacturer Code\"\t\"Cold Chain\"\t\"UPC\"\t\"NDC\"\t\"Date Created\"\t\"Date Updated\"\n\"1235\"\t\"product 1235\"\t\"category 123\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"\"\t\"false\"\t\"\"\t\"\"\t\"\"\t\"\""""
		def service = new ProductService()
		def message = shouldFail(RuntimeException) {
			service.importProducts(csv)
		}
		assertEquals("Invalid format", message)		
	}
	*/
    @Test
	void importProducts_shouldFailWhenProductNameIsMissing() {
		def row = ["1235","SKU-1","","category 123","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row)  			

		def message = shouldFail(RuntimeException) {
			productService.importProducts(csv)
		}
		assertTrue message.contains("Product name cannot be empty")
	}

    @Test
	void importProducts_shouldNotUpdateProductsWhenSaveToDatabaseIsFalse() {
		def product = DbHelper.createProductIfNotExists("Sudafed");
		assertNotNull product.id
		def row1 = ["${product.id}","","Sudafed 2","OTC Medicines","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]		
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1) 
		productService.importProducts(csv, false)		
		def product2 = Product.get(product.id)
		println ("Get product " + product2.name + " " + product2.id + " " + product2.productCode + " " + product.isAttached())		
		assertEquals "Sudafed", product2.name
		assertEquals "Medicines", product2.category.name
	}

    @Test
	void importProducts_shouldCreateNewProductWithNewCategory() {
		def category = Category.findByName("category 123")
		assertNull category
		def row1 = ["1235","","product 1235","category 123","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]		
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1) 
		
		productService.importProducts(csv, true)	
		def product = Product.findByName("product 1235")
		assertNotNull product
		category = Category.findByName("category 123")
		assertNotNull category
	}

    @Test
    void importProducts_shouldCreateNewProductWithExistingCategory() {
		def category = Category.findByName("Medicines")
		assertNotNull category
		def row1 = ["1235","","product 1235","Medicines","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]		
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1)
		productService.importProducts(csv, true)			
		def product = Product.findByName("product 1235")
		assertNotNull product
		assertEquals category, product.category		
	}

    @Test
	void importProducts_shouldUpdateNameOnExistingProduct() {
		def productBefore = DbHelper.createProductIfNotExists("Sudafed");
		assertNotNull productBefore.id
		def row1 = ["${productBefore.id}","AB12","Sudafed 2.0","Medicines","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]		
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1)
		productService.importProducts(csv, true)
		
		def productAfter = Product.get(productBefore.id)
		println ("Get product " + productAfter.name + " " + productAfter.id + " " + productAfter.productCode)
		assertEquals "Sudafed 2.0", productAfter.name		
	}

    @Test
	void importProducts_shouldUpdateAllFieldsOnExistingProduct() {
		def productBefore = DbHelper.createProductIfNotExists("Sudafed");
		assertNotNull productBefore.id
		def row1 = ["${productBefore.id}","AB12","Sudafed 2.0","Medicines","It's sudafed, dummy.","EA","Acme","Brand X","ACME-249248","Manufacturer Name","Vendor","Vendor Code","Vendor Name","true","UPC-1202323","NDC-122929-39292","",""]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1)
		productService.importProducts(csv, true)
		def productAfter = Product.get(productBefore.id)
		println ("Get product " + productAfter.name + " " + productAfter.id + " " + productAfter.productCode)
		assertEquals productBefore.id, productAfter.id
		assertEquals "AB12", productAfter.productCode
		assertEquals "Sudafed 2.0", productAfter.name	
		assertEquals "Medicines", productAfter.category.name
		assertEquals "It's sudafed, dummy.", productAfter.description
		assertEquals "EA", productAfter.unitOfMeasure
		assertEquals "Acme", productAfter.manufacturer
		assertEquals "Brand X", productAfter.brandName
		assertEquals "ACME-249248", productAfter.manufacturerCode
		assertEquals "Manufacturer Name", productAfter.manufacturerName
		assertEquals "Vendor", productAfter.vendor
		assertEquals "Vendor Code", productAfter.vendorCode
		assertEquals "Vendor Name", productAfter.vendorName
		assertTrue productAfter.coldChain		
		assertEquals "UPC-1202323", productAfter.upc
		assertEquals "NDC-122929-39292", productAfter.ndc
	}

    @Test
	void getDelimiter_shouldDetectCommaDelimiter() {
		def row = ["1235","SKU-1","","category 123","Description","Unit of Measure","Manufacture","Brand","ManufacturerCode","Manufacturer Name","Vendor","Vendor Code","Vendor Name","false","UPC","NDC","Date Created","Date Updated"]
		def row1 = ["","AB12","Sudafed 2","Medicines","Sudafed description","EA","Acme","Brand X","ACME-249248","Vendor Y","Y-1284","Sudafed","true","UPC-1202323","NDC-122929-39292","",""]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1)
		def delimiter = productService.getDelimiter(csv)
		assertEquals ",", delimiter
	}
    @Test
	void getDelimiter_shouldDetectTabDelimiter() {
		def row1 = ["","AB12","Sudafed 2","Medicines","Sudafed descrition","each","Acme","Brand X","ACME-249248","Manufacturer Name","Vendor","Vendor Code","Vendor Name","true","UPC-1202323","NDC-122929-39292","",""]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS, "\t") + csvize(row1, "\t")		
		def delimiter = productService.getDelimiter(csv)
		assertEquals "\t", delimiter
	}

    @Test
	void getDelimiter_shouldDetectSemiColonDelimiter() {
		def row1 = ["","00001","Sudafed 2","Medicines","Sudafed description","each","Acme","Brand X","ACME-249248","Manufacturer Name","Vendor","Vendor Code","Vendor Name","true","UPC-1202323","NDC-122929-39292","",""]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS, ";") + csvize(row1, ";")
		def delimiter = productService.getDelimiter(csv)
		assertEquals ";", delimiter
	}
	/*
	void test_getDelimiter_shouldFailOnInvalidDelimiter() {
		def csv = """"ID"&"Name"&"Category"&"Description"&"Product Code"&"Unit of Measure"&"Manufacturer"&"Manufacturer Code"&"Cold Chain"&"UPC"&"NDC"&"Date Created"&"Date Updated"\n"${product1.id}"&"Sudafed 2"&"Medicines"&"Sudafed description"&"00001"&"each"&"Acme"&"ACME-249248"&"true"&"UPC-1202323"&"NDC-122929-39292"&""&"""""
		def service = new ProductService()
		def message = shouldFail(RuntimeException) {
			service.getDelimiter(csv)
		}
		assertEquals("Invalid format", message)
	}
	*/

		

	/*
	void test_import_shouldFailWhenExistingProductHasChangedOnServer() {		
		// Create a new product 
		def today = new Date()
		def product1 = DbHelper.createProductIfNotExists("Sudafed");
		assertNotNull product1.id
		assertNotNull product1.lastUpdated
		assertEquals "Product should have been updated on server today", product1.lastUpdated.clearTime(), today.clearTime()
				
		// Attempt to import should fail due to the fact that the product's lastUpdated date is after the lastUpdated date in the CSV
		def csv = """"ID","Name","Category","Description","Product Code","Unit of Measure","Manufacturer","Manufacturer Code","Cold Chain","UPC","NDC","Date Created","Date Updated"\n"${product1.id}","Sudafed 2","Medicines","","","","","","false","","","2010-08-25 00:00:00.0","2013-01-01 00:00:00.0""""
		def service = new ProductService()
		def message = shouldFail(RuntimeException) {
			service.importProducts(csv)
		}		
		assertEquals("Product has been modified on server", message)		
	}
	*/
	
	/*
	void test_import_shouldFailWhenExistingProductCategoryHasChanged() {
		// Create a new product
		def today = new Date()
		def product1 = DbHelper.createProductIfNotExists("Sudafed");
		assertNotNull product1.id
		assertNotNull product1.lastUpdated
		assertEquals "Product should have been updated on server today", product1.lastUpdated.clearTime(), today.clearTime()
				
		// Attempt to import should fail due to the fact that the product's lastUpdated date is after the lastUpdated date in the CSV
		def csv = """"ID","Name","Category","Description","Product Code","Unit of Measure","Manufacturer","Manufacturer Code","Cold Chain","UPC","NDC","Date Created","Date Updated"\n"${product1.id}","Sudafed 2","Supplies","","","","","","false","","","","""""
		def service = new ProductService()
		def message = shouldFail(RuntimeException) {
			service.importProducts(csv)
		}		
		assertEquals("Product category cannot be modified", message)

		def product2 = Product.findByName("Sudafed")
		assertEquals product2.category.name, "Medicines"				
	}
	*/

    @Test
	void findOrCreateCategory_shouldReturnExistingCategory() {
		def categoryName = "Medicines"
		def existingCategory = Category.findByName(categoryName)		
		assertNotNull existingCategory
		def category = productService.findOrCreateCategory(categoryName)
		assertEquals existingCategory, category
	}

    @Test
	void findOrCreateCategory_shouldCreateNewCategory() {
		def categoryName = "Nonexistent Category"
		def nonexistentCategory = Category.findByName(categoryName)
		assertNull nonexistentCategory
		def category = productService.findOrCreateCategory(categoryName)
		def existingCategory = Category.findByName(categoryName)
		assertEquals existingCategory, category
	}

    @Test
	void findOrCreateCategory_shouldReturnRootCategoryOnRoot() {
		def categoryName = "ROOT"
		def category = productService.findOrCreateCategory(categoryName)
		
		println category
		assertEquals category.name, "ROOT"
	}

    @Test
	void findOrCreateCategory_shouldReturnRootCategoryOnEmpty() {
		def categoryName = ""
		def category = productService.findOrCreateCategory(categoryName)
		println category
        assertNotNull category
		assertEquals category.name, "ROOT"
	}

    @Test
	void exportProducts_shouldReturnAllProducts() {
		def csv = productService.exportProducts()		
		def lines = csv.split("\n")
		assertNotNull lines
		assertEquals lines[0], csvize(Constants.EXPORT_PRODUCT_COLUMNS).replace("\n","")
		
		//'"ID","Product Code","Name","Category","Description","Unit of Measure","Manufacturer","Manufacturer Code","Cold Chain","UPC","NDC","Date Created","Date Updated"'
	}

    @Test
	void exportProducts_shouldRenderProductsAsCsv() {
		def csv = productService.exportProducts()
		
		println csv
		def lines = csv.split("\n")
		
		// Remove quotes
		def columns = lines[0].replaceAll( "\"", "" ).split(",")
		println columns
		assertEquals "ID", columns[0]
		assertEquals "SKU", columns[1]
		assertEquals "Name", columns[2]
		assertEquals "Category", columns[3]
		assertEquals "Description", columns[4]		
		assertEquals "Unit of Measure", columns[5]
		assertEquals "Manufacturer", columns[6]
		assertEquals "Brand", columns[7]		
		assertEquals "Manufacturer Code", columns[8]
		assertEquals "Manufacturer Name", columns[9]
		assertEquals "Vendor", columns[10]
		assertEquals "Vendor Code", columns[11]
		assertEquals "Vendor Name", columns[12]
		assertEquals "Cold Chain", columns[13]
		assertEquals "UPC", columns[14]
		assertEquals "NDC", columns[15]
		assertEquals "Date Created", columns[16]
		assertEquals "Date Updated", columns[17]
	}

    @Test
	void getExistingProducts() {
		def product1 = DbHelper.createProductIfNotExists("Sudafed");
		def product2 = DbHelper.createProductIfNotExists("Advil");		
		assertNotNull product1.id		
		def row1 = ["${product1.id}","","Sudafed","Medicines","","","","","false","","","",""]
		def row2 = ["${product2.id}","","Advil","Medicines","","","","","","false","","","",""]		
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1) + csvize(row2)
				
		def existingProducts = productService.getExistingProducts(csv)
		assertEquals 2, existingProducts.size()
		assertEquals "Sudafed", existingProducts[0].name
		assertEquals "Advil", existingProducts[1].name		
	}

    @Test
	void getExistingProducts_shouldReturnAdvil() {
		def product = DbHelper.createProductIfNotExists("Advil");		
		assertNotNull product.id
		def row1 = ["","","Sudafed","Medicines","","","","","false","","","",""]
		def row2 = ["${product.id}","","Advil","Medicines","","","","","false","","","",""]
		def csv = csvize(Constants.EXPORT_PRODUCT_COLUMNS) + csvize(row1) + csvize(row2)
		def existingProducts = productService.getExistingProducts(csv)
		assertEquals 1, existingProducts.size()
		assertEquals "Advil", existingProducts[0].name
	}

    @Test
	void getAllTagLabels() {
		def service = new ProductService();
		def tags = service.getAllTagLabels()
		println tags
		assertEquals 6, tags.size()
	}

    @Test
	void getPopularTags() {
		//def service = new ProductService();
		
		def popularTagMap = productService.getPopularTags()
		
		def expectedProduct = Product.findByName("Ibuprofen 200mg tablet")
		def expectedTags = ["favorite", "fever reducer", "nsaid", "pain", "pain reliever"]
		def excludedTags = ["tagwithnoproducts"]
		
		
		println popularTagMap
		assertNotNull popularTagMap
		assertEquals 5, popularTagMap.keySet().size()
		def actualTags = popularTagMap.keySet().collect { it.tag }
		assertEquals expectedTags, actualTags
		assertEquals 1, popularTagMap[Tag.findByTag("fever reducer")]
		assertEquals 2, popularTagMap[Tag.findByTag("nsaid")]
		assertEquals 3, popularTagMap[Tag.findByTag("pain")]
		assertEquals 2, popularTagMap[Tag.findByTag("pain reliever")]
		//assertEquals expectedProduct, popularTagMap["favorite"][0]
	}

    @Test
    void getAllCategories() {
        def categories = Category.list()
        assertNotNull categories
        categories.each {
            println it.id + ":" + it?.name + ":" + it?.parentCategory?.name
        }
    }


    @Test
    void getRootCategory() {
        def rootCategory = productService.getRootCategory()
        assertNotNull rootCategory
        assertEquals rootCategory.name, "ROOT"
        assertTrue rootCategory.isRoot
        assertTrue rootCategory.isRootCategory()
    }

    @Test
	void getTopLevelCategories() {
		def topLevelCategories = productService.getTopLevelCategories()
		println topLevelCategories
		assertNotNull topLevelCategories		
		assertEquals 5, topLevelCategories.size()		
	}

    @Test
	void addTagsToProduct_shouldAddTagToProduct() {
		def product = Product.findByName("Ibuprofen 200mg tablet")
		assertNotNull product		
		productService.addTagsToProduct(product, ["awesome", "super"])
		println product.tags*.tag		
		assertEquals product.tagsToString(), "awesome,favorite,nsaid,pain,super"		
	}

    @Test
    void addTagsToProduct_shouldNotAddSameTagMoreThanOnce() {
        def product = Product.findByName("Ibuprofen 200mg tablet")
        assertNotNull product
        productService.addTagsToProduct(product, ["awesome", "super", "awesome"])
        println product.tags*.tag
        assertEquals product.tagsToString(), "awesome,favorite,nsaid,pain,super"
    }

    @Test
    void addTagsToProducts_shouldAddEachTagToAllProducts() {
        def products = []
        def tags = ["newtag1", "newtag2"]
        def ibuprofen = Product.findByName("Ibuprofen 200mg tablet")
        def acetaminophen = Product.findByName("Acetaminophen 325mg tablet")
        products << ibuprofen
        products << acetaminophen

        productService.addTagsToProducts(products, tags)

        assertNotNull Tag.findByTag("newtag1")
        assertNotNull Tag.findByTag("newtag2")

        assert ibuprofen.tagsToString().contains("newtag1")
        assert ibuprofen.tagsToString().contains("newtag2")
        assert acetaminophen.tagsToString().contains("newtag1")
        assert acetaminophen.tagsToString().contains("newtag2")

    }

    @Test
    void addTagsToProducts_shouldNotAddTagMoreThanOnce() {
        def products = []
        def tags = ["sametag", "sametag"]
        def ibuprofen = Product.findByName("Ibuprofen 200mg tablet")
        def acetaminophen = Product.findByName("Acetaminophen 325mg tablet")
        products << ibuprofen
        products << acetaminophen

        productService.addTagsToProducts(products, tags)

        // Check to make sure the tag was created
        def tag = Tag.findByTag("sametag")
        assertNotNull tag

        // Should have only created the tag once
        def sameTags = Tag.findByTag("sametag")
        assert 1, sameTags.size()

        // Check that both products have one instance of the tag
        assert ibuprofen.tagsToString().contains("sametag")
        assert acetaminophen.tagsToString().contains("sametag")

        // Check that the tag has only been added to the products once
        def occurrences = ibuprofen.tags.findIndexValues { it == tag }
        assertEquals 1, occurrences.size()

        occurrences = acetaminophen.tags.findIndexValues { it == tag }
        assertEquals 1, occurrences.size()
    }

    @Test
	void deleteTag_shouldDeleteTagFromDatabase() {
		def product = Product.findByName("Ibuprofen 200mg tablet")
		assertNotNull product	
		assertEquals product.tagsToString(), "favorite,nsaid,pain"	
		Tag tag = Tag.findByTag("favorite")	
		assertNotNull tag		
		productService.deleteTag(product, tag)		
		assertEquals "nsaid,pain", product.tagsToString()		
		Tag tag2 = Tag.findByTag("favorite")
		assertNull tag2
	}

    @Test
    void generateProductIdentifier_shouldGenerateUniqueIdentifiers() {

        for (int i = 0; i<100; i++) {

            println productService.generateProductIdentifier()
        }
    }

	
	@Test
	void saveProduct_failOnValidationError() {
        def product = new Product();
        def returnValue = productService.saveProduct(product)
        assertNull returnValue
        assertEquals 2, product.errors.getErrorCount()
        assertNotNull product.errors.getFieldError("name")
        assertNotNull product.errors.getFieldError("category")
        assertNull product.errors.getFieldError("description")
        //println product.errors['name']
        //println product.errors['category']
        //assertEquals 'Name is blank.', 'blank', product.errors['name']
        //assertEquals 'NickName is blank.', 'blank', user.errors['category']

        //product.errors

    }

    @Test
    void saveProduct_shouldSaveProduct() {
        def product = new Product();
        product.name = "Test product"
        product.category = Category.getRootCategory()
        def returnValue = productService.saveProduct(product)
        println returnValue
        assertNotNull returnValue
        assertEquals product, returnValue
        assertEquals returnValue.category, Category.getRootCategory()
        assertNotNull product.productCode
    }

    @Test
    void saveProduct_shouldSaveProductAndTags() {
        def product = new Product();
        product.name = "Test product"
        product.category = Category.getRootCategory()

        def returnValue = productService.saveProduct(product, "a tag,the next tag,another tag")
        assertNotNull returnValue
        assertNotNull product.id
        assertNotNull product.tags
        assertEquals 3, product.tags.size()


        // The following tests don't work because tags are not sorted in any particular order
        // so they are returned in random order.
        //assertEquals "a tag,another tag,the next tag", product.tagsToString()
        //assertEquals "a tag", product.tags.iterator().next().tag
        //assertEquals "another tag", product.tags.iterator().next().tag
        //assertEquals "the next tag", product.tags.iterator().next().tag

        // Lookup the product using the newly saved product id
        //def returnValue = Product.get(product.id)
        //assertNotNull returnValue

    }

    @Test
    void saveProduct_shouldFailOnInvalidCategory() {
        def product = new Product();
        productService.saveProduct(product)
        println product.errors
        assertNotNull product.errors.getFieldError("category")
    }


    @Test
    void saveProduct_shouldGenerateUniqueProductCode() {
        def product = new Product();
        product.name = "New product"
        product.category = Category.getRootCategory()
        productService.saveProduct(product)
        assertNotNull product
        assertNotNull product.productCode
    }

    @Test
    void saveProduct_shouldFailOnDuplicateProductCode() {
        def product = Product.findByProductCode("AB13")
        assertNotNull product

        product = new Product();
        product.name = "New product"
        product.productCode = "AB13"
        product.category = Category.getRootCategory()
        def returnValue = productService.saveProduct(product)
        println product.errors
        assertNull returnValue
        assertNotNull product.errors.getFieldError("productCode")


    }

    @Test
    void validateProductIdentifier_shouldReturnTrueOnUnique() {
        // Ensuring that product does not exist
        def product = Product.findByProductCode("ZZZZ")
        assertNull product
        assertTrue productService.validateProductIdentifier("ZZZZ")
    }

    @Test
    void validateProductIdentifier_shouldReturnFalseOnDuplicate() {
        // Ensuring that product does exist
        def product = Product.findByProductCode("AB13")
        assertNotNull product
        assertFalse productService.validateProductIdentifier("AB13")
    }

    @Test
    void validateProductIdentifier_shouldReturnFalseOnEmpty() {
        assertFalse productService.validateProductIdentifier("");
    }

    @Test
    void validateProductIdentifier_shouldReturnFalseOnNull() {
        assertFalse productService.validateProductIdentifier(null);
    }


    @Test
    void findOrCreateTag_shouldCreateTagSuccessfully() {
        def tag1 = Tag.findByTag("brand new tag")
        assertNull tag1
        def tag2 = productService.findOrCreateTag("brand new tag")
        assertNotNull tag2

    }

    @Test
    void findOrCreateTag_shouldFindExistingTag() {
        def tag1 = Tag.findByTag("favorite")
        assertNotNull tag1
        assertEquals 1, Tag.findAllByTag("favorite").size()

        def tag2 = productService.findOrCreateTag("favorite")
        assertNotNull tag2
        assertEquals tag1.id, tag2.id
        assertEquals tag1, tag2

        // Make sure there's still only one "favorite" tag
        assertEquals 1, Tag.findAllByTag("favorite").size()
    }

}
