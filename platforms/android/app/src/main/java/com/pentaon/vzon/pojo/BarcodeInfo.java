package com.pentaon.vzon.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pentaon on 20,6월,2018
 */
public class BarcodeInfo {
//    @SerializedName("shipId")
//    @Expose
//    public Long shipId;

//    @SerializedName("orderId")
//    @Expose
//    public Long orderId;

//    @SerializedName("shipItemId")
//    @Expose
//    public Long shipItemId;

//    @SerializedName("orderItemId")
//    @Expose
//    public Long orderItemId;

  /**
   * 기존 shipId, orderId 가 통합
   */
  @SerializedName("paramId")
  @Expose
  public Long paramId;

  /**
   * 기존 shipItemId, orderItemId 가 통합
   */
  @SerializedName("paramItemId")
  @Expose
  public Long paramItemId;


  @SerializedName("prodId")
  @Expose
  public Long prodId;



  @SerializedName("holdPartyId")
  @Expose
  public Long holdPartyId;

  @SerializedName("type")
  @Expose
  public String type;

  @SerializedName("serialNr")
  @Expose
  public String serialNr;

  public BarcodeInfo(Builder builder) {
    paramId = builder.paramId;
    paramItemId = builder.paramItemId;
    prodId = builder.prodId;
    holdPartyId = builder.holdPartyId;
    type = builder.type;
    serialNr = builder.serialNr;
  }


  public static class Builder {

/*    private Long shipId;
    private Long shipItemId;
    private Long orderId;
    private Long orderItemId;*/

    private Long paramId;
    private Long paramItemId;

    private Long prodId;
    private Long holdPartyId;
    private String type;
    private String serialNr;

    public Builder() {
    }

    /*public Builder setShipId(Long id) {
      this.shipId = id;
      return this;
    }

    public Builder setShipItemId(Long itemId) {
      this.shipItemId = itemId;
      return this;
    }*/

    /*public Builder setOrderId(Long orderId) {
      this.orderId = orderId;
      return this;
    }

    public Builder setOrderItemId(Long orderItemId) {
      this.orderItemId = orderItemId;
      return this;
    }*/

    public Builder setParamId(Long paramId) {
      this.paramId = paramId;
      return this;
    }

    public Builder setParamItemId(Long paramItemId) {
      this.paramItemId = paramItemId;
      return this;
    }

    public Builder setProdId(Long pId) {

      this.prodId = pId;
      return this;
    }


    public Builder setHoldPartyId(Long holdPartyId) {
      this.holdPartyId = holdPartyId;
      return this;
    }

    public Builder setType(String type) {
      this.type = type;
      return this;
    }

    public Builder setSerialNumber(String serialNumber) {
      this.serialNr = serialNumber;
      return this;
    }

    public BarcodeInfo build() {
      return new BarcodeInfo(this);
    }

  }


}
