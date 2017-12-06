package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RefundPolicy;

/**
 * Created by android_sam on 2017. 12. 4..
 */
@JsonObject
public class RefundPolicyData
{
    @JsonField(name = "comment")
    public String comment; // (string, optional),

    @JsonField(name = "refundManual")
    public boolean refundManual; // (boolean, optional),

    @JsonField(name = "refundPolicy")
    public String refundPolicy; // (string, optional) = ['NO_CHARGE_REFUND', 'SURCHARGE_REFUND', 'NRD', 'NONE']

    public RefundPolicy getRefundPolicy()
    {
        RefundPolicy refundPolicy = new RefundPolicy();

        refundPolicy.comment = this.comment;
        refundPolicy.refundManual = this.refundManual;
        refundPolicy.refundPolicy = this.refundPolicy;

        return refundPolicy;
    }
}
