Object.prototype.isEmpty = function () {
    if (this === null) return true;
    if (this.length > 0)    return false;
    if (this.length === 0)  return true;
    if (typeof this !== "object") return true;
    for (var key in this) {
        if (Object.prototype.hasOwnProperty.call(this, key)) return false;
    }
    return true;
};

var app = angular.module('app', ["ngPrettyJson", 'ngFileUpload']);
app.directive('fieldDesc', function () {
    return {
        restrict: 'E',
        scope: {data: '=', constraints: '='},
        template: '<table class="u-full-width">' +
        '<thead><tr><th>Name</th><th>Type</th><th>Description</th><th ng-if="constraints">Constraints</th></tr></thead>' +
        '<tbody>' +
        '<tr ng-if="datum.name"  ng-repeat="datum in data">' +
        '<td>{{datum.name}}</td><td>{{datum.type}}</td><td>{{datum.description}}' +
        '<div ng-if="datum.enum && !datum.enumValues.isEmpty()">' +
                '<span class="bold">{{datum.type}}</span>: ' +
                '<span ng-repeat="(key,v) in datum.enumValues">' +
                     '<span ng-click="datum.enumDescShow=!datum.enumDescShow">{{key}}' +
                              '<span ng-if="!v.isEmpty()">' +
                                  '<span ng-show="!datum.enumDescShow">+</span><span ng-show="datum.enumDescShow">-</span></span>' +
                                  '<span ng-show="datum.enumDescShow">{{v}}</span>' +
                              '</span>' +
                          '<span ng-if="!$last">, </span>' +
                '</span>' +
        '</div>' +
        '</td><td><span class="bold" ng-if="datum.required">Required</span> <div ng-repeat="con in datum.constraints"><span class="bold">{{con.type}}</span><span style="margin-left:10px" ng-if="con.value">{{con.value}}</span><span style="margin-left:10px" ng-if="con.message">{{con.message}}</span> </div></td>' +
        '</tr>' +
        '</tbody>' +
        '<tbody ng-if="datum.subClass"  ng-repeat="datum in data">' +
        '<tr><td colspan="4" class="bold"><span style="margin-left:30px;display:block;">{{datum.subType || datum.type}}</span></td>' +
        '<tr><td colspan="4"><field-desc style="margin-left:30px;display:block;" data="datum.subClass" constraints="constraints"></field-desc></td></tr>' +
        '</tbody>' +
        '</table>'
    }
});
app.filter('capitalize', function () {
    return function (input) {
        return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
    }
});
app.directive('sample', function () {
    return {
        restrict: 'E',
        scope: {data: '='},
        template: '<div style="padding-top:20px" ng-if="data.title||data.description"><div class="bold" ng-if="data.title">{{data.title}}</div><div ng-if="data.description" ng-bind-html="data.description | trust"></div></div><pre pretty-json="data.example"></pre>'
    }
});
app.filter('trust', [
    '$sce',
    function ($sce) {
        return function (value, type) {
            return $sce.trustAs(type || 'html', value);
        };
    }
]);
app.controller('apiCtrl', function ($scope, $http, $timeout) {
    $scope.order = {};
    $scope.orderBy = function (order) {
        if ($scope.order.order === order) {
            $scope.order.desc = !$scope.order.desc;
            return;
        }
        $scope.order.order = order;
    };
    $scope.type = 'urlencoded';
    $scope.methods = ['GET', 'HEAD', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'TRACE'];
    $scope.headers = typeof headers === "undefined" ? {} : headers;
    $scope.additionalExplain = additionalExplain;
    $scope.tableHeaders = typeof tableHeaders === "undefined" ? {
        name: "Name",
        url: "URL",
        methodsString: "Method"
    } : tableHeaders;
    if ($scope.tableHeaders) {
        $scope.order.order = Object.keys($scope.tableHeaders)[0];
    }
    $scope.apis = typeof apis === "undefined" ? [] : apis;
    $scope.title = typeof title === "undefined" ? "" : title;
    $scope.apis.forEach(function (api) {
        api.methodsString = api.methods.join(", ");
        if (!api.parameterSample)
            api.parameterSample = {};
    });

    $scope.$watch('method', function (method) {
        if (method === "GET" || method === "DELETE") {
            $scope.type = 'urlencoded';
        }
    });

    $scope.selectApi = function (api) {
        $scope.apis.forEach(function (api) {
            api.select = false;
        });
        api.select = true;
        if (!$scope.selectedApi)
            $scope.selectedApi = {};

        angular.copy(api, $scope.selectedApi);
        $scope.response = null;
        if ($scope.selectedApi.json) {
            $scope.type = 'json';
        }
        if ($scope.selectedApi.methods.length == 0) {
            $scope.method = 'GET';
        } else
            $scope.method = $scope.selectedApi.methods[0];
        $timeout(function () {
            window.scrollTo(0, 0);
        });
    };
    $scope.newHeader = function () {
        $scope.headers[prompt("Key?")] = "";
    };
    $scope.newParameter = function () {
        $scope.selectedApi.parameterSample[prompt("Key?")] = "";
    };
    $scope.deleteHeader = function (key) {
        delete $scope.headers[key];
    };

    $scope.$watch('type', function (type) {
        switch (type) {
            case "json":
                $scope.headers['Content-Type'] = 'application/json';
                break;
            case "urlencoded":
                $scope.headers['Content-Type'] = 'application/x-www-form-urlencoded';
                break;
            default:
                $scope.headers['Content-Type'] = 'multipart/form-data';
        }
    });
    $scope.$watch('method', function (method) {
        if (method === "GET" || method === "DELETE")
            $scope.type = 'urlencoded';
    });
    $scope.sendApi = function (api) {
        var method = $scope.method;
        var options = {
            method: method, url: api.url
        };
        options.headers = $scope.headers;
        if (method === "GET" || method === "DELETE") {
            options.url += "?dc=" + new Date().getTime().toString();
            options.params = api.parameterSample;
        } else if ($scope.type === "json")
            options.data = api.parameterSample;
        else if ($scope.type === "form") {
            var fd = new FormData();
            for (var key in api.parameterSample) {
                fd.append(key, api.parameterSample[key]);
            }
            options.data = fd;
            options.transformRequest = angular.identity;
        }
        else {
            options.transformRequest = function (obj) {
                var str = [];
                for (var p in obj) {
                    if (obj[p] === undefined || obj[p] === "" || typeof obj[p] === "function" || obj[p] === null || obj[p] === "null")
                        continue;
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
                return str.join("&");
            };
            options.data = api.parameterSample;
        }

        $http(options).then(function onSuccess(res) {
            $scope.response = res.data;
        }).catch(function onError(e) {
            $scope.response = e.data;
        });
    };
    $scope.isEmpty = function (map) {
        for (var key in map) {
            return !map.hasOwnProperty(key);
        }
        return true;
    };
});