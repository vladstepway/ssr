package ru.croc.ugd.ssr.service.flowerrorreport;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum FlowType {
    FLOW_TWO("Сведения о письмах с предложениями\n(Поток №2)"),
    FLOW_SEVEN("Сведения о готовности проекта договора (для подписания)\n"
        + "(Поток 7)"),
    FLOW_EIGHT("Сведения о заселяемой квартире, о решении\n"
        + " суда и денежном возмещении и о зарегистрированном договоре\n"
        + "(Поток 8)"),
    FLOW_TWELVE("Сведения о готовности проекта договора для ознакомления\n"
        + "(Поток 12)"),
    FLOW_THIRTEEN("Сведения о распорядительном документе\n"
        + "(Поток 13)");

    private final String fullName;

    public static Optional<FlowType> ofFullName(final String fullName) {
        return Arrays.stream(FlowType.values())
            .filter(flowType -> Objects.equals(flowType.fullName, fullName))
            .findFirst();
    }
}
