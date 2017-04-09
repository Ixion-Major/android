package com.flatmates.ixion.model;

import android.os.Parcel;
import android.os.Parcelable;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by gurpreet on 06/04/17.
 */

@SimpleSQLTable(table = "Blockchain", provider = "DataProvider")
public class BlockchainData implements Parcelable {

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
    @SimpleSQLColumn("categories")
    private String categories;


    public BlockchainData() {
    }

    public BlockchainData(String objectID, String contractID, String GUID, String title,
                          String description, String imageHash, String price, String vendorName,
                          String vendorLocation, String vendorHeaderHash, String currency, String categories) {
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
        this.categories = categories;
    }

    public BlockchainData(Parcel in) {
        String[] data = new String[12];
        in.readStringArray(data);
        this.objectID = data[0];
        this.contractID = data[1];
        this.GUID = data[2];
        this.title = data[3];
        this.description = data[4];
        this.imageHash = data[5];
        this.price = data[6];
        this.vendorName = data[7];
        this.vendorLocation = data[8];
        this.vendorHeaderHash = data[9];
        this.currency = data[10];
        this.categories = data[11];
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

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.objectID,
                this.contractID,
                this.GUID,
                this.title,
                this.description,
                this.imageHash,
                this.price,
                this.vendorName,
                this.vendorLocation,
                this.vendorHeaderHash,
                this.currency,
                this.categories
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BlockchainData createFromParcel(Parcel in) {
            return new BlockchainData(in);
        }

        public BlockchainData[] newArray(int size) {
            return new BlockchainData[size];
        }
    };

}
