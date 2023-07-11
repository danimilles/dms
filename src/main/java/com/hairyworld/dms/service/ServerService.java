package com.hairyworld.dms.service;

import com.hairyworld.dms.model.view.ClientTableData;

import java.util.List;

public interface ServerService {
    List<ClientTableData> getClientTableData();
}
