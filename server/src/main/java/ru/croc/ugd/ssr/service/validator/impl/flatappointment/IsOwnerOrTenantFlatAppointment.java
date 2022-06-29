package ru.croc.ugd.ssr.service.validator.impl.flatappointment;

import ru.croc.ugd.ssr.enums.StatusLiving;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsOwnerOrTenant;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

public class IsOwnerOrTenantFlatAppointment<T extends ValidatablePersonData> extends IsOwnerOrTenant<T> {

    public IsOwnerOrTenantFlatAppointment(final String errorMessage) {
        super(T -> errorMessage);
    }

    protected boolean isStatusMatchingOwnersOrTenant(final String statusLiving) {
        return StatusLiving.OWNER.value().toString().equals(statusLiving)
            || StatusLiving.USER.value().toString().equals(statusLiving)
            || StatusLiving.TENANT.value().toString().equals(statusLiving)
            || StatusLiving.RESIDENT.value().toString().equals(statusLiving);
    }
}
