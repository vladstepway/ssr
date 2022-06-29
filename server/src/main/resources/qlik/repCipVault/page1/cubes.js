define([], function () {
    'use strict';

    const mainCube = {
        qDimensions: [
            {
                qDef: {
                    qFieldDefs: ["АО"],
                    qFieldLabels: ["АО"]
                }
            }
        ],
        qMeasures: [
            {qDef: {qDef: "=Count(distinct unom)", qLabel: "Количество домов"}},
            {qDef: {qDef: "=Sum(flats_cnt)", qLabel: "Всего квартир в доме"}},
            {qDef: {qDef: "=Sum(resettlement_flats_cnt)", qLabel: "Всего квартир подлежит переселению"}},
            {qDef: {qDef: "=Sum(free_flats_cnt)", qLabel: "Свободно квартир"}},
            {qDef: {qDef: "=Sum(agreement_cnt)", qLabel: "Дали согласия"}},
            {qDef: {qDef: "=Sum(contract_cnt)", qLabel: "Заключены договора"}},
            {qDef: {qDef: "=Sum(reject_cnt)", qLabel: "Отказы"}},
            {qDef: {qDef: "=Sum(buy_cnt)", qLabel: "Заявления на докупку"}},
            {qDef: {qDef: "=Sum(compensation_flats_cnt)", qLabel: "Квартир на компенсацию"}},
            {qDef: {qDef: "=Sum(compensation_agree_cnt)", qLabel: "Согласия на компенсацию"}},
            {qDef: {qDef: "=Sum(compensation_reject_cnt)", qLabel: "Отказ от компенсации"}},
            {qDef: {qDef: "=Sum(release_flat_cnt)", qLabel: "Всего переселено"}},
            {qDef: {qDef: "=Sum(shipping_done_cnt)", qLabel: "Оказано содействие в переезде"}},
            {
                qDef: {
                    qDef: "=Sum(flats_cnt) / Sum(flats_cnt)",
                    qLabel: "Всего квартир в доме %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(resettlement_flats_cnt) / Sum(flats_cnt)",
                    qLabel: "Всего квартир подлежит переселению %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(free_flats_cnt) / Sum(flats_cnt)",
                    qLabel: "Свободно квартир %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(agreement_cnt) / Sum(flats_cnt)",
                    qLabel: "Дали согласия %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(contract_cnt) / Sum(flats_cnt)",
                    qLabel: "Заключены договора %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(reject_cnt) / Sum(flats_cnt)",
                    qLabel: "Отказы %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(buy_cnt) / Sum(flats_cnt)",
                    qLabel: "Заявления на докупку %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(compensation_flats_cnt) / Sum(flats_cnt)",
                    qLabel: "Квартир на компенсацию %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(compensation_agree_cnt) / Sum(flats_cnt)",
                    qLabel: "Согласия на компенсацию %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(compensation_reject_cnt) / Sum(flats_cnt)",
                    qLabel: "Отказ от компенсации %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(release_flat_cnt) / Sum(flats_cnt)",
                    qLabel: "Всего переселено %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            },
            {
                qDef: {
                    qDef: "=Sum(shipping_done_cnt) / Sum(flats_cnt)",
                    qLabel: "Оказано содействие в переезде %",
                    qIsAutoFormat: false,
                    isCustomFormatted: true,
                    numFormatFromTemplate: true,
                    qNumFormat: {qDec: ",", qFmt: "# ##0%", qThou: " ", qType: "R", qUseThou: 0, qnDec: 2}
                }
            }
        ],
        qInterColumnSortOrder: [0, 1],
        qInitialDataFetch: [
            {
                qTop: 0,
                qLeft: 0,
                qHeight: 300,
                qWidth: 30
            }
        ]
    };

    const cubeDef2 = {
        qDimensions: [{
            qDef: {
                qFieldDefs: ["АО"]
            }
        }],
        qMeasures: [{
            qDef: {
                qDef: "=Sum(flats_cnt)"
            }
        }],
        qInterColumnSortOrder: [0],
        qInitialDataFetch: [
            {
                qTop: 0,
                qLeft: 0,
                qHeight: 100,
                qWidth: 2
            }
        ]
    };

    return {
        mainCube: mainCube,
        cubeDef2: cubeDef2
    };
});
