//angular.module('SpringMusic', ['ngResource']).
//    factory('Info', function ($resource) {
//        return $resource('appinfo');
//    });
//
//angular.module('SpringMusic', []).controller('InfoController', [function($scope, Info) {
//    $scope.info = Info.get();
//	}]);

//function InfoController($scope, Info) {
//    $scope.info = Info.get();
//}

var springMusic = angular.module('SpringMusic');

//angular.module('SpringMusic', ['ngResource'])
springMusic
	.service('Info', ['$resource', function ($resource) {
        return $resource('appinfo');
    }])
    .controller('InfoController', ['$scope', 'Info', function($scope, Info) {
    	$scope.info = Info.get();
	}]);
