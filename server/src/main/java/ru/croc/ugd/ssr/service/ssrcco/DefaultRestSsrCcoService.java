package ru.croc.ugd.ssr.service.ssrcco;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.enums.SsrCcoOrganizationType.CONTRACTOR;
import static ru.croc.ugd.ssr.enums.SsrCcoOrganizationType.DEVELOPER;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.OrganizationInformation;
import ru.croc.ugd.ssr.dto.ssrcco.CreateSsrCcoEmployeesDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoEmployeeDto;
import ru.croc.ugd.ssr.dto.ssrcco.SsrCcoEmployeePeriod;
import ru.croc.ugd.ssr.enums.SsrCcoOrganizationType;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.SsrCcoMapper;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.apartmentdefectconfirmation.ApartmentDefectConfirmationService;
import ru.croc.ugd.ssr.service.document.SsrCcoDocumentService;
import ru.croc.ugd.ssr.ssrcco.SsrCcoData;
import ru.croc.ugd.ssr.ssrcco.SsrCcoEmployee;
import ru.croc.ugd.ssr.ssrcco.SsrCcoOrganization;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestSsrCcoService implements RestSsrCcoService {

    private final SsrCcoDocumentService ssrCcoDocumentService;
    private final SsrCcoMapper ssrCcoMapper;
    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final ApartmentDefectConfirmationService apartmentDefectConfirmationService;
    private final ApartmentInspectionService apartmentInspectionService;

    @Override
    public SsrCcoDto fetchById(final String id) {
        final SsrCcoDocument ssrCcoDocument = ssrCcoDocumentService.fetchDocument(id);

        return ssrCcoMapper.toSsrCcoDto(ssrCcoDocument);
    }

    @Override
    public SsrCcoDto fetchByPsDocumentId(final String psDocumentId) {
        return ssrCcoDocumentService.fetchByPsDocumentId(psDocumentId)
            .map(ssrCcoMapper::toSsrCcoDto)
            .orElseThrow(() -> new SsrException("Unable to find SsrCcoDocument by psDocumentId = " + psDocumentId));
    }

    public List<SsrCcoOrganization> fetchSsrCcoOrganizationsByUnom(final String unom) {
        final List<SsrCcoOrganization> developerCcoOrganizations = fetchSsrCcoOrganizations(
            () -> capitalConstructionObjectService.getDeveloperOrganisationsByUnom(unom),
            DEVELOPER
        );

        final List<SsrCcoOrganization> contractorCcoOrganizations = fetchSsrCcoOrganizations(
            () -> capitalConstructionObjectService.getContractOrganisationsByUnom(unom),
            CONTRACTOR
        );

        return Stream.of(developerCcoOrganizations, contractorCcoOrganizations)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    @Override
    public void createEmployees(final CreateSsrCcoEmployeesDto createEmployeesDto) {
        createEmployeesDto.getCcos()
            .forEach(ssrCcoDto -> createEmployees(ssrCcoDto, createEmployeesDto.getEmployees()));
    }

    private void createEmployees(final SsrCcoDto ssrCcoDto, final List<SsrCcoEmployeeDto> employeesToAdd) {
        final SsrCcoDocument ssrCcoDocument = ssrCcoDocumentService.fetchByPsDocumentId(ssrCcoDto.getPsDocumentId())
            .orElseGet(() -> createSsrCcoDocument(ssrCcoDto));
        final SsrCcoData ssrCcoData = ssrCcoDocument.getDocument().getSsrCcoData();
        final List<SsrCcoEmployee> employees = ssrCcoData.getEmployees();

        final Set<String> currentEmployeeLoginsWithTypes = employees.stream()
            .map(employee -> employee.getLogin() + employee.getType())
            .collect(Collectors.toSet());

        employeesToAdd.stream()
            .filter(employeeDto ->
                !currentEmployeeLoginsWithTypes.contains(employeeDto.getLogin() + employeeDto.getType().getTypeCode())
            )
            .forEach(employeeDto -> addEmployee(employeeDto, ssrCcoDocument));

        ssrCcoDocumentService.updateDocument(ssrCcoDocument, "createEmployees");

        ofNullable(ssrCcoData.getUnom())
            .ifPresent(this::actualizeTaskCandidatesBySsrCco);
    }

    private void addEmployee(final SsrCcoEmployeeDto ssrCcoEmployeeDto, final SsrCcoDocument ssrCcoDocument) {
        final SsrCcoEmployee ssrCcoEmployee = ssrCcoMapper.toSsrCcoEmployee(ssrCcoEmployeeDto);
        final SsrCcoData ssrCcoData = ssrCcoDocument.getDocument().getSsrCcoData();
        final List<SsrCcoEmployee> employees = ssrCcoData.getEmployees();

        employees.add(ssrCcoEmployee);

        log.debug(
            "Add employee to ssrCco: ssrCcoId = {}, employeeLogin = {}",
            ssrCcoDocument.getId(),
            ssrCcoEmployeeDto.getLogin()
        );
    }

    private SsrCcoDocument createSsrCcoDocument(final SsrCcoDto ssrCcoDto) {
        final SsrCcoDocument ssrCcoDocument = ssrCcoMapper.toSsrCcoDocument(ssrCcoDto);
        final SsrCcoData ssrCcoData = ssrCcoDocument.getDocument().getSsrCcoData();

        final List<SsrCcoOrganization> ssrCcoOrganizations = fetchSsrCcoOrganizationsByUnom(ssrCcoData.getUnom());
        ssrCcoData.getOrganizations().addAll(ssrCcoOrganizations);

        return ssrCcoDocumentService.createDocument(ssrCcoDocument, true, "createSsrCcoDocument");
    }

    private List<SsrCcoOrganization> fetchSsrCcoOrganizations(
        final Supplier<List<OrganizationInformation>> retrieveOrganizationInformationList,
        final SsrCcoOrganizationType ssrCcoOrganizationType
    ) {
        return retrieveOrganizationInformationList.get()
            .stream()
            .map(organizationInformation ->
                ssrCcoMapper.toSsrCcoOrganization(organizationInformation, ssrCcoOrganizationType.getTypeCode())
            )
            .collect(Collectors.toList());
    }

    @Override
    public void deleteEmployee(final String id, final String employeeId) {
        log.debug("Start deleteEmployee: ssrCcoId = {}, employeeId = {}", id, employeeId);

        final SsrCcoDocument ssrCcoDocument = ssrCcoDocumentService.fetchDocument(id);
        final SsrCcoData ssrCcoData = ssrCcoDocument.getDocument().getSsrCcoData();
        final List<SsrCcoEmployee> employees = ssrCcoData.getEmployees();

        final List<SsrCcoEmployee> employeesAfterRemoval = employees.stream()
            .filter(employee -> !Objects.equals(employee.getId(), employeeId))
            .collect(Collectors.toList());

        employees.clear();
        employees.addAll(employeesAfterRemoval);

        ssrCcoDocumentService.updateDocument(ssrCcoDocument, "deleteEmployee");

        ofNullable(ssrCcoData.getUnom())
            .ifPresent(this::actualizeTaskCandidatesBySsrCco);

        log.debug("Finish deleteEmployee: ssrCcoId = {}, employeeId = {}", id, employeeId);
    }

    @Override
    public void changeEmployeePeriod(
        final String id, final String employeeId, final SsrCcoEmployeePeriod employeePeriod
    ) {
        log.debug(
            "Start changeEmployeePeriod:"
                + " ssrCcoId = {}, employeeId = {}, employeePeriodFrom = {}, employeePeriodTo = {}",
            id, employeeId, employeePeriod.getPeriodFrom(), employeePeriod.getPeriodTo()
        );

        final SsrCcoDocument ssrCcoDocument = ssrCcoDocumentService.fetchDocument(id);
        final SsrCcoData ssrCcoData = ssrCcoDocument.getDocument().getSsrCcoData();
        final List<SsrCcoEmployee> employees = ssrCcoData.getEmployees();

        employees.stream()
            .filter(employee -> Objects.equals(employee.getId(), employeeId))
            .findFirst()
            .ifPresent(employee -> changeEmployeePeriod(employee, employeePeriod));

        ssrCcoDocumentService.updateDocument(ssrCcoDocument, "changeEmployeePeriod");

        ofNullable(ssrCcoData.getUnom())
            .ifPresent(this::actualizeTaskCandidatesBySsrCco);

        log.debug(
            "Finish changeEmployeePeriod:"
                + " ssrCcoId = {}, employeeId = {}, employeePeriodFrom = {}, employeePeriodTo = {}",
            id, employeeId, employeePeriod.getPeriodFrom(), employeePeriod.getPeriodTo()
        );
    }

    private void changeEmployeePeriod(final SsrCcoEmployee employee, final SsrCcoEmployeePeriod employeePeriod) {
        employee.setPeriodFrom(employeePeriod.getPeriodFrom());
        employee.setPeriodTo(employeePeriod.getPeriodTo());
    }

    private void actualizeTaskCandidatesBySsrCco(final String unom) {
        apartmentDefectConfirmationService.actualizeTaskCandidatesBySsrCco(unom);
        apartmentInspectionService.actualizeTaskCandidatesBySsrCco(unom);
    }
}
