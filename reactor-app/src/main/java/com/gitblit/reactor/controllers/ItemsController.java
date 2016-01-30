package com.gitblit.reactor.controllers;

import com.gitblit.reactor.dao.ItemDao;
import com.gitblit.reactor.models.Item;
import com.google.inject.Inject;
import fathom.metrics.Metered;
import fathom.rest.controller.GET;
import fathom.rest.controller.Param;
import fathom.rest.controller.Path;
import fathom.rest.controller.Produces;
import fathom.rest.controller.Return;
import fathom.rest.security.aop.RequirePermission;
import fathom.rest.security.aop.RequireToken;
import fathom.rest.swagger.ApiOperations;
import fathom.rest.swagger.ApiSummary;
import fathom.rest.swagger.Desc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To be discoverable, a controller must be annotated with {@code @Path}
 */
@Path("/items")
@Produces({Produces.JSON, Produces.XML})
@RequireToken
@RequirePermission("license:view")
@ApiOperations(tag = "items", description = "Item operations")
public class ItemsController extends ApiController {

    private final Logger log = LoggerFactory.getLogger(ItemsController.class);

    @Inject
    ItemDao dao;

    /**
     * Responds to a GET request of an integer id like "/api/items/1".
     * <p>
     * Notice that the {@code id} parameter is specified in the
     * {@link @GET} annotation and in the method signature.
     * </p>
     * <p>
     * This technique is relying on use of the Java 8 {@code -parameters}
     * flag passed to {@code javac}.  That flag preserves method parameter
     * names in the compiled class files.
     * </p>
     *
     * @param id
     */
    @GET("/{id: [0-9]+}")
    @ApiSummary("Get an item by id")
    @Return(code = 200, description = "Successful operation", onResult = Item.class)
    @Return(code = 404, description = "Item not found")
    @Metered
    public Item get(@Desc("ID of the item to retrieve") @Param("id") int id) {
        Item item = dao.get(id);
        return item;
    }

}
