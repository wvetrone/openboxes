<%@ page import="java.util.Locale" %>
<?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
	<!-- Include default page title -->
	<title><g:layoutTitle default="OpenBoxes" /></title>
	
	<!-- YUI -->
	<yui:stylesheet dir="reset-fonts-grids" file="reset-fonts-grids.css" />

	<!-- Include Favicon -->
	<link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

	<!-- Include Main CSS -->
	<!-- TODO Apparently there is a slight distinction between these two ... need to figure out what that distinction is -->
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'main.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'menu.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'form.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'footer.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'wizard.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'js/jquery.megaMenu/',file:'jquery.megamenu.css')}" type="text/css" media="all" />
    <link rel="stylesheet" href="${createLinkTo(dir:'js/jquery.nailthumb',file:'jquery.nailthumb.1.1.css')}" type="text/css" media="all" />
	<!--
	<link rel="stylesheet" href="${createLinkTo(dir:'js/jquery.mcdropdown/css',file:'jquery.mcdropdown.css')}" type="text/css" media="all" />
	-->

	<!-- Include javascript files -->
	<g:javascript library="application"/>

	<!-- Include jQuery UI files -->
	<g:javascript library="jquery" plugin="jquery" />
	<jqui:resources />
	<link href="${createLinkTo(dir:'js/jquery.ui/css/smoothness', file:'jquery-ui.css')}" type="text/css" rel="stylesheet" media="screen, projection" />

 	<!-- Include Jquery Validation and Jquery Validation UI plugins -->
 	<jqval:resources />
    <jqvalui:resources />

	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'custom.css')}" type="text/css" media="all" />
	<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'buttons.css')}" type="text/css" media="all" />

	<!-- jquery validation messages -->
	<g:if test="${ session?.user?.locale && session?.user?.locale != 'en'}">
		<script src="${createLinkTo(dir:'js/jquery.validation/', file:'messages_'+ session?.user?.locale + '.js')}"  type="text/javascript" ></script>
	</g:if>


	<!-- Custom styles to be applied to all pages -->
	<style type="text/css" media="screen"></style>

	<!-- Grails Layout : write head element for page-->
	<g:layoutHead />

	<g:render template="/common/customCss"/>

	<ga:trackPageview />
