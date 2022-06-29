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
            if (!this.$scope.table) {
                this.$scope.table = qlik.table(this);
                console.log('Table returned: ', this.$scope.table)
                this.$scope.reportData = this.$scope.table.rows
            }
            return qlik.Promise.resolve();
        },
        controller: ['$scope', function ($scope) {
            $scope.hc1 = $scope.layout.qHyperCube;
            $scope.todayDate = getTodayDate();
            $scope.getAOCount = getAOCount;
            $scope.isResettled = function (row) {
                return row[4].qText === '0';
            }
            $scope.resettledCount = function () {
                return $scope.hc1.qDataPages[0].qMatrix.filter(row => $scope.isResettled(row)).length;
            }
            // console.log('$scope: ', $scope);

            // const app = qlik.currApp();
            // Promise.all([app.createCube(cubes.mainCube), app.createCube(cubes.cubeDef2)]).then(cubes => {
            //     $scope.hc1 = cubes[0].layout.qHyperCube;
            //     $scope.hc2 = cubes[1].layout.qHyperCube;
            //     $scope.todayDate = getTodayDate();
            //     $scope.getAOCount = getAOCount;
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

function getAOCount(hc, index) {
    const currentAO = hc.qDataPages[0].qMatrix[index][0].qText;
    const prevAO = index <= 0 ? '' : hc.qDataPages[0].qMatrix[index - 1][0].qText;
    if (!currentAO.localeCompare(prevAO)) {
        return 0;
    }
    let cnt = 1;
    while (true) {
        const nextAO = index + cnt >= hc.qDataPages[0].qArea.qHeight ? '' : hc.qDataPages[0].qMatrix[index + cnt][0].qText;
        if (currentAO.localeCompare(nextAO)) {
            return cnt;
        }
        cnt = cnt + 1;
    }
}
