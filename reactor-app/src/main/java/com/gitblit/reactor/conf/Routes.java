package com.gitblit.reactor.conf;

import com.gitblit.reactor.dao.ItemDao;
import com.gitblit.reactor.util.MyStrings;
import com.google.inject.Inject;
import fathom.conf.Fathom;
import fathom.realm.Account;
import fathom.rest.RoutesModule;
import fathom.rest.security.AuthConstants;
import fathom.rest.security.CSRFHandler;
import fathom.rest.security.FormAuthenticationGuard;
import fathom.rest.security.FormAuthenticationHandler;
import fathom.rest.security.LogoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes extends RoutesModule {

    private final Logger log = LoggerFactory.getLogger(Routes.class);

    @Inject
    ItemDao dao;

    @Inject
    Fathom ftm;

    @Override
    protected void setup() {

        /*
         * Setup classpath resource handlers
         */
        addWebjarsResourceRoute().named("webjars resource route");
        addPublicResourceRoute().named("public resource route");

        /*
         * Define a resource exclusion regular expression.
         * This ensures that we don't waste time processing
         * the resource routes registered above.
         */
        final String appFilter = getResourceExclusionExpression();

        /*
         * Register a handler that binds some values to use on GET requests
         */
        GET(appFilter, (ctx) -> {
            // sets some response headers
            String name = MyStrings.capitalize(getSettings().getApplicationName());

            ctx.setHeader("app-name", name);
            ctx.setHeader("app-version", getSettings().getApplicationVersion());
            ctx.setHeader("fathom-mode", getSettings().getMode().toString());

            // put some values for the template engine or downstream handlers
            ctx.setLocal("appName", name);
            ctx.setLocal("appVersion", getSettings().getApplicationVersion());
            ctx.setLocal("bootDate", ftm.getBootDate());

            Account account = ctx.getSession(AuthConstants.ACCOUNT_ATTRIBUTE);
            if (account != null) {
                ctx.setLocal(AuthConstants.ACCOUNT_ATTRIBUTE, account);
            }

            ctx.next();

        }).named("response header & bindings filter");

        /*
         * Create a form authentication handler and guard for the "secure" routes
         */
        ALL("/login", FormAuthenticationHandler.class);
        ALL("/logout", LogoutHandler.class);

         /*
         * Register an CSRF token generator and validator.
         */
        ALL("/ui/?.*", CSRFHandler.class).named("CSRF handler");

        /*
         * Create a form authentication guard for secure routes.
         * In the absence of an authenticated session, the browser is redirected
         * to the login url.
         */
        FormAuthenticationGuard guard = new FormAuthenticationGuard("/login");
        GET("/ui/?.*", guard);
        POST("/ui/?.*", guard);

        /*
         * Root page
         */
        GET("/", (ctx) -> {
            ctx.setLocal("items", dao.getAll());
            ctx.render("index");
        }).named("root page");

        /*
         * Discover and add controllers
         */
        addControllers();

        /*
         * Add some ignore path definitions
          */
        getRouter().ignorePaths("/favicon.ico");

    }
}
