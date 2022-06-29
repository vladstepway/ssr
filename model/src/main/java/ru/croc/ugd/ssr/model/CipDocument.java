package ru.croc.ugd.ssr.model;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.reinform.cdp.document.model.DocumentAbstract;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

@Getter
@Setter
public class CipDocument extends DocumentAbstract<Cip> {

    @Nonnull
    @JsonProperty("Cip")  //должно соответствовать корневому элементу XSD
    private Cip document;

    @Override
    public String getId() {
        return document.getDocumentID();
    }

    @Override
    public void assignId(String id) {
        document.setDocumentID(id);
    }

    @Override
    public String getFolderId() {
        return document.getFolderGUID();
    }

    @Override
    public void assignFolderId(String id) {
        document.setFolderGUID(id);
    }

    @JsonIgnore
    public CipType.Employees getEmployees() {
        final CipType cip = document.getCipData();

        final CipType.Employees employees = ofNullable(cip.getEmployees())
            .orElseGet(CipType.Employees::new);
        cip.setEmployees(employees);

        return employees;
    }

    @JsonIgnore
    public List<String> getEmployeeLogins() {
        return getEmployees()
            .getEmployee()
            .stream()
            .map(CipType.Employees.Employee::getLogin)
            .collect(Collectors.toList());
    }

    public void updateEmployees(final List<String> newEmployeeLogins) {
        final List<CipType.Employees.Employee> newEmployeeList = toEmployeeList(newEmployeeLogins);

        final CipType.Employees employees = getEmployees();
        final List<CipType.Employees.Employee> employeeList = employees.getEmployee();

        employeeList.clear();
        employeeList.addAll(newEmployeeList);
    }

    public void addEmployees(final List<String> employeeLoginsToAdd) {
        final List<CipType.Employees.Employee> newEmployeeList = toEmployeeList(employeeLoginsToAdd);
        final CipType.Employees employees = getEmployees();

        employees.getEmployee().addAll(newEmployeeList);
    }

    private List<CipType.Employees.Employee> toEmployeeList(final List<String> newEmployeeLogins) {
        return newEmployeeLogins.stream()
            .map(login -> {
                final CipType.Employees.Employee employee = new CipType.Employees.Employee();
                employee.setLogin(login);

                return employee;
            })
            .collect(Collectors.toList());
    }
}
