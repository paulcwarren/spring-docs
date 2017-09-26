angular.module('SpringMusic')
	.service("Status", function () {
        var status = null;

        var success = function (message) {
            this.status = { isError: false, message: message };
        };

        var error = function (message) {
            this.status = { isError: true, message: message };
        };

        var clear = function () {
            this.status = null;
        };

        return {
            status: status,
            success: success,
            error: error,
            clear: clear
        }
    })
    .controller('StatusController', ['$scope', '$timeout', 'Status', function ($scope, $timeout, Status) {
	    $scope.$watch(
	        function () {
	            return Status.status;
	        },
	        function (status) {
	            $scope.status = status;
	            $timeout(function() {
	            	Status.clear();
	            }, 3000);
	        },
	        true);
	
	    $scope.clearStatus = function () {
	        Status.clear();
	    };
	}]);

//angular.module('status', []).
//	factory("Status", function () {
//	    var status = null;
//	
//	    var success = function (message) {
//	        this.status = { isError: false, message: message };
//	    };
//	
//	    var error = function (message) {
//	        this.status = { isError: true, message: message };
//	    };
//	
//	    var clear = function () {
//	        this.status = null;
//	    };
//	
//	    return {
//	        status: status,
//	        success: success,
//	        error: error,
//	        clear: clear
//	    }
//	});
//	
//	function StatusController($scope, Status) {
//	$scope.$watch(
//	    function () {
//	        return Status.status;
//	    },
//	    function (status) {
//	        $scope.status = status;
//	    },
//	    true);
//	
//	$scope.clearStatus = function () {
//	    Status.clear();
//	};
//}