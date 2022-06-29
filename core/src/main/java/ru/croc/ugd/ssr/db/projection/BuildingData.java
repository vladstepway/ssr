package ru.croc.ugd.ssr.db.projection;

/**
 * BuildingData.
 */
public interface BuildingData {

    /**
     * Retrieves unom.
     * @return unom
     */
    String getUnom();

    /**
     * Retrieves address.
     * @return address
     */
    String getAddress();

    /**
     * getSubjectRFP1.
     * @return SubjectRFP1
     */
    String getSubjectRfp1Json();

    /**
     * getElementP7.
     * @return ElementP7
     */
    String getElementP7Json();

    /**
     * getHouseL1TYPE.
     * @return HouseL1TYPE
     */
    String getHouseL1TypeJson();

    /**
     * getHouseL1VALUE.
     * @return HouseL1VALUE
     */
    String getHouseL1Value();

    /**
     * getTownP4Json.
     * @return TownP4Json
     */
    String getTownP4Json();

    /**
     * getSettlementP3Json.
     * @return SettlementP3Json
     */
    String getSettlementP3Json();

    /**
     * getLocalityP6Json.
     * @return LocalityP6Json
     */
    String getLocalityP6Json();

    /**
     * getCorpL2TypeJson.
     * @return CorpL2TypeJson
     */
    String getCorpL2TypeJson();

    /**
     * getCorpL2Value.
     * @return CorpL2Value
     */
    String getCorpL2Value();

    /**
     * getBuildingL3TypeJson.
     * @return BuildingL3TypeJson
     */
    String getBuildingL3TypeJson();

    /**
     * getBuildingL3Value.
     * @return BuildingL3Value
     */
    String getBuildingL3Value();
}
