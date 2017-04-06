package com.flatmates.ixion.model;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by gurpreet on 06/04/17.
 */

@SimpleSQLTable(table = "Blockchain", provider = "DataProvider")
public class BlockchainData {

    @SimpleSQLColumn(value = "object_id", primary = true)
    private String objectID;
    @SimpleSQLColumn("contract_id")
    private String contractID;
    @SimpleSQLColumn("guid")
    private String GUID;
    @SimpleSQLColumn("title")
    private String title;
    @SimpleSQLColumn("description")
    private String description;
    @SimpleSQLColumn("image_hash")
    private String imageHash;
    @SimpleSQLColumn("price")
    private String price;
    @SimpleSQLColumn("vendor_name")
    private String vendorName;
    @SimpleSQLColumn("vendor_location")
    private String vendorLocation;
    @SimpleSQLColumn("vendor_header_hash")
    private String vendorHeaderHash;
    @SimpleSQLColumn("currency")
    private String currency;


    public BlockchainData() {
    }

    public BlockchainData(String objectID, String contractID, String GUID, String title,
                          String description, String imageHash, String price, String vendorName,
                          String vendorLocation, String vendorHeaderHash, String currency) {
        this.objectID = objectID;
        this.contractID = contractID;
        this.GUID = GUID;
        this.title = title;
        this.description = description;
        this.imageHash = imageHash;
        this.price = price;
        this.vendorName = vendorName;
        this.vendorLocation = vendorLocation;
        this.vendorHeaderHash = vendorHeaderHash;
        this.currency = currency;
    }


    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorLocation() {
        return vendorLocation;
    }

    public void setVendorLocation(String vendorLocation) {
        this.vendorLocation = vendorLocation;
    }

    public String getVendorHeaderHash() {
        return vendorHeaderHash;
    }

    public void setVendorHeaderHash(String vendorHeaderHash) {
        this.vendorHeaderHash = vendorHeaderHash;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
