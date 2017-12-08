var springMusic = angular.module('SpringMusic');
springMusic.
    service('Documents', function ($resource, $http, SpringDataRestAdapter, Upload, $timeout) {
    	this.getAllDocuments = function getAllDocuments() {
    	    return this.unwrap($http({
    	        method : 'GET',
    	        url : 'http://localhost:9090/documents'
    	    }));
    	}
    	
    	this.searchContent = function searchContent(keyword) {
    	    return this.unwrap($http({
    	        method : 'GET',
    	        url : 'http://localhost:9090/documents/searchContent/findKeyword?keyword=' + keyword
    	    }));
    	}

    	this.save = function save(doc, onSaveSuccess, onSaveError) {
    		return $resource('http://localhost:9090/documents').save(doc,
    			function(result) {
		        	SpringDataRestAdapter.process(result).then(function(processedResponse) {
		        	    if (processedResponse._links.documentscontent == undefined) {
		        	        onSaveError({status: "500 (Missing 'documentscontent' href)"});
		        	    }

			        	var upload = Upload.upload(
			                {url: processedResponse._links.documentscontent.href,
			                 data: {file: doc.file},
			                });
			        	upload.then(function (response) {
			                $timeout(function () {
			                    onSaveSuccess(response);
			                 });
			            }, onSaveError);  
		        	});
    			}, 
    			onSaveError
    		);	
    	}
    	
    	this.unwrap = function wrap(promise) {
	        return SpringDataRestAdapter.process(promise).then(function(processedResponse) {

				var docs = [];

	    	  	// log the name of all categories contained in the response to the console
	    	  	angular.forEach(processedResponse._embeddedItems, function (doc, key) {
	    	  	  doc.id = doc._links.self.href;
	    		  docs.push(doc);
	    	  	});
	    	  	
	    	  	return docs;
    		});
    	}
    }).
    service('Document', function ($http, $q, Upload, $timeout) {
    	this.update = function update(doc) {
    		var def = $q.defer();

    		$http.put(doc.id, doc)
    			.success(function(response) {
    				if (doc.file) {
			        	var upload = Upload.upload(
			                {url: doc._links.documentscontent.href,
			                 data: {file: doc.file},
			                });
			        	upload.then(function (response) {
			                $timeout(function () {
			                    def.resolve(response);
			                 });
			            }, function(response) {
			                $timeout(function () {
				            	def.reject(response);
				            });
			            });  
    				} else {
		                $timeout(function () {
							def.resolve(response);
						});
					}
    			})
    			.error(function(response) {
	                $timeout(function () {
						def.resolve(response);
					});
				});

    		return def.promise;
    	}

    	this.remove = function remove(doc) {
    		var def = $q.defer();

    		$http({method: 'DELETE', url: doc._links.documentscontent.href, data: '', headers: {'Accept': doc.mimeType, 'Content-Type': doc.mimeType}})
    				.success(function(response) {
    					console.log("1");
			    		$http.delete(doc._links.self.href)
			    			.success(function(response) {
		    					console.log("2");
		    					def.resolve(response);
			    			})
			    			.error(function(response) {
    							def.reject(response);
    					});
					})
    				.error(function(response) {
						def.reject(response);
    				});

    		return def.promise;
    	}
    }).
    service("EditorStatus", function () {
        var editorEnabled = {};

        var enable = function (id, fieldName) {
            editorEnabled = { 'id': id, 'fieldName': fieldName };
        };

        var disable = function () {
            editorEnabled = {};
        };

        var isEnabled = function(id, fieldName) {
            return (editorEnabled['id'] == id && editorEnabled['fieldName'] == fieldName);
        };

        return {
            isEnabled: isEnabled,
            enable: enable,
            disable: disable
        }
    })
    .controller('DocumentsController', ['$scope', '$rootScope', '$uibModal', '$timeout', 'Status', 'Upload', 'SpringDataRestAdapter', 'Documents', 'Document', function($scope, $rootScope, $uibModal, $timeout, Status, Upload, SpringDataRestAdapter, Documents, Document) {
		function list() {
			Documents.getAllDocuments().then(function(results) {
			   $rootScope.docs = results;
			});
	    }
	
	    function clone (obj) {
	        return JSON.parse(JSON.stringify(obj));
	    }
	
	    function newDocument(doc) {
	    }

        function onContentUploadSuccess() {
            Status.success("Document(s) saved");
            list();
        }

        function onSaveError(result) {
            Status.error("Error saving document: " + result.status);
        }
	  	
	    $scope.addDocument = function () {
	        var addModal = $uibModal.open({
	            templateUrl: 'templates/documentForm.html',
	            controller: 'DocumentModalController',
	            resolve: {
	                doc: function () {
	                    return {};
	                },
	                action: function() {
	                    return 'add';
	                }
	            }
	        });
	
	        addModal.result.then(function (doc) {
		        Documents.save(doc,
		            onContentUploadSuccess,
		            onSaveError
		        );
	        });
	    };
	
	    $scope.updateDocument = function (doc) {
	        var updateModal = $uibModal.open({
	            templateUrl: 'templates/documentForm.html',
	            controller: 'DocumentModalController',
	            resolve: {
	                doc: function() {
	                    return clone(doc);
	                },
	                action: function() {
	                    return 'update';
	                }
	            }
	        });
	
	        updateModal.result.then(function (doc) {
		        Document.update(doc)
		            .then(function (result) {
		                Status.success("Document updated");
				    	$timeout(function() {
			                list();
				    	}, 1);
			        })
		            .catch(function (result) {
		                Status.error("Error updating document: " + JSON.stringify(result));
		            });
		        });
	    };
	
	    $scope.deleteDocument = function (doc) {
	        Document.remove(doc)
	            .then(function (result) {
	                Status.success("Document deleted");
			    	$timeout(function() {
		                list();
			    	}, 1);
		        })
	            .catch(function (result) {
	                Status.error("Error deleting document: " + result.status);
	            });
	    };
	
	    $scope.setAlbumsView = function (viewName) {
	        $scope.albumsView = "templates/" + viewName + ".html";
	    };
	    
	    $scope.uploadFiles = function(files) {
	        if (files && files.length) {
	          for (var i = 0; i < files.length; i++) {
	            doc = {
	            	'title': files[i].name,
	            	'file': files[i]
	            };
	            Documents.save(doc, onContentUploadSuccess, onSaveError);
	          }
	        }
	    };

	    $scope.init = function() {
	        list();
	        $scope.setAlbumsView("grid");
	        $scope.sortField = "name";
	        $scope.sortDescending = false;
	        $scope.files = [];
	    };
	}])
	.controller('DocumentModalController', ['$scope', '$uibModalInstance', 'doc', 'action', function ($scope, $uibModalInstance, doc, action) {
	    $scope.docAction = action;
	    $scope.doc = doc;
	
	    $scope.validateFile = function(file) {
	    	return true;
	    };

	    $scope.ok = function () {
	    	$uibModalInstance.close($scope.doc);
	    };
	
	    $scope.cancel = function () {
	    	$uibModalInstance.dismiss('cancel');
	    };
	}])
	.controller('DocumentEditorController', ['$scope', 'Document', 'Status', 'EditorStatus', function($scope, Document, Status, EditorStatus) {
	    $scope.enableEditor = function (doc, fieldName) {
	        $scope.newFieldValue = doc[fieldName];
	        EditorStatus.enable(doc.id, fieldName);
	    };
	
	    $scope.disableEditor = function () {
	        EditorStatus.disable();
	    };
	
	    $scope.isEditorEnabled = function (doc, fieldName) {
	        return EditorStatus.isEnabled(doc.id, fieldName);
	    };
	
	    $scope.save = function (doc, fieldName) {
	        if ($scope.newFieldValue === "") {
	            return false;
	        }
	
	        doc[fieldName] = $scope.newFieldValue;
	
	        Document.update(doc)
	        	.then(function() {
	        		Status.success("Document updated");
	        		list();
	        	})
	        	.catch(function() {
	        		Status.error("Error updating document: " + result.status);
	        	});
	
	        $scope.disableEditor();
	    };
	
	    $scope.disableEditor();
	}]);

