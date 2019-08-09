var springDocs = angular.module('SpringDocs');
springDocs
	.service('Errors', ['$resource', function ($resource) {
        return $resource('errors', {}, {
            kill: { url: 'errors/kill' },
            throw: { url: 'errors/throw' }
        });
    }])
    .controller('ErrorsController', ['$scope', 'Errors', 'Status', function ($scope, Errors, Status) {
	    $scope.kill = function() {
	        Errors.kill({},
	            function () {
	                Status.error("The application should have been killed, but returned successfully instead.");
	            },
	            function (result) {
	                if (result.status === 502)
	                    Status.error("An error occurred as expected, the application backend was killed: " + result.status);
	                else
	                    Status.error("An unexpected error occurred: " + result.status);
	            }
	        );
	    };
	
	    $scope.throwException = function() {
	        Errors.throw({},
	            function () {
	                Status.error("An exception should have been thrown, but was not.");
	            },
	            function (result) {
	                if (result.status === 500)
	                    Status.error("An error occurred as expected: " + result.status);
	                else
	                    Status.error("An unexpected error occurred: " + result.status);
	            }
	        );
	    };
    }]);
