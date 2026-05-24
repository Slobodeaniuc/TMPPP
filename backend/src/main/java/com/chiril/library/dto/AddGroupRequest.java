package com.chiril.library.dto;

import java.util.List;

public record AddGroupRequest(String id, String title, List<String> childItemIds) {}