angular.module('SpringMusic').
    directive('inPlaceEdit', function () {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,

            scope: {
                ipeFieldName: '@fieldName',
                ipeInputType: '@inputType',
                ipeInputClass: '@inputClass',
                ipePattern: '@pattern',
                ipeModel: '=model'
            },

            template:
                '<div>' +
                    '<span ng-hide="isEditorEnabled(ipeModel, ipeFieldName)" ng-click="enableEditor(ipeModel, ipeFieldName)">' +
                        '<span ng-transclude></span>' +
                    '</span>' +
                    '<span ng-show="isEditorEnabled(ipeModel, ipeFieldName)">' +
                        '<div class="input-append">' +
                            '<input type="{{ipeInputType}}" name="{{ipeFieldName}}" class="{{ipeInputClass}}" ' +
                                'ng-required ng-pattern="{{ipePattern}}" ng-model="newFieldValue" ' +
                                'ui-keyup="{enter: \'save(ipeModel, ipeFieldName)\', esc: \'disableEditor()\'}"/>' +
                            '<div class="btn-group btn-group-xs" role="toolbar">' +
                                '<button ng-click="save(ipeModel, ipeFieldName)" type="button" class="btn"><span class="glyphicon glyphicon-ok"></span></button>' +
                                '<button ng-click="disableEditor()" type="button" class="btn"><span class="glyphicon glyphicon-remove"></span></button>' +
                            '</div>' +
                        '</div>' +
                    '</span>' +
                '</div>',

            controller: 'DocumentEditorController'
        };
    });
