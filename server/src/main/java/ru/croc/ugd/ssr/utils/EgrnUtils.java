package ru.croc.ugd.ssr.utils;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import ru.croc.ugd.ssr.egrn.AnotherRestricted;
import ru.croc.ugd.ssr.egrn.AnotherTypeRestricted;
import ru.croc.ugd.ssr.egrn.AparthouseOwners;
import ru.croc.ugd.ssr.egrn.BondsHoldersOut;
import ru.croc.ugd.ssr.egrn.CertificatesHoldersOut;
import ru.croc.ugd.ssr.egrn.Dict;
import ru.croc.ugd.ssr.egrn.EntityOut;
import ru.croc.ugd.ssr.egrn.EntityUL;
import ru.croc.ugd.ssr.egrn.EquityParticipantsInfo;
import ru.croc.ugd.ssr.egrn.ExtractAboutPropertyRoom;
import ru.croc.ugd.ssr.egrn.ForeignPublic;
import ru.croc.ugd.ssr.egrn.GovementEntity;
import ru.croc.ugd.ssr.egrn.IndividualOut;
import ru.croc.ugd.ssr.egrn.InvestmentUnitOwnerOut;
import ru.croc.ugd.ssr.egrn.LegalEntityOut;
import ru.croc.ugd.ssr.egrn.LegalEntityUL;
import ru.croc.ugd.ssr.egrn.Municipality;
import ru.croc.ugd.ssr.egrn.NotEquityParticipantsInfo;
import ru.croc.ugd.ssr.egrn.NotResidentOut;
import ru.croc.ugd.ssr.egrn.OtherSubject;
import ru.croc.ugd.ssr.egrn.Partnership;
import ru.croc.ugd.ssr.egrn.PartnershipParticipant;
import ru.croc.ugd.ssr.egrn.PublicFormationType;
import ru.croc.ugd.ssr.egrn.PublicFormations;
import ru.croc.ugd.ssr.egrn.PublicServitude;
import ru.croc.ugd.ssr.egrn.ResidentOut;
import ru.croc.ugd.ssr.egrn.RestrictParties;
import ru.croc.ugd.ssr.egrn.RestrictRecordType;
import ru.croc.ugd.ssr.egrn.RestrictRecordsBaseParams;
import ru.croc.ugd.ssr.egrn.RestrictedRightsPartiesOut;
import ru.croc.ugd.ssr.egrn.RestrictedRightsPartyOut;
import ru.croc.ugd.ssr.egrn.RightHolderOut;
import ru.croc.ugd.ssr.egrn.RightHoldersOut;
import ru.croc.ugd.ssr.egrn.RightRecordsAboutProperty;
import ru.croc.ugd.ssr.egrn.Russia;
import ru.croc.ugd.ssr.egrn.SubjectOfRF;
import ru.croc.ugd.ssr.egrn.SubjectRestricted;
import ru.croc.ugd.ssr.egrn.Undefined;
import ru.croc.ugd.ssr.egrn.UnionState;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * EgrnUtils.
 */
public class EgrnUtils {

    public static String getRightHolderAsString(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return ofNullable(extractAboutPropertyRoom.getRightRecords())
            .map(RightRecordsAboutProperty::getRightRecord)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(RightRecordsAboutProperty.RightRecord::getRightHolders)
            .filter(Objects::nonNull)
            .map(RightHoldersOut::getRightHolder)
            .flatMap(Collection::stream)
            .map(EgrnUtils::toString)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Нет");
    }

    public static String getRestrictAsString(final ExtractAboutPropertyRoom extractAboutPropertyRoom) {
        return ofNullable(extractAboutPropertyRoom.getRestrictRecords())
            .map(RestrictRecordsBaseParams::getRestrictRecord)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(RestrictRecordType::getRestrictParties)
            .filter(Objects::nonNull)
            .map(RestrictParties::getRestrictedRightsParties)
            .filter(Objects::nonNull)
            .map(RestrictedRightsPartiesOut::getRestrictedRightsParty)
            .flatMap(Collection::stream)
            .map(RestrictedRightsPartyOut::getSubject)
            .filter(Objects::nonNull)
            .map(EgrnUtils::toString)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Нет");
    }

    private static String toString(final LegalEntityOut value) {
        return ofNullable(value)
            .map(LegalEntityOut::getEntity)
            .map(EgrnUtils::toString)
            .orElse(null);
    }

    private static String toString(final IndividualOut value) {
        return ofNullable(value)
            .filter(ind -> nonNull(ind.getName()) && nonNull(ind.getSurname()))
            .map(ind -> ind.getName() + " " + ind.getSurname())
            .orElse(null);
    }

    private static String toString(final ResidentOut value) {
        return ofNullable(value)
            .map(ResidentOut::getName)
            .orElse(null);
    }

    private static String toString(final GovementEntity value) {
        return ofNullable(value)
            .map(GovementEntity::getFullName)
            .orElse(null);
    }

    private static String toString(final NotResidentOut value) {
        return ofNullable(value)
            .map(NotResidentOut::getName)
            .orElse(null);
    }

