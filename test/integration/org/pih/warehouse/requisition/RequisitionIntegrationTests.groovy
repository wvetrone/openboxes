package org.pih.warehouse.requisition


import org.pih.warehouse.core.Location
import org.pih.warehouse.core.Person
import org.pih.warehouse.product.Product

class RequisitionIntegrationTests extends GroovyTestCase {

    void test_RequisitionSaved() {

        def location = Location.list().first()
        def product1 = Product.findByName("Advil 200mg")
        def product2 = Product.findByName("Tylenol 325mg")
        def item1 = new RequisitionItem(product: product1, quantity: 10)
        def item2 = new RequisitionItem(product: product2, quantity: 20)
        def person = Person.list().first()
        def requisition = new Requisition(name:'testRequisition'+ UUID.randomUUID().toString()[0..5], origin: location, destination: location, requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))
        requisition.addToRequisitionItems(item1)
        requisition.addToRequisitionItems(item2)

        requisition.validate()
        requisition.errors.each{ println(it)}

        assert requisition.save(flush:true)


    }

    void test_saveRequisitionItemOnly(){
        def location = Location.list().first()
        def person = Person.list().first()
        def requisition = new Requisition(name:'testRequisition'+ UUID.randomUUID().toString()[0..5], origin: location, destination: location, requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))

        assert requisition.save(flush:true)

        def product = Product.findByName("Advil 200mg")
		def item = new RequisitionItem(product: product, quantity: 10)
		requisition.addToRequisitionItems(item);
		
		assert requisition.save(flush:true)
		assertEquals 1, requisition.requisitionItems.size()

    }

//    Commented out because test not needed at this moment
//    void testGetPendingRequisitions() {
//        def person = Person.list().first()
//        def location = Location.list().first()
//        def location2 = Location.list().last()
//
//        def requisition1 = new Requisition(id:"requisition1", status: RequisitionStatus.CREATED,
//                origin: location, destination: location, name:"oldRequisition1",
//                description: "oldDescription1", requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))
//        def requisition2 = new Requisition(id:"requisition2", status: RequisitionStatus.OPEN,
//                origin: location, destination: location, name:"oldRequisition2",
//                description: "oldDescription2", requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))
//        def requisition3 = new Requisition(id:"requisition3", status: RequisitionStatus.OPEN,
//                origin: location2, destination: location2, name:"oldRequisition3",
//                description: "oldDescription3", requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))
//        def requisition4 = new Requisition(id:"requisition4", status: RequisitionStatus.CANCELED,
//                origin: location, destination: location, name:"oldRequisition4",
//                description: "oldDescription4", requestedBy: person, dateRequested: new Date(), requestedDeliveryDate: new Date().plus(1))
//
//        assert requisition1.save(flush:true)
//        assert requisition2.save(flush:true)
//        assert requisition3.save(flush:true)
//        assert requisition4.save(flush:true)
//
//        def service = new RequisitionService()
//        def collection = service.getPendingRequisitions(location)
//
//        assert collection.size() == 2
//        assert collection.find {it.id == requisition1.id}
//        assert collection.find {it.id == requisition2.id}
//        assert !collection.find {it.id == requisition3.id}
//        assert !collection.find {it.id == requisition4.id}
//
//    }

}
