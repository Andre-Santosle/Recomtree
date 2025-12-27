package com.recomtree.command.commands;

import com.recomtree.service.CatalogService;

public interface Command {
    String execute(CatalogService service, String[] args, String role);
}