<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">

    <xsl:template match="shippingApplication/shippingApplicationData">
        <fo:root font-family="Timesnewroman" xmlns:fo="http://www.w3.org/1999/XSL/Format">

            <fo:layout-master-set>

                <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm"
                                       margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="simpleA4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block text-align="center" font-size="16pt" font-weight="bold" margin-bottom="1cm">
                        Заявление на оказание помощи в организации переезда
                    </fo:block>
                    <fo:block font-size="10pt">
                        <fo:table table-layout="fixed" width="100%" border-collapse="separate" border-spacing="5pt 2pt">
                            <fo:table-column column-width="8cm"/>
                            <fo:table-column column-width="10cm"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="1cm">
                                            Заявление
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="eno"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <xsl:apply-templates select="applicant"/>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="1cm">
                                            Дополнительная информация
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="additionalInfo"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-size="14pt" font-weight="bold" margin-bottom="2mm">
                                            Основные сведения
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="2mm">
                                            Район
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="district"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="2mm">
                                            Старый адрес
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="apartmentFrom/stringAddress"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="2mm">
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold" margin-bottom="2mm">
                                            подъезд
                                            <xsl:if test="apartmentFrom/entrance">
                                                <xsl:value-of select="apartmentFrom/entrance"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentFrom/entrance)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            этаж
                                            <xsl:if test="apartmentFrom/floor">
                                                <xsl:value-of select="apartmentFrom/floor"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentFrom/floor)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            комнат
                                            <xsl:if test="apartmentFrom/rooms">
                                                <xsl:value-of select="apartmentFrom/rooms"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentFrom/rooms)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            код подъезда или номер домофона
                                            <xsl:if test="apartmentFrom/entranceCode">
                                                <xsl:value-of select="apartmentFrom/entranceCode"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentFrom/entranceCode)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="2mm">
                                            Новый адрес
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="apartmentTo/stringAddress"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="1cm">
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold" margin-bottom="2mm">
                                            подъезд
                                            <xsl:if test="apartmentTo/entrance">
                                                <xsl:value-of select="apartmentTo/entrance"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentTo/entrance)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            этаж
                                            <xsl:if test="apartmentTo/floor">
                                                <xsl:value-of select="apartmentTo/floor"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentTo/floor)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            комнат
                                            <xsl:if test="apartmentTo/rooms">
                                                <xsl:value-of select="apartmentTo/rooms"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentTo/rooms)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                            ,
                                            код подъезда или номер домофона
                                            <xsl:if test="apartmentTo/entranceCode">
                                                <xsl:value-of select="apartmentTo/entranceCode"/>
                                            </xsl:if>
                                            <xsl:if test="not(apartmentTo/entranceCode)">
                                                <xsl:text>-</xsl:text>
                                            </xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-size="14pt" font-weight="bold" margin-bottom="2mm">
                                            Дата и время переезда
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="2mm">
                                            Дата и время
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="shippingDateStart"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block margin-bottom="6cm">
                                            Бригада
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="brigade"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>


                        <fo:table width="100%" border-collapse="separate" border-spacing="29pt 60pt">

                            <fo:table-column column-width="5cm"/>
                            <fo:table-column column-width="0.5cm"/>
                            <fo:table-column column-width="8cm"/>
                            <fo:table-column column-width="0.5cm"/>
                            <fo:table-column column-width="4cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell padding-top="0.5cm" padding-left="1cm">
                                        <xsl:attribute name="border-top">solid 0.1mm black</xsl:attribute>
                                        <fo:block margin-bottom="1cm">
                                            (подпись)
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding-top="0.5cm">
                                        <xsl:attribute name="border-top">solid 0.1mm black</xsl:attribute>
                                        <fo:block margin-bottom="1cm">
                                            (Ф.И.О. расшифровка подписи заявителя)
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding-top="0.5cm" padding-left="1cm">
                                        <xsl:attribute name="border-top">solid 0.1mm black</xsl:attribute>
                                        <fo:block margin-bottom="1cm">
                                            (дата)
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="applicant">
        <fo:table-row>
            <fo:table-cell>
                <fo:block font-size="14pt" font-weight="bold" margin-bottom="2mm">
                    Данные заявителя
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell>
                <fo:block margin-bottom="2mm">
                    ФИО
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-weight="bold">
                    <xsl:value-of select="fullName"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell>
                <fo:block margin-bottom="2mm">
                    Электронная почта
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-weight="bold">
                    <xsl:value-of select="email"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell>
                <fo:block margin-bottom="2mm">
                    Телефон
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-weight="bold">
                    <xsl:value-of select="phone"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell>
                <fo:block margin-bottom="2mm">
                    Дополнительный телефон
                </fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-weight="bold">
                    <xsl:value-of select="additionalPhone"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>
</xsl:stylesheet>
