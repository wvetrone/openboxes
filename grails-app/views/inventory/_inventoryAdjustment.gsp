<div class="left">
	<g:form action="saveInventoryTransaction">
		<g:hiddenField name="transactionInstance.id" value="${command?.transactionInstance?.id}"/>
		<g:hiddenField name="transactionInstance.inventory.id" value="${command?.warehouseInstance?.inventory?.id}"/>
		<g:hiddenField name="transactionInstance.transactionType.id" value="${command?.transactionInstance?.transactionType?.id }"/>
		<table>
			<tr class="prop">
				<td class="name">
					<label><warehouse:message code="transaction.date.label"/></label>
				</td>
				<td class="value">
					<span>
						<g:jqueryDatePicker id="transactionDate" name="transactionInstance.transactionDate"
								value="${command?.transactionInstance?.transactionDate}" format="MM/dd/yyyy"/>
					</span>								
				</td>
			</tr>	
			<tr class="prop">
				<td class="name">
					<label><warehouse:message code="transaction.comment.label"/></label>
				</td>
				<td class="value">
					<span class="value">
						<g:textArea cols="80" rows="1" name="transactionInstance.comment" value="${command?.transactionInstance?.comment }"></g:textArea>

					</span>								
				</td>
			</tr>				
			<tr class="prop">
				<td class="name">
					<label><warehouse:message code="transaction.transactionEntries.label"/></label>
				</td>
				<td style="padding: 0px;">
					
					<table id="inventoryAdjustment">
						<thead>
							<tr class="odd">
								<th><warehouse:message code="product.label"/></th>
								<th><warehouse:message code="product.lotNumber.label"/></th>
								<th><warehouse:message code="default.expires.label"/></th>
								<th><warehouse:message code="inventory.onHandQuantity.label"/></th>
								<th><warehouse:message code="default.qty.label"/></th>
								<th><warehouse:message code="default.actions.label"/></th>
							</tr>
						</thead>
						<tbody>
							<g:set var="status" value="${0 }"/>
							<g:each var="product" in="${command?.productInventoryItems?.keySet() }">
								<%-- Hidden field used to keep track of the products that were selected --%>
								<g:hiddenField name="product.id" value="${product?.id }"/>
								
								<%-- Display one row for every inventory item --%>
								<g:each var="inventoryItem" in="${command?.productInventoryItems[product].sort { it.expirationDate } }">
									<g:hiddenField name="transactionEntries[${status }].inventoryItem.id" value="${inventoryItem?.id }"/>
									<tr class="row">
										<td>
											<format:product product="${product }"/>
										</td>
										<td>
											${inventoryItem?.lotNumber }
										</td>
										<td>
											<format:date obj="${inventoryItem?.expirationDate }" format="MMM yyyy"/>
										</td>
										<td>
											${command?.quantityMap[inventoryItem]?:0}
										</td>
										<td>
											<g:if test="${command?.transactionInstance?.transactionEntries }">
												<g:textField name="transactionEntries[${status }].quantity"
													value="${command?.transactionInstance?.transactionEntries[status]?.quantity }" size="1" autocomplete="off" />
											</g:if>
											<g:else>
												<g:textField name="transactionEntries[${status }].quantity"
													value="${command?.quantityMap[inventoryItem]?:0 }" size="1" autocomplete="off" />
											</g:else>
										</td>
										<td>
											<img class="delete middle" src="${createLinkTo(dir:'images/icons/silk',file:'delete.png')}" alt="${warehouse.message(code: 'delete.label') }"/>	
										</td>
									</tr>
									<g:set var="status" value="${status+1 }"/>										
								</g:each>
								
							</g:each>
						</tbody>
						<%-- 
						<tbody>
							<g:each var="transactionEntry" in="${transactionInstance?.transactionEntries}" status="i">
								<g:hiddenField name="transactionEntries[${i }].inventoryItem.id" value="${transactionEntry?.inventoryItem?.id }"/>
								<tr>
									<td>${transactionEntry?.inventoryItem?.product }</td>
									<td>${transactionEntry?.inventoryItem?.lotNumber }</td>
									<td>${transactionEntry?.inventoryItem?.expirationDate }</td>
									<td>${quantityMap[transactionEntry?.inventoryItem] }</td>
									<td><g:textField name="transactionEntries[${i }].quantity"
											value="${transactionEntry.quantity }" size="1" />
									</td>
								</tr>
							</g:each>
							<g:unless test="${!transactionInstance?.transactionEntries }">
								<tr class="empty">
									<td colspan="5" style="text-align: center; display:none;" id="noItemsRow">
										<span class="fade"><warehouse:message code="transaction.noItems.message"/></span>
									</td>
								</tr>
							</g:unless>
						</tbody>
						--%>
					</table>

				</td>
			</tr>		
			<tr class="prop">
				<td colspan="7">
					<div class="center">
						<button type="submit" name="save">								
							<warehouse:message code="default.button.save.label"/>
						</button>
						&nbsp;
						<g:link controller="inventory" action="browse">
							${warehouse.message(code: 'default.button.back.label')}
						</g:link>
						
					</div>
				</td>
			</tr>
		</table>				
	</g:form>
</div>

<script>
	$(document).ready(function() {

		alternateRowColors("#inventoryAdjustment");

		/**
		 * Delete a row from the table.
		 */		
		$("img.delete").livequery('click', function(event) { 
			$(this).closest('tr').fadeTo(400, 0, function () { 
		        $(this).remove();
				renameRowFields($("#inventoryAdjustment"));
				alternateRowColors("#inventoryAdjustment");
		    });
		    return false;
		});			
				
	});
</script>

