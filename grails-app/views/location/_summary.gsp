<div>
	<table>
		<tr>
		
			<td>
				<g:render template="actions"/>		
				&nbsp;
				
				<span class="title">
					${fieldValue(bean: locationInstance, field: "name")}
				</span>
			</td>
			<td style="text-align: right;">
				<div style="font-size: 1.2em">
					<g:if test="${locationInstance?.active}">
						<img src="${resource(dir: 'images/icons/silk', file: 'accept.png') }" class="middle" />
					</g:if>
					<g:else>
	   					<img src="${resource(dir: 'images/icons/silk', file: 'decline.png') }" class="middle" />
					</g:else>
					<label class="middle">${locationInstance?.active ? warehouse.message(code:'warehouse.active.label') : warehouse.message(code:'warehouse.inactive.label')}</label>
				</div>
			
			</td>
		</tr>
	</table>
</div>