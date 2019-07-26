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
			    		$http.delete(doc._links.self.href)
			    			.success(function(response) {
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

    	this.lock = function lock(doc) {
    		var def = $q.defer();
			var headers = {};
			headers["Accept"] = doc.mimeType;
			headers["Content-Type"] = doc.mimeType;

    		$http({method: 'PUT', url: doc._links.self.href + "/lock", data: '', headers: headers})
    				.success(function(response) {
		    			def.resolve(response);
					})
    				.error(function(response) {
						def.reject(response);
    				});

    		return def.promise;
    	}

    	this.unlock = function unlock(doc) {
    		var def = $q.defer();

    		$http({method: 'DELETE', url: doc._links.self.href + "/lock", data: ''})
    				.success(function(response) {
		    			def.resolve(response);
					})
    				.error(function(response) {
						def.reject(response);
    				});

    		return def.promise;
    	}


		this.version = function version(doc, versionData) {

			var def = $q.defer();
			var url = doc.id + '/version';

			$http.put(url, versionData)
				.success(function(response) {
					def.resolve(response);
				})
				.error(function(response) {
					def.reject(response);
				});

			return def.promise;
		}

		this.uploadVersion = function uploadVersion(doc, versionData) {
			var def = $q.defer();
			var url = doc.id + '/version';

			$http.put(url, versionData)
				.success(function(response) {

					var versionDoc = response;
					versionDoc.id = versionDoc._links.self.href;
					versionDoc.file = doc.file;

					if (versionDoc.versionNumber && doc.file) {
						doc._links = versionDoc._links;
						var upload = Upload.upload(
							{
								url: doc._links.documentscontent.href,
								data: {file: doc.file}
							});
						upload.then(function (response) {
							$timeout(function () {
								def.resolve(response);
							});
						}, function (response) {
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
				.error(function (response) {
					$timeout(function () {
						def.resolve(response);
					});
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
			   var ref = {cats: {}, catDocMap: {}};
			   categories(results, ref);
//                 $rootScope.cats = {"Something": {"Else": {}}, "Other": {}};
			   $rootScope.cats = ref.cats;
			   $rootScope.catsDocs = ref.catDocMap;
//               alert("cats " + JSON.stringify($rootScope.cats));
//               alert("catsDocs " + JSON.stringify($rootScope.catsDocs));
			   $rootScope.catsHead = $rootScope.cats;
			   $scope.breadcrumb = [];
			});
	    }

		function categories (docs, ref) {
			for (var i = 0; i < docs.length; i++) {
				if (docs[i].categories && docs[i].categories.length) {
					for (var j=0; j < docs[i].categories.length; j++) {
						var head = ref.cats;
						var elements = docs[i].categories[j].split("/");
						if (elements && elements.length) {
							for (var e = 0; e < elements.length; e++) {
								var element = elements[e];
								if (element === "") {
									continue;
								}
								if (!(element in head)) {
									head[element] = {};
								}
								head = head[element];
							}
						}
						if (!(element in ref.catDocMap)) {
							ref.catDocMap[element] = [];
						}
						ref.catDocMap[element].push(docs[i]);

					}
				}
			}
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

        // categories

        $scope.navigate = function (breadcrumb) {
            $scope.breadcrumb = breadcrumb;
            $rootScope.catsHead = $rootScope.cats;
            for (var i=0; i < breadcrumb.length; i++) {
                $rootScope.catsHead = $rootScope.catsHead[breadcrumb[i]];
            }
        }

        $scope.docsForCat = function(breadcrumb) {
            return $rootScope.catsDocs[breadcrumb[breadcrumb.length-1]];
        }

        // end categories

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

				if (!doc.versionNumber) {
					doc.versionNumber = "1";
				}

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
	                Status.error("Error deleting document: " + ((result) ?  result.status : ""));
	            });
	    };

	    $scope.lockDocument = function (doc) {
	        Document.lock(doc)
	            .then(function (result) {
	                Status.success("Document locked");
			    	$timeout(function() {
		                list();
			    	}, 1);
		        })
	            .catch(function (result) {
					console.log(result);
	                Status.error("Error locking document: " + ((result) ?  result.status : ""));
	            });
	    };

	    $scope.unlockDocument = function (doc) {
	        Document.unlock(doc)
	            .then(function (result) {
	                Status.success("Document unlocked");
			    	$timeout(function() {
		                list();
			    	}, 1);
		        })
	            .catch(function (result) {
	                Status.error("Error unlocking document: " + ((result) ?  result.status : ""));
	            });
	    };

		$scope.versionDocument = function (doc) {

			var versionData = {};
			versionData['number'] = (+ doc.versionNumber) +1;
			versionData['label'] = "next version " + versionData.number;

			Document.version(doc, versionData)
				.then(function (result) {
					Status.success("Document version created");
					$timeout(function() {
						list();
					}, 1);
				})
				.catch(function (result) {
					Status.error("Error versioning document: " + ((result) ?  result.status : ""));
				});
		};


	    $scope.uploadDocumentVersion = function (doc) {

			var uploadDocumentVersion = $uibModal.open({
				templateUrl: 'templates/versionForm.html',
				controller: 'DocumentVersionController',
				resolve: {
					doc: function() {
						return clone(doc);
					},
					action: function() {
						return 'upload';
					}
				}
			});

			uploadDocumentVersion.result.then(function (doc) {

				var versionData = {};
				versionData['number'] = doc.versionNumber;
				versionData['label'] = doc.versionLabel;

				Document.uploadVersion(doc, versionData)
					.then(function (result) {
						Status.success("Uploaded Document version");
						$timeout(function () {
							list();
						}, 1);
					})
					.catch(function (result) {
						Status.error("Error uploading Document version: " + JSON.stringify(result));
					});
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

				if (!doc.versionNumber) {
					doc.versionNumber = "1";
				}

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
	}])

	.controller('DocumentVersionController', ['$scope', '$uibModalInstance', 'doc', 'action', function ($scope, $uibModalInstance, doc, action) {
		$scope.docAction = action;
		$scope.doc = doc;

		$scope.doc.versionNumber = (+ doc.versionNumber) +1;
		$scope.doc.versionLabel = "";

		$scope.validateFile = function(file) {
			return true;
		};

		$scope.ok = function () {
			$uibModalInstance.close($scope.doc);
		};

		$scope.cancel = function () {
			$uibModalInstance.dismiss('cancel');
		};
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
