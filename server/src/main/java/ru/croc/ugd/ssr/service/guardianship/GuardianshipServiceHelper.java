package ru.croc.ugd.ssr.service.guardianship;

public class GuardianshipServiceHelper {
    public static String mapDeclineReasonTypeToString(int declineReason) {
        switch (declineReason) {
            case 1:
                return "Неполный пакет документов";
            case 2:
                return "Не соответствует интересам";
            case 3:
                return "Отказ заявителя";
            case 4:
                return "Иное";
            default:
                return null;
        }
    }
}
