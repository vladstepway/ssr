package ru.croc.ugd.ssr.service.guardianship;

import ru.croc.ugd.ssr.model.guardianship.GuardianshipRequestDocument;

public interface GuardianshipService {

    void startHandleGuardianshipRequestTask(final GuardianshipRequestDocument guardianshipRequestDocument);

    void forceFinishProcess(final GuardianshipRequestDocument guardianshipRequestDocument);

    void checkGuardianshipRequestAndStartHandle(final String affairId);
}
