package com.bassis.boot.event;

import com.bassis.bean.event.ApplicationListener;
import com.bassis.boot.application.TomcatUtil;

import javax.servlet.Filter;
import javax.servlet.Servlet;

public class ApplicationConfigListener implements ApplicationListener<ApplicationConfigEvent> {
    TomcatUtil tomcatUtil = TomcatUtil.getInstance();

    @Override
    public void onApplicationEvent(ApplicationConfigEvent event) {
        switch (event.getType()) {
            case ApplicationConfigEvent.servlet:
                tomcatUtil.addServlet((Servlet) event.getSource(), event.getServletName(), event.getStartUp());
                break;
            case ApplicationConfigEvent.filter:
                tomcatUtil.addFilter((Filter) event.getSource(), event.getFilterName(), event.getServletName(), event.getUrlPattern());
                break;
        }
    }
}
