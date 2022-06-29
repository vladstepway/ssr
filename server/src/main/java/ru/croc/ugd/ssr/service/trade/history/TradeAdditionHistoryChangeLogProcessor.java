package ru.croc.ugd.ssr.service.trade.history;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.core.JaversCoreProperties;
import org.javers.core.changelog.AbstractTextChangeLog;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionGeneralConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Formatter for trade addition history diff.
 */
@Slf4j
public class TradeAdditionHistoryChangeLogProcessor extends AbstractTextChangeLog {
    private static final PrettyValuePrinter PRETTY_VALUE_PRINTER;

    static {
        JaversCoreProperties.PrettyPrintDateFormats prettyPrintDateFormats =
                new JaversCoreProperties.PrettyPrintDateFormats();
        prettyPrintDateFormats.setLocalDate(TradeAdditionGeneralConstants.DATE_VALUE_FORMAT);
        PRETTY_VALUE_PRINTER = new PrettyValuePrinter(prettyPrintDateFormats);
    }
    
    
    public void onCommit(CommitMetadata commitMetadata) {
        this.appendln("commit " + commitMetadata.getId() + ", author: "
                + commitMetadata.getAuthor() + ", " + PRETTY_VALUE_PRINTER
                .format(commitMetadata.getCommitDate()));
    }

    public void onAffectedObject(GlobalId globalId) {
        if (StringUtils.contains(globalId.value(), "#newEstates")) {
            this.appendln("Новостройка: ");
        }
        if (StringUtils.contains(globalId.value(), "#oldEstate")) {
            this.appendln("Расселяемая недвижимость: ");
        }
    }

    public void onValueChange(ValueChange valueChange) {
        this.appendln("Поле изменено '" + getTranslationByProperty(valueChange.getPropertyName())
                + "'. До: '" + getFormattedValue(valueChange.getLeft())
                + "' После: '" + getFormattedValue(valueChange.getRight()) + "'");
    }

    private Object getFormattedValue(Object input) {
        if (input != null && input instanceof Enum) {
            try {
                Method valueMethod
                        = input.getClass().getMethod("value");
                return valueMethod.invoke(input);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return input.toString();
            }
        }
        return PRETTY_VALUE_PRINTER.format(input);
    }

    public void onMapChange(MapChange mapChange) {
        this.appendln("    map changed on '" + getTranslationByProperty(mapChange.getPropertyName())
                + "' property: "
                + mapChange.getEntryChanges());
    }

    public void onArrayChange(ArrayChange arrayChange) {
        this.appendln("Изменился список для элемента: '" + getTranslationByProperty(arrayChange.getPropertyName())
                + "'");
        //                + "' : "
        //                + arrayChange.getChanges());
    }

    public void onListChange(ListChange listChange) {
        this.appendCollectionChange(listChange);
    }

    public void onSetChange(SetChange setChange) {
        this.appendCollectionChange(setChange);
    }

    private void appendCollectionChange(CollectionChange collectionChange) {
        this.appendln("Изменился список для элемента: '"
                + getTranslationByProperty(collectionChange.getPropertyName()) + "'");
        //                + "' : "
        //                + collectionChange.getChanges());
    }

    private String getTranslationByProperty(final String propertyName) {
        if (!TradeAdditionGeneralConstants.TRADE_ADDITION_PROPERTY_TO_PRINT_MAP.containsKey(propertyName)) {
            return propertyName;
        }
        return TradeAdditionGeneralConstants.TRADE_ADDITION_PROPERTY_TO_PRINT_MAP.get(propertyName);
    }
}
