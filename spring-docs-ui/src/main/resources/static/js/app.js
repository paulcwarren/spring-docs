angular.module('SpringDocs', ['ngResource', 'ngRoute', 'ui.directives', 'ui.bootstrap', 'ngFileUpload', 'spring-data-rest', 'angular-mini-preview']).
    config(function ($locationProvider, $routeProvider) {

        $routeProvider.when('/errors', {
            controller: 'ErrorsController',
            templateUrl: 'templates/errors.html'
        });
        $routeProvider.otherwise({
            controller: 'DocumentsController',
            templateUrl: 'templates/documents.html'
        });
	})
    .controller('AppController', ['$scope', '$rootScope', 'Info', 'Documents', function($scope, $rootScope, Info, Documents) {
    	
    	Info.get().$promise.then(function(info) {
            if (!info.url.endsWith("/")) {
                info.url = info.url + "/";
            }
            $rootScope.Info = info;
    	});

    	var app = this;
    	app.keywords = "";
    	app.searchIcon = "search";

	    app.doSearch = function() {
	    	if (app.searchIcon == "search") {
		    	if (app.keywords) {
		    		app.searchIcon = "stop";

			    	Documents.searchContent(app.keywords)
			    		.then(function(results) {
			    			$rootScope.docs = results;
			    			app.searchIcon = "remove-circle";
			    		})
			    		.catch(function(err) {
			    			console.log(JSON.stringify(err));
			    			app.searchIcon = "remove-circle";
			    		});
		    	}
	    	} else if (app.searchIcon == "remove-circle") {
	    		Documents.getAllDocuments()
	    			.then(function(results) {
    					$rootScope.docs = results;
	    			})
			    	.catch(function(err) {
		    			console.log(JSON.stringify(err));
			    	});
		    	app.keywords = "";
	    		app.searchIcon = "search";
	    	}
	    };
	}]);
