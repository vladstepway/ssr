package ru.croc.ugd.ssr.utils;

import static java.util.Optional.ofNullable;

import lombok.experimental.UtilityClass;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract.Files.File;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class ContractUtils {

    public static Optional<File> getContractFileByFileType(final Contract contract, final String fileType) {
        return ofNullable(contract)
            .map(Contract::getFiles)
            .map(Contract.Files::getFile)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(file -> Objects.equals(file.getFileType(), fileType))
            .findFirst();
    }
}
