
package ru.croc.ugd.ssr.solr;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.reinform.cdp.search.service.CheckSolrDocumentAbstract;

@Slf4j
@Component
public class CheckSolrDocument
    extends CheckSolrDocumentAbstract
{

    @Value("${app.subsystem:UGD_SSR}")
    private String subsystemCode = "UGD_SSR";

    @PostConstruct
    public void init() {
        checkDocumentTypes(this.getClass().getPackage(), subsystemCode);
    }

}