    private static String toString(final PublicFormations value) {
        return ofNullable(value)
            .map(PublicFormations::getPublicFormationType)
            .map(EgrnUtils::toString)
            .orElse(null);
    }

    private static String toString(final SubjectOfRF value) {
        return ofNullable(value)
            .map(SubjectOfRF::getName)
            .map(Dict::getValue)
            .orElse(null);
    }

    private static String toString(final Municipality value) {
        return ofNullable(value)
            .map(Municipality::getName)
            .orElse(null);
    }

    private static String toString(final Russia value) {
        return ofNullable(value)
            .map(Russia::getName)
            .map(Dict::getValue)
            .orElse(null);
    }

    private static String toString(final ForeignPublic value) {
        return ofNullable(value)
            .map(ForeignPublic::getName)
            .map(Dict::getValue)
            .orElse(null);
    }

    private static String toString(final UnionState value) {
        return ofNullable(value)
            .map(UnionState::getName)
            .orElse(null);
    }

    private static String toString(final PublicServitude value) {
        return ofNullable(value)
            .map(PublicServitude::getPublic)
            .orElse(null);
    }

    private static String toString(final Undefined value) {
        return ofNullable(value)
            .map(Undefined::getUndefined)
            .orElse(null);
    }

    private static String toString(final AnotherRestricted value) {
        return ofNullable(value)
            .map(AnotherRestricted::getAnotherType)
            .map(EgrnUtils::toString)
            .orElse(null);

    }

    private static String toString(final AparthouseOwners value) {
        return ofNullable(value)
            .map(AparthouseOwners::getAparthouseOwnersName)
            .orElse(null);
    }

    private static String toString(final BondsHoldersOut value) {
        return ofNullable(value)
            .map(BondsHoldersOut::getBondsNumber)
            .orElse(null);
    }

    private static String toString(final CertificatesHoldersOut value) {
        return ofNullable(value)
            .map(CertificatesHoldersOut::getCertificateName)
            .orElse(null);
    }

    private static String toString(final EquityParticipantsInfo value) {
        return ofNullable(value)
            .map(EquityParticipantsInfo::getEquityParticipants)
            .orElse(null);
    }

    private static String toString(final NotEquityParticipantsInfo value) {
        return ofNullable(value)
            .map(NotEquityParticipantsInfo::getNotEquityParticipants)
            .orElse(null);
    }

    private static String toString(final InvestmentUnitOwnerOut value) {
        return ofNullable(value)
            .map(InvestmentUnitOwnerOut::getInvestmentUnitName)
            .orElse(null);
    }

    private static String toString(final Partnership value) {
        return ofNullable(value)
            .map(Partnership::getPartnershipParticipants)
            .map(Partnership.PartnershipParticipants::getPartnershipParticipant)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .map(PartnershipParticipant::getLegalEntity)
            .filter(Objects::nonNull)
            .map(LegalEntityUL::getEntity)
            .filter(Objects::nonNull)
            .map(EgrnUtils::toString)
            .findFirst()
            .orElse(null);
    }

    private static String toString(final OtherSubject value) {
        return ofNullable(value)
            .map(OtherSubject::getName)
            .orElse(null);
    }

    private static String toString(final RightHolderOut value) {
        return coalesce(
            () -> toString(value.getLegalEntity()),
            () -> toString(value.getIndividual()),
            () -> toString(value.getPublicFormation())
        );
    }

    private static String toString(final SubjectRestricted value) {
        return coalesce(
            () -> toString(value.getLegalEntity()),
            () -> toString(value.getIndividual()),
            () -> toString(value.getPublicFormation()),
            () -> toString(value.getPublicServitude()),
            () -> toString(value.getAnother()),
            () -> toString(value.getUndefined())
        );
    }

    private static String toString(final EntityOut value) {
        return coalesce(
            () -> toString(value.getResident()),
            () -> toString(value.getGovementEntity()),
            () -> toString(value.getNotResident())
        );
    }

    private static String toString(final PublicFormationType value) {
        return coalesce(
            () -> toString(value.getSubjectOfRf()),
            () -> toString(value.getMunicipality()),
            () -> toString(value.getRussia()),
            () -> toString(value.getForeignPublic()),
            () -> toString(value.getUnionState())
        );
    }

    private static String toString(final AnotherTypeRestricted value) {
        return coalesce(
            () -> toString(value.getAparthouseOwners()),
            () -> toString(value.getBondsHolders()),
            () -> toString(value.getCertificatesHolders()),
            () -> toString(value.getEquityParticipantsInfo()),
            () -> toString(value.getNotEquityParticipantsInfo()),
            () -> toString(value.getInvestmentUnitOwner()),
            () -> toString(value.getPartnership()),
            () -> toString(value.getOther())
        );
    }

    private static String toString(final EntityUL value) {
        return coalesce(
            () -> toString(value.getResident()),
            () -> toString(value.getNotResident())
        );
    }

    @SafeVarargs
    private static <T> T coalesce(Supplier<T>... items) {
        for (Supplier<T> i : items) {
            final T result = i.get();
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
