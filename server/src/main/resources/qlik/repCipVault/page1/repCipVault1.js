define(["qlik", "jquery", "text!./style.css", "text!./template.html", './cubes'], function (qlik, $, cssContent, template, cubes) {
    'use strict';
    $("<style>").html(cssContent).appendTo("head");

    return {
        template: template,
        initialProperties: {
            qHyperCubeDef: cubes.mainCube,
            disableNavMenu: true,
            showTitles: false,
            showDetails: false
        },
        definition: {
            type: "items",
            component: "accordion",
            items: {
                settings: {
                    uses: "settings",
                }
            }
        },
        support: {
            snapshot: true,
            export: true,
            exportData: true
        },
        paint: function () {
            return qlik.Promise.resolve();
        },
        controller: ['$scope', function ($scope) {
            $scope.hc1 = $scope.layout.qHyperCube;
            $scope.todayDate = getTodayDate();
            console.log('$scope: ', $scope);

            // const app = qlik.currApp();
            // Promise.all([app.createCube(cubes.mainCube), app.createCube(cubes.cubeDef2)]).then(cubes => {
            //     $scope.hc1 = cubes[0].layout.qHyperCube;
            //     $scope.hc2 = cubes[1].layout.qHyperCube;
            //     $scope.todayDate = getTodayDate();
            //     console.log('$scope: ', $scope);
            // })
        }]
    };
});

function getTodayDate() {
    const todayDate = new Date();
    const date = todayDate.getDate() >= 10 ? todayDate.getDate() : '0' + todayDate.getDate();
    const month = todayDate.getMonth() + 1 >= 10 ? todayDate.getMonth() + 1 : '0' + (todayDate.getMonth() + 1);
    const year = todayDate.getFullYear();
    return date + '.' + month + '.' + year;
}
