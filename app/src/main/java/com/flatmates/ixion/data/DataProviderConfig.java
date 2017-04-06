package com.flatmates.ixion.data;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by gurpreet on 06/04/17.
 */

@SimpleSQLConfig(
        name = "DataProvider",
        authority = "com.flatmates.ixion",
        database = "obdata.db",
        version = 1)
public class DataProviderConfig implements ProviderConfig {

    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }

}