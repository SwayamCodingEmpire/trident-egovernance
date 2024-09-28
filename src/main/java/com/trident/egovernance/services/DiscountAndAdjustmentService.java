package com.trident.egovernance.services;

import com.trident.egovernance.entities.permanentDB.Adjustments;
import com.trident.egovernance.entities.permanentDB.Discount;

public interface DiscountAndAdjustmentService {
    Boolean insertDiscountData(Discount discount);
    Adjustments addAdjustment(Adjustments adjustments);
}
