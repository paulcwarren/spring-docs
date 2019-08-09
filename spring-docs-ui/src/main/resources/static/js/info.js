var springDocs = angular.module('SpringDocs');

springDocs
	.service('Info', ['$resource', function ($resource) {
        return $resource('appinfo');
    }])
    .controller('InfoController', ['$scope', 'Info', function($scope, Info) {
    	$scope.info = Info.get();
	}]);