</head>
<body class="yui-skin-sam">

	<g:render template="/common/customVariables"/>
    <div id="doc3">

        <%--

        <g:if test="${flash.message}">
            <div id="notify-container" style="display: hidden;">
                <div id="notify-message" class="message">${flash.message}</div>
            </div>
        </g:if>
         --%>

        <g:if test="${session.useDebugLocale}">
            <div id="debug-header" class="notice" style="margin-bottom: 0px;">
                You are in DEBUG mode.
                <div class="right">
                    <g:link controller="localization" action="list">Show all localizations</g:link> |
                    <g:link controller="localization" action="create">Create new localization</g:link> |
                    <g:link controller="user" action="disableDebugMode">Disable debug mode</g:link>
                </div>
                <div id="localizations">
                     <!--
                        At some point we may want to display all translations for the page in a single div.
                        For the time being, flash.localizations is empty.
                     -->
                     <g:each var="localization" in="${flash.localizations }">
                         <div>
                             ${localization.code } = ${localization.text }
                         </div>
                     </g:each>
                </div>
            </div>
        </g:if>

        <!-- Header "hd" includes includes logo, global navigation -->
        <div id="hd" role="banner">
            <g:render template="/common/header"/>
        </div>
        <g:if test="${session?.user && session?.warehouse}">
            <div id="megamenu">
                <g:include controller="dashboard" action="megamenu"/>
            </div>
        </g:if>
        <g:if test="${session.user}">
            <div>
                <ul class="breadcrumb">
                    <li>
                        <g:link controller="dashboard" action="index">
                            <img src="${createLinkTo(dir:'images/icons/silk',file:'house.png')}" class="home"/>
                            <span><warehouse:message code="default.home.label" default="Home"/></span>
                        </g:link>
                    </li>
                    <g:if test="${session?.user && session?.warehouse}">
                        <li>
                            <a href="javascript:void(0);" class="warehouse-switch">
                                <img src="${createLinkTo(dir:'images/icons/silk',file:'map.png')}" class="map"/>
                                ${session?.warehouse?.name }
                            </a>
                        </li>
                    </g:if>
                    <g:if test="${controllerName }">
                        <li>
                            <g:link controller="${controllerName }" action="index">
                                <warehouse:message code="${controllerName + '.label'}" />
                            </g:link>
                        </li>
                    </g:if>
                    <%--
                    <g:if test="${actionName }">
                        <li>
                            <a href="">
                                ${actionName.capitalize() }
                            </a>
                        </li>
                    </g:if>
                    --%>
                    <g:if test="${g.layoutTitle() && !actionName.equals('index') && !actionName.contains('list') }">
                        <li>
                            <a href="#">${g.layoutTitle()}</a>
                        </li>
                    </g:if>

                </ul>
            </div>
        </g:if>

        <!-- Body includes the divs for the main body content and left navigation menu -->

        <div id="bd" role="main">
            <div id="yui-main">
                <div id="content" class="yui-b">
                    <g:layoutBody />
                </div>
            </div>
        </div>

        <g:if test="${session.useDebugLocale}">
            <div id="localization-dialog" class="dialog" style="display: none;" title="Edit Translation">
                <div id="localization-form">
                    <g:form controller="localization" action="save">
                        <style>
                            #localization-form label { display: block;
                                float: left;
                                width: 100px;}
                            #localization-form label.block { display: block; }
                            #localization-form div { margin: 10px; }
                        </style>
                        <div style="float: left;">
                            <div data-bind="if: id">
                                <label>ID</label>
                                <span data-bind="text: id"></span>
                                <input type="hidden" data-bind="value: id"/>
                            </div>
                            <div class="prop">
                                <label>Locale</label>
                                <input type="hidden" data-bind="value: locale"/>
                                <span data-bind="text: locale"></span>
                            </div>
                            <div class="prop">
                                <label>Code</label>
                                <input type="hidden" data-bind="value: code"/>
                                <span data-bind="text: code"></span>
                            </div>
                            <div class="prop">
                                <label>Original Text</label>
                                <span data-bind="text: text"></span>
                            </div>
                            <div class="prop">
                                <label>Translation</label>
                                <textarea cols="60" rows="6" data-bind="value: translation"></textarea>
                            </div>
                        </div>
                        <!--
                        <div style="float: left">
                            <div>
                                <label>Translation</label>
                                <textarea cols="60" rows="3" data-bind="value: text"></textarea>
                            </div>
                            <div>
                                <label>Translation</label>
                                <div data-bind="text: translation"></div>
                            </div>
                            <div>
                                <select id="src" name="src">
                                    <option value="en">English</option>
                                    <option value="fr">French</option>
                                    <option value="sp">Spanish</option>
                                </select>
                                to
                                <select id="dest" name="dest">
                                    <option value="en">English</option>
                                    <option value="fr" selected>French</option>
                                    <option value="sp">Spanish</option>
                                </select>
                            </div>
                            <div>
                                <button id="help-localization-btn" class="button">Help</button>

                            </div>
                        </div>
                        -->
                        <div class="clear"></div>
                        <div class="buttons">
                            <button id="save-localization-btn" class="button">Save</button>
                            <button id="delete-localization-btn" class="button">Delete</button>
                            <button id="close-localization-dialog-btn" class="button">Cancel</button>


                        </div>
                    </g:form>
                </div>
            </div>
        </g:if>


        <!-- YUI "footer" block that includes footer information -->
        <div id="ft" role="contentinfo">
            <g:render template="/common/footer" />
        </div>
	</div>
	<!-- Include other plugins -->
	<script src="${createLinkTo(dir:'js/jquery.ui/js/', file:'jquery.ui.autocomplete.selectFirst.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.cookies/', file:'jquery.cookies.2.2.0.min.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.cookie/', file:'jquery.cookie.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.tmpl/', file:'jquery.tmpl.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.tmplPlus/', file:'jquery.tmplPlus.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.livequery/', file:'jquery.livequery.min.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.livesearch/', file:'jquery.livesearch.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/jquery.hoverIntent/', file:'jquery.hoverIntent.minified.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/knockout/', file:'knockout-2.2.0.js')}" type="text/javascript"></script>
	<script src="${createLinkTo(dir:'js/', file:'knockout_binding.js')}" type="text/javascript"></script>

    <g:if test="${System.getenv().get('headless') != 'false'}" env="test"> 
    	<!--headless driver throw error when using watermark-->
	</g:if>
    <g:else>
        <script src="${createLinkTo(dir:'js/jquery.watermark/', file:'jquery.watermark.min.js')}" type="text/javascript" ></script>
    </g:else>
	<script src="${createLinkTo(dir:'js/', file:'global.js')}" type="text/javascript" ></script>	
	<script src="${createLinkTo(dir:'js/jquery.megaMenu/', file:'jquery.megamenu.js')}" type="text/javascript" ></script>
	<script src="${createLinkTo(dir:'js/', file:'underscore-min.js')}" type="text/javascript" ></script>
	
	<script type="text/javascript">
        <g:if test="${session.useDebugLocale}">
            // Define the localization
            if(typeof openboxes === "undefined") openboxes = {};
            if(typeof openboxes.localization === "undefined") openboxes.localization = {};
            openboxes.localization.Localization = function(data) {
                console.log(data);
                var self = this;
                if(!data) data = {};
                self.id = ko.observable(data.id);
                self.code = ko.observable(data.code);
                self.locale = ko.observable(data.locale);
                self.text = ko.observable(data.text);
                self.translation = ko.observable(data.translation);
                //self.deleteUrl = ko.observable("${request.contextPath}/json/deleteLocalization?id=" + data.id);
                //self.resolvedText = ko.observablae(data.resolvedText);
                //self.lastUpdated = ko.observable(data.lastUpdated);
                //self.version = ko.observable(data.version);
            };
        </g:if>


		$(function() {

            // Instantiate megamenu
            $(".megamenu").megamenu({'show_method':'simple', 'hide_method': 'simple'});

            <g:if test="${session.useDebugLocale}">
                // Initialize the localization dialog
                $("#localization-dialog").dialog({ autoOpen: false, modal: true, width: '600px' });

                // Instantiate a new localization object to be used
                var data = { id:"", code: "", text: "", translation: "" };
                var viewModel = new openboxes.localization.Localization(data);
                ko.applyBindings(viewModel);

                // Delete localization event handler
                $("#delete-localization-btn").click(function() {
                    event.preventDefault();
                    console.log("delete localization");
                    console.log($(this));
                    console.log($(event));
                    if (viewModel.id() == undefined) {
                        alert("This translation is not currently saved to the database so it cannot be deleted.");
                    }
                    else {
                        $.ajax({
                            url: "${request.contextPath}/json/deleteLocalization",
                            type: "get",
                            contentType: 'text/json',
                            dataType: "json",
                            data: {id: viewModel.id() },
                            success: function(data) {
                                alert("You have successfully deleted this localization.");
                                location.reload();
                            },
                            error: function(data) {
                                alert("An error occurred while deleting this translation.");
                            }
                        });
                    }

                });

                // Close dialog event handler
                $("#close-localization-dialog-btn").click(function() {
                    event.preventDefault();
                    $("#localization-dialog").dialog("close");
                });

                // Help event handler
                $("#help-localization-btn").click(function() {
                    event.preventDefault();
                    $.ajax({
                        url: "${request.contextPath}/json/getTranslation",
                        type: "get",
                        contentType: 'text/json',
                        dataType: "json",
                        data: {text: viewModel.text, src: "en", dest: "fr"},
                        success: function(data) {
                            //alert("success: " + data);
                            console.log(data);
                            viewModel.translation = data;
                            //ko.applyBindings(viewModel);
                        },
                        error: function(data) {
                            //console.log(data);
                            //alert("error");
                            viewModel.translation = "Error. Try again.";
                            //ko.applyBindings(viewModel);
                        }
                    });
                });

                // Save event handler
                $("#save-localization-btn").click(function() {
                    event.preventDefault();
                    var jsonData = ko.toJSON(viewModel);
                    console.log("save localization");
                    console.log(jsonData);

                    $.ajax({
                        url: "${request.contextPath}/json/saveLocalization",
                        type: "post",
                        contentType: 'text/json',
                        dataType: "json",
                        data: jsonData,
                        success: function(data) {
                            //alert("success");
                            $("#localization-dialog").dialog("close");
                            location.reload();
                        },
                        error: function(data) {
                            //alert("fail");
                            $("#localization-dialog").dialog("close");
                            location.reload();
                        }
                    });
                });

                // Open dialog event handler
                $(".open-localization-dialog").click(function() {
					var id = $(this).attr("data-id");
					var code = $(this).attr("data-code");
                    var resolvedMessage = $(this).attr("data-resolved-message");
                    console.log("Get localization");
                    console.log(id);
                    console.log(code);
					var url = "${request.contextPath}/json/getLocalization"
					$.getJSON( url, { id: id, code: code, resolvedMessage: resolvedMessage },
						function (data, status, jqxhr) {
                            console.log("getJSON response: ");
							console.log(data);
                            viewModel.id(data.id);
                            viewModel.code(data.code);
							viewModel.text(data.text);
                            viewModel.locale(data.locale);
                            viewModel.translation(data.translation);
						}
					);

					$("#localization-dialog").dialog('open');
					event.preventDefault();
				});
			</g:if>
			/*
			$.each( $(".localization"), function( key, value ) {
  				console.log( key + ": " + value );
  				//$(this).hide();
  				$(this).appendTo("#localizations");
			});
			*/
			
			$(".warehouse-switch").click(function() {
				//$("#warehouse-menu").toggle();
				$("#warehouseMenu").dialog({ 
					autoOpen: true, 
					modal: true, 
					width: 600,
					height: 400
				});
			});
			
			
			function showActions() {
				//$(this).children(".actions").show();
			}
			
			function hideActions() { 
				$(this).children(".actions").hide();
			}

			/* This is used to remove the action menu when the */
			$(".action-menu").hoverIntent({
				sensitivity: 1, // number = sensitivity threshold (must be 1 or higher)
				interval: 5,   // number = milliseconds for onMouseOver polling interval
				over: showActions,     // function = onMouseOver callback (required)
				timeout: 100,   // number = milliseconds delay before onMouseOut
				out: hideActions       // function = onMouseOut callback (required)
			});  
			
			// Create an action button that toggles the action menu on click
			//button({ text: false, icons: {primary:'ui-icon-gear',secondary:'ui-icon-triangle-1-s'} }).
			/*
			$(".action-btn").click(function(event) {
				$(this).parent().children(".actions").toggle();
				event.preventDefault();
			});
			*/
			/*			
			$(".action-btn").button({ text: false, icons: {primary:'ui-icon-gear',secondary:'ui-icon-triangle-1-s'} });
			*/			
			$(".action-btn").click(function(event) {
				//show the menu directly over the placeholder
				var actions = $(this).parent().children(".actions");

				// Need to toggle before setting the position 
				actions.toggle();

				// Set the position for the actions menu
			    actions.position({
					my: "left top",
					at: "left bottom",				  
					of: $(this).closest(".action-btn"),
					//offset: "0 0"
					collision: "flip"
				});
				
				// To prevent the action button from POST'ing to the server
				event.preventDefault();
			});
		});
	</script>
    <script type="text/javascript">
		var monthNamesShort = [];
		<g:each in="${1..12}" var="monthNum">
			monthNamesShort[${monthNum-1}] = '<warehouse:message code="month.short.${monthNum}.label"/>';
		</g:each>
    </script>    
    
    
</body>
</html>
